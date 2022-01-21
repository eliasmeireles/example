package com.udemy.course.processor;

import java.util.List;

import com.udemy.course.activity.GetUsersActivity;
import com.udemy.course.flow.FlowItem;
import com.udemy.course.flow.FlowProcessor;
import com.udemy.course.models.User;

public class GetUsersProcessor extends FlowProcessor<List<User>> {

	private final GetUsersActivity getUsersActivity;
	private final PersistsUserProcessor persistsUserProcessor;

	public GetUsersProcessor() {
		this.getUsersActivity = new GetUsersActivity();
		this.persistsUserProcessor = new PersistsUserProcessor();
	}

	@Override public FlowItem<List<User>> getProcessor(List<User> input) {
		return FlowProcessor.from(getUsersActivity)
				.forEach(persistsUserProcessor);
	}
}
