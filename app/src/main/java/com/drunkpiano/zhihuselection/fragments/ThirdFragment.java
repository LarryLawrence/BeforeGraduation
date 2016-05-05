package com.drunkpiano.zhihuselection.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/3/10.
 */
public class ThirdFragment extends Fragment {
    private RecyclerView rv ;
    protected RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String [] mDataset ;
    final int DATASET_COUNT = 100 ;
    CardView cardView ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        TextView tv=(TextView)rootView.findViewById(R.id.tv3);
//        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        View rootView = inflater.inflate(R.layout.card_view,container,false);
//        rv = (RecyclerView)rootView.findViewById(R.id.recyclerView);
//        rv.setHasFixedSize(true);
        cardView = (CardView) rootView.findViewById(R.id.card_view);
        SeekBar seek1 = (SeekBar) rootView.findViewById(R.id.seek1);
        SeekBar seek2 = (SeekBar) rootView.findViewById(R.id.seek2);
//        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if (b) {
//                    cardView.setCardElevation(i);//shadow
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if (b) {
//                    cardView.setRadius(i);//圆角大小设置
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        //define elements how to layout
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        rv.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        initDataset();
//        rv.setAdapter(mAdapter);

        return rootView;
    }

    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }

}


