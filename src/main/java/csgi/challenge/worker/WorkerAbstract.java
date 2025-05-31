package csgi.challenge.worker;

import csgi.challenge.Result;
import csgi.challenge.token.Token;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WorkerAbstract<T> implements Worker<T>, Runnable, AutoCloseable {
	private final Queue<Token> queue = new ConcurrentLinkedQueue<>();
	private final AtomicBoolean isClose;
	private final CompletableFuture<Result<T>> future = new CompletableFuture<>();
	private final int nInstances;

	WorkerAbstract(int numberOfInstances) {
		this.nInstances = numberOfInstances;
		this.isClose = new AtomicBoolean();
	}

	public void process(Token token) {
		this.queue.add(token);
	}

	protected abstract void onToken(Token token) throws Exception;

	public void run() {
		for (Token token = this.queue.poll(); !this.isClose.get() || token != null; token = this.queue.poll()) {
			if (token != null && token.value() != null) {
				try {
					this.onToken(token);
				} catch (Exception e) {
					this.future.completeExceptionally(e);
				}
			}
		}

		this.future.complete(this.get());
	}

	public void close() {
		this.isClose.set(true);
	}

	public CompletableFuture<Result<T>> getResultAsync() {
		return this.future;
	}

	protected abstract Result<T> get();

	public void complete() {
		this.close();
	}

	public abstract WorkMode getMode();

	public void execute(Executor executor) {
		for (int i = 0; i < this.nInstances; ++i) {
			executor.execute(this);
		}

	}
}