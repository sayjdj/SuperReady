package com.haffle.superready.market;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.haffle.superready.R;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.coupon.CouponPocketActivity;
import com.haffle.superready.db.MarketDBManager;
import com.haffle.superready.gcm.RegistrationIntentService;
import com.haffle.superready.goods_favorite.GoodsFavoriteActivity;
import com.haffle.superready.location.LocationSettingActivity;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.support.CustomerSupportActivity;
import com.haffle.superready.support.MarketRequestActivity;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends FragmentActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    ActivityManager activityManager = ActivityManager.getInstance();
    LinearLayout layout_marketRegion;
    LinearLayout layout_marketFavorite;
    LayoutInflater inflater;
    Context context;
    Activity activity;
    GoogleMap gmap;
    TextView textView_location;
    ImageView imageView_couponPocket;
    ReceiveMarket receiveMarket;
    RelativeLayout layout_favorite;
    MarketDBManager dbManager;
    ImageView imageView_setLocation;
    ImageView imageView_customerSupport;
    ImageView imageView_marketRequest;
    ImageView imageView_superReady;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static KinsightSession kinsightSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_main);

        activityManager.addActivity(this);
        context = getApplicationContext();
        activity = this;

        // kakao analytics session을 연다
        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
        }

        // market favorite의 db를 가져온다
        dbManager = new MarketDBManager(getApplicationContext(), "MARKET.db",
                null, 1);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();

        InfoManager.SetData(getApplicationContext());
        getInstanceIdToken();

        // notice 설정이 on이면 공지사항을 띄어준다
        if (sp.getBoolean(InfoManager.SHARED_PREFERENCE_NOTICEON, true)) {
            Intent intent = getIntent();
            String message = intent.getStringExtra("MESSAGE");
            displayNotice(message);
        }

        setView();
        checkKakaoLink();
        setClickListener();

        setMap();

        // 마트 리스트를 받아온다.
        receiveMarket = new ReceiveMarket(context, this, inflater);
        receiveMarket.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
            kinsightSession.tagScreen("전단목록-메인");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            setMap();

            layout_marketFavorite.removeAllViews();
            layout_marketRegion.removeAllViews();

            // 마트 리스트를 받아온다.
            receiveMarket = new ReceiveMarket(context, this, inflater);
            receiveMarket.execute();
        }
    }

    @Override
    protected void onPause() {
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

    private void setView() {
        layout_marketRegion = (LinearLayout) findViewById(R.id.main_marketRegion);
        layout_marketFavorite = (LinearLayout) findViewById(R.id.main_marketFavorite);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gmap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_locationMap)).getMap();
        textView_location = (TextView) findViewById(R.id.main_location);
        imageView_couponPocket = (ImageView) findViewById(R.id.main_couponPocket);
        layout_favorite = (RelativeLayout) findViewById(R.id.titlebar_main_favorite);
        imageView_customerSupport = (ImageView) findViewById(R.id.main_customerSupport);
        imageView_marketRequest = (ImageView) findViewById(R.id.main_marketRequest);
        imageView_setLocation = (ImageView) findViewById(R.id.main_locationSetting);
        imageView_superReady = (ImageView) findViewById(R.id.titlebar_main_superReady);
    }

    private void setClickListener() {

        // 고객센터 눌렀을 때
        imageView_customerSupport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_customerSupport = new Intent(getApplicationContext(),
                        CustomerSupportActivity.class);

                KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                kakaoAnalytics.startCustomerSupport(kinsightSession, context);

                startActivity(intent_customerSupport);
            }
        });

        imageView_marketRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_marketRequest = new Intent(getApplicationContext(),
                        MarketRequestActivity.class);

                // kakao Analytics

                startActivity(intent_marketRequest);
            }
        });

        // 위치변경 눌렀을 때
        imageView_setLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chkGpsService();
            }
        });

