package csgi.challenge.parser;

import csgi.challenge.token.Token;
import csgi.challenge.token.Tokenizer;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ParserImpl implements Parser {
	private final Tokenizer tokenizer;
	private final InputStreamReader in;
	private final AtomicBoolean isComplete;

	public ParserImpl(InputStreamReader in) throws IOException {
		this.in = in;
		this.tokenizer = new Tokenizer(in);
		this.isComplete = new AtomicBoolean(false);
	}

	public ParserImpl(String filePath) throws IOException {
		this(new FileReader(filePath));
	}

	public boolean hasNext() {
		return !isComplete.get() && tokenizer.hasNext();
	}

	public synchronized Token getToken() {
		if (isComplete.get()) {
			throw new IllegalStateException("Parser already closed");
		}

		Token result = tokenizer.next();
		if (!this.tokenizer.hasNext()) {
			close();
		}
		return result;

	}

	public void close() {
		this.isComplete.set(true);
		try {
			in.close();
		} catch (IOException ignored) {

		}
	}
}