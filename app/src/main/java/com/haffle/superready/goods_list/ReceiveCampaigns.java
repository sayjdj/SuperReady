package com.haffle.superready.goods_list;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.haffle.superready.R;
import com.haffle.superready.alarm.AlarmReceiver;
import com.haffle.superready.db.AlarmDBManager;
import com.haffle.superready.db.GoodsDBManager;
import com.haffle.superready.item.Alarm;
import com.haffle.superready.item.CouponCard;
import com.haffle.superready.item.Flyer;
import com.haffle.superready.item.FlyerCampaign;
import com.haffle.superready.item.Goods;
import com.haffle.superready.item.Market;
import com.haffle.superready.item.NoCampaign;
import com.haffle.superready.item.NormalCampaign;
import com.haffle.superready.item.TimeSaleCampaign;
import com.haffle.superready.manager.InfoManager;
import com.kakao.kinsight.sdk.android.KinsightSession;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

class ReceiveCampaigns extends AsyncTask<String, Integer, Void> {

    Context context;
    ListView listView;
    GoodsListViewAdapter listViewAdapter;
    LayoutInflater inflater;
    ScrollView scrollView;
    int height = 0;
    String highLightGoodsId = null;
    boolean searchGoods = false;
    GoodsDBManager goodsDBManager;
    AlarmDBManager alarmDBManager;
    ArrayList<TimeSaleCampaign> timeSaleCampaign = new ArrayList<TimeSaleCampaign>();
    ArrayList<NormalCampaign> normalCampaign = new ArrayList<NormalCampaign>();
    ArrayList<FlyerCampaign> flyerCampaign = new ArrayList<FlyerCampaign>();
    public static KinsightSession kinsightSession;
    Market market;
    boolean flagFavorite;
    boolean flagRegion;
    ProgressBar progressBar;
    String couponStatus;

    ReceiveCampaigns(Context context, Activity activity, ListView listView, ScrollView scrollView,
                     ArrayList<TimeSaleCampaign> timeSaleCampaign, ArrayList<NormalCampaign> normalCampaign,
                     Market market, boolean flagFavorite, boolean flagRegion) {
        this.context = context;
        this.listView = listView;
        this.scrollView = scrollView;
        this.timeSaleCampaign = timeSaleCampaign;
        this.normalCampaign = normalCampaign;
        this.market = market;
        this.flagFavorite = flagFavorite;
        this.flagRegion = flagRegion;

        progressBar = (ProgressBar) activity.findViewById(R.id.goodsList_progressBar);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        kinsightSession = new KinsightSession(context);
    }

