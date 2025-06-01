package csgi.challenge.broadcaster;

import csgi.challenge.parser.Parser;
import csgi.challenge.token.Token;
import csgi.challenge.worker.Worker;

import java.io.EOFException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Process that collects the information processed by the parser and publish to all workers connected to it
 */
public class Broadcaster {
	/**
	 * Number of threads that will execute the broadcasting
	 */
	private final CompletableFuture<Void> future;
	private final AtomicReference<CompletableFuture<Void>> completion;
	/**
	 * All workers associated to this broadcaster
	 */
	private final List<Worker<?>> workers;
	private final Queue<Token> tokenQueue;
	private volatile boolean parseComplete = false;
	private final AtomicInteger activeTasks = new AtomicInteger(0);
	private final CountDownLatch done = new CountDownLatch(1);
	/**
	 * Parser that will deliver the data to broadcast
	 */
	private final Parser parser;
	private final ExecutorService executorInbound;
	private final ExecutorService executorOutbound;

	/**
	 * When the parser finishes
	 * this flag will signal the workers to return the results
	 */


	public Broadcaster(Parser parser, Worker<?>... workers) {

		this.workers = Arrays.asList(workers);
		this.parser = parser;
		future = new CompletableFuture<>();
		executorInbound = Executors.newFixedThreadPool(100);
		executorOutbound = Executors.newFixedThreadPool(10);
		completion = new AtomicReference<>(new CompletableFuture<>());
		tokenQueue = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Adds worker to the broadcast
	 *
	 * @param worker worker that pretends to receive that from this broadcast
	 */
	public void addWorker(Worker<?> worker) {
		this.workers.add(worker);
	}

	public CompletableFuture<Void> start() {
		Thread thread = new Thread(this::run);
		thread.setName("BroadCaster");
		thread.start();
		return future;
	}

	public CompletableFuture<Void> start(int n, Executor executor) {
		while (--n >= 0) {
			executor.execute(this::run);
		}
		return future;
	}

	/**
	 * Process where retrieves the tokens from the parser queue and delivers to each worker
	 */
	private void run() {
		try {
			for (Token token = tokenQueue.poll(); !parseComplete || token != null; token = tokenQueue.poll()) {
				if (token == null) {
					continue;
				}
				for (Worker<?> worker : workers) {
					deliverToken(worker, token);
				}
				completion.getAndUpdate(cf -> CompletableFuture.allOf(cf, outboundCF));
			}
		} catch (NoSuchElementException e) {
			if (!(e.getCause() instanceof EOFException)) {
				completion.get().completeExceptionally(e.getCause());
				future.completeExceptionally(e.getCause());
			}
		}

		this.completion.get().thenAccept(unused -> {
					complete();
					future.complete(null);
				})
				.exceptionally(throwable -> {
					future.completeExceptionally(throwable);
					return null;
				}).thenApply((ignore) -> {
					this.executorInbound.close();
					this.executorOutbound.close();
					return null;
				});
	}


	private void deliverToken(Worker<?> worker, Token token) {
		if (worker.isCompleted()) {
			return;
		}
		activeTasks.incrementAndGet();
		CompletableFuture.runAsync(() -> worker.process(token), executorOutbound)
				.thenRun(() -> {
					if (activeTasks.decrementAndGet() == 0 && parseComplete) {
						done.countDown();
					}
				})
				.exceptionally(throwable -> {
					future.completeExceptionally(throwable);
					return null;
				});
	}

	private void runParser() {
		while (this.parser.hasNext()) {
			tokenQueue.add(parser.getToken());
		}
		parseComplete = true;
	}

	/**
	 * Called when the broadcast is complete and propagates this information to the workers
	 */
	public void complete() {
		for (Worker<?> worker : this.workers) {
			worker.complete();
		}

	}
}