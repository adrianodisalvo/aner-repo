package it.nexsoft.main;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.task.AnerShutdownTask;
import it.nexsoft.task.TimeoutRoutine;

public class ApplicationMain {
	
	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	public static final GregorianCalendar applicationStartDate = new GregorianCalendar();
	
	public static void main(String[] args) {
		
		logger.info("------------------------------------------");
		logger.info("Starting Automatic Nexsoft Email Responder");
		logger.info("------------------------------------------");
		
		logger.debug("Good Morning! Today is "
				+ applicationStartDate.get(Calendar.DAY_OF_MONTH) + "/"
				+ (applicationStartDate.get(Calendar.MONTH)+1) + "/"
				+ applicationStartDate.get(Calendar.YEAR));
		
		Runtime.getRuntime().addShutdownHook(new AnerShutdownTask());
		
		readParametersFromConfig();
		
		TimeoutRoutine tr = new TimeoutRoutine();
		
		while(tr.isRunning()) {}
		
		return;
	}

	private static void readParametersFromConfig() {
		try {
			FileInputStream propertiesFile = new FileInputStream(Globals.configFilePath);
			Properties prop = new Properties();
			prop.load(propertiesFile);
			Globals.username = prop.getProperty("username");
			Globals.password = prop.getProperty("password");
			Globals.timeoutInterval = Integer.parseInt(prop.getProperty("timeoutInterval", "900000"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			Globals.username = "recruiting@nexsoft.it";
			Globals.password = "adisalvo";
			Globals.timeoutInterval = 900000;
		}
	}
}
