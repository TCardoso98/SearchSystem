package csgi.challenge.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurationBuilder {
	private final List<String> paths = Collections.synchronizedList(new ArrayList<>());
	private String mode;
	private String workName;

	private ConfigurationBuilder() {
	}

	public void addPath(String path) {
		this.paths.add(path);
	}

	public void addAll(String[] filePaths) {
		for (String filePath : filePaths) {
			this.addPath(filePath);
		}

	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Configuration build() {
		return new Configuration(this.workName, this.mode, this.paths.toArray(new String[0]));
	}
}