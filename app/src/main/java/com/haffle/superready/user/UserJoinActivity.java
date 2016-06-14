package com.haffle.superready.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.kinsight.sdk.android.KinsightSession;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class UserJoinActivity extends FragmentActivity {

    ActivityManager activityManager = ActivityManager.getInstance();

    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");    // json 타입 설정
    OkHttpClient client;

    EditText editText_year;
    Button button_male;
    Button button_female;
    Button button_complete;
    Handler handler;
    int curYear;
    String gender = "";

    public static KinsightSession kinsightSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_user_join);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_user_join);

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
        }

        activityManager.addActivity(this);

        handler = new Handler();
        Calendar cal = Calendar.getInstance();
        curYear = cal.get(Calendar.YEAR);
        client = new OkHttpClient();

        setView();
        inputData();
        setClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
            kinsightSession.tagScreen("사용자정보입력");
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

    void setView() {
        editText_year = (EditText) findViewById(R.id.user_join_year);
        button_male = (Button) findViewById(R.id.user_join_male);
        button_female = (Button) findViewById(R.id.user_join_female);
        button_complete = (Button) findViewById(R.id.user_join_complete);
    }

    void setClickListener() {
        button_complete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkData()) {
                    sendData();
                }
            }
        });
    }

    void finishActivity(final Boolean isSuccess) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    Toast.makeText(getApplicationContext(), "가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    void inputData() {

        editText_year.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String year = editText_year.getText().toString();
                int num;

                if (!year.equals("")) {
                    num = Integer.parseInt(year);

                    if (!gender.equals("")) {
                        button_complete.setEnabled(true);
                    }

                    if (num < (curYear - 150) || num > curYear) {
                        editText_year.setTextColor(0xffFF0000);
                    } else {
                        editText_year.setTextColor(0xff1ED2A1);
                    }
                }
            }
        });

        button_male.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gender = "M";
                String year = editText_year.getText().toString();

                button_male.setBackgroundColor(0xffff1d1d);
                button_female.setBackgroundColor(0xffffffff);
                button_male.setTextColor(0xffffffff);
                button_female.setTextColor(0xff636363);

                if (!year.equals("") && !year.equals("")) {
                    button_complete.setEnabled(true);
                }
            }
        });

        button_female.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gender = "F";
                String year = editText_year.getText().toString();

                button_male.setBackgroundColor(0xffffffff);
                button_female.setBackgroundColor(0xffff1d1d);
                button_male.setTextColor(0xff636363);
                button_female.setTextColor(0xffffffff);

                if (!year.equals("") && !year.equals("")) {
                    button_complete.setEnabled(true);
                }
            }
        });
    }

    boolean checkData() {
        String year = editText_year.getText().toString();

        if (year.equals("") || gender.equals("")) {
            Toast.makeText(getApplicationContext(), "성년과 성별을 모두 입력하셨나요?", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int num = Integer.parseInt(year);
            if (num < (curYear - 150) || num > curYear) {
                Toast.makeText(getApplicationContext(), "생년을 올바르게 입력하셨는지 확인해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    void sendData() {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Thread thread = new Thread(new Runnable() {
            public void run() {
                String url = InfoManager.URL_USERJOIN;

                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("gender", gender);
                    jObj.put("birth", editText_year.getText().toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, jObj.toString());
                Request request = new Request.Builder()
                        .addHeader("X-Auth-Token", InfoManager.userToken)
                        .url(url)
                        .put(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();    // 서버에 전송

                    String json = response.body().string();

                    // 응답 parsing
                    JSONObject object = new JSONObject(json);

                    // success를 받은 경우
                    if (object.getString("status").equals("success")) {
                        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = mPref.edit();

                        editor.putString(InfoManager.SHARED_PREFERENCE_USERGENDER, gender);
                        editor.putString(InfoManager.SHARED_PREFERENCE_USERAGE, editText_year.getText().toString());
                        editor.putBoolean(InfoManager.SHARED_PREFERENCE_USERJOIN, true);    // 가입 유무 저장
                        editor.commit();

                        InfoManager.SetData(getApplicationContext());

                        KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                        kakaoAnalytics.sendUserInfo(kinsightSession, getApplicationContext());

                        finishActivity(true);    // activity 종료
                    } else {
                        finishActivity(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}