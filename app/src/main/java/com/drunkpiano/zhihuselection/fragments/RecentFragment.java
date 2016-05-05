package com.drunkpiano.zhihuselection.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.MainAdapter;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellData;
import com.drunkpiano.zhihuselection.utilities.Utilities;

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
import java.util.Date;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class RecentFragment extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    public SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView cardsListRv;
    static String dateWithChinese;
    int numCount = 0;
    int originNumCountForCompare = 30;
    Db db;
    String tabName = "recent";
    Long currentTimeInt;
    Long latestWebsiteUpdateTimeInt;
    SimpleDateFormat currentTime;
    String lastUpdate;
    SimpleDateFormat latestWebsiteUpdateTime;
    Long lastUpdateInt;
    SharedPreferences settings;
    String currentTimeStr;
    int dbLines = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //这里是预先展示数据库里的条目的情况.先读了numCount,它是由DB中cursor.movetonext得出来的,,不,现在改成直接从recentCount里读取.
        settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        numCount = settings.getInt("recentCount", 0);//defValue - Value to return if this preference does not exist.
        originNumCountForCompare = numCount; //这里先保存一份DB中原有的numCount的个数的副本用来对比,因为numCount可能会更新了等会儿（json中获取count时更新）.
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsListRv = (RecyclerView) root.findViewById(R.id.cards_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);//is getView() available? or do I need to inflate a view.
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

//        getChildFragmentManager().beginTransaction().add(R.id.swipe_refresh_layout, new NoNetWorkFragment()).commitAllowingStateLoss();
        cardsListRv.setLayoutManager(new LinearLayoutManager(getContext()));//用线性显示 类似于listview
        initThisFragment(false);
        return root;
    }
    private void initThisFragment(boolean chongxinlianjieshishi){
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.query("recent", null, null, null, null, null, null);

        while (cursor.moveToNext())//其实可以不用这样计算行数,直接用cursor.getCount;
            dbLines++;
        if (!cursor.moveToFirst()) {
            //数据库中没有数据.那么,1.记录更新数据库的时间 2.下载数据
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmm");
            SharedPreferences.Editor editor = settings.edit();
            //LastUpdate的SharedPreferences只需要三次写入,对应三种需要刷新list的情况:第一次是这里,DB为空的时候;第二次,打开后发现不为空,于是setuplist并且更新;第三次,用户下拉发现可以更新
            editor.putString("LastUpdateRecent", time.format(Calendar.getInstance().getTime()));
            editor.apply();
            System.out.println("数据库里没东西,下载.");
            //NECESSARY
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
            mSwipeRefreshLayout.setRefreshing(true);
            initiateDownloadToEmptyDB();
        } else {
//            mSwipeRefreshLayout.setProgressViewOffset(false, 0, 24);
//            mSwipeRefreshLayout.setRefreshing(true);
            setupList();
            //DB的最近更新时间
            lastUpdate = settings.getString("LastUpdateRecent", "198801011100");//defValue - Value to return if this preference does not exist.
            lastUpdateInt = Long.parseLong(lastUpdate);
            //现在的时间
            currentTime = new SimpleDateFormat("yyyyMMddHHmm");
            currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
            currentTimeInt = Long.parseLong(currentTimeStr);
            //网站最近更新时间,今天早上五点
            latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd");
            latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "1100");
//        if(true)
            if ((currentTimeInt > latestWebsiteUpdateTimeInt && lastUpdateInt < latestWebsiteUpdateTimeInt) || chongxinlianjieshishi) {
                mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
                mSwipeRefreshLayout.setRefreshing(true);
                System.out.println("对应createView的时候判断出需要刷新的情况");
                refreshListView(getDate());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("LastUpdateRecent", currentTimeStr);
                editor.apply();
            }
        }
        cursor.close();
        dbRead.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
