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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.CardsAdapter;
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
 * Created by DrunkPiano on 16/3/9.
 */
public class FMYesterday extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";

    //    HeyApplication application ;
//    int count = 3 ;
    ListView cardsList ;
    int numCount ;
    int originNumCountForCompare = 30 ;
    Db db = new Db(getContext());
    String tabName = "yesterday";

    @Override
    public void onStart() {
        System.out.println("FMYesterday--->onStart");
        super.onStart();
    }
    @Override
    public void onResume() {
        System.out.println("FMYesterday--->onResume");
        super.onResume();
    }
    @Override
    public void onAttach(Context context) {
        System.out.println("FMYesterday--->onAttach");
        super.onAttach(context);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        System.out.println("FMYesterday--->onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onPause() {
        System.out.println("FMYesterday--->onPause");
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("FMYesterday--->onCreateView");
//        application = (HeyApplication)getActivity().getApplication();
//        count = application.getYesterdayCount() ;
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //这里是预先展示数据库里的条目的情况.先读了numCount,它是由DB中cursor.movetonext得出来的.
        numCount = settings.getInt("yesterdayCount",30);//defValue - Value to return if this preference does not exist.
        originNumCountForCompare = numCount ; //这里先保存一份DB中原有的numCount的个数的副本用来对比,因为numCount可能会更新了等会儿.
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) root.findViewById(R.id.cards_list);
        setupList();

        //DB的最近更新时间
        String lastUpdate = settings.getString("LastUpdate", "198801010500");//defValue - Value to return if this preference does not exist.
        Long lastUpdateInt = Long.parseLong(lastUpdate);
        //现在的时间
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
        Long currentTimeInt = Long.parseLong(currentTimeStr);
        //网站最近更新时间,今天早上五点
        SimpleDateFormat latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd");
        Long latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "0500");

//        if(true)
        if(currentTimeInt>latestWebsiteUpdateTimeInt && lastUpdateInt<latestWebsiteUpdateTimeInt)
        {
            refreshListView();
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LastUpdate",currentTimeStr);
        editor.commit();


        return root;
    }
    public void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }
        private CardsAdapter createAdapter(){
            return new CardsAdapter(getActivity(),"yesterday",numCount);
    }

    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }
    public void refreshListView(){
        System.out.println("--------refreshListView,init------------");

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    System.out.println("--------refreshListView,doInBackground------------");

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
                    try {
                        SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("yesterdayCount", numCount);
                        editor.commit();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("e--->"+e.toString());
                    }
                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("yesterday", null, null, null, null, null, null);
                    if(myCursor.moveToFirst()){
                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        if(numCount>originNumCountForCompare)
                            numCount = originNumCountForCompare ; //今天的条目比昨天的多
                        for (int i = 0; i < numCount; i++) {
//                        for (int i = 0; i < array.length(); i++) {
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
//                            insertToSheet(LcData , tabName);
//                            System.out.println("-------->before update");
                            updateTables(LcData, tabName, i + 1);
                            //如果今天的_ids比昨天多,那么多出的部分,update是无效的,因为只有ids存在的情况下才能更新;想增加只能insert.
                            //如果什么都不处理,那么是否会出现out of index的情况?因为传入给cardAdapter的numCount比昨天的getCount要大.
                            //那就处理吧.怎么处理?先获取昨天的count,多出的部分用insert.
                            //先暂时不考虑今天的_ids比昨天少的情况.
                        }
                        for (int i = numCount; i < array.length(); i++) {
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
                            insertToTables(LcData , tabName);
//                            System.out.println("-------->before update");
//                            updateTables(LcData, tabName, i + 1);
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
                System.out.println("refresh   4   postExecute```````,numCount= "+ numCount);
//                pb.setVisibility(View.GONE);
//                getChildFragmentManager().beginTransaction().replace(R.id.bridge_container, new FMYesterday()).commitAllowingStateLoss();
                setupList();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/yesterday");//读今天的
//            }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/yesterday");//读今天的
    }

    public void updateTables(ListCellData data , String tabName , int ids ){
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
        System.out.println("FM title---------->"+data.getTitle());
        String whereClause="_id=?";
        String [] whereArgs = {String.valueOf(ids)};
        dbWrite.update(tabName, cv, whereClause, whereArgs);

        dbWrite.close();
//        notifyDataSetChanged();
//        FmSecond fs = new FmSecond();
//        fs.setupList();

    }



    public void insertToTables(ListCellData data , String tabName){
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

    private static String getDate(){
        String dateShouldBeReturned = "" ;
        if(Integer.parseInt(getCurrentTime())<501)
            dateShouldBeReturned = getYesterdayDate() ;
        else if(Integer.parseInt(getCurrentTime())>=501)
            dateShouldBeReturned = getSystemDate() ;
        return dateShouldBeReturned.trim() ;
    }
    private static String getCurrentTime(){
        SimpleDateFormat justTime = new SimpleDateFormat("HHmm");
        return justTime.format(Calendar.getInstance().getTime());
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


}
