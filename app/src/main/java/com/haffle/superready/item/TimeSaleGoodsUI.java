package com.haffle.superready.item;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haffle.superready.R;

public class TimeSaleGoodsUI {
	
	protected LinearLayout item;
	
	protected LinearLayout layout;
	protected RelativeLayout highLight;
	protected TextView prePrice;
	protected TextView curPrice;
	protected TextView name;
	protected TextView description;
	protected TextView restTime;
	protected TextView priceExplanation;
	protected ImageView image;
	protected ImageView favorite;
	protected ImageView discountRate;

	public TimeSaleGoodsUI(LayoutInflater inflater) {

		item = (LinearLayout)inflater.inflate(R.layout.item_timesale_goods, null);

		layout = (LinearLayout)item.findViewById(R.id.item_timeSaleGoods_layout);
		highLight = (RelativeLayout)item.findViewById(R.id.item_timeSaleGoods_highLight);
		prePrice = (TextView)item.findViewById(R.id.item_timeSaleGoods_prePrice);
		curPrice = (TextView)item.findViewById(R.id.item_timeSaleGoods_curPrice);
		name = (TextView)item.findViewById(R.id.item_timeSaleGoods_name);
		description = (TextView)item.findViewById(R.id.item_timeSaleGoods_description);
		favorite = (ImageView)item.findViewById(R.id.item_timeSaleGoods_favorite);
		image = (ImageView)item.findViewById(R.id.item_timeSaleGoods_image);
		restTime = (TextView)item.findViewById(R.id.item_timeSaleGoods_restTime);
		priceExplanation = (TextView)item.findViewById(R.id.item_timesaleGoods_priceExplanation);
		discountRate = (ImageView)item.findViewById(R.id.item_timeSaleGoods_discountRate);
	}

	public TextView getPriceExplanation() {
		return priceExplanation;
	}

	public void setPriceExplanation(TextView superReadyPrice) {
		this.priceExplanation = superReadyPrice;
	}

	public TextView getRestTime() {
		return restTime;
	}

	public void setRestTime(TextView restTime) {
		this.restTime = restTime;
	}

	public LinearLayout getItem() {
		return item;
	}

	public void setItem(LinearLayout item) {
		this.item = item;
	}

	public LinearLayout getLayout() {
		return layout;
	}

	public void setLayout(LinearLayout layout) {
		this.layout = layout;
	}

	public RelativeLayout getHighLight() {
		return highLight;
	}

	public void setHighLight(RelativeLayout highLight) {
		this.highLight = highLight;
	}

	public TextView getPrePrice() {
		return prePrice;
	}

	public void setPrePrice(TextView prePrice) {
		this.prePrice = prePrice;
	}

	public TextView getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(TextView curPrice) {
		this.curPrice = curPrice;
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

	public ImageView getFavorite() {
		return favorite;
	}

	public void setFavorite(ImageView favorite) {
		this.favorite = favorite;
	}

	public ImageView getImage() {
		return image;
	}

	public void setImage(ImageView image) {
		this.image = image;
	}

	public ImageView getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(ImageView discountRate) {
		this.discountRate = discountRate;
	}
}
