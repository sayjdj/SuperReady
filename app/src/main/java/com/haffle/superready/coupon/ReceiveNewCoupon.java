package com.haffle.superready.coupon;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haffle.superready.R;
import com.haffle.superready.item.Coupon;
import com.haffle.superready.item.Market;
import com.haffle.superready.manager.InfoManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

class ReceiveNewCoupon extends AsyncTask<String, Integer, Void> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    Market market;
    Context context;
    boolean isReceiveCoupon = false;
    Coupon coupon;
    TextView textView_text;
    TextView textView_couponPocketconfirmText;
    TextView textView_marketName;
    TextView textView_description;
    Button button_confirm;
    ImageView imageView_image;

    ReceiveNewCoupon(Context context,
                     Market market,
                     TextView textView_text,
                     TextView textView_couponPocketconfirmText,
                     TextView textView_marketName,
                     TextView textView_description,
                     Button button_confirm,
                     ImageView imageView_image) {

        this.context = context;
        this.market = market;
        this.textView_text = textView_text;
        this.textView_couponPocketconfirmText = textView_couponPocketconfirmText;
        this.textView_marketName = textView_marketName;
        this.textView_description = textView_description;
        this.button_confirm = button_confirm;
        this.imageView_image = imageView_image;

        coupon = new Coupon();
    }

    @Override
    protected Void doInBackground(String... urls) {
        String url = InfoManager.URL_COUPON_RECEIVE +
                market.getId() + "?access_token=" +
                InfoManager.userToken +
                "&stage=y&force=y";

        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody body = RequestBody.create(JSON, "");
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            parsingJson(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void temp) {

        if(isReceiveCoupon) {   // 쿠폰을 받았을 경우
            Glide.with(context)
                    .load(coupon.getImage())
                    .into(imageView_image);
            textView_text.setText("축하합니다\n" + "\"" + coupon.getName() +
                    "\"\n" + "증정 쿠폰을 받으셨어요!");
            textView_couponPocketconfirmText.setVisibility(View.VISIBLE);
            textView_marketName.setVisibility(View.VISIBLE);
            textView_description.setVisibility(View.VISIBLE);
            textView_marketName.setText(coupon.getMarketName());
            textView_description.setText(coupon.getDescription());
            button_confirm.setText("쿠폰함 보기");
        } else {                // 쿠폰을 받지 못했을 경우
            textView_text.setText("안타깝네요!\n이번엔 꽝입니다ㅠㅠ\n내일 다시 만나요.");
            imageView_image.setImageResource(R.drawable.coupon_sad_face);
            button_confirm.setText("마침");
        }
    }

    private void parsingJson(String json) {

        try {
            JSONObject object = new JSONObject(json);

            if (object.getString("status").equals("success")) { // 쿠폰을 받은 경우
                isReceiveCoupon = true;

                JSONObject dataObject = new JSONObject(object.getString("data"));

                coupon.setId(dataObject.getString("id"));
                coupon.setMarketName(dataObject.getString("marketName"));
                coupon.setName(dataObject.getString("name"));
                coupon.setDescription(dataObject.getString("description"));
                coupon.setImage(dataObject.getString("image"));
                coupon.setIssued(dataObject.getString("issued"));
                coupon.setUsed(dataObject.getString("used"));
                coupon.setExpiration(dataObject.getString("expiration"));
                coupon.setValid(dataObject.getString("valid"));
            } else {        // 쿠폰을 받지 못한 경우
                isReceiveCoupon = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}