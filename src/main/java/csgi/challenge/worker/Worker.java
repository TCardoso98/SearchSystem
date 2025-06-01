package csgi.challenge.worker;

import csgi.challenge.Result;
import csgi.challenge.token.Token;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface Worker<T> {
	void process(Token token);

	CompletableFuture<Result<T>> getResultAsync();

	void complete();

	boolean isCompleted();
}