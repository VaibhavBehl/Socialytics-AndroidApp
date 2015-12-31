package com.vbehl.connections.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vbehl.connections.fbmodels.Feed;
import com.vbehl.connections.layouts.FeedListItem;
import com.vbehl.connections.R;

/**
 * data provider for an Android List, knows how to display java.util.ArrayList.(easier that dealing with BaseAdapter).
 * 
 * @author Vaibhav.Behl
 *
 */
public class FeedListAdapter extends ArrayAdapter<Feed> implements OnClickListener {

  private Context context;
  private Map<String, Drawable> drawableMap;
  
  /*private List<Feed> feeds;
  private Map<Integer, ImageView> views;
  //private Map<Integer, ProgressBar> barViews;
  private Map<String,Bitmap> oldPicts = new  HashMap<String,Bitmap>();
  
  private final String BUNDLE_URL = "url";
  private final String BUNDLE_BM = "bm";
  private final String BUNDLE_POS = "pos";
  private final String BUNDLE_ID = "id";*/

  @SuppressLint("UseSparseArrays")
  public FeedListAdapter(Context context, List<Feed> feeds) {
    super(context, android.R.layout.simple_list_item_1, feeds);
    this.context = context;
    drawableMap = new HashMap<String, Drawable>();
    /*this.feeds = feeds;
    views = new HashMap<Integer, ImageView>();
    //barViews = new HashMap<Integer, ProgressBar>();
*/  }

  /**
   * when {@link ArrayAdapter} wants to show new item out of an array list.
   * 
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    FeedListItem layoutItem;
    if (convertView == null) {
      layoutItem = (FeedListItem) View.inflate(context, R.layout.feed_list_item, null);
    } else {
      layoutItem = (FeedListItem) convertView;
    }
    layoutItem.setFeed((Feed) getItem(position), drawableMap);
    layoutItem.setOnClickListener(this);
    
    /*// infos for the current element
    Feed entryFeed = feeds.get(position);

    //set some text fields
    TextView feedMsg = (TextView) layoutItem.findViewById(R.id.feed_msg);
    feedMsg.setText(entryFeed.getFeedMessage());
    //ImageView feedImage = (ImageView) layoutItem.findViewById(R.id.feed_image);
    //feedImage.setImageResource(R.drawable.generic_image);
    //ProgressBar bar = (ProgressBar) layoutItem.findViewById(R.id.feed_image_progress);

    // get the imageView for the current object
    ImageView v = (ImageView) layoutItem.findViewById(R.id.feed_image);

    // put infos in bundle and send to the LoadImage class
    Bundle b = new Bundle();

    //url of the pict
    b.putString(BUNDLE_URL, entryFeed.getFeedUrl());

    //position in the listView
    b.putInt(BUNDLE_POS, position);

    //id of the current object
    b.putString(BUNDLE_ID, entryFeed.getFeedId());

    //put info in the map in order to display in the onPostExecute
    views.put(position, v);
    //barViews.put(position, bar);
    //bar.setEnabled(true);
    
    LogUtils.d("FLA", "inside getView, position-" + position);

    // thread
    //new LoadImage().execute(b);
*/
    
    return layoutItem;
  }
  
//asyncTackClass for loadingpictures
 /* private class LoadImage extends AsyncTask<Bundle, Void, Bundle> {

      @Override
      protected Bundle doInBackground(Bundle... b) {

          Bitmap bm =null;
          LogUtils.d("FLA", "inside doInBackground FIRST line, postion - " + b[0].getInt(BUNDLE_POS) + ", url-" + b[0].getString(BUNDLE_URL));

          //cache: for better performance, check if url alredy exists
          if(oldPicts.get(b[0].getString(BUNDLE_URL))==null){
              try {
                  InputStream in = new java.net.URL(b[0].getString(BUNDLE_URL)).openStream();
                  bm = BitmapFactory.decodeStream(in);
              } catch (Exception e) {
            	  if(e != null && e.getMessage() != null && e.getMessage().length()>0)
            		  Log.e("Error", e.getMessage());
                  e.printStackTrace();
              }
              oldPicts.put(b[0].getString(BUNDLE_URL),bm);
          }else{
              bm = oldPicts.get(b[0].getString(BUNDLE_URL));
          }

          // get info from bundle
          Bundle bundle = new Bundle();
          bundle.putParcelable(BUNDLE_BM, bm);
          bundle.putInt(BUNDLE_POS, b[0].getInt(BUNDLE_POS));
          LogUtils.d("FLA", "inside doInBackground last line, postion - " + b[0].getInt(BUNDLE_POS));
          return bundle;
      }
      
      @Override
    protected void onCancelled(Bundle result) {
    	// TODO Auto-generated method stub
    	super.onCancelled(result);
    	//ProgressBar bar = barViews.get(result.getInt(BUNDLE_POS));
        //bar.setEnabled(false);
        //bar.setVisibility(View.GONE);
        LogUtils.d("FLA", "inside onCancelled last line, postion - " + result.getInt(BUNDLE_POS));
    }

      @Override
      protected void onPostExecute(Bundle result) {
          super.onPostExecute(result);
          LogUtils.d("FLA", "inside onPostExecute First line, postion - " + result.getInt(BUNDLE_POS));
          //ProgressBar bar = barViews.get(result.getInt(BUNDLE_POS));
          //bar.setEnabled(false);
          //bar.setVisibility(View.GONE);
          //get picture saved in the map + set
          ImageView view = views.get(result.getInt(BUNDLE_POS));
          Bitmap bm = (Bitmap) result.getParcelable(BUNDLE_BM);
          if (bm != null){ //if bitmap exists...
              view.setImageBitmap(bm);
          }else{ //if not picture, display the default ressource
              //view.setImageResource(R.drawable.generic_image);
          }
          LogUtils.d("FLA", "inside onPostExecute last line, postion - " + result.getInt(BUNDLE_POS));
      }

  }*/
  
  /*public long getFirstId() {
	    Feed firstFeed = getItem(0);
	    if (firstFeed == null) {
	      return 0;
	    } else {
	      return firstFeed.getId();
	    }
  }
  
  public long getLastId() {
	    Feed lastFeed = getItem(getCount()-1);
	    if (lastFeed == null) {
	      return 0;
	    } else {
	      return lastFeed.getId();
	    }
  }*/
  
  public void appendNewer(ArrayList<Feed> feeds) {
	    setNotifyOnChange(false);
	    for (Feed status : feeds) {
	      insert(status,0);
	    }
	    notifyDataSetChanged();
  }

  public void appendOlder(ArrayList<Feed> statii) {
    setNotifyOnChange(false);
    for (Feed status : statii) {
      add(status);
    }
    notifyDataSetChanged();
  }

	public void onClick(View v) {
		TextView feedHref = (TextView)v.findViewById(R.id.feed_href);
		if(feedHref != null && feedHref.getText() != null && feedHref.getText().toString().length()>0) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setData(Uri.parse(feedHref.getText().toString()));
			getContext().startActivity(intent);
		}
	}

}