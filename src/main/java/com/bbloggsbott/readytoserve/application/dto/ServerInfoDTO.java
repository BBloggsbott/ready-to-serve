package com.bbloggsbott.readytoserve.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServerInfoDTO {

    private String name;
    private String description;
    private String email;
    private String avatar;

    // Usernames or Repo names
    private String github;
    private String gitlab;
    private String linkedin;
    private String twitter;

    @JsonProperty("base_routes")
    private Set<String> routes;

}
