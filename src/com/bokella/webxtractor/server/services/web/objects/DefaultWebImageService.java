package com.bokella.webxtractor.server.services.web.objects;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bokella.webxtractor.server.domain.web.WebImage;
import com.bokella.webxtractor.server.domain.web.WebLink;
import com.bokella.webxtractor.server.domain.web.WebPage;

public class DefaultWebImageService implements WebObjectService {
	private static final Logger log = Logger.getLogger(DefaultWebImageService.class.getName());
	
	private WebObjectService webLinkService;
	
	private Pattern imgSrcPattern = Pattern.compile("(.*)/img\\[.*@src=\"([^\"]+)\".*");
	private Pattern imgWidthPattern = Pattern.compile(".*/img\\[.*@width=\"([^\"]+)\".*");
	private Pattern imgHeightPattern = Pattern.compile(".*/img\\[.*@height=\"([^\"]+)\".*");
	//private Pattern imgHrefPattern = Pattern.compile("(.*)/a\\[.*@href=\"([^\"]+)\".*\\]/img\\[.*@src=\"([^\"]+)\".*");
	private Matcher m;
	
	public DefaultWebImageService(WebObjectService webLinkService) {
		this.webLinkService = webLinkService;
	}
	
	public WebImage getFromXPath(WebPage parentPage, String xpathExpression) {
		if (xpathExpression.indexOf("src=") < 0) return null;
		
		WebLink parentLink = null;
		
		m = imgSrcPattern.matcher(xpathExpression);
		if (m.find()) {
			WebImage img = new WebImage(parentPage, xpathExpression);
			try {
				if (parentPage.getUrl() == null) {
					img.setUrl(new URL(m.group(2)));
				} else {
					img.setUrl(new URL(parentPage.getUrl(), m.group(2)));
				}
			} catch (Exception e) {
				log.warning("Image url " + m.group(2) + ": " + e.getMessage());
				try {
					img.setUrl(new URL(m.group(2)));
				} catch (Exception e2) {}
			}
			img.setParentXPath(m.group(1));
			
			if ((parentLink = (WebLink)webLinkService.getFromXPath(parentPage, m.group(1))) != null) {
				img.setParentLink(parentLink);
				img.setParentXPath(parentLink.getParentXPath());
			}
			
			m = imgWidthPattern.matcher(xpathExpression);
			if (m.find()) {
				try {
					img.setWidth(new Integer(m.group(1).replaceAll("[Pp][Xx]", "")).intValue());
				} catch (Exception e) { }
			} 

			m = imgHeightPattern.matcher(xpathExpression);
			if (m.find()) {
				try {
					img.setHeight(new Integer(m.group(1).replaceAll("[Pp][Xx]", "")).intValue());
				} catch (Exception e) { }
			} 
			return img;
		}
		return null;
	}
}
