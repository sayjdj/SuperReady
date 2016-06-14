package com.haffle.superready.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haffle.superready.R;
import com.haffle.superready.item.Market;

public class CouponReceiveActivity extends FragmentActivity {

    TextView textView_text;
    TextView textView_couponPocketconfirmText;
    TextView textView_marketName;
    TextView textView_description;
    Button button_confirm;
    ImageView imageView_image;
    RelativeLayout layout_back;
    Market market;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_coupon_receive);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_coupon_receive);

        Intent intent = getIntent();
        market = (Market) intent.getSerializableExtra("MARKET");

        setView();
        setClickListener();
    }

    private void setView() {
        textView_text = (TextView) findViewById(R.id.couponReceive_text);
        textView_couponPocketconfirmText = (TextView) findViewById(R.id.couponReceive_couponPocketConfirmText);
        textView_marketName = (TextView) findViewById(R.id.couponReceive_marketName);
        textView_description = (TextView) findViewById(R.id.couponReceive_description);
        button_confirm = (Button) findViewById(R.id.couponReceive_confirm);
        layout_back = (RelativeLayout) findViewById(R.id.titlebar_couponReceive_back);
        imageView_image = (ImageView) findViewById(R.id.couponReceive_image);
    }

    private void setClickListener() {
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button_confirm.getText().equals("확인하기")) {
                    ReceiveNewCoupon receiveCoupon = new ReceiveNewCoupon(getApplicationContext(),
                            market,
                            textView_text,
                            textView_couponPocketconfirmText,
                            textView_marketName,
                            textView_description,
                            button_confirm,
                            imageView_image);
                    receiveCoupon.execute();
                } else if (button_confirm.getText().equals("마침")) {
                    finish();
                } else {
                    Intent couponPocketIntent = new Intent(getApplicationContext(),
                            CouponPocketActivity.class);
                    startActivity(couponPocketIntent);
                }
            }
        });
    }


}
