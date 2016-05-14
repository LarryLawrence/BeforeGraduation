package com.drunkpiano.zhihuselection.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DrunkPiano on 16/3/16.
 */
public class Db extends SQLiteOpenHelper {

    public static Db mInstance = null ;
    public synchronized  Db getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Db(context);//单例
        }
        return mInstance;
    }

    public Db(Context context) {
        super(context, "dbb", null, 1);//name是数据库名;CursorFactory只在需要自定义Cursor时才使用;version是数据库版本,与onUpgrade相关
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS yesterday (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS recent    (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS archive   (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS linger    (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites    (_id integer primary key autoincrement,stitle text, ssummary text, saddress text)");

//        db.execSQL("CREATE TABLE IF NOT EXISTS DatesAlreadyInView    (_id integer primary key autoincrement,sYesterdayDate text,sRecentDate text,sArchiveDate text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
