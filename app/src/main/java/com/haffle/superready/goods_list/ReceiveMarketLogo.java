package com.haffle.superready.goods_list;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haffle.superready.item.Market;

class ReceiveMarketLogo extends AsyncTask<String, Integer, Boolean>{

	Context context;
	ImageView imageView;
	TextView textView;
	Market market;
	
	ReceiveMarketLogo(Context context, ImageView imageView, TextView textView, Market market) {
		this.context = context;
		this.imageView = imageView;
		this.textView = textView;
		this.market = market;
	}
	
	@Override
	protected Boolean doInBackground(String... urls) {
		if(market.getLogo().equals("")) {
			return false;
		} else {
			return true;
		}
	}
	protected void onPostExecute(Boolean isLogo){
		if(isLogo) {
			Glide.with(context)
					.load(market.getLogo() + "@white")
					.into(imageView);

			imageView.setVisibility(View.VISIBLE);
		} else {
			textView.setText(market.getName());
			textView.setVisibility(View.VISIBLE);
		}
	}
}