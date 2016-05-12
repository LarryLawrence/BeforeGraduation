package com.drunkpiano.zhihuselection.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drunkpiano.zhihuselection.fragments.ArchiveFragment;
import com.drunkpiano.zhihuselection.fragments.RecentFragment;
import com.drunkpiano.zhihuselection.fragments.YesterdayFragment;

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
                return new YesterdayFragment();
            case 1:
                return new RecentFragment();
            case 2:
                return new ArchiveFragment();

        }
        return null;
    }
    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "昨天";
            case 1:
                return "上周";
            case 2:
                return "往年";
        }
        return null;
    }
}
