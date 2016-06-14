package com.haffle.superready.goods_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haffle.superready.R;
import com.haffle.superready.analytics.KakaoAnalytics;
import com.haffle.superready.coupon.CouponPocketActivity;
import com.haffle.superready.coupon.CouponReceiveActivity;
import com.haffle.superready.db.GoodsDBManager;
import com.haffle.superready.flyer.GoodsFlyerActivity;
import com.haffle.superready.goods_detail.GoodsDetailActivity;
import com.haffle.superready.item.Campaign;
import com.haffle.superready.item.CouponCard;
import com.haffle.superready.item.Flyer;
import com.haffle.superready.item.Goods;
import com.haffle.superready.item.GoodsFavorite;
import com.haffle.superready.item.Market;
import com.haffle.superready.item.NoCampaign;
import com.haffle.superready.item.TwoGoods;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GoodsListViewAdapter extends BaseAdapter {
    ArrayList<Object> card;
    Context context;
    Market market;
    boolean flagFavorite;
    boolean flagRegion;
    GoodsDBManager goodsDBManager;
    ArrayList<GoodsFavorite> dbGoodsFavorite;
    GoodsCalRestTime goodsCalRestTime;
    public static KinsightSession kinsightSession;

    GoodsListViewAdapter(Context context, Market market, boolean flagFavorite, boolean flagRegion) {
        this.context = context;
        this.market = market;
        this.flagFavorite = flagFavorite;
        this.flagRegion = flagRegion;
        card = new ArrayList<Object>();
        kinsightSession = new KinsightSession(context);

        goodsDBManager = new GoodsDBManager(context, "GOODS.db", null, 1);
        dbGoodsFavorite = goodsDBManager.select();
        goodsCalRestTime = new GoodsCalRestTime(context);
    }

    @Override
    public int getCount() {
        return card.size();
    }

    @Override
    public Object getItem(int position) {
        return card.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TimeSaleGoodsHolder timeSaleGoodsHolder;
        NormalGoodsHolder normalGoodsHolder;
        FlyerHolder flyerHolder;
        CampaignHolder campaignHolder;
        NoCampaignHolder noCampaignHolder;
        final CouponCardHolder couponCardHolder;

        if (getItem(position) instanceof NoCampaign) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_no_goods, parent, false);
                noCampaignHolder = new NoCampaignHolder();

                convertView.setTag(noCampaignHolder);
            } else {
                noCampaignHolder = (NoCampaignHolder) convertView.getTag();
            }

        } else if (getItem(position) instanceof Campaign) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_campaign_division, parent, false);

                campaignHolder = new CampaignHolder();

                campaignHolder.campaignsDivision_name = (TextView) convertView.findViewById(R.id.item_campaignsDivision_name);
                campaignHolder.campaignsDivision_period = (TextView) convertView.findViewById(R.id.item_campaignsDivision_period);

                convertView.setTag(campaignHolder);
            } else {
                campaignHolder = (CampaignHolder) convertView.getTag();
            }

            Campaign campaign = (Campaign) card.get(position);

            campaignHolder.campaignsDivision_name.setText(campaign.getName());
            campaignHolder.campaignsDivision_period.setText(dateParsing(campaign.getStart(), campaign.getEnd()));

        } else if (getItem(position) instanceof Flyer) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_goods_flyer, parent, false);

                flyerHolder = new FlyerHolder();

                flyerHolder.layout = (LinearLayout) convertView.findViewById(R.id.item_goodsFlyer_layout);
                flyerHolder.name = (TextView) convertView.findViewById(R.id.item_goodsFlyer_name);
                flyerHolder.image = (ImageView) convertView.findViewById(R.id.item_goodsFlyer_image);

                convertView.setTag(flyerHolder);
            } else {
                flyerHolder = (FlyerHolder) convertView.getTag();
            }

            final Flyer flyer = (Flyer) card.get(position);

            flyerHolder.name.setText(flyer.getName());
            Glide.with(context)
                    .load(flyer.getImage())
                    .into(flyerHolder.image);

            flyerHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_flyerActivity = new Intent(context, GoodsFlyerActivity.class);
                    intent_flyerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                    intent_flyerActivity.putExtra("FLYERCAMPAIGN", flyer.getCampaign());


                    context.startActivity(intent_flyerActivity);
                }
            });

        } else if (getItem(position) instanceof Goods) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_timesale_goods, parent, false);

                timeSaleGoodsHolder = new TimeSaleGoodsHolder();

                timeSaleGoodsHolder.layout = (LinearLayout) convertView.findViewById(R.id.item_timeSaleGoods_layout);
                timeSaleGoodsHolder.prePrice = (TextView) convertView.findViewById(R.id.item_timeSaleGoods_prePrice);
                timeSaleGoodsHolder.curPrice = (TextView) convertView.findViewById(R.id.item_timeSaleGoods_curPrice);
                timeSaleGoodsHolder.name = (TextView) convertView.findViewById(R.id.item_timeSaleGoods_name);
                timeSaleGoodsHolder.description = (TextView) convertView.findViewById(R.id.item_timeSaleGoods_description);
                timeSaleGoodsHolder.restTime = (TextView) convertView.findViewById(R.id.item_timeSaleGoods_restTime);
                timeSaleGoodsHolder.priceExplanation = (TextView) convertView.findViewById(R.id.item_timesaleGoods_priceExplanation);
                timeSaleGoodsHolder.image = (ImageView) convertView.findViewById(R.id.item_timeSaleGoods_image);
                timeSaleGoodsHolder.favorite = (ImageView) convertView.findViewById(R.id.item_timeSaleGoods_favorite);
                timeSaleGoodsHolder.discountRate = (ImageView) convertView.findViewById(R.id.item_timeSaleGoods_discountRate);

                convertView.setTag(timeSaleGoodsHolder);
            } else {
                timeSaleGoodsHolder = (TimeSaleGoodsHolder) convertView.getTag();
            }

            Goods goods = (Goods) card.get(position);
            timeSaleGoodsHolder.prePrice.setText(addComma(goods.getPrePrice()));
            // 이전 가격에 가로줄
            timeSaleGoodsHolder.prePrice.setPaintFlags(timeSaleGoodsHolder.prePrice.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            timeSaleGoodsHolder.curPrice.setText(addComma(goods.getCurPrice()));
            timeSaleGoodsHolder.name.setText(goods.getName());
            timeSaleGoodsHolder.description.setText(goods.getDescription());
            goodsCalRestTime.addRestTime(goods, timeSaleGoodsHolder.restTime);   // 남은 시간을 위해 추가
            if (goods.getPriceExplanation().equals("")) {
                timeSaleGoodsHolder.priceExplanation.setText("슈퍼레디가");
            } else {
                timeSaleGoodsHolder.priceExplanation.setText(goods.getPriceExplanation());
            }
            Glide.with(context)
                    .load(goods.getImage())
                    .into(timeSaleGoodsHolder.image);

            String discountRate;
            int drawableId;

            // discountRate를 이용해 이미지 이름을 가진 string을 생성
            if (goods.getDiscountRate().equals("")) {
                discountRate = "@drawable/sale_percent_rate0";
            } else {
                discountRate = "@drawable/sale_percent_rate" + goods.getDiscountRate();
            }
            drawableId = context.getResources().getIdentifier(discountRate, "drawable", context.getPackageName()); // 생성한 string을 이용해 drawableId를 생성
            timeSaleGoodsHolder.discountRate.setBackgroundResource(drawableId);

            // 처음에 favorite이 안되있는 상태로 초기화
            timeSaleGoodsHolder.favorite.setBackgroundResource(R.drawable.likecircle_withshadow);
            goods.setFavorite(false);

            for (int i = 0; i < dbGoodsFavorite.size(); i++) {
                if (dbGoodsFavorite.get(i).getId().equals(goods.getId())) {
                    timeSaleGoodsHolder.favorite.setBackgroundResource(R.drawable.full_likecircle_withshadow);
                    goods.setFavorite(true);
                }
            }

            favoriteClickListener(timeSaleGoodsHolder.favorite,
                    goods, goods.getCampaign().getEnd(), true);
            goodsClickListener(timeSaleGoodsHolder.layout, goods, goods.getCampaign().getEnd());

        } else if (getItem(position) instanceof TwoGoods) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_normal_goods, parent, false);

                normalGoodsHolder = new NormalGoodsHolder();

                normalGoodsHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.item_normalGoods_left_layout);
                normalGoodsHolder.leftPrePrice = (TextView) convertView.findViewById(R.id.item_normalGoods_left_prePrice);
                normalGoodsHolder.leftCurPrice = (TextView) convertView.findViewById(R.id.item_normalGoods_left_curPrice);
                normalGoodsHolder.leftName = (TextView) convertView.findViewById(R.id.item_normalGoods_left_name);
                normalGoodsHolder.leftDescription = (TextView) convertView.findViewById(R.id.item_normalGoods_left_description);
                normalGoodsHolder.leftPriceExplanation = (TextView) convertView.findViewById(R.id.item_normalGoods_left_priceExplanation);
                normalGoodsHolder.leftImage = (ImageView) convertView.findViewById(R.id.item_normalGoods_left_image);
                normalGoodsHolder.leftFavorite = (ImageView) convertView.findViewById(R.id.item_normalGoods_left_favorite);
                normalGoodsHolder.leftDiscountRate = (ImageView) convertView.findViewById(R.id.item_normalGoods_left_discountRate);

                normalGoodsHolder.rightLayout = (LinearLayout) convertView.findViewById(R.id.item_normalGoods_right_layout);
                normalGoodsHolder.rightPrePrice = (TextView) convertView.findViewById(R.id.item_normalGoods_right_prePrice);
                normalGoodsHolder.rightCurPrice = (TextView) convertView.findViewById(R.id.item_normalGoods_right_curPrice);
                normalGoodsHolder.rightName = (TextView) convertView.findViewById(R.id.item_normalGoods_right_name);
                normalGoodsHolder.rightDescription = (TextView) convertView.findViewById(R.id.item_normalGoods_right_description);
                normalGoodsHolder.rightPriceExplanation = (TextView) convertView.findViewById(R.id.item_normalGoods_right_priceExplanation);
                normalGoodsHolder.rightImage = (ImageView) convertView.findViewById(R.id.item_normalGoods_right_image);
                normalGoodsHolder.rightFavorite = (ImageView) convertView.findViewById(R.id.item_normalGoods_right_favorite);
                normalGoodsHolder.rightDiscountRate = (ImageView) convertView.findViewById(R.id.item_normalGoods_right_discountRate);

                convertView.setTag(normalGoodsHolder);
            } else {
                normalGoodsHolder = (NormalGoodsHolder) convertView.getTag();
            }

            TwoGoods twoGoods = (TwoGoods) card.get(position);
            Goods leftGoods = twoGoods.getLeftGoods();
            Goods rightGoods = twoGoods.getRightGoods();

            normalGoodsHolder.leftPrePrice.setText(addComma(leftGoods.getPrePrice()));
            normalGoodsHolder.leftPrePrice.setPaintFlags(normalGoodsHolder.leftPrePrice.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            normalGoodsHolder.leftCurPrice.setText(addComma(leftGoods.getCurPrice()));
            normalGoodsHolder.leftName.setText(leftGoods.getName());
            normalGoodsHolder.leftDescription.setText(leftGoods.getDescription());
            if (leftGoods.getPriceExplanation().equals("")) {
                normalGoodsHolder.leftPriceExplanation.setText("슈퍼레디가");
            } else {
                normalGoodsHolder.leftPriceExplanation.setText(leftGoods.getPriceExplanation());
            }
            Glide.with(context)
                    .load(leftGoods.getImage())
                    .into(normalGoodsHolder.leftImage);

            String discountRate;
            int drawableId;

            // discountRate를 이용해 이미지 이름을 가진 string을 생성
            if (leftGoods.getDiscountRate().equals("")) {
                discountRate = "@drawable/sale_percent_rate0";
            } else {
                discountRate = "@drawable/sale_percent_rate" + leftGoods.getDiscountRate();
            }
            drawableId = context.getResources().getIdentifier(discountRate, "drawable", context.getPackageName()); // 생성한 string을 이용해 drawableId를 생성
            normalGoodsHolder.leftDiscountRate.setBackgroundResource(drawableId);

            // 처음에 favorite가 안되있는 상태로 초기화
            normalGoodsHolder.leftFavorite.setBackgroundResource(R.drawable.btn_likered);
            leftGoods.setFavorite(false);

            for (int i = 0; i < dbGoodsFavorite.size(); i++) {
                if (leftGoods.getId().equals(dbGoodsFavorite.get(i).getId())) {
                    normalGoodsHolder.leftFavorite.setBackgroundResource(R.drawable.goods_card_btn_likefull);
                    leftGoods.setFavorite(true);
                }
            }

            favoriteClickListener(normalGoodsHolder.leftFavorite,
                    leftGoods, leftGoods.getCampaign().getEnd(), false);
            goodsClickListener(normalGoodsHolder.leftLayout, leftGoods, leftGoods.getCampaign().getEnd());

            if (rightGoods != null) {
                normalGoodsHolder.rightLayout.setVisibility(View.VISIBLE);

                normalGoodsHolder.rightPrePrice.setText(addComma(rightGoods.getPrePrice()));
                normalGoodsHolder.rightPrePrice.setPaintFlags(normalGoodsHolder.rightPrePrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                normalGoodsHolder.rightCurPrice.setText(addComma(rightGoods.getCurPrice()));
                normalGoodsHolder.rightName.setText(rightGoods.getName());
                normalGoodsHolder.rightDescription.setText(rightGoods.getDescription());
                if (rightGoods.getPriceExplanation().equals("")) {
                    normalGoodsHolder.rightPriceExplanation.setText("슈퍼레디가");
                } else {
                    normalGoodsHolder.rightPriceExplanation.setText(rightGoods.getPriceExplanation());
                }
                Glide.with(context)
                        .load(rightGoods.getImage())
                        .into(normalGoodsHolder.rightImage);

                // discountRate를 이용해 이미지 이름을 가진 string을 생성
                if (rightGoods.getDiscountRate().equals("")) {
                    discountRate = "@drawable/sale_percent_rate0";
                } else {
                    discountRate = "@drawable/sale_percent_rate" + rightGoods.getDiscountRate();
                }
                drawableId = context.getResources().getIdentifier(discountRate, "drawable", context.getPackageName()); // 생성한 string을 이용해 drawableId를 생성
                normalGoodsHolder.rightDiscountRate.setBackgroundResource(drawableId);

                // 처음에 favorite가 안되있는 상태로 초기화
                normalGoodsHolder.rightFavorite.setBackgroundResource(R.drawable.btn_likered);
                rightGoods.setFavorite(false);

                for (int i = 0; i < dbGoodsFavorite.size(); i++) {
                    if (rightGoods.getId().equals(dbGoodsFavorite.get(i).getId())) {
                        normalGoodsHolder.rightFavorite.setBackgroundResource(R.drawable.goods_card_btn_likefull);
                        rightGoods.setFavorite(true);
                    }
                }

                favoriteClickListener(normalGoodsHolder.rightFavorite,
                        rightGoods, rightGoods.getCampaign().getEnd(), false);
                goodsClickListener(normalGoodsHolder.rightLayout, rightGoods, rightGoods.getCampaign().getEnd());
            }

        } else if (getItem(position) instanceof CouponCard) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_coupon_card, parent, false);

                couponCardHolder = new CouponCardHolder();
                couponCardHolder.text = (TextView)convertView.findViewById(R.id.item_couponCard_text);
                couponCardHolder.button = (Button)convertView.findViewById(R.id.item_couponCard_button);

                convertView.setTag(couponCardHolder);
            } else {
                couponCardHolder = (CouponCardHolder) convertView.getTag();
            }

            CouponCard couponCard = (CouponCard) card.get(position);
            couponCardHolder.text.setText(couponCard.getText());
            couponCardHolder.button.setText(couponCard.getButton());

            couponCardHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(couponCardHolder.button.getText().equals("쿠폰받기")) {
                        Intent couponReceiveIntent = new Intent(context, CouponReceiveActivity.class);
                        couponReceiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                        couponReceiveIntent.putExtra("MARKET", market);
                        context.startActivity(couponReceiveIntent);
                    } else {
                        Intent couponPocketIntent = new Intent(context, CouponPocketActivity.class);
                        couponPocketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(couponPocketIntent);
                    }
                }
            });
        }

        return convertView;
    }

    // timeSale 상품을 추가한다
    void addTimeSaleGoods(ArrayList<Goods> timeSaleGoods) {

        for (int i = 0; i < timeSaleGoods.size(); i++) {
            card.add(timeSaleGoods.get(i));
        }
    }

    // normal 상품을 추가한다
    // UI상으로 normal 상품은 2개가 한 layout에 뿌려지기 때문에 TwoGoods 객체로 묶어서 추가한다.
    void addNormalGoods(ArrayList<Goods> normalGoods) {

        for (int i = 0; i < normalGoods.size(); i += 2) {
            card.add(convertTwoGoods(normalGoods, i));
        }
    }

    void addNoCampaign(NoCampaign noCampaign) {
        card.add(noCampaign);
    }

    // image 상품을 추가한다
    void addFlyer(ArrayList<Flyer> flyer) {

        card.add(flyer.get(0));
    }

    // campaign 구분자를 위해 capaign을 추가한다.
    void addCampaign(Campaign campaign) {
        card.add(campaign);
    }

    void addCouponCard(CouponCard couponCard) {
        card.add(couponCard);
    }

    private String addComma(String price) {
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

    class TimeSaleGoodsHolder {
        LinearLayout layout;
        TextView prePrice;
        TextView curPrice;
        TextView name;
        TextView description;
        TextView restTime;
        TextView priceExplanation;
        ImageView image;
        ImageView favorite;
        ImageView discountRate;
    }

    class NormalGoodsHolder {
        LinearLayout leftLayout;
        TextView leftPrePrice;
        TextView leftCurPrice;
        TextView leftName;
        TextView leftDescription;
        TextView leftPriceExplanation;
        ImageView leftImage;
        ImageView leftFavorite;
        ImageView leftDiscountRate;

        LinearLayout rightLayout;
        TextView rightPrePrice;
        TextView rightCurPrice;
        TextView rightName;
        TextView rightDescription;
        TextView rightPriceExplanation;
        ImageView rightImage;
        ImageView rightFavorite;
        ImageView rightDiscountRate;
    }

    class FlyerHolder {
        LinearLayout layout;
        ImageView image;
        TextView name;
    }

    class CampaignHolder {
        TextView campaignsDivision_name;
        TextView campaignsDivision_period;
    }

    class NoCampaignHolder {

    }

    class CouponCardHolder {
        TextView text;
        Button button;
    }

    TwoGoods convertTwoGoods(ArrayList<Goods> goods, int i) {
        TwoGoods twoGoods = new TwoGoods();

        if (i + 1 < goods.size()) {
            twoGoods.setLeftGoods(goods.get(i));
            twoGoods.setRightGoods(goods.get(i + 1));
        } else {
            twoGoods.setLeftGoods(goods.get(i));
        }

        return twoGoods;
    }

    void goodsClickListener(LinearLayout item, final Goods goods,
                            final String endTime) {

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_goodsDetailActivity = new Intent(context, GoodsDetailActivity.class);
                intent_goodsDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // activity가 아닌 class에서 activity를 띄우기 위한 flag
                intent_goodsDetailActivity.putExtra("GOODS", goods);
                intent_goodsDetailActivity.putExtra("MARKETNAME", market.getName());
                intent_goodsDetailActivity.putExtra("MARKETID", market.getId());
                intent_goodsDetailActivity.putExtra("ENDTIME", endTime);

                if (flagFavorite && flagRegion) {
                    KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                    kakaoAnalytics.searchGoods(kinsightSession, context, market, true, true);
                } else if (!flagFavorite && flagRegion) {
                    KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                    kakaoAnalytics.searchGoods(kinsightSession, context, market, false, true);
                } else {
                    KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                    kakaoAnalytics.searchGoods(kinsightSession, context, market, true, false);
                }

                context.startActivity(intent_goodsDetailActivity);
            }
        });
    }

    void favoriteClickListener(final ImageView imageView_favorite,
                               final Goods goods,
                               final String endTime,
                               final boolean flag_timeSaleGoods) {
        imageView_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.isFavorite()) {
                    if (flag_timeSaleGoods) {
                        imageView_favorite.setBackgroundResource(R.drawable.likecircle_withshadow);
                    } else {
                        imageView_favorite.setBackgroundResource(R.drawable.btn_likered);
                    }

//                    cancelAlarm(goods);    // 걸려있는 알람을 해제한다.

                    goodsDBManager.deleteWithGoodsId(Integer.parseInt(goods.getId()));

                    if (flag_timeSaleGoods) {
                        KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                        kakaoAnalytics.deleteFavoriteGoods(kinsightSession, context, true, false, endTime, goods.getCurPrice());
                    } else {
                        KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                        kakaoAnalytics.deleteFavoriteGoods(kinsightSession, context, true, true, endTime, goods.getCurPrice());
                    }

                    Toast.makeText(context, "장보기 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    goods.setFavorite(false);
                } else {
                    if (flag_timeSaleGoods) {    // time sale 상품일 경우
                        imageView_favorite.setBackgroundResource(R.drawable.full_likecircle_withshadow);

//                        setAlarm(goods, endTime);    // 하루 남았을 때 울리는 알림을 건다.

                        goodsDBManager.insert(new GoodsFavorite(
                                goods.getId(),
                                goods.getName(),
                                goods.getDescription(),
                                market.getName(),
                                market.getId(),
                                goods.getCurPrice(),
                                endTime,
                                goods.getImage(),
                                goods.isTimeSale()));

                        KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                        kakaoAnalytics.setFavoriteGoods(kinsightSession, context, true, false, endTime, market, goods.getCurPrice());

                    } else {                    // basic sale 상품일 경우
                        imageView_favorite.setBackgroundResource(R.drawable.goods_card_btn_likefull);

                        goodsDBManager.insert(new GoodsFavorite(
                                goods.getId(),
                                goods.getName(),
                                goods.getDescription(),
                                market.getName(),
                                market.getId(),
                                goods.getCurPrice(),
                                endTime,
                                goods.getImage(),
                                goods.isTimeSale()));

                        KakaoAnalytics kakaoAnalytics = KakaoAnalytics.getKakaoAnalytics();
                        kakaoAnalytics.setFavoriteGoods(kinsightSession, context, true, true, endTime, market, goods.getCurPrice());
                    }

                    Toast.makeText(context, "장보기 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    goods.setFavorite(true);
                }
            }
        });
    }

    void setChangedData() {
        dbGoodsFavorite = goodsDBManager.select();
    }

    private String dateParsing(String start, String end) {
        StringTokenizer startStr = new StringTokenizer(start, "- T : .");
        String[] startDate = new String[7];
        StringTokenizer endStr = new StringTokenizer(end, "- T : .");
        String[] endDate = new String[7];

        for(int i=0; startStr.hasMoreTokens(); i++) {
            startDate[i] = startStr.nextToken();
        }

        for(int i=0; endStr.hasMoreTokens(); i++) {
            endDate[i] = endStr.nextToken();
        }

        return startDate[1] + "월 " + startDate[2] + "일 부터 " +
                endDate[1] + "월" + endDate[2] + "일 까지";
    }
}