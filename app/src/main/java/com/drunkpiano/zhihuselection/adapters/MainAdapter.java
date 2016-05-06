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
    String dateWithChinese;
    ArrayList<ListCellData> dataArrayList = new ArrayList<>();
    public Context context;

    public MainAdapter(Context context, String tableName, int count ,String dateWithChinese) {
        this.context = context;
        this.tableName = tableName;
        this.count = count;
        this.dateWithChinese = dateWithChinese;
        System.out.println("dateWithChinese----constructor--->"+this.dateWithChinese);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            System.out.println("ITEM_WITH_DATE");
            return ITEM_WITH_DATE;
        }
            else{
            System.out.println("NORMAL_ITEM");
            return NORMAL_ITEM;}
    }


    public class DataSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        TextView info;
        public View rootView;
//        MainAdapter mainAdapter;

        public DataSetViewHolder(View itemView) {
            super(itemView);
//            mainAdapter = adapter;
            title = (TextView) itemView.findViewById(R.id.title);
            info = (TextView) itemView.findViewById(R.id.info);
            rootView = itemView.findViewById(R.id.cv_item);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //http://www.zhihu.com/question/questionid/answer/answerid
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("address", "http://www.zhihu.com/question/" + data[getPosition()].getQuestionid() + "/answer/" + data[getPosition()].getAnswerid());
            intent.putExtra("title", data[getPosition()].getTitle());
            intent.putExtra("summary", data[getPosition()].getSummary());
//            System.out.println(getPosition() + "-----------------=------->" + Integer.parseInt(String.valueOf(getItemId())));
            System.out.println("http://www.zhihu.com/question/" + data[getPosition()].getQuestionid() + "/answer/" + data[getPosition()].getAnswerid());

            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public class DataSetViewHolderWithDate extends DataSetViewHolder {
        TextView date;

        public DataSetViewHolderWithDate(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_show);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QueryData();
        View v ;
        if(viewType == NORMAL_ITEM)
        {//getItemViewType----->传viewType给onCreateVIewHolder,holder再inflate.然后bindViewholder
        // create a new view

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_answer_item_card_view, parent, false);
            System.out.println("inflate---->ITEM_WITH_DATE");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolder(v);

        }
        else  {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_single_answer_item_card_view_with_date, parent, false);
            System.out.println("inflate---->NORMAL_ITEM");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(lp);
            return new DataSetViewHolderWithDate(v);

        }

        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.length < 0) {
            return;
        }
        if (holder instanceof DataSetViewHolderWithDate) {
            System.out.println("dateWithChinese----0--->" + dateWithChinese);
//            DataSetViewHolder dataSetViewHolder = (DataSetViewHolder) holder;
            ((DataSetViewHolderWithDate)holder).title.setText(data[position].getTitle());
            ((DataSetViewHolderWithDate)holder).info.setText(data[position].getSummary());
            ((DataSetViewHolderWithDate)holder).date.setText(dateWithChinese);

        } else if(holder instanceof DataSetViewHolder){
            System.out.println("dateWithChinese----1--->"+dateWithChinese);
//            DataSetViewHolderWithDate dataSetViewHolderWithDate = (DataSetViewHolderWithDate) holder;
//            System.out.println("dateWithChinese----2--->"+dateWithChinese);
//            dataSetViewHolderWithDate.date.setText(dateWithChinese);
            ((DataSetViewHolder)holder).title.setText(data[position].getTitle());
            ((DataSetViewHolder)holder).info.setText(data[position].getSummary());

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
        }

        return (count != 0);
    }
}