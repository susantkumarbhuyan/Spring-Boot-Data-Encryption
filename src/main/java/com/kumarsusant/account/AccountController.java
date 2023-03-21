package com.kumarsusant.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kumarsusant.commonclasses.User;

@RestController
@RequestMapping("/account")
public class AccountController {
	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
	@Autowired
	private AccountService accountServices;

	@GetMapping(value = "/public/login2", headers = "Accept=application/json")
	public String publiclogin(@RequestBody User user) {
		return "Started HSignzUserAppServicesApplication in 2.112 seconds (process running for 436.201)";
	}

	@PostMapping(value = "/public/login", headers = "Accept=application/json")
	public User register(@RequestBody User user) {
		return accountServices.register(user);
	}

	@GetMapping(value = "/admin/login", headers = "Accept=application/json")
	public String adminlogin() {
		return "adminsuccess";
	}

	@GetMapping(value = "/public/getuser", headers = "Accept=application/json")
	public User getUser(String id) {
		return accountServices.getUser(id);
	}
}
