package com.drunkpiano.zhihuselection.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.CardsAdapter;
import com.drunkpiano.zhihuselection.HeyApplication;
import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FMArchive extends Fragment {
    HeyApplication application ;
    int count = 3 ;
    ListView cardsList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        application = (HeyApplication)getActivity().getApplication();
        count = application.getArchiveCount() ;
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) root.findViewById(R.id.cards_list);
        setupList();
        return root;
    }
    public void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }
        private CardsAdapter createAdapter(){
            return new CardsAdapter(getActivity(),"archive",count);
    }

    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }

}
