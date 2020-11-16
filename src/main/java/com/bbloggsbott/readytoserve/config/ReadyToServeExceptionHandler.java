package com.bbloggsbott.readytoserve.config;

import com.bbloggsbott.readytoserve.media.exception.ResourceNotFoundException;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.text.ParseException;

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

    @ExceptionHandler(value = ParseException.class)
    public ResponseEntity<String> handleParseException(IOException ex){
        return new ResponseEntity<>("Error while parsing Page", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = PageNotFoundException.class)
    public ResponseEntity<String> handlePageNotFoundException(PageNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
