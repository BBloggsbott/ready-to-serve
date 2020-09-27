package com.bbloggsbott.readytoserve.media.service;

import com.bbloggsbott.readytoserve.application.dto.SettingsDTO;
import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
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
public class MediaService {

    @Autowired
    private SettingsService settingsService;

    private SettingsDTO settingsDTO;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String PDF_EXTENSION = "pdf";
    private final String JSON_EXTENSION = "json";
    private final String PNG_EXTENSION = "png";
    private final String JPG_EXTENSION = "jpg";
    private final String JPEG_EXTENSION = "jpeg";
    private final String GIF_EXTENSION = "gif";
    private final String TXT_EXTENSION = "txt";
    private final String HTML_EXTENSION = "html";
    private final String MD_EXTENSION = "md";
    private final String XML_EXTENSION = "xml";

    @PostConstruct
    public void init(){
        settingsDTO = settingsService.getSettings();
    }

    public byte[] getFileAsByteArray(String filename) throws IOException {
        File file = getFile(filename);
        if (!file.exists()){
            logger.error("Resource {} not found", filename);
            throw new ResourceNotFoundException(filename);
        }
        try{
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            logger.error("Error while converting file {} to byte array", filename);
            throw ex;
        }
    }

    private File getFile(String filename){
        String filepath = Paths.get(settingsDTO.getFilesDir(), filename).toString();
        logger.info("Getting file from {}", filepath);
        return new File(filepath);
    }

    public HttpHeaders getHeaders(String filename){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaTypeFromFileName(filename));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }

    private MediaType getMediaTypeFromFileName(String filename){
        String extension = FilenameUtils.getExtension(filename);
        switch (extension){
            case PDF_EXTENSION: return MediaType.APPLICATION_PDF;
            case JSON_EXTENSION: return MediaType.APPLICATION_JSON;
            case PNG_EXTENSION: return MediaType.IMAGE_PNG;
            case JPG_EXTENSION:
            case JPEG_EXTENSION: return MediaType.IMAGE_JPEG;
            case GIF_EXTENSION: return MediaType.IMAGE_GIF;
            case TXT_EXTENSION: return MediaType.TEXT_PLAIN;
            case HTML_EXTENSION: return MediaType.TEXT_HTML;
            case MD_EXTENSION: return MediaType.TEXT_MARKDOWN;
            case XML_EXTENSION: return MediaType.TEXT_XML;
            default: return MediaType.ALL;
        }
    }
}
