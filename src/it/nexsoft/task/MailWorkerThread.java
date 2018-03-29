package it.nexsoft.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.dao.BlacklistedAddressDao;
import it.nexsoft.entities.BlacklistedAddress;
import it.nexsoft.mail.MailSender;
import it.nexsoft.parser.CustomParserFactory;
import it.nexsoft.parser.Parser;

public class MailWorkerThread extends Thread {
	
	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	private Message receivedMessage;
	private String subjectAppend = "";
	
	public MailWorkerThread(Message receivedMessage) {
		this.receivedMessage = receivedMessage;
	}
	
	@Override
	public void run() {
		
		logger.trace("Starting MailWorkerThread " + this.getName());
		
		if (receivedMessage != null)
		{
			try {
				String recipientEmailAddress = discoverApplicantAddress();
				
				if (recipientEmailAddress != null && !recipientEmailAddress.isEmpty()) {
					logger.debug("Found the candidate email address: " + recipientEmailAddress);
					synchronized(MailWorkerThread.class) {
						logger.debug("---> Entering critical section");
						if (BlacklistedAddressDao.getInstance().checkAddress(recipientEmailAddress)) {
							if (MailSender.getInstance().sendMail(recipientEmailAddress, subjectAppend)) {
								receivedMessage.setFlag(Flag.SEEN, true);
								Calendar today = new GregorianCalendar();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								BlacklistedAddress blAddress = new BlacklistedAddress(recipientEmailAddress, sdf.format(today.getTime()));
								BlacklistedAddressDao.getInstance().persist(blAddress);
								logger.info(recipientEmailAddress + " : message succesfully sent");
							} else {
								logger.warn(recipientEmailAddress + " : error sending email");
							}
						} else {
							receivedMessage.setFlag(Flag.SEEN, true);
							logger.warn(recipientEmailAddress + " : the email to this address was already sent in the last month, skipping it");
						}
						logger.debug("---> Exiting critical section");
					}
				} else {
					receivedMessage.setFlag(Flag.SEEN, true);
					logger.warn("Recipient email address is null or empty. The automatic email will not be sent.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			
		} else {
			logger.warn("There is no receivedMessage in MailWorkerThread " + this.getName());
		}
		
		logger.trace("Stopping MailWorkerThread " + this.getName());
	}

	private String discoverApplicantAddress() throws MessagingException, IOException {
		
		logger.info("Trying to recover the applicant email address");
		
		String sReturn = null;
		String senderAddress = receivedMessage.getFrom()[0].toString();
		
		logger.debug("Received message from " + senderAddress);
		
		if (senderAddress.contains("route@monster.com") &&
				receivedMessage.getSubject().contains("Hai appena ricevuto una candidatura per")) {
			subjectAppend = "Monster";
			logger.info("Received message from " + subjectAppend);
			sReturn = discoverFromBody();
		}
		else if (senderAddress.contains("@indeedemail.com") &&
				receivedMessage.getSubject().contains("Candidatura per") &&
				receivedMessage.getSubject().contains("inviata da") &&
				receivedMessage.getSubject().contains("attraverso Indeed")) {
			subjectAppend = "Indeed";
			logger.info("Received message from " + subjectAppend);
			sReturn = discoverFromAttach();
		}
		else if (senderAddress.contains("@biancolavoro.it") &&
				receivedMessage.getSubject().contains("Candidatura per") &&
				receivedMessage.getSubject().contains("(Rif.")) {
			subjectAppend = "Euspert";
			logger.info("Received message from " + subjectAppend);
			sReturn = discoverFromBody();
		}
		//else if (senderAddress.contains("adro.disalvo@fastwebnet.it")) // TODO : test purpose, to be deleted
		//	sReturn = discoverFromBody();
		else
			logger.debug("The current email is not coming from an online application");
		
		return sReturn;
	}

	private String discoverFromBody() throws IOException, MessagingException {
		
		String sReturn = null;
		
		logger.info("Discovering email address from body");
		
		Object content = receivedMessage.getContent();
		
		if (content instanceof String)
		{
			sReturn = Parser.parseBody((String)content);
		} else if (content instanceof Multipart) {
			Multipart multipart = (Multipart)receivedMessage.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				
				BodyPart bodyPart = multipart.getBodyPart(i);
				String parsedString = Parser.parseBody(bodyPart.getContent().toString());
				
				if (parsedString != null && !parsedString.isEmpty()) {
					sReturn = parsedString;
					break;
				}
			}
		}
		
		if (sReturn == null || sReturn.isEmpty())
			sReturn = discoverFromAttach();
		
		return sReturn;
	}
	
	private String discoverFromAttach() throws IOException, MessagingException {
		
		String sReturn = null;
		Parser parser = null;
		
		logger.info("Discovering email address from attachments");
		List<File> attachments = getAttachedFiles();
		
		logger.debug("Found " + attachments.size() + " attachments");
		
		for (int i = 0; i < attachments.size(); i++) {
			if (Stream.of("pdf", "doc", "docx").anyMatch(FilenameUtils.getExtension(attachments.get(i).getName())::equalsIgnoreCase)) {
				parser = CustomParserFactory.getParser(attachments.get(i));
				break;
			}
		}
		
		if (parser != null)
			sReturn = parser.parseAttachment();
		else
			logger.warn("Attachments has extensions not handled by this application");
		
		cleanAttachments(attachments);
		return sReturn;
	}

	private List<File> getAttachedFiles() throws IOException, MessagingException {

		List<File> retList = new ArrayList<File>();
		
		Object content = receivedMessage.getContent();
		
		logger.debug("Attachment is of type " + content.getClass());
		
		if (content instanceof com.sun.mail.util.BASE64DecoderStream) {
			
			// TODO : to be completed
			//com.sun.mail.util.BASE64DecoderStream attachStream = (com.sun.mail.util.BASE64DecoderStream)receivedMessage.getContent();
			
		} else if (content instanceof Multipart) {
			
			Multipart multipart = (Multipart)receivedMessage.getContent();
			
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				
				if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
					StringUtils.isBlank(bodyPart.getFileName())) {
					continue; // dealing with attachments only
				}
				InputStream is = bodyPart.getInputStream();
				File dir = new File("tmp");
				if (!dir.exists()) dir.mkdir();
		        File f = new File(dir + File.separator + bodyPart.getFileName());
		        FileOutputStream fos = new FileOutputStream(f);
		        byte[] buf = new byte[4096];
		        int bytesRead;
		        while((bytesRead = is.read(buf))!=-1) {
		            fos.write(buf, 0, bytesRead);
		        }
		        fos.close();
		        retList.add(f);
			}
		}
		
		return retList;
	}
	
	private void cleanAttachments(List<File> attachments) {
		for (File f : attachments) {
			logger.debug("Deleting " + f.getName());
			f.delete();
		}
	}
}
