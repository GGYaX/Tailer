package me.yanxin.tailserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
	private static final PropertiesLoader INSTANCE = new PropertiesLoader();

	private static Properties properties;

	private static final String PROPERTIES_FILES = "config_server.properties";

	public static final PropertiesLoader getInstance() {
		return INSTANCE;
	}

	/**
	 * Load fichier properties.
	 * 
	 */
	public static void loadFichiersProperties() {
		properties = getInstance()._loadProperties(PROPERTIES_FILES);
	}

	private Properties _loadProperties(String pFilepath) {
		Properties properties = new Properties();
		try {
			InputStream vInput = getClass().getClassLoader()
					.getResourceAsStream(pFilepath);
			if (vInput == null) {
				throw new IOException(pFilepath + " inexistant");
			}
			properties.load(vInput);
			vInput.close();

		} catch (IOException vEx) {
			System.out
					.println("Erreur lors du chargement du fichier properties classpath:"
							+ pFilepath);
		}
		return properties;
	}
}
