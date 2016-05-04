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
import android.widget.Toast;

import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellData;
import com.drunkpiano.zhihuselection.adapters.MyAdapter;
import com.drunkpiano.zhihuselection.R;
import com.gc.materialdesign.widgets.SnackBar;

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
public class RecentFragment extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    public SwipeRefreshLayout mSwipeRefreshLayout ;

//    ListView cardsList ;
    RecyclerView cardsListRv ;
    int numCount ;
    int originNumCountForCompare = 30 ;
    Db db ;
    String tabName = "recent";
    Long currentTimeInt ;
    Long latestWebsiteUpdateTimeInt ;
    SimpleDateFormat currentTime ;
    String lastUpdate ;
    SimpleDateFormat latestWebsiteUpdateTime ;
    Long lastUpdateInt ;
    SharedPreferences settings ;
    String currentTimeStr ;
    int dbLines = 0 ;


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
        numCount = settings.getInt("recentCount",20);//defValue - Value to return if this preference does not exist.
        originNumCountForCompare = numCount ; //这里先保存一份DB中原有的numCount的个数的副本用来对比,因为numCount可能会更新了等会儿（json中获取count时更新）.
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsListRv = (RecyclerView) root.findViewById(R.id.cards_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);//is getView() available? or do I need to inflate a view.

        cardsListRv.setLayoutManager(new LinearLayoutManager(getContext()));//这里用线性显示 类似于listview
//        cardsListRv.setLayoutManager(new GridLayoutManager(getContext(), 2));//这里用线性宫格显示 类似于grid view
//        cardsListRv.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        //现在尝试将FMRecent该造成不需要Bridge的情况
        //首先,判断recent table中是否有数据
        db = new Db(getContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor cursor = dbRead.query("recent", null, null, null, null, null, null);
        while(cursor.moveToNext())
            dbLines ++ ;
        if(!cursor.moveToFirst()) {
            //数据库中没有数据.那么,1.记录更新数据库的时间 2.下载数据
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmm");
            SharedPreferences.Editor editor = settings.edit();
            //LastUpdate的SharedPreferences只需要三次写入,对应三种需要刷新list的情况:第一次是这里,DB为空的时候;第二次,打开后发现不为空,于是setuplist并且更新;第三次,用户下拉发现可以更新
            editor.putString("LastUpdateRecent", time.format(Calendar.getInstance().getTime()));
            editor.apply();
            System.out.println("数据库里没东西,下载.");
            //我觉得它已经在执行了,因为有log,只是看不见而已
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, 24);
            mSwipeRefreshLayout.setRefreshing(true);
            initiateDownloadToEmptyDB();
        }
        else
        {
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
            if(currentTimeInt>latestWebsiteUpdateTimeInt && lastUpdateInt<latestWebsiteUpdateTimeInt)
                {
                    System.out.println("对应createView的时候判断出需要刷新的情况");
                    refreshListView();
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("LastUpdateRecent", currentTimeStr);
                    editor.apply();
                }
        }

