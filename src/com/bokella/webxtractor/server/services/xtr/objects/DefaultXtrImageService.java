package com.bokella.webxtractor.server.services.xtr.objects;

import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.dao.xtr.objects.XtrImageDao;
import com.bokella.webxtractor.server.domain.web.WebImage;
import com.bokella.webxtractor.server.util.io.UrlContentReader;

public class DefaultXtrImageService implements XtrImageService {
	private static final Logger log = Logger.getLogger(DefaultXtrImageService.class.getName());
	
	private XtrImageDao imageDao = null;
	private UrlContentReader urlContentReader = null;
	
	public DefaultXtrImageService (
			XtrImageDao imageDao,
			UrlContentReader urlContentReader) {
		this.imageDao = imageDao;
		this.urlContentReader = urlContentReader;
	}

	public XtrImage createFromSmall(WebImage webImage) throws XtrServiceException {
		if (webImage.getUrl() == null) {
			throw new XtrServiceException("Cannot create thumb from original without a src to fetch");
		}
		
		try {
			List<XtrImage> existingImages = this.getImageDao().getByThumbUrl(webImage.getUrl().toString());
			if (existingImages.size() > 0) {
				if (existingImages.size() > 1) {
					log.warning("More images with same thumb url " + webImage.getUrl().toString() + ", returning best match");
				}
				return existingImages.get(0);
			}
		} catch (Exception e) {
			throw new XtrServiceException("Cannot create thumb from small without database check: " + e.getMessage());
		}
		
		if ((webImage.getParentLink() == null) ||
			(webImage.getParentLink().getUrl() == null)) {
			throw new XtrServiceException("Cannot create thumb from small image without a href to follow");
		}
		
		XtrImage xtrImage = new XtrImage();
		xtrImage.setThumbUrl(webImage.getUrl().toString());
		xtrImage.setThumbWidth(webImage.getWidth());
		xtrImage.setThumbHeight(webImage.getHeight());
		
		xtrImage.setUrl(webImage.getParentLink().getUrl().toString());
		
		String guessType = URLConnection.guessContentTypeFromName(xtrImage.getUrl());
		xtrImage.setThumbMatchScore(((guessType != null) && guessType.startsWith("image")) ? 100 : 0);
		
		this.saveImage(xtrImage);
		
		return xtrImage;
	}

	public XtrImage createFromOriginal(WebImage webImage) throws XtrServiceException {
		if (webImage.getUrl() == null) {
			throw new XtrServiceException("Cannot create thumb from original without a src to fetch");
		}

		try {
			return this.getImageDao().getByUrl(webImage.getUrl().toString());
		} catch (Exception e) {
			// there is no existing image, continue to create new
		}
		
		XtrImage xtrImage = new XtrImage();
		xtrImage.setUrl(webImage.getUrl().toString());
		
		if (webImage.getWidth() > 0) {
			xtrImage.setWidth(webImage.getWidth());
		}
		
		if (webImage.getHeight() > 0) {
			xtrImage.setHeight(webImage.getHeight());
		}
		
		this.saveImage(xtrImage);
		
		return xtrImage;
	}

	public void saveImage(XtrImage xtrImage) throws XtrServiceException {
		try {
			this.getImageDao().save(xtrImage);
		} catch (Exception e) {
			throw new XtrServiceException("Could not save image " + xtrImage.getUrl() + ": " + e.getMessage());
		}
	}
	
	public void deleteImage(XtrImage xtrImage) throws XtrServiceException {
		try {
			this.getImageDao().delete(xtrImage);
		} catch (Exception e) {
			throw new XtrServiceException("Could not delete image " + xtrImage.getUrl() + ": " + e.getMessage());
		}
	}
	
	public XtrImageDao getImageDao() {
		return imageDao;
	}

	public UrlContentReader getUrlContentReader() {
		return urlContentReader;
	}
}
