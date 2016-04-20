package com.drunkpiano.zhihuselection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CardsAdapter extends BaseAdapter {

    ListCellData [] data  ;
    int id = 0 ;
    int count = 0 ;
    String sheetName = "";


    ArrayList<ListCellData> dataArrayList = new ArrayList<>();
    private  Context context;

    public CardsAdapter(Context context,  String sheetName ,int count) {
        this.context = context;
        this.sheetName = sheetName ;
        this.count = count ;
//        System.out.println("---------------->this is the constructor");
    }
    public CardsAdapter(){}//Timer中调用

    @Override
    public int getCount() {
//        System.out.println("---------------->this is the getCount() and the count is: "+count);
        return count ;
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
        System.out.println("---------------->this is the getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_single_answer_item, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
//            holder.action = (TextView) convertView.findViewById(R.id.action);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        ListCellData data = getItem(position);
            QueryData();
            if(data.length>1) {
//                System.out.println("BB" + data[0]);
                holder.title.setText(data[position].getTitle());
                holder.info.setText(data[position].getSummary());
            }
        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
        private TextView action;
        private TextView info;
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
