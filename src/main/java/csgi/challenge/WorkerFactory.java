package csgi.challenge;

import csgi.challenge.worker.Worker;

public interface WorkerFactory {
	Worker<?> get(int var1);
}