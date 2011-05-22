package com.bokella.webxtractor.domain.xtr.objects;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;

import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false") 
public class XtrImage implements IsSerializable, Cloneable {
	@PrimaryKey  
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String key;
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)  
	@Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true") 
	private String url = null;
	
	@Persistent
	private Integer width = 0;
	
	@Persistent
	private Integer height = 0;
	
	@Persistent
	private String thumb_url = null;
	
	@Persistent
	private Integer thumb_width = 0;
	
	@Persistent
	private Integer thumb_height = 0;

	@Persistent
	private Integer thumb_match_score = 0;
	
	@Persistent
	private Integer thumb_match_iteration = 0;
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setThumbUrl(String origUrl) {
		this.thumb_url = origUrl;
	}

	public void setThumbWidth(Integer origWidth) {
		this.thumb_width = origWidth;
	}

	public void setThumbHeight(Integer origHeight) {
		this.thumb_height = origHeight;
	}
	
	public void setThumbMatchScore(Integer matchScore) {
		this.thumb_match_score = matchScore;
	}
	
	public void setThumbMatchIteration(Integer matchIteration) {
		this.thumb_match_iteration = matchIteration;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Integer getWidth() {
		return width;
	}
	
	public Integer getHeight() {
		return height;
	}
	
	public String getThumbUrl() {
		return thumb_url;
	}
	
	public Integer getThumbWidth() {
		return thumb_width;
	}
	
	public Integer getThumbHeight() {
		return thumb_height;
	}
	
	public Integer getThumbMatchScore() {
		return thumb_match_score;
	}
	
	public Integer getThumbMatchIteration() {
		return thumb_match_iteration;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("img");
		if (url != null) {
			sb.append(" src=\""); 
			sb.append(url.toString());
			sb.append("\"");
		}
		if (width > 0) {
			sb.append(" width=\""); 
			sb.append(width);
			sb.append("\"");
		}
		if (height > 0) {
			sb.append(" height=\""); 
			sb.append(height);
			sb.append("\"");
		}
		if (thumb_url != null) {
			sb.append(" thumbsrc=\""); 
			sb.append(thumb_url.toString());
			sb.append("\"");
		}
		if (thumb_width > 0) {
			sb.append(" thumbwidth=\""); 
			sb.append(thumb_width);
			sb.append("\"");
		}
		if (thumb_height > 0) {
			sb.append(" thumbheight=\""); 
			sb.append(thumb_height);
			sb.append("\"");
		}
		if (thumb_match_iteration > 0) {
			sb.append(" thumbmatchiteration=\""); 
			sb.append(thumb_match_iteration);
			sb.append("\"");
		}
		if (thumb_match_score > 0) {
			sb.append(" thumbmatchscore=\""); 
			sb.append(thumb_match_score);
			sb.append("\"");
		}
		
		return sb.toString();
	}
	
	public XtrImage clone() {
		XtrImage clone = new XtrImage();
		clone.key = this.key;
		clone.url = this.url;
		clone.width = this.width;
		clone.height = this.height;
		clone.thumb_url = this.thumb_url;
		clone.thumb_width = this.thumb_width;
		clone.thumb_height = this.thumb_height;
		clone.thumb_match_score = this.thumb_match_score;
		clone.thumb_match_iteration = this.thumb_match_iteration;
		return clone;
	}
}
