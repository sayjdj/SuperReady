package com.haffle.superready.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;

import java.util.HashMap;
import java.util.Map;

public class MarketRequestActivity extends FragmentActivity {

    ActivityManager activityManager = ActivityManager.getInstance();
    WebView webView;
    Map<String, String> header = new HashMap<String, String>();
    RelativeLayout layout_back;
//    public static KinsightSession kinsightSession;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_market_request);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_market_request);

//        if (InfoManager.flagKakao) {
//            kinsightSession = new KinsightSession(getApplicationContext());
//            kinsightSession.open();
//        }

        activityManager.addActivity(this);

        setView();
        setClickListener();

        header.put("X-Auth_Token", InfoManager.userToken);

        webView.getSettings().setJavaScriptEnabled(true);      // 웹뷰에서 자바 스크립트 사용
        webView.loadUrl(InfoManager.URL_MARKET_REQUEST + InfoManager.userToken, header);            // 웹뷰에서 불러올 URL 입력
        webView.setWebViewClient(new WebViewClientClass());    // client 연결

        context = this;

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.endsWith("superready.done")) {
                        new AlertDialog.Builder(context)
                                .setTitle("제출완료")
                                .setMessage("소중한 의견 감사합니다.")
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();

                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                }
            }

            );

            webView.setWebChromeClient(new

            WebChromeClient() {

                @Override
                public boolean onJsAlert (WebView view, String url, String message,
                final android.webkit.JsResult result){
                    new AlertDialog.Builder(context)
                            .setTitle("오류")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            };
        });
    }

    public void onResume() {
        super.onResume();

//        if (InfoManager.flagKakao) {
//            kinsightSession.open();
//            kinsightSession.tagScreen("마켓요청");
//        }
    }

    public void onPause() {
        super.onPause();

//        if (InfoManager.flagKakao) {
//            kinsightSession.close();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManager.deleteTopActivity();
    }

    void setView() {
        webView = (WebView) findViewById(R.id.marketRequest_webView);
        layout_back = (RelativeLayout) findViewById(R.id.titlebar_marketRequest_back);
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
