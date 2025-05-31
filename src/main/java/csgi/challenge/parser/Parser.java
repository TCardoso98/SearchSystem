package csgi.challenge.parser;

import csgi.challenge.token.Token;
import csgi.challenge.token.Tokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class Parser implements Runnable {
	private final Queue<Token> destiny;
	private final Tokenizer tokenizer;
	private final InputStreamReader in;
	private final CompletableFuture<Void> future;

	public Parser(InputStreamReader in) throws IOException {
		this.in = in;
		this.tokenizer = new Tokenizer(in);
		this.destiny = new ConcurrentLinkedQueue();
		this.future = new CompletableFuture();
	}

	public Parser(String filePath) throws IOException {
		this(new FileReader(filePath));
		this.future.thenAccept((unused) -> {
			try {
				this.in.close();
			} catch (IOException _) {
			}

		});
	}

	public void run() {
		try {
			while (this.tokenizer.hasNext()) {

				Token token = this.tokenizer.next();
				if (token != null && token.value() != null) {
					this.destiny.add(token);
				}

			}
			this.future.complete(null);
		} catch (IOException e) {
			this.future.completeExceptionally(e);
		}

	}

	public Token getToken() {
		return (Token) this.destiny.poll();
	}

	public CompletableFuture<Void> get() {
		return this.future;
	}

	public void execute(Executor executor) {
		executor.execute(this);
	}
}