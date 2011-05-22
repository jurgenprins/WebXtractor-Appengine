package com.bokella.webxtractor.server.dao.xtr.objects;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.bokella.webxtractor.domain.xtr.objects.XtrDataSource;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;
import com.bokella.webxtractor.server.util.dao.JDOUtil;

public class JDOXtrDataSourceDao implements XtrDataSourceDao {
	private static final Logger log = Logger.getLogger(JDOXtrDataSourceDao.class.getName());
	
	public XtrDataSource getByKey(String key) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			XtrDataSource dataSource = pm.getObjectById(XtrDataSource.class, key);
			pm.close();
			return dataSource;
		} catch (Exception e) {
			pm.close();
			log.severe("Failed to retrieve datasource " + key + ": " + e.getMessage());
			throw new XtrDaoException("Failed to retrieve datasource " + key + ": " + e.getMessage());			
		}
	}
	
	public XtrDataSource getByUrl(String url) throws XtrDaoException {
		return this.getByKey(url);
	}
	
	public String save(XtrDataSource xtrDataSource) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(xtrDataSource);
			pm.currentTransaction().commit();
			log.info("DataSource stored.. " + xtrDataSource.getKey() + " : " + xtrDataSource.toString());
		} finally {
			pm.close();
		}
		
		return xtrDataSource.getUrl();
	}

	public void delete(XtrDataSource xtrDataSource) {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			XtrDataSource xtrPersistedDataSource = pm.getObjectById(XtrDataSource.class, xtrDataSource.getKey());
			pm.deletePersistent(xtrPersistedDataSource);
			pm.currentTransaction().commit();
			log.info("DataSource deleted.. " + xtrDataSource.getKey() + " : " + xtrDataSource.toString());
		} finally {
			pm.close();
		}
	}
	
	public void deleteAll() {
		PersistenceManager pm = this.getPersistenceManager();
		
		Query dqry = pm.newQuery(XtrImage.class);
		dqry.deletePersistentAll();
	}
	
	public PersistenceManager getPersistenceManager() {
		return JDOUtil.getPersistenceManagerFactory().getPersistenceManager();
	}
}
