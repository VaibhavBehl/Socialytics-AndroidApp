package com.vbehl.connections.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ShowTileAdapter extends BaseAdapter {
   private String [] test={"Hi","I","Aman","great","abe","chutiye"};
   private Context context;
   
   public ShowTileAdapter(Context c){
	   context=c;
   }
	public int getCount() {
		return test.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Button button;
		if(arg1==null){
			button= new Button(context);
			button.setLayoutParams(new GridView.LayoutParams(100, 55));
			button.setPadding(8, 8, 8, 8);
		}
		else {
			button=(Button)arg1;
		}
		button.setText(test[arg0]);
		button.setId(arg0);
		return button;
		
	}

}
