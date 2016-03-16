package com.drunkpiano.zhihuselection;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.ArrayList;

/**
 * Created by DrunkPiano on 16/3/10.
 */
public class FmThird extends Fragment {
    private ListView cardsList ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_layout,container,false);
        cardsList = (ListView)rootView.findViewById(R.id.cards_list);
        setupList();
        return rootView;
    }

    private void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }

    private CardsAdapter createAdapter(){
        ArrayList<String> items = new ArrayList<String>();
        for(int i = 0 ; i < 100 ; i ++)
        {
            items.add(i,"Text 4 list item" + i );
        }
        return new CardsAdapter(getActivity(), items, new ListItemButtonClickListener());
    }

    private final class ListItemButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList.getLastVisiblePosition(); i++) {
                if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_1)) {
                    // PERFORM AN ACTION WITH THE ITEM AT POSITION i
                    Toast.makeText(getActivity(), "Clicked on Left Action Button of List Item " + i, Toast.LENGTH_SHORT).show();
                } else if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_2)) {
                    // PERFORM ANOTHER ACTION WITH THE ITEM AT POSITION i
                    Toast.makeText(getActivity(), "Clicked on Right Action Button of List Item " + i, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }
}


