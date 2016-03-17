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

import com.drunkpiano.zhihuselection.utilities.Utilities;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FmSecond extends Fragment{

    ListView cardsList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) root.findViewById(R.id.cards_list);
//        root.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utilities ut = new Utilities(getContext() , "recent");
//                if (ut.shouldUpdateDB()) {
//                    ut.DownloadJSONAndUpdateDB();
//                }
//            }
//        });
        Utilities ut = new Utilities(getContext(), "recent");
                if (ut.shouldUpdateDB()) {
                    ut.DownloadJSONAndUpdateDB();
                }

        setupList();
        return root;
    }
    private void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }
        private CardsAdapter createAdapter(){
        return new CardsAdapter(getActivity(),"recent");
    }


    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
