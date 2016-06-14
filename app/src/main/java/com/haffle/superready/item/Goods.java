package com.haffle.superready.item;

import java.io.Serializable;

public class Goods implements Serializable{
	private static final long serialVersionUID = 8503076163036361466L;
	
	protected String id;
	protected String name;
	protected String description;
	protected String prePrice;		// 이전가격
	protected String curPrice;		// 현재가격
	protected String image;
	protected String priceExplanation;
	protected String discountRate;
	protected String labels;
	protected boolean favorite;
	protected boolean timeSale;
	protected Campaign campaign;

	public Goods() {
		favorite = false;
	}
	public boolean isTimeSale() {
		return timeSale;
	}
	public void setTimeSale(boolean timeSale) {
		this.timeSale = timeSale;
	}
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPrePrice() {
		return prePrice;
	}
	public void setPrePrice(String prePrice) {
		this.prePrice = prePrice;
	}
	public String getCurPrice() {
		return curPrice;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {	
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setCurPrice(String curPrice) {
		this.curPrice = curPrice;
	}
	public String getDiscountRate() {
		return discountRate;
	}
	public void setDiscountRate(String discountRate) {
		this.discountRate = discountRate;
	}
	public String getPriceExplanation() {
		return priceExplanation;
	}
	public void setPriceExplanation(String priceExplanation) {
		this.priceExplanation = priceExplanation;
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Goods goods = (Goods) o;

		if (id != null ? !id.equals(goods.id) : goods.id != null) return false;
		return true;

	}
}
