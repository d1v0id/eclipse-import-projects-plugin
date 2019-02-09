package ru.divoid.eclipse.bring.projects;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext iac) throws Exception {
		new Startup().earlyStartup();
		return null;
	}

	@Override
	public void stop() {
		// no implementation
	}

}
