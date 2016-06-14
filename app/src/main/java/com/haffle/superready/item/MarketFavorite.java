package com.haffle.superready.item;

public class MarketFavorite extends Market {
	private static final long serialVersionUID = 3621166050899450259L;
	
	protected int dbId;
	
	public MarketFavorite() {}
	
	public MarketFavorite(int dbId, String id, String name, String phone, String latitude, String longitude,
			String description, String panorama, String businessHours, String logo) {
		this.dbId = dbId;
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.panorama = panorama;
		this.businessHours = businessHours;
		this.logo = logo;
	}
	
	public int getDbId() {
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
}
