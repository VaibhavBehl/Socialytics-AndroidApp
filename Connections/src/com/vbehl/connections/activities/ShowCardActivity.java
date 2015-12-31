package com.vbehl.connections.activities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.views.CardUI;
import com.vbehl.connections.FbConnectionsApplication;
import com.vbehl.connections.fbmodels.Feed;
import com.vbehl.connections.fbmodels.FeedInteractionType;
import com.vbehl.connections.fbmodels.Score;
import com.vbehl.connections.fbutils.FbConnectionUtils;
import com.vbehl.connections.util.ImageFetcher;
import com.vbehl.connections.util.ImageCache.ImageCacheParams;
import com.vbehl.connections.utils.LogUtils;
import com.vbehl.connections.R;

public class ShowCardActivity extends FragmentActivity implements OnClickListener {

	private CardUI cardUI;

	private static final String LOG_TAG = "ShowCardActivity";
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "Shows";
	//private ProgressBar bar;
	private ShowCardActivity activity;
	private ProgressDialog progressDialog;
	
	private FbConnectionsApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		activity = this;
		app = (FbConnectionsApplication) getApplication();
		setContentView(R.layout.activity_main);

		cardUI = (CardUI) findViewById(R.id.cardsview);
		//bar = (ProgressBar) findViewById(R.id.progressBar1);
		//bar.setVisibility(View.GONE);
		//bar.setVisibility(View.VISIBLE);
		ImageCacheParams cacheParams = new ImageCacheParams(this,
				IMAGE_CACHE_DIR);

