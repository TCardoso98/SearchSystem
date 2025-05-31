package csgi.challenge;

import csgi.challenge.broadcaster.Broadcaster;
import csgi.challenge.parser.Parser;
import csgi.challenge.worker.WorkMode;
import csgi.challenge.worker.WorkerAbstract;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
   private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

   public Main() {
   }

   public static void main(String[] args) {
      try {
         FileReader fileReader = new FileReader("random_text_300mb.txt");

         try {
            WorkerAbstract<?> counterWorker = WorkMode.START_WITH_COUNTER.value.get(3);
            WorkerAbstract<?> wordLengthWorkerWorker = WorkMode.LONGER_THAN.value.get(3);
            Parser parser = new Parser(fileReader);
            Broadcaster broadcaster = new Broadcaster(parser, 2, new WorkerAbstract[]{counterWorker,
                    wordLengthWorkerWorker});
            broadcaster.execute(executor);
            long start = System.currentTimeMillis();
            CompletableFuture[] var10000 = new CompletableFuture[2];
            CompletableFuture var10003 = counterWorker.getResultAsync();
            PrintStream var10004 = System.out;
            Objects.requireNonNull(var10004);
            var10000[0] = var10003.thenAccept(var10004::println);
            var10003 = wordLengthWorkerWorker.getResultAsync();
            var10004 = System.out;
            Objects.requireNonNull(var10004);
            var10000[1] = var10003.thenAccept(var10004::println);
            CompletableFuture.allOf(var10000).thenAccept((unused) -> System.out.println("Duration: " + (System.currentTimeMillis() - start))).exceptionally((throwable) -> {
               System.err.println(throwable);
               return null;
            }).get();
         } catch (Throwable var9) {
            try {
               fileReader.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         fileReader.close();
      } catch (ExecutionException | InterruptedException | IOException e) {
         throw new RuntimeException(e);
      }
   }
}