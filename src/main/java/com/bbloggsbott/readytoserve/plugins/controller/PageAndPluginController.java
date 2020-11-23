package com.bbloggsbott.readytoserve.plugins.controller;

import com.bbloggsbott.readytoserve.pages.dto.PageResponseDTO;
import com.bbloggsbott.readytoserve.pages.exception.PageNotFoundException;
import com.bbloggsbott.readytoserve.pages.service.PageService;
import com.bbloggsbott.readytoserve.plugins.exception.PluginRequestMethodNotAllowed;
import com.bbloggsbott.readytoserve.plugins.service.PluginService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class PageAndPluginController {

    @Autowired
    private PageService pageService;

    @Autowired
    private PluginService pluginService;


    @RequestMapping("/**")
    public ResponseEntity getPage(HttpServletRequest request, @RequestParam Map<String, Object> requestParams, @RequestBody Map<String, Object> requestBody) throws ParseException, PageNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginRequestMethodNotAllowed {
        log.info("Got {} request to {}, param: {}, body {}", request.getMethod(), request.getRequestURL(), requestParams, requestBody);
        Object response = pluginService.getResponse(request.getRequestURI(), requestParams, requestBody, request.getMethod());
        if (response != null){
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        return new ResponseEntity<PageResponseDTO>(pageService.getPageResponse(request.getRequestURI()), HttpStatus.OK);
    }

}
