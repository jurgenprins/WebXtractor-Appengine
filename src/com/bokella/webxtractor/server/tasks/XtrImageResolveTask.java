package com.bokella.webxtractor.server.tasks;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.bokella.webxtractor.domain.xtr.XtrGallery;
import com.bokella.webxtractor.domain.xtr.objects.XtrImage;
import com.bokella.webxtractor.server.domain.web.WebImage;
import com.bokella.webxtractor.server.domain.web.WebLink;
import com.bokella.webxtractor.server.domain.web.WebPage;
import com.bokella.webxtractor.server.services.TaskService;
import com.bokella.webxtractor.server.services.web.WebPageService;
import com.bokella.webxtractor.server.services.xtr.XtrGalleryService;

public class XtrImageResolveTask extends XtrTask {
	private static final Logger log = Logger.getLogger(XtrImageResolveTask.class.getName());
	
	private static final int MAX_LINKS_TO_CONSIDER = 1;
	
	private WebPageService webPageService = null;
	private XtrGalleryService xtrGalleryService = null;
	private TaskService taskService = null;
	private XtrImage xtrImage = null;
	
	public XtrImageResolveTask(
			WebPageService webPageService,
			XtrGalleryService xtrGalleryService,
			TaskService taskService,
			XtrImage xtrImage) {
		super("resolve " + xtrImage.toString());
		
		this.webPageService = webPageService;
		this.xtrGalleryService = xtrGalleryService;
		this.taskService = taskService;
		this.xtrImage = xtrImage;
		
		this.setPayloadAttribute("xtrImageKey", this.xtrImage.getKey());
	}
	
	public XtrImageResolveTask(
			WebPageService webPageService,
			XtrGalleryService xtrGalleryService,
			TaskService taskService,
			byte[] payload) {
		super("?", payload);
		
		this.webPageService = webPageService;
		this.xtrGalleryService = xtrGalleryService;
		this.taskService = taskService;
		
		String xtrImageKey = (String)this.getPayloadAttribute("xtrImageKey");
		
		try {
			this.xtrImage = xtrGalleryService.getXtrImageService().getImageDao().getByKey(xtrImageKey);
		} catch (Exception e) {
			log.severe("Cannot instantiate image from key " + xtrImageKey);
		}
		
		this.setName("resolve " + xtrImage.toString());
	}
	
