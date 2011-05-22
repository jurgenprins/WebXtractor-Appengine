package com.bokella.webxtractor.server.services.web.objects;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bokella.webxtractor.server.domain.web.WebLink;
import com.bokella.webxtractor.server.domain.web.WebPage;

public class DefaultWebLinkService implements WebObjectService {
	private static final Logger log = Logger.getLogger(DefaultWebLinkService.class.getName());
	
	private String linkLabelRegEx = ".*/a\\[.+?\\]\\[text\\(\\)=\"([^\"]*)\"\\].*";
	private String linkHrefRegEx = "(.*)/a\\[.*@href=\"([^\"]+)\".*\\].*";
	private Pattern linkLabelPattern = Pattern.compile(linkLabelRegEx);
	private Pattern linkHrefPattern = Pattern.compile(linkHrefRegEx);
	private Matcher m;
	
	public WebLink getFromXPath(WebPage parentPage, String xpathExpression) {
		if (xpathExpression.indexOf("href=") < 0) return null;
		
		m = linkHrefPattern.matcher(xpathExpression);
		if (m.find()) {
			WebLink link = new WebLink(parentPage, xpathExpression);
			try {
				if (parentPage.getUrl() == null) {
					link.setUrl(new URL(m.group(2)));
				} else {
					link.setUrl(new URL(parentPage.getUrl(), m.group(2)));
				}
			} catch (Exception e) {
				log.warning("Link href " + m.group(1) + ": " + e.getMessage());
				try {
					link.setUrl(new URL(m.group(2)));
				} catch (Exception e2) {}
			}
			link.setParentXPath(m.group(1));
			
			m = linkLabelPattern.matcher(xpathExpression);
			if (m.find()) {
				link.setLabel(m.group(1));
			} 

			return link;
		}
		return null;
	}
}
