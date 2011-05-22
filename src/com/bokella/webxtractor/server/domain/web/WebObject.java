package com.bokella.webxtractor.server.domain.web;


public class WebObject {
	private WebPage parentPage = null;
	private String parentXPath = null;
	private String xpath = null;

	public WebObject(WebPage parentPage, String xpath) {
		this.parentPage = parentPage;
		this.xpath = xpath;
	}
	
	public WebPage getParentPage() {
		return parentPage;
	}
	
	public String getXpath() {
		return xpath;
	}
	
	public String getParentXPath() {
		return parentXPath;
	}

	public void setParentXPath(String parentXPath) {
		this.parentXPath = parentXPath;
	}
	
	public String getKey() {
		return parentPage.getUrl().toString().concat(">").concat(xpath);
	}
}
