package com.bokella.webxtractor.server.services;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.server.services.exceptions.ExtractorServiceException;

import java.net.URL;

public interface ExtractorService {
	public XtrGallery createFrom(String input, URL url) throws ExtractorServiceException;
}