//                if (!mSwipeRefreshLayout.isRefreshing()) {
//                    mSwipeRefreshLayout.setRefreshing(true);
//                }
                mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
                mSwipeRefreshLayout.setRefreshing(true);
                new SwipeRefreshBackgroundTask().execute();
                Snackbar.make(getView(), "「一周」栏目每天11:00更新", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

//    private void initiateDownloadToEmptyDB() {
//        if (!mSwipeRefreshLayout.isRefreshing()) {
//            mSwipeRefreshLayout.setRefreshing(true);
//        }
//        System.out.println("initiateDownloadToEmptyDB");
//        downloadJSONAndUpdateDB();
//    }

    private void onRefreshComplete() {

        lastUpdate = settings.getString("LastUpdateRecent", "198801010500");//defValue - Value to return if this preference does not exist.
        lastUpdateInt = Long.parseLong(lastUpdate);

        System.out.println("onRefreshComplete");
        if (!(currentTimeInt > latestWebsiteUpdateTimeInt && lastUpdateInt < latestWebsiteUpdateTimeInt)) {
            System.out.println("已经是最新的内容了");
        } else {

        }
//        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setupList() {
        cardsListRv.setItemAnimator(new DefaultItemAnimator());
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);

        cardsListRv.setAdapter(new MainAdapter(getActivity(), "recent", myCursor.getCount(),dateWithChinese));
        dbRead.close();
        myCursor.close();
    }

    public void refreshListView(String dateStr) {
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
                    JSONObject root = new JSONObject();
                    try {
                        root = new JSONObject(builder.toString());
                    }
                    catch (JSONException e)
                    {
                        System.out.println("没能转换成JSON");
//                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(getView(), "网络连接不上了", Snackbar.LENGTH_INDEFINITE).setAction("重新连接试试",new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                new SwipeRefreshBackgroundTask().execute();
                                initThisFragment(true);
                            }
                        }).show();

                    }
                    numCount = root.getInt("count");
                    System.out.println("refreshListView-->root中的count是" + root.getInt("count"));

                    try {
                        SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("recentCount", numCount);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getInstance(getContext()).getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
                    if (myCursor.moveToFirst()) {
                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        //这里的numCount已经是本次获取的条目数
                        if (numCount > originNumCountForCompare)
                            numCount = originNumCountForCompare; //今天的条目比昨天的多,那么把先update已有条目,再insert新的.
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
                        System.out.println("numCount----->"+ numCount+  "array.length()--->"+array.length());
                        for (int i = numCount; i < array.length(); i++) {//如果numCount=array.length(),这里不会执行
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
                            insertToTables(LcData, tabName);
//                            System.out.println("-------->before update");
//                            updateTables(LcData, tabName, i + 1);
                        }
//                        if(numCount<originNumCountForCompare)//今天的条目比昨天少
//                        {
//                            for(int i = numCount + 1 ; i < originNumCountForCompare ; i ++)
//                            deleteDBLines(tabName, i );
//                        }
                        System.out.println(numCount + "--=====================++===============---" + dbLines);
                        if (numCount < dbLines)//今天的条目比db中的少
                        {
                            for (int i = numCount + 1; i < dbLines + 1; i++)
                                deleteDBLines(tabName, i);
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

            @Override
            protected void onPostExecute(Void aVoid) {
                System.out.println("refresh   4   postExecute```````,numCount= " + numCount);
//                pb.setVisibility(View.GONE);
                setupList();
                mSwipeRefreshLayout.setRefreshing(false);
                db.close();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + dateStr + "/recent");//读今天的
//            }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160404" + "/recent");//读今天的
    }

    private class SwipeRefreshBackgroundTask extends AsyncTask<Void, Void, Void> {
        static final int TASK_DURATION = 500; // 3 seconds

        @Override
        protected Void doInBackground(Void... params) {

            //DB的最近更新时间
            lastUpdate = settings.getString("LastUpdateRecent", "19880101100");//defValue - Value to return if this preference does not exist.
            lastUpdateInt = Long.parseLong(lastUpdate);
            //现在的时间
            currentTime = new SimpleDateFormat("yyyyMMddHHmm");
            currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
            currentTimeInt = Long.parseLong(currentTimeStr);
            //网站最近更新时间,今天早上五点
            latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd");
            latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "1100");

//            if(currentTimeInt>latestWebsiteUpdateTimeInt && lastUpdateInt<latestWebsiteUpdateTimeInt)
            if (true)//强制下拉每次刷新
            {
//                System.out.println("!lastUpdateInt---->" + lastUpdateInt + "\nlatestWebsiteUpdateTimeInt---->" + latestWebsiteUpdateTimeInt + "\ncurrentTimeInt-->" + currentTimeInt);
//                Toast.makeText(getActivity(),"!lastUpdateInt---->"+lastUpdateInt+"\nlatestWebsiteUpdateTimeInt---->"+latestWebsiteUpdateTimeInt+"\ncurrentTimeInt-->"+currentTimeInt,Toast.LENGTH_LONG).show();
                refreshListView(getDate());
                System.out.println("正在更新..");
//                Toast.makeText(getActivity(),"正在更新..",Toast.LENGTH_SHORT).show();
                //改写最后更新时间
                settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                //第二次更新LastUpdate的SP,在下拉后决定刷新的时候
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("LastUpdateRecent", currentTimeStr);
                editor.apply();
            } else {
                try {
                    Thread.sleep(TASK_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onRefreshComplete();
        }
    }

    public void initiateDownloadToEmptyDB() {
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
                    JSONObject root = new JSONObject();
                    try{
                        root = new JSONObject(builder.toString());
                    }catch (JSONException e)
                    {
                        System.out.println("init转换JSON出错");
                        Snackbar.make(getView(), "网络连接不上了", Snackbar.LENGTH_INDEFINITE).setAction("重新连接试试",new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                new SwipeRefreshBackgroundTask().execute();
                                initThisFragment(true);
                            }
                        }).show();
//                        getFragmentManager().beginTransaction().replace(R.id.container, new NoNetWorkFragment()).commit();
                    }
                    numCount = root.getInt("count");
                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("recentCount", numCount);
                    editor.apply();

                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
                    //如果yesterday table里面没有数据,才insert
                    if (!myCursor.moveToFirst()) {
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

                            insertToTables(LcData, tabName);
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
            @Override
            protected void onPostExecute(Void aVoid) {
                //setupList放在这里,可以保证下载完成再setuplist.注意,如果在这个asyncTask外面再嵌套一个asyncTask,上层的postExecute不会等这一层的执行完才执行!是两个线程了.
                setupList();
                mSwipeRefreshLayout.setRefreshing(false);
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/recent");//读今天的
//    }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/recent");//读今天的
    }

    public void deleteDBLines(String tabName, int ids) {
        db = new Db(getContext());
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = {String.valueOf(ids)};
        dbWrite.delete(tabName, whereClause, whereArgs);//没有cv
        dbWrite.close();
    }

    public void updateTables(ListCellData data, String tabName, int ids) {
        db = new Db(getContext());
        //WRITE
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", data.getTitle());
        cv.put("stime", data.getTime());
        cv.put("ssummary", data.getSummary());
        cv.put("squestionid", data.getQuestionid());
        cv.put("sanswerid", data.getAnswerid());
        cv.put("sauthorname", data.getAuthorname());
        cv.put("sauthorhash", data.getAuthorhash());
        cv.put("savatar", data.getAvatar());
        cv.put("svote", data.getVote());
        System.out.println("FM title---------->" + data.getTitle());
        String whereClause = "_id=?";
        String[] whereArgs = {String.valueOf(ids)};
        dbWrite.update(tabName, cv, whereClause, whereArgs);

        dbWrite.close();

    }

    public void insertToTables(ListCellData data, String tabName) {
        db = new Db(getContext());
        //WRITE
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", data.getTitle());
        cv.put("stime", data.getTime());
        cv.put("ssummary", data.getSummary());
        cv.put("squestionid", data.getQuestionid());
        cv.put("sanswerid", data.getAnswerid());
        cv.put("sauthorname", data.getAuthorname());
        cv.put("sauthorhash", data.getAuthorhash());
        cv.put("savatar", data.getAvatar());
        cv.put("svote", data.getVote());
        System.out.println("Bridge title---------->" + data.getTitle());

        dbWrite.insert(tabName, null, cv);
        dbWrite.close();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    /**
     * Respond to the user's selection of the Refresh action item. Start the SwipeRefreshLayout
     * progress bar, then initiate the background task that refreshes the content.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //    Date randomDate = randomDate("2010-09-20", "2010-09-21");
//                SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd");
//                return justDate.format(Calendar.getInstance().getTime());
                Date randomDate = Utilities.randomDate("20140919",getSystemDate());
                String randomDateStr = new SimpleDateFormat("yyyyMMdd").format(randomDate);
                System.out.println(randomDateStr);
                dateWithChinese = new SimpleDateFormat("yyyy年M月d日 E").format(randomDate);
                //返回20140919到今天的随机一天
                refreshListView(randomDateStr);
                Snackbar.make(getView(), "时光机带你随机来到了"+dateWithChinese+"~", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setProgressViewOffset(false, 0, 96);
                mSwipeRefreshLayout.setRefreshing(true);
                break;
            case R.id.action_date_picker:
                Snackbar.make(getView(), "选择了XX日.", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_refresh:
//                Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
                System.out.println("刷新");
                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // Start our refresh background task
                new SwipeRefreshBackgroundTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentTime() {
        SimpleDateFormat justTime = new SimpleDateFormat("HHmm");
        return justTime.format(Calendar.getInstance().getTime());
    }

    private static String getSystemDate() {
        SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd");
        return justDate.format(Calendar.getInstance().getTime());
    }

    private static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterdayDate = new SimpleDateFormat("yyyyMMdd ").format(cal.getTime());
        return yesterdayDate;
    }

    private static String getDate() {
        String dateShouldBeReturned = "";
        if (Integer.parseInt(getCurrentTime()) < 1101)
            dateShouldBeReturned = getYesterdayDate();
        else if (Integer.parseInt(getCurrentTime()) >= 1101)
            dateShouldBeReturned = getSystemDate();
        return dateShouldBeReturned.trim();
    }

}