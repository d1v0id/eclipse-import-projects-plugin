package ru.divoid.eclipse.bring.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ImportProjects implements IStartup {

	private static final String IMPORT_ARGUMENT = "i";
	private static final String EXCLUDE_ARGUMENT = "e";
	private static final String IMPORT_LONG_ARGUMENT = "import";
	private static final String EXCLUDE_LONG_ARGUMENT = "exclude";
	private static final String IMPORT_ARGUMENT_DESCRIPTION = "Paths for searching projects for import";
	private static final String EXCLUDE_ARGUMENT_DESCRIPTION = "Project names which should be excluded";
	private static final String VALUE_SEPARATOR = " ";
	private static final String SEARCHING_MESSAGE = "Searching for projects in %s";
	private static final String REFRESHING_MESSAGE = "Refreshing project %s %s";
	private static final String IMPORTING_PROJECT = "Importing project %s %s";
	private static final String PROJECT_PATTERN = "\\.project";

	private final Log log = new Log();

	private List<String> importPaths = new ArrayList<>();
	private List<String> excludeProjects = new ArrayList<>();

	@Override
	public void earlyStartup() {
		parseArguments();

		for (String path : importPaths) {
			log.info(String.format(SEARCHING_MESSAGE, path));
			List<File> projectFiles = findFilesRecursively(path, new ArrayList<>());
			projectFiles.forEach(this::importProject);
		}
	}

	private void parseArguments() {
		BundleContext context = Activator.getContext();
		ServiceReference<?> serviceReference = context.getServiceReference(IApplicationContext.class.getName());
		IApplicationContext applicationContext = (IApplicationContext) context.getService(serviceReference);
		String[] args = (String[]) applicationContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		
		Option importOption = createArgument(IMPORT_ARGUMENT, IMPORT_LONG_ARGUMENT, IMPORT_ARGUMENT_DESCRIPTION, true);
		Option excludeOption = createArgument(EXCLUDE_ARGUMENT, EXCLUDE_LONG_ARGUMENT, EXCLUDE_ARGUMENT_DESCRIPTION,
				false);

		Options options = new Options();
		options.addOption(importOption);
		options.addOption(excludeOption);
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine commandLine = parser.parse(options, args);
			importPaths = parseArgumentValue(commandLine, IMPORT_ARGUMENT);
			excludeProjects = parseArgumentValue(commandLine, EXCLUDE_ARGUMENT);
		} catch (ParseException e) {
			log.error(e);
		}
	}
	
	private Option createArgument(String name, String longName, String description, boolean required) {
		return Option.builder(name).required(required).desc(description).longOpt(longName).build();
	}
	
	private List<String> parseArgumentValue(CommandLine commandLine, String argument) {
		List<String> values = new ArrayList<>();

		if (commandLine.hasOption(argument)) {
			String importPathsValue = commandLine.getOptionValue(argument);
			values = Stream.of(importPathsValue.split(VALUE_SEPARATOR)).collect(Collectors.toList());
		}

		return values;
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
			} else if (Pattern.matches(PROJECT_PATTERN, file.getName()) &&
					!excludeProjects.contains(file.getParent())) {
				returnedList.add(file);
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