//         Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        // END_INCLUDE (change_colors)

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                initiateRefresh();
            }
        });
    }
    public void initiateRefresh() {
        new SwipeRefreshBackgroundTask().execute();
    }
    private void initiateDownloadToEmptyDB(){
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        System.out.println("initiateDownloadToEmptyDB");
//        new DownloadToEmptyDbBackgroundTask().execute();
        downloadJSONAndUpdateDB();
    }

    private void onRefreshComplete() {

        lastUpdate = settings.getString("LastUpdateRecent", "198801010500");//defValue - Value to return if this preference does not exist.
        lastUpdateInt = Long.parseLong(lastUpdate);

        System.out.println("onRefreshComplete");
        if(!(currentTimeInt>latestWebsiteUpdateTimeInt && lastUpdateInt<latestWebsiteUpdateTimeInt))
        {
            System.out.println("已经是最新的内容了");

//            Toast.makeText(getActivity(),"「一周」板块每天11:00更新~",Toast.LENGTH_SHORT).show();
//            Toast.makeText(getActivity(),"!lastUpdateInt---->"+lastUpdateInt+"\nlatestWebsiteUpdateTimeInt---->"+latestWebsiteUpdateTimeInt+"\ncurrentTimeInt-->"+currentTimeInt,Toast.LENGTH_LONG).show();

        }
        else{
//            Toast.makeText(getActivity(),"?lastUpdateInt---->"+lastUpdateInt+"\nlatestWebsiteUpdateTimeInt---->"+latestWebsiteUpdateTimeInt+"\ncurrentTimeInt-->"+currentTimeInt,Toast.LENGTH_LONG).show();

//            Toast.makeText(getActivity(),"Loading Complete!",Toast.LENGTH_SHORT).show();
            //更新动作不是在这里哦 这里已经刷新完成
        }
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setupList() {
//        cardsList.setAdapter(createAdapter());
//        cardsList.setOnItemClickListener(new MyItemOnClickListener());
        cardsListRv.setItemAnimator(new DefaultItemAnimator());
        cardsListRv.setAdapter(createAdapter());
//        cardsListRv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL ));

    }

    private MyAdapter createAdapter(){
        return new MyAdapter(getActivity(),"recent",numCount);
    }

//    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
//        }
//    }
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
                    try {
                        SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("recentCount", numCount);
                        editor.apply();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("e--->"+e.toString());
                    }
                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
                    if(myCursor.moveToFirst()){
                        JSONArray array = root.getJSONArray("answers");//获取数组
                        ListCellData LcData = new ListCellData();
                        //这里的numCount已经是本次获取的条目数
                        if(numCount>originNumCountForCompare)
                            numCount = originNumCountForCompare ; //今天的条目比昨天的多,那么把先update已有条目,再insert新的.
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
                            insertToTables(LcData , tabName);
//                            System.out.println("-------->before update");
//                            updateTables(LcData, tabName, i + 1);
                        }
//                        if(numCount<originNumCountForCompare)//今天的条目比昨天少
//                        {
//                            for(int i = numCount + 1 ; i < originNumCountForCompare ; i ++)
//                            deleteDBLines(tabName, i );
//                        }
                        System.out.println(numCount+"--=====================++===============---"+dbLines);
                        if(numCount<dbLines)//今天的条目比db中的少
                        {
                            for(int i = numCount + 1 ; i < dbLines + 1 ; i ++)
                                deleteDBLines(tabName, i );
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
                System.out.println("refresh   4   postExecute```````,numCount= " + numCount);
//                pb.setVisibility(View.GONE);
                setupList();
                db.close();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/recent");//读今天的
//            }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/recent");//读今天的
    }

    private class SwipeRefreshBackgroundTask extends AsyncTask<Void ,Void , Void>{
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
            if(true)//强制下拉每次刷新
            {
//                System.out.println("!lastUpdateInt---->" + lastUpdateInt + "\nlatestWebsiteUpdateTimeInt---->" + latestWebsiteUpdateTimeInt + "\ncurrentTimeInt-->" + currentTimeInt);
//                Toast.makeText(getActivity(),"!lastUpdateInt---->"+lastUpdateInt+"\nlatestWebsiteUpdateTimeInt---->"+latestWebsiteUpdateTimeInt+"\ncurrentTimeInt-->"+currentTimeInt,Toast.LENGTH_LONG).show();


                refreshListView();
                System.out.println("正在更新..");
//                Toast.makeText(getActivity(),"正在更新..",Toast.LENGTH_SHORT).show();
                //改写最后更新时间
                settings = getActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                //第二次更新LastUpdate的SP,在下拉后决定刷新的时候
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("LastUpdateRecent", currentTimeStr);
                editor.apply();
            }
            else
            {
                try {
                    Thread.sleep(TASK_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null ;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onRefreshComplete();
        }
    }
//
//    private class DownloadToEmptyDbBackgroundTask extends AsyncTask <Void, Void, Void>{
//        @Override
//        protected Void doInBackground(Void... params) {
//            downloadJSONAndUpdateDB();
//            return null;
//        }
//
////        @Override
////        protected void onPostExecute(Void aVoid) {
////            super.onPostExecute(aVoid);
////            mSwipeRefreshLayout.setRefreshing(false);
////        }
//    }
    public void deleteDBLines(String tabName , int ids){
        db = new Db(getContext());
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        String whereClause = "_id=?";
        String [] whereArgs = {String.valueOf(ids)};
        dbWrite.delete(tabName, whereClause , whereArgs);//没有cv
        dbWrite.close();
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

    }
    private static String getDate(){
        String dateShouldBeReturned = "" ;
        if(Integer.parseInt(getCurrentTime())<1101)
            dateShouldBeReturned = getYesterdayDate() ;
        else if(Integer.parseInt(getCurrentTime())>=1101)
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
                    SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("recentCount",numCount) ;
                    editor.apply();

                    //写入日期到database
                    db = new Db(getContext());
                    SQLiteDatabase dbRead = db.getReadableDatabase();
                    Cursor myCursor = dbRead.query("recent", null, null, null, null, null, null);
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
                //setupList放在这里,可以保证下载完成再setuplist.注意,如果在这个asyncTask外面再嵌套一个asyncTask,上层的postExecute不会等这一层的执行完才执行!是两个线程了.
                setupList();
//                Toast.makeText(getActivity(),"Init Success.",Toast.LENGTH_SHORT).show();

                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(getView(),"「一周」栏目每天11:00更新", Snackbar.LENGTH_SHORT).show();
                //只需要下载就可以了
//                System.out.println("postExecute BB```````,numCount= " + numCount);
////                pb.setVisibility(View.GONE);
//                getChildFragmentManager().beginTransaction().replace(R.id.bridge_container, new FMYesterday()).commitAllowingStateLoss();
                super.onPostExecute(aVoid);
            }
        }.execute("http://api.kanzhihu.com/getpostanswers/" + getDate() + "/recent");//读今天的
//    }.execute("http://api.kanzhihu.com/getpostanswers/" + "20160405" + "/recent");//读今天的
    }

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
    }


    @Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_main, menu);
}

    // BEGIN_INCLUDE (setup_refresh_menu_listener)
    /**
     * Respond to the user's selection of the Refresh action item. Start the SwipeRefreshLayout
     * progress bar, then initiate the background task that refreshes the content.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
//                Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
                System.out.println("刷新");
                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                // Start our refresh background task
                initiateRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
