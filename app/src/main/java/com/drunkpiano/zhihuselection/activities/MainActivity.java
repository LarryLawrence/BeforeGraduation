package com.drunkpiano.zhihuselection.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.SectionsPagerAdapter;
import com.drunkpiano.zhihuselection.utilities.Utilities;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static boolean netWorkAvailable = false;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#14000000"));
            //setStatusBarColor在v21/styles.xml中设置了（其实无需设置,因为可以沿用5.0以下配色）
            getWindow().setNavigationBarColor(Color.parseColor("#C33A29"));
        }
        SectionsPagerAdapter mSectionsPagerAdapter;
        ViewPager mViewPager;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (null != mViewPager)
            mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (null != tabLayout)
            tabLayout.setupWithViewPager(mViewPager);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean user_first = settings.getBoolean("FirstLaunch", true);//defValue - Value to return if this preference does not exist.
        if (user_first) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FirstLaunch", false);
            editor.apply();
            System.out.println("first launch");
        } else {
            System.out.println("not first launch");
        }
        if (Utilities.isNetworkAvailable(MainActivity.this)) {
            netWorkAvailable = true;
            System.out.println("有网络有网络有网络有网络有网络有网络有网络");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (null != drawer)
//        drawer.setDrawerListener(toggle);
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (null != navigationView) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().findItem(R.id.nav_favorites).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    item.setChecked(false);
                    return false;
                }
            });
            navigationView.getMenu().findItem(R.id.nav_about).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    item.setChecked(false);
                    return false;
                }
            });
            navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    item.setChecked(false);
                    return false;
                }
            });
            navigationView.getMenu().findItem(R.id.nav_guide).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    item.setChecked(false);
                    return false;
                }
            });
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (id == R.id.nav_favorites) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_guide) {
                Toast.makeText(MainActivity.this, "guide", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_about) {
                Toast.makeText(MainActivity.this, "nav_slideshow", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, AnyActivity.class);
                startActivity(intent);

            }
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
