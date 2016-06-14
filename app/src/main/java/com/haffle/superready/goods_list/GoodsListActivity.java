package com.haffle.superready.goods_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haffle.superready.R;
import com.haffle.superready.goods_favorite.GoodsFavoriteActivity;
import com.haffle.superready.item.Market;
import com.haffle.superready.item.NormalCampaign;
import com.haffle.superready.item.TimeSaleCampaign;
import com.haffle.superready.location.RoughMapActivity;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.notice.MarketNoticeActivity;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.util.ArrayList;

public class GoodsListActivity extends FragmentActivity {

    ActivityManager activityManager = ActivityManager.getInstance();

    ScrollView scrollView;
    ListView listView;
    ImageView imageView_map;
    ImageView imageView_phonenumber;
    ImageView imageView_marketImage;
    ImageView imageView_marketLogo;
    ImageView imageView_marketNews;
    RelativeLayout layout_favorite;
    TextView textView_marketBusinessHours;
    TextView textView_marketLogo;
    TextView textView_marketDescription;
    RelativeLayout layout_back;
    ArrayList<TimeSaleCampaign> timeSaleCampaign = new ArrayList<TimeSaleCampaign>();
    ArrayList<NormalCampaign> normalCampaign = new ArrayList<NormalCampaign>();
    Handler handler;
    String highLightGoodsId;
    boolean flagFavorite;
    boolean flagRegion;
    ReceiveCampaigns receiveCampaigns;

    public static KinsightSession kinsightSession;

    Market market;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_goods_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_goods_list);

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
        }

        activityManager.addActivity(this);
        context = getApplicationContext();
        handler = new Handler();

        setView();
        setMarketInfo();
        setClickListener();

        try {
            // 상품 리스트를 받아온다
            receiveCampaigns = new ReceiveCampaigns(context, this, listView, scrollView,
                    timeSaleCampaign, normalCampaign, market, flagFavorite, flagRegion);
            receiveCampaigns.execute(highLightGoodsId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //		if((timeSaleCampaign.size() != 0) || (normalCampaign.size() != 0)) {
//			GoodsFavoriteCheck goodsFavoriteCheck = new GoodsFavoriteCheck(context, timeSaleCampaign, normalCampaign);
//		}

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
            kinsightSession.tagScreen("전단상품목록");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            receiveCampaigns.notifyDataSetChanged();
        }
    }

    void setView() {
        scrollView = (ScrollView) findViewById(R.id.goodslist_scrollView);
        imageView_map = (ImageView) findViewById(R.id.goodsList_marketMap);
        imageView_phonenumber = (ImageView) findViewById(R.id.goodsList_marketPhonenumber);
        imageView_marketImage = (ImageView) findViewById(R.id.goodsList_marketImage);
        imageView_marketLogo = (ImageView) findViewById(R.id.titlebar_goodsList_marketLogoImage);
        imageView_marketNews = (ImageView) findViewById(R.id.goodsList_marketNotice);
        layout_favorite = (RelativeLayout) findViewById(R.id.titlebar_goodsList_favorite);
        textView_marketBusinessHours = (TextView) findViewById(R.id.goodsList_marketBusinesshours);
        textView_marketLogo = (TextView) findViewById(R.id.titlebar_goodsList_marketLogoText);
        textView_marketDescription = (TextView) findViewById(R.id.goodsList_marketDescription);
        layout_back = (RelativeLayout) findViewById(R.id.titlebar_goodsList_back);
        listView = (ListView) findViewById(R.id.goodsList_listView);
    }

    void setMarketInfo() {

        Intent intent = getIntent();
        market = (Market) intent.getSerializableExtra("MARKET");
        highLightGoodsId = intent.getStringExtra("HIGHLIGHTGOODSID");
        flagFavorite = intent.getBooleanExtra("FLAGFAVORITE", false);
        flagRegion = intent.getBooleanExtra("FLAGREGION", false);

        // 마켓 이미지 설정
        Glide.with(context)
                .load(market.getPanorama())
                .into(imageView_marketImage);

        // 마켓 로고 설정
        ReceiveMarketLogo receiveImage = new ReceiveMarketLogo(context, imageView_marketLogo, textView_marketLogo, market);
        receiveImage.execute();

        // 마켓 운영시간, 설명 설정
        textView_marketBusinessHours.setText("운영시간 " + market.getBusinessHours());
        textView_marketDescription.setText(market.getName() + "\n" + market.getDescription());
    }

    void setClickListener() {
        imageView_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chkGpsService()) {
                    // Intent에 Data객체 저장
                    Intent intent_wayToMartActivity =
                            new Intent(getApplicationContext(), RoughMapActivity.class);
                    intent_wayToMartActivity.putExtra("MARKET", market);
                    startActivity(intent_wayToMartActivity);
                }
            }
        });
        imageView_phonenumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + market.getPhone()));
                startActivity(intent_call);
            }
        });
        imageView_marketNews.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
                    Intent intent_marketNews = new Intent(getApplicationContext(), MarketNoticeActivity.class);
                    intent_marketNews.putExtra("MARKET", market);
                    startActivity(intent_marketNews);
                }
            }
        );
        layout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_goodsFavoriteActivity =
                        new Intent(getApplicationContext(), GoodsFavoriteActivity.class);
                startActivityForResult(intent_goodsFavoriteActivity, 0);
            }
        });
    }

    private boolean chkGpsService() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNETWORK = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPS && !isNETWORK) {    // 둘다 꺼져있을 때
            // gps off 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 사용 설정");
            gsDialog.setMessage("슈퍼레디에서 위치 정보를 사용하려면 위치 서비스 권한을 허용해주세요.");
            gsDialog.setNegativeButton("설정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // gps 설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }
}