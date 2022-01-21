package com.udemy.course;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.udemy.course.processor.GetUsersProcessor;
import com.udemy.course.processor.ParallelGetUsersProcessor;

public class HelloRxJava {

	public static void main(String[] args) {
		var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");
		System.out.printf("%n%nParallel execution stated at::> %s%n", LocalDateTime.now().format(dateTimeFormatter));
		new ParallelGetUsersProcessor().execute(null);
		System.out.printf("%nParallel execution completed at::> %s%n%n", LocalDateTime.now().format(dateTimeFormatter));

		System.out.printf("Sync execution stated at::> %s%n", LocalDateTime.now().format(dateTimeFormatter));
		new GetUsersProcessor().execute(null);
		System.out.printf("%nSync execution completed at::> %s%n%n", LocalDateTime.now().format(dateTimeFormatter));

	}
}
