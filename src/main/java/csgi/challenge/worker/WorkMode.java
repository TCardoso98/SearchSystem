package csgi.challenge.worker;


import java.util.function.Supplier;

public enum WorkMode {
	START_WITH_M_COUNTER(WordCounterWorker::new),
	LONGER_THAN_5(WordLengthWorker::new);

	public final Supplier<Worker<?, ?>> value;

	WorkMode(Supplier<Worker<?, ?>> workerSupplier) {
		this.value = workerSupplier;
	}
}