package csgi.challenge.broadcaster;

import csgi.challenge.parser.Parser;
import csgi.challenge.token.Token;
import csgi.challenge.worker.Worker;
import csgi.challenge.worker.WorkerAbstract;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class Broadcaster implements Runnable {
	private final int nInstances;
	private final List<Worker<?>> workers;
   private final Parser parser;
   private volatile boolean close;


   public Broadcaster(Parser parser, int nInstances, WorkerAbstract<?>... workers) {
	   this.nInstances = nInstances;
	   this.workers = Arrays.asList(workers);
      this.close = false;
      this.parser = parser;
      parser.get().handle((var1, var2) -> this.close = true);
   }

   public void addWorker(Worker<?> worker) {
      this.workers.add(worker);
   }

   public void run() {
      for(Token token = this.parser.getToken(); !this.close || token != null; token = this.parser.getToken()) {
         if (token != null && token.value() != null) {
            for(Worker<?> worker : this.workers) {
               worker.process(token);
            }
         }
      }

      this.complete();
   }

   public void complete() {
      for(Worker<?> worker : this.workers) {
         worker.complete();
      }

   }

   public void execute(Executor executor) {
      for(Worker<?> worker : this.workers) {
         worker.execute(executor);
      }
      for (int i = 0; i <this.nInstances; i++) {
         executor.execute(this);
      }
      this.parser.execute(executor);
   }
}