package it.nexsoft.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class BlacklistedAddress {
	
	@Id
	private String address;
	private String date;
	
	public BlacklistedAddress(String address, String date) {
		super();
		this.address=address;
		this.date=date;
	}
	
	public BlacklistedAddress() {
		super();
	}
	
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address=address; }
	
	public String getDate() { return date; }
	public void setDate(String date) { this.date=date; }
	
	@Override
	public String toString() {
		return "BlacklistedAddress [address=" + address + ", date=" + date + "]";
	}
}
