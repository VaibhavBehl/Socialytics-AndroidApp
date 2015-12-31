package com.vbehl.connections.fbmodels;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * MAP[from user id -> List<Feed> feeds] CARD clicked, check 'from user id', get List<Feed> from map.
 * 
 * Feed -> counter(set after sort feeds list), type(pic,vid,msg), pic id/url/href, vid id/url/href, status id/url/href
 * 
 * Equivalent to a status from twitter. SO each FROM USER ID will be associated to a LIST of these feeds which will be displayed using this array list.
 * 
 * 
 * It'll have data like image url, video url, status message string, TYPE enum(image/video/status), etc.
 * 
 * @author vbehl
 *
 */
public class Feed implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1895288172446425567L;
	
	private int index;
	
	private FeedType feedType;
	private String feedId; 
	private String feedUrl;
	private String feedHref;
	private String feedStatusMessage;
	private String feedMessage;
	private Set<FeedInteractionType> interactionTypes = new HashSet<FeedInteractionType>();
	private Date feedDate;
	
	
	public Feed() {
	}
	
	public Feed(Feed feed) {
		this.index = feed.index;
		this.feedType = feed.feedType; 
		this.feedId = feed.feedId; 
		this.feedUrl = feed.feedUrl;
		this.feedHref = feed.feedHref; 
		this.feedStatusMessage = feed.feedStatusMessage;
		this.feedMessage = feed.feedMessage;
		for(FeedInteractionType interactionType:feed.getInteractionTypes()) {
			this.interactionTypes.add(interactionType);
		}
		this.feedDate = feed.feedDate;
	}
	
	public Feed(FeedType feedType, String feedId, String feedUrl, String feedHref, Date feedDate) {
		this.feedType = feedType;
		this.feedId = feedId;
		this.feedUrl = feedUrl;
		this.feedHref = feedHref;
		this.setFeedDate(feedDate);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FeedType getFeedType() {
		return feedType;
	}

	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	/**
	 * 'id' of the json object
	 * 
	 * @return
	 */
	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public String getFeedHref() {
		return feedHref;
	}

	public void setFeedHref(String feedHref) {
		this.feedHref = feedHref;
	}

	public String getFeedStatusMessage() {
		return feedStatusMessage;
	}

	public void setFeedStatusMessage(String feedStatusMessage) {
		this.feedStatusMessage = feedStatusMessage;
	}

	public String getFeedMessage() {
		return feedMessage;
	}

	public void setFeedMessage(String feedMessage) {
		this.feedMessage = feedMessage;
	}

	public Set<FeedInteractionType> getInteractionTypes() {
		return interactionTypes;
	}

	public void setInteractionTypes(Set<FeedInteractionType> interactionTypes) {
		this.interactionTypes = interactionTypes;
	}
	
	public boolean addInteractionTypes(FeedInteractionType interactionType) {
		return this.interactionTypes.add(interactionType);
	}

	public Date getFeedDate() {
		return feedDate;
	}

	public void setFeedDate(Date feedDate) {
		this.feedDate = feedDate;
	}

}
