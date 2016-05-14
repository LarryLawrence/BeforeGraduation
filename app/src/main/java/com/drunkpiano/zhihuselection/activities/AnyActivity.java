package com.drunkpiano.zhihuselection.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.fragments.GuideFragment;
import com.drunkpiano.zhihuselection.fragments.SettingsFragment;
import com.drunkpiano.zhihuselection.fragments.AboutFragment;

public class AnyActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any);
        toolbar = (Toolbar) findViewById(R.id.any_toolbar);
        toolbar.setTitle("答案");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        if (null != toolbar)
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("fragmentName");
        String titleName ;
        toolbar.setTitle("你好");

        if (fragmentName.equals("settings")) {
            titleName = "设置";
            toolbar.setTitle(titleName);//这个一定放在fragment transaction之前..

            getFragmentManager().beginTransaction()
                    .replace(R.id.any_container, new SettingsFragment())
                    .commit();
//            toolbar.setTitle(titleName);
        }
        else if(fragmentName.equals("about")){
            titleName = "关于";
            toolbar.setTitle(titleName);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.any_container, new AboutFragment())
                    .commit();
        }
        else if(fragmentName.equals("guide")) {
            titleName = "指南";
            toolbar.setTitle(titleName);//这个一定放在fragment transaction之前..

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.any_container, new GuideFragment())
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
