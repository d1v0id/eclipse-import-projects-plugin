package com.seeq.eclipse;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class Application implements IApplication {
	
	private static BundleContext bundleContext;

	@Override
	public Object start(IApplicationContext iac) throws Exception {
		bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		new Startup().earlyStartup();
		return null;
	}

	@Override
	public void stop() {
		// no implementation
	}
	
	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}