//         관심상품 눌렀을 때
        layout_favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_goodsFavoriteActivity = new Intent(
                        getApplicationContext(), GoodsFavoriteActivity.class);
                startActivity(intent_goodsFavoriteActivity);
            }
        });

        imageView_superReady.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("앱 버전 확인")        // 제목 설정
                        .setMessage(InfoManager.buildType + "\n\n" +
                                "http://superready" + InfoManager.urlAddess + ".azurewebsites.net/v0" + "\n\n" +
                                "Kakao Analytics : " + InfoManager.flagKakao + "\n" +
                                "Crashlytics : " + InfoManager.flagCrashLytics + "\n\n" +
                                "UserId : " + InfoManager.userToken + "\n" +
                                "UserGender : " + InfoManager.userGender + "\n" +
                                "UserAge : " + InfoManager.userAge)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성

                // Debug와 Staging에서만 alert Dialog를 띄운다
                if (InfoManager.buildType.equals("DEBUG") || InfoManager.buildType.equals("STAGING")) {
                    dialog.show();
                }
            }
        });

        imageView_couponPocket.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_coupon = new Intent(getApplicationContext(),
                        CouponPocketActivity.class);
                startActivity(intent_coupon);
            }
        });
    }

    // google map setting
    private void setMap() {
        // CameraUpdateFactory is not initialized error를 고치기 위한 코드
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LatLng startingPoint = new LatLng(Double.parseDouble(InfoManager.latitude), Double.parseDouble(InfoManager.longitude));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 15));
        gmap.getUiSettings().setAllGesturesEnabled(false); // 지도 touch 비활성화

        textView_location.setText(getAddress(Double.parseDouble(InfoManager.latitude), Double.parseDouble(InfoManager.longitude)));
    }

    private String getAddress(double lat, double lng) {
        String address = null;

        // 위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // 주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        do {
            try {
                list = geocoder.getFromLocation(lat, lng, 1);

                if (list.size() > 0) { // 주소값이 존재하면
                    Address addr = list.get(0);
                    address = addr.getLocality() + " " + addr.getThoroughfare();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (address == null);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(InfoManager.SHARED_PREFERENCE_ADDRESS, address);
        editor.commit();

        return address;
    }

    void checkKakaoLink() {
        Intent intent_kacao = getIntent();

        // 카카오 링크로 앱이 실행됬을 경우
        if (Intent.ACTION_VIEW.equals(intent_kacao.getAction())) { // 커스텀 url일때

            activityManager.finishAllActivityExceptOne();

            Uri uri = intent_kacao.getData();
            String scheme_data = uri.getQuery(); // data=your_data 값이 나오게 된다

            StringTokenizer str = new StringTokenizer(scheme_data, "= &");
            String[] data = new String[4];

            for (int i = 0; str.hasMoreTokens(); i++) {
                data[i] = str.nextToken();
            }
            String first_name = data[0];
            String first_value = data[1];
            String second_name = data[2];
            String second_value = data[3];

            if (first_name.equals("marketId")) {
                KakaoReceiveMarket kakaoReceiveMarket = new KakaoReceiveMarket(
                        context, layout_marketRegion, inflater);
                kakaoReceiveMarket.execute(first_value, second_value);
            } else {
                KakaoReceiveMarket kakaoReceiveMarket = new KakaoReceiveMarket(
                        context, layout_marketRegion, inflater);
                kakaoReceiveMarket.execute(second_value, first_value);
            }
        }
    }

    private boolean chkGpsService() {

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNETWORK = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPS && !isNETWORK) { // 둘다 꺼져있을 때
            // gps off 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 사용 설정");
            gsDialog.setMessage("슈퍼레디에서 위치 정보를 사용하려면 위치 서비스 권한을 허용해주세요.");
            gsDialog.setNegativeButton("설정",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // gps 설정 화면으로 이동
                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    return;
                                }
                            }).create().show();

            return false;
        } else {
            Intent intent_locationSettingActivity = new Intent(
                    getApplicationContext(), LocationSettingActivity.class);
            startActivityForResult(intent_locationSettingActivity, 0);
            return true;
        }
    }

    void displayNotice(final String text) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("공지사항");
            dialog.setMessage(text);
            dialog.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            })
                    .setPositiveButton("다시보지않음", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean(InfoManager.SHARED_PREFERENCE_NOTICEON, false);
                            editor.commit();
                        }
                    }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    // Google Play Service를 사용할 수 있는 환경이지를 체크한다.
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}