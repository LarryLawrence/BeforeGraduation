/*
 * This Activity is for displaying webView.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fix it to accord with standard coding disciplines;
 */

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

public class WebViewActivity extends AppCompatActivity {
    public final static String IMAGE_LOAD = "loadImagePreference";
    private String mAddress = "http://www.zhihu.com";
    private String mTitle = "";
    private String mSummary = "";
    private WebView myWebView;
    private Db mDb;
    private boolean mAlreadyStarred = false;
    private SwipeRefreshLayout mWebSwipeRefreshLayout;
    private LinearLayout mLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mLinearLayout = (LinearLayout) findViewById(R.id.web_ll);
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        mWebSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_web);
        if (null != mWebSwipeRefreshLayout) {
            mWebSwipeRefreshLayout.setColorSchemeResources(
                    R.color.swipe_color_1, R.color.swipe_color_2,
                    R.color.swipe_color_3, R.color.swipe_color_4);
            mWebSwipeRefreshLayout.setProgressViewOffset(false, 20, 80);
            mWebSwipeRefreshLayout.setRefreshing(true);
        }
        mWebSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myWebView.reload();
            }
        });
        if (null != toolbar) {
            toolbar.setTitle("答案");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        this.initMyWebView();
    }

    private void initMyWebView() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent intent = getIntent();
        mAddress = intent.getStringExtra("mAddress");
        mTitle = intent.getStringExtra("mTitle");
        mSummary = intent.getStringExtra("mSummary");
        myWebView = (WebView) findViewById(R.id.webView);

        //scrollBar
        if (null != myWebView) {
            myWebView.setVerticalScrollBarEnabled(true);
            myWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        }
        boolean disableJavascript = settings.getBoolean("disableJavascript", false);
        String loadImage = settings.getString(IMAGE_LOAD, "always");
        boolean loadIMG = true;
        if (myWebView != null) {
            myWebView.getSettings().setJavaScriptEnabled(!disableJavascript);
            switch (loadImage) {
                case "always":
                    loadIMG = true;
                    break;
                case "ifWifi": {
                    loadIMG = Utilities.isWifi(getApplicationContext());
                    break;
                }
                case "never":
                    loadIMG = false;
            }
            myWebView.getSettings().setBlockNetworkImage(!loadIMG);
        }
//        启动缓存
        myWebView.getSettings().setAppCacheEnabled(true);
//        设置缓存模式
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        if (myWebView != null)
            myWebView.loadUrl(mAddress);
        myWebView.setWebViewClient(new myWebViewClient());
    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebSwipeRefreshLayout.setRefreshing(true);//重定向答案的时候
            //http://www.zhihu.com/?next=%2Fquestion%2F45968097%2Fanswer%2F100778963
//            if(url.contains("?next="))
//            {
//                url = url.replace("%2F", "/");
//                System.out.println("first replace"+url);
//                url = url.replace("?next=/","");
//                System.out.println("second replace"+url);
//            }
            if (url.contains("?next=")) {
                Snackbar.make(mLinearLayout, "时光机的WebView没法解析这条回答；用知乎客户端打开应该可以看到。",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("好的，用知乎客户端查看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mAddress);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();
            }
            if (url.contains("intent://questions/") && url.contains("com.zhihu.android")) {
                Uri uri = Uri.parse(mAddress);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebSwipeRefreshLayout.setRefreshing(false);
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
        String snackMsg;
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "『" + mTitle + "』\n"
                        + "    " + mSummary + "\n" + mAddress + "\nvia「知乎每日精选」");
                startActivity(Intent.createChooser(shareIntent, "分享到："));
                break;
            case R.id.action_open_zhihu:
                Uri uri = Uri.parse(mAddress);
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
                Snackbar.make(mLinearLayout, snackMsg, Snackbar.LENGTH_SHORT)
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
                mWebSwipeRefreshLayout.setRefreshing(true);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean favoriteItemPressed() {
        mDb = new Db(getApplicationContext());
        SQLiteDatabase dbRead = mDb.getInstance(WebViewActivity.this).getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
        myCursor.moveToFirst();
        for (int i = 0; i < myCursor.getCount(); i++) {
            myCursor.moveToFirst();
            myCursor.move(i);
            String storedAddress = myCursor.getString(3);
            if (mAddress.trim().equals(storedAddress.trim())) {
                mAlreadyStarred = true;
                break;
            }
        }
        //空表的时候必定添加
        if (!mAlreadyStarred)
            addFavorite();
        dbRead.close();
        myCursor.close();
        return mAlreadyStarred;
    }

    private void addFavorite() {
        mDb = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = mDb.getInstance(getApplicationContext()).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", mTitle);
        cv.put("ssummary", mSummary);
        cv.put("saddress", mAddress);
        dbWrite.insert("favorites", null, cv);
        dbWrite.close();
    }

    private void removeFavorite() {
        mDb = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = mDb.getInstance(getApplicationContext()).getWritableDatabase();
        String whereClause = "saddress=?";
        String[] whereArgs = {mAddress};
        dbWrite.delete("favorites", whereClause, whereArgs);//没有content values
        dbWrite.close();
    }
}
