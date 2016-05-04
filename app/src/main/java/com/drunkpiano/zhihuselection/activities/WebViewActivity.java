package com.drunkpiano.zhihuselection.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.utilities.Db;

/**
 * Created by DrunkPiano on 16/4/27.
 */
public class WebViewActivity extends AppCompatActivity {
    String address = "http://www.zhihu.com" ;
    String title = "";
    String summary = "";
    WebView myWebView ;
    Toolbar toolbar ;
    Db db ;
    String snackMsg;
    boolean alreadyStarred = false ;

    com.gc.materialdesign.views.ProgressBarIndeterminate progressBarIndeterminate ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        progressBarIndeterminate = (com.gc.materialdesign.views.ProgressBarIndeterminate)findViewById(R.id.web_progress);
        toolbar.setTitle("答案");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_favorite);
        if(fab!= null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean i  = favoriteItemPressed();
                    if(!i)
                        snackMsg = "收藏成功";
                    else
                        snackMsg = "已经收藏过这一条";
                    Snackbar.make(view, snackMsg, Snackbar.LENGTH_SHORT)
                            .setAction("撤销收藏", new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    System.out.println("撤销收藏");
                                    removeFavorite();
                                    // Perform anything for the action selected
                                }
                            }).show();
                }
            });

        this.initMyWebView();
    }
    private void initMyWebView(){
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        title = intent.getStringExtra("title");
        summary = intent.getStringExtra("summary");
        this.myWebView = (WebView) findViewById(R.id.webView);
        this.myWebView.setWebViewClient(new myWebViewClient());
//        this.myWebView.setWebChromeClient();
        this.myWebView.loadUrl(address);
    }
    class myWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return super.shouldOverrideUrlLoading(view, url);
            view.loadUrl(url);
            return true ;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBarIndeterminate.setVisibility(WebView.GONE);
            super.onPageFinished(view, url);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()){
            myWebView.goBack();
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
            case R.id.action_share :
                System.out.println(id);
                break ;
            case R.id.action_refresh:
                System.out.println(id);
                break ;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean favoriteItemPressed(){
        db = new Db(getApplicationContext());
        SQLiteDatabase dbRead = db.getInstance(WebViewActivity.this).getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
//        myCursor.moveToFirst();
        System.out.println("cursorcount===========>"+myCursor.getCount());
        //
        myCursor.moveToFirst();
//        {
        //这段for循环,在空表的时候不会执行
        //如果不为空,顺序搜索
            for(int i = 0 ; i < myCursor.getCount()   ; i ++)
            {
                myCursor.moveToFirst();
                myCursor.move(i);
//                myCursor.moveToNext();
//                myCursor.move(i);  错误表达!
                String storedAddress = myCursor.getString(3);
                System.out.println("gddress.trim()"+address.trim());
                System.out.println("storedAddress.trim()------>"+storedAddress.trim());
                //String compare 不能用 等于号!!!
                if(address.trim().equals(storedAddress.trim())  ) {
                    System.out.println("有了地址------>"+address);
                    alreadyStarred = true ;
                    break;
                }
            }
        //空表的时候必定添加
        if(!alreadyStarred)
            addFavorite();
//        else
//            removeFavorite();
        dbRead.close();

        return alreadyStarred ;
    }
    private void addFavorite(){
        db = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = db.getInstance(getApplicationContext()).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", title);
        cv.put("ssummary", summary);
        cv.put("saddress", address);
        dbWrite.insert("favorites", null, cv);
        dbWrite.close();
        System.out.println("收藏成功------>" + address);
    }

    private void removeFavorite(){
        db = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = db.getInstance(getApplicationContext()).getWritableDatabase();
        String whereClause = "saddress=?";
        String [] whereArgs = {address};
        dbWrite.delete("favorites", whereClause , whereArgs);//没有cv
        dbWrite.close();

    }
}
