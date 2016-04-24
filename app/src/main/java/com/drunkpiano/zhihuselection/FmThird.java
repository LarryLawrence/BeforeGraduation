package com.drunkpiano.zhihuselection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by DrunkPiano on 16/3/10.
 */
public class FmThird extends Fragment {
    private RecyclerView rv ;
    protected RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String [] mDataset ;
    final int DATASET_COUNT = 100 ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        TextView tv=(TextView)rootView.findViewById(R.id.tv3);
//        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        View rootView = inflater.inflate(R.layout.fragment_recycler,container,false);
        rv = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);

        //define elements how to layout
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        initDataset();
        mAdapter = new MyAdapter(mDataset);
        rv.setAdapter(mAdapter);

        return rootView;
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }
}


