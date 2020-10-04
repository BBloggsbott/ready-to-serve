package com.bbloggsbott.readytoserve.pages.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    @JsonIgnore
    private String pageFilePath;

    @JsonProperty("title")
    private String title;

    @JsonIgnore
    @JsonProperty("url_path")
    private String urlPath;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("content_md")
    private String contentMD;

    @JsonProperty("content_html")
    private String contentHtml;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("excerpt")
    private String excerpt;

    public PageDTO(String pageFilePath){
        this.pageFilePath = pageFilePath;
    }

}
