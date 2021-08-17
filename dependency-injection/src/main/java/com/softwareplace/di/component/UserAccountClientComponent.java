package com.softwareplace.di.component;

import java.util.logging.Logger;

import com.softwareplace.di.annotation.Autowired;
import com.softwareplace.di.annotation.Component;
import com.softwareplace.di.service.AccountService;
import com.softwareplace.di.service.UseService;

@Component
public class UserAccountClientComponent {
	private static final Logger logger = Logger.getLogger(UserAccountClientComponent.class.getName());

	@Autowired
	private UseService useService;

	@Autowired
	private AccountService accountService;

	public void displayUserAccount() {
		logger.info("Username: " + useService.getUserName());
		logger.info("Account Number: " + accountService.getAccountNumber(""));
	}
}
