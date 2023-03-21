package com.kumarsusant.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kumarsusant.commonclasses.User;

@Service("accountServices")
public class AccountService {
	@Autowired
	private IAccountDao accountDao;

	public User register(User user) {
		return accountDao.registerUser(user);

	}

	public User getUser(String id) {

		return accountDao.getUser(id);
	}
}
