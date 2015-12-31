package com.vbehl.connections.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.vbehl.connections.R;

public class ShowCard extends Card {

	public ShowCard(String title, int image) {
		super(title, image);
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.showcard,
				null);

		((TextView) view.findViewById(R.id.title)).setText(title);
		((ImageView) view.findViewById(R.id.imageView1))
				.setImageResource(image);

		return view;
	}

}
