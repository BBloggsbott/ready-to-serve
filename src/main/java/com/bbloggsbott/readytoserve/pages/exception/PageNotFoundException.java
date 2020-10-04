package com.bbloggsbott.readytoserve.pages.exception;

public class PageNotFoundException extends Exception {

    private String urlPath;

    public PageNotFoundException(String urlPath){
        super(String.format("Page for %s not found", urlPath));
        this.urlPath = urlPath;
    }
}
