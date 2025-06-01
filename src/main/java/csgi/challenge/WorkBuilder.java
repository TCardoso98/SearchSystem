package csgi.challenge;

import csgi.challenge.broadcaster.Broadcaster;
import csgi.challenge.configuration.Configuration;
import csgi.challenge.parser.ParserImpl;
import csgi.challenge.worker.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorkBuilder {
	private final Map<String, Broadcaster> broadcasterMap = new ConcurrentHashMap<>();
	private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
	private final List<Configuration> configurations = new ArrayList<>();
	private final List<Worker<?>> workers = new ArrayList<>();

	public WorkBuilder() {
	}

	public WorkBuilder addConfiguration(Configuration configuration) {
		this.configurations.add(configuration);
		return this;
	}

	public WorkBuilder build() {
		for (Configuration configuration : this.configurations) {
			Worker<?> worker = configuration.mode.value.get();

			for (String filePath : configuration.filePaths) {
				this.broadcasterMap.computeIfAbsent(filePath, (s) -> this.getBroadcaster(s, configuration.parserInstanceNumber)).addWorker(worker);
			}

			this.workers.add(worker);
		}

		return this;
	}


	private Broadcaster getBroadcaster(String s, int parserInstanceNumber) {
		try {
			ParserImpl parser = new ParserImpl(s);
			return new Broadcaster(parser);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}