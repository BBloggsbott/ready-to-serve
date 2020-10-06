package com.bbloggsbott.readytoserve.application.service;

import com.bbloggsbott.readytoserve.application.dto.SettingsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class SettingsService {

    @Value("${profile.settings.file}")
    private String settingsFileName;

    private SettingsDTO settings;

    private final String gravatarUrl = "https://www.gravatar.com/avatar/";
    private final String DEFAULT_DATETIME_FORMAT = "dd-MM-yyyy";

    @PostConstruct
    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File settingsFile = new File(settingsFileName);
        if (!settingsFile.exists()){
            throw new FileNotFoundException(settingsFileName + " file not found");
        }
        settings = mapper.readValue(settingsFile, SettingsDTO.class);
        createDefaultImageUrl();
        processPaths();
        if (settings.getDateTimeFormat() == null){
            settings.setDateTimeFormat(DEFAULT_DATETIME_FORMAT);
        }
    }

    /**
     * Uses the emails in settings to create a gravatar url
     */
    private void createDefaultImageUrl() throws NoSuchAlgorithmException {
        String email = settings.getEmail();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String hashEmail = new BigInteger(1, md.digest(email.trim().getBytes())).toString(16);
        if (settings.getServerImageSize() != null){
            settings.setServerImageUrl(String.format("%s%s?s=%s", gravatarUrl, hashEmail, settings.getServerImageSize()));
            return;
        }
        settings.setServerImageUrl(String.format("%s%s", gravatarUrl, hashEmail));
    }

    private void processPaths(){
        if (settings.getBaseDir() == "."){
            settings.setBaseDir(System.getProperty("user.dir"));
        }
        settings.setDataDir(Paths.get(settings.getBaseDir(), settings.getDataDir()).toString());
        settings.setServerInfoFile(Paths.get(settings.getDataDir(), settings.getServerInfoFile()).toString());
        settings.setFilesDir(Paths.get(settings.getBaseDir(), settings.getFilesDir()).toString());
        settings.setPagesDir(Paths.get(settings.getBaseDir(), settings.getPagesDir()).toString());
    }

    public SettingsDTO getSettings() {
        return settings;
    }
}