	public void execute() {
		URL thumbUrl = null;
		URL origUrl = null;
		WebPage webPage = null; 
	
		log.info("Executing task " + this.getName());
		
		if (xtrImage == null) {
			log.warning("Image is not set, so cannot resolve");
			return;
		}
		
		if (xtrImage.getThumbUrl() == null) {
			log.warning("Image " + xtrImage.toString() + " does not have a thumb url set, so cannot resolve");
			return;
		}
		
		try {
			thumbUrl = new URL(xtrImage.getThumbUrl());
		} catch (Exception e) {
			log.warning("Thumbnail url " + xtrImage.getThumbUrl() + ": " + e.getMessage());
			return;
		}
		
		if (xtrImage.getUrl() == null) {
			log.warning("Thumbnail " + xtrImage.getThumbUrl() + " does not have an original url set, so cannot follow");
			return;
		}
		
		try {
			origUrl = new URL(xtrImage.getUrl());
		} catch (Exception e) {
			log.warning("Image url " + xtrImage.getUrl() + ": " + e.getMessage());
			return;
		}
		
		webPage = null;
		try {
			webPage = this.webPageService.createFrom(origUrl);
		} catch (Exception e) {
			log.warning("Could not parse page: " + e.getMessage()); 
			return;
		}

		boolean considerForCleanup = true;
		
		// Extract best image from this page
		XtrImage bestXtrImage = this.getBestXtrImageFrom(thumbUrl, webPage);
		boolean isBetterThanCurrent = (bestXtrImage != null);
		
		if (isBetterThanCurrent) {
			if (xtrImage.getThumbMatchScore() > bestXtrImage.getThumbMatchScore()) {
				log.info("Skip candidate, the initial " + xtrImage.toString() + " has better match than " + bestXtrImage.toString());
				isBetterThanCurrent = false;
			}
			try {
				XtrImage storedXtrImage = this.xtrGalleryService.getXtrImageService().getImageDao().getByUrl(xtrImage.getUrl());
				if (storedXtrImage.getThumbMatchScore() > bestXtrImage.getThumbMatchScore()) {
					log.info("Skip candidate, the stored initial " + storedXtrImage.toString() + " has better match than " + bestXtrImage.toString());
					isBetterThanCurrent = false;
				}
			} catch (Exception e) {}
		}
		if (isBetterThanCurrent) {
			try {
				this.xtrGalleryService.getXtrImageService().saveImage(bestXtrImage);
				
				List<XtrGallery> xtrGalleriesAffected = this.xtrGalleryService.getGalleriesByImage(xtrImage);
				
				List<XtrImage> xtrImagesToAdd = new ArrayList<XtrImage>();
				xtrImagesToAdd.add(bestXtrImage);
				
				for (XtrGallery xtrGallery : xtrGalleriesAffected) {
					this.xtrGalleryService.addImagesToGallery(xtrGallery, xtrImagesToAdd);
				}
				
				//TODO: add image to galleries of the original image
				log.info("Stored better alternative " + bestXtrImage.toString());
				
				try {
					List<XtrImage> otherImages = this.xtrGalleryService.getXtrImageService().getImageDao().getByThumbUrl(bestXtrImage.getThumbUrl());
					for (XtrImage otherImage : otherImages) {
						if (!bestXtrImage.getUrl().equals(otherImage.getUrl())) {
							try {
								this.xtrGalleryService.getXtrImageService().deleteImage(otherImage);

								List<XtrImage> xtrImagesToRemove = new ArrayList<XtrImage>();
								xtrImagesToRemove.add(otherImage);
								
								for (XtrGallery xtrGallery : xtrGalleriesAffected) {
									this.xtrGalleryService.removeImagesFromGallery(xtrGallery, xtrImagesToRemove);
								}
								
								log.info("Removed obsolete " + otherImage.toString());
							} catch (Exception e) {
								log.warning("Cannot remove obsolete " + otherImage.toString() + ": " + e.getMessage());
							}
						}
					}
				} catch (Exception e) {
					log.warning("Cannot remove obsolete versions of " + bestXtrImage.toString() + ": " + e.getMessage());
				}
				
				for (XtrGallery xtrGallery : xtrGalleriesAffected) {
					this.xtrGalleryService.saveGallery(xtrGallery);
				}
				
				// so that candidatelinks resolving takes place using our new best image as base
				xtrImage = bestXtrImage;
				considerForCleanup = false;
			} catch (Exception e) {
				log.warning("Candidate " + bestXtrImage.toString() + " cannot be saved to update original url to " + xtrImage.getUrl() + ": " + e.getMessage());
			}
		} else {
			if ((bestXtrImage != null) &&
			    !bestXtrImage.getUrl().equals(xtrImage.getUrl())) {
				try {
					this.xtrGalleryService.getXtrImageService().deleteImage(bestXtrImage);
					log.info("Removed skipped candidate " + bestXtrImage.toString());
				} catch (Exception e) {
					log.warning("Cannot remove skipped candidate " + bestXtrImage.toString() + ": " + e.getMessage());
				}
			}
		}
		
		// Get candidate links from this page that might contain even an better image representation
		if (this.taskService != null) {
			URL[] candidateLinks = this.getCandidateLinks(thumbUrl, webPage, MAX_LINKS_TO_CONSIDER);
			if ((candidateLinks != null) && (candidateLinks.length > 0)) {
				for (URL candidateUrl : candidateLinks) {
					if (xtrImage.getUrl().equals(candidateUrl.toString())) {
						continue;
					}
					
					log.info("Consider " + candidateUrl.getPath().toString() + " as resolver for " + webPage.getUrl().getPath().toString());
					
					XtrImage candidateXtrImage = xtrImage.clone();
					candidateXtrImage.setUrl(candidateUrl.toString());
					if (candidateXtrImage.getThumbMatchIteration() == 2) {
						log.info("Skip adding 2nd resolve task for " + xtrImage.toString() + " as it is already iterated");
						continue;
					}
					
					try {
						this.xtrGalleryService.getXtrImageService().saveImage(candidateXtrImage);
					} catch (Exception e) {
						log.warning("Cannot store candidate " + candidateXtrImage.toString() + ": " + e.getMessage());
						continue;
					}
					
					String guessType = URLConnection.guessContentTypeFromName(xtrImage.getUrl());
					if ((guessType == null) || !guessType.startsWith("image")) {
						this.taskService.addTask(
								new XtrImageResolveTask(
									this.webPageService, 
									this.xtrGalleryService,
									null,
									candidateXtrImage));
					
						this.taskService.process(XtrImageResolveTask.class.getName());
					}
				}
			}
		} 
		
		if (considerForCleanup) {
			try {
				log.info("Cleaning up " + xtrImage.toString());
				this.xtrGalleryService.getXtrImageService().deleteImage(xtrImage);
			} catch (Exception e) {
				log.warning("Cannot delete resolved image " + xtrImage.toString() + ": " + e.getMessage());
			}
		}
	}
	
