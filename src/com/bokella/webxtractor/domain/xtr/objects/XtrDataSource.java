package com.bokella.webxtractor.domain.xtr.objects;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false") 
public class XtrDataSource implements IsSerializable {
	@PrimaryKey  
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String key;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)  
	@Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true") 
	private String url = null;
	
	public String getKey() {
		return key;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
