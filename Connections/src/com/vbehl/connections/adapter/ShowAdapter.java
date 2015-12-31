package com.vbehl.connections.adapter;

import com.vbehl.connections.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

public class ShowAdapter extends BaseAdapter {
	public static int[] images={R.drawable.safe_image,R.drawable.face,R.drawable.safe_image,
		R.drawable.tw,R.drawable.safe_image,R.drawable.safe_image
	};
	private Long showId;
	private Context context;

	public ShowAdapter(Context context, Long showId) {
		this.context = context;
		this.showId = showId;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageButton iv;
		if (convertView != null) {
			iv = (ImageButton) convertView;
		} else {
			iv = new ImageButton(context);
			iv.setLayoutParams(new GridView.LayoutParams(210, 210));
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setPadding(2, 2, 2, 2);
		}
            iv.setImageResource(images[position]);
		// TODO Auto-generated method stub
		return iv;
	}

}