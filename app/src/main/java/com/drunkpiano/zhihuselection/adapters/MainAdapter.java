package com.drunkpiano.zhihuselection.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.activities.WebViewActivity;
import com.drunkpiano.zhihuselection.utilities.Db;
import com.drunkpiano.zhihuselection.utilities.ListCellData;

import java.util.ArrayList;

/**
 * Created by DrunkPiano on 16/4/24.
 */
public class MainAdapter extends RecyclerView.Adapter {
    private static final int NORMAL_ITEM = 0;
    private static final int ITEM_WITH_DATE = 1;
    ListCellData[] data;
    int count = 0;
    String tableName = "";
    ArrayList<ListCellData> dataArrayList = new ArrayList<>();
    public Context context;

    public MainAdapter(Context context, String tableName, int count) {
        this.context = context;
        this.tableName = tableName;
        this.count = count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ITEM_WITH_DATE;
        else
            return NORMAL_ITEM;
    }


    public static class DataSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        TextView info;
        public View rootView;
        MainAdapter mainAdapter;

        public DataSetViewHolder(View itemView, MainAdapter adapter) {
            super(itemView);
            mainAdapter = adapter;
            title = (TextView) itemView.findViewById(R.id.title);
            info = (TextView) itemView.findViewById(R.id.info);
            rootView = itemView.findViewById(R.id.cv_item);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //http://www.zhihu.com/question/questionid/answer/answerid
            Intent intent = new Intent(mainAdapter.context, WebViewActivity.class);
            intent.putExtra("address", "http://www.zhihu.com/question/" + mainAdapter.data[getPosition()].getQuestionid() + "/answer/" + mainAdapter.data[getPosition()].getAnswerid());
            intent.putExtra("title", mainAdapter.data[getPosition()].getTitle());
            intent.putExtra("summary", mainAdapter.data[getPosition()].getSummary());
//            System.out.println(getPosition() + "-----------------=------->" + Integer.parseInt(String.valueOf(getItemId())));
            System.out.println("http://www.zhihu.com/question/" + mainAdapter.data[getPosition()].getQuestionid() + "/answer/" + mainAdapter.data[getPosition()].getAnswerid());

            mainAdapter.context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public class DataSetViewHolderWithDate extends DataSetViewHolder {
        TextView date;

        public DataSetViewHolderWithDate(View itemView, MainAdapter adapter) {
            super(itemView, adapter);
            date = (TextView) itemView.findViewById(R.id.date_show);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
        View v ;
        if(viewType == NORMAL_ITEM)
        {
        // create a new view
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_answer_item_card_view, parent, false);

        }
        else  {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_date, parent, false);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new DataSetViewHolder(v, this);
        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.length < 0) {
            return;
        }
        if (holder instanceof DataSetViewHolder) {
            DataSetViewHolder dataSetViewHolder = (DataSetViewHolder) holder;
            dataSetViewHolder.title.setText(data[position].getTitle());
            dataSetViewHolder.info.setText(data[position].getSummary());
        } else {
            DataSetViewHolderWithDate dataSetViewHolderWithDate = (DataSetViewHolderWithDate) holder;
            dataSetViewHolderWithDate.date.setText("2016年1月1日");
            dataSetViewHolderWithDate.title.setText(data[position].getTitle());
            dataSetViewHolderWithDate.info.setText(data[position].getSummary());

        }
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return position;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        return mDataset.length;
        return count;
    }


    public boolean QueryData() {
        Db db = new Db(context);
        //READ
        SQLiteDatabase dbRead = db.getReadableDatabase();
        //public Cursor query(String table,String[] columns,String selection,String[]  selectionArgs,String groupBy,String having,String orderBy,String limit);
        Cursor myCursor = dbRead.query(tableName, null, null, null, null, null, null);
//        if(myCursor.moveToFirst()) {
        //遍历游标
//            for(count=0;count<myCursor.getCount();count++){
//            for(count=0;count<myCursor.getCount();count++){
        while (myCursor.moveToNext()) {
            ListCellData dataCell = new ListCellData(myCursor.getString(1), myCursor.getString(2), myCursor.getString(3), myCursor.getString(4), myCursor.getString(5), myCursor.getString(6), myCursor.getString(7), myCursor.getString(8), myCursor.getString(9));
//                dataArrayList.add(i,dataCell);
            dataArrayList.add(dataCell);
//                    count++;
//                System.out.println(id+":"+authorname+":"+vote);
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
            Toast.makeText(context, "访问网络失败了", Toast.LENGTH_SHORT).show();
        }

        return (count != 0);
    }
}