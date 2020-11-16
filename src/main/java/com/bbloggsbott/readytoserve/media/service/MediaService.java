package com.bbloggsbott.readytoserve.media.service;

import com.bbloggsbott.readytoserve.application.dto.SettingsDTO;
import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
import com.bbloggsbott.readytoserve.media.util.MediaFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class MediaService {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    MediaFileUtil mediaFileUtil;

    private SettingsDTO settingsDTO;

    @PostConstruct
    public void init(){
        settingsDTO = settingsService.getSettings();
    }

    public byte[] getFileAsByteArray(String filename) throws IOException {
        File file = getFile(filename);
        if (!file.exists()){
            log.error("Resource {} not found", filename);
            throw new ResourceNotFoundException(filename);
        }
        try{
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            log.error("Error while converting file {} to byte array", filename);
            throw ex;
        }
    }

    private File getFile(String filename){
        String filepath = Paths.get(settingsDTO.getFilesDir(), filename).toString();
        log.info("Getting file from {}", filepath);
        return new File(filepath);
    }

    public HttpHeaders getHeaders(String filename){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaFileUtil.getMediaTypeFromFileName(filename));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }
}
