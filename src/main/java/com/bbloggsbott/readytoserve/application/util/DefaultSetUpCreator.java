package com.bbloggsbott.readytoserve.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DefaultSetUpCreator {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String DATA_DIR = "data";
	private final String PAGES_DIR = "pages";
	private final String PLUGINS_DIR = "plugins";
	private final String FILES_DIR = "files";
	private final String PLUGINS_CONFIG_FILE = "plugins.yml";
	private final String SERVER_INFO_FILE = "server_info.yml";

	private void createDefaultDirectories() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		logger.info("Creating Directories");
		File dataDir = new File(DATA_DIR);
		File pagesDir = new File(PAGES_DIR);
		File pluginsDir = new File(PLUGINS_DIR);
		File filesDir = new File(FILES_DIR);
		dataDir.mkdirs();
		pagesDir.mkdirs();
		pluginsDir.mkdirs();
		filesDir.mkdirs();

		logger.info("Creating default server info file");
		Map<String, String> defaultServerInfo = new HashMap();
		FileWriter serverInfoFile = new FileWriter(Paths.get(dataDir.getPath(), SERVER_INFO_FILE).toString());
		defaultServerInfo.put("name", "John Doe");
		defaultServerInfo.put("avatar", "");
		defaultServerInfo.put("description", "Default content of server_info file");
		defaultServerInfo.put("email", "");
		defaultServerInfo.put("twitter", "");
		defaultServerInfo.put("linkedin", "");
		defaultServerInfo.put("github", "BBloggsbott/ready-to-serve");
		defaultServerInfo.put("gitlab", "");
		serverInfoFile.write(mapper.writeValueAsString(defaultServerInfo));
		serverInfoFile.close();

		logger.info("Creating default page file");
		String defaultSamplePageContent = "---\n" +
				"pagemeta\n" +
				"title: This is the page title\n" +
				"url_path: /default_path\n" +
				"date: 13-10-2020\n" +
				"tags: sample,demo\n" +
				"excerpt: This is an excerpt for this page\n" +
				"---\n" +
				"# Sample page\n" +
				"\n" +
				"This is a sample page. Edit any content in this file and it will get reflected without the need to restart the server";
		FileWriter samplePageFile = new FileWriter(Paths.get(pagesDir.getPath(), "sample_page.md").toString());
		samplePageFile.write(defaultSamplePageContent);
		samplePageFile.close();

		logger.info("Creating default plugin config file");
		File pluginConfigFile = new File(Paths.get(pluginsDir.getPath(), PLUGINS_CONFIG_FILE).toString());
		pluginConfigFile.createNewFile();

		logger.info("Creating default media file");
		FileWriter filesSampleFile = new FileWriter(Paths.get(filesDir.getPath(), "sample_file.html").toString());
		filesSampleFile.write("<h1>Files Dir</h1><p>Add any file in this directory for direct access</p>");
		filesSampleFile.close();

		logger.info("Default Directories creation done");
	}

	private void createSettingsJson() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		logger.info("Creating default settings");
		Map<String, String> settings = new HashMap();
		settings.put("email", "");
		settings.put("base_directory", ".");
		settings.put("data_directory", DATA_DIR);
		settings.put("files_directory", FILES_DIR);
		settings.put("pages_directory", PAGES_DIR);
		settings.put("plugins_directory", PLUGINS_DIR);
		settings.put("plugins_config_file", PLUGINS_CONFIG_FILE);
		settings.put("server_image_size", "250px");
		settings.put("server_info_file", SERVER_INFO_FILE);
		settings.put("datetimeformat", "dd-MM-yyyy");
		FileWriter file = new FileWriter("settings.json");
		file.write(mapper.writeValueAsString(settings));
		file.close();
	}

	public void doSetUp() throws IOException {
		createSettingsJson();
		createDefaultDirectories();
	}

}
