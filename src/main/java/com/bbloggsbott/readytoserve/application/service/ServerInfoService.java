package com.bbloggsbott.readytoserve.application.service;

import com.bbloggsbott.readytoserve.application.dto.ServerInfoDTO;
import com.bbloggsbott.readytoserve.application.util.ServerUtil;
import com.bbloggsbott.readytoserve.pages.service.PagePathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@Service
public class ServerInfoService {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private PagePathService pagePathService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServerInfoDTO serverInfo;

    @PostConstruct
    private void init() throws IOException {
        logger.info("Reading server info");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String serverInfoFilename = settingsService.getSettings().getServerInfoFile();
        File serverInfoFile = new File(serverInfoFilename);
        if (!serverInfoFile.exists()){
            logger.info("Server info file doesn't exist");
            throw new FileNotFoundException("Server Info file not found");
        }
        serverInfo = mapper.readValue(serverInfoFile, ServerInfoDTO.class);
        if (serverInfo.getAvatar() == null || serverInfo.getAvatar().isEmpty()){
            serverInfo.setAvatar(settingsService.getSettings().getServerImageUrl());
        }
        ServerUtil.prepareOnlineURLs(serverInfo);
        serverInfo.setRoutes(pagePathService.getBasePaths());
        logger.info("Server info loading complete");
    }

    public ServerInfoDTO getServerInfo(){
        return serverInfo;
    }

}
