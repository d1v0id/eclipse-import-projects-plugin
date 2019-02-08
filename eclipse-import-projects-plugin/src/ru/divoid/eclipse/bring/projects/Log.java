package ru.divoid.eclipse.bring.projects;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * 
 * @author Lamb Gao, Paul Verest
 */
public class Log {
	
	private static final String UNEXPECTED_EXCEPTION_MESSAGE = "Unexpected Exception";

    public void info(String message) {
        log(IStatus.INFO, IStatus.OK, message, null);
    }

    public void error(Throwable exception) {
        log(IStatus.ERROR, IStatus.ERROR, UNEXPECTED_EXCEPTION_MESSAGE, exception);
    }

    private void log(int severity, int code, String message, Throwable exception) {
        IStatus status = new Status(severity, Activator.PLUGIN_ID, code, message, exception);
        ILog log = Activator.getDefault().getLog();
        log.log(status);
    }

}
