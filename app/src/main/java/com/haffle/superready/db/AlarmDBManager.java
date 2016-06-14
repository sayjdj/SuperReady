package com.haffle.superready.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.haffle.superready.item.Alarm;

public class AlarmDBManager {
	
	SQLiteOpenHelper sph;
	
	public AlarmDBManager(Context context, String name, CursorFactory factory, int version) {

		sph =  new SQLiteOpenHelper(context, name, factory, version) {
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL("CREATE TABLE ALARM(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ " goodsId TEXT,"
						+ " goodsName TEXT,"
						+ " restTime TEXT);");
			}
		};
	}
	
	public void insert(Alarm alarm) {
		SQLiteDatabase db = sph.getWritableDatabase();
		
		db.execSQL("insert into ALARM(goodsId, goodsName, restTime) " + 
				" values('" + alarm.getGoodsId() + "'," +
				"'" + alarm.getGoodsName() + "'," +
				"'" + alarm.getRestTime() + "');");
		
		db.close();
	}

	public void delete(int i) {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from ALARM where id = " + i + ";");
		db.close();     
	}
	
	public void deleteWithGoodsId(int i) {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from ALARM where goodsId = " + i + ";");
		db.close(); 
	}

	public ArrayList<Alarm> select() {
		SQLiteDatabase db = sph.getReadableDatabase();
		
		ArrayList<Alarm> list = new ArrayList<Alarm>();
		
		Cursor cursor = db.rawQuery("select * from ALARM", null);
		while(cursor.moveToNext()) {
			Alarm bm = new Alarm(
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3));
					
			list.add(bm);
		}

		db.close();   

		return list;
	}

	public void allDelete() {
		SQLiteDatabase db = sph.getReadableDatabase();
		db.execSQL("delete from ALARM");
		db.close(); 
	}
}
