package com.haffle.superready.item;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haffle.superready.R;

public class MarketFavoriteUI {

	protected LinearLayout item;

	protected TextView name;
	protected TextView description;
	protected ImageView image;
	protected RelativeLayout deleteFavorite;

	public MarketFavoriteUI(LayoutInflater inflater) {
		item = (LinearLayout)inflater.inflate(R.layout.item_market_favorite, null);

		name = (TextView)item.findViewById(R.id.item_marketFavorite_name);
		description = (TextView)item.findViewById(R.id.item_marketFavorite_description);
		image = (ImageView)item.findViewById(R.id.item_marketFavorite_image);
		deleteFavorite = (RelativeLayout)item.findViewById(R.id.item_marketFavorite_deleteFavoriteLayout);
	}
	
	public LinearLayout getItem() {
		return item;
	}

	public void setItem(LinearLayout item) {
		this.item = item;
	}

	public TextView getName() {
		return name;
	}

	public void setName(TextView name) {
		this.name = name;
	}

	public TextView getDescription() {
		return description;
	}

	public void setDescription(TextView description) {
		this.description = description;
	}

	public ImageView getImage() {
		return image;
	}

	public void setImage(ImageView image) {
		this.image = image;
	}

	public RelativeLayout getDeleteFavorite() {
		return deleteFavorite;
	}

	public void setDeleteFavorite(RelativeLayout deleteFavorite) {
		this.deleteFavorite = deleteFavorite;
	}
}
