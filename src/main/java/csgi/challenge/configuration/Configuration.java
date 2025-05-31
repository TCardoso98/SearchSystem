package csgi.challenge.configuration;

import csgi.challenge.worker.WorkMode;

public class Configuration {
   public final String workName;
   public final WorkMode mode;
   public final String[] filePaths;
   public final int workerInstanceNumber;
   public final int parserInstanceNumber;

   public Configuration(String workName, String mode, String[] filePaths) {
      this(workName, 1, mode, 1, filePaths);
   }

   public Configuration(String workName, int workerInstanceNumber, String mode, int parserInstanceNumber, String[] filePaths) {
      this.workName = workName;
      this.mode = WorkMode.valueOf(mode);
      this.filePaths = filePaths;
      this.workerInstanceNumber = workerInstanceNumber;
      this.parserInstanceNumber = parserInstanceNumber;
   }
}
