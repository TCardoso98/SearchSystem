package csgi.challenge.configuration;

import com.beust.jcommander.Parameter;
import csgi.challenge.worker.WorkMode;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
	public static final String WORK_MODE_DESCRIPTION =
			"""
					type of processing displayed\
					
					\tAvailable:\
					
					\t\tSTART_WITH_M_COUNTER - Counts how many words start with m or M\
					
					\t\tLONGER_THAN_5 - Displays all words bigger than 5 characters""";

	@Parameter(names = {"-w", "--workMode"}, description = WORK_MODE_DESCRIPTION, converter = WorkModeConverter.class, required = true)
	public WorkMode mode;
	@Parameter(names = {"-f", "--files"}, description = "Comma-separated list of group names to be run", required = true)
	public List<String> filePaths = new ArrayList<>();
	@Parameter(names = {"--help", "-h"}, help = true)
	public boolean help = false;

	private static class WorkModeConverter implements com.beust.jcommander.IStringConverter<WorkMode> {
		@Override
		public WorkMode convert(String value) {
			return WorkMode.valueOf(value);
		}
	}
}