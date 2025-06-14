package csgi.challenge.worker;

import csgi.challenge.Result;
import csgi.challenge.token.Token;
import csgi.challenge.token.TokenType;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WordLengthWorker extends WorkerAbstract<Token, Result<String[]>> {
	private final Collection<String> words = new ConcurrentLinkedQueue<>();
	//private static final int MAX_WORD_SIZE = -1;
	private static final int MIN_WORD_SIZE = 5;


	@Override
	protected void onToken(Token token) {
		if (token.type() == TokenType.WORD) {
			String value = token.value();
			int length = value.length();
			if (length >= MIN_WORD_SIZE) {
				this.words.add(token.value());
			}
		}

	}

	@Override
	protected Result<String[]> get() {
		return new Result<>() {
			public String[] get() {
				return WordLengthWorker.this.words.toArray(new String[0]);
			}

			public String toString() {
				return WordLengthWorker.this.words.toString();
			}
		};
	}

	public WorkMode getMode() {
		return WorkMode.LONGER_THAN_5;
	}
}