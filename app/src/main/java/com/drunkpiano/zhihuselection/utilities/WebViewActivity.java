package com.drunkpiano.zhihuselection.utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.webkit.WebView;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/4/27.
 */
public class WebViewActivity extends Activity {
    String address = "http://www.zhihu.com" ;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        System.out.println("hellohellohellohellohello222");
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_web);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
//        myWebView.loadUrl(address);
//        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("http://www.baidu.com");
        myWebView.requestFocus();
        System.out.println("hellohellohellohellohello");
    }
}


//Uri uri = Uri.parse("http://www.google.com");
//Intent it  = new Intent(Intent.ACTION_VIEW,uri);
//    startActivity(it);