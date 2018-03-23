package it.nexsoft.dao;

import java.text.ParseException;

import it.nexsoft.entities.BlacklistedAddress;

public interface IBlacklistedAddressDao extends IDao<BlacklistedAddress> {
	boolean checkAddress(String recipientAddress) throws ParseException;
}
