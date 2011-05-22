package com.bokella.webxtractor.server.services.xtr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.XtrGalleryDao;
import com.bokella.webxtractor.server.domain.web.WebImage;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.services.xtr.objects.XtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrImageService;

public class DefaultXtrGalleryService implements XtrGalleryService {
	private static final Logger log = Logger.getLogger(DefaultXtrGalleryService.class.getName());
	
	private XtrGalleryDao galleryDao = null;
	private XtrImageService xtrImageService = null;
	private XtrDataSourceService xtrDataSourceService = null;
	
	public static final int MIN_IMAGE_WIDTH = 50;
	public static final int MIN_IMAGE_HEIGHT = 50;
	public static final int MIN_IMAGE_GROUPCOUNT = 10;
	
	public DefaultXtrGalleryService(
			XtrGalleryDao galleryDao,
			XtrImageService xtrImageService,
			XtrDataSourceService xtrDataSourceService) {
		this.galleryDao = galleryDao;
		this.xtrImageService = xtrImageService;
		this.xtrDataSourceService = xtrDataSourceService;
	}
	
	public List<XtrGallery> addFrom(String[] tags, WebPage webPage) throws XtrServiceException {
		// first try to find images from webPage that represent thumbnails
		Map<String, XtrImage> imagesByUrl = new HashMap<String, XtrImage>();
		Map<String, Map<String, WebImage>> imagesByParentXPath = new HashMap<String, Map<String, WebImage>>();
		
		for (WebImage webImage : webPage.getImages()) {
			if ((webImage.getUrl() == null) || (webImage.getParentLink() == null)) {
				continue;
			}
			
			if ((webImage.getWidth() > 0) && (webImage.getWidth() < MIN_IMAGE_WIDTH) &&
			    (webImage.getHeight() > 0) && (webImage.getHeight() < MIN_IMAGE_HEIGHT)) {
				continue;
			}
		
			Map<String, WebImage> webImages = imagesByParentXPath.get(webImage.getParentXPath());
			if (webImages == null) {
				imagesByParentXPath.put(webImage.getParentXPath(), new HashMap<String, WebImage>());
				webImages = imagesByParentXPath.get(webImage.getParentXPath());
			}
			webImages.put(webImage.getUrl().toString(), webImage);
		}
		
    	Iterator iter = imagesByParentXPath.entrySet().iterator();
    	Iterator iter2 = null;
    	WebImage webImage = null;
    	XtrImage xtrImage = null;
        while(iter.hasNext()) {
          Map.Entry mEntry = (Map.Entry)iter.next();
          if (((Map<String, WebImage>)mEntry.getValue()).size() > MIN_IMAGE_GROUPCOUNT) {
        	  iter2 = ((Map<String, WebImage>)mEntry.getValue()).entrySet().iterator();
        	  while(iter2.hasNext()) {
        		  Map.Entry mEntry2 = (Map.Entry)iter2.next();
        		  webImage = (WebImage)mEntry2.getValue();
        		  try {
        			  //log.info("From group " + mEntry.getKey().toString() + " (" + ((Map<String, WebImage>)mEntry.getValue()).size() + ") create " + webImage.getUrl().toString());
        			  xtrImage = this.xtrImageService.createFromSmall(webImage);
        			  imagesByUrl.put(xtrImage.getUrl(), xtrImage);
        		  } catch (XtrServiceException e) {
        			  log.warning("Could not add " + webImage.getKey() + ": " + e.getMessage());
        		  }
        	  }
          } else {
        	  //log.info("Skip group " + mEntry.getKey().toString() + " containing only " + ((Map<String, WebImage>)mEntry.getValue()).size() + " entries");
          }
        }
        
        log.info("From " + webPage.getUrl().toString() + " will add " + imagesByUrl.values().size() + " images..");
        return this.addFrom(tags, new ArrayList<XtrImage>(imagesByUrl.values()));
	}
	
