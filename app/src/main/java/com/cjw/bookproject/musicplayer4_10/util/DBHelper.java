package com.cjw.bookproject.musicplayer4_10.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final int VERSION = 1;
	private static final String DB_NAME = "cjw_music";
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table mp3_music ("
			+"_id integer primary key autoincrement not null,"
			+"name varchar(50) , "
			+"path varchar(100) ," 
			+"artist varchar(50),"
			+"played integer(1) default 0,"
			+"happy integer(1) default 0,"
			+"quiet integer(1) default 0,"
			+"sad integer(1) default 0)");
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
