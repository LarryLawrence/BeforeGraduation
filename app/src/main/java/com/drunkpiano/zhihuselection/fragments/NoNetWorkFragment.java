package com.drunkpiano.zhihuselection.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/4/26.
 * 是否有必要加一个下拉刷新
 */
public class NoNetWorkFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.no_network_layout, container, false);
        return root;
    }
}
