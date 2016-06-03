/*
 * Copyright (c) 2016.
 * com.drunkpiano.zhihuselection.fragments.RecentFragment
 * version 1.1.2
 * DrunkPiano All Rights Reserved
 */
package com.drunkpiano.zhihuselection.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.drunkpiano.zhihuselection.activities.WebViewActivity;
import com.drunkpiano.zhihuselection.adapters.RecentAdapter;
import com.drunkpiano.zhihuselection.utilities.DatePickerFragment;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellData;
import com.drunkpiano.zhihuselection.utilities.MainItemClickListener;
import com.drunkpiano.zhihuselection.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecentFragment extends Fragment implements DatePickerFragment.TheListener {
    public static final String PREFS_NAME = "MyPrefsFile";
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView cardsListRv;
    private String dateWithChinese;
    private Db db;
    private String tabName = "recent";
    private Long currentTimeInt;
    private Long latestWebsiteUpdateTimeInt;
    private SimpleDateFormat currentTime;
    private String lastUpdate;
    private SimpleDateFormat latestWebsiteUpdateTime;
    private Long lastUpdateInt;
    private String currentTimeStr;
    private SharedPreferences settings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getInstance(getContext()).getReadableDatabase();
        Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
        myCursor.close();
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsListRv = (RecyclerView) root.findViewById(R.id.cards_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);//is getView() available? or do I need to inflate a view.
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardsListRv.setLayoutManager(llm);
        initThisFragment(false);
        return root;
    }

    private void refreshTime() {
        //DB的最近更新时间
        lastUpdate = settings.getString("LastUpdateRecent", "198801011100");    //defValue - Value to return if this preference does not exist.
        lastUpdateInt = Long.parseLong(lastUpdate);
        //现在的时间
        currentTime = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
        currentTimeInt = Long.parseLong(currentTimeStr);
        //网站最近更新时间,今天早上五点
        latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "1100");
    }

    private void initThisFragment(boolean chongxinlianjieshishi) {
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.query("recent", null, null, null, null, null, null);

        while (cursor.moveToNext())     //其实可以不用这样计算行数,直接用cursor.getCount;
            if (!cursor.moveToFirst()) {
                //数据库中没有数据.那么,1.记录更新数据库的时间 2.下载数据
                SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);

                dateWithChinese = getDate().substring(0, 4) + "年" + getDate().substring(4, 6) + "月" + getDate().substring(6, getDate().length()) + "日";
                SharedPreferences.Editor editor = settings.edit();
                /*-
                 * LastUpdate的SharedPreferences只需要三次写入,对应三种需要刷新list的情况:
                 * 第一次是这里,DB为空的时候;
                 * 第二次,打开后发现不为空,
                 * 于是setup list并且更新;第三次,用户下拉发现可以更新
                 */
                editor.putString("LastUpdateRecent", time.format(Calendar.getInstance().getTime()));
                editor.putString("RecentLastViewedDateChinese", dateWithChinese);
                editor.apply();
                mSwipeRefreshLayout.setRefreshing(true);
                initiateDownloadToEmptyDB();

                //为防止自动刷新后,用户下拉刷新,需要更新下面的参数
                lastUpdate = settings.getString("LastUpdateRecent", "198801011700");//defValue - Value to return if this preference does not exist.
                lastUpdateInt = Long.parseLong(lastUpdate);
            } else {
                //几处progressBar:
                //1.onCreateView发现数据库没内容时   正常 （pb在第一个card上）
                //2.onCreateView发现内容需要更新时   正常 （pb紧贴tab--->改成了find之后统一set）
                String lastViewedDateChinese = settings.getString("RecentLastViewedDateChinese", "上次看到这里");//defValue - Value to return if this preference does not exist.
                setupList(lastViewedDateChinese);

//        if(true)
                refreshTime();
                if ((currentTimeInt > latestWebsiteUpdateTimeInt && lastUpdateInt < latestWebsiteUpdateTimeInt) || chongxinlianjieshishi) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshListView(getDate());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("LastUpdateRecent", currentTimeStr);
                    editor.apply();
                    if (getView() != null)
                        Snackbar.make(getView(), "「上周」栏目有新内容, 稍等..", Snackbar.LENGTH_LONG).show();
                }
            }
        cursor.close();
        dbRead.close();
    }

    @Override
    public void onResume() {
        refreshTime();
        if (currentTimeInt > latestWebsiteUpdateTimeInt && lastUpdateInt < latestWebsiteUpdateTimeInt) {
            initThisFragment(false);//这是为了防止用户没退出进程第二天早晨onResume的时候需要更新
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTime();
                db = new Db(getContext());
                SQLiteDatabase dbRead = db.getReadableDatabase();
                Cursor cursor = dbRead.query("recent", null, null, null, null, null, null);

                if ((currentTimeInt > latestWebsiteUpdateTimeInt && lastUpdateInt < latestWebsiteUpdateTimeInt) || (!cursor.moveToFirst()))
                    swipeRefresh();
                else if (null != getView()) {
                    Snackbar.make(getView(), "已是最新, 「上周」栏目每天中午11:00更新", Snackbar.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                cursor.close();
            }
        });
    }

    //refresh ListView(）一次调用这里,下拉刷新的时候;一次在启动时判断需要更新的情况;还有一次再SHUFFLE的时候;还有一次在item菜单
    private void swipeRefresh() {
        //DB的最近更新时间
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        lastUpdate = settings.getString("LastUpdateRecent", "19880101100");//defValue - Value to return if this preference does not exist.
        lastUpdateInt = Long.parseLong(lastUpdate);
        //现在的时间
        currentTime = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
        currentTimeInt = Long.parseLong(currentTimeStr);
        //网站最近更新时间,今天早上五点
        latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "1100");

        mSwipeRefreshLayout.setRefreshing(true);
        refreshListView(getDate());

        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //第二次更新LastUpdate的SP,在下拉后决定刷新的时候
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LastUpdateRecent", currentTimeStr);
        editor.apply();
    }

    public void setupList(String dateWithChinese) {
        final int POSITION_TOP = 0;
        cardsListRv.setItemAnimator(new DefaultItemAnimator());
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
        RecentAdapter recentAdapter = new RecentAdapter(getActivity(), "recent", myCursor.getCount(), dateWithChinese, callBack);
        cardsListRv.setAdapter(recentAdapter);
        cardsListRv.scrollToPosition(POSITION_TOP);

        dbRead.close();
        myCursor.close();
    }

    public void refreshListView(final String dateStr) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        builder.append(line);
                    }
                    br.close();
                    isr.close();
                    is.close();
                    //获取答案数量
                    JSONObject root = new JSONObject(builder.toString());
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getInstance(getContext()).getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
                    if (myCursor.moveToFirst()) {

                        SQLiteDatabase dbForDrop = db.getWritableDatabase();
                        dbForDrop.execSQL("DELETE FROM recent");
                        dbForDrop.execSQL("CREATE TABLE IF NOT EXISTS recent (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
                        dbForDrop.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'recent';");
                        dbForDrop.close();

                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        for (int i = 0; i < root.getInt("count"); i++) {
                            JSONObject jo = array.getJSONObject(i);
                            LcData.setTitle(jo.getString("title"));
                            LcData.setSummary(jo.getString("summary"));
                            LcData.setQuestionid(jo.getString("questionid"));
                            LcData.setAnswerid(jo.getString("answerid"));
                            insertToTables(LcData, tabName);
                        }
                    }
                    dbRead.close();
                    myCursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (null != getView())
                        Snackbar.make(getView(), "网络出了问题", Snackbar.LENGTH_INDEFINITE).setAction("刷新试试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initThisFragment(true);
                            }
                        }).show();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dateWithChinese = dateStr.substring(0, 4) + "年" + dateStr.substring(4, 6) + "月" + dateStr.substring(6, dateStr.length()) + "日";
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("RecentLastViewedDateChinese", dateWithChinese);
                editor.apply();
                setupList(dateWithChinese);
                mSwipeRefreshLayout.setRefreshing(false);
                if (null != getView())
                    Snackbar.make(getView(), "加载完成!", Snackbar.LENGTH_LONG);
                db.close();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + dateStr + "/recent");//读今天的
