package com.haffle.superready.goods_detail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haffle.superready.alarm.AlarmReceiver;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.db.AlarmDBManager;
import com.haffle.superready.db.GoodsDBManager;
import com.haffle.superready.goods_favorite.GoodsFavoriteActivity;
import com.haffle.superready.item.Alarm;
import com.haffle.superready.item.Goods;
import com.haffle.superready.item.GoodsFavorite;
import com.haffle.superready.item.Market;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.AppActionBuilder;
import com.kakao.AppActionInfoBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.kakao.kinsight.sdk.android.KinsightSession;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class GoodsDetailActivity extends FragmentActivity {

	ActivityManager activityManager = ActivityManager.getInstance();

	LinearLayout layout;
	TextView textView_name;
	TextView textView_marketName;
	TextView textView_description;
	TextView textView_endTime;
	TextView textView_price;
	TextView textView_bottomBar_price;
	RelativeLayout layout_back;
	RelativeLayout layout_favorite;
	ImageView imageView_image;
	ImageView imageView_kakao;
	ImageView imageView_favorite;
	GoodsDBManager dbManager;
	AlarmDBManager alarmDBManager;
	ArrayList<GoodsFavorite> bm;
	Map<String, String> header = new HashMap<String, String>();
	TextView textView_bottomBar_superReadyPrice;
	TextView textView_superReadyPrice;

	Goods goods;
	String marketId;
	String marketName;
	String endTime;
	Market market;

	private KakaoLink kakaoLink;
	private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
	public static KinsightSession kinsightSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_goods_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_goods_detail);

		if (InfoManager.flagKakao) {
			kinsightSession = new KinsightSession(getApplicationContext());
			kinsightSession.open();
		}

		activityManager.addActivity(this);
		header.put("X-SR-User-id", InfoManager.userToken);

		dbManager = new GoodsDBManager(getApplicationContext(), "GOODS.db", null, 1);
		alarmDBManager = new AlarmDBManager(getApplicationContext(), "ALARM.db", null, 1);

		try {
			kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
			kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}

		setView();
		setGoodsInfo();
		addWebView();
		setOnClickListener();
		
		if(goods.isTimeSale()) {
			GoodsDetailCalRestTime goodsDetailCalRestTime = new GoodsDetailCalRestTime(
					getApplicationContext(), textView_endTime, dateParsing(endTime));
		}
		
		receiverMarketData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (InfoManager.flagKakao) {
			kinsightSession = new KinsightSession(getApplicationContext());
			kinsightSession.open();
			kinsightSession.tagScreen("상품상세보기");
		}

		bm = dbManager.select();	// Favorite의 모든 market을 가져온다

		imageView_favorite.setBackgroundResource(R.drawable.btn_likered);
		goods.setFavorite(false);

		// favorite check 후 icon 변경
		for(int i=0; i<bm.size(); i++) {
			if(goods.getId().equals(bm.get(i).getId())) {
				imageView_favorite.setBackgroundResource(R.drawable.goods_card_btn_likefull);
				goods.setFavorite(true);
			}
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
		layout = (LinearLayout)findViewById(R.id.goodsDetail_layout);
		textView_name = (TextView)findViewById(R.id.goodsDetail_name);
		textView_description = (TextView)findViewById(R.id.goodsDetail_description);
		textView_marketName = (TextView)findViewById(R.id.goodsDetail_marketName);
		textView_endTime = (TextView)findViewById(R.id.goodsDetail_endTime);
		textView_price = (TextView)findViewById(R.id.goodsDetail_price);
		textView_bottomBar_price = (TextView)findViewById(R.id.goodsDetail_bottomBar_price);
		layout_back = (RelativeLayout)findViewById(R.id.titlebar_goodsDetail_back);
		layout_favorite = (RelativeLayout)findViewById(R.id.titlebar_goodsDetail_favorite);
		imageView_image = (ImageView)findViewById(R.id.goodsDetail_image);
		imageView_kakao = (ImageView)findViewById(R.id.goodsDetail_bottomBar_kakao);
		imageView_favorite = (ImageView)findViewById(R.id.goodsDetail_bottomBar_favorite);
		textView_bottomBar_superReadyPrice = (TextView)findViewById(R.id.goodsDetail_bottomBar_superReadyPrice);
		textView_superReadyPrice = (TextView)findViewById(R.id.goodsDetail_superReadyPrice);
	}

	// 상품 정보들을 ui에 반영
	void setGoodsInfo() {
		Intent intent = getIntent();

		goods = (Goods)intent.getSerializableExtra("GOODS");
		marketName = intent.getStringExtra("MARKETNAME");
		marketId = intent.getStringExtra("MARKETID");
		endTime = intent.getStringExtra("ENDTIME");

		Glide
		.with(getApplicationContext())
		.load(goods.getImage())
		.into(imageView_image);

		textView_name.setText(goods.getName());
		textView_description.setText(goods.getDescription());
		textView_marketName.setText(marketName);
		textView_price.setText(addComma(goods.getCurPrice()));
		textView_bottomBar_price.setText(addComma(goods.getCurPrice()));
		
		if(addComma(goods.getCurPrice()).equals("")) {
			textView_bottomBar_superReadyPrice.setVisibility(View.INVISIBLE);
			textView_superReadyPrice.setVisibility(View.INVISIBLE);
		}
	}

	void setOnClickListener() {
		imageView_kakao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendKakaoMessage();
			}
		});
		layout_favorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent userCart_intent = new Intent(getApplicationContext(), GoodsFavoriteActivity.class);
				startActivity(userCart_intent);
			}
		});
		layout_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		imageView_favorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(goods.isFavorite()) {
					imageView_favorite.setBackgroundResource(R.drawable.btn_likered);

					cancelAlarm(goods);

					dbManager.deleteWithGoodsId(Integer.parseInt(goods.getId()));

					if(goods.isTimeSale()) {	// timeSale 상품일 경우
						KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
						kakaoAnalytics.deleteFavoriteGoods(kinsightSession, getApplicationContext(),
								false, false, endTime, goods.getCurPrice());	
					} else {
						KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
						kakaoAnalytics.deleteFavoriteGoods(kinsightSession, getApplicationContext(),
								false, true, endTime, goods.getCurPrice());	
					}

					Toast.makeText(getApplicationContext(), "장보기 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();

					goods.setFavorite(false);
				} else {
					imageView_favorite.setBackgroundResource(R.drawable.goods_card_btn_likefull);

					setAlarm(goods, endTime);

					dbManager.insert(new GoodsFavorite(
							goods.getId(),
							goods.getName(),
							goods.getDescription(),
							marketName,
							marketId,
							goods.getCurPrice(),
							endTime,
							goods.getImage(),
							goods.isTimeSale())); 

					if(goods.isTimeSale()) {	// timeSale 상품일 경우
						KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
						kakaoAnalytics.setFavoriteGoods(kinsightSession, getApplicationContext(),
								false, false, endTime, market, goods.getCurPrice());	
					} else {
						KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
						kakaoAnalytics.setFavoriteGoods(kinsightSession, getApplicationContext(),
								false, true, endTime, market, goods.getCurPrice());	
					}

					KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();

					Toast.makeText(getApplicationContext(), "장보기 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
					goods.setFavorite(true);
				}
			}
		});
	}

	void addWebView() {
		WebView webView = new WebView(layout.getContext());

		webView.getSettings().setJavaScriptEnabled(true);      // 웹뷰에서 자바 스크립트 사용
		webView.loadUrl(InfoManager.URL_GOODSDETAIL_WEBVIEW + goods.getId(), header);          // 웹뷰에서 불러올 URL 입력
		webView.setWebViewClient(new WebViewClientClass());  	// client 연결

		layout.addView(webView);
	}

	private class WebViewClientClass extends WebViewClient { 
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			view.loadUrl(url, header);
			return true; 
		} 
	}

	public void sendKakaoMessage(){
		try{
			kakaoTalkLinkMessageBuilder.addText(goods.getName() + "\n" + addComma(goods.getCurPrice()) +
					"(" + goods.getDescription() + ")" + "\n\n" + marketName);
			kakaoTalkLinkMessageBuilder.addImage(goods.getImage(), 300, 300);
			kakaoTalkLinkMessageBuilder.addAppButton("슈퍼레디 앱 열기", new AppActionBuilder()
			.addActionInfo(AppActionInfoBuilder
					.createAndroidActionInfoBuilder()
					.setExecuteParam("marketId=" + marketId + "&goodsId=" + goods.getId())
					.build())
					.addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder()
							.setExecuteParam("marketId=" + marketId + "&goodsId=" + goods.getId())
							.build())
							.build());

			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);

			KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
			kakaoAnalytics.shareKakao(kinsightSession, getApplicationContext(),
					!goods.isTimeSale(), endTime, market, goods.getCurPrice());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	Calendar dateParsing(String string) {
		StringTokenizer str = new StringTokenizer(string, "- T : .");
		String[] date = new String[7];

		for(int i=0; str.hasMoreTokens(); i++) {
			date[i] = str.nextToken();
		}

		Calendar cal;
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) -1,
				Integer.parseInt(date[2]), Integer.parseInt(date[3]),
				Integer.parseInt(date[4]), Integer.parseInt(date[5]));	// 연, 월(-1해야함), 일, 시간, 분, 초
		cal.add(Calendar.HOUR, 9);

		return cal;
	}

	void setAlarm(Goods goods, String time) {

		alarmDBManager.insert(new Alarm(goods.getId(), goods.getName(), time));

		Calendar restTime = dateParsing(time);

		restTime.set(restTime.get(Calendar.YEAR),
				restTime.get(Calendar.MONTH),
				restTime.get(Calendar.DATE) - 1,
				restTime.get(Calendar.HOUR_OF_DAY),
				restTime.get(Calendar.MINUTE),
				restTime.get(Calendar.SECOND));

		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

		intent.putExtra("TITLE", goods.getName());
		intent.putExtra("TEXT", "특가 세일기간이 하루 남았습니다!!!");
		intent.putExtra("GOODSID", goods.getId());

		PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(goods.getId()), intent, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, restTime.getTimeInMillis(), pIntent);
	}

	void cancelAlarm(Goods goods) {
		alarmDBManager.deleteWithGoodsId(Integer.parseInt(goods.getId()));

		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

		PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(goods.getId()), intent, 0);
		alarmManager.cancel(pIntent);
	}

	String addComma(String price) {
		DecimalFormat df = new DecimalFormat("###,###,###");
		
		if(price.equals("0")) {
			return "";
		}

		String parsingPrice;
		try {
			parsingPrice = df.format(Long.parseLong(price)) + "원";
		} catch(Exception e) {
			parsingPrice = "";
		}
		return parsingPrice;
	}

	void receiverMarketData() {
		Thread thread1 = new Thread(new Runnable() {
			public void run() {

				String url = InfoManager.URL_MARKET + marketId;
				String json = receiveJson(url);

				parsingJson(json);
			}
		}); 
		thread1.start();
	}

	// server에서 market 관련 json을 받아온다
	String receiveJson(String url) {
		OkHttpClient client = new OkHttpClient();
		String json = "";

		Request request = new Request.Builder()
		.addHeader("X-Auth-Token", InfoManager.userToken)
		.url(url)
		.build();

		try {
			Response response = client.newCall(request).execute();
			json = response.body().string();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	// server에서 받아온 json을 parsing
	void parsingJson(String jsonString) {
		try {
			JSONObject object = new JSONObject(jsonString);
			JSONObject insideObject = new JSONObject(object.getString("data"));

			market = new Market(
					checkNull(insideObject, "id"),
					checkNull(insideObject, "name"),
					checkNull(insideObject, "phone"),
					checkNull(insideObject, "latitude"),
					checkNull(insideObject, "longitude"),
					checkNull(insideObject, "description"),
					checkNull(insideObject, "panorama"),
					checkNull(insideObject, "businessHours"),
					checkNull(insideObject, "logo"));


		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private String checkNull(JSONObject jsonObject, String string) {
		try {
			if (jsonObject.has(string)) {
				return jsonObject.getString(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}