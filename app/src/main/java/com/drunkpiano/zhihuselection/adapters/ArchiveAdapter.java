package com.drunkpiano.zhihuselection.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellData;
import com.drunkpiano.zhihuselection.utilities.MainItemClickListener;

import java.util.ArrayList;

/**
 * Created by DrunkPiano on 16/4/24.
 */
public class ArchiveAdapter extends RecyclerView.Adapter {
    private static final int NORMAL_ITEM = 0;
    private static final int ITEM_WITH_DATE = 1;
    private static final int ITEM_WITH_END = 2;

    static ListCellData[] data;
    int count = 0;
    String tableName = "";
    String dateWithChinese;
    ArrayList<ListCellData> dataArrayList = new ArrayList<>();
    public Context context;
    private  MainItemClickListener mainItemClickListener;

    public ArchiveAdapter(Context context, String tableName, int count, String dateWithChinese, MainItemClickListener callBack) {
        this.context = context;
        this.tableName = tableName;
        this.count = count;
        this.dateWithChinese = dateWithChinese;
        this.mainItemClickListener = callBack;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_WITH_DATE;
        } else if (position == count-1) {
            return ITEM_WITH_END;
        } else {
            return NORMAL_ITEM;
        }
    }


    public class DataSetViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView info;
        public View rootView;
//        public CardView layout;

        public DataSetViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            TextPaint tp = title.getPaint();
            tp.setFakeBoldText(true);
            info = (TextView) itemView.findViewById(R.id.info);
            rootView = itemView.findViewById(R.id.card_single);
        }

    }

    public View.OnClickListener linkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainItemClickListener.onMainItemClick(data[(Integer) v.getTag()]);
        }
    };
    public View.OnClickListener endingImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainItemClickListener.onEndImageClick();
        }
    };

    public class DataSetViewHolderWithDate extends DataSetViewHolder {
        TextView date;

        public DataSetViewHolderWithDate(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_show);
        }
    }

    public class DataSetViewHolderWithEnd extends DataSetViewHolder {
        ImageView endingImage;
        public DataSetViewHolderWithEnd(View itemView) {
            super(itemView);
            endingImage = (ImageView)itemView.findViewById(R.id.endingImgAdapter);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
        View v;
        if (viewType == NORMAL_ITEM) {//getItemViewType----->传viewType给onCreateVIewHolder,holder再inflate.然后bindViewholder
            // create a new view

            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view, parent, false);
//            System.out.println("inflate---->ITEM_WITH_DATE");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolder(v);

        } else if (viewType == ITEM_WITH_DATE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_date, parent, false);
//            System.out.println("inflate---->NORMAL_ITEM");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolderWithDate(v);

        } else  {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_ending, parent, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolderWithEnd(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.length < 0) {
            return;
        }

        if (holder instanceof DataSetViewHolderWithDate) {
            ((DataSetViewHolderWithDate) holder).title.setText(data[position].getTitle());
            ((DataSetViewHolderWithDate) holder).info.setText(data[position].getSummary());
            ((DataSetViewHolderWithDate) holder).date.setText(dateWithChinese);
            ((DataSetViewHolderWithDate) holder).rootView.setTag(position);
            ((DataSetViewHolderWithDate) holder).rootView.setOnClickListener(linkListener);

        }else if (holder instanceof DataSetViewHolderWithEnd) {
            ((DataSetViewHolderWithEnd) holder).title.setText(data[position].getTitle());
            ((DataSetViewHolderWithEnd) holder).info.setText(data[position].getSummary());
            ((DataSetViewHolderWithEnd) holder).rootView.setTag(position);
            ((DataSetViewHolderWithEnd) holder).rootView.setOnClickListener(linkListener);
            ((DataSetViewHolderWithEnd) holder).endingImage.setOnClickListener(endingImageListener);
        }

        else if (holder instanceof DataSetViewHolder) {
            ((DataSetViewHolder) holder).title.setText(data[position].getTitle());
            ((DataSetViewHolder) holder).info.setText(data[position].getSummary());
            ((DataSetViewHolder) holder).rootView.setTag(position);
            ((DataSetViewHolder) holder).rootView.setOnClickListener(linkListener);
        }
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return count;
    }


    public boolean QueryData() {
        Db db = new Db(context);
        //READ
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor myCursor = dbRead.query(tableName, null, null, null, null, null, null);

        while (myCursor.moveToNext()) {
            ListCellData dataCell = new ListCellData(myCursor.getString(1), myCursor.getString(2), myCursor.getString(3), myCursor.getString(4));
            dataArrayList.add(dataCell);
        }
        myCursor.close();
        dbRead.close();

        data = new ListCellData[count];
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                data[i] = dataArrayList.get(i);
            }
        } else {
            System.out.println("访问网络失败了");
        }
        return (count != 0);
    }


}