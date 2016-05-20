package com.drunkpiano.zhihuselection.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.drunkpiano.zhihuselection.fragments.ArchiveFragment;
import com.drunkpiano.zhihuselection.fragments.NoNetworkFragment;
import com.drunkpiano.zhihuselection.fragments.RecentFragment;
import com.drunkpiano.zhihuselection.fragments.YesterdayFragment;
import com.drunkpiano.zhihuselection.utilities.Utilities;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (Utilities.isNetworkAvailable(context))
                    return new YesterdayFragment();
                else
                    return new NoNetworkFragment();
            case 1:
                if (Utilities.isNetworkAvailable(context))
                    return new RecentFragment();
                else
                    return new NoNetworkFragment();
            case 2:
                if (Utilities.isNetworkAvailable(context))
                    return new ArchiveFragment();
                else
                    return new NoNetworkFragment();
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
