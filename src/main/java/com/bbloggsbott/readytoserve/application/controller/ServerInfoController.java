package com.bbloggsbott.readytoserve.application.controller;

import com.bbloggsbott.readytoserve.application.dto.ServerInfoDTO;
import com.bbloggsbott.readytoserve.application.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerInfoController {

    @Autowired
    private ServerInfoService serverInfoService;

    @GetMapping("/")
    public ServerInfoDTO getServerInfo(){
        return serverInfoService.getServerInfo();
    }

}
