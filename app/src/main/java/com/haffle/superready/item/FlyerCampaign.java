package com.haffle.superready.item;

import java.io.Serializable;
import java.util.ArrayList;

public class FlyerCampaign extends Campaign implements Serializable {
	ArrayList<Flyer> flyer = new ArrayList<Flyer>();

	public ArrayList<Flyer> getFlyer() {
		return flyer;
	}

	public void setFlyer(ArrayList<Flyer> flyer) {
		this.flyer = flyer;
	}
}
