package com.drunkpiano.zhihuselection.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.fragments.SettingsFragment;

public class AnyActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any);
        toolbar = (Toolbar) findViewById(R.id.any_toolbar);

        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.any_container, new SettingsFragment())
                .commit();
    }

}
