package com.vbehl.connections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;

import com.facebook.Session;
import com.vbehl.connections.fbmodels.FbUser;
import com.vbehl.connections.fbmodels.Feed;
import com.vbehl.connections.fbmodels.Score;

/**
 * Used to maintain Global application state across the whole application.
 * 
 * @author Vaibhav.Behl
 */
public class FbConnectionsApplication extends Application {

	public static Session session;
	private FbUser homeUser;
	private List<Map.Entry<String, Score>> connectionList;
	private boolean isNullconnectionList;
	private boolean isEmptyconnectionList;
	private Map<String, String> idNameMap;
	private Map<String, List<Feed>> fromUserIdAndFeedsListMap = new HashMap<String, List<Feed>>();

	public FbUser getHomeUser() {
		return homeUser;
	}
	
	/**
	 * set during login phase in MainFragment, and re-init on app-restart.
	 * 
	 * @param homeUser
	 */
	public void setHomeUser(FbUser homeUser) {
		this.homeUser = homeUser;
	}
	
	public List<Map.Entry<String, Score>> getConnectionList() {
		return connectionList;
	}

	public void setConnectionList(List<Map.Entry<String, Score>> connectionList) {
		this.connectionList = connectionList;
	}

	public boolean isNullconnectionList() {
		return isNullconnectionList;
	}

	public void setIsNullconnectionList(boolean isNullconnectionList) {
		this.isNullconnectionList = isNullconnectionList;
	}

	public boolean isEmptyconnectionList() {
		return isEmptyconnectionList;
	}

	public void setIsEmptyconnectionList(boolean isEmptyconnectionList) {
		this.isEmptyconnectionList = isEmptyconnectionList;
	}

	public Map<String, String> getIdNameMap() {
		return idNameMap;
	}

	public void setIdNameMap(Map<String, String> idNameMap) {
		this.idNameMap = idNameMap;
	}

	public Map<String, List<Feed>> getFromUserIdAndFeedsListMap() {
		return fromUserIdAndFeedsListMap;
	}

	public void setFromUserIdAndFeedsListMap(
			Map<String, List<Feed>> fromUserIdAndFeedsListMap) {
		this.fromUserIdAndFeedsListMap = fromUserIdAndFeedsListMap;
	}

	public Session getSession() {
		return session;
	}

	public void  setSession(Session session) {
		FbConnectionsApplication.session = session;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

}
