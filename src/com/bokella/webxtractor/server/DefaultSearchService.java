package com.bokella.webxtractor.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.bokella.webxtractor.client.SearchService;
import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.services.ExtractorService;
import com.bokella.webxtractor.server.services.TaskService;
import com.bokella.webxtractor.server.services.xtr.XtrGalleryService;
import com.bokella.webxtractor.server.tasks.ExtractorTask;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DefaultSearchService extends SpringDispatchedRemoteServiceServlet 
				implements SearchService {
	private static final Logger log = Logger.getLogger(DefaultSearchService.class.getName());
	
	private XtrGalleryService xtrGalleryService;
	private TaskService taskService;
	private ExtractorService extractorService;
	
	public DefaultSearchService (
			XtrGalleryService xtrGalleryService,
			TaskService taskService,
			ExtractorService extractorService) {
		this.xtrGalleryService = xtrGalleryService;
		this.taskService = taskService;
		this.extractorService = extractorService;
	}
	
	public List<XtrImage> find(String baseUrl, String input, Boolean fresh, Boolean resolve) {
		//String baseUrl = "http://www.bokella.com/resources/flickr_index.html";
		//String baseUrl = "http://www.flickr.com/search/?q=".concat(input);
		//String baseUrl = "http://www.webshots.com/search?query=".concat(input).concat("&new=1&source=chromeheader");
		//String baseUrl = "http://search.fotki.com/?q=".concat(input).concat("&p=4");
		
		if (fresh) {
			xtrGalleryService.getXtrImageService().getImageDao().deleteAll();
			xtrGalleryService.getGalleryDao().deleteAll();
		}
		
		if (resolve) {
			try {
				this.taskService.addTask(
						new ExtractorTask(
							this.extractorService, 
							input,
							new URL(baseUrl)));
			} catch (MalformedURLException e) {
				log.warning("When spawning extraction task: " + e.getMessage());
			} 
		
			this.taskService.process(ExtractorTask.class.getName());
		}
		
		List<XtrImage> results = new ArrayList<XtrImage>();
		
		XtrGallery xtrGallery = xtrGalleryService.getGalleryByTag(input);
		if (xtrGallery != null) {
			results.addAll(xtrGalleryService.getImagesForGallery(xtrGallery));
		}
		
		return results;
		
				
	}
}
