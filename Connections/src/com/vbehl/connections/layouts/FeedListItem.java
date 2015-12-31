package com.vbehl.connections.layouts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vbehl.connections.fbmodels.Feed;
import com.vbehl.connections.fbmodels.FeedType;
import com.vbehl.connections.tasks.LoadImageAsyncTask;
import com.vbehl.connections.tasks.LoadImageAsyncTask.LoadImageAsyncTaskResponder;
import com.vbehl.connections.R;

public class FeedListItem extends RelativeLayout implements LoadImageAsyncTaskResponder{

	private ImageView feedImage;
	private ImageView videoPlayButtonImage;
	private TextView feedStatusMsg;
	private TextView feedMsg;
	private TextView feedHref;
	private AsyncTask<URL, Void, Drawable> latestLoadTask;

	public FeedListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}*/
	
	public void setFeed(Feed feed, Map<String, Drawable> drawableMap) {
		feedImage = (ImageView)findViewById(R.id.feed_image);
		videoPlayButtonImage = ((ImageView)findViewById(R.id.video_play_button_image));
		feedStatusMsg = (TextView)findViewById(R.id.feed_status_msg);
		feedMsg = (TextView)findViewById(R.id.feed_msg);
		feedHref = (TextView)findViewById(R.id.feed_href);
		
		if(FeedType.STATUS == feed.getFeedType()) {
			feedStatusMsg.setVisibility(View.VISIBLE);
			feedImage.setVisibility(View.GONE);
			videoPlayButtonImage.setVisibility(View.GONE);
			
			feedStatusMsg.setGravity(Gravity.CENTER);
			feedStatusMsg.setBackgroundResource(R.drawable.tile);
			feedStatusMsg.setText(feed.getFeedStatusMessage());
		}
		if(FeedType.PICTURE == feed.getFeedType() || FeedType.VIDEO == feed.getFeedType()) {
			feedStatusMsg.setVisibility(View.GONE);
			feedImage.setVisibility(View.VISIBLE);
			if(FeedType.VIDEO == feed.getFeedType()) {
				videoPlayButtonImage.setVisibility(View.VISIBLE);
			}
		}
		feedMsg.setText(feed.getFeedMessage());
		feedHref.setText(feed.getFeedHref());
		// cancel old task 
	    if (null != latestLoadTask) { 
	      latestLoadTask.cancel(true);
	    }
	    if(FeedType.STATUS != feed.getFeedType()) {
	    	try {
	    		latestLoadTask = new LoadImageAsyncTask(this, drawableMap).execute(new URL(feed.getFeedUrl()));
	    	} catch (MalformedURLException e) {
	    		e.printStackTrace();
	    	}
	    }
	}

	public void imageLoading() {
		feedImage.setImageDrawable(getResources().getDrawable(R.drawable.loading));
	}

	public void imageLoadCancelled() {
		// do nothing
	}

	public void imageLoaded(Drawable drawable) {
		feedImage.setImageDrawable(drawable);
	}

}