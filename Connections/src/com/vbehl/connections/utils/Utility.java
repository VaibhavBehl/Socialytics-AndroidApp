package com.vbehl.connections.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class Utility {
	public static final String LOG_TAG = "FbresearchApp";
	/*private static final String NAME = "name";
	private static final String CHANNEL = "channel";
	private static final String TIME = "time";
	private static final String DATE = "date";
	private static final String DESCRIPTION = "description";
	private static final String CATEGORY = "category";
	private static final String RECURRING = "recurring";
	private static final String FBID = "fbId";
	private static final String MEDIAS = "medias";
	private static final String SHOW_ID = "showId";
	private static final String TWITTER_HASH_TAG = "twitterHashTag";
	private static final String RATING = "rating";
	private static final String WEBSITE_URL = "websiteUrl";
	private static final String FB_COVER_IMAGE_URL = "fbCoverImageUrl";
	private static final String SCHEDULE = "schedule";
	private static final String FB_TALKING_ABOUT_COUNT = "fbTalkingAboutCount";
	private static final String FB_LIKES_COUNT = "fbLikesCount";
	private static final String FB_CATEGORY = "fbCategory";
	private static final String FB_FAN_PAGE_URL = "fbFanPageUrl";
	private static final String ID = "id";
	private static final String NULL = "null";*/
	private DefaultHttpClient defaultHttpClient;

	static JSONObject jsonObject = null;

	public DefaultHttpClient getHttpClient() {
		if (defaultHttpClient == null) {
			defaultHttpClient = new DefaultHttpClient();
		}
		return defaultHttpClient;
	}

	public synchronized HttpResponse post(String body, String url) {
		HttpPost httpPost = new HttpPost(url);
		HttpResponse httpResponse = null;
		try {
			StringEntity entity = new StringEntity(body);
			entity.setContentType("application/xml");
			httpPost.setEntity(entity);
			httpResponse = getHttpClient().execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
		return httpResponse;

	}
}
