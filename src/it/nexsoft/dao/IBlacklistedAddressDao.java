package it.nexsoft.dao;

import it.nexsoft.entities.BlacklistedAddress;

public interface IBlacklistedAddressDao extends IDao<BlacklistedAddress> {
	public boolean checkAddress(String recipientAddress);
}
