package com.haffle.superready.coupon;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.haffle.superready.item.Coupon;
import com.haffle.superready.manager.InfoManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class ReceiveMyCouponList extends AsyncTask<String, Integer, Void> {

    Context context;
    String json;
    ArrayList<Coupon> coupons = new ArrayList<Coupon>();
    CouponPocketViewAdapter availableCouponPocketViewAdapter;
    CouponPocketViewAdapter usedCouponPocketViewAdapter;
    ListView availableCouponListView;
    ListView usedCouponListView;

    ReceiveMyCouponList(Context context,
                        CouponPocketViewAdapter availableCouponPocketViewAdapter,
                        CouponPocketViewAdapter usedCouponPocketViewAdapter,
                        ListView availableCouponListView,
                        ListView usedCouponListView) {
        this.context = context;
        this.availableCouponPocketViewAdapter = availableCouponPocketViewAdapter;
        this.usedCouponPocketViewAdapter = usedCouponPocketViewAdapter;
        this.availableCouponListView = availableCouponListView;
        this.usedCouponListView = usedCouponListView;
    }

    @Override
    protected Void doInBackground(String... urls) {
        receiveJson();
        parsingJson();
        addCoupons();
        return null;
    }

    protected void onPostExecute(Void temp) {
        availableCouponListView.setAdapter(availableCouponPocketViewAdapter);
        usedCouponListView.setAdapter(usedCouponPocketViewAdapter);
        setListViewHeightBasedOnChildren(availableCouponListView);
        setListViewHeightBasedOnChildren(usedCouponListView);
    }

    private void receiveJson() {
        String url = InfoManager.URL_COUPON_LIST +
                InfoManager.userToken +
                "&stage=y&force=y";

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            json = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsingJson() {

        try {
            JSONObject object = new JSONObject(json);
            JSONArray dataArray = new JSONArray(object.getString("data"));

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);

                coupons.add(new Coupon());

                coupons.get(i).setId(dataObject.getString("id"));
                coupons.get(i).setMarketName(dataObject.getString("marketName"));
                coupons.get(i).setName(dataObject.getString("name"));
                coupons.get(i).setDescription(dataObject.getString("description"));
                coupons.get(i).setImage(dataObject.getString("image"));
                coupons.get(i).setIssued(dataObject.getString("issued"));
                coupons.get(i).setUsed(dataObject.getString("used"));
                coupons.get(i).setExpiration(dataObject.getString("expiration"));
                coupons.get(i).setValid(dataObject.getString("valid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void addCoupons() {

        for (int i = 0; i < coupons.size(); i++) {
            if (coupons.get(i).getValid().equals("true")) {
                coupons.get(i).setType(0);
                availableCouponPocketViewAdapter.addCoupon(coupons.get(i));
            } else {
                if (coupons.get(i).getUsed().equals("null")) {   // 사용되지 않았을 경우
                    coupons.get(i).setType(2);
//                    coupons.get(i).setType(0);
                } else {
                    coupons.get(i).setType(1);
//                    coupons.get(i).setType(0);
                }
                usedCouponPocketViewAdapter.addCoupon(coupons.get(i));
            }
        }
    }
}