package it.nexsoft.main;

import java.io.File;

public class Globals {
	
	public static final String configFilePath = "properties" + File.separator + "aner.properties";
	
	public static final String imapHost = "imaps.aruba.it";
	public static final String imapPort = "993";
	
	public static final String smtpHost = "smtps.aruba.it";
	public static final String smtpPort = "465";
	
	public static String username;
	public static String password;
	
	public static int timeoutInterval;
	public static final int timeoutDelay = 0;
}
