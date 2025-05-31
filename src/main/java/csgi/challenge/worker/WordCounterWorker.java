package csgi.challenge.worker;


import csgi.challenge.Result;
import csgi.challenge.token.Token;

import java.util.concurrent.atomic.AtomicInteger;

public class WordCounterWorker extends WorkerAbstract<Integer> {
	private final AtomicInteger counter = new AtomicInteger();

	WordCounterWorker(int numberOfInstances) {
		super(numberOfInstances);
	}

	@Override
	protected void onToken(Token token) {
		if (!token.value().isEmpty() && token.isWord()) {
			char firstChar = token.value().charAt(0);
			if (firstChar == 'm' || firstChar == 'M') {
				this.counter.incrementAndGet();
			}

		}
	}

	@Override
	protected Result<Integer> get() {
		return new Result<>() {
			private final int result;

			{
				this.result = WordCounterWorker.this.counter.get();
			}

			public Integer get() {
				return this.result;
			}

			public String toString() {
				return String.valueOf(this.result);
			}
		};
	}

	public WorkMode getMode() {
		return WorkMode.START_WITH_COUNTER;
	}
}