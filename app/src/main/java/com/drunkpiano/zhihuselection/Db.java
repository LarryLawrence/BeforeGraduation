package com.drunkpiano.zhihuselection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DrunkPiano on 16/3/16.
 */
public class Db extends SQLiteOpenHelper {

    public Db(Context context) {
        super(context, "dbb", null, 1);//name是数据库名;CursorFactory只在需要自定义Cursor时才使用;version是数据库版本,与onUpgrade相关
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        //打开或创建test.db数据库
//        SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS person");
//        //创建person表
//        db.execSQL("CREATE TABLE person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age SMALLINT)");
//        Person person = new Person();
//        person.name = "john";
//        person.age = 30;
        db.execSQL("CREATE TABLE IF NOT EXISTS yesterday (_id integer primary key autoincrement,stitle text,stime text,ssummary text,squestionid text,sanswerid text,sauthorname text,sauthorhash text,savatar text, svote, text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS recent    (_id integer primary key autoincrement,stitle text,stime text,ssummary text,squestionid text,sanswerid text,sauthorname text,sauthorhash text,savatar text, svote, text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS archive   (_id integer primary key autoincrement,stitle text,stime text,ssummary text,squestionid text,sanswerid text,sauthorname text,sauthorhash text,savatar text, svote, text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS linger    (_id integer primary key autoincrement,stitle text,stime text,ssummary text,squestionid text,sanswerid text,sauthorname text,sauthorhash text,savatar text, svote, text)");
//        db.execSQL("CREATE TABLE IF NOT EXISTS DatesAlreadyInView    (_id integer primary key autoincrement,sYesterdayDate text,sRecentDate text,sArchiveDate text)");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public static String getSystemDataATime(){
        //24小时制
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //12小时制
//      SimpleDateFormat dateFormat12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat24.format(Calendar.getInstance().getTime());
    }

}
