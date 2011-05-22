package com.bokella.webxtractor.domain.xtr;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false") 
public class XtrGallery implements IsSerializable {
	@PrimaryKey  
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String key;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)  
	@Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true") 
	private String tag = null;
	
	@Persistent(defaultFetchGroup = "true")
	private transient Set<String> images = new HashSet<String>();
	
	@Persistent(defaultFetchGroup = "true")
	private transient Set<String> dataSources = new HashSet<String>();
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Set<String> getImages() {
		return images;
	}
	
	public void setImages(Set<String> images) {
		this.images = images;
	}
	
	public Set<String> getDataSources() {
		return dataSources;
	}
	
	public void setDataSources(Set<String> dataSources) {
		this.dataSources = dataSources;
	}

}
