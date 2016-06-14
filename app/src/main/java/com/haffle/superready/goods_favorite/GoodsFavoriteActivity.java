package com.haffle.superready.goods_favorite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.db.GoodsDBManager;
import com.haffle.superready.goods_detail.GoodsDetailActivity;
import com.haffle.superready.item.GoodsFavorite;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class GoodsFavoriteActivity extends Activity {

    ActivityManager activityManager = ActivityManager.getInstance();

    Activity activity;
    LayoutInflater inflater;
    LinearLayout layout;
    GoodsDBManager dbManager;
    RelativeLayout layout_empty;
    RelativeLayout layout_complete;
    ArrayList<TextView> arrayList_textView = new ArrayList<TextView>();
    ArrayList<Calendar> arrayList_endTime = new ArrayList<Calendar>();
    Context context;
    List<GoodsFavorite> bm;
    public static KinsightSession kinsightSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_goods_favorite);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_goods_favorite);

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
        }

        activityManager.addActivity(this);
        activity = this;

        context = getApplicationContext();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) findViewById(R.id.goodsFavorite_layout);

        setView();
        setClickListener();

        dbManager = new GoodsDBManager(getApplicationContext(), "GOODS.db", null, 1);

        getGoodsFavoriteList();

        GoodsFavoriteCalRestTime calTime = new GoodsFavoriteCalRestTime(context, arrayList_textView, arrayList_endTime);
    }

    public void onResume() {
        super.onResume();

        if (InfoManager.flagKakao) {
            kinsightSession = new KinsightSession(getApplicationContext());
            kinsightSession.open();
            kinsightSession.tagScreen("관심상품목록");
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

    void setView() {
        layout_empty = (RelativeLayout) findViewById(R.id.titlebar_goodsFavorite_empty);
        layout_complete = (RelativeLayout) findViewById(R.id.titlebar_goodsFavorite_complete);
    }

    void setClickListener() {
        layout_empty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder gsDialog = new AlertDialog.Builder(activity);
                gsDialog.setMessage("관심 상품을 모두 삭제하시겠습니까?");
                gsDialog.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                bm = dbManager.select();
                                int timeSaleGoodsCount = 0;
                                int endSaleGoodsCount = 0;

                                for (int i = 0; i < bm.size(); i++) {
                                    if (bm.get(i).isTimeSale()) {
                                        timeSaleGoodsCount++;
                                    }
                                    if (timeOut(bm.get(i).getEndTime())) {
                                        endSaleGoodsCount = 0;
                                    }
                                }

                                dbManager.allDelete();

                                KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                                kakaoAnalytics.emptyFavoriteGoods(kinsightSession, context, timeSaleGoodsCount, bm.size() - timeSaleGoodsCount,
                                        endSaleGoodsCount, bm.size() - endSaleGoodsCount);

                                layout.removeAllViews();
                                getGoodsFavoriteList();

                                Toast.makeText(getApplicationContext(), "모든 관심상품이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        return;
                                    }
                                }).create().show();
            }
        });

        layout_complete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_OK);

                finish();
            }
        });
    }

    // scroll view에 bookmart 추가
    void addGoods(final int i) {
        LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_goods_favorite, null);

        ImageView imageView_left_image = (ImageView) item.findViewById(R.id.item_goodsFavorite_left_image);
        TextView textView_left_name = (TextView) item.findViewById(R.id.item_goodsFavorite_left_name);
        TextView textView_left_measure = (TextView) item.findViewById(R.id.item_goodsFavorite_left_measure);
        TextView textView_left_martName = (TextView) item.findViewById(R.id.item_goodsFavorite_left_marketName);
        TextView textView_left_price = (TextView) item.findViewById(R.id.item_goodsFavorite_left_price);
        TextView textView_left_endTime = (TextView) item.findViewById(R.id.item_goodsFavorite_left_restTime);
        TextView textView_left_specialPrice = (TextView) item.findViewById(R.id.item_goodsFavorite_left_specialPrice);
        final ImageView imageView_left_delete = (ImageView) item.findViewById(R.id.item_goodsFavorite_left_delete);

        LinearLayout layer_left = (LinearLayout) item.findViewById(R.id.item_goodsFavorite_left_layout);

        Glide    // 이미지 받아오기
                .with(context)
                .load(bm.get(i).getImage())
                .into(imageView_left_image);

        textView_left_name.setText(bm.get(i).getName());
        textView_left_measure.setText(bm.get(i).getDescription());
        textView_left_martName.setText(bm.get(i).getMarketName());
        textView_left_price.setText(addComma(bm.get(i).getCurPrice()));

        if (bm.get(i).isTimeSale()) {
            textView_left_price.setTextColor(Color.parseColor("#ff1d1e"));
            arrayList_textView.add(textView_left_endTime);
            arrayList_endTime.add(dateParsing(bm.get(i).getEndTime()));
        } else {
            textView_left_specialPrice.setVisibility(View.GONE);
        }

        imageView_left_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder gsDialog = new AlertDialog.Builder(activity);
                gsDialog.setMessage("삭제하시겠습니까?");
                gsDialog.setNegativeButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.delete(bm.get(i).getDbId());

                                layout.removeAllViews();
                                getGoodsFavoriteList();

                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        return;
                                    }
                                }).create().show();
            }
        });

        layer_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_GoodsDetailActivity = new Intent(context, GoodsDetailActivity.class);
                intent_GoodsDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                intent_GoodsDetailActivity.putExtra("GOODS", bm.get(i));
                intent_GoodsDetailActivity.putExtra("MARKETNAME", bm.get(i).getMarketName());
                intent_GoodsDetailActivity.putExtra("MARKETID", bm.get(i).getMarketId());
                intent_GoodsDetailActivity.putExtra("ENDTIME", bm.get(i).getEndTime());

                context.startActivity(intent_GoodsDetailActivity);
            }
        });

        if (i + 1 < bm.size()) {
            ImageView imageView_right_image = (ImageView) item.findViewById(R.id.item_goodsFavorite_right_image);
            TextView textView_right_name = (TextView) item.findViewById(R.id.item_goodsFavorite_right_name);
            TextView textView_right_measure = (TextView) item.findViewById(R.id.item_goodsFavorite_right_measure);
            TextView textView_right_martName = (TextView) item.findViewById(R.id.item_goodsFavorite_right_marketName);
            TextView textView_right_price = (TextView) item.findViewById(R.id.item_goodsFavorite_right_price);
            TextView textView_right_endTime = (TextView) item.findViewById(R.id.item_goodsFavorite_right_restTime);
            TextView textView_right_specialPrice = (TextView) item.findViewById(R.id.item_goodsFavorite_right_specialPrice);
            final ImageView imageView_right_delete = (ImageView) item.findViewById(R.id.item_goodsFavorite_right_delete);

            LinearLayout layer_right = (LinearLayout) item.findViewById(R.id.item_goodsFavorite_right_layout);
            layer_right.setVisibility(View.VISIBLE);

            Glide    // 이미지 받아오기
                    .with(context)
                    .load(bm.get(i + 1).getImage())
                    .into(imageView_right_image);

            textView_right_name.setText(bm.get(i + 1).getName());
            textView_right_measure.setText(bm.get(i + 1).getDescription());
            textView_right_martName.setText(bm.get(i + 1).getMarketName());
            textView_right_price.setText(addComma(bm.get(i + 1).getCurPrice()));

            if (bm.get(i + 1).isTimeSale()) {
                textView_right_price.setTextColor(Color.parseColor("#ff1d1e"));
                arrayList_textView.add(textView_right_endTime);
                arrayList_endTime.add(dateParsing(bm.get(i + 1).getEndTime()));
            } else {
                textView_right_specialPrice.setVisibility(View.GONE);
            }

            imageView_right_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder gsDialog = new AlertDialog.Builder(activity);
                    gsDialog.setMessage("삭제하시겠습니까?");
                    gsDialog.setNegativeButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dbManager.delete(bm.get(i + 1).getDbId());

                                    layout.removeAllViews();
                                    getGoodsFavoriteList();

                                    Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            return;
                                        }
                                    }).create().show();
                }
            });

            layer_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_GoodsDetailActivity = new Intent(context, GoodsDetailActivity.class);
                    intent_GoodsDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                    intent_GoodsDetailActivity.putExtra("GOODS", bm.get(i + 1));
                    intent_GoodsDetailActivity.putExtra("MARKETNAME", bm.get(i + 1).getMarketName());
                    intent_GoodsDetailActivity.putExtra("ENDTIME", bm.get(i + 1).getEndTime());

                    context.startActivity(intent_GoodsDetailActivity);
                }
            });
        }

        layout.addView(item);    // scrollView에 추가
    }

    void getGoodsFavoriteList() {
        bm = dbManager.select();

        for (int i = 0; i < bm.size(); i += 2) {
            addGoods(i);
        }
    }

    Calendar dateParsing(String string) {
        StringTokenizer str = new StringTokenizer(string, "- T : .");
        String[] date = new String[7];

        for (int i = 0; str.hasMoreTokens(); i++) {
            date[i] = str.nextToken();
        }

        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                Integer.parseInt(date[4]), Integer.parseInt(date[5]));    // 연, 월(-1해야함), 일, 시간, 분, 초
        cal.add(Calendar.HOUR, 9);

        return cal;
    }

    String addComma(String price) {
        DecimalFormat df = new DecimalFormat("###,###,###");

        if (price.equals("0")) {
            return "";
        }

        String parsingPrice;
        try {
            parsingPrice = df.format(Long.parseLong(price)) + "원";
        } catch (Exception e) {
            parsingPrice = "";
        }
        return parsingPrice;
    }

    private boolean timeOut(String restTime) {

        Calendar curCalendar = Calendar.getInstance();
        Calendar restCalendar = dateParsing(restTime);

        if (restCalendar.getTimeInMillis() - curCalendar.getTimeInMillis() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
