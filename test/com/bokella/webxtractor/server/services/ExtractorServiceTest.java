package com.bokella.webxtractor.server.services;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.JDOXtrGalleryDao;
import com.bokella.webxtractor.server.dao.xtr.objects.JDOXtrDataSourceDao;
import com.bokella.webxtractor.server.dao.xtr.objects.JDOXtrImageDao;
import com.bokella.webxtractor.server.services.web.DefaultWebPageService;
import com.bokella.webxtractor.server.services.web.WebPageService;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebImageService;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebLinkService;
import com.bokella.webxtractor.server.services.xtr.DefaultXtrGalleryService;
import com.bokella.webxtractor.server.services.xtr.XtrGalleryService;
import com.bokella.webxtractor.server.services.xtr.objects.DefaultXtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.DefaultXtrImageService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrImageService;
import com.bokella.webxtractor.server.tasks.XtrImageResolveTask;
import com.bokella.webxtractor.server.util.io.DefaultUrlContentReader;
import com.bokella.webxtractor.server.util.test.GAELocalServiceTestCase;


public class ExtractorServiceTest extends GAELocalServiceTestCase {
	private static final Logger log = Logger.getLogger(ExtractorServiceTest.class.getName());
	
	private WebPageService webPageService;
	private XtrImageService xtrImageService;
	private XtrDataSourceService xtrDataSourceService;
	private XtrGalleryService xtrGalleryService;
	private TaskService taskService;
	private DefaultExtractorService extractorService;
	
	public void setUp() throws Exception {
		webPageService = new DefaultWebPageService(
							new DefaultUrlContentReader(),
							new DefaultWebLinkService(),
							new DefaultWebImageService(
								new DefaultWebLinkService()));
		
		xtrImageService = new DefaultXtrImageService(
				new JDOXtrImageDao(),
				new DefaultUrlContentReader());
		
		xtrDataSourceService = new DefaultXtrDataSourceService(
				new JDOXtrDataSourceDao());
		
		xtrGalleryService = new DefaultXtrGalleryService(
								new JDOXtrGalleryDao(),
								xtrImageService,
								xtrDataSourceService);
		
		taskService = new DefaultTaskService();
		
		extractorService = new DefaultExtractorService(
								webPageService, 
								xtrGalleryService,
								taskService);
		
		super.setUp();
	}

	public void testFlickrThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/flickr_index.html");
		XtrGallery xtrGallery = extractorService.createFrom("flickr_test", sourceUrl);
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(1, xtrImages.size()); 
		
		taskService.process(XtrImageResolveTask.class.getName());
				
		/*
		ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
		LocalTaskQueue ltq = (LocalTaskQueue) proxy.getService(LocalTaskQueue.PACKAGE);
		String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
        assertEquals(58, qsi.getTaskInfo().size());
        */
	}
	
	public void testWebshotsThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/webshots_index.html");
		XtrGallery xtrGallery = extractorService.createFrom("webshots_test", sourceUrl);
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(72, xtrImages.size()); 
		
		taskService.process(XtrImageResolveTask.class.getName());
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}

}
