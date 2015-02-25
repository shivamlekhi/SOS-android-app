package com.timeofneedSOS;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "sos.db";

	public static final String TABLE_NAME = "numbers";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NUMBERS_URI = "Number_Uri";

	public static final String SQL_QUERY = "create table " + TABLE_NAME + "("
			+ COLUMN_ID + " Integer PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_NUMBERS_URI + " text);";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_QUERY);
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
		onCreate(db);
	}
}
