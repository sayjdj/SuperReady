package com.haffle.superready.market;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.db.MarketDBManager;
import com.haffle.superready.goods_list.GoodsListActivity;
import com.haffle.superready.item.Market;
import com.haffle.superready.item.MarketFavorite;
import com.haffle.superready.item.MarketFavoriteUI;
import com.haffle.superready.item.MarketUI;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.kinsight.sdk.android.KinsightSession;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class ReceiveMarket extends AsyncTask<String, Integer, String>{

	ArrayList<Market> regionMarket = new ArrayList<Market>();
	ArrayList<MarketUI> regionMarketUI = new ArrayList<MarketUI>();
	ArrayList<MarketFavoriteUI> favoriteMarketUI = new ArrayList<MarketFavoriteUI>();

	Context context;
	Activity activity;
	LinearLayout layout_marketFavorite;
	LinearLayout layout_marketRegion;
	RelativeLayout layout_marketFavoriteText;
	LayoutInflater inflater;
	MarketDBManager dbManager;
	public static KinsightSession kinsightSession;
	ProgressBar progressBar;

	ReceiveMarket(Context context, Activity activity, LayoutInflater inflater) {
		this.context = context;
		this.inflater = inflater;
		this.activity = activity;
		layout_marketFavorite = (LinearLayout)activity.findViewById(R.id.main_marketFavorite);
		layout_marketRegion = (LinearLayout)activity.findViewById(R.id.main_marketRegion);
		layout_marketFavoriteText = (RelativeLayout)activity.findViewById(R.id.main_marketFavoriteText);
		progressBar = (ProgressBar)activity.findViewById(R.id.main_progressBar);

		kinsightSession = new KinsightSession(context);
	}

	@Override
	protected String doInBackground(String... urls) {

		String url = InfoManager.URL_MARKET + "?lat=" + Double.parseDouble(InfoManager.latitude) +
				"&lng=" + Double.parseDouble(InfoManager.longitude);
		dbManager = new MarketDBManager(context, "MARKET.db", null, 1);

		String jsonString = receiveJson(url);
		parsingJson(jsonString);

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPostExecute(String temp){
		addItem();
		progressBar.setVisibility(View.INVISIBLE);
	}

	// 즐겨찾기 마켓과 이 지역 마켓을 추가해준다.
	void addItem() {
		layout_marketRegion.removeAllViews();
		layout_marketFavorite.removeAllViews();

		ArrayList<MarketFavorite> favoriteMarket = dbManager.select();	// Favorite의 모든 market을 가져온다

		// 즐겨찾기 마켓이 없을 경우 "나의 관심마켓" textView를 gone
		if(favoriteMarket.size() == 0) {
			layout_marketFavoriteText.setVisibility(View.GONE);
		} else {
			layout_marketFavoriteText.setVisibility(View.VISIBLE);
		}

		// favorite, region인지 check
		for(int i=0; i<favoriteMarket.size(); i++) {
			favoriteMarket.get(i).setFavorite(true);
			for(int j=0; j<regionMarket.size(); j++) {
				regionMarket.get(j).setRegion(true);
				if(favoriteMarket.get(i).getId().equals(regionMarket.get(j).getId())) {
					favoriteMarket.get(i).setRegion(true);
					regionMarket.get(j).setFavorite(true);
				}
			}
		}

		// 나의 관심마트 추가
		for(int i=0; i<favoriteMarket.size(); i++) {
			addFavoriteMarket(favoriteMarket.get(i));	// 관심 마트 추가
		}

		boolean flag_emptyRegionMarket = true;

		// 이 지역 슈퍼레디 마켓 추가
		for(int i=0; i<regionMarket.size(); i++) {

			if(!regionMarket.get(i).isFavorite()) {
				addRegionMarket(regionMarket.get(i));
				flag_emptyRegionMarket = false;
			}
		}

		// region Market이 없을 경우
		if(flag_emptyRegionMarket) {
			LinearLayout item = (LinearLayout)inflater.inflate(R.layout.item_no_market, null);

			layout_marketRegion.addView(item);
		}
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
			JSONArray array = new JSONArray(object.getString("data"));

			for(int i=0; i<array.length(); i++) {
				JSONObject insideObject = array.getJSONObject(i);

				regionMarket.add(new Market(
						checkNull(insideObject, "id"),
						checkNull(insideObject, "name"),
						checkNull(insideObject, "phone"),
						checkNull(insideObject, "latitude"),
						checkNull(insideObject, "longitude"),
						checkNull(insideObject, "description"),
						checkNull(insideObject, "panorama"),
						checkNull(insideObject, "businessHours"),
						checkNull(insideObject, "logo"))
				);
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	// scroll view에 FavoriteMarket 추가
	void addFavoriteMarket(final MarketFavorite marketFavorite) {

		MarketFavoriteUI marketFavoriteUI = new MarketFavoriteUI(inflater);

		Glide
		.with(context)
		.load(marketFavorite.getPanorama())
		.into(marketFavoriteUI.getImage());

		marketFavoriteUI.getName().setText(marketFavorite.getName());
		marketFavoriteUI.getDescription().setText(marketFavorite.getDescription());

		layout_marketFavorite.addView(marketFavoriteUI.getItem());	// scrollview에 추가

		if(marketFavorite.isRegion()) {
			setMarketClickListener(marketFavoriteUI.getItem(), marketFavorite, true, true);
		} else {
			setMarketClickListener(marketFavoriteUI.getItem(), marketFavorite, true, false);
		}

		marketFavoriteUI.getDeleteFavorite().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder gsDialog = new AlertDialog.Builder(activity);
				gsDialog.setMessage("삭제하시겠습니까?");
				gsDialog.setNegativeButton("확인",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
						kakaoAnalytics.deleteFavoriteMarket(kinsightSession, context, marketFavorite);

						dbManager.delete(marketFavorite.getDbId());

						addItem();

						Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
					}
				})
				.setPositiveButton("취소",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						return;
					}
				}).create().show();
			}
		});
	}

	// scroll view에 이 지역 마켓을 추가해준다
	void addRegionMarket(final Market regionMarket) {

		MarketUI marketUI = new MarketUI(inflater);

		Glide
		.with(context)
		.load(regionMarket.getPanorama())
		.into(marketUI.getImage());

		marketUI.getName().setText(regionMarket.getName());
		marketUI.getDescription().setText(regionMarket.getDescription());

		layout_marketRegion.addView(marketUI.getItem());	// scrollview에 추가
		setMarketClickListener(marketUI.getItem(), regionMarket, false, true);
		marketUI.getAddFavorite().setOnClickListener(new OnClickListener() {
			@Override
				public void onClick(View v) {
				KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
				kakaoAnalytics.setFavoriteMarket(kinsightSession, context, regionMarket);

				dbManager.insert(regionMarket);

				addItem();

				Toast.makeText(context, "즐겨찾기 마켓에 추가되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 마켓 선택했을 때 click 동작
	void setMarketClickListener(final LinearLayout item, final Market market,
			final boolean flagFavorite, final boolean flagRegion) {

		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent_goodsListActivity = new Intent(context, GoodsListActivity.class);
				intent_goodsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	// activity가 아닌 class에서 activity를 띄우기 위한 flag
				intent_goodsListActivity.putExtra("MARKET", market);
				intent_goodsListActivity.putExtra("FLAGFAVORITE", flagFavorite);
				intent_goodsListActivity.putExtra("FLAGREGION", flagRegion);

				if(flagFavorite && flagRegion) {
					KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
					kakaoAnalytics.searchGoods(kinsightSession, context, market, true, true);
				} else if(!flagFavorite && flagRegion) {
					KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
					kakaoAnalytics.searchGoods(kinsightSession, context, market, false, true);
				} else {
					KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
					kakaoAnalytics.searchGoods(kinsightSession, context, market, true, false);
				}

				context.startActivity(intent_goodsListActivity);
			}
		});
	}

	private String checkNull(JSONObject jsonObject, String string) {
		try {
			if (jsonObject.has(string)) {
				return jsonObject.getString(string);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}