		cacheParams.setMemCacheSizePercent(0.15f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, 154, 127);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(this.getSupportFragmentManager(),
				cacheParams);
		progressDialog = ProgressDialog.show(this, "", "Loading data from facebook servers, this may take a few seconds depending upon your connection speed!", true);
		new FetchShowTask().execute(getApplication());

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(progressDialog != null) 
			progressDialog.dismiss();
		//if (bar != null)
			//bar.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(progressDialog != null) 
			progressDialog.dismiss();
		//bar = null;
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}*/

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	//MAIN TASK, which fetches all things to be shown in card, i'll also use this async task to fetch from facebook
	public class FetchShowTask extends AsyncTask<Object, Void, FacebookRequestError> {

		@Override
		protected FacebookRequestError doInBackground(Object... params) {
			Session fbSession = app.getSession();
			FacebookRequestError error = null;
			if(fbSession != null && fbSession.isOpened()){
				Map<String, Score> connectionMap = 
						FbConnectionUtils.getUserConnectionMap(app, fbSession, error);
				if(error == null && connectionMap != null && !connectionMap.isEmpty()) {
						//no errors found, get top 10 from connectionMap and set in app
						List<Map.Entry<String, Score>> connectionList =
								new LinkedList<Map.Entry<String, Score>>( connectionMap.entrySet() );
						Collections.sort( connectionList, new Comparator<Map.Entry<String, Score>>()
								{
									public int compare( Map.Entry<String, Score> o1, Map.Entry<String, Score> o2 ) {
										return (o2.getValue()).compareTo( o1.getValue() );
									}
								});
						app.setConnectionList(connectionList);
						app.setIdNameMap(FbConnectionUtils.createIdNameMapFromIds(connectionList, fbSession, error));
				} else if(error == null) {
					if(connectionMap == null) {
						app.setIsNullconnectionList(true);
					} else if(connectionMap.isEmpty()) {
						app.setIsEmptyconnectionList(true);
					}
				}
			} 
			//ServiceFactory.getUtility().fetchShow(ShowConstants.PROD_SHOW_URL,
				//	(FbConnectionsApplication) params[0]);
			return error;
		}

		@Override
		protected void onPostExecute(FacebookRequestError error) {
			super.onPostExecute(error);
			if(progressDialog != null)
				progressDialog.dismiss();
			//if (bar != null)
				//bar.setVisibility(View.GONE);
			if(error == null) {
				List<Map.Entry<String, Score>> connectionList = app.getConnectionList();
				Map<String, String> idNameMap = app.getIdNameMap();
				if(connectionList != null && !connectionList.isEmpty() && idNameMap != null && !idNameMap.isEmpty()) {
					loadCards(connectionList, idNameMap);
				} else if(app.isNullconnectionList()) {
					Toast.makeText(activity.getApplicationContext(), LogUtils.DEFAULT_ERROR_MSG, Toast.LENGTH_LONG).show();
					finish();
				} else if(app.isEmptyconnectionList()) {
					Toast.makeText(activity.getApplicationContext(), LogUtils.NOT_ENOUGH_DATA, Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(activity.getApplicationContext(), LogUtils.DEFAULT_ERROR_MSG, Toast.LENGTH_LONG).show();
					finish();
				}
			} else {
				String errorMsg = getResources().getString(error.getUserActionMessageId());
				if(errorMsg.isEmpty()) {
					errorMsg = LogUtils.DEFAULT_ERROR_MSG;
				}
				Toast.makeText(activity.getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
				finish();
			}
			
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if(progressDialog != null)
				progressDialog.dismiss();
			/*if (bar != null) {
				bar = null;
			}*/
		}

	}

	public void loadCards(List<Entry<String, Score>> connectionList, Map<String, String> idNameMap) {
		LogUtils.d(LOG_TAG, "loading show cards");
		int count = 0;
		for(Map.Entry<String, Score> obj : connectionList) {
			LogUtils.d("ShowCardActivity", obj.getKey() + " ==>>" + obj.getValue().getScore());
		}
		
		for(Map.Entry<String, Score> obj : connectionList) {
			if(count < FbConnectionUtils.LIST_MAX) {
				String userId = obj.getKey();
				
				int pic = 0,video = 0,status = 0;
				List<Feed> feeds = app.getFromUserIdAndFeedsListMap().get(userId);
				for(Feed f:feeds) {
					switch(f.getFeedType()) {
						case PICTURE: pic++; break;
						case VIDEO: video++; break;
						case STATUS: status++; break;
					}
				}
				String relationString = "\n\n\n";// = "Total Connections(" + (pic+video+status) + ")";
				if(pic>0)
					relationString += "Pictures(" + pic + ")\n";
				if(video>0)
					relationString += "Videos(" + video + ")\n";
				if(status>0)
					relationString += "Status(" + status + ")";
				//relationString +="\n\n";
				
				String userName = idNameMap.get(userId);
				ShowCard showCard = new ShowCard((count+1) + ". " + userName, R.drawable.url1,
						getProfileUrlFromId(userId), 
						"Connection Score : " + obj.getValue().getScore(),
						relationString,
						userId, userId);
				cardUI.addCard(showCard);
				showCard.setOnClickListener(this);
				count++;
			}
		}
		cardUI.refresh();
	}
	
	private String getProfileUrlFromId(String id) {
		return "https://graph.facebook.com/" + id + "/picture?width=150&height=150";
	}

	public class ShowCard extends Card {

		private String url;
		//private String showId;
		private String desc1;
		private String fromId;

		public ShowCard(String title, int image, String url) {
			super(title, image);
			this.url = url;
		}

		public ShowCard(String title, int image, String url, String desc, String desc1,
				String showId, String fromId) {
			super(title, desc, image);
			this.url = url;
			//this.showId = showId;
			this.desc1 = desc1;
			this.fromId = fromId;
		}

		@Override
		public View getCardContent(Context context) {
			View view = LayoutInflater.from(context).inflate(R.layout.showcard,
					null);

			((TextView) view.findViewById(R.id.title)).setText(title);
			((TextView) view.findViewById(R.id.description)).setText(desc);
			((TextView) view.findViewById(R.id.description1)).setText(desc1);
			//((TextView) view.findViewById(R.id.showId)).setText(showId);
			((TextView) view.findViewById(R.id.fromUserId)).setText(fromId);

			ImageView imageView = ((ImageView) view
					.findViewById(R.id.imageView1));
			mImageFetcher.loadImage(url, imageView);

			return view;
		}

	}

	public void onClick(View v) {
		String fromUserId = ((TextView)v.findViewById(R.id.fromUserId)).getText().toString();
		String fromUserName = app.getIdNameMap().get(fromUserId);
		
		if(LogUtils.loggingEnabled)
			Toast.makeText(activity.getApplicationContext(), "card clicked for - " + fromUserName, Toast.LENGTH_LONG).show();
		
		// when card is clicked
		Intent intent= new Intent(this, FeedListDemoActivity.class);
		//intent.putExtra("length", 0);
		
		List<Feed> feeds = app.getFromUserIdAndFeedsListMap().get(fromUserId);
		initAndSortFeeds(feeds, fromUserName);
		/*List<Feed> feeds = new ArrayList<Feed>();
		Feed feed1 = new Feed("001");
		Feed feed2 = new Feed("002");
		Feed feed3 = new Feed("003");
		feeds.add(feed1);
		feeds.add(feed2);
		feeds.add(feed3);*/
		intent.putExtra("fromUserIdName", fromUserName);
		if(feeds != null && feeds.size()>0){
			intent.putExtra("length", feeds.size());
			for(int i=0; i< feeds.size(); i++){
				intent.putExtra(""+ i, feeds.get(i));
			}
		}
		startActivity(intent);
	}

	private void initAndSortFeeds(List<Feed> feeds, String fromUserIdName) {
		for(Feed f:feeds) {
			String feedMessage = "";
			for(FeedInteractionType type:f.getInteractionTypes()) {
				if(feedMessage.length()>0) {
					feedMessage += "\n";
				}
				feedMessage += "\u2022 " + type.getMessage().replace("FRIEND", fromUserIdName);
			}
			Date date = f.getFeedDate();
			DateTime dateTime = date==null?null:new DateTime(date);
			DateTimeFormatter dtf = DateTimeFormat.forPattern("MMM dd, yyyy");
			String dateString = dtf.print(dateTime);
			
			f.setFeedMessage(feedMessage + "(" + dateString +")");
		}
		Collections.sort(feeds, new Comparator<Feed>() {
			public int compare(Feed lhs, Feed rhs) {
				return rhs.getFeedDate().compareTo(lhs.getFeedDate());
			}
		});
	}

}
