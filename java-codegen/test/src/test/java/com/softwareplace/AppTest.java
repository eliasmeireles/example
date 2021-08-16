package com.softwareplace;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.softwareplace.config.ConfigSourceImpl;
import com.softwareplace.config.DataSource;
import com.softwareplace.datasource.CustomDataSource;

class AppTest {

	private ConfigSourceImpl configSource;

	@BeforeEach
	void tearsUp() {
		configSource = new ConfigSourceImpl();
	}

	@Test
	void configImpl_Validation() {
		assertNotNull(configSource);
	}

	@Test
	void configImpl_Values_Validation() {
		List<DataSource> dataSources = configSource.values();

		assertNotNull(dataSources);
		assertFalse(dataSources.isEmpty());
		assertTrue(dataSources.get(0) instanceof CustomDataSource);
	}
}
