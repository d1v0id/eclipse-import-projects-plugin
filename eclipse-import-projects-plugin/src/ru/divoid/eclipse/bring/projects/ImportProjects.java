package ru.divoid.eclipse.bring.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IStartup;

public class ImportProjects implements IStartup {

	private static final String IMPORT_ARGUMENT = "import";
	private static final String EXCLUDE_ARGUMENT = "exclude";
	private static final String SEARCHING_MESSAGE = "Searching for projects in %s";
	private static final String REFRESHING_MESSAGE = "Refreshing project %s %s";
	private static final String IMPORTING_PROJECT = "Importing project %s %s";
	private static final String PROJECT_PATTERN = "\\.project";

	private final Log log = new Log();

	private List<String> excludeProjects = new ArrayList<>();

	@Override
	public void earlyStartup() {
		CommandLine commandLine = new CommandLine(Arrays.asList(IMPORT_ARGUMENT, EXCLUDE_ARGUMENT));
		commandLine.parse();
		List<String> importPaths = commandLine.valuesOf(IMPORT_ARGUMENT);
		excludeProjects = commandLine.valuesOf(EXCLUDE_ARGUMENT);

		for (String path : importPaths) {
			log.info(String.format(SEARCHING_MESSAGE, path));
			List<File> projectFiles = findFilesRecursively(path, new ArrayList<>());
			projectFiles.forEach(this::importProject);
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

	private void importProject(File projectFile) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			Path projectFilePath = new Path(projectFile.toString());
			IProjectDescription description = workspace.loadProjectDescription(projectFilePath);
			IProject project = workspace.getRoot().getProject(description.getName());

			if (project.isOpen()) {
				log.info(String.format(REFRESHING_MESSAGE, description.getName(), description.getLocationURI()));
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} else {
				log.info(String.format(IMPORTING_PROJECT, description.getName(), description.getLocationURI()));
				project.create(description, null);
				project.open(null);
			}
		} catch (CoreException e) {
			log.error(e);
		}
	}

}
