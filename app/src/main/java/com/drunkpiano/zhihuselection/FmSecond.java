package com.drunkpiano.zhihuselection;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FmSecond extends Fragment{
    TextView tv ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_first,container,false);
        tv = (TextView)root.findViewById(R.id.tv1);
        tv.setText("second");
        return root;
    }
}
