package com.bbloggsbott.readytoserve.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("base_directory")
    private String baseDir;

    @JsonProperty("data_directory")
    private String dataDir;

    @JsonProperty("navigation_file")
    private String navigationFile;

    @JsonProperty("server_info_file")
    private String serverInfoFile;

    @JsonProperty("server_image_url")
    private String serverImageUrl;

    @JsonProperty("server_image_size") // Works only with image from gravatar
    private String serverImageSize;

    @JsonProperty("files_directory")
    private String filesDir;

    @JsonProperty("pages_directory")
    private String pagesDir;

    @JsonProperty("datetimeformat")
    private String dateTimeFormat;

    @JsonIgnore
    private String absolutePagePath;

}
