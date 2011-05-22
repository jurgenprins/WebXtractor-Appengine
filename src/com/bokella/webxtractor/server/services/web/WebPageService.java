package com.bokella.webxtractor.server.services.web;

import java.net.URL;

import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.web.exceptions.WebPageServiceException;

import org.w3c.dom.Document;

public interface WebPageService {
	WebPage createFrom(URL sourceUrl) throws WebPageServiceException;
	
	WebPage createFrom(String html) throws WebPageServiceException;
	WebPage createFrom(String html, URL sourceUrl) throws WebPageServiceException;
	
	WebPage createFrom(Document dom) throws WebPageServiceException;	
	WebPage createFrom(Document dom, URL sourceUrl) throws WebPageServiceException;
}
