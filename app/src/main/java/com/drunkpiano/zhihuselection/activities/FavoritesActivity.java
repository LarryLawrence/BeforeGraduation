/*
 * Copyright (c) 2016.
 * com.drunkpiano.zhihuselection.activities.FavoriteActivity
 * version 1.1.2
 * DrunkPiano All Rights Reserved
 */
package com.drunkpiano.zhihuselection.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.FavoritesAdapter;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.MyItemClickListener;

/*The Activity for displaying user's favorite items.*/
public class FavoritesActivity extends AppCompatActivity implements MyItemClickListener {
    private FavoritesAdapter favoritesAdapter;
    private RecyclerView rv;
    private Db db;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        frameLayout = (FrameLayout) findViewById(R.id.fav_text);
        if (toolbar != null) {
            toolbar.setTitle("收藏夹");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        db = new Db(getApplicationContext());
        SQLiteDatabase dbRead = db.getInstance(getApplicationContext()).getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
        if (myCursor.getCount() != 0) {
            frameLayout.setVisibility(View.INVISIBLE);
        }
        myCursor.close();
        rv = (RecyclerView) findViewById(R.id.fav_cards_list);
        rv.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        favoritesAdapter = new FavoritesAdapter(FavoritesActivity.this, "favorites");
        rv.setAdapter(favoritesAdapter);
        favoritesAdapter.setOnLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();//局部变量和第一条语句之前 空行

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_clear_fav:
                db = new Db(getApplicationContext());
                SQLiteDatabase dbRead = db.getInstance(getApplicationContext()).getReadableDatabase();
                Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
                int num = myCursor.getCount();
                for (int i = 0; i < num; i++)
                    onInterfaceItemLongClick(0);
                myCursor.close();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInterfaceItemLongClick(int position) {
        favoritesAdapter.remove(position);
        Snackbar.make(rv, "已移除", Snackbar.LENGTH_SHORT).show();
    }
}