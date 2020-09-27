package com.bbloggsbott.readytoserve.config;

import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ReadyToServeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<String> handleResourceNotFoundException(IOException ex){
        if (ex instanceof ResourceNotFoundException){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Error while serving resource", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
