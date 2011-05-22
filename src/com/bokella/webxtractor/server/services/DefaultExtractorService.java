package com.bokella.webxtractor.server.services;

//import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.exceptions.ExtractorServiceException;
import com.bokella.webxtractor.server.services.web.WebPageService;
import com.bokella.webxtractor.server.services.web.exceptions.WebPageServiceException;
import com.bokella.webxtractor.server.services.xtr.XtrGalleryService;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.tasks.XtrImageResolveTask;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

public class DefaultExtractorService implements ExtractorService {
	private static final Logger log = Logger.getLogger(DefaultExtractorService.class.getName());
	private WebPageService webPageService = null;
	private XtrGalleryService xtrGalleryService = null;
	private TaskService taskService = null;
	
	public DefaultExtractorService(
			WebPageService webPageService,
			XtrGalleryService xtrGalleryService,
			TaskService taskService) {
		this.webPageService = webPageService;
		this.xtrGalleryService = xtrGalleryService;
		this.taskService = taskService;
	}
	
	public XtrGallery createFrom(String input, URL url) throws ExtractorServiceException {
		WebPage webPage = null;
		try {
			webPage = this.webPageService.createFrom(url);
		} catch (WebPageServiceException e) {
			log.severe("Could not parse html: " + e.getMessage()); 
			throw new ExtractorServiceException("Could not parse html: " + e.getMessage());
		}
		log.info(webPage.getImages().size() + " images found");
		
		String[] tags = {input};
		List<XtrGallery> xtrGalleries = null;
		try {
			xtrGalleries = this.xtrGalleryService.addFrom(tags, webPage);
		} catch (XtrServiceException e) {
			log.severe("Could not parse html: " + e.getMessage()); 
			throw new ExtractorServiceException("Could not parse html: " + e.getMessage());
		}
		
		// determine the gallery to return
		XtrGallery xtrGallery = null;
		if (xtrGalleries.size() > 0) {
			 xtrGallery = xtrGalleries.get(0);
		}
		if (xtrGallery == null) {
			log.severe("No galleries found/created"); 
			throw new ExtractorServiceException("No galleries found/created");
		}
		
		List<XtrImage> xtrImages = this.xtrGalleryService.getImagesForGallery(xtrGallery);
		log.info(xtrImages.size() + " thumbs found");
		
		// spawn tasks to resolve non-image urls of images to actual image urls
		for(XtrImage xtrImage : xtrImages) {
			if (xtrImage.getThumbMatchIteration() == 1) {
				log.info("Skip adding resolve task for " + xtrImage.toString() + " as it is already iterated");
				continue;
			}
			
			String guessType = URLConnection.guessContentTypeFromName(xtrImage.getUrl());
			if ((guessType == null) || !guessType.startsWith("image")) {
				this.taskService.addTask(
					new XtrImageResolveTask(
						webPageService, 
						xtrGalleryService,
						this.taskService,
						xtrImage));
			}
		}
		
		this.taskService.process(XtrImageResolveTask.class.getName());
		
		return xtrGallery;
	}
}
