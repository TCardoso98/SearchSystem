package csgi.challenge.worker;

import csgi.challenge.Result;
import csgi.challenge.token.Token;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WorkerAbstract<T> implements Worker<T> {


	private final AtomicBoolean isComplete = new AtomicBoolean(false);
	private final CompletableFuture<Result<T>> future = new CompletableFuture<>();


	public void process(Token token) {
		onToken(token);
	}

	protected abstract void onToken(Token token);

	@Override
	public boolean isCompleted() {
		return isComplete.get();
	}

	protected abstract Result<T> get();

	public CompletableFuture<Result<T>> getResultAsync() {
		return this.future;
	}

	public void complete() {
		this.isComplete.set(true);
	}

	public abstract WorkMode getMode();
}