package com.drunkpiano.zhihuselection.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ThirdFragment();
            case 1:
                return new RecentFragment();
            case 2:
                return new ThirdFragment();
            case 3:
                return new ThirdFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "日常";
            case 1:
                return "一周";
            case 2:
                return "经典";
            case 3:
                return "闲逛";
        }
        return null;
    }
}
