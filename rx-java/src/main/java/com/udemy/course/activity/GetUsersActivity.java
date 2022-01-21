package com.udemy.course.activity;

import java.util.List;

import com.udemy.course.flow.FlowItem;
import com.udemy.course.gateway.GetUsersGateway;
import com.udemy.course.models.User;

import io.reactivex.rxjava3.core.Single;

public class GetUsersActivity implements FlowItem<List<User>> {

	private final GetUsersGateway gateway;

	public GetUsersActivity() {
		this.gateway = new GetUsersGateway();
	}

	@Override public Single<List<User>> execute(List<User> input) {
		return gateway.getUsers();
	}
}
