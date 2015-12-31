package com.vbehl.connections.utils;

import android.util.Log;

public class LogUtils {
	
	public static String DEFAULT_ERROR_MSG = "Unknown Error has occoured fetching data from Facebook servers!, please try again later.";
	public static String NOT_ENOUGH_DATA = "Not enough data in your facebook account for this app to work :(";
	
	public static boolean loggingEnabled = false; 
	
	public static void d(String tag, String msg) {
		if(loggingEnabled) {
			Log.d(tag, msg);
		}
	}



}
