package com.bbloggsbott.readytoserve.application.util;

import com.bbloggsbott.readytoserve.application.dto.ServerInfoDTO;
import org.springframework.stereotype.Component;

public class ServerUtil {

    private static final String GITHUB_BASEURL = "https://github.com/";
    private static final String GITLAB_BASEURL = "https://gitlab.com/";
    private static final String LINKEDIN_BASEURL = "https://linkedin.com/in/";
    private static final String TWITTER_BASEURL = "https://twitter.com/";

    private static String getGitHubURL(String github){
        return GITHUB_BASEURL + github.strip();
    }

    private static String getGitLabURL(String gitlab){
        return GITLAB_BASEURL + gitlab.strip();
    }

    private static String getLinkedinURL(String linkedin){
        return LINKEDIN_BASEURL + linkedin.strip();
    }

    private static String getTwitterURL(String twitter){
        return TWITTER_BASEURL + twitter.strip();
    }

    public static void prepareOnlineURLs(ServerInfoDTO serverInfo){
        serverInfo.setGithub(getGitHubURL(getGitHubURL(serverInfo.getGithub())));
        serverInfo.setGitlab(getGitLabURL(getGitLabURL(serverInfo.getGitlab())));
        serverInfo.setLinkedin(getLinkedinURL(serverInfo.getLinkedin()));
        serverInfo.setTwitter(getTwitterURL(serverInfo.getTwitter()));
    }

}
