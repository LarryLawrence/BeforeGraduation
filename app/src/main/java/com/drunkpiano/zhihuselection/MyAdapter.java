package com.drunkpiano.zhihuselection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DrunkPiano on 16/4/24.
 */
public class MyAdapter extends RecyclerView.Adapter{
    ListCellData [] data  ;
    int id = 0 ;
    int count = 0 ;
    String sheetName = "";
    ArrayList<ListCellData> dataArrayList = new ArrayList<>();
    private Context context;

    public MyAdapter(Context context,  String sheetName ,int count) {
        this.context = context;
        this.sheetName = sheetName ;
        this.count = count ;
    }

    public static class DataSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView title;
        private TextView info;
        public View rootView;

        public DataSetViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            info = (TextView) itemView.findViewById(R.id.info);
            rootView = itemView.findViewById(R.id.cv_item);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.DataSetViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        QueryData();
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_answer_item_card_view, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new DataSetViewHolder(v);
        // set the view's size, margins, paddings and layout parameters
//        ...
//        ViewHolder vh = new ViewHolder(v);
//        ViewHolder vh = new ViewHolder(new TextView(parent.getContext()));
//        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.title.setText(mDataset[position]);

        DataSetViewHolder dataSetViewHolder = (DataSetViewHolder) holder;
//        Person person = list.get(i);
//        dataSetViewHolder.title.setText(person.getName());
//        dataSetViewHolder.info.setText(person.getAge() + "岁");
        if(data.length>1) {
//                System.out.println("BB" + data[0]);
            dataSetViewHolder.title.setText(data[position].getTitle());
            dataSetViewHolder.info.setText(data[position].getSummary());
        }
    }
    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return position ;
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        return mDataset.length;
        return count;
    }
    public boolean QueryData(){
        Db db = new Db(context);
        //READ
        SQLiteDatabase dbRead = db.getReadableDatabase();
        //public Cursor query(String table,String[] columns,String selection,String[]  selectionArgs,String groupBy,String having,String orderBy,String limit);
        Cursor myCursor = dbRead.query(sheetName, null, null, null, null, null, null);
//        if(myCursor.moveToFirst()) {
        //遍历游标
//            for(count=0;count<myCursor.getCount();count++){
//            for(count=0;count<myCursor.getCount();count++){
        while(myCursor.moveToNext()){
            ListCellData dataCell = new ListCellData(myCursor.getString(1),myCursor.getString(2),myCursor.getString(3),myCursor.getString(4),myCursor.getString(5),myCursor.getString(6),myCursor.getString(7),myCursor.getString(8),myCursor.getString(9));
//                dataArrayList.add(i,dataCell);
            dataArrayList.add(dataCell);
//                    count++;
//                System.out.println(id+":"+authorname+":"+vote);
        }
        myCursor.close();
        dbRead.close();

        data = new ListCellData[count];
        for(int i = 0 ; i <count ; i ++)
        {
            data[i] = dataArrayList.get(i);
        }
        if(count!=0) return true ;
        else return false ;
    }
}