    @Override
    protected Void doInBackground(String... urls) {

        highLightGoodsId = urls[0];

        goodsDBManager = new GoodsDBManager(context, "GOODS.db", null, 1);
        alarmDBManager = new AlarmDBManager(context, "ALARM.db", null, 1);

        checkCoupon();

        String url = InfoManager.URL_CAMPAIGNS_FIRST +
                market.getId() +
                InfoManager.URL_CAMPAIGNS_SECOND;

        String jsonCampaigns = receiveCampaigns(url);    // 서버에서 json data를 받아온다.
        parsingCampaigns(jsonCampaigns);

        receiveGoods();

        listViewAdapter = new GoodsListViewAdapter(context, market, flagFavorite, flagRegion);   // listViewAdapter 객체 생성

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void onPostExecute(Void temp) {

        if(couponStatus.equals("available")) {      // 쿠폰 사용가능한 경우
            addCouponCard(new CouponCard("오늘" + market.getName() + "의 \n" +
            "무료 쿠폰을 받지 않으셨어요.", "쿠폰받기"));
        } else if(couponStatus.equals("noevent")) {     // 쿠폰이 없는 경우
        } else {                // 쿠폰을 이미 받은 경우
            addCouponCard(new CouponCard("오늘 쿠폰을 이미 확인하셨습니다.", "쿠폰함 보기"));
        }

        // 상품이 없을 경우
        if ((timeSaleCampaign.size() == 0) && (normalCampaign.size() == 0) && (flyerCampaign.size() == 0)) {
            addNoCampaign();
        } else {
            // image상품을 추가
            for(int i=0; i<flyerCampaign.size(); i++) {
                addImageCampaign(flyerCampaign.get(i));
            }
            // 기간상품을 추가
            for (int i = 0; i < timeSaleCampaign.size(); i++) { // campaign 확인
                addTimeSaleCampaign(timeSaleCampaign.get(i));
            }
            // 기본상품을 추가
            for (int i = 0; i < normalCampaign.size(); i++) {
                addNormalCampaign(normalCampaign.get(i));
            }
        }

        listView.setAdapter(listViewAdapter);           // listView와 listViewAdapter 연결
        setListViewHeightBasedOnChildren(listView);

        progressBar.setVisibility(View.INVISIBLE);
    }

    // scroll view에 기간 상품 추가
    void addTimeSaleCampaign(TimeSaleCampaign timeSaleCampaign) {
        listViewAdapter.addCampaign(timeSaleCampaign);  // campaign을 추가
        listViewAdapter.addTimeSaleGoods(timeSaleCampaign.getGoods());  // timesale 상품을 추가
    }

    void addNormalCampaign(NormalCampaign normalCampaign) {
        listViewAdapter.addCampaign(normalCampaign);    // campaign을 추가
        listViewAdapter.addNormalGoods(normalCampaign.getGoods());      // normal 상품을 추가
    }

    void addImageCampaign(FlyerCampaign imageCampaign) {
        listViewAdapter.addCampaign(imageCampaign);     // campaign을 추가
        listViewAdapter.addFlyer(imageCampaign.getFlyer());      // image 상품을 추가
    }

    void addNoCampaign() {
        listViewAdapter.addNoCampaign(new NoCampaign());

    }

    void addCouponCard(CouponCard couponCard) {
        listViewAdapter.addCouponCard(couponCard);
    }

    String receiveCampaigns(String url) {

        OkHttpClient client = new OkHttpClient();
        String json = "";

        Request request = new Request.Builder()
                .addHeader("X-Auth-Token", InfoManager.userToken)
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            json = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    void receiveGoods() {
        OkHttpClient client = new OkHttpClient();
        String json = "";

        for (int i = 0; i < flyerCampaign.size(); i++) {
            String url = InfoManager.URL_GOODS_FIRST + flyerCampaign.get(i).getId() +
                    InfoManager.URL_GOODS_SECOND;

            Request request = new Request.Builder()
                    .addHeader("X-Auth-Token", InfoManager.userToken)
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                json = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            parsingFlyer(json, flyerCampaign.get(i));
        }

        for (int i = 0; i < timeSaleCampaign.size(); i++) {
            String url = InfoManager.URL_GOODS_FIRST + timeSaleCampaign.get(i).getId() +
                    InfoManager.URL_GOODS_SECOND;

            Request request = new Request.Builder()
                    .addHeader("X-Auth-Token", InfoManager.userToken)
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                json = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            parsingTimeSaleGoods(json, timeSaleCampaign.get(i));
        }

        for (int i = 0; i < normalCampaign.size(); i++) {
            String url = InfoManager.URL_GOODS_FIRST + normalCampaign.get(i).getId() +
                    InfoManager.URL_GOODS_SECOND;

            Request request = new Request.Builder()
                    .addHeader("X-Auth-Token", InfoManager.userToken)
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                json = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            parsingNormalGoods(json, normalCampaign.get(i));
        }
    }

    void parsingCampaigns(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray dataArray = new JSONArray(object.getString("data"));

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject dataObject = dataArray.getJSONObject(i);

                if (dataObject.getString("type").equals("big")) {    // timeSaleCampaign parsing
                    timeSaleCampaign.add(new TimeSaleCampaign());

                    int timeSaleCampaignSize = timeSaleCampaign.size() - 1;

                    timeSaleCampaign.get(timeSaleCampaignSize).setId(checkNull(dataObject, "id"));
                    timeSaleCampaign.get(timeSaleCampaignSize).setName(checkNull(dataObject, "name"));
                    timeSaleCampaign.get(timeSaleCampaignSize).setDescription(checkNull(dataObject, "description"));
                    timeSaleCampaign.get(timeSaleCampaignSize).setStart(checkNull(dataObject, "startDate"));
                    timeSaleCampaign.get(timeSaleCampaignSize).setEnd(checkNull(dataObject, "endDate"));

                } else if(dataObject.getString("type").equals("small")){    // normalCampaign parsing
                    normalCampaign.add(new NormalCampaign());

                    int normalCampaignSize = normalCampaign.size() - 1;

                    normalCampaign.get(normalCampaignSize).setId(checkNull(dataObject, "id"));
                    normalCampaign.get(normalCampaignSize).setName(checkNull(dataObject, "name"));
                    normalCampaign.get(normalCampaignSize).setDescription(checkNull(dataObject, "description"));
                    normalCampaign.get(normalCampaignSize).setStart(checkNull(dataObject, "startDate"));
                    normalCampaign.get(normalCampaignSize).setEnd(checkNull(dataObject, "endDate"));
                } else if(dataObject.getString("type").equals("image")) {
                    flyerCampaign.add(new FlyerCampaign());

                    int imageCampaignSize = flyerCampaign.size() - 1;

                    flyerCampaign.get(imageCampaignSize).setId(checkNull(dataObject, "id"));
                    flyerCampaign.get(imageCampaignSize).setName(checkNull(dataObject, "name"));
                    flyerCampaign.get(imageCampaignSize).setDescription(checkNull(dataObject, "description"));
                    flyerCampaign.get(imageCampaignSize).setStart(checkNull(dataObject, "startDate"));
                    flyerCampaign.get(imageCampaignSize).setEnd(checkNull(dataObject, "endDate"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // timeSale json을 파싱해서 timeSale 객체를 생성
    void parsingTimeSaleGoods(String json, TimeSaleCampaign timeSaleCampaign) {

        ArrayList<Goods> timeSaleGoods = timeSaleCampaign.getGoods();

        try {
            JSONObject object = new JSONObject(json);
            JSONArray dataArray = new JSONArray(object.getString("data"));

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject dataObject = dataArray.getJSONObject(i);

                timeSaleGoods.add(new Goods());

                timeSaleGoods.get(i).setCampaign(timeSaleCampaign);
                timeSaleGoods.get(i).setId(checkNull(dataObject, "id"));
                timeSaleGoods.get(i).setName(checkNull(dataObject, "name"));
                timeSaleGoods.get(i).setPrePrice(checkNull(dataObject, "normalPrice"));
                timeSaleGoods.get(i).setCurPrice(checkNull(dataObject, "retailPrice"));
                timeSaleGoods.get(i).setDescription(checkNull(dataObject, "description"));
                timeSaleGoods.get(i).setImage(checkNull(dataObject, "image"));
                timeSaleGoods.get(i).setPriceExplanation(checkNull(dataObject, "priceExplanation"));
                timeSaleGoods.get(i).setDiscountRate(checkNull(dataObject, "discountRate"));
                timeSaleGoods.get(i).setLabels(checkNull(dataObject, "labels"));
                timeSaleGoods.get(i).setTimeSale(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // normal 상품 json을 파싱해서 NormalGoods 객체를 생성
    void parsingNormalGoods(String json, NormalCampaign normalCampaign) {

        ArrayList<Goods> normalGoods = normalCampaign.getGoods();

        try {
            JSONObject object = new JSONObject(json);
            JSONArray dataArray = new JSONArray(object.getString("data"));

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject dataObject = dataArray.getJSONObject(i);

                normalGoods.add(new Goods());

                normalGoods.get(i).setCampaign(normalCampaign);
                normalGoods.get(i).setId(checkNull(dataObject, "id"));
                normalGoods.get(i).setName(checkNull(dataObject, "name"));
                normalGoods.get(i).setPrePrice(checkNull(dataObject, "normalPrice"));
                normalGoods.get(i).setCurPrice(checkNull(dataObject, "retailPrice"));
                normalGoods.get(i).setDescription(checkNull(dataObject, "description"));
                normalGoods.get(i).setImage(checkNull(dataObject, "image"));
                normalGoods.get(i).setPriceExplanation(checkNull(dataObject, "priceExplanation"));
                normalGoods.get(i).setDiscountRate(checkNull(dataObject, "discountRate"));
                normalGoods.get(i).setLabels(checkNull(dataObject, "labels"));
                normalGoods.get(i).setTimeSale(false);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // image 상품 json을 파싱해서 Flyer 객체를 생성
    void parsingFlyer(String json, FlyerCampaign imageCampaign) {

        ArrayList<Flyer> flyers = imageCampaign.getFlyer();

        try {
            JSONObject object = new JSONObject(json);
            JSONArray dataArray = new JSONArray(object.getString("data"));

            for (int i = 0; i < dataArray.length(); i++) {

                JSONObject dataObject = dataArray.getJSONObject(i);

                flyers.add(new Flyer());

                flyers.get(i).setCampaign(imageCampaign);
                flyers.get(i).setId(checkNull(dataObject, "id"));
                flyers.get(i).setName(checkNull(dataObject, "name"));
                flyers.get(i).setPrePrice(checkNull(dataObject, "normalPrice"));
                flyers.get(i).setCurPrice(checkNull(dataObject, "retailPrice"));
                flyers.get(i).setDescription(checkNull(dataObject, "description"));
                flyers.get(i).setImage(checkNull(dataObject, "image"));
                flyers.get(i).setPriceExplanation(checkNull(dataObject, "priceExplanation"));
                flyers.get(i).setDiscountRate(checkNull(dataObject, "discountRate"));
                flyers.get(i).setLabels(checkNull(dataObject, "labels"));
                flyers.get(i).setTimeSale(false);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    Calendar dateParsing(String string) {
        StringTokenizer str = new StringTokenizer(string, "- T : .");
        String[] date = new String[7];

        for (int i = 0; str.hasMoreTokens(); i++) {
            date[i] = str.nextToken();
        }

        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                Integer.parseInt(date[4]), Integer.parseInt(date[5]));    // 연, 월(-1해야함), 일, 시간, 분, 초
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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("TITLE", goods.getName());
        intent.putExtra("TEXT", "특가 세일기간이 하루 남았습니다!!!");
        intent.putExtra("GOODSID", goods.getId());

        PendingIntent pIntent = PendingIntent.getBroadcast(context, Integer.parseInt(goods.getId()), intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, restTime.getTimeInMillis(), pIntent);
    }

    void cancelAlarm(Goods goods) {
        alarmDBManager.deleteWithGoodsId(Integer.parseInt(goods.getId()));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, Integer.parseInt(goods.getId()), intent, 0);
        alarmManager.cancel(pIntent);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    void notifyDataSetChanged() {
        listViewAdapter.setChangedData();
        listViewAdapter.notifyDataSetChanged();
    }

    private void checkCoupon() {
        OkHttpClient client = new OkHttpClient();
        String json = "";

        String url = InfoManager.URL_COUPON_RECEIVE +
                market.getId() + "?access_token=" +
                InfoManager.userToken +
                "&stage=y&force=y";

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            json = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            JSONObject object = new JSONObject(json);

            couponStatus = object.getString("status");
            Log.e("wkdgusdn3", couponStatus);
            couponStatus = "available";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}