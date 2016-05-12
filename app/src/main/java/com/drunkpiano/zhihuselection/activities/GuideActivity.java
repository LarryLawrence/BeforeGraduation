package com.drunkpiano.zhihuselection.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.GuideAdapter;
import com.drunkpiano.zhihuselection.utilities.DividerItemDecoration;

public class GuideActivity extends AppCompatActivity {
    private GuideAdapter guideAdapter;
    Toolbar toolbar;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        toolbar = (Toolbar) findViewById(R.id.toolbar_guide);
        if (toolbar != null) {
            toolbar.setTitle("指南");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        rv = (RecyclerView) findViewById(R.id.guide_cards_list);
        rv.setLayoutManager(new LinearLayoutManager(GuideActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        guideAdapter = new GuideAdapter(getApplicationContext());
        rv.setAdapter(guideAdapter);
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