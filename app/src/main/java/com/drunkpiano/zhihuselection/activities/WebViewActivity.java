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
import com.drunkpiano.zhihuselection.utilities.Utilities;

/**
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

    com.gc.materialdesign.views.ProgressBarIndeterminate progressBarIndeterminate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        progressBarIndeterminate = (com.gc.materialdesign.views.ProgressBarIndeterminate) findViewById(R.id.web_progress);
        toolbar.setTitle("答案");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_favorite);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean i = favoriteItemPressed();
                    if (!i)
                        snackMsg = "收藏成功";
                    else
                        snackMsg = "已经收藏过这一条";
                    Snackbar.make(view, snackMsg, Snackbar.LENGTH_SHORT)
                            .setAction("撤销收藏", new View.OnClickListener() {
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

    private void initMyWebView() {
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        title = intent.getStringExtra("title");
        summary = intent.getStringExtra("summary");
        myWebView = (WebView) findViewById(R.id.webView);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean disableJavascript = settings.getBoolean("disableJavascript", true);
        String loadImage = settings.getString(IMAGE_LOAD, "always");
        boolean loadIMG = true;
        if (myWebView != null) {
            myWebView.getSettings().setJavaScriptEnabled(!disableJavascript);
            System.out.println("disableJavascript---------------->" + disableJavascript);

            switch (loadImage) {
                case "always":
                    loadIMG = true;
                    break;
                case "ifWifi": {
                    if (Utilities.isNetworkAvailable(getApplicationContext()) && Utilities.isWifi(getApplicationContext()))
                        loadIMG = true;
                    else
                        loadIMG = false;
                    break;
                }
                case "never":
                    loadIMG = false;
            }
            System.out.println("setBlockNetworkImage---------------->"+loadImage+"-------"+loadIMG);
            myWebView.getSettings().setBlockNetworkImage(!loadIMG);
        }
        //启动缓存
        //        myWebView.getSettings().setAppCacheEnabled(true);
        //设置缓存模式
        //        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //        this.myWebView.setWebChromeClient();
        //顺序有关系?
        if (myWebView != null)
            myWebView.loadUrl(address);
        myWebView.setWebViewClient(new myWebViewClient());
    }

    class myWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//            return super.shouldOverrideKeyEvent(view, event);
//        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //添加评论页监听
            //intent://questions/37120133/#Intent;scheme=zhihu;package=com.zhihu.android;end
            String containMsg = address.substring(11, address.length());
            System.out.println("url------------->" + url);
            System.out.println("containsMsg-------1------->" + containMsg);

            if (url.contains("intent://questions/") && url.contains("com.zhihu.android")) {
                System.out.print("contains-------2------->" + containMsg);
                Uri uri = Uri.parse(address);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
            if (url.contains("comment")) {
                toolbar.setTitle("评论");

            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBarIndeterminate.setVisibility(WebView.GONE);
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
                Uri uri = Uri.parse(address);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
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
        System.out.println("收藏成功------>" + address);
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
