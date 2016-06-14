package com.haffle.superready.goods_list;

import android.content.Context;

import com.haffle.superready.db.GoodsDBManager;
import com.haffle.superready.item.GoodsFavorite;
import com.haffle.superready.item.NormalCampaign;
import com.haffle.superready.item.TimeSaleCampaign;

import java.util.ArrayList;

public class GoodsFavoriteCheck {

	ArrayList<TimeSaleCampaign> timeSaleCampaign;
	ArrayList<NormalCampaign> normalCampaign;
	ArrayList<GoodsFavorite> bm;
	Context context; 
	GoodsDBManager dbManager;

	GoodsFavoriteCheck(Context context,
			ArrayList<TimeSaleCampaign> timeSaleCampaign, ArrayList<NormalCampaign> normalCampaign) {

		this.context = context;
		this.timeSaleCampaign = timeSaleCampaign;
		this.normalCampaign = normalCampaign;

		dbManager = new GoodsDBManager(context, "GOODS.db", null, 1);
		bm = dbManager.select();	// Favorite의 모든 market을 가져온다

//		FavoriteCheck();
	}


//	void FavoriteCheck() {
//
//		for(int i=0; i<timeSaleCampaign.size(); i++) {
//
//			ArrayList<Goods> timeSaleGoods = timeSaleCampaign.get(i).getGoods();
//			ArrayList<TimeSaleGoodsUI> timeSaleGoodsUI = timeSaleCampaign.get(i).getuI();
//
//			for(int j=0; j<timeSaleGoods.size(); j++) {
//				timeSaleGoodsUI.get(j).getFavorite().setBackgroundResource(R.drawable.likecircle_withshadow);
//				timeSaleGoods.get(j).setFavorite(false);
//
//				for(int k=0; k<bm.size(); k++) {
//					if(timeSaleGoods.get(j).getId().equals(bm.get(k).getId())) {
//						timeSaleGoodsUI.get(j).getFavorite().setBackgroundResource(R.drawable.full_likecircle_withshadow);
//						timeSaleGoods.get(j).setFavorite(true);
//					}
//				}
//			}
//		}
//		for(int i=0; i<normalCampaign.size(); i++) {
//			ArrayList<Goods> normalGoods = normalCampaign.get(i).getGoods();
//			ArrayList<NormalGoodsUI> normalGoodsUI = normalCampaign.get(i).getuI();
//
//			for(int j=0; j<normalGoods.size(); j+=2) {
//				normalGoodsUI.get(j/2).getLeftFavorite().setBackgroundResource(R.drawable.btn_likered);
//				normalGoods.get(j).setFavorite(false);
//
//				for(int k=0; k<bm.size(); k++) {
//					if(normalGoods.get(j).getId().equals(bm.get(k).getId())) {
//						normalGoodsUI.get(j/2).getLeftFavorite().setBackgroundResource(R.drawable.goods_card_btn_likefull);
//						normalGoods.get(j).setFavorite(true);
//					}
//				}
//
//				if(j+1 < normalGoods.size()) {
//					normalGoodsUI.get(j/2).getRightFavorite().setBackgroundResource(R.drawable.btn_likered);
//					normalGoods.get(j+1).setFavorite(false);
//
//					for(int k=0; k<bm.size(); k++) {
//						if(normalGoods.get(j+1).getId().equals(bm.get(k).getId())) {
//							normalGoodsUI.get(j/2).getRightFavorite().setBackgroundResource(R.drawable.goods_card_btn_likefull);
//							normalGoods.get(j+1).setFavorite(true);
//						}
//					}
//				}
//			}
//		}
//	}
}
