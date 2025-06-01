package csgi.challenge;

import csgi.challenge.broadcaster.Broadcaster;
import csgi.challenge.parser.Parser;
import csgi.challenge.parser.ParserImpl;
import csgi.challenge.worker.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.*;

public class Main {
	private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

	public Main() {
	}

	public static void main(String[] args) {
		try (Parser p = new ParserImpl("random_text_300mb.txt");
		     ExecutorService exe = Executors.newVirtualThreadPerTaskExecutor()
		) {
			Worker<Integer> c = new WordCounterWorker();
			Worker<String[]> l = new WordLengthWorker();
			Broadcaster b = new Broadcaster(p, c, l);
			b.start(10, exe);


			CompletableFuture.allOf(
					c.getResultAsync().thenAccept(System.out::println),
					l.getResultAsync().thenAccept(System.out::println)).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}