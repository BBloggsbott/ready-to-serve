package com.bbloggsbott.readytoserve.media.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MediaFileUtil {

	private final String PDF_EXTENSION = "pdf";
	private final String JSON_EXTENSION = "json";
	private final String PNG_EXTENSION = "png";
	private final String JPG_EXTENSION = "jpg";
	private final String JPEG_EXTENSION = "jpeg";
	private final String GIF_EXTENSION = "gif";
	private final String TXT_EXTENSION = "txt";
	private final String HTML_EXTENSION = "html";
	private final String MD_EXTENSION = "md";
	private final String XML_EXTENSION = "xml";

	public MediaType getMediaTypeFromFileName(String filename){
		String extension = FilenameUtils.getExtension(filename);
		switch (extension){
			case PDF_EXTENSION: return MediaType.APPLICATION_PDF;
			case JSON_EXTENSION: return MediaType.APPLICATION_JSON;
			case PNG_EXTENSION: return MediaType.IMAGE_PNG;
			case JPG_EXTENSION:
			case JPEG_EXTENSION: return MediaType.IMAGE_JPEG;
			case GIF_EXTENSION: return MediaType.IMAGE_GIF;
			case TXT_EXTENSION: return MediaType.TEXT_PLAIN;
			case HTML_EXTENSION: return MediaType.TEXT_HTML;
			case MD_EXTENSION: return MediaType.TEXT_MARKDOWN;
			case XML_EXTENSION: return MediaType.TEXT_XML;
			default: return MediaType.ALL;
		}
	}

}
