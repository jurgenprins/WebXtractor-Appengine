package com.bokella.webxtractor.server.domain.web;

import java.net.URL;


public class WebImage extends WebObject {
	private URL url = null;
	private WebLink parentLink = null;
	private int width = 0;
	private int height = 0;
	
	public WebImage(WebPage parentPage, String xpath) {
		super(parentPage, xpath);
	}
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public WebLink getParentLink() {
		return parentLink;
	}

	public void setParentLink(WebLink parentLink) {
		this.parentLink = parentLink;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("img");
		if (url != null) {
			sb.append(" src=\""); 
			sb.append(url.toString());
			sb.append("\"");
		}
		if (width > 0) {
			sb.append(" width=\""); 
			sb.append(width);
			sb.append("\"");
		}
		if (height > 0) {
			sb.append(" height=\""); 
			sb.append(height);
			sb.append("\"");
		}
		if ((parentLink != null) && (parentLink.getUrl() != null)) {
			sb.append(" href=\""); 
			sb.append(parentLink.getUrl().toString());
			sb.append("\"");
		}
		return sb.toString();
	}
}
