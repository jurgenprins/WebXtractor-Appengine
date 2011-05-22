package com.bokella.webxtractor.server.dao.xtr.objects;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.dao.xtr.exceptions.XtrDaoException;

public interface XtrImageDao {
	public XtrImage getByKey(String key) throws XtrDaoException;
	public XtrImage getByUrl(String url) throws XtrDaoException;
	public List<XtrImage> getByThumbUrl(String thumbUrl) throws XtrDaoException;
	public String save(XtrImage thumb) throws XtrDaoException;
	public void delete(XtrImage xtrImage);
	public void deleteAll();
	public PersistenceManager getPersistenceManager();
}
