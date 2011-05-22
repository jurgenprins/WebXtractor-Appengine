package com.bokella.webxtractor.server.services.xtr.objects;

import java.net.URL;
import java.util.logging.Logger;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.objects.XtrImageDao;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.util.io.UrlContentReader;

public class GAEXtrImageService extends DefaultXtrImageService {
	private static final Logger log = Logger.getLogger(GAEXtrImageService.class.getName());

	private ImagesService imagesService = ImagesServiceFactory.getImagesService();
	
	public GAEXtrImageService (
			XtrImageDao imageDao,
			UrlContentReader urlContentReader) {
		super(imageDao, urlContentReader);
	}

	public boolean cacheThumb(XtrImage xtrImage) throws XtrServiceException {
		try {
			log.info("Fetching " + xtrImage.getThumbUrl() + " ..");
			byte[] imgData = this.getUrlContentReader().readBinary(new URL(xtrImage.getThumbUrl()));
		    
			log.info("Creating normalized thumb from " + imgData.length + " bytes");
			Image oldImg = ImagesServiceFactory.makeImage(imgData);
			Transform resize = ImagesServiceFactory.makeResize(200, 200);
			Image newImg = imagesService.applyTransform(resize, oldImg, ImagesService.OutputEncoding.JPEG);
			
			log.info("Storing thumb data.. " + newImg.getWidth());
			//thumb.setData(new Blob(newImg.getImageData()));
			String key = this.getImageDao().save(xtrImage);
			log.info("Thumb saved, key: " + key);
			
			return true;
		} catch (Exception e) { 
			log.severe("Could not cache thumb " + xtrImage.getThumbUrl() + " : " + e.getMessage());
			throw new XtrServiceException("Could not cache thumb " + xtrImage.getThumbUrl() + " : " + e.getMessage());
		}
	}
}
