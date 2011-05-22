package com.bokella.webxtractor.server.domain.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WebPage {
	private URL url = null;
	
	private List<WebImage> images = new ArrayList<WebImage>();
	private List<WebLink> links = new ArrayList<WebLink>();
	
	public WebPage (URL url) {
		this.url = url;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public List<WebImage> getImages() {
		return images;
	}
	
	public void setImages(List<WebImage> images) {
		this.images = images;
	}
	
	public List<WebLink> getLinks() {
		return links;
	}
	
	public void setLinks(List<WebLink> links) {
		this.links = links;
	}
}
