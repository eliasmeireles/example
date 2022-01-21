package com.udemy.course.gateway;

import java.util.ArrayList;
import java.util.List;

import com.udemy.course.models.User;

import io.reactivex.rxjava3.core.Single;

public class GetUsersGateway {

	public Single<List<User>> getUsers() {
		return Single.fromCallable(() -> {
			var user = new ArrayList<User>();

			while (user.size() < 100) {
				user.add(new User(String.format("User - %d", user.size()), String.format("user%d@email.com", user.size())));
			}
			return user;
		});
	}
}
