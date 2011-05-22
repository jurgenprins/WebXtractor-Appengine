package com.bokella.webxtractor.server.domain.web;

import java.net.URL;


public class WebLink extends WebObject {
	private URL url = null;
	private String label = null;
		
	public WebLink(WebPage parentPage, String xpath) {
		super(parentPage, xpath);
	}
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder("link");
		if (url != null) {
			sb.append(" href=\""); 
			sb.append(url.toString());
			sb.append("\"");
		}
		if (label != null) {
			sb.append(" label=\""); 
			sb.append(label);
			sb.append("\"");
		}
		return sb.toString();
	}
}
