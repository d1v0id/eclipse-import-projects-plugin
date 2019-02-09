package com.seeq.eclipse.utils;

import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * 
 * @author Lamb Gao, Paul Verest
 */
public class Log {

	private static final String PLUGIN_ID = "eclipse-import-projects-plugin"; //$NON-NLS-1$
	private static final String UNEXPECTED_EXCEPTION_MESSAGE = "Unexpected Exception";

	public void info(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}

	public void error(Throwable exception) {
		log(IStatus.ERROR, IStatus.ERROR, UNEXPECTED_EXCEPTION_MESSAGE, exception);
	}

	private void log(int severity, int code, String message, Throwable exception) {
		IStatus status = new Status(severity, PLUGIN_ID, code, message, exception);
		StatusManager.getManager().handle(status, StatusManager.LOG);
	}	

}
