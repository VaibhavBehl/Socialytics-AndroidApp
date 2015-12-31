package com.vbehl.connections.fbmodels;

public enum FeedInteractionType {
	PICTURE_TAG_BY("You were tagged in this pic by FRIEND. "),
	PICTURE_TAG_ALONG_WITH("You were tagged in this pic along with FRIEND. "),
	PICTURE_TAG_IS_USER_LIKES("You liked a pic you were tagged in along with FRIEND. "),
	PICTURE_FRIEND_LIKES("FRIEND likes your pic. "),
	PICTURE_FRIEND_TAGGED("You tagged FRIEND in your pic. "),
	PICTURE_COMMENT_TAGGED_FRIEND("You tagged FRIEND in a comment on this pic. "),
	PICTURE_FRIEND_LIKES_COMMENT("FRIEND likes a comment on this pic. "),
	PICTURE_FRIEND_COMMENTED_HERE("FRIEND wrote a comment on this pic. "),
	PICTURE_YOU_LIKE_FRIENDS_COMMENT("You likes FRIEND's comment on this pic. "),
	PICTURE_FRIEND_COMMENTED_TAGGED_YOU("FRIEND tagged you in a comment on this pic. "),
	
	VIDEO_TAG_BY("You were tagged in this video by FRIEND. "), 
	VIDEO_TAG_ALONG_WITH("You were tagged in this video along with FRIEND. "),
	VIDEO_UPLOADED_TAG_FRIEND("You tagged FRIEND in your uploaded video. "),
	VIDEO_UPLOADED_FRIEND_WHO_LIKE("FRIEND has liked your uploaded video. "), 
	VIDEO_TAG_COMMENTS("FRIEND has commented on a video you were tagged in. "),
	VIDEO_COMMENT_TAGGED_FRIEND("You tagged FRIEND in this video's comment. "),
	VIDEO_FRIEND_LIKES_COMMENT("FRIEND likes a comment you made on this video. "),
	VIDEO_FRIEND_COMMENTED_HERE("FRIEND wrote a comment on this video. "),
	VIDEO_YOU_LIKE_FRIENDS_COMMENT("You likes FRIEND's comment on this video. "),
	VIDEO_FRIEND_COMMENTED_TAGGED_YOU("FRIEND tagged you in a comment on this video. "),
	
	STATUS_TAG_BY("You were tagged in this status by FRIEND. "),
	STATUS_COMMENT_TAGGED_FRIEND("You tagged FRIEND in a comment on this status. "),
	STATUS_FRIEND_LIKES("FRIEND likes your status. "),
	STATUS_FRIEND_TAGGED("You tagged FRIEND in your status. "),
	STATUS_FRIEND_LIKES_COMMENT("FRIEND likes a comment on this status. "),
	STATUS_FRIEND_COMMENTED_HERE("FRIEND wrote a comment on this status. "),
	STATUS_YOU_LIKE_FRIENDS_COMMENT("You likes FRIEND's comment on this status. "),
	STATUS_FRIEND_COMMENTED_TAGGED_YOU("FRIEND tagged you in a comment on this status. ");
	
	private FeedInteractionType(String message) {
		this.message = message;
	}
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
