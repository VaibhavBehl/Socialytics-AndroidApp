package com.vbehl.connections.fbutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.vbehl.connections.FbConnectionsApplication;
import com.vbehl.connections.fbmodels.FbUser;
import com.vbehl.connections.fbmodels.Feed;
import com.vbehl.connections.fbmodels.FeedInteractionType;
import com.vbehl.connections.fbmodels.FeedType;
import com.vbehl.connections.fbmodels.Score;
import com.vbehl.connections.utils.LogUtils;

public class FbConnectionUtils {
	 
	public static final String LOG_TAG = "FbresearchApp";
	public static int LIST_MAX = 10;
	private static int SCORE = 1;
	private static int PHOTO_REQUEST_INDEX = 0;
	private static int ALBUM_LIST_REQUEST_INDEX = 1;
	private static int USER_STATUS_INDEX = 2;
	private static int USER_VIDEOS_INDEX = 3;
	private static int USER_VIDEOS_UPLOADED_INDEX = 4;
	
	public static Map<String, String> createIdNameMapFromIds(List<Map.Entry<String, Score>> connectionList, Session fbSession, FacebookRequestError error) {
		Map<String, String> idNameMap = null;
		List<Request> requests = new ArrayList<Request>();
		int count = 0;
		for(Map.Entry<String, Score> obj : connectionList) {
			if(count <LIST_MAX) {
				String url = "/" + obj.getKey();
				Request request = new Request(
						fbSession,
						url,
						null,
						HttpMethod.GET
						);
				requests.add(request);
				count++;
			}
		}
		List<Response> responses = Request.executeBatchAndWait(requests);
		boolean errorFound = FbConnectionUtils.flagErrors(responses, error);
		if(!errorFound) {
			idNameMap = new HashMap<String, String>();
			int testUserCount = 0;
			for(Response response:responses) {
				GraphObject graphObj = response.getGraphObject();
				JSONObject o = graphObj.getInnerJSONObject();
				try {
					String id = (String) o.get("id");
					String name = (String) o.get("name");
					idNameMap.put(id, name);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return idNameMap;
	}
	
	public static boolean flagErrors(List<Response> responseList,
			FacebookRequestError error) {
		boolean errorFound = false;
		for(Response response:responseList) {
			FacebookRequestError responseError = response.getError();
			if(responseError != null) {
				error = responseError;
				errorFound = true;
				break;
			}
		}
		return errorFound;
	}
	
	
	/**
	 * execute on background thread
	 */
	public static Map<String, Score> getUserConnectionMap(FbConnectionsApplication application, Session fbSession, FacebookRequestError error) {
		Map<String, Score> connectionMap = null;
		// SET1 : Request Batch 1
		Request photoRequest = getPhotoRequest(fbSession);//0
		Request albumListRequest = getAlbumListRequest(fbSession);//1
		Request userStatusRequest = getUserStatusRequest(fbSession);//2
		Request userVideosRequest = getUserVideosRequest(fbSession);//3
		Request userVideosUploadedRequest = getUserVideosUploadedRequest(fbSession);//4
		
		List<Response> responseList = Request.executeBatchAndWait(photoRequest, albumListRequest, userStatusRequest, userVideosRequest, userVideosUploadedRequest);
		boolean errorFound = flagErrors(responseList, error);
		if(!errorFound) {
			connectionMap = new HashMap<String, Score>(); 
			FbUser homeUser = application.getHomeUser();
			Map<String, List<Feed>> fromUserIdAndFeedsListMap = application.getFromUserIdAndFeedsListMap();
			processPhotoResponse(connectionMap, responseList.get(PHOTO_REQUEST_INDEX), homeUser, fromUserIdAndFeedsListMap);
			List<String> albums = processAlbumListResponse(connectionMap, responseList.get(ALBUM_LIST_REQUEST_INDEX), homeUser);
			processUserStatusResponse(connectionMap, responseList.get(USER_STATUS_INDEX), homeUser, fromUserIdAndFeedsListMap);
			processUserVideosResponse(connectionMap, responseList.get(USER_VIDEOS_INDEX), homeUser, fromUserIdAndFeedsListMap);
			processUserVideosUploadedResponse(connectionMap, responseList.get(USER_VIDEOS_UPLOADED_INDEX), homeUser, fromUserIdAndFeedsListMap);
			// SET 2 : Request Batch 2
			List<Request> albumPhotosRequestList = getAlbumPhotosRequest(fbSession,albums);
			if(albumPhotosRequestList != null && albumPhotosRequestList.size() > 0) {
				List<Response> responseListAlbums = Request.executeBatchAndWait(albumPhotosRequestList);
				flagErrors(responseListAlbums, error);
				processAlbumPhotoResponse(connectionMap, responseListAlbums, homeUser, fromUserIdAndFeedsListMap);
			}
		}
		return connectionMap;
	}
	
	private static Request getPhotoRequest(Session fbSession) {
		//me/photos?fields=id,created_time,from,tags.limit(50){id},comments.limit(100){from,message_tags,user_likes,likes{id}},likes.limit(100){id}
		String url = "/me/photos";
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,created_time,from,source,link,tags.limit(50){id},comments.limit(100){from,message_tags,user_likes,likes{id}},likes.limit(100){id}");
		bundle.putString("limit", "80");
		return new Request(
				fbSession,
				url,
			    bundle,
			    HttpMethod.GET
			);
	}
	
	private static Request getAlbumListRequest(Session fbSession) {
		String url = "/me/albums";
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,name,likes,comments");
		return new Request(
				fbSession,
				url,
			    bundle,
			    HttpMethod.GET
			);
	}
	
	private static Request getUserStatusRequest(Session fbSession) {
		// /me/statuses?fields=updated_time,tags.limit(50){id},likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}&limit=100
		String url = "/me/statuses";
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,message,updated_time,tags.limit(50){id},likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}");
		bundle.putString("limit", "100");
		return new Request(
				fbSession,
				url,
			    bundle,
			    HttpMethod.GET
		);
	}
	
	private static Request getUserVideosRequest(Session fbSession) {
		// /me/videos?fields=from,tags,likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}&limit=100
		String url = "/me/videos";
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,updated_time,from,tags,description,picture,source,likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}");
		bundle.putString("limit", "100");
		return new Request(
				fbSession,
				url,
			    bundle,
			    HttpMethod.GET
		);
	}
	
	private static Request getUserVideosUploadedRequest(Session fbSession) {
		// /me/videos?fields=from,tags,likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}&limit=100
		String url = "/me/videos/uploaded";
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id,updated_time,from,tags,description,picture,source,likes.limit(100){id},comments.limit(100){from,message_tags,user_likes,likes{id}}");
		bundle.putString("limit", "100");
		return new Request(
				fbSession,
				url,
			    bundle,
			    HttpMethod.GET
		);
	}
	
	private static List<Request> getAlbumPhotosRequest(Session fbSession,
			List<String> albums) {
		List<Request> requests = new ArrayList<Request>();
		for(String album:albums) {
			String url = "/" + album + "/photos";
			Bundle bundle = new Bundle();
			bundle.putString("fields", "id,created_time,from,source,link,name,likes,comments,name_tags,tags{id}");
			bundle.putString("limit", "80");
			Request request = new Request(
					fbSession,
					url,
				    bundle,
				    HttpMethod.GET
			);
			requests.add(request);
		}
		return requests;
	}

	private static void processPhotoResponse(Map<String, Score> connectionMap, Response response, FbUser homeUser, Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		GraphObject graphObj = response.getGraphObject();
        JSONObject o = graphObj.getInnerJSONObject();
        String homeUserId = homeUser.getId();
        String picId = "";
        LogUtils.d("show card activity", o.toString());
        try {
			JSONArray pics = o.getJSONArray("data");
			LogUtils.d("-----------------------------------show card activity------data P I C S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+pics.length());
			for(int i=0; i<pics.length(); i++) {
				JSONObject pic = pics.getJSONObject(i);
				picId = pic.getString("id");
				JSONObject picFrom = pic.getJSONObject("from");
				String picFromId = (String) picFrom.get("id");
				Date createdTime = getJavaDate(pic.getString("created_time"));
				
				if(!homeUserId.equals(picFromId)) { // only use those pic tagged by others, ignore 'from=home user' pics because they'll be picked in 'album'
					Feed picFeed = new Feed(FeedType.PICTURE, picId, pic.getString("source"), pic.getString("link"), createdTime);
					addOrUpdateUserScore(connectionMap, picFromId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, picFeed, FeedInteractionType.PICTURE_TAG_BY); // HIGH: this user tagged home user in his pic
					
					if(pic.has("tags")) {
						JSONObject tagsJsonObj = pic.getJSONObject("tags");
						List<String> tagUserIds = getTagIds(tagsJsonObj);
						tagUserIds.removeAll(Arrays.asList(homeUserId));
						addOrUpdateUserScore(connectionMap, tagUserIds, homeUserId, 2*SCORE, fromUserIdAndFeedsListMap, picFeed, FeedInteractionType.PICTURE_TAG_ALONG_WITH);// LOW-MED: others tagged along with home user
						//addFromUserAndFeedToMap(fromUserIdAndFeedsListMap, tagUserIds, picFeed);
					}
					if(pic.has("likes")) {
						JSONObject likes = pic.getJSONObject("likes");
						List<String> likeUserIds = getLikeIds(likes);
						if(likeUserIds.contains(homeUserId)) {
							addOrUpdateUserScore(connectionMap, picFromId, homeUserId, 4*SCORE, fromUserIdAndFeedsListMap, picFeed, FeedInteractionType.PICTURE_TAG_IS_USER_LIKES);// MED-HIGH: home user likes this pic
						}
					}
					if(pic.has("comments")) {
						JSONObject picComments = pic.getJSONObject("comments");
						initCommentsJson(picComments, connectionMap, homeUserId, false, fromUserIdAndFeedsListMap, picFeed);// comments on pic
					}
				}
			}
        } catch (JSONException e) {
        	LogUtils.d("SCA", "-------------------------------------------PIC ID >>>>>>>>>>>>>>." + picId);
			e.printStackTrace();
		}
	}
	
	private static List<String> processAlbumListResponse(
			Map<String, Score> connectionMap, Response response, FbUser homeUser) {
		GraphObject graphObj = response.getGraphObject();
        JSONObject o = graphObj.getInnerJSONObject();
        LogUtils.d("show card activity", o.toString());
        List<String> albumList = new ArrayList<String>();
        String homeUserId = homeUser.getId();
        String albumId = "";
        try {
			JSONArray albums = o.getJSONArray("data");
			LogUtils.d("-----------------------------------show card activity------data A L B U M S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+albums.length());
			for(int i=0; i<albums.length(); i++) {
				JSONObject album = albums.getJSONObject(i);
				albumId = album.getString("id");
				LogUtils.d("-----------------------------------show card activity------data A L B U M S  I D ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+albumId);
				albumList.add(albumId);
				if(album.has("likes")) {
					JSONObject albumLikes = album.getJSONObject("likes");
					List<String> albumLikeUserIds = getLikeIds(albumLikes);
					addOrUpdateUserScore(connectionMap, albumLikeUserIds, homeUserId, 4*SCORE, null, null, null);// MED: others who like this album
				}
				if(album.has("comments")) {
					JSONObject albumComments = album.getJSONObject("comments");
					initCommentsJson(albumComments, connectionMap, homeUserId, true, null, null);
				}
			}
        } catch (JSONException e) {
        	LogUtils.d("SCA", "-------------------------------------------PIC ID >>>>>>>>>>>>>>." + albumId);
			e.printStackTrace();
		}
        return albumList;
	}
	
	private static void processUserStatusResponse(
			Map<String, Score> connectionMap, Response response, FbUser homeUser, Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		GraphObject graphObj = response.getGraphObject();
        JSONObject o = graphObj.getInnerJSONObject();
        LogUtils.d("show card activity", o.toString());
        String homeUserId = homeUser.getId();
        String statusId = "";
        try {
			JSONArray statuses = o.getJSONArray("data");
			LogUtils.d("-----------------------------------show card activity------data A L B U M S's   P H O T O S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+statuses.length());
			for(int i=0; i<statuses.length() ; i++) {
				JSONObject status = statuses.getJSONObject(i);
				statusId = status.getString("id");
				Date updated_time = getJavaDate(status.getString("updated_time"));
				String homeUserName = homeUser.getName();
				if(status.has("message")) {
					String statusFeedHref = "https://www.facebook.com/" + homeUserId + "/posts/" + statusId;
					Feed statusFeed = new Feed(FeedType.STATUS, statusId, null, statusFeedHref, updated_time);
					statusFeed.setFeedStatusMessage(homeUserName + " : " + status.getString("message"));
					if(status.has("likes")) {
						JSONObject statusLikes = status.getJSONObject("likes");
						List<String> likeUserIds = getLikeIds(statusLikes);
						likeUserIds.removeAll(Arrays.asList(homeUserId));
						addOrUpdateUserScore(connectionMap, likeUserIds, homeUserId, 6*SCORE, fromUserIdAndFeedsListMap, statusFeed, FeedInteractionType.STATUS_FRIEND_LIKES);// MED-HIGH: someone likes home user's status
					}
					if(status.has("comments")) {
						JSONObject statusComments = status.getJSONObject("comments");
						initCommentsJson(statusComments, connectionMap, homeUserId, true, fromUserIdAndFeedsListMap, statusFeed);
					}
					if(status.has("tags")) {
						JSONObject tagsJsonObj = status.getJSONObject("tags");
						List<String> tagUserIds = getTagIds(tagsJsonObj);
						tagUserIds.removeAll(Arrays.asList(homeUserId));
						addOrUpdateUserScore(connectionMap, tagUserIds, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, statusFeed, FeedInteractionType.STATUS_FRIEND_TAGGED);// HIGH: someone tagged by the home user in status
					}
				}
			}
			
        } catch (JSONException e) {
        	LogUtils.d("SCA", "-------------------------------------------PIC ID >>>>>>>>>>>>>>." + statusId);
			e.printStackTrace();
		}
	}
	
	private static void processAlbumPhotoResponse(
			Map<String, Score> connectionMap, List<Response> responseListAlbums, FbUser homeUser, Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		for(Response response:responseListAlbums) {
			GraphObject graphObj = response.getGraphObject();
			if(graphObj != null) {
				JSONObject o = graphObj.getInnerJSONObject();
				LogUtils.d("show card activity", o.toString());
				String homeUserId = homeUser.getId(); 
				String photoId = "";
				try {
					JSONArray photos = o.getJSONArray("data");
					LogUtils.d("-----------------------------------show card activity------data A L B U M S's   P H O T O S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+photos.length());
					for(int i=0; i<photos.length() ; i++) {
						JSONObject photo = photos.getJSONObject(i);
						photoId = photo.getString("id");
						Date createdTime = getJavaDate(photo.getString("created_time"));
						Feed photoFeed = new Feed(FeedType.PICTURE, photoId, photo.getString("source"), photo.getString("link"), createdTime);
						
						if(photo.has("likes")) {
							JSONObject likes = photo.getJSONObject("likes");
							List<String> likeUserIds = getLikeIds(likes);
							likeUserIds.removeAll(Arrays.asList(homeUserId));
							addOrUpdateUserScore(connectionMap, likeUserIds, homeUserId, 6*SCORE, fromUserIdAndFeedsListMap, photoFeed, FeedInteractionType.PICTURE_FRIEND_LIKES);// MED-HIGH: others who like this pic
						}
						if(photo.has("comments")) {
							JSONObject picComments = photo.getJSONObject("comments");
							initCommentsJson(picComments, connectionMap, homeUserId, true, fromUserIdAndFeedsListMap, photoFeed);
						}
						if(photo.has("tags")) {
							JSONObject tagsJsonObj = photo.getJSONObject("tags");
							List<String> tagUserIds = getTagIds(tagsJsonObj);
							tagUserIds.removeAll(Arrays.asList(homeUserId));
							addOrUpdateUserScore(connectionMap, tagUserIds, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, photoFeed, FeedInteractionType.PICTURE_FRIEND_TAGGED);// HIGH: users tagged by the home user
						}
						if(photo.has("name_tags")) {
							JSONObject photoNameTags = photo.getJSONObject("name_tags");
							
							Iterator<?> keys = photoNameTags.keys();
							while( keys.hasNext() ){
								String key = (String)keys.next();
								JSONArray arr = photoNameTags.getJSONArray(key);
								for(int k=0; k<arr.length(); k++) {
									JSONObject arrObj = arr.getJSONObject(k);
									addOrUpdateUserScore(connectionMap, arrObj.getString("id"), homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, photoFeed, FeedInteractionType.PICTURE_FRIEND_TAGGED);// HIGH: users tagged by the home user
								}
							}
						}
					}
					
				} catch (JSONException e) {
					LogUtils.d("SCA", "-------------------------------------------PIC ID >>>>>>>>>>>>>>." + photoId);
					e.printStackTrace();
				}
			} else {
				LogUtils.d("----------------------------------processAlbumPhotoResponse() ... graphObject NULL->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "----------------------------------processAlbumPhotoResponse() ... graphObject NULL->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			}
		}
	}
	
	private static void processUserVideosResponse(
			Map<String, Score> connectionMap, Response response, FbUser homeUser, Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		GraphObject graphObj = response.getGraphObject();
        JSONObject o = graphObj.getInnerJSONObject();
        String homeUserId = homeUser.getId();
        String videoId = "";
        LogUtils.d("show card activity", o.toString());
        try {
			JSONArray videos = o.getJSONArray("data");
			LogUtils.d("-----------------------------------show card activity------data V I D E O S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+videos.length());
			for(int i=0; i<videos.length(); i++) {
				JSONObject video = videos.getJSONObject(i);
				videoId = video.getString("id");
				JSONObject videoFrom = video.getJSONObject("from");
				String videoFromId = (String) videoFrom.get("id");
				Date updatedTime = getJavaDate(video.getString("updated_time"));
				if(!homeUserId.equals(videoFromId)) { // only use those VIDEOS tagged by others, ignore 'from=home user' pics because they'll be picked in /videos/uploaded
					
					Feed videoFeed = new Feed(FeedType.VIDEO, videoId, video.getString("picture"), video.getString("source"), updatedTime);
					addOrUpdateUserScore(connectionMap, videoFromId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, videoFeed, FeedInteractionType.VIDEO_TAG_BY); // HIGH: this user tagged home user in his VIDEO
					
					if(video.has("tags")) {
						JSONObject tagsJsonObj = video.getJSONObject("tags");
						List<String> tagUserIds = getTagIds(tagsJsonObj);
						tagUserIds.removeAll(Arrays.asList(homeUserId));
						addOrUpdateUserScore(connectionMap, tagUserIds, homeUserId, 2*SCORE, fromUserIdAndFeedsListMap, videoFeed, FeedInteractionType.VIDEO_TAG_ALONG_WITH);// LOW-MED: others tagged along with home user
					}
					if(video.has("likes")) {
						JSONObject likes = video.getJSONObject("likes");
						List<String> likeUserIds = getLikeIds(likes);
						if(likeUserIds.contains(homeUserId)) {
							addOrUpdateUserScore(connectionMap, videoFromId, homeUserId, 4*SCORE, null, null, null);// MED-HIGH: home user likes this VIDEO
						}
					}
					if(video.has("comments")) {
						JSONObject videoComments = video.getJSONObject("comments");
						initCommentsJson(videoComments, connectionMap, homeUserId, false, fromUserIdAndFeedsListMap, videoFeed);// comments on VIDEO
					}
				}
			}
        } catch (JSONException e) {
        	LogUtils.d("SCA", "-------------------------------------------VIDEO ID >>>>>>>>>>>>>>." + videoId);
			e.printStackTrace();
		}
	}
	
	private static void processUserVideosUploadedResponse(
			Map<String, Score> connectionMap, Response response, FbUser homeUser, Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		GraphObject graphObj = response.getGraphObject();
        JSONObject o = graphObj.getInnerJSONObject();
        String homeUserId = homeUser.getId();
        String videoId = "";
        LogUtils.d("show card activity", o.toString());
        try {
			JSONArray videos = o.getJSONArray("data");
			LogUtils.d("-----------------------------------show card activity------data V I D E O S length->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+videos.length());
			for(int i=0; i<videos.length(); i++) {
				JSONObject video = videos.getJSONObject(i);
				videoId = video.getString("id");
				Date updatedTime = getJavaDate(video.getString("updated_time"));
				
				Feed videoFeed = new Feed(FeedType.VIDEO, videoId, video.getString("picture"), video.getString("source"), updatedTime);
				//addFromUserAndFeedToMap(fromUserIdAndFeedsListMap, videoFromId, videoFeed);
				
				if(video.has("tags")) {
					JSONObject tagsJsonObj = video.getJSONObject("tags");
					List<String> tagUserIds = getTagIds(tagsJsonObj);
					tagUserIds.removeAll(Arrays.asList(homeUserId));
					addOrUpdateUserScore(connectionMap, tagUserIds, homeUserId, 6*SCORE, fromUserIdAndFeedsListMap, videoFeed, FeedInteractionType.VIDEO_UPLOADED_TAG_FRIEND);// MED-HIGH: others users tagged by home user
				}
				if(video.has("likes")) {
					JSONObject likes = video.getJSONObject("likes");
					List<String> likeUserIds = getLikeIds(likes);
					likeUserIds.removeAll(Arrays.asList(homeUserId));
					addOrUpdateUserScore(connectionMap, likeUserIds, homeUserId, 6*SCORE, fromUserIdAndFeedsListMap, videoFeed, FeedInteractionType.VIDEO_UPLOADED_FRIEND_WHO_LIKE);// MED-HIGH: other user who likes this VIDEO
				}
				if(video.has("comments")) {
					JSONObject videoComments = video.getJSONObject("comments");
					initCommentsJson(videoComments, connectionMap, homeUserId, true, fromUserIdAndFeedsListMap, videoFeed);// comments on VIDEO
				}
			}
        } catch (JSONException e) {
        	LogUtils.d("SCA", "-------------------------------------------VIDEO ID >>>>>>>>>>>>>>." + videoId);
			e.printStackTrace();
		}
	}
	
	
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	
	/**
	 * returns the like id's for a Like Json obj
	 */
	private static List<String> getLikeIds(JSONObject likesJsonObj) throws JSONException {
		List<String> likeIds = new ArrayList<String>();
		JSONArray likesData = likesJsonObj.getJSONArray("data");
		for(int i=0; i<likesData.length() ; i++) {
			JSONObject like = likesData.getJSONObject(i);
			String likeId = (String) like.get("id");
			likeIds.add(likeId);
		}
		return likeIds;
	}
	
	private static List<String> getTagIds(JSONObject tagsJsonObj) throws JSONException {
		List<String> tagIds = new ArrayList<String>();
		JSONArray tagsData = tagsJsonObj.getJSONArray("data");
		for(int j=0; j<tagsData.length() ; j++) {
			JSONObject tag = tagsData.getJSONObject(j);
			if(tag.has("id")) {
				String taggedUserId = (String) tag.get("id");
				tagIds.add(taggedUserId);
			}
		}
		return tagIds;
	}
	
	/**
	 * processes the comments Json Obj
	 * @param commentFeedType 
	 * @param fromUserIdAndFeedsListMap 
	 */
	private static void initCommentsJson(JSONObject commentsJsonObj, Map<String, Score> connectionMap, String homeUserId, boolean isHomeUsersObject, 
			Map<String, List<Feed>> fromUserIdAndFeedsListMap, Feed commentFeedType) throws JSONException {
		FeedInteractionType tagInteractionType = null, likeInteractionType = null, commentedOnInteractionType = null, isUserLikeInteractionType = null, commentMessageTagInteractionType = null;
		if(commentFeedType != null) {
			switch(commentFeedType.getFeedType()) {
			case VIDEO: 
				tagInteractionType = FeedInteractionType.VIDEO_COMMENT_TAGGED_FRIEND; 
				likeInteractionType = FeedInteractionType.VIDEO_FRIEND_LIKES_COMMENT;
				commentedOnInteractionType = FeedInteractionType.VIDEO_FRIEND_COMMENTED_HERE;
				isUserLikeInteractionType = FeedInteractionType.VIDEO_YOU_LIKE_FRIENDS_COMMENT;
				commentMessageTagInteractionType = FeedInteractionType.VIDEO_FRIEND_COMMENTED_TAGGED_YOU;
				break;
			case PICTURE: 
				tagInteractionType = FeedInteractionType.PICTURE_COMMENT_TAGGED_FRIEND; 
				likeInteractionType = FeedInteractionType.PICTURE_FRIEND_LIKES_COMMENT;
				commentedOnInteractionType = FeedInteractionType.PICTURE_FRIEND_COMMENTED_HERE;
				isUserLikeInteractionType = FeedInteractionType.PICTURE_YOU_LIKE_FRIENDS_COMMENT;
				commentMessageTagInteractionType = FeedInteractionType.PICTURE_FRIEND_COMMENTED_TAGGED_YOU;
				break;
			case STATUS: 
				tagInteractionType = FeedInteractionType.STATUS_COMMENT_TAGGED_FRIEND; 
				likeInteractionType = FeedInteractionType.STATUS_FRIEND_LIKES_COMMENT;
				commentedOnInteractionType = FeedInteractionType.STATUS_FRIEND_COMMENTED_HERE;
				isUserLikeInteractionType = FeedInteractionType.STATUS_YOU_LIKE_FRIENDS_COMMENT;
				commentMessageTagInteractionType = FeedInteractionType.STATUS_FRIEND_COMMENTED_TAGGED_YOU;
				break;
			}
		}
		
		JSONArray commentsData = commentsJsonObj.getJSONArray("data");
		for(int j=0; j<commentsData.length() ; j++) {
			JSONObject comment = commentsData.getJSONObject(j);
			JSONObject commentFrom = comment.getJSONObject("from");
			String commentFromId = (String) commentFrom.get("id");
			if(homeUserId.equals(commentFromId)) { //comment by user- look for tags to others in this comment, and who all like it
				if(comment.has("message_tags")) {
					JSONArray messageTags = comment.getJSONArray("message_tags");
					for(int k=0; k<messageTags.length() ; k++) { 
						JSONObject messageTag = messageTags.getJSONObject(k);
						String messageTagUserId = (String) messageTag.get("id");
						addOrUpdateUserScore(connectionMap, messageTagUserId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, commentFeedType, tagInteractionType);// HIGH: user tags someone in his pic's comment
					}
				}
				if(comment.has("likes")) {
					JSONObject commentLikes = comment.getJSONObject("likes");
					List<String> commentLikeUserIds = getLikeIds(commentLikes);
					addOrUpdateUserScore(connectionMap, commentLikeUserIds, homeUserId, 6*SCORE, fromUserIdAndFeedsListMap, commentFeedType, likeInteractionType);// MED-HIGH: someone likes home users comment on a pic
				}
			} else { //comment by others, check for user_likes flag and if someone has tagged home user in his message
				if(isHomeUsersObject)
					addOrUpdateUserScore(connectionMap, commentFromId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, commentFeedType, commentedOnInteractionType); // HIGH: someone comments on home user's object
				Boolean isUserLikes = (Boolean)comment.get("user_likes");
				if(isUserLikes)
					addOrUpdateUserScore(connectionMap, commentFromId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, commentFeedType, isUserLikeInteractionType); // HIGH: home user likes comment by someone else
				if(comment.has("message_tags")) {
					JSONArray messageTags = comment.getJSONArray("message_tags");
					for(int k=0; k<messageTags.length() ; k++) { 
						JSONObject messageTag = messageTags.getJSONObject(k);
						String messageTagUserId = (String) messageTag.get("id");
						if(homeUserId.equals(messageTagUserId))
							addOrUpdateUserScore(connectionMap, commentFromId, homeUserId, 8*SCORE, fromUserIdAndFeedsListMap, commentFeedType, commentMessageTagInteractionType);// HIGH: someone tags home user in his pic's comment
					}
				}
			}
		}
	}
	
	private static void addOrUpdateUserScore(Map<String, Score> connectionMap,
			List<String> fromIds, String homeUserStaticId, int score, Map<String, List<Feed>> fromUserIdAndFeedsListMap, Feed picFeed, FeedInteractionType feedInteractionType) {
		for(String fromId:fromIds) {
			addOrUpdateUserScore(connectionMap, fromId, homeUserStaticId, score, fromUserIdAndFeedsListMap, picFeed, feedInteractionType);
		}
	}
	
	private static void addOrUpdateUserScore(Map<String, Score> connectionMap, String fromId, String homeUserStaticId, int score, 
			Map<String, List<Feed>> fromUserIdAndFeedsListMap, Feed feed, FeedInteractionType feedInteractionType) {
		
		if(connectionMap.containsKey(fromId)){
			Score scoreObj = connectionMap.get(fromId);
			scoreObj.setScore(scoreObj.getScore() + score);
			scoreObj.setConnectionDataPoint(scoreObj.getConnectionDataPoint() + 1);
			connectionMap.put(fromId, scoreObj);
		} else {
			Score scoreObj = new Score(score, 1);
			connectionMap.put(fromId, scoreObj);
		}
		if(homeUserStaticId.equalsIgnoreCase(fromId)) {
			//LogUtils.d("xxxxxxxxxx", Arrays.toString(Thread.currentThread().getStackTrace()));
			new Exception("Stack trace").printStackTrace();
			//return;
		}
		if(fromUserIdAndFeedsListMap != null && feed != null && feedInteractionType != null)
			addToMapFromUserAndFeed(fromUserIdAndFeedsListMap, fromId, feed, feedInteractionType);
	}
	
	/**
	 * add user 'fromId' and Feed (to feed list), to the -> Map<String, List<Feed>> fromUserIdAndFeedsListMap
	 * 
	 * @param fromUserIdAndFeedsListMap
	 * @param fromId
	 * @param feed new feed
	 * @param feedInteractionType 
	 */
	private static void addToMapFromUserAndFeed(Map<String, List<Feed>> fromUserIdAndFeedsListMap, String fromId, Feed feed, FeedInteractionType feedInteractionType) {
		Feed feedClone = new Feed(feed);
		feedClone.addInteractionTypes(feedInteractionType);
		
		List<Feed> feeds = fromUserIdAndFeedsListMap.get(fromId);
		if(feeds == null) {
			feeds = new ArrayList<Feed>();
			fromUserIdAndFeedsListMap.put(fromId, feeds);
			feeds.add(feedClone);
		} else {
			boolean found = false;
			for(Feed f: feeds) {
				if(f.getFeedId().equals(feedClone.getFeedId())) {//feed id matched, append the text.
					found=true;
					f.addInteractionTypes(feedInteractionType);
					//f.setFeedMessage(f.getFeedMessage() + feedInteractionType.getMessage());
					break;
				}
			}
			if(!found) {
				feeds.add(feedClone);
			}
		}
	}
	
	
	public static Date getJavaDate(String ISODate) {
		DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();
		return parser2.parseDateTime(ISODate).toDate();
	}
}