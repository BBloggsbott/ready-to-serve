package com.bbloggsbott.readytoserve.media.exception;

import java.io.FileNotFoundException;

public class ResourceNotFoundException extends FileNotFoundException {

    public ResourceNotFoundException(String resourceName){
        super(String.format("Resource %s not found", resourceName));
    }
}
