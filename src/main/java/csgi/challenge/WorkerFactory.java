package csgi.challenge;

import csgi.challenge.worker.WorkerAbstract;

public interface WorkerFactory {
   WorkerAbstract<?> get(int var1);
}