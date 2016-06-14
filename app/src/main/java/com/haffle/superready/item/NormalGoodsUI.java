package com.haffle.superready.item;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haffle.superready.R;

public class NormalGoodsUI {
	
	protected LinearLayout item;
	
	protected LinearLayout leftLayout;
	protected RelativeLayout leftHighLight;
	protected TextView leftPrePrice;
	protected TextView leftCurPrice;
	protected TextView leftName;
	protected TextView leftDescription;
	protected ImageView leftFavorite;
	protected ImageView leftImage;
	protected TextView leftEventDescription;
	protected ImageView leftDiscountRate;

	protected LinearLayout rightLayout;
	protected RelativeLayout rightHighLight;
	protected TextView rightPrePrice;
	protected TextView rightCurPrice;
	protected TextView rightName;
	protected TextView rightDescription;
	protected ImageView rightFavorite;
	protected ImageView rightImage;
	protected TextView rightEventDescription;
	protected ImageView rightDiscountRate;

	public NormalGoodsUI(LayoutInflater inflater) {
		item = (LinearLayout)inflater.inflate(R.layout.item_normal_goods, null);
		
		leftLayout = (LinearLayout)item.findViewById(R.id.item_normalGoods_left_layout);
		leftHighLight = (RelativeLayout)item.findViewById(R.id.item_normalGoods_left_highLight);
		leftPrePrice = (TextView)item.findViewById(R.id.item_normalGoods_left_prePrice);
		leftCurPrice = (TextView)item.findViewById(R.id.item_normalGoods_left_curPrice);
		leftName = (TextView)item.findViewById(R.id.item_normalGoods_left_name);
		leftDescription = (TextView)item.findViewById(R.id.item_normalGoods_left_description);
		leftFavorite = (ImageView)item.findViewById(R.id.item_normalGoods_left_favorite);
		leftImage = (ImageView)item.findViewById(R.id.item_normalGoods_left_image);
		leftEventDescription = (TextView)item.findViewById(R.id.item_normalGoods_left_priceExplanation);
		leftDiscountRate = (ImageView)item.findViewById(R.id.item_normalGoods_left_discountRate);

		rightLayout = (LinearLayout)item.findViewById(R.id.item_normalGoods_right_layout);
		rightHighLight = (RelativeLayout)item.findViewById(R.id.item_normalGoods_right_highLight);
		rightPrePrice = (TextView)item.findViewById(R.id.item_normalGoods_right_prePrice);
		rightCurPrice = (TextView)item.findViewById(R.id.item_normalGoods_right_curPrice);
		rightName = (TextView)item.findViewById(R.id.item_normalGoods_right_name);
		rightDescription = (TextView)item.findViewById(R.id.item_normalGoods_right_description);
		rightFavorite = (ImageView)item.findViewById(R.id.item_normalGoods_right_favorite);
		rightImage = (ImageView)item.findViewById(R.id.item_normalGoods_right_image);
		rightEventDescription = (TextView)item.findViewById(R.id.item_normalGoods_right_priceExplanation);
		rightDiscountRate = (ImageView)item.findViewById(R.id.item_normalGoods_right_discountRate);
	}

	public TextView getLeftEventDescription() {
		return leftEventDescription;
	}

	public void setLeftEventDescription(TextView leftSuperReadyPrice) {
		this.leftEventDescription = leftSuperReadyPrice;
	}

	public TextView getRightEventDescription() {
		return rightEventDescription;
	}

	public void setRightEventDescription(TextView rightEventDescription) {
		this.rightEventDescription = rightEventDescription;
	}

	public LinearLayout getItem() {
		return item;
	}

	public void setItem(LinearLayout item) {
		this.item = item;
	}

	public LinearLayout getLeftLayout() {
		return leftLayout;
	}

	public void setLeftLayout(LinearLayout leftLayout) {
		this.leftLayout = leftLayout;
	}

	public RelativeLayout getLeftHighLight() {
		return leftHighLight;
	}

	public void setLeftHighLight(RelativeLayout leftHighLight) {
		this.leftHighLight = leftHighLight;
	}

	public TextView getLeftName() {
		return leftName;
	}

	public void setLeftName(TextView leftName) {
		this.leftName = leftName;
	}

	public TextView getLeftDescription() {
		return leftDescription;
	}

	public void setLeftDescription(TextView leftDescription) {
		this.leftDescription = leftDescription;
	}

	public ImageView getLeftFavorite() {
		return leftFavorite;
	}

	public void setLeftFavorite(ImageView leftFavorite) {
		this.leftFavorite = leftFavorite;
	}

	public ImageView getLeftImage() {
		return leftImage;
	}

	public void setLeftImage(ImageView leftImage) {
		this.leftImage = leftImage;
	}

	public LinearLayout getRightLayout() {
		return rightLayout;
	}

	public void setRightLayout(LinearLayout rightLayout) {
		this.rightLayout = rightLayout;
	}

	public RelativeLayout getRightHighLight() {
		return rightHighLight;
	}

	public void setRightHighLight(RelativeLayout rightHighLight) {
		this.rightHighLight = rightHighLight;
	}

	public TextView getRightName() {
		return rightName;
	}

	public TextView getRightDescription() {
		return rightDescription;
	}

	public void setRightDescription(TextView rightDescription) {
		this.rightDescription = rightDescription;
	}

	public ImageView getRightFavorite() {
		return rightFavorite;
	}

	public void setRightFavorite(ImageView rightFavorite) {
		this.rightFavorite = rightFavorite;
	}

	public ImageView getRightImage() {
		return rightImage;
	}

	public void setRightImage(ImageView rightImage) {
		this.rightImage = rightImage;
	}

	public ImageView getRightDiscountRate() {
		return rightDiscountRate;
	}

	public void setRightDiscountRate(ImageView rightDiscountRate) {
		this.rightDiscountRate = rightDiscountRate;
	}

	public ImageView getLeftDiscountRate() {
		return leftDiscountRate;
	}

	public void setLeftDiscountRate(ImageView leftDiscountRate) {
		this.leftDiscountRate = leftDiscountRate;
	}

	public void setRightName(TextView rightName) {
		this.rightName = rightName;
	}

	public TextView getLeftPrePrice() {
		return leftPrePrice;
	}

	public void setLeftPrePrice(TextView leftPrePrice) {
		this.leftPrePrice = leftPrePrice;
	}

	public TextView getLeftCurPrice() {
		return leftCurPrice;
	}

	public void setLeftCurPrice(TextView leftCurPrice) {
		this.leftCurPrice = leftCurPrice;
	}

	public TextView getRightPrePrice() {
		return rightPrePrice;
	}

	public void setRightPrePrice(TextView rightPrePrice) {
		this.rightPrePrice = rightPrePrice;
	}

	public TextView getRightCurPrice() {
		return rightCurPrice;
	}

	public void setRightCurPrice(TextView rightCurPrice) {
		this.rightCurPrice = rightCurPrice;
	}
}