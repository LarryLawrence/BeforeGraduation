package com.drunkpiano.zhihuselection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by DrunkPiano on 16/3/10.
 */
public class FmThird extends Fragment {
    private ListView cardsList ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test,container,false);

//        System.out.println("Fragment3");
//        Toast.makeText(getContext(),"Fragment3!",Toast.LENGTH_SHORT).show();
        cardsList = (ListView)rootView.findViewById(R.id.cards_list);
        return rootView;
    }
}


