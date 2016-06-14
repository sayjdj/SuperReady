package com.haffle.superready.market;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.goods_list.GoodsListActivity;
import com.haffle.superready.item.Market;
import com.haffle.superready.manager.InfoManager;
import com.kakao.kinsight.sdk.android.KinsightSession;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

class KakaoReceiveMarket extends AsyncTask<String, Integer, String>{

	Market market = new Market();

	Context context;
	LinearLayout layout;
	LayoutInflater inflater;
	String kakaoLinkGoodsId = "0";
	public static KinsightSession kinsightSession;

	KakaoReceiveMarket(Context context, LinearLayout layout, LayoutInflater inflater) {
		this.context = context;
		this.layout = layout;
		this.inflater = inflater;
	}

	@Override
	protected String doInBackground(String... urls) {
		
		String kakaoLinkMarketId = urls[0];
		kakaoLinkGoodsId = urls[1];
		
		kinsightSession = new KinsightSession(context);

		// 특정 마켓의 data를 받아온다.
		String url = InfoManager.URL_SPECIFICMARKET + kakaoLinkMarketId;
		
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		.addHeader("X-Auth-Token", InfoManager.userToken)
		.url(url)
		.build();

		Response response = null;

		try {
			response = client.newCall(request).execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject object = new JSONObject(response.body().string());
			JSONObject dataObject = new JSONObject(object.getString("data"));

			market.setId(checkNull(dataObject, "id"));
			market.setName(checkNull(dataObject, "name"));
			market.setPhone(checkNull(dataObject, "phone"));
			market.setLatitude(checkNull(dataObject, "latitude"));
			market.setLongitude(checkNull(dataObject, "longitude"));
			market.setDescription(checkNull(dataObject, "description"));
			market.setPanorama(checkNull(dataObject, "panorama"));
			market.setBusinessHours(checkNull(dataObject, "businessHours"));
			market.setLogo(checkNull(dataObject, "logo"));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	protected void onPostExecute(String temp){
		Intent intent_goodsListActivity = new Intent(context, GoodsListActivity.class);
		intent_goodsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	// activity가 아닌 class에서 activity를 띄우기 위한 flag
		intent_goodsListActivity.putExtra("MARKET", market);
		intent_goodsListActivity.putExtra("HIGHLIGHTGOODSID", kakaoLinkGoodsId);
		
		KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
		kakaoAnalytics.searchKakaoLink(kinsightSession, context, market);
		
		context.startActivity(intent_goodsListActivity);
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
