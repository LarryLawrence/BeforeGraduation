/*
 * This Activity is for displaying user's favorite items.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fix it to accord with standard coding disciplines;
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

public class FavoritesActivity extends AppCompatActivity implements MyItemClickListener {
    private FavoritesAdapter mFavoritesAdapter;
    private RecyclerView mRecyclerView;
    private Db mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mDb = new Db(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        mRecyclerView = (RecyclerView) findViewById(R.id.fav_cards_list);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fav_text);

        if (toolbar != null) {
            toolbar.setTitle(getApplicationContext().getString(R.string.toolbar_activity_favorite));
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        SQLiteDatabase dbRead = mDb.getInstance(getApplicationContext()).getReadableDatabase();
        Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
        if (myCursor.getCount() != 0) {
            if (null != frameLayout) {
                frameLayout.setVisibility(View.INVISIBLE);
            }
        }
        myCursor.close();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFavoritesAdapter = new FavoritesAdapter(FavoritesActivity.this, "favorites");
        mRecyclerView.setAdapter(mFavoritesAdapter);
        mFavoritesAdapter.setOnLongClickListener(this);
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
                mDb = new Db(getApplicationContext());
                SQLiteDatabase dbRead = mDb.getInstance(getApplicationContext()).getReadableDatabase();
                Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
                int num = myCursor.getCount();
                for (int i = 0; i < num; i++)

                    //永远删除首位的item
                    onInterfaceItemLongClick(0);
                myCursor.close();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInterfaceItemLongClick(int position) {
        mFavoritesAdapter.remove(position);
        Snackbar.make(mRecyclerView, getApplicationContext().getString(R.string.snack_bar_already_removed), Snackbar.LENGTH_SHORT).show();
    }
}