	public XtrImage getBestXtrImageFrom(URL thumbUrl, WebPage webPage) {
		// Score page images as candidates for being the original to thumb..
		log.info(webPage.getImages().size() + " images found on original page.. ");
	
		// map to store found images with assigned score
		Map<String, WebImage> candidatesByScore = new TreeMap<String, WebImage>();
		
		String tmpParts[] = thumbUrl.getFile().split("[/\\?&_]");
		if (tmpParts.length < 1) return null;
		String thumbParts[] = new String[tmpParts.length - 1];
		System.arraycopy(tmpParts, 0, thumbParts, 0, tmpParts.length - 1);
	
		String imgCandidateParts[];
		int score = 0;
		int weight = 0;
		
		for (WebImage webImage : webPage.getImages()) {
			if ((xtrImage.getThumbWidth() > 0) &&
			    (webImage.getWidth() > 0) &&
			    (xtrImage.getThumbWidth() >= webImage.getWidth())) {
				continue;
			}
			
			if (webImage.getUrl() == null) {
				continue;
			}
			
			tmpParts = webImage.getUrl().getFile().split("[/\\?&_]");
			if (tmpParts.length < 1) continue;
			imgCandidateParts = new String[tmpParts.length - 1];
			System.arraycopy(tmpParts, 0, imgCandidateParts, 0, tmpParts.length - 1);
			
			score = 0;
			weight = 0;
			for (String thumbPart : thumbParts) {
				if (thumbPart.isEmpty()) continue;
				weight++;
				for (String imgCandidatePart : imgCandidateParts) {
					if (thumbPart.contentEquals(imgCandidatePart)) {
						score+= weight;
					}
				}
			}
			if (score > 0) {
				candidatesByScore.put(String.format("%06d", score).concat("|").concat(webImage.getKey()), webImage);
			}
		}
		
		Object[] scores = candidatesByScore.keySet().toArray();
		WebImage bestWebImage = null;
		XtrImage bestXtrImage = null;
		
		if (scores.length > 0) {
			for (Object scoreIdx : scores) {
				log.info(scoreIdx + ": " +  ((WebImage)candidatesByScore.get(scoreIdx)).toString());
			}
			
			bestWebImage = (WebImage)candidatesByScore.get(scores[scores.length - 1]);
			try {
				bestXtrImage = this.xtrGalleryService.getXtrImageService().createFromOriginal(bestWebImage);
			} catch (Exception e) {
				log.warning("While creating original " + bestWebImage.getUrl().toString()  + " for thumbnail url " + xtrImage.getThumbUrl() + ": " + e.getMessage());
				return null;
			}
			
			// copy thumb props (as this was what we started from) 
			bestXtrImage.setThumbUrl(xtrImage.getThumbUrl());
			bestXtrImage.setThumbWidth(xtrImage.getThumbWidth());
			bestXtrImage.setThumbHeight(xtrImage.getThumbHeight());
			
			tmpParts = scores[scores.length - 1].toString().split("\\|");
			if (tmpParts.length > 0) {
				bestXtrImage.setThumbMatchScore(new Integer(tmpParts[0]));
				bestXtrImage.setThumbMatchIteration(xtrImage.getThumbMatchIteration() + 1);
			}
		}
		
		return bestXtrImage;
	}
	
