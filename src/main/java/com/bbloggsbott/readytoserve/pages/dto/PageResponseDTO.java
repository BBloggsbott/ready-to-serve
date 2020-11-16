package com.bbloggsbott.readytoserve.pages.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponseDTO {

    @JsonProperty("is_page")
    private boolean isPage;

    @JsonProperty("children")
    private List<String> children;

    @JsonProperty("page")
    private PageDTO pageDTO;

}
