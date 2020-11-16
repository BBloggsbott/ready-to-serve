package com.bbloggsbott.readytoserve.pages.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DirectoryWatchingService {

    @Autowired
    private PagePathService pagePathService;

    private List<Thread> watcherThreads = new ArrayList<>();

    @PreDestroy
    public void destroy(){
        for (Thread watcherThread: watcherThreads){
            watcherThread.interrupt();
        }
    }

    private void createDirectoryWatcher(String directory) throws IOException {
        log.info("Creating Directory watcher for {}", directory);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path directoryPath = Paths.get(directory);
        directoryPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );
        Runnable watcher = () -> {
            WatchKey watchKey = null;
            log.info("Monitoring directory {} for changes", directory);
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                log.info("Error while monitoring {}", directory);
                e.printStackTrace();
            }
            while (watchKey != null){
                for (WatchEvent event: watchKey.pollEvents()){
                    log.info("Event: {}, File: {}", event.kind(), event.context());
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                        String createdFile =  Paths.get(directory, event.context().toString()).toString();
                        log.info("Create Event detected for {}", createdFile);
                        List<File> directoriesToWatch =  pagePathService.createPathsOnFileCreate(createdFile);
                        watchDirectories(directoriesToWatch);
                        log.info("Create Event Processed");
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
                        String deletedFile = Paths.get(directory, event.context().toString()).toString();
                        log.info("Delete event detected for {}", deletedFile);
                        pagePathService.deletePathsOnFileDelete(deletedFile);
                        deleteDirectoryWatchingThread(deletedFile);
                        log.info("Delete event processed");
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
                        String modifiedFile = Paths.get(directory, event.context().toString()).toString();
                        log.info("Modify event detected for {}", modifiedFile);
                        pagePathService.modifyPathsOnFileModify(modifiedFile);
                        log.info("Modify Event processed");
                    }
                }
                watchKey.reset();
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    if (!new File(directoryPath.toString()).exists())
                        break;
                    log.info("Error while monitoring {}", directory);
                    e.printStackTrace();
                }
            }
        };
        Thread watcherThread = new Thread(watcher, directory);
        watcherThreads.add(watcherThread);
        watcherThread.start();
    }

    private void deleteDirectoryWatchingThread(String dirname){
        if (!FilenameUtils.getExtension(dirname).isEmpty()){
            return;
        }
        log.info("Stopping watcher thread for {}", dirname);
        watcherThreads = watcherThreads.stream().filter(thread -> {
            if (thread.getName().equalsIgnoreCase(dirname)){
                thread.interrupt();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        log.info("Stopped watcher thread for {}", dirname);
    }

    public void watchDirectories(List<File> directories) {
        for(File file: directories){
            try {
                createDirectoryWatcher(file.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error while creating watcher for {}", file.getAbsoluteFile());
                e.printStackTrace();
            }
        }
    }

}
