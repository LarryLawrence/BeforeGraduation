package com.drunkpiano.zhihuselection.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.AnimAdapter;

public class FavoritesActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv;
//    SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        toolbar.setTitle("收藏夹");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }

        rv = (RecyclerView) findViewById(R.id.fav_cards_list);
        rv.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new AnimAdapter(FavoritesActivity.this, "favorites"));
        System.out.println("this is Fav Activity onCreate!" +
                "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_clear_fav:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}