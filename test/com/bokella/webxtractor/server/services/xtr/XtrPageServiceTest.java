package com.bokella.webxtractor.server.services.xtr;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.JDOXtrGalleryDao;
import com.bokella.webxtractor.server.dao.xtr.objects.JDOXtrDataSourceDao;
import com.bokella.webxtractor.server.dao.xtr.objects.JDOXtrImageDao;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.web.DefaultWebPageService;
import com.bokella.webxtractor.server.services.web.WebPageService;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebImageService;
import com.bokella.webxtractor.server.services.web.objects.DefaultWebLinkService;
import com.bokella.webxtractor.server.services.xtr.objects.DefaultXtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.DefaultXtrImageService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrImageService;
import com.bokella.webxtractor.server.util.io.DefaultUrlContentReader;
import com.bokella.webxtractor.server.util.test.GAELocalServiceTestCase;

public class XtrPageServiceTest extends GAELocalServiceTestCase {
	private static final Logger log = Logger.getLogger(XtrPageServiceTest.class.getName());
	
	private WebPageService webPageService;
	private XtrImageService xtrImageService;
	private XtrDataSourceService xtrDataSourceService;
	private XtrGalleryService xtrGalleryService;
	
	public void setUp() throws Exception {
		super.setUp();
       
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
		
		super.setUp();
	}

	public void testFlickrThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/flickr_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
	
		String[] tags = {"test_flickr"};
		List<XtrGallery> xtrGalleries = xtrGalleryService.addFrom(tags, webPage);
		XtrGallery xtrGallery = xtrGalleries.get(0);
		
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(58, xtrImages.size());
		
		assertCorrectGalleryPersistence(xtrGallery);
	}
	
	public void testWebshotsThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/webshots_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
		
		String[] tags = {"test_webshots"};
		List<XtrGallery> xtrGalleries = xtrGalleryService.addFrom(tags, webPage);
		XtrGallery xtrGallery = xtrGalleries.get(0);
		
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(72, xtrImages.size()); 
		
		assertCorrectGalleryPersistence(xtrGallery);
	}
	
	public void testFotkiThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/fotki_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
	
		String[] tags = {"test_flickr"};
		List<XtrGallery> xtrGalleries = xtrGalleryService.addFrom(tags, webPage);
		XtrGallery xtrGallery = xtrGalleries.get(0);
		
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(11, xtrImages.size());
		
		assertCorrectGalleryPersistence(xtrGallery);
	}
	
	public void testFreeonesLinks() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/freeones_portal.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
	
		String[] tags = {"eva+angelina"};
		/*
		List<XtrLinkCollection> xtrLinkCollections = xtrLinkCollectionService.addFrom(tags, webPage);
		XtrLinkCollection xtrLinkCollection = xtrLinkCollections.get(0);
		
		List<XtrLink> xtrLinks = xtrLinkCollectionService.getLinksForLinkCollection(xtrLinkCollection);
		assertEquals(11, xtrLinks.size());
		
		assertCorrectLinkCollectionPersistence(xtrLinkCollection);
		*/
	}
		
	public void testFreeonesThumbs() throws Exception {
		URL sourceUrl = new URL("http://localhost:8888/resources/freeones_index.html");
		WebPage webPage = webPageService.createFrom(sourceUrl);
	
		String[] tags = {"eva+angelina"};
		List<XtrGallery> xtrGalleries = xtrGalleryService.addFrom(tags, webPage);
		XtrGallery xtrGallery = xtrGalleries.get(0);
		
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		assertEquals(11, xtrImages.size());
		
		assertCorrectGalleryPersistence(xtrGallery);
	}
	
		
	public void assertCorrectGalleryPersistence(XtrGallery xtrGallery) throws Exception {
		xtrGalleryService.getGalleryDao().save(xtrGallery);
		
		XtrGallery xtrGallery2 = xtrGalleryService.getGalleryDao().getByTag(xtrGallery.getTag());
		assertEquals(xtrGallery.getTag(), xtrGallery2.getTag());
	
		List<XtrImage> xtrImages = xtrGalleryService.getImagesForGallery(xtrGallery);
		List<XtrImage> xtrImages2 = xtrGalleryService.getImagesForGallery(xtrGallery2);
		
		for(XtrImage xtrImage1 : xtrImages) {
			boolean found = false;
			for (XtrImage xtrImage2 : xtrImages2) {
				if (xtrImage1.getUrl().equals(xtrImage2.getUrl())) {
					found = true;
				}
			}
			if (!found) {
				log.info(xtrImage1.getUrl().toString() + " cannot be found!");
			}
			assertTrue(found);
		}
		
		assertEquals(xtrImages.size(), xtrImages2.size());
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

}
