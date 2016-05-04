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

import java.util.ArrayList;

/**
 * Created by DrunkPiano on 16/5/3.
 */
public class AnimAdapter extends RecyclerView.Adapter<AnimAdapter.NormalTextViewHolder> {

    ListCellDataSimplified[] data;
    Db db;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;
    String tableName;
    int count;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    ArrayList<ListCellDataSimplified> dataArrayList = new ArrayList<>();

//    private ArrayList<String> mTitles = new ArrayList<>();

    public AnimAdapter(Context context, String tableName) {
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
        dataArrayList.remove(position);
        notifyItemRemoved(position);
        db = new Db(mContext);
        dbWrite = db.getInstance(mContext).getWritableDatabase();
//        String whereClause = "_id=?";
        String whereClause = "saddress=?";
        String [] whereArgs = {data[count-1-position].getAddress()};
        dbWrite.delete("favorites", whereClause, whereArgs);//没有cv
        dbWrite.close();
        count -- ;

    }


    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("this is favorite onCreateViewHolder");

        QueryData();
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.list_single_answer_item_card_view, parent, false), this);
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        System.out.println("this is favorite onBindViewHolder");

        if(data.length>0) {
            holder.title.setText(data[count - 1 - position].getTitle());
            holder.info.setText(data[count - 1 - position].getSummary());
        }
    }

    @Override
    public int getItemCount() {
//        return dataArrayList == null ? 0 : dataArrayList.size();
        return count ;
    }

    public  class NormalTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView title;
        TextView info;
        AnimAdapter mAdapter;
        View rootView;

        NormalTextViewHolder(View view, AnimAdapter adapter) {
            super(view);
            mAdapter = adapter;
            title = (TextView) view.findViewById(R.id.title);
            info = (TextView) view.findViewById(R.id.info);
            rootView = view.findViewById(R.id.cv_item);

            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mAdapter.mContext, WebViewActivity.class);
            intent.putExtra("address", mAdapter.data[count - 1 - getPosition()].getAddress());
            intent.putExtra("title", mAdapter.data[count - 1 - getPosition()].getTitle());
            intent.putExtra("summary", mAdapter.data[count - 1 - getPosition()].getSummary());
            mAdapter.mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            remove(getPosition());

            return true;
        }
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
        for (int i = 0; i < count ; i++) {
            data[i] = dataArrayList.get(i);
            System.out.println("hello---------->" + data[i].getTitle());
        }
        return (count != 0 );

    }
}