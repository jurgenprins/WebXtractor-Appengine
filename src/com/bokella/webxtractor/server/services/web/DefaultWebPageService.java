package com.bokella.webxtractor.server.services.web;

import com.bokella.webxtractor.server.domain.web.WebImage;
import com.bokella.webxtractor.server.domain.web.WebLink;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.web.exceptions.WebPageServiceException;
import com.bokella.webxtractor.server.services.web.objects.WebObjectService;
import com.bokella.webxtractor.server.util.XPathGenerator;
import com.bokella.webxtractor.server.util.io.UrlContentReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

public class DefaultWebPageService implements WebPageService  {
	private static final Logger log = Logger.getLogger(DefaultWebPageService.class.getName());
	
	private static final String voidUrl = "file://dev/null";
	
	private UrlContentReader urlContentReader = null;
	private WebObjectService webLinkService;
	private WebObjectService webImageService;
	
	public DefaultWebPageService(
			UrlContentReader urlContentReader,
			WebObjectService webLinkService,
			WebObjectService webImageService) {
		this.urlContentReader = urlContentReader;
		this.webLinkService = webLinkService;
		this.webImageService = webImageService;
	}
	
	public WebPage createFrom(URL url) throws WebPageServiceException {
		String html = null;
		try {
			html = this.urlContentReader.readString(url);
		} catch (Exception e) {
			log.severe("Could not fetch " + url.toString() + ": " + e.getMessage());
			throw new WebPageServiceException("Could not fetch " + url.toString() + ": " + e.getMessage());
		}
		log.info(html.length() + " bytes read");
		
		return createFrom(html, url);
	}
	
	public WebPage createFrom(String html) throws WebPageServiceException {
		try {
			return createFrom(html, new URL(voidUrl));
		} catch (MalformedURLException e) {
			throw new WebPageServiceException(e.getMessage());
		} catch (WebPageServiceException e) {
			throw e;
		}
	}
	
	public WebPage createFrom(String html, URL url) throws WebPageServiceException {
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node;
		
		try {
			 node = cleaner.clean(html);
		} catch (Exception e) {
			log.severe("Cannot clean HTML: " + e.getMessage());
			throw new WebPageServiceException("Cannot clean HTML: " + e.getMessage());
		}
		
		DomSerializer domSerializer = new DomSerializer(cleaner.getProperties());
		Document dom;
		try {
			 dom = domSerializer.createDOM(node);
		} catch (Exception e) {
			log.severe("Cannot parse HTML: " + e.getMessage());
			throw new WebPageServiceException("Cannot parse HTML: " + e.getMessage());
		}
		
		return createFrom(dom, url);
	}
	
	public WebPage createFrom(Document dom) throws WebPageServiceException {
		try {
			return createFrom(dom, new URL(voidUrl));
		} catch (MalformedURLException e) {
			throw new WebPageServiceException(e.getMessage());
		} catch (WebPageServiceException e) {
			throw e;
		}
	}
	
	public WebPage createFrom(Document dom, URL url) throws WebPageServiceException {
		List<String> xpathExprs = new ArrayList<String>();
		try {
			String startXPathExpr = "";
			XPathGenerator.processNodeList(dom.getChildNodes(), startXPathExpr, xpathExprs);
		} catch (Exception e) {
			log.severe("Cannot parse DOM: " + e.getMessage());
			throw new WebPageServiceException("Cannot parse DOM: " + e.getMessage());
		}
		
		WebPage webPage = new WebPage(url);
		WebLink webLink = null;
		WebImage webImage = null;
		
		log.info("Abstracting webobjects from " + xpathExprs.size() + " expressions");
		for(String s : xpathExprs) {
			if ((webLink = (WebLink)webLinkService.getFromXPath(webPage, s)) != null) {
				webPage.getLinks().add(webLink);
			}
			if ((webImage = (WebImage)webImageService.getFromXPath(webPage, s)) != null) {
				webPage.getImages().add(webImage);
			}
		}
		
		return webPage;
	}
}
