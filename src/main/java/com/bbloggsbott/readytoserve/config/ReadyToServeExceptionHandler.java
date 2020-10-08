package com.bbloggsbott.readytoserve.config;

import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

@ControllerAdvice
public class ReadyToServeExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(IOException ex){
        logger.error("IO Exception {}", ex.getStackTrace());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PageNotFoundException.class)
    public ResponseEntity<String> handlePageNotFoundException(PageNotFoundException ex){
        logger.error("Page Not Found Exception {}", ex.getStackTrace());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ParseException.class, IOException.class, InvocationTargetException.class, NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, ClassNotFoundException.class})
    public ResponseEntity<String> handlePluginExceptions(Exception ex){
        logger.info("Exception {}", ex.getStackTrace());
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
