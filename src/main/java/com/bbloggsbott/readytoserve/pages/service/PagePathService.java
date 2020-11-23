package com.bbloggsbott.readytoserve.pages.service;

import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.pages.dto.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PagePathService {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private MarkdownPageLoadService markdownPageLoadService;

    private final HashMap<String, String> pagePaths = new HashMap<>();

    private final HashMap<String, HashSet<String>> directoryPaths = new HashMap<String, HashSet<String>>();

    private final HashSet<String> basePaths = new HashSet<>();

    public HashMap<String, HashSet<String>> getUrlDirectories(){
        return directoryPaths;
    }

    public HashMap<String, String> getPagePaths(){
        return pagePaths;
    }

    public HashSet<String> getBasePaths(){
        return basePaths;
    }

    public List<File> setPathsInDirectoryAndGetChildDirectories(File directory) {
        log.info("Loading pages in {}", directory);
        File[] files = directory.listFiles();
        ArrayList<File> directories = new ArrayList<>();
        for (File file: files){
            if (file.isFile() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("md")){
                PageDTO pageDTO = new PageDTO(file.getAbsolutePath());
                try {
                    pageDTO.setContentMD(Files.readString(Paths.get(pageDTO.getPageFilePath())));
                    markdownPageLoadService.setPageMeta(pageDTO);
                } catch (ParseException e) {
                    log.error("Error while parsing meta for {}. Skipping", pageDTO.getPageFilePath());
                    e.printStackTrace();
                    continue;
                } catch (IOException e){
                    log.error("Error while reading file {}. Skipping", pageDTO.getPageFilePath());
                    e.printStackTrace();
                    continue;
                }
                pagePaths.put(StringUtils.strip(pageDTO.getUrlPath().strip(), "/"), pageDTO.getPageFilePath());
                makePageDirectoryEntry(pageDTO);
            } else if (file.isDirectory()){
                directories.add(file);
            }
        }
        return directories;
    }

    private void makePageDirectoryEntry(PageDTO pageDTO){
        log.info("Creating Entries for child paths");
        String[] paths = StringUtils.strip(pageDTO.getUrlPath(), "/").strip().split("/");
        ArrayList<String> relativePaths = new ArrayList<>();

        basePaths.add(paths[0]);
        relativePaths.add(paths[0]);
        for (int i = 1; i < paths.length; i++){
            String prevRelPath = relativePaths.get(i-1);
            String path = prevRelPath + "/" + paths[i];
            relativePaths.add(path);
            if (!directoryPaths.containsKey(prevRelPath)){
                directoryPaths.put(prevRelPath, new HashSet<>());
            }
            directoryPaths.get(prevRelPath).add(path);
        }
    }

    public PageDTO getPageDTOFromUrl(String urlPath) throws IOException, ParseException {
        Path pageFilePath = Paths.get(pagePaths.get(urlPath));
        PageDTO pageDTO = new PageDTO(pageFilePath.toAbsolutePath().toString());
        pageDTO.setContentMD(Files.readString(pageFilePath));
        markdownPageLoadService.setPageMeta(pageDTO);
        return pageDTO;
    }

    public List<File> createPathsOnFileCreate(String filename){
        File file = new File(filename);
        Set<File> directories = new HashSet<>();
        List<File> childDirectories = new ArrayList<>();
        if (file.isDirectory()){
            childDirectories = setPathsInDirectoryAndGetChildDirectories(file);
            directories.add(file);
            directories.addAll(childDirectories);
            while (childDirectories.size() != 0){
                childDirectories = childDirectories.stream().map(this::setPathsInDirectoryAndGetChildDirectories).flatMap(List::stream).collect(Collectors.toList());
                directories.addAll(childDirectories);
            }
        } else if (file.isFile() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("md")){
            PageDTO pageDTO = new PageDTO(file.getAbsolutePath());
            try {
                pageDTO.setContentMD(Files.readString(Paths.get(pageDTO.getPageFilePath())));
                markdownPageLoadService.setPageMeta(pageDTO);
            } catch (ParseException e) {
                log.error("Error while parsing meta for {}. Skipping", pageDTO.getPageFilePath());
                e.printStackTrace();
            } catch (IOException e){
                log.error("Error while reading file {}. Skipping", pageDTO.getPageFilePath());
                e.printStackTrace();
            }
            pagePaths.put(StringUtils.strip(pageDTO.getUrlPath(), "/"), pageDTO.getPageFilePath());
            makePageDirectoryEntry(pageDTO);
        } else {
            log.info("New file was neither a markdown file nor a directory");
        }
        return new ArrayList<>(directories);
    }

    private void deletePathBasedOnFileName(String filename){
        ArrayList<String> keysToDelete = new ArrayList<>();
        pagePaths.forEach((key, value) -> {
            if (value.equalsIgnoreCase(filename)){
                keysToDelete.add(key);
            }
        });
        keysToDelete.forEach(key -> {
            pagePaths.remove(key);
            deleteDirectoryPaths(key);
        });
    }

    private void deleteDirectoryPaths(String urlPath){
        if (urlPath.isEmpty() || urlPath.equalsIgnoreCase("/"))
            return;
        String[] pathParts = StringUtils.strip(urlPath, "/").split("/");
        String lastPath = pathParts[pathParts.length-1];
        String prevPath = StringUtils.strip(urlPath, "/").substring(0, urlPath.length()-lastPath.length());
        prevPath = StringUtils.strip(prevPath, "/");
        if (prevPath != null && !prevPath.isEmpty()){
            HashSet<String> filteredDirectoryPaths = new HashSet<String>(directoryPaths.get(prevPath).stream().filter(path -> !path.equalsIgnoreCase(urlPath)).collect(Collectors.toSet()));
            directoryPaths.put(prevPath, filteredDirectoryPaths);
        }
        if (directoryPaths.get(prevPath) == null || directoryPaths.get(prevPath).size() == 0){
            directoryPaths.remove(lastPath);
            deleteDirectoryPaths(prevPath);
            if (basePaths.contains(prevPath)){
                basePaths.remove(prevPath);
            }
        }
    }

    private boolean deletePathsForPagesInDirectory(String dirPath){
        log.info("Deleting all pages in directory {}", dirPath);
        boolean deleted = false;
        List<String> pathsToDelete = new ArrayList<>();
        pagePaths.forEach((key, value) -> {
            if (value.contains(dirPath+"/"))
                pathsToDelete.add(key);
        });
        if (pathsToDelete.size()!=0)
            deleted = true;
        pathsToDelete.forEach(path -> {
            pagePaths.remove(path);
            deleteDirectoryPaths(path);
        });
        return deleted;
    }

    public void deletePathsOnFileDelete(String filename){
        File file = new File(filename);
        if (FilenameUtils.getExtension(file.getName()).isEmpty()){
            deletePathsForPagesInDirectory(file.getAbsolutePath());
        }
        deletePathBasedOnFileName(file.getAbsolutePath());
    }

    public void modifyPathsOnFileModify(String filename){
        File file = new File(filename);
        if (file.isFile() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("md")) {
            PageDTO pageDTO = new PageDTO(file.getAbsolutePath());
            try {
                pageDTO.setContentMD(Files.readString(Paths.get(file.getAbsolutePath())));
                markdownPageLoadService.setPageMeta(pageDTO);
            } catch (IOException e) {
                log.info("Error while reading {} to update modified file", file.getAbsolutePath());
                e.printStackTrace();
                return;
            } catch (ParseException e) {
                log.info("Error while parsing {} to load metadata for modified file", file.getAbsolutePath());
                e.printStackTrace();
                return;
            }
            deletePathBasedOnFileName(file.getAbsolutePath());
            pagePaths.put(StringUtils.strip(pageDTO.getUrlPath(), "/"), file.getAbsolutePath());
            makePageDirectoryEntry(pageDTO);
        }
    }

}
