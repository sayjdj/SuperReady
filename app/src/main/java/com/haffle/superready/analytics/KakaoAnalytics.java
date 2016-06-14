package com.haffle.superready.analytics;

import android.content.Context;

import com.haffle.superready.item.Market;
import com.haffle.superready.manager.InfoManager;
import com.kakao.kinsight.sdk.android.KinsightSession;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KakaoAnalytics {

    private KakaoAnalytics() {};

    private static KakaoAnalytics kakaoAnalytics = new KakaoAnalytics();

    public static KakaoAnalytics getKakaoAnalytics() {
        return kakaoAnalytics;
    }

    public void searchKakaoLink(KinsightSession kinsightSession, Context context, Market market) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude,
                    InfoManager.longitude,
                    market.getLatitude(),
                    market.getLongitude()));
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("카카오링크 조회", attributes);
        }
    }

    public void setFavoriteMarket(KinsightSession kinsightSession, Context context, Market market) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude,
                    InfoManager.longitude,
                    market.getLatitude(),
                    market.getLongitude()));
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("관심마켓 등록", attributes);
        }
    }

    public void deleteFavoriteMarket(KinsightSession kinsightSession, Context context, Market market) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude,
                    InfoManager.longitude,
                    market.getLatitude(),
                    market.getLongitude()));
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("관심마켓 삭제", attributes);
        }
    }

    public void changeLocation(KinsightSession kinsightSession, Context context,
                               String curLat, String curLng,
                               String setLat, String setLng) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 설정한 위치 사이 거리", calDistance(curLat, curLng, setLat, setLng));
            kinsightSession.addEvent("위치 변경 완료", attributes);
        }
    }

    public void sendUserInfo(KinsightSession kinsightSession, Context context) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("지역", InfoManager.address);
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("사용자 정보 제출", attributes);
        }
    }

    public void startCustomerSupport(KinsightSession kinsightSession, Context context) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("지역", InfoManager.address);
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("고객지원 시작", attributes);
        }
    }

    public void sendCustomerSupport(KinsightSession kinsightSession, Context context) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("지역", InfoManager.address);
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            kinsightSession.addEvent("고객지원 제출", attributes);
        }
    }

    public void setFavoriteGoods(KinsightSession kinsightSession, Context context,
                                 boolean flagView, boolean flagCampaign,
                                 String restTime, Market market, String price) {
        // flagView는 전단뷰일 경우에는 true, 상세뷰일 경우에는 false
        // flagCampaign은 일반 캠페인일 경우에는 true, 특가 캠페인일 경우에는 false
        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            if (flagView) {
                attributes.put("등록한 뷰", "전단뷰");
            } else {
                attributes.put("등록한 뷰", "상세뷰");
            }
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            if (flagCampaign) {
                attributes.put("캠페인 유형", "일반");
            } else {
                attributes.put("캠페인 유형", "특가");
            }
            attributes.put("캠페인 남은 시간", calRestHour(restTime));
            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude, InfoManager.longitude,
                    market.getLatitude(), market.getLongitude()));
            attributes.put("가격", Integer.parseInt(price));
            kinsightSession.addEvent("관심상품 등록", attributes);
        }
    }

    public void deleteFavoriteGoods(KinsightSession kinsightSession, Context context,
                                    boolean flagView, boolean flagCampaign,
                                    String restTime, String price) {

        // flagView는 전단뷰일 경우에는 true, 상세뷰일 경우에는 false
        // flagCampaign은 일반 캠페인일 경우에는 true, 특가 캠페인일 경우에는 false
        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            if (flagView) {
                attributes.put("삭제한 뷰", "전단뷰");
            } else {
                attributes.put("삭제한 뷰", "상세뷰");
            }
            if (flagCampaign) {
                attributes.put("캠페인 유형", "일반");
            } else {
                attributes.put("캠페인 유형", "특가");
            }
            attributes.put("캠페인 남은 시간", calRestHour(restTime));
            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("가격", Integer.parseInt(price));
            kinsightSession.addEvent("관심상품 삭제", attributes);
        }
    }

    public void emptyFavoriteGoods(KinsightSession kinsightSession, Context context,
                                   int timeSaleGoodsCount, int normalSaleGoodsCount,
                                   int endSaleGoodsCount, int restSaleGoodsCount) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("특가상품 갯수", timeSaleGoodsCount);
            attributes.put("일반상품 갯수", normalSaleGoodsCount);
            attributes.put("캠페인 기간 끝난 상품 갯수", endSaleGoodsCount);
            attributes.put("캠페인 기간 남은 상품 갯수", restSaleGoodsCount);
            kinsightSession.addEvent("관심상품 비우기", attributes);
        }
    }

    public void searchGoods(KinsightSession kinsightSession, Context context, Market market,
                            boolean flagFavorite, boolean flagRegion) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude, InfoManager.longitude,
                    market.getLatitude(), market.getLongitude()));
            if (flagFavorite && flagRegion) {
                attributes.put("마켓 조회패턴", "관심-근처");
            } else if (flagFavorite && !flagRegion) {
                attributes.put("마켓 조회패턴", "관심");
            } else {
                attributes.put("마켓 조회패턴", "근처");
            }
            kinsightSession.addEvent("전단상품목록 조회", attributes);
        }
    }

    public void searchGoodsDetail(KinsightSession kinsightSession, Context context, Market market,
                                  boolean flagFavorite, boolean flagRegion) {

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude, InfoManager.longitude,
                    market.getLatitude(), market.getLongitude()));
            if (flagFavorite && flagRegion) {
                attributes.put("마켓 조회패턴", "관심-근처");
            } else if (flagFavorite && !flagRegion) {
                attributes.put("마켓 조회패턴", "관심");
            } else {
                attributes.put("마켓 조회패턴", "근처");
            }
            kinsightSession.addEvent("상품상세 조회", attributes);
        }
    }

    public void shareKakao(KinsightSession kinsightSession, Context context,
                           boolean flagCampaign,
                           String restTime, Market market, String price) {

        // flagCampaign은 일반 캠페인일 경우에는 true, 특가 캠페인일 경우에는 false

        if (InfoManager.flagKakao) {
            final Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("앱 실행 횟수", Integer.parseInt(InfoManager.userVisitCount));
            attributes.put("현위치와 마켓사이 거리", calDistance(InfoManager.latitude, InfoManager.longitude,
                    market.getLatitude(), market.getLongitude()));
            attributes.put("사용자 나이", calAge(InfoManager.userAge));
            attributes.put("사용자 성별", changeGender(InfoManager.userGender));
            if (flagCampaign) {
                attributes.put("캠페인 유형", "일반");
            } else {
                attributes.put("캠페인 유형", "특가");
            }
            attributes.put("캠페인 남은 시간", calRestHour(restTime));
            attributes.put("가격", Integer.parseInt(price));
            kinsightSession.addEvent("카카오톡 공유", attributes);
        }
    }

    private String changeGender(String gender) {
        if (gender.equals("M")) {
            return "남자";
        } else if (gender.equals("F")) {
            return "여자";
        } else {
            return "알수없음";
        }
    }

    private int calDistance(String string_lat1, String string_lng1, String string_lat2, String string_lng2) {

        double lat1 = Double.parseDouble(string_lat1);
        double lng1 = Double.parseDouble(string_lng1);
        double lat2 = Double.parseDouble(string_lat2);
        double lng2 = Double.parseDouble(string_lng2);

        double theta, dist;
        theta = lng1 - lng2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        int divisionMeter = (int) dist / 500;
        int restMeter = (int) dist % 500;
        if (restMeter >= 250) {
            divisionMeter++;
        }

        divisionMeter *= 500;

        return divisionMeter;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    private int calRestHour(String restTime) {
        StringTokenizer str = new StringTokenizer(restTime, "- T : .");
        String[] date = new String[7];

        Calendar curCalendar = Calendar.getInstance();

        for (int i = 0; str.hasMoreTokens(); i++) {
            date[i] = str.nextToken();
        }

        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                Integer.parseInt(date[4]), Integer.parseInt(date[5]));    // 연, 월(-1해야함), 일, 시간, 분, 초

        int restHour = changeHour((cal.getTimeInMillis()
                - curCalendar.getTimeInMillis()) / 1000);

        return restHour;
    }

    private int changeHour(long restTime) {

        int restHour = 0;

        restHour += (restTime / (60 * 60 * 24)) * 24;
        restTime %= (60 * 60 * 24);
        restHour += (restTime / (60 * 60));

        return restHour;
    }

    private String calAge(String birth) {
        Calendar calendar = Calendar.getInstance();

        if (birth.equals("알수없음")) {
            return birth;
        }

        int curYear = calendar.get(Calendar.YEAR);
        int age = calendar.get(Calendar.YEAR) - Integer.parseInt(birth) + 1;
        String firstWord;
        String secondWord;

        if ((age % 10) < 3) {
            secondWord = "초반";
        } else if ((age % 10) < 7) {
            secondWord = "중반";
        } else {
            secondWord = "후반";
        }

        if ((age / 10) == 0) {
            return "10대 미만";
        } else if ((age / 10) >= 6) {
            return "60대 이상";
        } else {
            return (age / 10) + "0대 " + secondWord;
        }
    }
}