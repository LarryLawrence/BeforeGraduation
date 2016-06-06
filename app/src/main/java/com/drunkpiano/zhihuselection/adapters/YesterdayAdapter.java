/*
 * This adapter is used for displaying the yesterday viewpager.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fix it to accord with standard coding disciplines;
 */

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

public class YesterdayAdapter extends RecyclerView.Adapter {
    private static final int NORMAL_ITEM = 0;
    private static final int ITEM_WITH_DATE = 1;
    private static final int ITEM_WITH_END = 2;
    private static ListCellData[] mData;
    private int mCount = 0;
    private String mTableName = "";
    private String mDateWithChinese;
    private ArrayList<ListCellData> mDataArrayList = new ArrayList<>();
    private Context mContext;
    private MainItemClickListener mMainItemClickListener;

    public YesterdayAdapter(Context mContext, String mTableName, int mCount,
                            String mDateWithChinese, MainItemClickListener callBack) {
        this.mContext = mContext;
        this.mTableName = mTableName;
        this.mCount = mCount;
        this.mDateWithChinese = mDateWithChinese;
        this.mMainItemClickListener = callBack;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_WITH_DATE;
        } else if (position == mCount - 1) {
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
            mMainItemClickListener.onMainItemClick(mData[(Integer) v.getTag()]);
        }
    };
    public View.OnClickListener endingImgeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMainItemClickListener.onEndImageClick();
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
            endingImage = (ImageView) itemView.findViewById(R.id.endingImgAdapter);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
        View v;
        if (viewType == NORMAL_ITEM) {

            //getItemViewType----->传viewType给onCreateVIewHolder,holder再inflate.然后bindViewholder
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view, parent, false);
            LinearLayout.LayoutParams lp
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolder(v);

        } else if (viewType == ITEM_WITH_DATE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_date, parent, false);
            LinearLayout.LayoutParams lp
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolderWithDate(v);

        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_ending, parent, false);
            LinearLayout.LayoutParams lp
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolderWithEnd(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData.length < 0) {
            return;
        }

        if (holder instanceof DataSetViewHolderWithDate) {
            ((DataSetViewHolderWithDate) holder).title.setText(mData[position].getTitle());
            ((DataSetViewHolderWithDate) holder).info.setText(mData[position].getSummary());
            ((DataSetViewHolderWithDate) holder).date.setText(mDateWithChinese);
            ((DataSetViewHolderWithDate) holder).rootView.setTag(position);
            ((DataSetViewHolderWithDate) holder).rootView.setOnClickListener(linkListener);

        } else if (holder instanceof DataSetViewHolderWithEnd) {
            ((DataSetViewHolderWithEnd) holder).title.setText(mData[position].getTitle());
            ((DataSetViewHolderWithEnd) holder).info.setText(mData[position].getSummary());
            ((DataSetViewHolderWithEnd) holder).rootView.setTag(position);
            ((DataSetViewHolderWithEnd) holder).rootView.setOnClickListener(linkListener);
            ((DataSetViewHolderWithEnd) holder).endingImage.setOnClickListener(endingImgeListener);

        } else if (holder instanceof DataSetViewHolder) {
            ((DataSetViewHolder) holder).title.setText(mData[position].getTitle());
            ((DataSetViewHolder) holder).info.setText(mData[position].getSummary());
            ((DataSetViewHolder) holder).rootView.setTag(position);
            ((DataSetViewHolder) holder).rootView.setOnClickListener(linkListener);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public boolean QueryData() {
        Db db = new Db(mContext);
        //READ
        SQLiteDatabase dbRead = db.getReadableDatabase();
        Cursor myCursor = dbRead.query(mTableName, null, null, null, null, null, null);

        while (myCursor.moveToNext()) {
            ListCellData dataCell = new ListCellData(myCursor.getString(1), myCursor.getString(2),
                    myCursor.getString(3), myCursor.getString(4));
            mDataArrayList.add(dataCell);
        }
        myCursor.close();
        dbRead.close();

        mData = new ListCellData[mCount];
        if (mCount > 0) {
            for (int i = 0; i < mCount; i++) {
                mData[i] = mDataArrayList.get(i);
            }
        }
        return (mCount != 0);
    }
}