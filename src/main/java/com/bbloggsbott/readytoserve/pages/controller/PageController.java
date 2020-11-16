package com.bbloggsbott.readytoserve.pages.controller;

import com.bbloggsbott.readytoserve.pages.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/routes")
    public HashSet<String> getRoutes(){
        return pageService.getBasePaths();
    }

}
