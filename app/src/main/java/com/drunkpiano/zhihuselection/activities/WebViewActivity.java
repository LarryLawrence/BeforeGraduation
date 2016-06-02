package com.drunkpiano.zhihuselection.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.Utilities;

/*
 * Created by DrunkPiano on 16/4/27.
 */

public class WebViewActivity extends AppCompatActivity {
    public final static String IMAGE_LOAD = "loadImagePreference";
    String address = "http://www.zhihu.com";
    String title = "";
    String summary = "";
    WebView myWebView;
    Toolbar toolbar;
    Db db;
    String snackMsg;
    boolean alreadyStarred = false;
    SwipeRefreshLayout webSwipeRefreshLayout;
    LinearLayout linearLayout ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        linearLayout = (LinearLayout)findViewById(R.id.web_ll);
        toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        webSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_web);
        if (null != webSwipeRefreshLayout) {
            webSwipeRefreshLayout.setColorSchemeResources(
                    R.color.swipe_color_1, R.color.swipe_color_2,
                    R.color.swipe_color_3, R.color.swipe_color_4);
            webSwipeRefreshLayout.setProgressViewOffset(false, 20, 80);
            webSwipeRefreshLayout.setRefreshing(true);
        }
        webSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myWebView.reload();
            }
        });

        toolbar.setTitle("答案");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }

        this.initMyWebView();
//        myWebView.scrollTo(0, 600);
    }


    private void initMyWebView() {
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
//        System.out.println("address in webactivity" + address);
        title = intent.getStringExtra("title");
        summary = intent.getStringExtra("summary");
        myWebView = (WebView) findViewById(R.id.webView);
        //scrollBar
        if (null != myWebView) {
            myWebView.setVerticalScrollBarEnabled(true);
            myWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean disableJavascript = settings.getBoolean("disableJavascript", false);
        String loadImage = settings.getString(IMAGE_LOAD, "always");
        boolean loadIMG = true;
        if (myWebView != null) {
            myWebView.getSettings().setJavaScriptEnabled(!disableJavascript);
//            System.out.println("disableJavascript---------------->" + disableJavascript);
            switch (loadImage) {
                case "always":
                    loadIMG = true;
                    break;
                case "ifWifi": {
                    if (Utilities.isWifi(getApplicationContext()))
//                    if (Utilities.isNetworkAvailable(getApplicationContext()) && Utilities.isWifi(getApplicationContext()))
                        loadIMG = true;
                    else
                        loadIMG = false;
                    break;
                }
                case "never":
                    loadIMG = false;
            }
//            System.out.println("setBlockNetworkImage---------------->" + loadImage + "-------" + loadIMG);
            myWebView.getSettings().setBlockNetworkImage(!loadIMG);
//            myWebView.addJavascriptInterface();
        }

//        启动缓存
                myWebView.getSettings().setAppCacheEnabled(true);
//        设置缓存模式
                myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if (myWebView != null)
            myWebView.loadUrl(address);
//        System.out.println("setBlockNetworkImage---------------->" + address);
        myWebView.setWebViewClient(new myWebViewClient());
    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        webSwipeRefreshLayout.setRefreshing(true);//重定向答案的时候
            //http://www.zhihu.com/?next=%2Fquestion%2F45968097%2Fanswer%2F100778963
//            if(url.contains("?next="))
//            {
//                url = url.replace("%2F", "/");
//                System.out.println("first replace"+url);
//                url = url.replace("?next=/","");
//                System.out.println("second replace"+url);
//            }
         if(url.contains("?next=")) {
             Snackbar.make(linearLayout,"时光机的WebView没法解析这条回答；用知乎客户端打开应该可以看到。",Snackbar.LENGTH_INDEFINITE).setAction("好的，用知乎客户端查看", new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Uri uri = Uri.parse(address);
                     Intent intent = new Intent();
                     intent.setAction(Intent.ACTION_VIEW);
                     intent.setData(uri);
                     startActivity(intent);
                 }
             }).show();
         }
            if (url.contains("intent://questions/") && url.contains("com.zhihu.android")) {
                Uri uri = Uri.parse(address);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
//            if (url.contains("comment")) {
//                toolbar.setTitle("评论");
//            }
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            progressBarIndeterminate.setVisibility(WebView.GONE);
            webSwipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(view, url);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
//                if (myWebView.canGoBack()) {
//                    myWebView.goBack();
//                    break;
//                }
                onBackPressed();
                break;
            case R.id.action_share:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "『" + title + "』\n" + "    " + summary + "\n" + address + "\nvia「知乎每日精选」");
                startActivity(Intent.createChooser(shareIntent, "分享到："));
                break;
            case R.id.action_open_zhihu:
//                System.out.println("address------>" + address);
                Uri uri = Uri.parse(address);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                break;

            case R.id.action_web_add_to_fav:
                boolean i = favoriteItemPressed();
                if (!i)
                    snackMsg = "收藏成功";
                else
                    snackMsg = "已经收藏过这一条";
                Snackbar.make(linearLayout, snackMsg, Snackbar.LENGTH_SHORT)
                        .setAction("撤销收藏", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("撤销收藏");
                                removeFavorite();
                                // Perform anything for the action selected
                            }
                        }).show();
                break;
            case R.id.action_web_refresh:
                myWebView.reload();
                webSwipeRefreshLayout.setRefreshing(true);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean favoriteItemPressed() {
        db = new Db(getApplicationContext());
        SQLiteDatabase dbRead = db.getInstance(WebViewActivity.this).getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
//        myCursor.moveToFirst();
        System.out.println("cursorcount===========>" + myCursor.getCount());
        //
        myCursor.moveToFirst();
//        {
        //这段for循环,在空表的时候不会执行
        //如果不为空,顺序搜索
        for (int i = 0; i < myCursor.getCount(); i++) {
            myCursor.moveToFirst();
            myCursor.move(i);
//                myCursor.moveToNext();
//                myCursor.move(i);  错误表达!
            String storedAddress = myCursor.getString(3);
            System.out.println("gddress.trim()" + address.trim());
            System.out.println("storedAddress.trim()------>" + storedAddress.trim());
            //String compare 不能用 等于号!!!
            if (address.trim().equals(storedAddress.trim())) {
                System.out.println("有了地址------>" + address);
                alreadyStarred = true;
                break;
            }
        }
        //空表的时候必定添加
        if (!alreadyStarred)
            addFavorite();
//        else
//            removeFavorite();
        dbRead.close();
        myCursor.close();
        return alreadyStarred;
    }

    private void addFavorite() {
        db = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = db.getInstance(getApplicationContext()).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", title);
        cv.put("ssummary", summary);
        cv.put("saddress", address);
        dbWrite.insert("favorites", null, cv);
        dbWrite.close();
//        System.out.println("收藏成功------>" + address);
    }

    private void removeFavorite() {
        db = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = db.getInstance(getApplicationContext()).getWritableDatabase();
        String whereClause = "saddress=?";
        String[] whereArgs = {address};
        dbWrite.delete("favorites", whereClause, whereArgs);//没有cv
        dbWrite.close();

    }
}
