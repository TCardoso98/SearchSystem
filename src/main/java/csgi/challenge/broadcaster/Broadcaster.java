package csgi.challenge.broadcaster;

import csgi.challenge.parser.Parser;
import csgi.challenge.worker.Worker;

import java.io.EOFException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Process that collects the information processed by the parser and publish to all workers connected to it
 */
public class Broadcaster<T> {
	/**
	 * Number of threads that will execute the broadcasting
	 */
	private final CompletableFuture<Void> future;
	/**
	 * All workers associated with this broadcaster
	 */
	private final List<Worker<T, ?>> workers;
	/**
	 * Parser that will deliver the data to broadcast
	 */
	private final Parser<T> parser;
	private final ExecutorService executorService;
	private final Thread.Builder threadFactoryWorker;
	private final Thread parserThread;
	private final Thread broadcastThread;

	/**
	 * When the parser finishes,
	 * this flag will signal the workers to return the results
	 */

	@SafeVarargs
	public Broadcaster(Parser<T> parser, Worker<T, ?>... workers) {

		this.workers = new ArrayList<>(Arrays.asList(workers));
		this.parser = parser;


		executorService = Executors.newCachedThreadPool();
		threadFactoryWorker = Thread.ofVirtual().name("Worker-", 0);
		parserThread = Thread.ofVirtual().unstarted(parser);
		broadcastThread = Thread.ofVirtual().unstarted(this::run);
		future = new CompletableFuture<>().thenAccept((unused) -> {
					parserThread.interrupt();
					executorService.shutdown();
				}
		);
	}


	/**
	 * Adds worker to the broadcast
	 *
	 * @param worker worker that pretends to receive that from this broadcast
	 */
	public void addWorker(Worker<T, ?> worker) {
		this.workers.add(worker);
	}

	public CompletableFuture<Void> start() {
		for (Worker<T, ?> worker : workers) {
			threadFactoryWorker.start(worker);
		}
		broadcastThread.start();
		parserThread.start();
		return future;
	}

	/**
	 * Process where retrieves the tokens from the parser queue and delivers to each worker
	 */
	private void run() {
		try {
			while (!this.parser.isComplete()) {
				T token = parser.poll();
				if (token == null) {
					continue;
				}
				for (Worker<T, ?> worker : workers) {
					worker.process(token);
				}
				/*CompletableFuture.runAsync(() -> {
					for (Worker<T, ?> worker : workers) {
						deliverToken(worker, finalToken);
					}
				}, executorService);*/

			}
		} catch (NoSuchElementException e) {
			if (!(e.getCause() instanceof EOFException)) {
				future.completeExceptionally(e.getCause());
			}
		}
		complete();
		future.complete(null);
	}


	/**
	 * Called when the broadcast is complete and propagates this information to the workers
	 */
	public void complete() {
		for (Worker<T, ?> worker : this.workers) {
			worker.complete();
		}

	}
}