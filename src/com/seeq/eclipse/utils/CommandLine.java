package com.seeq.eclipse.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

public class CommandLine {

	private static final String OPTION_KEY = "-"; //$NON-NLS-1$
	private static final String OPTION_KEY_REGEXP = "^" + OPTION_KEY + "[a-z]+$"; //$NON-NLS-1$
	private static final String UNSUPPORTED_ARGUMENT_MESSAGE = "Error: Unsupported argument: %s";

	private final Log log = new Log();
	private final List<String> options;
	private final Map<String, List<String>> parsedArguments;

	public CommandLine(List<String> options) {
		this.options = options;
		this.parsedArguments = new HashMap<>();
	}

	public void parse() {
		String[] args = Platform.getApplicationArgs();
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
		return parsedArguments.getOrDefault(option, new ArrayList<>());
	}

	private boolean isSupportedOption(String argument) {
		if (!argument.matches(OPTION_KEY_REGEXP)) {
			return false;
		}

		String option = argumentToOption(argument);
		boolean contains = options.contains(option);

		if (!contains) {
			log.info(String.format(UNSUPPORTED_ARGUMENT_MESSAGE, option));
		}

		return contains;
	}

	private String argumentToOption(String argument) {
		return argument.substring(OPTION_KEY.length());
	}
}
