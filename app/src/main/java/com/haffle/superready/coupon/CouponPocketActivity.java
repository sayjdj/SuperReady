package com.haffle.superready.coupon;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haffle.superready.R;

public class CouponPocketActivity extends FragmentActivity {

    RelativeLayout layout_back;
    CouponPocketViewAdapter availableCouponPocketViewAdapter;
    CouponPocketViewAdapter usedCouponPocketViewAdapter;
    ListView availableCouponListView;
    ListView usedCouponListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_coupon_pocket);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_coupon_pocket);

        setView();
        setClickListener();

        ReceiveMyCouponList receiveMyCouponList = new ReceiveMyCouponList(getApplicationContext(),
                availableCouponPocketViewAdapter,
                usedCouponPocketViewAdapter,
                availableCouponListView,
                usedCouponListView);
        receiveMyCouponList.execute();
    }

    private void setView() {
        layout_back = (RelativeLayout) findViewById(R.id.titlebar_couponPocket_back);
        availableCouponListView = (ListView) findViewById(R.id.couponPocket_availableCoupon_listView);
        availableCouponPocketViewAdapter = new CouponPocketViewAdapter(getApplicationContext(), this);   // listViewAdapter 객체 생성
        usedCouponListView = (ListView) findViewById(R.id.couponPocket_usedCoupon_listView);
        usedCouponPocketViewAdapter = new CouponPocketViewAdapter(getApplicationContext(), this);   // listViewAdapter 객체 생성
    }

    private void setClickListener() {
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
