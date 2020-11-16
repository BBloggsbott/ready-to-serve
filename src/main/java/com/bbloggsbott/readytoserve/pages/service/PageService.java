package com.bbloggsbott.readytoserve.pages.service;

import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.pages.dto.PageResponseDTO;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PageService {

    @Autowired
    private PagePathService pagePathService;

    @Autowired
    private DirectoryWatchingService directoryWatchingService;

    @Autowired
    private SettingsService settingsService;

    @PostConstruct
    public void init(){
        String pagesDirectory = settingsService.getSettings().getPagesDir();
        directoryWatchingService.watchDirectories(Arrays.asList(new File(pagesDirectory)));
        List<File> childDirectories = pagePathService.setPathsInDirectoryAndGetChildDirectories(new File(pagesDirectory));
        List<File> finalChildDirectories = childDirectories;
        childDirectories.forEach(dir -> directoryWatchingService.watchDirectories(finalChildDirectories));
        do {
            childDirectories = childDirectories.stream().map(file -> pagePathService.setPathsInDirectoryAndGetChildDirectories(file)).flatMap(List::stream).collect(Collectors.toList());
            directoryWatchingService.watchDirectories(childDirectories);
        } while (childDirectories.size() != 0);
    }

    public PageResponseDTO getPageResponse(String pageUrlPath) throws IOException, ParseException, PageNotFoundException {
        log.info("Got request for {}", pageUrlPath);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        String cleanedUrl = StringUtils.strip(pageUrlPath, "/");
        if (pagePathService.getUrlDirectories().containsKey(cleanedUrl)){
            log.info("Found a url directory entry");
            pageResponseDTO.setPage(false);
            pageResponseDTO.setChildren(new ArrayList<>(pagePathService.getUrlDirectories().get(cleanedUrl)));
        } else if (pagePathService.getPagePaths().containsKey(cleanedUrl)){
            log.info("Found a page entry");
            pageResponseDTO.setPage(true);
            pageResponseDTO.setPageDTO(pagePathService.getPageDTOFromUrl(cleanedUrl));
        } else {
            throw new PageNotFoundException(pageUrlPath);
        }
        log.info("Responding with {}", pageResponseDTO);
        return pageResponseDTO;
    }

    public HashSet<String> getBasePaths(){
        return pagePathService.getBasePaths();
    }

}
