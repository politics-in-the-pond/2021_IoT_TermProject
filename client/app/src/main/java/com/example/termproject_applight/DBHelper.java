package com.example.termproject_applight;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "setting.db";
    //static final int DATABASE_VERSION = 2;

    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }
/*
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE setting (name TEXT, color TEXT, time TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS setting");
        onCreate(db);
    }

}