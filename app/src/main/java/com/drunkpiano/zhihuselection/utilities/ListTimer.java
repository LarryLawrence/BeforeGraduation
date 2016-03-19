package com.drunkpiano.zhihuselection.utilities;

/**
 * Created by DrunkPiano on 16/3/18.
 */
import com.drunkpiano.zhihuselection.CardsAdapter;
import com.drunkpiano.zhihuselection.FmSecond;

import java.util.Timer;
import java.util.TimerTask;

public class ListTimer extends TimerTask {
    Timer timer = new Timer();

    @Override
    public void run() {
//        System.out.println("execute " + jobName);
        CardsAdapter ca = new CardsAdapter();
        if(ca.QueryData()) timer.cancel();
        FmSecond fs = new FmSecond() ;
    }

    public void timerStarts() {
        long delay1 = 1 * 1000;
        long period1 = 1000;
        // 从现在开始 1 秒钟之后，每隔 1 秒钟执行一次 job1
        timer.schedule(new ListTimer(), delay1, period1);
        long delay2 = 2 * 1000;
        long period2 = 2000;
        // 从现在开始 2 秒钟之后，每隔 2 秒钟执行一次 job2
        timer.schedule(new ListTimer(), delay2, period2);
    }
}