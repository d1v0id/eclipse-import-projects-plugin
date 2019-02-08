package ru.divoid.eclipse.bring.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    private static final String IMPORT_ARGUMENT = "-import";
    
    private static final String SEARCHING_MESSAGE = "Searching for projects in %s";
    private static final String REFRESHING_MESSAGE = "Refreshing project %s %s";
    private static final String IMPORTING_PROJECT = "Importing project %s %s";
    private static final String PROJECT_PATTERN = "\\.project";
    
    private final Log log = new Log();
    
    @Override
    public void earlyStartup() {
        List<String> importPaths = getImportPaths();

        for (String path : importPaths) {
        	log.info(String.format(SEARCHING_MESSAGE, path));
            List<File> projectFiles = findFilesRecursively(path, PROJECT_PATTERN, new ArrayList<>());
            projectFiles.forEach(this::importProject);
        }
    }

	private List<String> getImportPaths() {
        BundleContext context = Activator.getContext();
        ServiceReference<?> serviceReference = context.getServiceReference(IApplicationContext.class.getName());
        IApplicationContext applicationContext = (IApplicationContext) context.getService(serviceReference);
        String[] args = (String[]) applicationContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        List<String> importPaths = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.compareToIgnoreCase(IMPORT_ARGUMENT) == 0) {
                i++;
                if (i < args.length) {
                    importPaths.add(args[i]);
                }
            }
        }

        return importPaths;
    }

    private List<File> findFilesRecursively(String path, String pattern, List<File> returnedList) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return returnedList;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                findFilesRecursively(file.getAbsolutePath(), pattern, returnedList);
            } else if (Pattern.matches(pattern, file.getName())) {
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
