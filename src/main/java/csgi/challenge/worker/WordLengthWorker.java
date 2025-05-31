package csgi.challenge.worker;

import csgi.challenge.Result;
import csgi.challenge.token.Token;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WordLengthWorker extends WorkerAbstract<String[]> {
   private final Collection<String> words = new ConcurrentLinkedQueue<>();
   private static final int MAX_WORD_SIZE = 1;
   private static final int MIN_WORD_SIZE = 1;

   WordLengthWorker(int numberOfInstances) {
      super(numberOfInstances);
   }

   protected void onToken(Token token) throws Exception {
      if (token.isWord()) {
         int length = token.value().length();
         if (length >= MIN_WORD_SIZE && length <= MAX_WORD_SIZE) {
            this.words.add(token.value());
         }
      }

   }

   protected Result<String[]> get() {
      return new Result<String[]>() {
         public String[] get() {
            return (String[])WordLengthWorker.this.words.toArray(new String[0]);
         }

         public String toString() {
            return WordLengthWorker.this.words.toString();
         }
      };
   }

   public WorkMode getMode() {
      return WorkMode.LONGER_THAN;
   }
}