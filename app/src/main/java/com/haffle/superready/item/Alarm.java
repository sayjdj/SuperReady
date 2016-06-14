package com.haffle.superready.item;

public class Alarm {
	String goodsId;
	String goodsName;
	String restTime;
	
	public Alarm(String goodsId, String goodsName, String restTime) {
		this.goodsId = goodsId;
		this.goodsName = goodsName;
		this.restTime = restTime;
	}
	
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getRestTime() {
		return restTime;
	}
	public void setRestTime(String restTime) {
		this.restTime = restTime;
	}
}
