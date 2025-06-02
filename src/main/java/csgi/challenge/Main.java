package csgi.challenge;

import com.beust.jcommander.JCommander;
import csgi.challenge.configuration.Configuration;

import java.util.concurrent.*;

public class Main {

	public Main() {
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Configuration conf = new Configuration();
		JCommander jCommander = JCommander.newBuilder()
				                        .addObject(conf)
				                        .build();
		jCommander.parse(args);
		if (conf.help) {
			jCommander.usage();
			return;
		}
		CompletableFuture<?>[] results = new WorkBuilder().addConfiguration(conf).build().start();
		for (CompletableFuture<?> result : results) {
			result.thenAccept(System.out::println);
		}
		CompletableFuture.allOf(results).get();

	}
}