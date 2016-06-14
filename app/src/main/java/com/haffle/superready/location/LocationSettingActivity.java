package com.haffle.superready.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.util.List;
import java.util.Locale;

public class LocationSettingActivity extends FragmentActivity implements LocationListener {

	ActivityManager activityManager = ActivityManager.getInstance();

	LocationManager lm = null;
	Location location;
	double latitude;
	double longitude;
	TextView textView_location;
	RelativeLayout layout_complete;
	RelativeLayout layout_back;
	int count = 0;
	GoogleMap gMap;

	public static KinsightSession kinsightSession;

	// 최소 GPS 정보 업데이트 거리 10미터 
	private static final long MIN_DISTANCE = 10; 
	// 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
	private static final long MIN_TIME = 60000; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_location_setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_location_setting);

		if (InfoManager.flagKakao) {
			kinsightSession = new KinsightSession(getApplicationContext());
			kinsightSession.open();
		}

		activityManager.addActivity(this);

		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		setView();
		setMap();
		setClickListener();
	}

	public void onResume() {
		super.onResume();

		if (InfoManager.flagKakao) {
			kinsightSession = new KinsightSession(getApplicationContext());
			kinsightSession.open();
			kinsightSession.tagScreen("위치변경");
		}
	}

	public void onPause() {
		if (InfoManager.flagKakao) {
			kinsightSession.close();
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.deleteTopActivity();
	}

	void setView() {
		textView_location = (TextView)findViewById(R.id.titlebarLocation_location);
		gMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.locationSetting_map)).getMap();
		layout_complete = (RelativeLayout)findViewById(R.id.titlebarLocation_complete);
		//		completeButton.setEnabled(false);
		layout_back = (RelativeLayout)findViewById(R.id.titlebarLocation_back);
		textView_location = (TextView)findViewById(R.id.titlebarLocation_location);
	}

	void setClickListener() {
		layout_complete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LatLng latLng = gMap.getCameraPosition().target;

				SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor= mPref.edit();
				editor.putString(InfoManager.SHARED_PREFERENCE_LATITUDE, Double.toString(latLng.latitude));
				editor.putString(InfoManager.SHARED_PREFERENCE_LONGITUDE, Double.toString(latLng.longitude));
				editor.commit();

				InfoManager.latitude = Double.toString(latLng.latitude);
				InfoManager.longitude = Double.toString(latLng.longitude);
				
				InfoManager.SetData(getApplicationContext());
				
				KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
				kakaoAnalytics.changeLocation(kinsightSession, getApplicationContext(),
						InfoManager.latitude, InfoManager.longitude,
						Double.toString(latLng.latitude), Double.toString(latLng.longitude));

				setResult(RESULT_OK);
				finish();
			}
		});

		layout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition arg0) {

				double latitude = arg0.target.latitude;
				double longitude = arg0.target.longitude;

				/*	if(count > 0 ) {
					completeButton.setEnabled(true);
					completeButton.setTextColor(Color.parseColor("#0054FF"));
				}*/

				count ++;

				String location = getAddress(latitude, longitude);

				if(location == null) {
					textView_location.setText("위치를 찾지 못했습니다");
				} else {
					textView_location.setText(location);
				}
			}
		});
	}

	void getLocation() {
		boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNETWORK = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if(isNETWORK) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  MIN_DISTANCE, MIN_TIME , this);

			if(lm != null) {
				location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
			}
		}
		else if(isGPS) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,  MIN_DISTANCE, MIN_TIME , this);

			if(lm != null) {
				location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
			}
		}

		LatLng startingPoint = new LatLng(latitude, longitude);
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	// 위도 경도를 구, 동으로 바꿔주는 function
	public String getAddress(double lat, double lng){
		String address = null;

		//위치정보를 활용하기 위한 구글 API 객체
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		//주소 목록을 담기 위한 HashMap
		List<Address> list = null;

		try{
			list = geocoder.getFromLocation(lat, lng, 1);
		} catch(Exception e){
			e.printStackTrace();
		}

		if(list.size() > 0) {	// 주소값이 존재하면

			Address addr = list.get(0);
			if(addr.getLocality() == null) {
				return null;
			} else if(addr.getThoroughfare() == null) {
				address = addr.getLocality(); 
			} else {
				address = addr.getLocality() + " "
						+ addr.getThoroughfare();	
			}
		}

		return address;
	}

	// google map의 위치 지정
	void setMap() {

		LatLng startingPoint = new LatLng(Double.parseDouble(InfoManager.latitude), Double.parseDouble(InfoManager.longitude));
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 16));
		gMap.setMyLocationEnabled(true);
	}
}