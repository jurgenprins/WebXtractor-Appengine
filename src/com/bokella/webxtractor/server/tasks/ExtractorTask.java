package com.bokella.webxtractor.server.tasks;

import java.net.URL;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.server.services.ExtractorService;
import com.bokella.webxtractor.server.services.exceptions.ExtractorServiceException;

public class ExtractorTask extends XtrTask {
	private static final Logger log = Logger.getLogger(ExtractorTask.class.getName());
	
	private ExtractorService extractorService = null;
	private String input = null;
	private URL pageUrl = null;
	
	public ExtractorTask(
			ExtractorService extractorService,
			String input,
			URL pageUrl) {
		super("extract " + pageUrl.toString());
			
		this.extractorService = extractorService;
		this.input = input;
		this.pageUrl = pageUrl;
		
		this.setPayloadAttribute("input", input);
		this.setPayloadAttribute("pageUrl", pageUrl);
	}
	
	public ExtractorTask(
			ExtractorService extractorService,
			byte[] payload) {
		super("?", payload);
		
		this.extractorService = extractorService;
		
		this.input = (String)this.getPayloadAttribute("input");
		this.pageUrl = (URL)this.getPayloadAttribute("pageUrl");
		
		this.setName("extract " + this.pageUrl.toString());
	}
	
	public void execute() {
		log.info("Executing task " + this.getName());
		
		XtrGallery xtrPage = null;
		try {
			xtrPage = this.extractorService.createFrom(input, this.pageUrl);
		} catch (ExtractorServiceException e) {
			log.severe("Could not compile page: " + e.getMessage()); 
		}
	}
	
}
