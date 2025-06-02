package csgi.challenge;

import csgi.challenge.broadcaster.Broadcaster;
import csgi.challenge.configuration.Configuration;
import csgi.challenge.parser.ParserImpl;
import csgi.challenge.worker.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WorkBuilder {
	private final Map<String, Broadcaster<?>> broadcasterMap = new ConcurrentHashMap<>();
	private final List<Configuration> configurations = new ArrayList<>();
	private final List<Worker<?, ?>> workers = new ArrayList<>();

	public WorkBuilder() {
	}

	public WorkBuilder addConfiguration(Configuration configuration) {
		this.configurations.add(configuration);
		return this;
	}

	public WorkBuilder build() {
		for (Configuration configuration : this.configurations) {
			Worker worker = configuration.mode.value.get();

			for (String filePath : configuration.filePaths) {
				this.broadcasterMap.computeIfAbsent(filePath, this::getBroadcaster).addWorker(worker);
			}

			this.workers.add(worker);
		}

		return this;
	}

	public CompletableFuture<?>[] start() {
		CompletableFuture<?>[] allFutures = new CompletableFuture[workers.size()];

		for (Broadcaster<?> broadcaster : broadcasterMap.values()) {
			broadcaster.start();
		}
		int i = 0;
		for (Worker<?, ?> worker : workers) {
			allFutures[i++] = worker.getResultAsync();
		}
		return allFutures;
	}


	private Broadcaster getBroadcaster(String s) {
		try {
			ParserImpl parser = new ParserImpl(s);
			return new Broadcaster(parser);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}