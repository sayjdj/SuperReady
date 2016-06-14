package com.haffle.superready.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.haffle.superready.item.Market;
import com.haffle.superready.item.MarketFavorite;

public class MarketDBManager {
	
	SQLiteOpenHelper sph;
	
	public MarketDBManager(Context context, String name, CursorFactory factory, int version) {

		sph =  new SQLiteOpenHelper(context, name, factory, version) {
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL("CREATE TABLE MARKET(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " marketId TEXT,"
						+ " name TEXT,"
						+ " phone TEXT,"
						+ " latitude TEXT,"
						+ " longitude TEXT,"
						+ " description TEXT,"
						+ " panorama TEXT,"
						+ " businessHours TEXT,"
						+ " logo TEXT);");
			}
		};
	}
	
	public void insert(Market market) {
		SQLiteDatabase db = sph.getWritableDatabase();
		
		db.execSQL("insert into MARKET(marketId, name, phone, latitude, longitude, description, panorama,"
				+ "businessHours, logo) " + 
				" values('" + market.getId() + "'," +
				"'" + market.getName() + "'," +
				"'" + market.getPhone() + "'," +
				"'" + market.getLatitude() + "'," +
				"'" + market.getLongitude() + "'," +
				"'" + market.getDescription() + "'," +
				"'" + market.getPanorama() + "'," +
				"'" + market.getBusinessHours() + "'," +
				"'" + market.getLogo() + "');");
		
		db.close();
	}

	public void delete(int i) {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from MARKET where id = " + i + ";");
		db.close();     
	}

	public ArrayList<MarketFavorite> select() {
		SQLiteDatabase db = sph.getReadableDatabase();
		
		ArrayList<MarketFavorite> list = new ArrayList<MarketFavorite>();
		
		Cursor cursor = db.rawQuery("select * from MARKET", null);
		while(cursor.moveToNext()) {
			MarketFavorite bm = new MarketFavorite(cursor.getInt(0),
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

		Cursor cursor = db.rawQuery("SELECT max(id) FROM MARKET", null);
		cursor.moveToFirst();

		int id = cursor.getInt(0);
		cursor.close();

		return id; 
	}
	
	public int getTheMarketNumber() {
		int number;
		
		SQLiteDatabase db = sph.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM MARKET", null);
		cursor.moveToFirst();
		
		number = cursor.getInt(0);
		
		return number;
	}
	
	public void allDelete() {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from MARKET");
		db.close(); 
	}
}
