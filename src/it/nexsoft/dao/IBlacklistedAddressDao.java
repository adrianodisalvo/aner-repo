package it.nexsoft.dao;

import it.nexsoft.entities.BlacklistedAddress;

public interface IBlacklistedAddressDao extends IDao<BlacklistedAddress> {
	boolean checkAddress(String recipientAddress);
}
