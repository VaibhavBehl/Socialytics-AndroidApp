package com.vbehl.connections.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.vbehl.connections.R;
import com.vbehl.connections.adapter.FeedListAdapter;
import com.vbehl.connections.fbmodels.Feed;

public class FeedListDemoActivity extends ListActivity{

	//private static final String LOG_TAG = "ShiftApp";
	//private FbConnectionsApplication app;
	//static ProgressDialog pd;
	//private LoadMoreListItem footerView;
	private FeedListAdapter adapter;
	FeedListDemoActivity ref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_list_demo);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//app = (FbConnectionsApplication)getApplication();
		ref = this;
		//footerView = (LoadMoreListItem) getLayoutInflater().inflate(R.layout.load_more, null);
		//getListView().addFooterView(footerView);
		
		Intent intent = getIntent();
		String fromUserIdName = intent.getStringExtra("fromUserIdName");
		setTitle("Interactions with " + fromUserIdName);
		int length = intent.getIntExtra("length", 0);
		Bundle ext = intent.getExtras();
		if(ext != null && ext.size()>0){
			ArrayList<Feed> feeds = new ArrayList<Feed>();
			for(int i=0;i<length;i++){
				feeds.add((Feed) ext.getSerializable(""+i));
			}
			adapter = new FeedListAdapter(ref, feeds);
			//footerView.showFooterText();
			setListAdapter(adapter);
			//getListView().setSelection(1);
		} /*else {
			ArrayList<Feed> feeds = null;
			feeds = new ArrayList<Feed>();
			FeedType feedType = FeedType.PICTURE;
			
			Feed feed1 = new Feed("001");
			feed1.setFeedType(feedType);
			feed1.setFeedUrl("https://m.ak.fbcdn.net/sphotos-e.ak/hphotos-ak-frc3/v/t1.0-9/p180x540/575661_4215327498875_849572483_n.jpg?oh=65ddbeb0a2dd73ed94a0f8b8a9348a1e&oe=5515DB75&__gda__=1423724640_3a764aad33456d3a8d38c176f165896e");
			feed1.setFeedHref("https://www.facebook.com/photo.php?fbid=4215327498875&set=a.4215326138841.2176099.1155696310&type=1");
			feed1.setFeedMessage("tagged in this pic - Feed1");
			
			Feed feed2 = new Feed("002");
			feed2.setFeedType(feedType);
			feed2.setFeedUrl("https://m.ak.fbcdn.net/sphotos-g.ak/hphotos-ak-xaf1/v/t1.0-9/p180x540/602494_4215326618853_1859387153_n.jpg?oh=c8cbf7b3c9faed1814d8f1664e0eb349&oe=5515F444&__gda__=1423861377_352fd41e40857c68de964641b0f9e686");
			feed2.setFeedHref("https://www.facebook.com/photo.php?fbid=4215326618853&set=a.4215326138841.2176099.1155696310&type=1");
			feed2.setFeedMessage("tagged in this pic - Feed2");
			
			Feed feed3 = new Feed("003");
			feed3.setFeedType(feedType);
			feed3.setFeedUrl("https://scontent-a.xx.fbcdn.net/hphotos-xaf1/t31.0-8/s720x720/463332_3420775595574_349396255_o.jpg");
			feed3.setFeedHref("https://www.facebook.com/photo.php?fbid=3420775595574&set=a.1908610392389.2111726.1155696310&type=1");
			feed3.setFeedMessage("tagged in this pic - Feed3");
			
			feeds.add(feed1);
			feeds.add(feed2);
			feeds.add(feed3);
			adapter = new FeedListAdapter(ref, feeds);
			footerView.showFooterText();
			setListAdapter(adapter);
			getListView().setSelection(1);
		}*/
		
		/*((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				loadNewerTweets();
			}
		});*/
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//loadTimelineIfNeeded();
	}
	
	/*@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(v.equals(footerView)){
			footerView.showProgress();
			//loadOlderTweets();
		}
	}*/
	
	/*private void loadTimelineIfNeeded() {
		if(getListAdapter() == null){
			loadHomeTimeline();
		}
	}*/
	
	/*@SuppressWarnings("unchecked")
	private void loadHomeTimeline() {
		try{
			//pd = ProgressDialog.show(this, "", getString(R.string.loading), true);
			//footerView.showProgress();
			new AsyncTask<Void, Void, ArrayList<Feed>>() {
				@Override
				protected ArrayList<Feed> doInBackground(Void... params) {
					ArrayList<Feed> feeds = null;
					feeds = new ArrayList<Feed>();
					Feed feed1 = new Feed("001");
					Feed feed2 = new Feed("002");
					Feed feed3 = new Feed("003");
					feeds.add(feed1);
					feeds.add(feed2);
					feeds.add(feed3);
					try {
						statii = (ArrayList<twitter4j.Status>) twitter.getHomeTimeline();
					} catch (TwitterException e) {
						Log.d(LOG_TAG, "error message- " + e.getErrorMessage() + ", ExceptionCode-" + e.getExceptionCode() + ", LocalizedMessage-" + e.getLocalizedMessage() +", Message-" + e.getMessage() + ", RequestPath-" + e.getRequestPath() + ", StatusCode-" + e.getStatusCode() + ", Cause-" + e.getCause() + ", FeatureSpecificRateLimitStatus-" + e.getFeatureSpecificRateLimitStatus() + ", RateLimitStatus-" + e.getRateLimitStatus() + ", StackTrace-" + e.getStackTrace() );
					}
					return feeds;
				}
				@Override
				protected void onPostExecute(ArrayList<Feed> feeds) {
					if(feeds == null){
						Toast.makeText(getApplicationContext(), "Unable to access Twitter Data, Server Error !", Toast.LENGTH_LONG).show();
						finish();
					}
					else{
						adapter = new FeedListAdapter(ref, feeds);
						
					    //footerView.showFooterText();
					    //footerView.hideProgress();
					    
					    setListAdapter(adapter);
					    getListView().setSelection(1);
						//if(pd != null)pd.dismiss();
					}
				}
				@Override
				protected void onCancelled() {
					//if(pd != null)pd.dismiss();
				}
			}.execute();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/*private void loadNewerTweets() {
    	new AsyncTask<FeedListAdapter, Void, ArrayList<Status>>() {
			@Override
			protected ArrayList<twitter4j.Status> doInBackground(
					FeedListAdapter... params) {
				ArrayList<twitter4j.Status> newStatii = null; 
				try{
					newStatii = (ArrayList<twitter4j.Status>) twitter.getHomeTimeline(new Paging().sinceId(adapter.getFirstId()));
				} catch (TwitterException e) {
					Log.d(LOG_TAG, "error message- " + e.getErrorMessage() + ", ExceptionCode-" + e.getExceptionCode() + ", LocalizedMessage-" + e.getLocalizedMessage() +", Message-" + e.getMessage() + ", RequestPath-" + e.getRequestPath() + ", StatusCode-" + e.getStatusCode() + ", Cause-" + e.getCause() + ", FeatureSpecificRateLimitStatus-" + e.getFeatureSpecificRateLimitStatus() + ", RateLimitStatus-" + e.getRateLimitStatus() + ", StackTrace-" + e.getStackTrace() );
				}
				return newStatii;
			}
			protected void onPostExecute(java.util.ArrayList<twitter4j.Status> newStatii) {
				if(newStatii == null){
					Toast.makeText(getApplicationContext(), "Unable to access Twitter Data, Server Error !", Toast.LENGTH_LONG).show();
				}
				else{
					adapter.appendNewer(newStatii);
				}
				getListView().setSelection(1);
				//((PullToRefreshListView) getListView()).onRefreshComplete();
			}
    	}.execute(adapter);
  }*/
	
	/*private void loadOlderTweets() {
	    	new AsyncTask<FeedListAdapter, Void, ArrayList<Status>>() {
				@Override
				protected ArrayList<twitter4j.Status> doInBackground(
						FeedListAdapter... params) {
					ArrayList<twitter4j.Status> oldStatii = null; 
					try{
						oldStatii = (ArrayList<twitter4j.Status>) twitter.getHomeTimeline(new Paging().maxId(adapter.getLastId()-1));
					} catch (TwitterException e) {
						Log.d(LOG_TAG, "error message- " + e.getErrorMessage() + ", ExceptionCode-" + e.getExceptionCode() + ", LocalizedMessage-" + e.getLocalizedMessage() +", Message-" + e.getMessage() + ", RequestPath-" + e.getRequestPath() + ", StatusCode-" + e.getStatusCode() + ", Cause-" + e.getCause() + ", FeatureSpecificRateLimitStatus-" + e.getFeatureSpecificRateLimitStatus() + ", RateLimitStatus-" + e.getRateLimitStatus() + ", StackTrace-" + e.getStackTrace() );
					}
					return oldStatii;
				}
				protected void onPostExecute(java.util.ArrayList<twitter4j.Status> oldStatii) {
					if(oldStatii == null){
						Toast.makeText(getApplicationContext(), "Unable to access Twitter Data, Server Error !", Toast.LENGTH_LONG).show();
					}
					else{
						adapter.appendOlder(oldStatii);
					}
					footerView.hideProgress();
				}
	    	}.execute(adapter);
	  }*/
	
}
