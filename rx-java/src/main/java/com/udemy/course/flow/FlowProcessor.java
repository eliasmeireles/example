package com.udemy.course.flow;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public abstract class FlowProcessor<T> implements FlowItem<T> {
	private FlowItem<T> from;

	public static <T> FlowProcessor<T> from(FlowItem<T> flowItem) {
		final FlowProcessor<T> flowProcessor = new FlowProcessor<>() {

			@Override public FlowItem<T> getProcessor(T input) {
				return flowItem;
			}
		};

		flowProcessor.from = flowItem;
		return flowProcessor;
	}

	public <E> FlowProcessor<T> forEach(FlowProcessor<E> flowItem) {
		execute(null).subscribe(result -> forEach(flowItem, (List<E>) result));
		return this;
	}

	public <E> FlowProcessor<T> forEachParallel(FlowProcessor<E> flowItem) {
		execute(null).subscribe(result -> forEachParallel(flowItem, (List<E>) result));
		return this;
	}

	private <E> void forEach(FlowProcessor<E> flowItem, List<E> result) {
		result.forEach(value -> flowItem.getProcessor(value).execute(value).blockingGet());
	}

	private <E> void forEachParallel(FlowProcessor<E> flowItem, List<E> result) {
		result.forEach(value -> parallelExecution(flowItem, value));
	}

	private <E> void parallelExecution(FlowProcessor<E> flowItem, E value) {
		new Thread(build(flowItem, value)).start();
	}

	private <E> Runnable build(FlowProcessor<E> flowItem, E value) {
		return () -> flowItem.getProcessor(value).execute(value).blockingGet();
	}

	public abstract FlowItem<T> getProcessor(T input);

	@Override public Single<T> execute(T input) {
		if (from == null) {
			return getProcessor(input).execute(null);
		}
		return from.execute(input);
	}
}
