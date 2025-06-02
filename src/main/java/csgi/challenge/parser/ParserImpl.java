package csgi.challenge.parser;

import csgi.challenge.token.Token;
import csgi.challenge.token.Tokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class ParserImpl extends ParserAbstract<Token> {
	private final Tokenizer tokenizer;
	private final InputStreamReader in;
	private final BlockingQueue<Token> queue;

	public ParserImpl(InputStreamReader in) throws IOException {
		super();
		this.in = in;
		this.tokenizer = new Tokenizer(in);
		this.queue = new ArrayBlockingQueue<>(100);
	}

	public ParserImpl(String filePath) throws IOException {
		this(new FileReader(filePath));
	}

	public boolean hasNext() {
		return !complete.get() && tokenizer.hasNext();
	}

	@Override
	public boolean isComplete() {
		return complete.get() && queue.peek() == null;
	}

	public Token getToken() {
		if (complete.get()) {
			throw new IllegalStateException("Parser already closed");
		}
		Token result = tokenizer.next();
		complete.compareAndSet(false, !this.tokenizer.hasNext());
		return result;

	}

	public Token poll() {
		return queue.poll();
	}


	public void close() {
		super.close();
		try {
			in.close();
		} catch (IOException ignored) {
		}
	}

	@Override
	public void run() {
		while (hasNext()) {
			Token token = this.getToken();
			for (; token != null; ) {
				try {
					queue.put(token);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		complete.compareAndSet(false, true);
	}
}