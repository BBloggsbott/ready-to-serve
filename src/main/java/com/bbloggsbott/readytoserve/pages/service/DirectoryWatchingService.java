package com.bbloggsbott.readytoserve.pages.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class DirectoryWatchingService {

    @Autowired
    private PagePathService pagePathService;

    private List<Thread> watcherThreads = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PreDestroy
    public void destroy(){
        for (Thread watcherThread: watcherThreads){
            watcherThread.interrupt();
        }
    }

    private void createDirectoryWatcher(String directory) throws IOException {
        logger.info("Creating Directory watcher for {}", directory);
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
            logger.info("Monitoring directory {} for changes", directory);
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                logger.info("Error while monitoring {}", directory);
                e.printStackTrace();
            }
            while (watchKey != null){
                for (WatchEvent event: watchKey.pollEvents()){
                    logger.info("Event: {}, File: {}", event.kind(), event.context());
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                        String createdFile =  Paths.get(directory, event.context().toString()).toString();
                        logger.info("Create Event detected for {}", createdFile);
                        List<File> directoriesToWatch =  pagePathService.createPathsOnFileCreate(createdFile);
                        watchDirectories(directoriesToWatch);
                        logger.info("Create Event Processed");
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
                        String deletedFile = Paths.get(directory, event.context().toString()).toString();
                        logger.info("Delete event detected for {}", deletedFile);
                        pagePathService.deletePathsOnFileDelete(deletedFile);
                        deleteDirectoryWatchingThread(deletedFile);
                        logger.info("Delete event processed");
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
                        String modifiedFile = Paths.get(directory, event.context().toString()).toString();
                        logger.info("Modify event detected for {}", modifiedFile);
                        pagePathService.modifyPathsOnFileModify(modifiedFile);
                        logger.info("Modify Event processed");
                    }
                }
                watchKey.reset();
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    if (!new File(directoryPath.toString()).exists())
                        break;
                    logger.info("Error while monitoring {}", directory);
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
        logger.info("Stopping watcher thread for {}", dirname);
        watcherThreads = watcherThreads.stream().filter(thread -> {
            if (thread.getName().equalsIgnoreCase(dirname)){
                thread.interrupt();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        logger.info("Stopped watcher thread for {}", dirname);
    }

    public void watchDirectories(List<File> directories) {
        for(File file: directories){
            try {
                createDirectoryWatcher(file.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Error while creating watcher for {}", file.getAbsoluteFile());
                e.printStackTrace();
            }
        }
    }

}
