package csgi.challenge.worker;

import csgi.challenge.WorkerFactory;

import java.util.function.Supplier;

public enum WorkMode {
	START_WITH_COUNTER(WordCounterWorker::new),
	LONGER_THAN(WordLengthWorker::new);

	public final Supplier<Worker<?>> value;

	WorkMode(Supplier<Worker<?>> workerSupplier) {
		this.value = workerSupplier;
	}
}