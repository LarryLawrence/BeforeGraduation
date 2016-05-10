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

/**
 * Created by DrunkPiano on 16/5/3.
 */
public class FavoritesAdapter extends RecyclerView.Adapter {

    private static final int NORMAL_ITEM = 0;
    private static final int ITEM_WITH_DATE = 1;
    ListCellDataSimplified[] data;
    Db db;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;
    String tableName;
    int count;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    ArrayList<ListCellDataSimplified> dataArrayList = new ArrayList<>();
    MyItemClickListener mLongClickListener;

    public FavoritesAdapter(Context context, String tableName) {
        mContext = context;
        this.tableName = tableName;
        mLayoutInflater = LayoutInflater.from(context);
        System.out.println("this is favorite constructor");

        db = new Db(mContext);
        dbRead = db.getInstance(mContext).getReadableDatabase();
        Cursor myCursor = dbRead.query(tableName, null, null, null, null, null, null);
        count = myCursor.getCount();
        myCursor.close();
        dbRead.close();
    }

    public void remove(int position) {
//        System.out.println("remove postion------>"+ position + "---"+ dataArrayList.get(position).getTitle());
        dataArrayList.remove(count - 1 - position);
        notifyItemRemoved(position);
        db = new Db(mContext);
        dbWrite = db.getInstance(mContext).getWritableDatabase();
        String whereClause = "saddress=?";
        String[] whereArgs = {data[count - 1 - position].getAddress()};
        dbWrite.delete("favorites", whereClause, whereArgs);//没有cv
        dbWrite.close();
        count--;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
//        if (viewType == NORMAL_ITEM)
        //原本想要用在MainAdapter上面的,写在了这个Adapter上面..所以取消If
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.list_single_answer_item_card_view, parent, false));
//        else
//            return new NormalTextViewHolderWithDate(mLayoutInflater.inflate(R.layout.list_single_answer_item_card_view_with_date, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.length <= 0)
            return;
        if (holder instanceof NormalTextViewHolder) {
            NormalTextViewHolder vh1 = (NormalTextViewHolder) holder;
            vh1.title.setText(data[count - 1 - position].getTitle());
            vh1.info.setText(data[count - 1 - position].getSummary());
        } else {
            NormalTextViewHolderWithDate vh2 = (NormalTextViewHolderWithDate) holder;
            vh2.date.setText("2015年11月11日");
            vh2.title.setText(data[count - 1 - position].getTitle());
            vh2.info.setText(data[count - 1 - position].getSummary());
        }
    }

    @Override
    public int getItemCount() {
//        return dataArrayList == null ? 0 : dataArrayList.size();
        return count;
    }

    public class NormalTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView title;
        TextView info;
        View rootView;

        public NormalTextViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            info = (TextView) view.findViewById(R.id.info);
            rootView = view.findViewById(R.id.cv_item);

            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("address", data[count - 1 - getAdapterPosition()].getAddress());
            intent.putExtra("title", data[count - 1 - getAdapterPosition()].getTitle());
            intent.putExtra("summary", data[count - 1 - getAdapterPosition()].getSummary());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
//            remove(getAdapterPosition());
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
        if (position == 0)
            return ITEM_WITH_DATE;
        else
            return NORMAL_ITEM;
    }

    public boolean QueryData() {
        System.out.println("this is favorite queryData");

        db = new Db(mContext);
        SQLiteDatabase dbRead = db.getInstance(mContext).getReadableDatabase();
        Cursor myCursor = dbRead.query(tableName, null, null, null, null, null, null);
        count = myCursor.getCount();
        //myCursor默认是在first位置的
        while (myCursor.moveToNext()) {
            ListCellDataSimplified dataCell = new ListCellDataSimplified(myCursor.getString(1), myCursor.getString(2), myCursor.getString(3));
            dataArrayList.add(dataCell);
        }
        myCursor.close();
        dbRead.close();
        data = new ListCellDataSimplified[count];
        for (int i = 0; i < count; i++) {
            data[i] = dataArrayList.get(i);
        }
        return (count != 0);
    }
}