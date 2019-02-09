package com.seeq.eclipse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.ui.IStartup;

import com.seeq.eclipse.internal.ImportProjects;
import com.seeq.eclipse.utils.CommandLine;
import com.seeq.eclipse.utils.Log;

public class Startup implements IStartup {

	private static final String IMPORT_ARGUMENT = "import"; //$NON-NLS-1$
	private static final String EXCLUDE_ARGUMENT = "exclude"; //$NON-NLS-1$
	private static final String SEARCHING_MESSAGE = "Searching for projects in %s";
	private static final String PROJECT_PATTERN = "\\.project"; //$NON-NLS-1$

	private final Log log = new Log();

	private List<String> importPaths;
	private List<String> excludeProjects;
	private List<File> projects;

	@Override
	public void earlyStartup() {
		parseCommandLine();
		findProjectsForImport();
		ImportProjects importProjects = new ImportProjects(projects);
		importProjects.handle();
	}

	private void parseCommandLine() {
		CommandLine commandLine = new CommandLine(Arrays.asList(IMPORT_ARGUMENT, EXCLUDE_ARGUMENT));
		commandLine.parse();
		importPaths = commandLine.valuesOf(IMPORT_ARGUMENT);
		excludeProjects = commandLine.valuesOf(EXCLUDE_ARGUMENT);
	}

	private void findProjectsForImport() {
		projects = new ArrayList<>();

		for (String path : importPaths) {
			log.info(String.format(SEARCHING_MESSAGE, path));
			List<File> projectFiles = findFilesRecursively(path, new ArrayList<>());
			projects.addAll(projectFiles);
		}
	}

	private List<File> findFilesRecursively(String path, List<File> returnedList) {
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null) {
			return returnedList;
		}

		for (File file : list) {
			if (file.isDirectory()) {
				findFilesRecursively(file.getAbsolutePath(), returnedList);
			} else if (Pattern.matches(PROJECT_PATTERN, file.getName())) {
				String projectName = file.getParentFile().getName();
				if (!excludeProjects.contains(projectName)) {
					returnedList.add(file);
				}
			}
		}

		return returnedList;
	}

}
