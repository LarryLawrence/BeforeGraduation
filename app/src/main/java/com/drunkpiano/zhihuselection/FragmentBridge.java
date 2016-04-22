package com.drunkpiano.zhihuselection;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
public class FragmentBridge extends Fragment {
    private HeyApplication application ;
    ProgressBar pb ;
    int numCount = 30;
    Db db = new Db(getContext());
    String tabName = "";
    String dateShouldBeReturned = "" ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getContext(), "FragmentBridge", Toast.LENGTH_SHORT).show();
        System.out.println("FragmentBridge!");

        View root = inflater.inflate(R.layout.progressbar_fragment,container,false);
//        final Button btn = (Button)root.findViewById(R.id.load);
//         pb = (ProgressBar)root.findViewById(R.id.progressBar);

        tabName = getArguments().getString("tabName") ;
//        System.out.println("getArguments()获取的tabName-------->" + tabName);
//        tabName = "recent" ;
        if(tabName=="yesterday" && Integer.parseInt(getCurrentTime())<501)//注意要去掉开头的0;0501是一个极大的数
            dateShouldBeReturned = getYesterdayDate() ;
        else if(tabName=="yesterday" && Integer.parseInt(getCurrentTime())>=501)
            dateShouldBeReturned = getSystemDate() ;
        else if(tabName=="recent" && Integer.parseInt(getCurrentTime())<1101)
            dateShouldBeReturned = getYesterdayDate() ;
        else if(tabName=="recent" && Integer.parseInt(getCurrentTime())>=1101)
            dateShouldBeReturned = getSystemDate() ;
        else if(tabName=="archive" && Integer.parseInt(getCurrentTime())<1701)
            dateShouldBeReturned = getYesterdayDate() ;
        else if(tabName=="archive" && Integer.parseInt(getCurrentTime())>=1701)
            dateShouldBeReturned = getSystemDate() ;

        DownloadJSONAndUpdateDB();

        return root ;

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
                    //获取答案数量
                    JSONObject root = new JSONObject(builder.toString());
                    numCount = root.getInt("count");
                    //存放到application里面
                    application = (HeyApplication)getActivity().getApplication();
                    application.setYesterdayCount(numCount);

                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("DatesAlreadyInView", null, null, null, null, null, null);
                    SQLiteDatabase dbWrite = db.getWritableDatabase();
                    ContentValues cv = new ContentValues();

                    if(!myCursor.moveToFirst()) {//首次启动,第一行没有东西,注意是任何一列的第一行都没有东西
                        //往第一行的每个column写东西,写的是virgin
                        cv.put("sYesterdayDate","virgin");
                        cv.put("sRecentDate", "virgin");
                        cv.put("sArchiveDate", "virgin");
                        dbWrite.insert("DatesAlreadyInView", null, cv);
                    }
//                    这时候第一行已经有virgin了,把virgin替换成日期
                    myCursor.moveToFirst();
                    if(myCursor.getString(1).equals("virgin")){//@wack
                        System.out.println("may-your-blade-seeks-vagence");
                    }

                        if( (tabName.equals("yesterday")) && (myCursor.getString(1).equals("virgin")) ) {
                            cv.put("sYesterdayDate", dateShouldBeReturned);
                            String whereClause="_id=?";
                            String [] whereArgs = {String.valueOf(1)};
                            dbWrite.update("DatesAlreadyInView", cv, whereClause, whereArgs);
                            System.out.println("yesterday---------->virginnnnnnnnnnnnn");
                        }
                        if( (tabName.equals("recent")) && (myCursor.getString(2).equals("virgin")) ) {
                            cv.put("sRecentDate", dateShouldBeReturned);
                            String whereClause="_id=?";
                            String [] whereArgs = {String.valueOf(1)};
                            dbWrite.update("DatesAlreadyInView", cv, whereClause, whereArgs);
                            System.out.println("recent---------->virginnnnnnnnnnnnn");

                        }
                        if( (tabName.equals("archive")) && (myCursor.getString(3).equals("virgin")) ) {
                            cv.put("sArchiveDate", dateShouldBeReturned);
                            String whereClause="_id=?";
                            String [] whereArgs = {String.valueOf(1)};
                            dbWrite.update("DatesAlreadyInView", cv, whereClause, whereArgs);
                        }
                    dbWrite.close();

                    if(  ( (myCursor.getString(1).equals("virgin")) && !myCursor.moveToNext() )
                            || ( (myCursor.getString(2).equals("virgin")) && !myCursor.moveToNext() )
                                || ( (myCursor.getString(2).equals("virgin")) && !myCursor.moveToNext() )) {
//                    System.out.println("Application中的yesterdayCount" + application.getYesterdayCount());
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
                System.out.println("postExecute```````,numCount= "+ numCount);
                pb.setVisibility(View.GONE);
                getChildFragmentManager().beginTransaction().replace(R.id.bridge_container, new FmSecond()).commit();
                super.onPostExecute(aVoid);
            }
//        }.execute("http://api.kanzhihu.com/getpostanswers/" + getSystemDate() + "/" + "yesterday");//读今天的
          }.execute("http://api.kanzhihu.com/getpostanswers/" + dateShouldBeReturned + "/" + tabName);//读今天的

        }

    private void insertToSheet(ListCellData data , String tabName){
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