//            }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160404" + "/recent");//读今天的
    }

    public void initiateDownloadToEmptyDB() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection();
                    InputStream is = connection.getInputStream();
                    //IS可指定字符集,所以这是一个字节到字符的转换
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
//                        System.out.println(line);
                        builder.append(line);
                    }
                    //读取完成,依次向上关闭连接
                    br.close();
                    isr.close();
                    is.close();
                    //获取答案数量
                    JSONObject root = new JSONObject(builder.toString());
                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getInstance(getContext()).getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
                    //如果yesterday table里面没有数据,才ins ert
                    if (!myCursor.moveToFirst()) {
                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jo = array.getJSONObject(i);
                            LcData.setTitle(jo.getString("title"));
                            LcData.setSummary(jo.getString("summary"));
                            LcData.setQuestionid(jo.getString("questionid"));
                            LcData.setAnswerid(jo.getString("answerid"));
                            insertToTables(LcData, tabName);
                        }
                    }
                    dbRead.close();
                    myCursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (null != getView())
                        Snackbar.make(getView(), "网络出了问题..", Snackbar.LENGTH_INDEFINITE).setAction("刷新试试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initThisFragment(true);
                            }
                        }).show();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                dateWithChinese = new SimpleDateFormat("yyyy年M月d日 E", Locale.CHINA).format(Calendar.getInstance().getTime());
                //setup List放在这里,可以保证下载完成再setup list.注意,如果在这个asyncTask外面再嵌套一个asyncTask,上层的postExecute不会等这一层的执行完才执行!是两个线程了.
                setupList(dateWithChinese);
                mSwipeRefreshLayout.setRefreshing(false);
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/recent");//读今天的
//    }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/recent");//读今天的
    }

    public void insertToTables(ListCellData data, String tabName) {
        db = new Db(getContext());
        //WRITE
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", data.getTitle());
        cv.put("ssummary", data.getSummary());
        cv.put("squestionid", data.getQuestionid());
        cv.put("sanswerid", data.getAnswerid());

        dbWrite.insert(tabName, null, cv);
        dbWrite.close();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //返回20140919到今天的随机一天
                Date randomDate = Utilities.randomDate("20140919", getSystemDate());
                String randomDateStr = new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(randomDate);
                refreshListView(randomDateStr);
                dateWithChinese = new SimpleDateFormat("yyyy年M月d日 E", Locale.CHINA).format(randomDate);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("RecentLastViewedDateChinese", dateWithChinese);
                editor.apply();

                if (null != getView())
                    Snackbar.make(getView(), "时光机带您降落在 : " + dateWithChinese + "", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(true);
                break;
            case R.id.action_date_picker: {
                DatePickerFragment picker = new DatePickerFragment();
                picker.onDateSetListener(this);

                picker.show(getFragmentManager(), "datePicker");

                break;
            }
            case R.id.action_refresh:
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                swipeRefresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentTime() {
        SimpleDateFormat justTime = new SimpleDateFormat("HHmm", Locale.CHINA);
        return justTime.format(Calendar.getInstance().getTime());
    }

    private static String getSystemDate() {
        SimpleDateFormat justDate = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return justDate.format(Calendar.getInstance().getTime());
    }

    private static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyyMMdd ", Locale.CHINA).format(cal.getTime());
    }

    private static String getDate() {
        String dateShouldBeReturned = "";
        if (Integer.parseInt(getCurrentTime()) < 1101)
            dateShouldBeReturned = getYesterdayDate();
        else if (Integer.parseInt(getCurrentTime()) >= 1101)
            dateShouldBeReturned = getSystemDate();
        return dateShouldBeReturned.trim();
    }

//    @Override
//    public void onMainItemClick(ListCellData answer) {
//        Intent intent = new Intent(getContext(), WebViewActivity.class);
//        intent.putExtra("address", "http://www.zhihu.com/question/" + answer.getQuestionid() + "/answer/" + answer.getAnswerid());
//        intent.putExtra("title", answer.getTitle());
//        intent.putExtra("summary", answer.getSummary());
//        startActivity(intent);
//    }


    private MainItemClickListener callBack = new MainItemClickListener() {

        @Override
        public void onMainItemClick(ListCellData answer) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean doNotUseClient = defaultSharedPreferences.getBoolean("doNotUseClient", true);
//            boolean disableJavascript = settings.getBoolean("disableJavascript", true);
            if (doNotUseClient) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("address", "http://www.zhihu.com/question/" + answer.getQuestionid() + "/answer/" + answer.getAnswerid());
                intent.putExtra("title", answer.getTitle());
                intent.putExtra("summary", answer.getSummary());
                startActivity(intent);
            } else {
                Uri uri = Uri.parse("http://www.zhihu.com/question/" + answer.getQuestionid() + "/answer/" + answer.getAnswerid());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        @Override
        public void onEndImageClick() {
            cardsListRv.smoothScrollToPosition(0);
        }
    };

    @Override
    public void returnDate(String date, String date2) {
        long one = Long.parseLong(date);
        if (one < 20140919) {
            if (null != getView())
                Snackbar.make(getView(), "时光机到不了那里,请选择2014年9月19日之后的日期", Snackbar.LENGTH_LONG).show();
        } else if (one > Long.parseLong(getDate())) {
            if (null != getView())
                Snackbar.make(getView(), "这一台时光机不能到未来去啊", Snackbar.LENGTH_LONG).show();
        } else {
            refreshListView(date);
            mSwipeRefreshLayout.setRefreshing(true);
            if (null != getView())
                Snackbar.make(getView(), "时光机带您开往" + date2, Snackbar.LENGTH_LONG).show();
        }
    }
}