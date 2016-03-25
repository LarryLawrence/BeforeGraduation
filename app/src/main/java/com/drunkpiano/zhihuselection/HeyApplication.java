package com.drunkpiano.zhihuselection;

import android.app.Application;

/**
 * Created by DrunkPiano on 16/3/23.
 */
public class HeyApplication extends Application {
    private int yesterdayCount ;
    private int recentCount ;
    private int archiveCount ;

    public int getYesterdayCount() {
        return yesterdayCount;
    }

    public void setYesterdayCount(int yesterdayCount) {
        this.yesterdayCount = yesterdayCount;
    }

    public int getRecentCount() {
        return recentCount;
    }

    public void setRecentCount(int recentCount) {
        this.recentCount = recentCount;
    }

    public int getArchiveCount() {
        return archiveCount;
    }

    public void setArchiveCount(int archiveCount) {
        this.archiveCount = archiveCount;
    }
}
