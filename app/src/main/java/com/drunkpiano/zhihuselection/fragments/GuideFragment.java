package com.drunkpiano.zhihuselection.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.adapters.GuideAdapter;

public class GuideFragment extends Fragment {

    RecyclerView rv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_favorites, container, false);
        rv = (RecyclerView) v.findViewById(R.id.fav_cards_list);//在v里面找
        GuideAdapter guideAdapter = new GuideAdapter(getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        rv.setAdapter(guideAdapter);
        return v ;
    }

}
