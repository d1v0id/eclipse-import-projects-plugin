package ru.divoid.eclipse.bring.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class CommandLine {

	private static final String OPTION_KEY = "-";
	private static final String OPTION_KEY_REGEXP = "^" + OPTION_KEY + "[a-z]+$";
	private static final String UNSUPPORTED_ARGMENT = "Error: Unsupported argument: %s";
	
	private final Log log = new Log();
	private final List<String> options;
	private final Map<String, List<String>> parsedArguments;
	
	public CommandLine(List<String> options) {
		this.options = options;
		this.parsedArguments = new HashMap<>();
	}
	
	public void parse() {
		BundleContext context = Activator.getContext();
		ServiceReference<?> serviceReference = context.getServiceReference(IApplicationContext.class.getName());
		IApplicationContext applicationContext = (IApplicationContext) context.getService(serviceReference);
		String[] args = (String[]) applicationContext.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		String currentKey = null;

		for (String argument : args) {
			if (isSupportedOption(argument)) {
				currentKey = argumentToOption(argument);
				parsedArguments.put(currentKey, new ArrayList<>());
			} else if (currentKey != null) {
				parsedArguments.get(currentKey).add(argument);
			}
		}
	}
	
	public List<String> valuesOf(String option) {
		return parsedArguments.get(option);
	}
	
	private boolean isSupportedOption(String argument) {
		if (!argument.matches(OPTION_KEY_REGEXP)) {
			return false;
		}
		
		String option = argumentToOption(argument);
		boolean contains =  options.contains(option);

		if (!contains) {
			log.info(String.format(UNSUPPORTED_ARGMENT, option));
		}

		return contains;
	}
	
	private String argumentToOption(String argument) {
		return argument.substring(OPTION_KEY.length());
	}
}
