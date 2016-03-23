package com.drunkpiano.zhihuselection;

import android.app.Application;

/**
 * Created by DrunkPiano on 16/3/23.
 */
public class HeyApplication extends Application {
    private int yesterdayCount ;


    public int getYesterdayCount() {
        return yesterdayCount;
    }

    public void setYesterdayCount(int yesterdayCount) {
        this.yesterdayCount = yesterdayCount;
    }
}
