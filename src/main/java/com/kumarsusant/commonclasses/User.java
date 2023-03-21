package com.kumarsusant.commonclasses;

import org.springframework.data.annotation.Id;

import com.bol.secure.Encrypted;

//@Document("user")
public class User {
	@Id
	private String id;
	private String name;
	private int dob;
	@Encrypted
	private long phoneNo;

	public User(String id, String name, int dob, long phoneNo) {
		super();
		this.id = id;
		this.name = name;
		this.dob = dob;
		this.phoneNo = phoneNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDob() {
		return dob;
	}

	public void setDob(int dob) {
		this.dob = dob;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(long phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", dob=" + dob + ", phoneNo=" + phoneNo + "]";
	}

}
