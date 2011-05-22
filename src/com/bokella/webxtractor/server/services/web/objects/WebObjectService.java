package com.bokella.webxtractor.server.services.web.objects;

import com.bokella.webxtractor.server.domain.web.WebObject;
import com.bokella.webxtractor.server.domain.web.WebPage;

public interface WebObjectService {
	public WebObject getFromXPath(WebPage parentPage, String xpathExpression);
}
