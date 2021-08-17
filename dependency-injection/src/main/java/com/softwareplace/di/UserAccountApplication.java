package com.softwareplace.di;

import java.util.Objects;
import java.util.logging.Logger;

import com.softwareplace.di.component.UserAccountClientComponent;
import com.softwareplace.di.inject.Injector;

public class UserAccountApplication {

	private static final Logger logger = Logger.getLogger(UserAccountApplication.class.getSimpleName());

	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();

		Injector.startApplication(UserAccountApplication.class);
		Objects.requireNonNull(Injector.getService(UserAccountClientComponent.class))
				.displayUserAccount();

		logger.info("Elapsed time: " + getFormattedDifferenceOfMillis(System.currentTimeMillis(), startTime) + " seconds");
	}

	static String getFormattedDifferenceOfMillis(long endTime, long startTime) {
		String valueFormatted = String.format("%04d", (endTime - startTime));
		return valueFormatted.substring(0, valueFormatted.length() - 3) + "," + valueFormatted.substring(valueFormatted.length() - 3);
	}
}
