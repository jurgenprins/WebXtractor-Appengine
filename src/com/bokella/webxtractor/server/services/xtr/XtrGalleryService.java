package com.bokella.webxtractor.server.services.xtr;

import java.util.List;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.XtrGalleryDao;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.services.xtr.objects.XtrDataSourceService;
import com.bokella.webxtractor.server.services.xtr.objects.XtrImageService;

public interface XtrGalleryService {
	public List<XtrGallery> addFrom(String[] tags, WebPage webPage) throws XtrServiceException;
	public List<XtrGallery> addFrom(String[] tags, List<XtrImage> xtrImages) throws XtrServiceException;
	XtrGallery getGalleryByTag(String tag);
	public List<XtrGallery> getGalleriesByImage(XtrImage xtrImage);
	public void addImagesToGallery(XtrGallery xtrGallery, List<XtrImage> xtrImages);
	public void removeImagesFromGallery(XtrGallery xtrGallery, List<XtrImage> xtrImages);
	public List<XtrImage> getImagesForGallery(XtrGallery xtrGallery);
	public void addDataSourcesToGallery(XtrGallery xtrGallery, List<XtrDataSource> xtrDataSources);
	public void removeDataSourcesFromGallery(XtrGallery xtrGallery, List<XtrDataSource> xtrDataSources);
	public List<XtrDataSource> getDataSourcesForGallery(XtrGallery xtrGallery);
	public XtrGalleryDao getGalleryDao();
	public void saveGallery(XtrGallery xtrGallery) throws XtrServiceException;
	public XtrImageService getXtrImageService();
	public XtrDataSourceService getXtrDataSourceService();
}
