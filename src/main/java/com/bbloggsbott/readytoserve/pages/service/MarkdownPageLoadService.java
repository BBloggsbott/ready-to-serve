package com.bbloggsbott.readytoserve.pages.service;

import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.pages.dto.PageDTO;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MarkdownPageLoadService {

    @Autowired
    SettingsService settingsService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Parser mdParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    private final String PAGEMETA = "pagemeta";
    private final String PAGETITLE = "title";
    private final String PAGETAGS = "tags";
    private final String PAGEURL_PATH = "url_path";
    private final String PAGEEXCERPT = "excerpt";
    private final String PAGEDATE = "date";

    private final int EXCERPT_LENGTH = 128;

    private final List<String> NECESSARY_PAGE_KEYS = Arrays.asList(PAGETITLE, PAGETAGS, PAGEURL_PATH, PAGEEXCERPT, PAGEDATE);

    private final String PAGE_META_REGEX = "---\\n(.*\\n)+---";

    public void setPageMeta(PageDTO pageDTO) throws ParseException {
        logger.info("Setting page meta for {}", pageDTO.getPageFilePath());
        Node node = mdParser.parse(pageDTO.getContentMD());
        if (node.getFirstChild() == null || node.getFirstChild().getNext() == null){
            logger.info("Setting default meta");
            setAllMetaToDefault(pageDTO);
            pageDTO.setContentHtml(htmlRenderer.render(node));
            return;
        }
        Node metaNode = node.getFirstChild().getNext();
        logger.info("Extracting Page meta from markdown");
        List<String> metaNodeContent = Arrays.asList(htmlRenderer.render(metaNode).replaceAll("</?h2>", "").split("\n"));
        HashMap<String, String> metaMap = new HashMap<>();
        if (metaNodeContent.get(0).equalsIgnoreCase(PAGEMETA)){
            logger.info("Meta exists. Setting meta");
            for (int i=1; i < metaNodeContent.size();i++){
                String content = metaNodeContent.get(i);
                String[] parts = content.split(":");
                if (parts.length >= 2){
                    metaMap.put(parts[0].strip(), content.substring(parts[0].length()+1).strip());
                } else if (parts.length == 1){
                    metaMap.put(parts[0].strip(), null);
                }
            }
            for (String key: NECESSARY_PAGE_KEYS){
                if (metaMap.containsKey(key) && metaMap.get(key) != null){
                    setPageMetaForKey(pageDTO, key, metaMap.get(key));
                } else {
                    logger.info("Necessary key {} is null or not found. Using default", key);
                    setDefaultPageMeta(pageDTO, key);
                }
            }
            logger.info("Removing meta from markdown");
            pageDTO.setContentMD(removePageMeta(pageDTO.getContentMD()));
            pageDTO.setExcerpt(removePageMeta(pageDTO.getExcerpt()));
        } else {
            logger.info("Meta not found. Using defaults");
            setAllMetaToDefault(pageDTO);
        }
        logger.info("Creating HTML for content");
        pageDTO.setContentHtml(htmlRenderer.render(mdParser.parse(pageDTO.getContentMD())));
        logger.info("Setting page meta for {} complete", pageDTO.getPageFilePath());
    }

    private String removePageMeta(String contentMD){
        return contentMD.replaceAll(PAGE_META_REGEX, "");
    }

    private void setPageMetaForKey(PageDTO pageDTO, String key, String value) throws ParseException {
        String[] pathParts;
        switch (key){
            case PAGETITLE:
                pageDTO.setTitle(value.strip());
                break;
            case PAGETAGS: pageDTO.setTags(Arrays.asList(value.strip().split(","))); break;
            case PAGEURL_PATH:
                pageDTO.setUrlPath(value.strip());
                break;
            case  PAGEEXCERPT:
                pageDTO.setExcerpt(value.strip());
                break;
            case PAGEDATE:
                SimpleDateFormat dateFormat = new SimpleDateFormat(settingsService.getSettings().getDateTimeFormat());
                try{
                    pageDTO.setDate(dateFormat.parse(value));
                } catch (ParseException e){
                    logger.error("Error while parsing date {} in {}", value, pageDTO.getPageFilePath());
                    throw e;
                }
        }
    }

    private void setDefaultPageMeta(PageDTO pageDTO, String key){
        String[] pathParts;
        switch (key){
            case PAGETITLE:
                pathParts = pageDTO.getPageFilePath().split("/");
                pageDTO.setTitle(pathParts[pathParts.length - 1]);
                break;
            case PAGETAGS: pageDTO.setTags(new ArrayList<>()); break;
            case PAGEURL_PATH:
                pathParts = pageDTO.getPageFilePath().split(settingsService.getSettings().getPagesDir());
                pageDTO.setUrlPath(pathParts[pathParts.length - 1]);
                break;
            case  PAGEEXCERPT:
                String excerpt;
                if (pageDTO.getContentMD().length() < EXCERPT_LENGTH){
                    excerpt = pageDTO.getContentMD();
                } else {
                    excerpt = pageDTO.getContentMD().substring(0, EXCERPT_LENGTH);
                }
                pageDTO.setExcerpt(excerpt);
                break;
            case PAGEDATE: pageDTO.setDate(new Date());
        }
    }

    private void setAllMetaToDefault(PageDTO pageDTO){
        for (String key: NECESSARY_PAGE_KEYS){
            setDefaultPageMeta(pageDTO, key);
        }
    }



}
