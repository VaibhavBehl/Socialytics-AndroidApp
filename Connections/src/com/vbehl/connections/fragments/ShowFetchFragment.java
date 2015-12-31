package com.vbehl.connections.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vbehl.connections.FbConnectionsApplication;
import com.vbehl.connections.provider.Images;
import com.vbehl.connections.ui.ImageGridActivity;
import com.vbehl.connections.R;

public class ShowFetchFragment extends Fragment {

	private ProgressDialog pd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		pd = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true);
		
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... url) {
				/*for (String u : url) {
					ServiceFactory.getUtility().fetchShow(u,
							(FbConnectionsApplication) getActivity().getApplication());
				}*/
				Images.reloadImages((FbConnectionsApplication) getActivity()
						.getApplication());
				return null;
			}

			@Override
			protected void onPostExecute(Void params) {
				pd.dismiss();
				((ImageGridActivity)getActivity()).startImageGridFragment();
			}
			@Override
			protected void onCancelled() {
				pd.dismiss();
			}
    	  }.execute("http://shiftapp.herokuapp.com/show/shows"); // TODO: need to pass in SHOW array URL's
	}

}
