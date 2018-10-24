package it.nexsoft.task;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnerShutdownTask extends Thread {

	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	@Override
	public void run() {
		logger.info("------------------------------------------");
		logger.info("Stopping Automatic Nexsoft Email Responder");
		logger.info("------------------------------------------");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Press Enter to continue ...");
		sc.nextLine();
		sc.close();
	}
}
