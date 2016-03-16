package com.drunkpiano.zhihuselection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CardsAdapter extends BaseAdapter {

    private List<String> items;
    private final OnClickListener itemButtonClickListener;
    private final Context context;

    public CardsAdapter(Context context, List<String> items, OnClickListener itemButtonClickListener) {
        this.context = context;
        this.items = items;
        this.itemButtonClickListener = itemButtonClickListener;
    }
    private ListCellData[] data = new ListCellData[]{
            new ListCellData("bb","如何看待BB....?","BB内容内容内容内容内容内容内容内容内容","img1","100"),
            new ListCellData("cc","如何看待CC....?","CC内容内容内容内容内容内容内容内容内容","img2","100"),
            new ListCellData("dd","如何看待DD....?","DD内容内容内容内容内容内容内容内容内容","img2","100"),
    };

    @Override
    public int getCount() {
        return items.size();
    }

//    @Override
//    public String getItem(int position) {
//        return items.get(position);
//    }
    public ListCellData getItem(int position)
    {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_single_answer_item, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.action = (TextView) convertView.findViewById(R.id.action);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ListCellData data = getItem(position);
        holder.title.setText(data.getTitle());
        holder.info.setText(data.getSummary());
//        if (itemButtonClickListener != null) {
//            holder.itemButton1.setOnClickListener(itemButtonClickListener);
//            holder.itemButton2.setOnClickListener(itemButtonClickListener);
//        }
//
        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
        private TextView action;
        private TextView info;
    }

}
