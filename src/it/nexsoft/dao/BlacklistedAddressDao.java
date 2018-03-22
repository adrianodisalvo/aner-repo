package it.nexsoft.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.nexsoft.entities.BlacklistedAddress;

public class BlacklistedAddressDao extends Dao<BlacklistedAddress> implements IBlacklistedAddressDao {
	
	private static BlacklistedAddressDao instance;
	
	private BlacklistedAddressDao() {}
	
	public static BlacklistedAddressDao getInstance() {
		if (instance == null)
			instance = new BlacklistedAddressDao();
		return instance;
	}

	public boolean checkAddress(String recipientAddress) {
		
		boolean bRet = false;
		
		BlacklistedAddress blacklistedAddress = entityManager.find(BlacklistedAddress.class, recipientAddress);
		
		if (blacklistedAddress != null) {
			Calendar oneMonthAgo = GregorianCalendar.getInstance();
			oneMonthAgo.add(Calendar.MONTH, -1);
			if (blacklistedAddress.getDate().before(oneMonthAgo.getTime()))
				bRet = true;
		}
		return bRet;
	}
}
