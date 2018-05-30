package it.nexsoft.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nexsoft.entities.BlacklistedAddress;
import it.nexsoft.main.Globals;

public class BlacklistedAddressDao extends Dao<BlacklistedAddress> implements IBlacklistedAddressDao {
	
	//temporary
	private static final Logger logger = LogManager.getLogger("ApplicationMain");
	
	private static BlacklistedAddressDao instance;
	
	private BlacklistedAddressDao() {}
	
	public static BlacklistedAddressDao getInstance() {
		if (instance == null)
			instance = new BlacklistedAddressDao();
		return instance;
	}

	public boolean checkAddress(String recipientAddress) throws ParseException {
		
		boolean bRet = true;
		
		logger.debug("Checking e-mail address " + recipientAddress + " from db");
		
		BlacklistedAddress blacklistedAddress = entityManager.find(BlacklistedAddress.class, recipientAddress);
		
		if (blacklistedAddress != null) {
			
			logger.debug("Found " + recipientAddress + " into blacklisted table, let's check the date field...");
			
			Calendar oneMonthAgo = GregorianCalendar.getInstance();
			oneMonthAgo.add(Calendar.MONTH, -1);
			SimpleDateFormat sdf = new SimpleDateFormat(Globals.dateFormat);
			
			logger.debug("Mumble... " + recipientAddress + " is blacklisted since " + sdf.parse(blacklistedAddress.getDate()).toString() );
			logger.debug("... and today is " + GregorianCalendar.getInstance().getTime().toString() + "...");
			logger.debug("So if one month ago was " + oneMonthAgo.getTime().toString() + "... Let me do my maths...");
			
			if (sdf.parse(blacklistedAddress.getDate()).before(oneMonthAgo.getTime())) {
				logger.debug("Well ok, " + recipientAddress + " is blacklisted by date! See you in a month (or less)!");
				bRet = false;
			} else {
				logger.debug("Ok, ok, " + recipientAddress + " is not blacklisted by date! You shall pass!");
			}
		}
		
		return bRet;
	}
}
