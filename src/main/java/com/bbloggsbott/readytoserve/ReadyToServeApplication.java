package com.bbloggsbott.readytoserve;

import com.bbloggsbott.readytoserve.application.util.DefaultSetUpCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class ReadyToServeApplication {

	public static void main(String[] args) throws IOException {
		File settingsFile = new File("settings.json");
		if (!settingsFile.exists()){
			DefaultSetUpCreator defaultSetUpCreator = new DefaultSetUpCreator();
			defaultSetUpCreator.doSetUp();
		}
		SpringApplication.run(ReadyToServeApplication.class, args);
	}

}
