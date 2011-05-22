package com.bokella.webxtractor.server.dao.xtr;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;
import com.bokella.webxtractor.server.util.dao.JDOUtil;

public class JDOXtrGalleryDao implements XtrGalleryDao {
	private static final Logger log = Logger.getLogger(JDOXtrGalleryDao.class.getName());
	
	public XtrGallery getByTag(String tag) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			XtrGallery xtrGallery = pm.getObjectById(XtrGallery.class, tag);
			pm.close();
			return xtrGallery;
		} catch (Exception e) {
			pm.close();
			log.severe("Failed to retrieve gallery + " + tag + ": " + e.getMessage());
			throw new XtrDaoException("Failed to retrieve gallery + " + tag + ": " + e.getMessage());			
		}
	}
	
	public List<XtrGallery> getByImage(String imageUrl) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		
		Query q = pm.newQuery(XtrGallery.class);
		q.setFilter("images == imageParam");
		q.declareParameters("String imageParam");
		
		List<XtrGallery> results;
		try {
			results = (List<XtrGallery>) q.execute(imageUrl);
			log.info("Found " + results.size() + " results");
		} finally {
			q.closeAll();
			pm.close();
		}
		
		return results;
	}
	
 	public List<XtrGallery> getAll() {
		PersistenceManager pm = this.getPersistenceManager();
		
		Query dqry = pm.newQuery(XtrGallery.class);
		
		List<XtrGallery> results;
		try {
			results = (List<XtrGallery>) dqry.execute();
			log.info("Found " + results.size() + " results");
		    if (results.iterator().hasNext()) {
		    } else {
		    	
		    }
		} finally {
			dqry.closeAll();
			pm.close();
		}
		
		return results;
	}
	
	public String save(XtrGallery xtrGallery) throws XtrDaoException {
		PersistenceManager pm = this.getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(xtrGallery);
			pm.currentTransaction().commit();
			log.info("Gallery stored.. " + xtrGallery.getTag());
		} finally {
			pm.close();
		}
		
		return xtrGallery.getTag();
	}

	public void deleteAll() {
		PersistenceManager pm = this.getPersistenceManager();
		
		Query dqry = pm.newQuery(XtrGallery.class);
		dqry.deletePersistentAll();
	}
	
	public PersistenceManager getPersistenceManager() {
		return JDOUtil.getPersistenceManagerFactory().getPersistenceManager();
	}
}
