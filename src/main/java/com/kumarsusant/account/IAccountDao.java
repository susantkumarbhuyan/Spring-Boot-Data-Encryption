package com.kumarsusant.account;

import com.kumarsusant.commonclasses.User;

public interface IAccountDao {

	public User registerUser(User user);

	public User getUser(String id);

}
