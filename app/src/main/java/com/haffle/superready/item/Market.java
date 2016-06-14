package com.haffle.superready.item;

import java.io.Serializable;

public class Market implements Serializable {
    private static final long serialVersionUID = -7327879897272517489L;

    protected String id;
    protected String name;
    protected String phone;
    protected String latitude;
    protected String longitude;
    protected String description;
    protected String panorama;
    protected String businessHours;
    protected String logo;
    protected boolean favorite;
    protected boolean region;

    public Market() {
        favorite = false;
        region = false;
    }

    public Market(String id, String name, String phone, String latitude, String longitude,
                  String description, String panorama, String businessHours, String logo) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.panorama = panorama;
        this.businessHours = businessHours;
        this.logo = logo;

        favorite = false;
        region = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPanorama() {
        return panorama;
    }

    public void setPanorama(String panorama) {
        this.panorama = panorama;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isRegion() {
        return region;
    }

    public void setRegion(boolean region) {
        this.region = region;
    }
}
