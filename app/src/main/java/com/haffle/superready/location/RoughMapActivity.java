package com.haffle.superready.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haffle.superready.item.Market;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.kinsight.sdk.android.KinsightSession;

public class RoughMapActivity extends FragmentActivity {

	ActivityManager activityManager = ActivityManager.getInstance();

	GoogleMap gMap;
	Market market = new Market();
	RelativeLayout layout_back;
	public static KinsightSession kinsightSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_rough_map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_rough_map);

		if (InfoManager.flagKakao) {
			kinsightSession = new KinsightSession(getApplicationContext());
			kinsightSession.open();
		}

		activityManager.addActivity(this);

		setView();

		Intent intent = getIntent();
		market = (Market)intent.getSerializableExtra("MARKET");

		setMap();
		setClickListener();
	}

	public void onResume() {
		super.onResume();

		if (InfoManager.flagKakao) {
			kinsightSession.open();
			kinsightSession.tagScreen("슈퍼마켓약도");
		}
	}

	public void onPause() {
		super.onPause();

		if (InfoManager.flagKakao) {
			kinsightSession.close();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.deleteTopActivity();
	}

	void setView() {
		gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.way_to_market)).getMap();
		layout_back = (RelativeLayout)findViewById(R.id.titlebar_rough_map_back);
	}

	// google map setting
	void setMap() {
		LatLng startingPoint = new LatLng(Double.parseDouble(market.getLatitude()),
				Double.parseDouble(market.getLongitude()));
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 15));

		Bitmap markerImage =  BitmapFactory.decodeResource(getResources(), R.drawable.market_location); 
		markerImage = Bitmap.createScaledBitmap(markerImage, 100, 100, true);

		MarkerOptions optFirst = new MarkerOptions();
		optFirst.position(startingPoint); // 위도, 경도
		optFirst.title(market.getName()); // 제목 미리보기
		optFirst.icon(BitmapDescriptorFactory.fromBitmap(markerImage));
		gMap.addMarker(optFirst).showInfoWindow();
		gMap.setMyLocationEnabled(true);
	}

	void setClickListener() {
		layout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}