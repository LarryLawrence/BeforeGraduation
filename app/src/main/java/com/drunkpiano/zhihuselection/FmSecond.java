package com.drunkpiano.zhihuselection;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FmSecond extends Fragment {

    ListView cardsList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) root.findViewById(R.id.cards_list);
        CardsAdapter ca = new CardsAdapter(getContext(), "yesterday");
//        if (ca.shouldUpdateDB()) {
//            ca.DownloadJSONAndUpdateDB();
//        }
        setupList();
        return root;

    }
    public void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }
        private CardsAdapter createAdapter(){
//            CardsAdapter cAdapter = new CardsAdapter(getActivity(),"recent");
//            cAdapter.notifyDataSetChanged();
//            return  cAdapter;
            return new CardsAdapter(getActivity(),"yesterday");
    }


    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }

    public void delay(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //显示dialog
            }
        }, 5000);
    }

}
