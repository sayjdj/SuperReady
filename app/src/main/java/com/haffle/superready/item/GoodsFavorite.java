package com.haffle.superready.item;

import java.io.Serializable;

public class GoodsFavorite extends Goods implements Serializable{
	private static final long serialVersionUID = 5917666930531067208L;
	
	protected int dbId;
	protected String marketName;
	protected String marketId;
	protected String endTime;
	
	public GoodsFavorite(int dbId, String id, String name, String description, String marketName, String marketId, String curPrice, String endTime, String image, String timeSale) {
		this.dbId = dbId;
		this.id = id;
		this.name = name;
		this.description = description;
		this.marketName = marketName;
        this.marketId = marketId;
		this.curPrice = curPrice;
		this.endTime = endTime;
		this.image = image;
		if(timeSale.equals("true")) {
			this.timeSale = true;
		} else {
			this.timeSale = false;
		}
	}
	public GoodsFavorite(String id, String name, String description, String marketName, String marketId, String curPrice, String endTime, String image, String timeSale) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.marketName = marketName;
        this.marketId = marketId;
		this.curPrice = curPrice;
		this.endTime = endTime;
		this.image = image;
		if(timeSale.equals("true")) {
			this.timeSale = true;
		} else {
			this.timeSale = false;
		}
	}
	public GoodsFavorite(String id, String name, String description, String marketName, String marketId, String curPrice, String endTime, String image, boolean timeSale) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.marketName = marketName;
        this.marketId = marketId;
		this.curPrice = curPrice;
		this.endTime = endTime;
		this.image = image;
		this.timeSale = timeSale;
	}

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getmarketName() {
		return marketName;
	}
	public void setmarketName(String marketName) {
		this.marketName = marketName;
	}
	public int getDbId() {
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
}
