package com.bokella.webxtractor.server.services.web;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.TestCase;

import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebImageService;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebLinkService;
import com.bokella.webxtractor.server.util.io.DefaultUrlContentReader;

public class WebPageServiceTest extends TestCase {
	private WebPageService webPageService;
	
	protected void setUp() throws Exception {
		webPageService = new DefaultWebPageService(
							new DefaultUrlContentReader(),
							new DefaultWebLinkService(),
							new DefaultWebImageService(
								new DefaultWebLinkService()));
		super.setUp();
	}

	public void testMe() throws Exception {
		String data	= "<data><username>admin</username></data>";
	       
        DocumentBuilder docBuilder 			= DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document 		doc 				= docBuilder.parse(new ByteArrayInputStream(data.getBytes()));
        Node			node	 = XPathAPI.selectSingleNode(doc, "//username/text()");
        
        System.out.println("username = " + node.getNodeValue());
	}
	       
	public void testFlickrImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/flickr_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(144, webPage.getImages().size()); 
	}
	
	public void testFlickrLinks() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/flickr_photo.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(359, webPage.getLinks().size());  
	}
	
	public void testFlickrOriginalImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/flickr_original.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(7, webPage.getImages().size()); 
	}
	
	public void testWebshotsImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/webshots_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(79, webPage.getImages().size()); 
	}
	
	public void testWebshotsLinks() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/webshots_photo.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(455, webPage.getLinks().size());  
	}
	
	public void testWebshotsOriginalImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/webshots_original.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(1, webPage.getImages().size()); 
	}
	
	public void testFotkiImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/fotki_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(16, webPage.getImages().size()); 
	}
	
	public void testFotkiLinks() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/fotki_photo.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(93, webPage.getLinks().size());  
	}
	
	public void testFotkiOriginalImages() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/fotki_original.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(13, webPage.getImages().size()); 
	}
	
	public void testFreeonesLinks() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/freeones_portal.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		assertEquals(634, webPage.getLinks().size());  
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
