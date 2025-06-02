package csgi.challenge.worker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WorkerAbstract<T, R> implements Worker<T, R> {
	private final AtomicBoolean isComplete;
	private final CompletableFuture<R> future;
	private final BlockingQueue<T> pending;

	protected WorkerAbstract() {
		pending = new ArrayBlockingQueue<>(100);
		isComplete = new AtomicBoolean(false);
		future = new CompletableFuture<>();

	}

	public void process(T token) {
		for (; ; ) {
			try {
				pending.put(token);
				return;
			} catch (InterruptedException ignored) {

			}
		}
	}

	protected abstract void onToken(T token);

	@Override
	public boolean isCompleted() {
		return isComplete.get() && pending.peek() == null;
	}

	protected abstract R get();

	public CompletableFuture<R> getResultAsync() {
		return this.future;
	}

	public void complete() {
		this.isComplete.set(true);

	}

	public abstract WorkMode getMode();

	@Override
	public void run() {
		while (!this.isCompleted()) {
			T t = pending.poll();
			if (t != null) {
				onToken(t);
			}
		}
		future.complete(get());
	}
}