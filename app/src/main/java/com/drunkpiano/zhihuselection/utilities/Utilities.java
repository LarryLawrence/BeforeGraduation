package com.drunkpiano.zhihuselection.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.drunkpiano.zhihuselection.Db;
import com.drunkpiano.zhihuselection.ListCellData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

    public void DownloadJSONAndUpdateDB(){
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream is = connection.getInputStream();
                    //需要把它包装成更加简洁的读取数据的方式
                    //IS可指定字符集,所以这是一个字节到字符的转换
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    //BR可以读取一行字符串
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        builder.append(line);
                    }
                    //读取完成,依次向上关闭连接
                    br.close();
                    isr.close();
                    is.close();
                    JSONObject root = new JSONObject(builder.toString());
//                            System.out.println("result="+root.getString("result"));//获取元素
                    JSONArray array = root.getJSONArray("answers");//获取数组
                    ListCellData LcData = new ListCellData();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        LcData.setTitle(jo.getString("title"));
                        LcData.setTime(jo.getString("time"));
                        LcData.setSummary(jo.getString("summary"));
                        LcData.setQuestionid(jo.getString("questionid"));
                        LcData.setAnswerid(jo.getString("answerid"));
                        LcData.setAuthorname(jo.getString("authorname"));
                        LcData.setAuthorhash(jo.getString("authorhash"));
                        LcData.setAvatar(jo.getString("avatar"));
                        LcData.setVote(jo.getString("vote"));

                        insertToSheet(LcData);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            //stitle text,stime text,ssummary text,squestionid text,sanswerid text,sauthorname text,sauthorhash text,savatar text, svote, text)")

        }.execute("http://api.kanzhihu.com/getpostanswers/" + getSystemData() + "/" + sheet);//读今天的

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
        cv.put("svote",data.getVote());
        dbWrite.insert(sheet, null, cv);

        dbWrite.close();
    }

    private static String getSystemData(){
        //24小时制
//        SimpleDateFormat dateFormat24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //12小时制
//      SimpleDateFormat dateFormat12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd");
        return justDate.format(Calendar.getInstance().getTime());
    }
    public static boolean shouldUpdateDB(){
        return true ;
    }
}
