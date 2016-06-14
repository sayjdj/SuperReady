package com.haffle.superready.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.haffle.superready.R;
import com.haffle.superready.item.Market;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.util.HashMap;
import java.util.Map;

public class MarketNoticeActivity extends FragmentActivity {

    ActivityManager activityManager = ActivityManager.getInstance();
    WebView webView;
    Map<String, String> header = new HashMap<String, String>();
    RelativeLayout layout_back;
    public static KinsightSession kinsightSession;
    Context context;
    Market market;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_market_notice);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_market_notice);

//        if (InfoManager.flagKakao) {
//            kinsightSession = new KinsightSession(getApplicationContext());
//            kinsightSession.open();
//        }

        Intent intent = getIntent();
        market = (Market)intent.getSerializableExtra("MARKET");

        activityManager.addActivity(this);

        setView();
        setClickListener();

        header.put("X-Auth-Token", InfoManager.userToken);

        webView.getSettings().setJavaScriptEnabled(true);      // 웹뷰에서 자바 스크립트 사용
        webView.loadUrl(InfoManager.URL_MARKET_NOTICE + market.getId(), header);            // 웹뷰에서 불러올 URL 입력
        webView.setWebViewClient(new WebViewClientClass());    // client 연결
    }

    public void onResume() {
        super.onResume();

//        if (InfoManager.flagKakao) {
//            kinsightSession = new KinsightSession(getApplicationContext());
//            kinsightSession.open();
//            kinsightSession.tagScreen("고객지원");
//        }
    }

    public void onPause() {
//        if (InfoManager.flagKakao) {
//            kinsightSession.close();
//        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManager.deleteTopActivity();
    }

    void setView() {
        webView = (WebView) findViewById(R.id.marketNotice_webView);
        layout_back = (RelativeLayout) findViewById(R.id.titlebar_marketNotice_back);
    }

    void setClickListener() {
        layout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url, header);
            return true;
        }
    }
}
