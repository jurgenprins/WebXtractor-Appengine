package com.bokella.webxtractor.server.services.xtr.objects;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.dao.xtr.objects.XtrImageDao;
import com.bokella.webxtractor.server.domain.web.WebImage;

public interface XtrImageService {
	public XtrImage createFromSmall(WebImage webImage) throws XtrServiceException;
	public XtrImage createFromOriginal(WebImage webImage) throws XtrServiceException;
	public void saveImage(XtrImage xtrImage) throws XtrServiceException;
	public void deleteImage(XtrImage xtrImage) throws XtrServiceException;
	public XtrImageDao getImageDao();
}
