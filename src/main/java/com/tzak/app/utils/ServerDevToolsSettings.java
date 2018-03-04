package com.tzak.app.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Włąsna implementacja org.springframework.boot.devtools.settings.DevToolsSettings
 * Wykorzystujemy te funkcjonalności po stronie aplikacji klienta (config-app) dlatego musimy to zaimplementować żeby w runnable Jarze korzystać z tego
 */
public class ServerDevToolsSettings {

	public static final String SETTINGS_RESOURCE_LOCATION = "META-INF/spring-devtools.properties";

	private static final Log logger = LogFactory.getLog(ServerDevToolsSettings.class);

	private static ServerDevToolsSettings settings;

	private final List<Pattern> restartIncludePatterns = new ArrayList<Pattern>();

	private final List<Pattern> restartExcludePatterns = new ArrayList<Pattern>();

	ServerDevToolsSettings() {
	}

	void add(Map<?, ?> properties) {
		Map<String, Pattern> includes = getPatterns(properties, "restart.include.");
		this.restartIncludePatterns.addAll(includes.values());
		Map<String, Pattern> excludes = getPatterns(properties, "restart.exclude.");
		this.restartExcludePatterns.addAll(excludes.values());
	}

	private Map<String, Pattern> getPatterns(Map<?, ?> properties, String prefix) {
		Map<String, Pattern> patterns = new LinkedHashMap<String, Pattern>();
		for (Map.Entry<?, ?> entry : properties.entrySet()) {
			String name = String.valueOf(entry.getKey());
			if (name.startsWith(prefix)) {
				Pattern pattern = Pattern.compile((String) entry.getValue());
				patterns.put(name, pattern);
			}
		}
		return patterns;
	}

	public boolean isRestartInclude(URL url) {
		return isMatch(url.toString(), this.restartIncludePatterns);
	}

	public boolean isRestartExclude(URL url) {
		return isMatch(url.toString(), this.restartExcludePatterns);
	}

	private boolean isMatch(String url, List<Pattern> patterns) {
		for (Pattern pattern : patterns) {
			if (pattern.matcher(url).find()) {
				return true;
			}
		}
		return false;
	}

	public static ServerDevToolsSettings get() {
		if (settings == null) {
			settings = load();
		}
		return settings;
	}

	static ServerDevToolsSettings load() {
		return load(SETTINGS_RESOURCE_LOCATION);
	}

	static ServerDevToolsSettings load(String location) {
		try {
			ServerDevToolsSettings settings = new ServerDevToolsSettings();
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
					.getResources(location);
			while (urls.hasMoreElements()) {
				settings.add(PropertiesLoaderUtils
						.loadProperties(new UrlResource(urls.nextElement())));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Included patterns for restart : "
						+ settings.restartIncludePatterns);
				logger.debug("Excluded patterns for restart : "
						+ settings.restartExcludePatterns);
			}
			return settings;
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable to load devtools settings from "
					+ "location [" + location + "]", ex);
		}
	}

}
