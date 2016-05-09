package com.drunkpiano.zhihuselection.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.AnimAdapter;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.MyItemClickListener;

public class FavoritesActivity extends AppCompatActivity implements MyItemClickListener {
    private AnimAdapter animAdapter;
    Toolbar toolbar;
    RecyclerView rv;
    Db db;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
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

        if (myCursor.getCount() != 0)
////            Snackbar.make(getView(),)
        {
//            setContentView(R.layout.activity_favorites);
            frameLayout.setVisibility(View.INVISIBLE);
        }
        myCursor.close();


        rv = (RecyclerView) findViewById(R.id.fav_cards_list);
        rv.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        animAdapter = new AnimAdapter(FavoritesActivity.this, "favorites");
        rv.setAdapter(animAdapter);
        animAdapter.setOnLongClickListener(this);
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
                db = new Db(getApplicationContext());
                SQLiteDatabase dbRead = db.getInstance(getApplicationContext()).getReadableDatabase();
                Cursor myCursor = dbRead.query("favorites", null, null, null, null, null, null);
                int num = myCursor.getCount();
                System.out.println("getCount--------->" + num);
                for (int i = 0; i < num; i++)
                    onInterfaceItemLongClick(0);
                myCursor.close();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInterfaceItemLongClick(int position) {

//        new android.support.v7.app.AlertDialog.Builder(this).setMessage("移除这一条").setPositiveButton("好的", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
        animAdapter.remove(position);
//
//            }
//        }).setNegativeButton("不用", new)
    }
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        android.support.v7.app.AlertDialog builder = new android.support.v7.app.AlertDialog(FavoritesActivity.this);
//        builder.setMessage("移除这一条")
//                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton("不用", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }
}