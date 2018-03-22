package it.nexsoft.mail;

import java.util.Properties;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.main.ApplicationMain;
import it.nexsoft.main.Globals;

public class MailReceiver {
	
	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	private static MailReceiver instance;
	
	private MailReceiver() {}
	
	public static MailReceiver getInstance() {
		if (instance == null)
			instance = new MailReceiver();
		return instance;
	}
	
	public Message[] checkMail() {
		
		logger.trace("Start checkMail");
		
		Message[] messages = null;
		
		try {
			logger.info("Setting imap properties");
			logger.debug("Properties: "
					+ "protocol = imap; "
					+ "imap.host = " + Globals.imapHost + "; "
					+ "imap.port = " + Globals.imapPort + "; "
					+ "imap.ssl.enable = true; "
					+ "imap.username = " + Globals.username + "; "
					+ "imap.password = " + Globals.password + ";");
			
			Properties props = new Properties();
			props.put("mail.store.protocol", "imaps");
			props.put("mail.imap.port", Globals.imapPort);
			props.put("mail.imap.ssl.enable", "true");
			
			logger.info("Creating session");
			Session session = Session.getInstance(props);
			
			logger.info("Connecting to the server");
			Store store = session.getStore();
			store.connect(Globals.imapHost, Globals.username, Globals.password);
			
			logger.info("Reading email folder");
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			
			//logger.info("Searching the email folder for recent messages (received in the last " + Globals.timeoutInterval/60000 + " minutes)" );
			logger.info("Searching the email folder for unread messages");
			SearchTerm searchTerm = new SearchTerm() {
				private static final long serialVersionUID = -6240633139232405545L;
				@Override
				public boolean match(Message arg0) {
					boolean bRet = false;
					try {
						if (/*System.currentTimeMillis() - arg0.getReceivedDate().getTime() <= Globals.timeoutInterval*/
								!arg0.getFlags().contains(Flag.SEEN) &&
								arg0.getReceivedDate().after(ApplicationMain.applicationStartDate.getTime()) )
							bRet = true;
					} catch (MessagingException e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
					return bRet;
				}
			};
			messages = folder.search(searchTerm , folder.getMessages());
			
			//logger.info("Closing folder and store");
			//emailFolder.close(false);
			//store.close();
			
		} catch (NoSuchProviderException nspe) {
			nspe.printStackTrace();
			logger.error(nspe.getMessage());
		} catch (MessagingException me) {
			me.printStackTrace();
			logger.error(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		logger.trace("Stop checkMail");
		
		return messages;
	}
	
}
