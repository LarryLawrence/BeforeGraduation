package com.drunkpiano.zhihuselection.fragments;

import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FMYesterday extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    HeyApplication application ;
    int count = 3 ;
    ListView cardsList ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        application = (HeyApplication)getActivity().getApplication();
        count = application.getYesterdayCount() ;
        View root = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) root.findViewById(R.id.cards_list);
        setupList();

//        int asd = 201603281708 ;
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        //DB的最近更新时间
        String lastUpdate = settings.getString("LastUpdate", "198801010500");//defValue - Value to return if this preference does not exist.
        Long lastUpdateInt = Long.parseLong(lastUpdate);
        //现在的时间
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTimeStr = currentTime.format(Calendar.getInstance().getTime()).trim();
        Long currentTimeInt = Long.parseLong(currentTimeStr);
        //网站最近更新时间,今天早上五点
        SimpleDateFormat latestWebsiteUpdateTime = new SimpleDateFormat("yyyyMMdd");
        Long latestWebsiteUpdateTimeInt = Long.parseLong(latestWebsiteUpdateTime.format(Calendar.getInstance().getTime()).trim() + "0500");

//        if(true)
        if(currentTimeInt>latestWebsiteUpdateTimeInt && lastUpdateInt<latestWebsiteUpdateTimeInt)
        {
//            更新
            BridgeYesterday by = new BridgeYesterday() ;
            by.downloadJSONAndUpdateDB();
            System.out.println("update!!!!!!!!!!!!!!!!!!!!!!!");
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LastUpdate",currentTimeStr);
        editor.commit();


        return root;
    }
    public void setupList(){
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new MyItemOnClickListener());
    }
        private CardsAdapter createAdapter(){
            return new CardsAdapter(getActivity(),"yesterday",count);
    }

    class MyItemOnClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }

}
