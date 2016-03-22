package com.drunkpiano.zhihuselection.utilities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/3/20.
 */
public class BBFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_test,container,false);
        System.out.println("BBFragment");
        Toast.makeText(getContext(), "bbFRAGMENT", Toast.LENGTH_SHORT).show();


        return  root ;
    }
}
