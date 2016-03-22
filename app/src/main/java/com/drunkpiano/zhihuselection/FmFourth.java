package com.drunkpiano.zhihuselection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by DrunkPiano on 16/3/11.
 */
public class FmFourth extends Fragment {
    ListView cardsList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(),"Fragment4",Toast.LENGTH_SHORT).show();
        System.out.println("Fragment4!");
        View rootView = inflater.inflate(R.layout.fragment_test,container,false);//inflate一个listview
        return rootView;
    }


    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
