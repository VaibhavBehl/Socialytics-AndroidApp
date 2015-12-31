package com.vbehl.connections.tasks;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class LoadImageAsyncTask  extends AsyncTask<URL, Void, Drawable> {

	  private Map<String, Drawable> drawableMap;
	
	  public interface LoadImageAsyncTaskResponder {
	    public void imageLoading();
	    public void imageLoadCancelled();
	    public void imageLoaded(Drawable drawable);
	  }

	  private LoadImageAsyncTaskResponder responder;

	  public LoadImageAsyncTask(LoadImageAsyncTaskResponder responder, Map<String, Drawable> drawableMap) {
	    this.responder = responder;
	    this.drawableMap = drawableMap;
	  }

	  @Override
	  protected Drawable doInBackground(URL... args) {
	    try {
	    	Drawable drawable = null;
	    	if(drawableMap.get(args[0].toString()) == null) {
	    		drawable = Drawable.createFromStream(args[0].openStream(), args[0].toString());
	    	} else {
	    		drawable = drawableMap.get(args[0].toString());
	    	}
	    	return drawable;
	    } catch (IOException e) {
	      Log.e(getClass().getName(), "Could not load image.", e);
	      return null;
	    }
	  }

	  @Override
	  protected void onPreExecute() {
	    super.onPreExecute();
	    responder.imageLoading();
	  }

	  @Override
	  protected void onCancelled() {
	    super.onCancelled();
	    responder.imageLoadCancelled();
	  }

	  @Override
	  protected void onPostExecute(Drawable result) {
	    super.onPostExecute(result);
	    responder.imageLoaded(result);
	  }

	}
