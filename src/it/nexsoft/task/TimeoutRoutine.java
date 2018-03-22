package it.nexsoft.task;

import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.mail.MailReceiver;
import it.nexsoft.main.Globals;

public class TimeoutRoutine {

	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	private Timer timer;
	private boolean isRunning = true;
	
	public TimeoutRoutine() {
		logger.info("Instantiating the Timeout Routine");
		this.timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				isRunning = true;
				try {
					doTheJob();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					isRunning = false;
				}
			}
		};
		logger.info("Scheduling the timer task");
		timer.schedule(task , Globals.timeoutDelay, Globals.timeoutInterval);
	}
	
	private void doTheJob() throws Exception {
		
		logger.trace("Start doTheJob()");
		
		Message[] messages = MailReceiver.getInstance().checkMail();
		
		logger.debug("Found " + messages.length + " new e-mail messages");
		
		for (Message message : messages)
		{
			new MailWorkerThread(message).start();
		}
		
		logger.trace("Stop doTheJob()");
	}

	public boolean isRunning() {
		return isRunning;
	}

}
