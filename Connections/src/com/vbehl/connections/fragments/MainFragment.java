package com.vbehl.connections.fragments;

import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.vbehl.connections.FbConnectionsApplication;
import com.vbehl.connections.activities.ShowCardActivity;
import com.vbehl.connections.fbmodels.FbUser;
import com.vbehl.connections.fbutils.FbConnectionUtils;
import com.vbehl.connections.utils.LogUtils;
import com.vbehl.connections.R;

/**
 * Contains first login screen having (fb and twitter) buttons.
 * 
 * @author Vaibhav.Behl
 * 
 */
public class MainFragment extends Fragment implements OnClickListener {

	private static final String LOG_TAG = FbConnectionUtils.LOG_TAG;
	private FbConnectionsApplication app;
	private UiLifecycleHelper uiHelper;
	private Button continueButton;
	private Button aboutAppButton;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		// @Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (FbConnectionsApplication) getActivity().getApplication();
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.launch, container, false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("user_status", "user_photos", "user_videos"));
		authButton.setFragment(this);
		continueButton = (Button) view.findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);
		continueButton.setVisibility(View.INVISIBLE);
		
		aboutAppButton = (Button) view.findViewById(R.id.aboutAppButton);
		aboutAppButton.setOnClickListener(this);

		//Button button = (Button) view.findViewById(R.id.tw_login_button);
		//button.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
		if (session != null && session.isOpened()) {
			continueButton.setVisibility(View.VISIBLE);
		} else {
			continueButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void onClick(View v) {
		//continueButton on click, always check session before continue
		switch (v.getId()) {
			case R.id.continueButton:
				Session session = Session.getActiveSession();
				if(session != null && session.isOpened()){
					Intent intent = new Intent(getActivity(), ShowCardActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "You've logged off, login again :)", Toast.LENGTH_SHORT).show();
				}
			break;
			 
			case R.id.aboutAppButton:
				AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity())
				.setTitle("About this app!")
				.setMessage("This app analyses your facebook data and lists your top 10 friends with the highest " +
						"'Connection Score'. This score is calculated using an algorithm which analyses your interaction with your friends " +
						"using data points like- photo tags, comments, likes etc." +
						"\n\n This app further shows you a 'Collage' of recent 'photos','videos' and 'statuses' you share with that person, to relive those memories. " +
						/*
						"This score is calculated using an algorithm which analyses your interaction with your friends " +
						"using data points like- photo tags, comments, likes etc." +
						*/
						/*"This 'Connection Score' is calculated using your interaction with your friends " +
						"using data from - photo tags, comments, likes etc. " +*/
						"\n\n For accurate results with this app allow " +
						"all permissions requested. ")
						/*"All data is fetched directly from facebook's servers to your device, " +
						"no user identifiable data needs to leave your device.")*/
				/*.setMessage("This app looks at your facebook data and lists your top 10 friends with the highest " +
						"'+' and shows a 'Collage' of recent 'photos', 'videos' and 'statuses'. " +
						"This 'Connection Score' is calculated using an algorithm which analyses your interaction with your friends " +
						"using data points like- photo tags, comments, likes etc. \n\n For best experience with this app allow " +
						"all permissions requested. All data is fetched directly from facebook's servers to your device, " +
						"no user identifiable data will ever leave your device.")*/
		        		.setNegativeButton("Close",new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int id) {
		        				dialog.cancel();
		        			}
		        		}).create();
				alertDialog.show();
				TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
				textView.setTextSize(15);
				textView.setTypeface(Typeface.SANS_SERIF);
			break;
		/*case R.id.tw_login_button:
			if (!app.isAuthorized()) {
				Intent intent = new Intent(getActivity(),
						TwitterAuthActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(getActivity(),
						ImageGridActivity.class);
				startActivity(intent);
				// Toast.makeText(getApplicationContext(), "TLogIn",//,access
				// token-" + TwitterUtils.accessTokenTemp.getToken() + "!!" +
				// TwitterUtils.accessTokenTemp.getTokenSecret(),
				// Toast.LENGTH_SHORT).show();
			}
			break;*/
		}

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			LogUtils.d(LOG_TAG, "Logged in...");
			((FbConnectionsApplication) app).setSession(session);
			Request.newMeRequest(session,
					new Request.GraphUserCallback() {

						public void onCompleted(final GraphUser user,
								Response response) {
							if(user != null) {
								FbUser homeUser = new FbUser(user.getId()); 
								homeUser.setFirstName(user.getFirstName());
								homeUser.setLastName(user.getLastName());
								homeUser.setName(user.getName());
								app.setHomeUser(homeUser);
								getActivity().runOnUiThread(new Runnable() { 
									public void run() {
										if(isFirstTime()) {
											Toast.makeText(getActivity().getApplicationContext(), "Hi " + user.getFirstName() + "! press continue to move on.", Toast.LENGTH_SHORT).show();
										}
									}
								});
							} else {
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										if(isFirstTime()) {
											Toast.makeText(getActivity().getApplicationContext(), LogUtils.DEFAULT_ERROR_MSG, Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
							
							//ServiceFactory.getRegisterServiceImpl()
								//	.registerUser(user);

						}
					}).executeAsync();
			//Intent intent = new Intent(getActivity(), ShowCardActivity.class);
			//startActivity(intent);
		} else if (state.isClosed()) {
			LogUtils.d(LOG_TAG, "Logged out...");
		}
	}
	
	/***
	 * Checks that application runs first time and write flag at SharedPreferences 
	 * @return true if 1st time
	 */
	private boolean isFirstTime() {
	    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
	    boolean ranBefore = preferences.getBoolean("RanBefore", false);
	    if (!ranBefore) {
	        // first time
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putBoolean("RanBefore", true);
	        editor.commit();
	    }
	    return !ranBefore;
	}

}
