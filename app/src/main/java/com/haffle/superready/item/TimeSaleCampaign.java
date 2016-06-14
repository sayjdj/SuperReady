package com.haffle.superready.item;

import java.util.ArrayList;

public class TimeSaleCampaign extends Campaign {
	ArrayList<Goods> goods = new ArrayList<Goods>();

	public ArrayList<Goods> getGoods() {
		return goods;
	}

	public void setGoods(ArrayList<Goods> goods) {
		this.goods = goods;
	}

}
