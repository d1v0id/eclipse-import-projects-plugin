package ru.divoid.eclipse.bring.projects.internal;

import java.util.List;
import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import ru.divoid.eclipse.bring.projects.utils.Log;

public class ImportProjects {

	private static final String REFRESHING_MESSAGE = "Refreshing project %s %s";
	private static final String IMPORTING_MESSAGE = "Importing project %s %s";

	private final Log log = new Log();
	private final List<File> files;

	public ImportProjects(List<File> files) {
		this.files = files;
	}

	public void handle() {
		files.forEach(this::importProject);
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
				log.info(String.format(IMPORTING_MESSAGE, description.getName(), description.getLocationURI()));
				project.create(description, null);
				project.open(null);
			}
		} catch (CoreException e) {
			log.error(e);
		}
	}
}
