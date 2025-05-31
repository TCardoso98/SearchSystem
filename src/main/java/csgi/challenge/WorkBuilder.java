package csgi.challenge;

import csgi.challenge.broadcaster.Broadcaster;
import csgi.challenge.configuration.Configuration;
import csgi.challenge.parser.Parser;
import csgi.challenge.worker.Worker;
import csgi.challenge.worker.WorkerAbstract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorkBuilder {
   private final Map<String, Broadcaster> broadcasterMap = new ConcurrentHashMap();
   private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
   private final List<Configuration> configurations = new ArrayList();
   private final List<Worker<?>> workers = new ArrayList();

   public WorkBuilder() {
   }

   public WorkBuilder addConfiguration(Configuration configuration) {
      this.configurations.add(configuration);
      return this;
   }

   public WorkBuilder build() {
      for(Configuration configuration : this.configurations) {
         Worker<?> worker = configuration.mode.value.get(configuration.workerInstanceNumber);

         for(String filePath : configuration.filePaths) {
            ((Broadcaster)this.broadcasterMap.computeIfAbsent(filePath, (s) -> this.getBroadcaster(s, configuration.parserInstanceNumber))).addWorker(worker);
         }

         this.workers.add(worker);
      }

      return this;
   }

   public Worker<?>[] execute() {
      for(Broadcaster value : this.broadcasterMap.values()) {
         value.execute(this.executor);
      }

      return (Worker[])this.workers.toArray(new Worker[0]);
   }

   private Broadcaster getBroadcaster(String s, int parserInstanceNumber) {
      try {
         Parser parser = new Parser(s);
         return new Broadcaster(parser, parserInstanceNumber, new WorkerAbstract[0]);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}