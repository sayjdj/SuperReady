package com.haffle.superready.coupon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haffle.superready.R;
import com.haffle.superready.item.Coupon;
import com.haffle.superready.manager.InfoManager;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class CouponPocketViewAdapter extends BaseAdapter {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    ArrayList<Coupon> coupons;
    Context context;
    Activity activity;
    CouponPocketViewAdapter availableCouponPocketViewAdapter;
    CouponPocketViewAdapter usedCouponPocketViewAdapter;

    CouponPocketViewAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        coupons = new ArrayList<Coupon>();
    }

    @Override
    public int getCount() {
        return coupons.size();
    }

    @Override
    public Object getItem(int position) {
        return coupons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CouponHolder couponHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_coupon, parent, false);
            couponHolder = new CouponHolder();

            couponHolder.opacity = (RelativeLayout) convertView.findViewById(R.id.item_coupon_opacity);
            couponHolder.marketName = (TextView) convertView.findViewById(R.id.item_coupon_marketName);
            couponHolder.name = (TextView) convertView.findViewById(R.id.item_coupon_name);
            couponHolder.description = (TextView) convertView.findViewById(R.id.item_coupon_description);
            couponHolder.period = (TextView) convertView.findViewById(R.id.item_coupon_period);
            couponHolder.image = (ImageView) convertView.findViewById(R.id.item_coupon_image);
            couponHolder.use = (Button) convertView.findViewById(R.id.item_coupon_use);

            convertView.setTag(couponHolder);
        } else {
            couponHolder = (CouponHolder) convertView.getTag();
        }

        final Coupon coupon = (Coupon) coupons.get(position);
        if (coupon.getType() == 0) { // 사용 가능
            couponHolder.opacity.setBackgroundColor(Color.parseColor("#00ffffff"));
        } else if (coupon.getType() == 1) {   // 사용 됨
            couponHolder.use.setBackgroundColor(Color.parseColor("#839cc2"));
            couponHolder.use.setText("사용됨");
            couponHolder.opacity.setBackgroundColor(Color.parseColor("#88ffffff"));
        } else {    // 기간 만료
            couponHolder.use.setBackgroundColor(Color.parseColor("#b97c7c"));
            couponHolder.use.setText("유효기간 만료");
            couponHolder.opacity.setBackgroundColor(Color.parseColor("#88ffffff"));
        }

        couponHolder.marketName.setText(coupon.getMarketName());
        couponHolder.name.setText(coupon.getName());
        couponHolder.description.setText(coupon.getDescription());
        couponHolder.period.setText(calRestTime(coupon.getExpiration()));

        Glide.with(context)
                .load(coupon.getImage())
                .into(couponHolder.image);

        couponHolder.use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("쿠폰사용");
                builder.setMessage("정말 사용하시겠습니까?\n" +
                        "매장 점주만 눌러주세요");
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Thread thread = new Thread() {
                                            @Override
                                            public void run() {
                                                couponUse(coupon.getId());
                                            }
                                        };
                                        thread.start();
                                    }
                                }).create().show();
            }
        });

        return convertView;
    }

    // coupon을 추가한다
    void addCoupon(Coupon coupon) {
        this.coupons.add(coupon);
    }

    class CouponHolder {
        RelativeLayout opacity;
        TextView marketName;
        TextView name;
        TextView description;
        TextView period;
        ImageView image;
        Button use;
    }

    private String calRestTime(String string) {
        StringTokenizer str = new StringTokenizer(string, "- T : .");
        String[] date = new String[7];

        for (int i = 0; str.hasMoreTokens(); i++) {
            date[i] = str.nextToken();
        }

        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                Integer.parseInt(date[4]), Integer.parseInt(date[5]));    // 연, 월(-1을 해야함), 일, 시간, 분, 초
        cal.add(Calendar.HOUR, 9);

        Calendar curCal = Calendar.getInstance();

        if (cal.get(Calendar.YEAR) == curCal.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == curCal.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == curCal.get(Calendar.DAY_OF_MONTH)) {
            return "오늘까지 사용가능";
        } else {
            return (cal.get(Calendar.MONTH) + 1) + "월 " +
                    cal.get(Calendar.DAY_OF_MONTH) + "일까지 사용가능";
        }
    }

    private void couponUse(String couponId) {

        String url = InfoManager.URL_COUPON_USE +
                couponId + "?access_token=" +
                InfoManager.userToken +
                "&stage=y&force=y";

        OkHttpClient client = new OkHttpClient();

        try {
            RequestBody body = RequestBody.create(JSON, "");
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteCoupons() {
        Log.e("wkdgusdn3", "1");
        coupons.clear();
        Log.e("wkdgusdn3", "2");
    }
}