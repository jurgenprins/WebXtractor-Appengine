package com.bokella.webxtractor.server.services.xtr.objects;

import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.server.services.xtr.exceptions.XtrServiceException;
import com.bokella.webxtractor.server.dao.xtr.objects.XtrDataSourceDao;
import com.bokella.webxtractor.server.domain.web.WebLink;

public class DefaultXtrDataSourceService implements XtrDataSourceService {
	private static final Logger log = Logger.getLogger(DefaultXtrDataSourceService.class.getName());
	
	private XtrDataSourceDao dataSourceDao = null;
	
	public DefaultXtrDataSourceService (
			XtrDataSourceDao dataSourceDao) {
		this.dataSourceDao = dataSourceDao;
	}

	public XtrDataSource createFrom(WebLink webLink) throws XtrServiceException {
		if (webLink.getUrl() == null) {
			throw new XtrServiceException("Cannot create datasource from link without a url to fetch");
		}
		
		try {
			return this.getDataSourceDao().getByUrl(webLink.getUrl().toString());
		} catch (Exception e) { }
		
		XtrDataSource xtrDataSource = new XtrDataSource();
		xtrDataSource.setUrl(webLink.getUrl().toString());
		
		this.saveDataSource(xtrDataSource);
		
		return xtrDataSource;
	}
	
	public void saveDataSource(XtrDataSource xtrDataSource) throws XtrServiceException {
		try {
			this.getDataSourceDao().save(xtrDataSource);
		} catch (Exception e) {
			throw new XtrServiceException("Could not save datasource " + xtrDataSource.getUrl() + ": " + e.getMessage());
		}
	}
	
	public void deleteDataSource(XtrDataSource xtrDataSource) throws XtrServiceException {
		try {
			this.getDataSourceDao().delete(xtrDataSource);
		} catch (Exception e) {
			throw new XtrServiceException("Could not delete datasource " + xtrDataSource.getUrl() + ": " + e.getMessage());
		}
	}
	
	public XtrDataSourceDao getDataSourceDao() {
		return dataSourceDao;
	}
}
