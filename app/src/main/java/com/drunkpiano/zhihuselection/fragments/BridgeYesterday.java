package com.drunkpiano.zhihuselection.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.drunkpiano.zhihuselection.Db;
import com.drunkpiano.zhihuselection.ListCellData;
import com.drunkpiano.zhihuselection.R;

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
 * Created by DrunkPiano on 16/3/19.
 */

//numCount,第一次操作是在判断DB为空时,downloadJSONAndUpdateDB()下载的时候,读json的的条数然后赋值给yesterdayCount;
// 然后不论是否DB为空,都会transeform到FMYesterday,进去之后,立刻读取yesterdayCount,然后setuplist()
    //然后开始判断是否需要刷新.如果是,执行refreshListView().里面会重新读json的条数,覆盖numCount
public class BridgeYesterday extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    ProgressBar pb ;
    int numCount = 30;
    Db db = new Db(getContext());
    String tabName = "yesterday";
    SwipeRefreshLayout mSwipeRefreshLayout ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Toast.makeText(getContext(), "FragmentBridge", Toast.LENGTH_SHORT).show();
        System.out.println("FragmentBridge!");
        View root ;
        root = inflater.inflate(R.layout.progressbar_fragment,container,false);
        pb = (ProgressBar)root.findViewById(R.id.progressBar);

//        mSwipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.swipe_refresh_layout);
//        if (!mSwipeRefreshLayout.isRefreshing()) {
//            mSwipeRefreshLayout.setRefreshing(true);
//        }
//        InitiateSimulateRefresh();

        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.query("yesterday", null, null, null, null, null, null);
        if(!cursor.moveToFirst()) {
            //记录更新数据库的时间
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//            String user_first = settings.getString("LastUpdate", "19880101");//defValue - Value to return if this preference does not exist.
            SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmm");
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("LastUpdate", time.format(Calendar.getInstance().getTime()));
            editor.commit();
            System.out.println("数据库里没东西,下载.");
            downloadJSONAndUpdateDB();
        }
        else
        {
            System.out.println("it's not downloading, it's the old database~~~~~~~~~~~~");
            //1.先呈现db中现有的内容;
            pb.setVisibility(View.GONE);
            int _ids = 0 ;
            while(cursor.moveToNext())
            {
                _ids ++ ;
            }
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("yesterdayCount", _ids) ;
            editor.commit();
            System.out.println("_ids------------------>" + _ids);
//            application = (HeyApplication)getActivity().getApplication();
//            application.setYesterdayCount(_ids);
            getChildFragmentManager().beginTransaction().replace(R.id.bridge_container, new FMYesterday()).commitAllowingStateLoss();
            //2.然后,如果上一次打开标签时间在今天之前,那就要更新.否则不执行.是在这个fragment更新吗?应该是新的里面.
//            loadFromDbAndTransform();
        }
        dbRead.close();
        cursor.close();
            return root ;
    }

    public void downloadJSONAndUpdateDB(){
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
                    //获取答案数量
                    JSONObject root = new JSONObject(builder.toString());
                    numCount = root.getInt("count");
                    //存放到application里面
//                    application = (HeyApplication)getActivity().getApplication();
//                    application.setYesterdayCount(numCount);
//                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
//                    Boolean user_first = settings.getInt("",true);//defValue - Value to return if this preference does not exist.
//                    if(user_first) {
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putBoolean("FirstLaunch", false);
//                        editor.commit();
//                        System.out.println("first launch");
//                    }
                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("yesterdayCount",numCount) ;
                    editor.commit();

                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("yesterday", null, null, null, null, null, null);
                    //如果yesterday table里面没有数据,才insert
                    if(!myCursor.moveToFirst()){
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

                        insertToSheet(LcData , tabName);
                        }
                    }
                    dbRead.close();
                    myCursor.close();
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
            @Override
            protected void onPostExecute(Void aVoid) {
                System.out.println("postExecute BB```````,numCount= " + numCount);
                pb.setVisibility(View.GONE);
                getChildFragmentManager().beginTransaction().replace(R.id.bridge_container, new FMYesterday()).commitAllowingStateLoss();
                super.onPostExecute(aVoid);
             }
            }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/yesterday");//读今天的
//    }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/yesterday");//读今天的
}

//    public void loadFromDbAndTransform(){
//        //1.先呈现db中现有的内容;然后如果上一次打开标签时间在昨天,这一次打开在今天,那就要更新.否则不执行.
//
//    }


    public void insertToSheet(ListCellData data , String tabName){
        db = new Db(getContext());
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
        System.out.println("Bridge title---------->" + data.getTitle());

        dbWrite.insert(tabName, null, cv);

        dbWrite.close();
//        notifyDataSetChanged();
//        FmSecond fs = new FmSecond();
//        fs.setupList();

    }
    private static String getCurrentTime(){
        SimpleDateFormat justTime = new SimpleDateFormat("HHmm");
        return justTime.format(Calendar.getInstance().getTime());
        //如果返回的是10点之前的数字,首位的0在parseInt时会被去掉
//        e.g.*String s="01100";
//        e.g.*int i=Integer.parseInt(s);
//        e.g.*System.out.println(i);//1100
    }
    private static String getDate(){
        String dateShouldBeReturned = "" ;

        if(Integer.parseInt(getCurrentTime())<501)
            dateShouldBeReturned = getYesterdayDate() ;
        else if(Integer.parseInt(getCurrentTime())>=501)
            dateShouldBeReturned = getSystemDate() ;

        return dateShouldBeReturned.trim() ;
    }

    private static String getSystemDate(){
        SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd");
        return justDate.format(Calendar.getInstance().getTime());
    }

    private static String getYesterdayDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterdayDate = new SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());
        return yesterdayDate ;
    }

//    private void InitiateSimulateRefresh(){
//        final int REFRESH_DURATION = 1000 ;
//
//        new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                try{
//                    Thread.sleep(REFRESH_DURATION);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        }.execute();
//
//    }

}
