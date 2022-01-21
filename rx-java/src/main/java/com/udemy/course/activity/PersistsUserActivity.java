package com.udemy.course.activity;

import com.udemy.course.flow.FlowItem;
import com.udemy.course.gateway.PersistsUserGateway;
import com.udemy.course.models.User;

import io.reactivex.rxjava3.core.Single;

public class PersistsUserActivity implements FlowItem<User> {
	private final PersistsUserGateway gateway;

	public PersistsUserActivity() {
		this.gateway = new PersistsUserGateway();
	}

	@Override public Single<User> execute(User input) {
		return gateway.persists(input);
	}
}
