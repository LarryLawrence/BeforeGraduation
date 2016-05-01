package com.drunkpiano.zhihuselection.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
    boolean alreadyStarred = false ;

    com.gc.materialdesign.views.ProgressBarIndeterminate progressBarIndeterminate ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar) findViewById(R.id.toobar_custom);
        progressBarIndeterminate = (com.gc.materialdesign.views.ProgressBarIndeterminate)findViewById(R.id.web_progress);
        toolbar.setTitle("答案");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

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
        getMenuInflater().inflate(R.menu.web,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break ;
            case R.id.action_star :
                favoriteItemPressed();
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

    private void favoriteItemPressed(){
        db = new Db(getApplicationContext());
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
        if(myCursor.moveToFirst())
        {
            for(int i = 0 ; i < myCursor.getCount(); i ++)
            {
                myCursor.move(i);
                String storedAddress = myCursor.getString(3);
                System.out.println(address);
                if(address == storedAddress )
                    alreadyStarred = true ;
            }
        }
        if(alreadyStarred)
            addFavorite();
        else
            removeFavorite();
        dbRead.close();
    }
    private void addFavorite(){
        db = new Db(getApplicationContext());
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stitle", title);
        cv.put("ssummary", summary);
        cv.put("saddress", address);
        dbWrite.insert("favorites", null, cv);
        dbWrite.close();

    }

    private void removeFavorite(){

    }
}
