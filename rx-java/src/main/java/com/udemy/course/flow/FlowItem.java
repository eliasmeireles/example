package com.udemy.course.flow;

import io.reactivex.rxjava3.core.Single;

public interface FlowItem<T> {

	Single<T> execute(T input);
}
