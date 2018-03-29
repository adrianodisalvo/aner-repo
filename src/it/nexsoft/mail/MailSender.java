package it.nexsoft.mail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.main.Globals;

public class MailSender {
	
	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	private static MailSender instance;

	// TODO : delete this parameter (test purpose only) ... OR NOT?!
	private String recipientEmailAddress;
	private String subjectAppend;
	
	private MailSender() {}
	
	public static MailSender getInstance() {
		if (instance == null)
			instance = new MailSender();
		return instance;
	}
	
	public boolean sendMail(String recipientEmailAddress, String subjectAppend) {
		
		boolean bRet = false;
		
		logger.trace("Start sendMail for " + recipientEmailAddress);
		
		this.recipientEmailAddress = recipientEmailAddress;
		this.subjectAppend = subjectAppend;
		
		logger.info(recipientEmailAddress + " : setting smtp properties");
		logger.debug("Properties: "
				+ "protocol = smtps; "
				+ "smtp.host = " + Globals.smtpHost + "; "
				+ "smtp.port = " + Globals.smtpPort + "; "
				+ "smtp.auth = true; "
				+ "smtp.username = " + Globals.username + "; "
				+ "smtp.password = " + Globals.password + ";");
		Properties props = new Properties();
		props.put("mail.store.protocol", "smtps");
		props.put("mail.smtp.host", Globals.smtpHost);
		props.put("mail.smtp.port", Globals.smtpPort);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", Globals.smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		
		logger.info(recipientEmailAddress + " : creating session with authentication");
		Session session = Session.getInstance(props,
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Globals.username, Globals.password);
			}
		});
		
		logger.info(recipientEmailAddress + " : creating message to be sent");
		Message toBeSentMessage = new MimeMessage(session);
		
		try {
			setMessageHeaders(toBeSentMessage);
			setMessageContent(toBeSentMessage);
			
			logger.info(recipientEmailAddress + " : sending message via smtp");
			Transport.send(toBeSentMessage);
			bRet = true;
			
		} catch (AddressException ae) {
			ae.printStackTrace();
			logger.error(ae.getMessage());
		} catch (MessagingException me) {
			me.printStackTrace();
			logger.error(me.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.error(ioe.getMessage());
		}
		
		logger.trace("Stop sendMail for " + recipientEmailAddress);
		return bRet;
	}

	private void setMessageHeaders(Message toBeSentMessage) throws AddressException, MessagingException {
		
		logger.info(recipientEmailAddress + " : setting message headers");
		
		toBeSentMessage.setFrom(new InternetAddress("selezione@nexsoft.it"));
		//toBeSentMessage.setFrom(new InternetAddress("a.disalvo@nexsoft.it"));
		
		toBeSentMessage.addRecipient(RecipientType.TO, new InternetAddress(recipientEmailAddress));
		toBeSentMessage.addRecipient(RecipientType.CC, new InternetAddress("m.masucci@nexsoft.it"));
		//toBeSentMessage.addRecipient(RecipientType.CC, new InternetAddress("a.disalvo@nexsoft.it"));
		//toBeSentMessage.addRecipient(RecipientType.CC, new InternetAddress("f.saporito@nexsoft.it"));
		
		toBeSentMessage.setSubject("Riferimenti aziendali Nexsoft - Candidatura " + subjectAppend);
	}
	
	private void setMessageContent(Message toBeSentMessage) throws MessagingException, IOException {
		
		logger.info(recipientEmailAddress + " : setting message body");
		Multipart multipart = new MimeMultipart();
		
		BodyPart htmlMessageBodyPart = new MimeBodyPart();
		String htmlText = new String(Files.readAllBytes(Paths.get("template" + File.separator + "mailTemplate.html")), Charset.defaultCharset() );
		//htmlText = htmlText.replace("RECIPIENT_EMAIL_ADDRESS_PLACEHOLDER", recipientEmailAddress);
		htmlMessageBodyPart.setContent(htmlText, "text/html");
		multipart.addBodyPart(htmlMessageBodyPart);
		
		
		logger.info(recipientEmailAddress + " : setting message attachments");
		BodyPart messageBodyPart = new MimeBodyPart();
        String filename = "./template/SchedaRecruitment.doc";
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename.substring(filename.lastIndexOf('/') + 1));
        multipart.addBodyPart(messageBodyPart);
        
        BodyPart imageBodyPart = new MimeBodyPart();
		InputStream imageStream = new BufferedInputStream(new FileInputStream("template" + File.separator + "signature.jpg"));
        DataSource fds = new ByteArrayDataSource(IOUtils.toByteArray(imageStream), "image/jpeg");
        imageBodyPart.setDataHandler(new DataHandler(fds));
        imageBodyPart.setHeader("Content-ID", "<signature.jpg>");
        imageBodyPart.setFileName("signature.jpg");
        multipart.addBodyPart(imageBodyPart);
		
		toBeSentMessage.setContent(multipart);
	}
}
