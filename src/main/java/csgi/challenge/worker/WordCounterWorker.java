package csgi.challenge.worker;


import csgi.challenge.token.Token;

import java.util.concurrent.atomic.AtomicInteger;

public class WordCounterWorker extends WorkerAbstract<Token, Integer> {
	private final AtomicInteger counter = new AtomicInteger();


	@Override
	protected void onToken(Token token) {
		if (!token.value().isEmpty()) {
			char firstChar = token.value().charAt(0);
			if (firstChar == 'm' || firstChar == 'M') {
				this.counter.incrementAndGet();

			}

		}
	}

	@Override
	protected Integer get() {
		return counter.get();
	}

	public WorkMode getMode() {
		return WorkMode.START_WITH_M_COUNTER;
	}
}