/*
 * The fragment to show the archive page.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
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
import com.drunkpiano.zhihuselection.adapters.ArchiveAdapter;
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

public class ArchiveFragment extends Fragment implements DatePickerFragment.TheListener {
    private static final String PREFS_NAME = "MyPrefsFile";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mCardsListRv;
    private String mDateWithChinese;
    private Db mDb;
    private String mTabName = "archive";
    private Long mCurrentTimeInt;
    private Long mLatestWebsiteUpdateTimeInt;
    private SimpleDateFormat mCurrentTime;
    private String mLastUpdate;
    private SimpleDateFormat mLatestWebsiteUpdateTime;
    private Long mLastUpdateInt;
    private String mCurrentTimeStr;
    private SharedPreferences mSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mSettings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mDb = new Db(getContext());
        SQLiteDatabase dbRead = mDb.getInstance(getContext()).getReadableDatabase();
        Cursor myCursor = dbRead.query("archive", null, null, null, null, null, null);
        myCursor.close();
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        mCardsListRv = (RecyclerView) root.findViewById(R.id.cards_list);

        //is getView() available? or do I need to inflate a view.
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCardsListRv.setLayoutManager(llm);
        initThisFragment(false);
        return root;
    }

    private void refreshTime() {
        mLastUpdate = mSettings.getString("LastUpdateArchive", "198801011700");
        mLastUpdateInt = Long.parseLong(mLastUpdate);
        //现在的时间
        mCurrentTime = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        mCurrentTimeStr = mCurrentTime.format(Calendar.getInstance().getTime()).trim();
        mCurrentTimeInt = Long.parseLong(mCurrentTimeStr);
        //网站最近更新时间,今天早上五点
        mLatestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        mLatestWebsiteUpdateTimeInt = Long.parseLong(mLatestWebsiteUpdateTime.format
                (Calendar.getInstance().getTime()).trim() + "1700");
    }

    private void initThisFragment(boolean tryToConnectAgain) {
        mDb = new Db(getContext());
        SQLiteDatabase dbRead = mDb.getReadableDatabase();
        Cursor cursor = dbRead.query("archive", null, null, null, null, null, null);

        while (cursor.moveToNext())//其实可以不用这样计算行数,直接用cursor.getCount;
            if (!cursor.moveToFirst()) {

                //数据库中没有数据.那么,1.记录更新数据库的时间 2.下载数据
                SharedPreferences mSettings = getContext().
                        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
                mDateWithChinese = getDate().substring(0, 4) + "年" + getDate().substring(4, 6)
                        + "月" + getDate().substring(6, getDate().length()) + "日";
                SharedPreferences.Editor editor = mSettings.edit();

                /*LastUpdate的SharedPreferences只需要三次写入,对应三种需要刷新list的情况:
                 * 第一次是这里,DB为空的时候;
                 * 第二次,打开后发现不为空,于是setup list并且更新;
                 第三次,用户下拉发现可以更新
                 */
                editor.putString("LastUpdateArchive", time.format(Calendar.getInstance().getTime()));
                editor.putString("ArchiveLastViewedDateChinese", mDateWithChinese);

                editor.apply();
                mSwipeRefreshLayout.setRefreshing(true);
                initiateDownloadToEmptyDB();
                //为防止自动刷新后,用户下拉刷新,需要更新下面的参数
                mLastUpdate = mSettings.getString("LastUpdateArchive", "198801011700");
                mLastUpdateInt = Long.parseLong(mLastUpdate);
            } else {

                //几处progressBar:
                //1.onCreateView发现数据库没内容时   正常 （pb在第一个card上）
                //2.onCreateView发现内容需要更新时   正常 （pb紧贴tab--->改成了find之后统一set）
                String lastViewedDateChinese
                        = mSettings.getString("ArchiveLastViewedDateChinese",
                        getContext().getString(R.string.page_last_read));
                setupList(lastViewedDateChinese);

//        if(true)
                refreshTime();
                if ((mCurrentTimeInt > mLatestWebsiteUpdateTimeInt &&
                        mLastUpdateInt < mLatestWebsiteUpdateTimeInt) || tryToConnectAgain) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshListView(getDate());
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("LastUpdateArchive", mCurrentTimeStr);
                    editor.apply();
                    if (getView() != null) {
                        Snackbar.make(getView(), getContext().getString(R.string.archive_updating),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        cursor.close();
        dbRead.close();
    }

    @Override
    public void onResume() {
        refreshTime();
        if (mCurrentTimeInt > mLatestWebsiteUpdateTimeInt
                && mLastUpdateInt < mLatestWebsiteUpdateTimeInt) {
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
                mDb = new Db(getContext());
                SQLiteDatabase dbRead = mDb.getReadableDatabase();
                Cursor cursor = dbRead.query("archive", null, null, null, null, null, null);

                if ((mCurrentTimeInt > mLatestWebsiteUpdateTimeInt
                        && mLastUpdateInt < mLatestWebsiteUpdateTimeInt) || (!cursor.moveToFirst()))
                    swipeRefresh();
                else if (null != getView()) {
                    Snackbar.make(getView(), getContext().getString(R.string.archive_already_latest),
                            Snackbar.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                cursor.close();
            }
        });
    }

    //refresh ListView(）一次调用这里,下拉刷新的时候;一次在启动时判断需要更新的情况;
    // 还有一次再SHUFFLE的时候;还有一次在item菜单
    private void swipeRefresh() {
        //DB的最近更新时间
        mSettings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mLastUpdate = mSettings.getString("LastUpdateArchive", "19880101700");//defValue - Value to return if this preference does not exist.
        mLastUpdateInt = Long.parseLong(mLastUpdate);
        //现在的时间
        mCurrentTime = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
        mCurrentTimeStr = mCurrentTime.format(Calendar.getInstance().getTime()).trim();
        mCurrentTimeInt = Long.parseLong(mCurrentTimeStr);
        //网站最近更新时间,今天早上五点
        mLatestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        mLatestWebsiteUpdateTimeInt = Long.parseLong(mLatestWebsiteUpdateTime.format
                (Calendar.getInstance().getTime()).trim() + "1700");

        mSwipeRefreshLayout.setRefreshing(true);
        refreshListView(getDate());

        mSettings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //第二次更新LastUpdate的SP,在下拉后决定刷新的时候
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("LastUpdateArchive", mCurrentTimeStr);
        editor.apply();
    }

    public void setupList(String mDateWithChinese) {
        mCardsListRv.setItemAnimator(new DefaultItemAnimator());
        mDb = new Db(getContext());
        SQLiteDatabase dbRead = mDb.getReadableDatabase();
        Cursor myCursor = dbRead.query("archive", null, null, null, null, null, null);
        ArchiveAdapter archiveAdapter = new ArchiveAdapter(getActivity(), "archive",
                myCursor.getCount(), mDateWithChinese, callBack);
        mCardsListRv.setAdapter(archiveAdapter);
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
                    JSONObject
                            root = new JSONObject(builder.toString());

                    //写入日期到database
                    mDb = new Db(getContext());
                    SQLiteDatabase dbRead = mDb.getInstance(getContext()).getReadableDatabase();
                    Cursor myCursor = dbRead.query("archive", null, null, null, null, null, null);
                    if (myCursor.moveToFirst()) {

                        SQLiteDatabase dbForDrop = mDb.getWritableDatabase();
                        dbForDrop.execSQL("DELETE FROM archive");
                        dbForDrop.execSQL("CREATE TABLE IF NOT EXISTS archive (_id integer primary key autoincrement,stitle text,ssummary text,squestionid text,sanswerid text)");
                        dbForDrop.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'archive';");
                        dbForDrop.close();

                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        for (int i = 0; i < root.getInt("count"); i++) {
                            JSONObject jo = array.getJSONObject(i);
                            LcData.setTitle(jo.getString("title"));
                            LcData.setSummary(jo.getString("summary"));
                            LcData.setQuestionid(jo.getString("questionid"));
                            LcData.setAnswerid(jo.getString("answerid"));
                            insertToTables(LcData, mTabName);
                        }
                    }
                    dbRead.close();
                    myCursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (null != getView())
                        Snackbar.make(getView(), "网络出了问题", Snackbar.LENGTH_INDEFINITE)
                                .setAction("刷新试试", new View.OnClickListener() {
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
                mDateWithChinese = dateStr.substring(0, 4) + "年" + dateStr.substring(4, 6) + "月"
                        + dateStr.substring(6, dateStr.length()) + "日";
                setupList(mDateWithChinese);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("ArchiveLastViewedDateChinese", mDateWithChinese);
                editor.apply();
                mSwipeRefreshLayout.setRefreshing(false);
                if (null != getView())
                    Snackbar.make(getView(), "加载完成!", Snackbar.LENGTH_LONG);
                mDb.close();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + dateStr + "/archive");//读今天的
//            }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160404" + "/archive");//读今天的
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
                        builder.append(line);
                    }
                    //读取完成,依次向上关闭连接
                    br.close();
                    isr.close();
                    is.close();
                    //获取答案数量
                    JSONObject root = new JSONObject(builder.toString());

                    //写入日期到database
                    mDb = new Db(getContext());
                    SQLiteDatabase dbRead = mDb.getInstance(getContext()).getReadableDatabase();
                    Cursor myCursor = dbRead.query("archive", null, null, null, null, null, null);
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
                            insertToTables(LcData, mTabName);
                        }
                    }
                    dbRead.close();
                    myCursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (null != getView())
                        Snackbar.make(getView(), "网络出了问题..", Snackbar.LENGTH_INDEFINITE)
                                .setAction("刷新试试", new View.OnClickListener() {
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
                setupList(mDateWithChinese);
                mSwipeRefreshLayout.setRefreshing(false);
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/archive");//读今天的
    }

    public void insertToTables(ListCellData data, String mTabName) {
        mDb = new Db(getContext());
        //WRITE
        SQLiteDatabase dbWrite = mDb.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", data.getTitle());
        cv.put("ssummary", data.getSummary());
        cv.put("squestionid", data.getQuestionid());
        cv.put("sanswerid", data.getAnswerid());

        dbWrite.insert(mTabName, null, cv);
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

                SharedPreferences.Editor editor = mSettings.edit();
                Date randomDate = Utilities.randomDate("20140919", getSystemDate());
                String randomDateStr = new SimpleDateFormat
                        ("yyyyMMdd", Locale.CHINA).format(randomDate);
                refreshListView(randomDateStr);
                mDateWithChinese = new SimpleDateFormat
                        ("yyyy年M月d日 E", Locale.CHINA).format(randomDate);

                editor.putString("ArchiveLastViewedDateChinese", mDateWithChinese);
                editor.apply();
                if (null != getView())
                    Snackbar.make(getView(), "时光机带您降落在 : " + mDateWithChinese + "",
                            Snackbar.LENGTH_LONG).show();
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
        if (Integer.parseInt(getCurrentTime()) < 1701)
            dateShouldBeReturned = getYesterdayDate();
        else if (Integer.parseInt(getCurrentTime()) >= 1701)
            dateShouldBeReturned = getSystemDate();
        return dateShouldBeReturned.trim();
    }

    private MainItemClickListener callBack = new MainItemClickListener() {

        @Override
        public void onMainItemClick(ListCellData answer) {

            SharedPreferences defaultSharedPreference = PreferenceManager.
                    getDefaultSharedPreferences(getContext());
            boolean doNotUseClient = defaultSharedPreference.getBoolean("doNotUseClient", true);

//            boolean disableJavascript = mSettings.getBoolean("disableJavascript", true);
            if (doNotUseClient) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("address", "http://www.zhihu.com/question/"
                        + answer.getQuestionid() + "/answer/" + answer.getAnswerid());
                intent.putExtra("title", answer.getTitle());
                intent.putExtra("summary", answer.getSummary());
                startActivity(intent);
            } else {
                Uri uri = Uri.parse("http://www.zhihu.com/question/" + answer.getQuestionid()
                        + "/answer/" + answer.getAnswerid());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        @Override
        public void onEndImageClick() {
            mCardsListRv.smoothScrollToPosition(0);
        }
    };

    @Override
    public void returnDate(String date, String date2) {

        long one = Long.parseLong(date);
        if (one < 20140919) {
            if (null != getView())
                Snackbar.make(getView(), "时光机到不了那里,请选择2014年9月19日之后的日期",
                        Snackbar.LENGTH_LONG).show();
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