package com.bokella.webxtractor.server.services.xtr.objects;


import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.dao.xtr.objects.XtrDataSourceDao;
import com.bokella.webxtractor.server.domain.web.WebLink;

public interface XtrDataSourceService {
	public XtrDataSource createFrom(WebLink webLink) throws XtrServiceException;
	public void saveDataSource(XtrDataSource xtrDataSource) throws XtrServiceException;
	public void deleteDataSource(XtrDataSource xtrDataSource) throws XtrServiceException;
	public XtrDataSourceDao getDataSourceDao();
}
