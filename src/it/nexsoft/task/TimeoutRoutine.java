package it.nexsoft.task;

import java.util.GregorianCalendar;
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
		
		Message[] messages = null;;
		
		if( isWorkingTime() ) {
			messages = MailReceiver.getInstance().checkMail();
			
			logger.debug("Found " + messages.length + " new e-mail messages");
			
			for (Message message : messages)
			{
				new MailWorkerThread(message).start();
			}
			
		} else {
			logger.warn("Now it's not working time, I'll go to sleep again...");
		}
		
		logger.trace("Stop doTheJob()");
	}

	public boolean isRunning() {
		return isRunning;
	}

	private boolean isWorkingTime() {
		
		boolean bRet = false;
		
		GregorianCalendar now = new GregorianCalendar();
		int dayOfWeek = now.get(GregorianCalendar.DAY_OF_WEEK);
		int hourOfDay = now.get(GregorianCalendar.HOUR_OF_DAY);
		
		if( dayOfWeek >= GregorianCalendar.MONDAY &&
			dayOfWeek <= GregorianCalendar.FRIDAY &&
			hourOfDay >= 9 &&
			hourOfDay < 18) {
			bRet = true;
		}
		
		return bRet;
	}
}
