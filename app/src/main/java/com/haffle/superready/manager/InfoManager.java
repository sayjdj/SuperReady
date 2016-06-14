package com.haffle.superready.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haffle.superready.BuildConfig;

public class InfoManager {
	
	private static InfoManager infoManager = new InfoManager(); 
	
	public static final String SHARED_PREFERENCE_USERTOKEN = "USERTOKEN";
	public static final String SHARED_PREFERENCE_USERJOIN = "USERJOIN";
	public static final String SHARED_PREFERENCE_USERVISITCOUNT = "USERVISITCOUNT";
	public static final String SHARED_PREFERENCE_USERREGISTRATION = "USERREGISTRATION";
	public static final String SHARED_PREFERENCE_LATITUDE = "LATITUDE";
	public static final String SHARED_PREFERENCE_LONGITUDE = "LONGITUDE";
	public static final String SHARED_PREFERENCE_NOTICEID = "NOTICEID";
	public static final String SHARED_PREFERENCE_NOTICEON = "NOTICEON";
	public static final String SHARED_PREFERENCE_USERGENDER = "USERGENDER";
	public static final String SHARED_PREFERENCE_USERAGE = "USERAGE";
	public static final String SHARED_PREFERENCE_ADDRESS = "ADDRESS";
	
	// production 배포용 superready.azurewebsites.net
		// staging 내부 배포용 superready-staging.azurewebsites.net
	// develop 개발용 superready-develop.azurewebsites.net
	
	public static String URL_MARKET;
	public static String URL_SPECIFICMARKET;
	public static String URL_USERENTER;
	public static String URL_USERJOIN;
	public static String URL_USERREGISTRATION;
	public static String URL_CAMPAIGNS_FIRST;
	public static String URL_CAMPAIGNS_SECOND;
	public static String URL_GOODSDETAIL_WEBVIEW;
	public static String URL_CUSTOMER_SUPPORT;
	public static String URL_MARKET_REQUEST;
	public static String URL_GOODS_FIRST;
	public static String URL_GOODS_SECOND;
	public static String URL_FLYER_WEBVIEW;
	public static String URL_MARKET_NOTICE;
	public static String URL_COUPON_RECEIVE;
	public static String URL_COUPON_LIST;
	public static String URL_COUPON_USE;

	public static String userToken;
	public static String userVisitCount;
	public static String userGender;
	public static String userAge;
	public static String latitude;
	public static String longitude;
	public static String address;

	public static boolean flagKakao;
	public static boolean flagCrashLytics;
	public static String urlAddess;
	public static String buildType;

	public static void SetData(Context context) {

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		userToken = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_USERTOKEN, "0");
		userVisitCount = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_USERVISITCOUNT, "0");
		userGender = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_USERGENDER, "알수없음");
		userAge = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_USERAGE, "알수없음");
		latitude = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_LATITUDE, "37.565720");
		longitude = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_LONGITUDE, "127.021904");
		address = sharedPreferences.getString(InfoManager.SHARED_PREFERENCE_ADDRESS, "알수없음");

		flagKakao = BuildConfig.USE_KAKAO_ANALYTICS;
		flagCrashLytics = BuildConfig.USE_CRASHLYTICS;
		urlAddess = BuildConfig.URL_ADDRESS;
		buildType = BuildConfig.BUILDTYPE;

		URL_MARKET = "http://superready" + urlAddess + ".azurewebsites.net/v1/markets/";
		URL_SPECIFICMARKET = "http://superready" + urlAddess + ".azurewebsites.net/v1/markets/";
		URL_USERENTER = "http://superready" + urlAddess + ".azurewebsites.net/v1/users/my/enter";
		URL_USERJOIN = "http://superready" + urlAddess + ".azurewebsites.net/v1/users/my";
		URL_USERREGISTRATION = "http://superready" + urlAddess + ".azurewebsites.net/v1/users";
		URL_CAMPAIGNS_FIRST = "http://superready" + urlAddess + ".azurewebsites.net/v1/markets/";
		URL_CAMPAIGNS_SECOND = "/campaigns/now";
		URL_GOODSDETAIL_WEBVIEW = "http://superready" + urlAddess + ".azurewebsites.net/app/detail/#";
		URL_CUSTOMER_SUPPORT = "http://superready" + urlAddess + ".azurewebsites.net/app/support/#";
		URL_MARKET_REQUEST = "http://superready" + urlAddess + ".azurewebsites.net/app/request/#";
		URL_GOODS_FIRST = "http://superready" + urlAddess + ".azurewebsites.net/v1/campaigns/";
		URL_GOODS_SECOND = "/goods";
		URL_FLYER_WEBVIEW = "http://superready" + urlAddess + ".azurewebsites.net/app/image/#";
		URL_MARKET_NOTICE = "https://superready.cantto.com/notice/market/";
		URL_COUPON_RECEIVE = "https://superready.cantto.com/user/coupon/market/";
		URL_COUPON_LIST = "https://superready.cantto.com/user/coupon/all?access_token=";
		URL_COUPON_USE = "https://superready.cantto.com/user/coupon/";

 	}
	
	public static InfoManager getInfoManager() {
		return infoManager;
	}
}