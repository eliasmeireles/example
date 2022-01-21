package com.udemy.course.gateway;

import java.util.Random;

import com.udemy.course.models.User;

import io.reactivex.rxjava3.core.Single;

public class PersistsUserGateway {

	final Random random = new Random();

	public Single<User> persists(User user) {
		return Single.fromCallable(() -> {
			System.out.print(".");
			final int sleep = random.nextInt(1) + 50;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return user;
		});
	}
}
