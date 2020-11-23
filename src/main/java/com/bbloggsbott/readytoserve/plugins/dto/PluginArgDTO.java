package com.bbloggsbott.readytoserve.plugins.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginArgDTO {

    private String name;
    private String type;
    private Boolean requestParam;

}
