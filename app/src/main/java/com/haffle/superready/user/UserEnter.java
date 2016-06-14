package com.haffle.superready.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.market.MainActivity;
import com.haffle.superready.notice.BlockNoticeActivity;
import com.haffle.superready.notice.UpdateNoticeActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.StringTokenizer;

class UserEnter extends AsyncTask<String, Integer, String> {

    Activity activity;
    Context context;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Handler handler;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Response response = null;

    public UserEnter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        handler = new Handler();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();
    }

    @Override
    protected String doInBackground(String... urls) {

        final String url = InfoManager.URL_USERENTER;
        String json = null;

        JSONObject jsonObject = new JSONObject();

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .addHeader("X-Auth-Token", InfoManager.userToken)
                .url(url)
                .post(body)
                .build();
        try {
            response = client.newCall(request).execute();
            json = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        jsonParsing(json);
    }

    void jsonParsing(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONObject dataObject = new JSONObject(object.getString("data"));
            JSONObject noticeObject = new JSONObject(dataObject.getString("notice"));
            JSONObject supportVersionObject = new JSONObject(noticeObject.getString("supportVersion"));

            String message = noticeObject.getString("message");
            String id = noticeObject.getString("id");
            String block = noticeObject.getString("block");
            String supportVersionAndroid = supportVersionObject.getString("android");
            String appVersion = getAppVersion();

            // 저장되있던 notice id과 새로운 notice id을 비교하여 다르면 notice 설정을 on으로 해준다.
            if (!sp.getString(InfoManager.SHARED_PREFERENCE_NOTICEID, "").equals(id)) {
                editor.putBoolean(InfoManager.SHARED_PREFERENCE_NOTICEON, true);

                // 새로운 id를 저장
                editor.putString(InfoManager.SHARED_PREFERENCE_NOTICEID, id);
                editor.commit();
            }

            // server에서 block을 요청했을 경우
            if (block.equals("true")) {
                Intent intent_blockNoticeActivity = new Intent(context, BlockNoticeActivity.class);
                intent_blockNoticeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                intent_blockNoticeActivity.putExtra("MESSAGE", message);
                context.startActivity(intent_blockNoticeActivity);

                ActivityManager activityManager = ActivityManager.getInstance();
                activityManager.finishAllActivity();
            } else if (decisionBlock(appVersion, supportVersionAndroid)) { // appversion이 낮아서 강제 update를 해야할 경우
                Intent intent_updateNoticeActivity = new Intent(context, UpdateNoticeActivity.class);
                intent_updateNoticeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                intent_updateNoticeActivity.putExtra("MESSAGE", message);
                context.startActivity(intent_updateNoticeActivity);

                ActivityManager activityManager = ActivityManager.getInstance();
                activityManager.finishAllActivity();
            } else {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MESSAGE", message);
                context.startActivity(intent);
                activity.finish();

                // user의 방문 횟수
                int count = Integer.parseInt(sp.getString(InfoManager.SHARED_PREFERENCE_USERVISITCOUNT, "0"));
                // 사용자 가입을 했는지 확인
                Boolean isJoin = sp.getBoolean(InfoManager.SHARED_PREFERENCE_USERJOIN, false);

                // 정보 등록을 하지 않았을 경우, 사용자 가입을 유도한다.
                if (!isJoin) {
                    Intent intent_userJoin = new Intent(context, UserJoinActivity.class);
                    intent_userJoin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                    context.startActivity(intent_userJoin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getAppVersion() {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi.versionName;
    }

    boolean decisionBlock(String appVersion, String supportVersion) {
        StringTokenizer appVersionToken = new StringTokenizer(appVersion, ".");
        StringTokenizer supportVersionToken = new StringTokenizer(supportVersion, ".");

        int[] array_appVersion = new int[3];
        int[] array_supportVersion = new int[3];

        // appVersion을 token
        for (int i = 0; appVersionToken.hasMoreTokens(); i++) {
            array_appVersion[i] = Integer.parseInt(appVersionToken.nextToken());
        }

        // supportVersion을 token
        for (int i = 0; supportVersionToken.hasMoreTokens(); i++) {
            array_supportVersion[i] = Integer.parseInt(supportVersionToken.nextToken());
        }

        if (array_supportVersion[0] > array_appVersion[0]) {
            return true;
        } else if (array_supportVersion[1] > array_appVersion[1]) {
            return true;
        } else {
            return false;
        }
    }
}