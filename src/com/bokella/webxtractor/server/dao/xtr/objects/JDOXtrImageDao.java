package com.bokella.webxtractor.server.dao.xtr.objects;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;
import com.bokella.webxtractor.server.util.dao.JDOUtil;

public class JDOXtrImageDao implements XtrImageDao {
	private static final Logger log = Logger.getLogger(JDOXtrImageDao.class.getName());
	
	public XtrImage getByKey(String key) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			XtrImage thumb = pm.getObjectById(XtrImage.class, key);
			pm.close();
			return thumb;
		} catch (Exception e) {
			pm.close();
			log.severe("Failed to retrieve image " + key + ": " + e.getMessage());
			throw new XtrDaoException("Failed to retrieve image " + key + ": " + e.getMessage());			
		}
	}
	
	public XtrImage getByUrl(String url) throws XtrDaoException {
		return this.getByKey(url);
	}
	
	public List<XtrImage> getByThumbUrl(String thumbUrl) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		
		Query q = pm.newQuery(XtrImage.class);
		q.setFilter("thumb_url == thumbUrlParam");
		q.setOrdering("thumb_match_score desc, thumb_match_iteration desc");
		q.declareParameters("String thumbUrlParam");
		
		List<XtrImage> results;
		try {
			results = (List<XtrImage>) q.execute(thumbUrl);
			log.info("Found " + results.size() + " results");
		} finally {
			q.closeAll();
			pm.close();
		}
		
		return results;
	}
	
	public String save(XtrImage xtrImage) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(xtrImage);
			pm.currentTransaction().commit();
			log.info("Image stored.. " + xtrImage.getKey() + " : " + xtrImage.toString());
		} finally {
			pm.close();
		}
		
		return xtrImage.getUrl();
	}

	public void delete(XtrImage xtrImage) {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			XtrImage xtrPersistedImage = pm.getObjectById(XtrImage.class, xtrImage.getKey());
			pm.deletePersistent(xtrPersistedImage);
			pm.currentTransaction().commit();
			log.info("Image deleted.. " + xtrImage.getKey() + " : " + xtrImage.toString());
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
