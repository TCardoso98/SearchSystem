package csgi.challenge.worker;

import java.util.concurrent.CompletableFuture;

public interface Worker<T, R> extends Runnable {
	void process(T token);

	CompletableFuture<R> getResultAsync();

	void complete();

	boolean isCompleted();
}