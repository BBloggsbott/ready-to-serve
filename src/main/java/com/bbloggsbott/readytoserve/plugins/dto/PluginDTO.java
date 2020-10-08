package com.bbloggsbott.readytoserve.plugins.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;

@Getter
@Setter
public class PluginDTO {

    private String name;

    private String jarfile;

    private String method;

    private String endpoint;

    private String requestType;

    private ArrayList<PluginArgDTO> args;

    private String className;
    private String methodName;

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.writeValueAsString(this);
    }
}
