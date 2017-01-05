package com.cisco.env;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class EnvUtil {
	final static Logger log = LoggerFactory.getLogger(EnvUtil.class);
	static Properties props = null;

	static void loadProperties(final String filename) {
		props = new Properties();
		for ( String path : Arrays.asList(filename, "conf/" + filename, "classpath://" + filename) ) {
			try ( InputStream is = (path.startsWith("classpath://")
					? Thread.currentThread().getContextClassLoader().getResourceAsStream(path.replace("classpath://", ""))
							: new FileInputStream(path)) ) {
				log.debug("Loading properties from {}", path);
				try {
					props.load(is);
				} catch (IOException e) {
					log.error("Failed load properties from inputstream " + path, e);
				}
			} catch (IOException ioe) {
				log.debug("Failed to load properties from " + path, ioe);
			}
		}
	}
	/**
	 * To return the value for the given If the system environment variable is
	 * null then return value from the properties
	 *
	 * @param @
	 */
	public static String get(String propertyName) {
		String propertyValue = System.getenv(propertyName);
		if (propertyValue == null) {
			if (props == null) {
				loadProperties("config.properties");
			}
			propertyValue = props.getProperty(propertyName);
		}
		return propertyValue;
	}
}