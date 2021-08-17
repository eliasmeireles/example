package com.softwareplace.di.impl;

import com.softwareplace.di.annotation.Component;
import com.softwareplace.di.service.UseService;

@Component
public class UseServiceImpl implements UseService {
	@Override public String getUserName() {
		return "Elias Meirels";
	}
}
