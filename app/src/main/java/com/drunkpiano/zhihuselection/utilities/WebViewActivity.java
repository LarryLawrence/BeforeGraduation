package com.drunkpiano.zhihuselection.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.drunkpiano.zhihuselection.R;
/**
 * Created by DrunkPiano on 16/4/27.
 */
public class WebViewActivity extends AppCompatActivity {
    String address = "http://www.zhihu.com" ;
    WebView myWebView ;
    Toolbar toolbar ;
    com.gc.materialdesign.views.ProgressBarIndeterminate progressBarIndeterminate ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        toolbar = (Toolbar) findViewById(R.id.toobar_custom);
        progressBarIndeterminate = (com.gc.materialdesign.views.ProgressBarIndeterminate)findViewById(R.id.web_progress);
//        toolbar.setTitle("Zhihu Selection");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        this.initMyWebView();
    }
    private void initMyWebView(){
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
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
}
