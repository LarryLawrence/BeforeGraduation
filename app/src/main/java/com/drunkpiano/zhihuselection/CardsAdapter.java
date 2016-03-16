package com.drunkpiano.zhihuselection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CardsAdapter extends BaseAdapter {

    ListCellData [] data ;
//    private List<String> items;
//    private final OnClickListener itemButtonClickListener;
    private final Context context;

    public CardsAdapter(Context context, ListCellData[] data) {
        this.context = context;
        this.data = data ;
//        this.itemButtonClickListener = itemButtonClickListener;
    }


    @Override
    public int getCount() {
        return data.length;
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
//        ListCellData data = getItem(position);
        holder.title.setText(data[position].getTitle());
        holder.info.setText(data[position].getSummary());
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
