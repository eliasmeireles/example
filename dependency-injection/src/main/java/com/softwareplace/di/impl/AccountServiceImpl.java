package com.softwareplace.di.impl;

import com.softwareplace.di.annotation.Component;
import com.softwareplace.di.service.AccountService;

@Component
public class AccountServiceImpl implements AccountService {

	@Override public Long getAccountNumber(String userName) {
		return 2316546987L;
	}
}
