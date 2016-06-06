/*
 * This adapter is used for displaying the favorite viewpager.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fix it to accord with standard coding disciplines;
 */

package com.drunkpiano.zhihuselection.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.activities.WebViewActivity;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellDataSimplified;
import com.drunkpiano.zhihuselection.utilities.MyItemClickListener;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter {
    private static final int NORMAL_ITEM = 0;
    private static final int ITEM_WITH_DATE = 1;
    private ListCellDataSimplified[] mData;
    private Db mDb;
    private String mTableName;
    private int mCount;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<ListCellDataSimplified> mDataArrayList = new ArrayList<>();
    private MyItemClickListener mLongClickListener;

    public FavoritesAdapter(Context context, String mTableName) {
        mContext = context;
        SQLiteDatabase dbRead = mDb.getInstance(mContext).getReadableDatabase();
        this.mTableName = mTableName;
        mLayoutInflater = LayoutInflater.from(context);
        mDb = new Db(mContext);
        Cursor myCursor = dbRead.query(mTableName, null, null, null, null, null, null);
        mCount = myCursor.getCount();
        myCursor.close();
        dbRead.close();
    }

    public void remove(int position) {
        SQLiteDatabase dbWrite;
        mDataArrayList.remove(mCount - 1 - position);
        notifyItemRemoved(position);
        mDb = new Db(mContext);
        dbWrite = mDb.getInstance(mContext).getWritableDatabase();
        String whereClause = "saddress=?";
        String[] whereArgs = {mData[mCount - 1 - position].getAddress()};
        dbWrite.delete("favorites", whereClause, whereArgs);//没有cv
        dbWrite.close();
        mCount--;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
        return new NormalTextViewHolder(mLayoutInflater.inflate
                (R.layout.list_single_answer_item_no_elevation, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData.length <= 0) {
            return;
        }
        if (holder instanceof NormalTextViewHolder) {
            NormalTextViewHolder vh1 = (NormalTextViewHolder) holder;
            vh1.title.setText(mData[mCount - 1 - position].getTitle());
            vh1.info.setText(mData[mCount - 1 - position].getSummary());
        } else {
            NormalTextViewHolderWithDate vh2;
            try {
                vh2 = (NormalTextViewHolderWithDate) holder;
                vh2.date.setText("2015年11月11日");
                vh2.title.setText(mData[mCount - 1 - position].getTitle());
                vh2.info.setText(mData[mCount - 1 - position].getSummary());
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public class NormalTextViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView title;
        TextView info;
        View rootView;

        public NormalTextViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            info = (TextView) view.findViewById(R.id.info);
            rootView = view.findViewById(R.id.card_no_ele);

            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("address", mData[mCount - 1 - getAdapterPosition()].getAddress());
            intent.putExtra("title", mData[mCount - 1 - getAdapterPosition()].getTitle());
            intent.putExtra("summary", mData[mCount - 1 - getAdapterPosition()].getSummary());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickListener != null) {
                mLongClickListener.onInterfaceItemLongClick(getAdapterPosition());
            }
            return true;
        }
    }

    public void setOnLongClickListener(MyItemClickListener listener) {
        this.mLongClickListener = listener;
    }

    public class NormalTextViewHolderWithDate extends NormalTextViewHolder {
        TextView date;

        public NormalTextViewHolderWithDate(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_show);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_WITH_DATE;
        } else {
            return NORMAL_ITEM;
        }
    }

    public boolean QueryData() {
        mDb = new Db(mContext);
        SQLiteDatabase dbRead = mDb.getInstance(mContext).getReadableDatabase();
        Cursor myCursor = dbRead.query(mTableName, null, null, null, null, null, null);
        mCount = myCursor.getCount();

        //myCursor默认是在first位置的
        while (myCursor.moveToNext()) {
            ListCellDataSimplified dataCell = new ListCellDataSimplified(myCursor.getString(1),
                    myCursor.getString(2), myCursor.getString(3));
            mDataArrayList.add(dataCell);
        }
        myCursor.close();
        dbRead.close();
        mData = new ListCellDataSimplified[mCount];
        for (int i = 0; i < mCount; i++) {
            mData[i] = mDataArrayList.get(i);
        }
        return (mCount != 0);
    }
}