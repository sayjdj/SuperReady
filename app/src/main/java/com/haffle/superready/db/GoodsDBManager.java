package com.haffle.superready.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.haffle.superready.item.GoodsFavorite;

import java.util.ArrayList;

public class GoodsDBManager {
	
	SQLiteOpenHelper sph;
	
	public GoodsDBManager(Context context, String name, CursorFactory factory, int version) {

		sph =  new SQLiteOpenHelper(context, name, factory, version) {
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL("CREATE TABLE GOODS(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " goodsId TEXT,"
						+ " name TEXT,"
						+ " description TEXT,"
						+ " marketName TEXT,"
						+ " marketId TEXT,"
						+ " price TEXT,"
						+ " endTime TEXT,"
						+ " panorama TEXT,"
						+ " timeSale TEXT);");
			}
		};
	}
	
	public void insert(GoodsFavorite goodsFavorite) {
		SQLiteDatabase db = sph.getWritableDatabase();
		
		db.execSQL("insert into GOODS(goodsId, name, description, marketName, marketId, price, endTime, panorama, timeSale) " +
				" values('" + goodsFavorite.getId() + "'," +
				"'" + goodsFavorite.getName() + "'," +
				"'" + goodsFavorite.getDescription() + "'," +
				"'" + goodsFavorite.getMarketName() + "'," +
				"'" + goodsFavorite.getMarketId() + "'," +
				"'" + goodsFavorite.getCurPrice() + "'," +
				"'" + goodsFavorite.getEndTime() + "'," +
				"'" + goodsFavorite.getImage() + "'," +
				"'" + Boolean.toString(goodsFavorite.isTimeSale()) + "');");

		db.close();
	}

	public void delete(int i) {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from GOODS where id = " + i + ";");
		db.close();     
	}
	
	public void deleteWithGoodsId(int i) {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from GOODS where goodsId = " + i + ";");
		db.close(); 
	}

	public ArrayList<GoodsFavorite> select() {
		SQLiteDatabase db = sph.getReadableDatabase();
		
		ArrayList<GoodsFavorite> list = new ArrayList<GoodsFavorite>();
		
		Cursor cursor = db.rawQuery("select * from GOODS", null);
		while(cursor.moveToNext()) {
			GoodsFavorite bm = new GoodsFavorite(cursor.getInt(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5),
					cursor.getString(6),
					cursor.getString(7),
					cursor.getString(8),
					cursor.getString(9));
					
			list.add(bm);
		}

		db.close();   

		return list;
	}

	// maxid를 반환
	public int getMaxId() {
		SQLiteDatabase db = sph.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT max(id) FROM GOODS", null);
		cursor.moveToFirst();

		int id = cursor.getInt(0);
		cursor.close();

		return id; 
	}
	
	public void allDelete() {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from GOODS");
		db.close(); 
	}
}
