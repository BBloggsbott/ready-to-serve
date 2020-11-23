package com.bbloggsbott.readytoserve.config;

import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import com.bbloggsbott.readytoserve.plugins.exception.PluginRequestMethodNotAllowed;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ReadyToServeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(IOException ex){
        log.error("IO Exception {}", ex.getStackTrace());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PageNotFoundException.class)
    public ResponseEntity<String> handlePageNotFoundException(PageNotFoundException ex){
        log.error("Page Not Found Exception {}", ex.getStackTrace());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ParseException.class, IOException.class, InvocationTargetException.class, NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, ClassNotFoundException.class})
    public ResponseEntity<String> handlePluginExceptions(Exception ex){
        log.info("Exception {}", ex.getStackTrace());
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = PluginRequestMethodNotAllowed.class)
    public ResponseEntity<String> hanglePluginRequestMethodNotAllowed(PluginRequestMethodNotAllowed ex){
        log.info(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

}
