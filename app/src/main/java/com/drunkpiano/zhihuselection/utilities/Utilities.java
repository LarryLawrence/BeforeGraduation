package com.drunkpiano.zhihuselection.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.drunkpiano.zhihuselection.Db;
import com.drunkpiano.zhihuselection.ListCellData;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DrunkPiano on 16/3/17.
 */
public class Utilities {
    Context context ;
    String sheet = "";
    public Utilities(Context context ,String chooseSheet) {
        this.context = context ;
        this.sheet = chooseSheet ;
    }

    private void insertToSheet(ListCellData data ){
        Db db = new Db(context);
        //WRITE
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle",data.getTitle());
        cv.put("stime",data.getTime());
        cv.put("ssummary",data.getSummary());
        cv.put("squestionid",data.getQuestionid());
        cv.put("sanswerid",data.getAnswerid());
        cv.put("sauthorname", data.getAuthorname());
        cv.put("sauthorhash",data.getAuthorhash());
        cv.put("savatar",data.getAvatar());
        cv.put("svote", data.getVote());
        dbWrite.insert(sheet, null, cv);

        dbWrite.close();

    }

    private static String getCurrentTime(){
        SimpleDateFormat justTime = new SimpleDateFormat("HHmm");
        return justTime.format(Calendar.getInstance().getTime());
        //如果返回的是10点之前的数字,首位的0在parseInt时会被去掉
//        e.g.*String s="01100";
//        e.g.*int i=Integer.parseInt(s);
//        e.g.*System.out.println(i);//1100
    }

    private static String getSystemDate(){
        //24小时制
//        SimpleDateFormat dateFormat24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //12小时制
//      SimpleDateFormat dateFormat12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd");
        return justDate.format(Calendar.getInstance().getTime());
    }

    private static String getYesterdayDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterdayDate = new SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());
        return yesterdayDate ;
    }

    public static boolean shouldUpdateDB(){
        return true ;
    }
}
