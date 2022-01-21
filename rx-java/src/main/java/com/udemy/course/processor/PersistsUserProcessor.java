package com.udemy.course.processor;

import com.udemy.course.activity.PersistsUserActivity;
import com.udemy.course.flow.FlowItem;
import com.udemy.course.flow.FlowProcessor;
import com.udemy.course.models.User;

public class PersistsUserProcessor extends FlowProcessor<User> {

	private final PersistsUserActivity activity;

	public PersistsUserProcessor() {
		this.activity = new PersistsUserActivity();
	}

	@Override public FlowItem<User> getProcessor(User input) {
		return FlowProcessor.from(activity);
	}
}