    public List<XtrGallery> addFrom(String[] tags, List<XtrImage> xtrImages) throws XtrServiceException {
    	List<XtrGallery> xtrGalleries = new ArrayList<XtrGallery>();
		
    	List<XtrImage> sameThumbs = null;
    	Map<String, XtrImage> toRemoveImages = new HashMap<String, XtrImage>();
    	for (XtrImage xtrImage : xtrImages) {
    		try {
	    		sameThumbs = this.getXtrImageService().getImageDao().getByThumbUrl(xtrImage.getThumbUrl());
	    		for (XtrImage sameThumb : sameThumbs) {
	    			if (sameThumb.getUrl().equals(xtrImage.getUrl())) {
	    				continue;
	    			}
	    			if (sameThumb.getThumbMatchIteration() > xtrImage.getThumbMatchIteration()) {
	    				toRemoveImages.put(xtrImage.getUrl(), xtrImage);
	    				log.info("Skip to add " + xtrImage.toString() + " as we already have a more evolved " + sameThumb.toString());
	    				continue;
	    			}
	    			if (sameThumb.getThumbMatchScore() >= xtrImage.getThumbMatchScore()) {
	    				toRemoveImages.put(xtrImage.getUrl(), xtrImage);
	    				log.info("Skip to add " + xtrImage.toString() + " as we already have a better scoring " + sameThumb.toString());
	    			}
	    		}
    		} catch (Exception e) {
    			log.warning("While detecting same thumbs for " + xtrImage.getThumbUrl() + ": " + e.getMessage());
    		}
    	}
    	xtrImages.removeAll(toRemoveImages.values());
    	
        for (String tag : tags) {
        	XtrGallery xtrGallery = this.getGalleryByTag(tag);
        	if (xtrGallery == null) {
        		log.info("Gallery was not found, creating it " + tag);
        		xtrGallery = new XtrGallery();
            	xtrGallery.setTag(tag);
            	this.addImagesToGallery(xtrGallery, xtrImages);
            	try {
            		this.saveGallery(xtrGallery);
            	} catch (Exception e) {
            		xtrGallery = null;
            		log.warning("Could not save gallerie(s): " + e.getMessage());
            	}
        	}
        	
        	if (xtrGallery == null) {
        		continue;
        	}
        	
    		xtrGalleries.add(xtrGallery);
        }
        
		return xtrGalleries;
	}
	
	public XtrGallery getGalleryByTag(String tag) {
		try {
			return this.getGalleryDao().getByTag(tag);
		} catch (Exception e) {
			log.info("Could not get gallery " + tag + " : " + e.getMessage());
			return null;
    	}
	}
	
	public List<XtrGallery> getGalleriesByImage(XtrImage xtrImage) {
		try {
			return this.getGalleryDao().getByImage(xtrImage.getUrl());
		} catch (Exception e) {
			log.info("Could not get galleries for image " + xtrImage.getUrl() + " : " + e.getMessage());
			return null;
    	}
	}
	
	public void saveGallery(XtrGallery xtrGallery) throws XtrServiceException {
		try {
			this.getGalleryDao().save(xtrGallery);
		} catch (Exception e) {
			throw new XtrServiceException("Could not save gallery " + xtrGallery.getTag() + ": " + e.getMessage());
		}
		log.info("Gallery saved " + xtrGallery.getTag());
	}
	
	public void addDataSourcesToGallery(XtrGallery xtrGallery, List<XtrDataSource> xtrDataSources) {
		for (XtrDataSource xtrDataSource : xtrDataSources) {
			xtrGallery.getDataSources().add(xtrDataSource.getUrl());
		}
	}
	
	public void removeDataSourcesFromGallery(XtrGallery xtrGallery, List<XtrDataSource> xtrDataSources) {
		for (XtrDataSource xtrDataSource : xtrDataSources) {
			xtrGallery.getDataSources().remove(xtrDataSource.getUrl());
		}
	}
	
	public List<XtrDataSource> getDataSourcesForGallery(XtrGallery xtrGallery) {
		List<XtrDataSource> xtrDataSources = new ArrayList<XtrDataSource>();
		
		Set<String> dataSourceUrls = xtrGallery.getDataSources();
		for (String dataSourceUrl : dataSourceUrls) {
			try {
				xtrDataSources.add(this.getXtrDataSourceService().getDataSourceDao().getByUrl(dataSourceUrl));
			} catch (Exception e) {
				log.info("Could not get datasource " + dataSourceUrl + " for gallery " + xtrGallery.getTag() + ": " + e.getMessage());
			}
		}
		
		log.info(xtrDataSources.size() + " xtrdatasources returned for " + xtrGallery.toString());
		return xtrDataSources;
	}
	
	public void addImagesToGallery(XtrGallery xtrGallery, List<XtrImage> xtrImages) {
		for (XtrImage xtrImage : xtrImages) {
			xtrGallery.getImages().add(xtrImage.getUrl());
		}
	}
	
	public void removeImagesFromGallery(XtrGallery xtrGallery, List<XtrImage> xtrImages) {
		for (XtrImage xtrImage : xtrImages) {
			xtrGallery.getImages().remove(xtrImage.getUrl());
		}
	}
	
	public List<XtrImage> getImagesForGallery(XtrGallery xtrGallery) {
		List<XtrImage> xtrImages = new ArrayList<XtrImage>();
		
		Set<String> imageUrls = xtrGallery.getImages();
		for (String imageUrl : imageUrls) {
			try {
				xtrImages.add(this.getXtrImageService().getImageDao().getByUrl(imageUrl));
			} catch (Exception e) {
				log.info("Could not get image " + imageUrl + " for gallery " + xtrGallery.getTag() + ": " + e.getMessage());
			}
		}
		
		log.info(xtrImages.size() + " xtrimages returned for " + xtrGallery.toString());
		return xtrImages;
	}
	
	public XtrGalleryDao getGalleryDao() {
		return galleryDao;
	}
	
	public XtrImageService getXtrImageService() {
		return xtrImageService;
	}
	
	public XtrDataSourceService getXtrDataSourceService() {
		return xtrDataSourceService;
	}

}
