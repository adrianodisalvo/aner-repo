package it.nexsoft.entities;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class BlacklistedAddress {
	
	@Column(unique=true)
	private String address;
	private Date date;
	
	public BlacklistedAddress(String address, Date date) {
		super();
		this.address=address;
		this.date=date;
	}
	
	public BlacklistedAddress() {
		super();
	}
	
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address=address; }
	
	public Date getDate() { return date; }
	public void setDate(Date date) { this.date=date; }
	
	@Override
	public String toString() {
		return "Blacklist [address=" + address + ", date=" + date + "]";
	}
}
