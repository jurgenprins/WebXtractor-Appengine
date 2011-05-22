package com.bokella.webxtractor.server.dao.xtr;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;

public interface XtrGalleryDao {
	public XtrGallery getByTag(String tag) throws XtrDaoException;
	public List<XtrGallery> getByImage(String imageUrl) throws XtrDaoException;
	public List<XtrGallery> getAll();
	public String save(XtrGallery xtrGallery) throws XtrDaoException;
	public void deleteAll();
	public PersistenceManager getPersistenceManager();
}
