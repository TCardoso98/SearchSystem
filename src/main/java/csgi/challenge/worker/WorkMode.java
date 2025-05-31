package csgi.challenge.worker;

import csgi.challenge.WorkerFactory;

public enum WorkMode {
	START_WITH_COUNTER(WordCounterWorker::new),
	LONGER_THAN(WordLengthWorker::new);

	public final WorkerFactory value;

	WorkMode(WorkerFactory workerSupplier) {
		this.value = workerSupplier;
	}
}