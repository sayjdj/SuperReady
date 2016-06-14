package com.haffle.superready.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

import com.haffle.superready.manager.InfoManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistration {

    Context context;
    String deviceId;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");    // json 타입 설정
    OkHttpClient client = new OkHttpClient();

    public UserRegistration(Context context) {
        this.context = context;

        RegisterUser();
    }

    public void RegisterUser() {
        deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        Thread thread = new Thread(new Runnable() {
            public void run() {

                String url = InfoManager.URL_USERREGISTRATION;

                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("deviceId", deviceId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, jObj.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();    // 서버에 전송

                    // 응답 parsing
                    JSONObject object = new JSONObject(response.body().string());

                    // success를 받을 경우
                    if (object.getString("status").equals("success")) {
                        JSONObject idObject = object.getJSONObject("data");

                        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = mPref.edit();

                        String token = idObject.getString("token");

                        editor.putBoolean(InfoManager.SHARED_PREFERENCE_USERREGISTRATION, true);    // user 등록유무 저장
                        editor.putString(InfoManager.SHARED_PREFERENCE_USERTOKEN, token);    // user id 저장
                        InfoManager.userToken = token;
                        editor.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
