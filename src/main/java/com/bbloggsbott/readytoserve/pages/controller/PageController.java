package com.bbloggsbott.readytoserve.pages.controller;

import com.bbloggsbott.readytoserve.pages.dto.PageResponseDTO;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import com.bbloggsbott.readytoserve.pages.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;

@RestController
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/routes")
    public HashSet<String> getRoutes(){
        return pageService.getBasePaths();
    }

    @GetMapping("/**")
    public PageResponseDTO getPage(HttpServletRequest request) throws ParseException, PageNotFoundException, IOException {
        return pageService.getPageResponse(request.getRequestURI());
    }

}
