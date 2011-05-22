package com.bokella.webxtractor.server.dao.xtr.objects;

import javax.jdo.PersistenceManager;

import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;

public interface XtrDataSourceDao {
	public XtrDataSource getByUrl(String url) throws XtrDaoException;
	public String save(XtrDataSource xtrDataSource) throws XtrDaoException;
	public void delete(XtrDataSource xtrDataSource);
	public void deleteAll();
	public PersistenceManager getPersistenceManager();
}
