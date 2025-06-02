package csgi.challenge.parser;

import java.util.concurrent.CompletableFuture;

public interface Parser<T> extends Runnable, AutoCloseable {


	boolean hasNext();

	boolean isComplete();

	T poll();

	CompletableFuture<?> waitCompletion();
}