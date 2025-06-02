package csgi.challenge.parser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ParserAbstract<T> implements Parser<T> {
	protected final CompletableFuture<?> future;
	protected final AtomicBoolean complete;


	protected ParserAbstract() {
		future = new CompletableFuture<>();
		complete = new AtomicBoolean(false);
	}

	public boolean isComplete() {
		return complete.get();
	}

	@Override
	public CompletableFuture<?> waitCompletion() {
		return future;
	}

	public void close() {
		this.complete.compareAndSet(false, true);
	}
}