	public URL[] getCandidateLinks(URL thumbUrl, WebPage webPage, int numLinks) {
		// Follow page links as perhaps (better) originals are found there..
		log.info(webPage.getLinks().size() + " links found on original page");
		
		// map to store found links with assigned score
		Map<String, WebLink> candidatesByScore = new TreeMap<String, WebLink>();
		
		String[] pageHostParts = webPage.getUrl().getHost().split("\\.");
		String pageDomain = null;
		if (pageHostParts.length > 2) {
			String[] domainParts = new String[2];
			System.arraycopy(pageHostParts, pageHostParts.length-2, domainParts, 0, 2);
			pageDomain = domainParts[0].concat(".").concat(domainParts[1]);
		} else {
			pageDomain = webPage.getUrl().getHost();
		}
		
		String pageBasePath = webPage.getUrl().getPath();
		String linkBasePath;
		int idx = pageBasePath.lastIndexOf('/');
		String pageBaseParentPath = (idx >= 0) ? pageBasePath.substring(0, idx) : pageBasePath;
		String linkBaseParentPath = null;
		
		String tmpParts[] = thumbUrl.getFile().split("[/\\?&_]");
		if (tmpParts.length < 1) return null;
		String thumbParts[] = new String[tmpParts.length - 1];
		System.arraycopy(tmpParts, 0, thumbParts, 0, tmpParts.length - 1);
		
		String linkCandidateParts[];
		int score = 0;
		int weight = 0;
		
		for (WebLink webLink : webPage.getLinks()) {
			if (webLink.getUrl() == null) {
				continue;
			}
			if (!webLink.getUrl().getHost().endsWith(pageDomain)) continue;
			
			linkBasePath = webLink.getUrl().getPath();
			idx = linkBasePath.lastIndexOf('/');
			linkBaseParentPath = (idx >= 0) ? linkBasePath.substring(0, idx) : linkBasePath;
			
			if (linkBaseParentPath.length() < pageBaseParentPath.length()) continue;
			if (!linkBaseParentPath.startsWith(pageBaseParentPath)) continue;
			
			if (webPage.getUrl().getFile().equals(webLink.getUrl().getFile())) continue;
			
			tmpParts = webLink.getUrl().getFile().split("[/\\?&_]");
			if (tmpParts.length < 1) continue;
			linkCandidateParts = new String[tmpParts.length - 1];
			System.arraycopy(tmpParts, 0, linkCandidateParts, 0, tmpParts.length - 1);
			
			score = 0;
			weight = 0;
			for (String thumbPart : thumbParts) {
				if (thumbPart.isEmpty()) continue;
				weight++;
				for (String linkCandidatePart : linkCandidateParts) {
					if (thumbPart.contentEquals(linkCandidatePart)) {
						score+= weight;
					}
				}
			}
			candidatesByScore.put(String.format("%06d", score).concat("|").concat(webLink.getKey()), webLink);			
		}
		
		// map to store found links with assigned score
		Map<String, URL> uniqueCandidates = new HashMap<String, URL>();
		
		Object[] scores = candidatesByScore.keySet().toArray();
		
		if (scores.length > 0) {
			for (int i = scores.length - 1; i >= 0; i--) {
				if (uniqueCandidates.keySet().toArray().length < numLinks) {
					uniqueCandidates.put(((WebLink)candidatesByScore.get(scores[i])).getUrl().toString(), ((WebLink)candidatesByScore.get(scores[i])).getUrl());
				}
			}
		}
		
		return uniqueCandidates.values().toArray(new URL[0]);
	}
}
