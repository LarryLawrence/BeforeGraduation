package com.drunkpiano.zhihuselection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DrunkPiano on 16/3/9.
 */
public class FmFirst extends Fragment{
    TextView tv ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_first,container,false);

        tv = (TextView)root.findViewById(R.id.tv1);
        tv.setText("bb");
        root.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AsyncTask<String,Void,Void>(){
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            URL url = new URL(params[0]);
                            URLConnection connection = url.openConnection();
                            InputStream is = connection.getInputStream();
                            //需要把它包装成更加简洁的读取数据的方式
                            //IS可指定字符集,所以这是一个字节到字符的转换
                            InputStreamReader isr = new InputStreamReader(is,"utf-8");
                            //BR可以读取一行字符串
                            BufferedReader br = new BufferedReader(isr);
                            String line ;
                            StringBuilder builder = new StringBuilder();
                            while((line=br.readLine())!=null)
                            {
                                System.out.println(line);
                                builder.append(line);
                            }
                            //读取完成,依次向上关闭连接
                            br.close();
                            isr.close();
                            is.close();
                            JSONObject root = new JSONObject(builder.toString());
//                            System.out.println("result="+root.getString("result"));
                            JSONArray array = root.getJSONArray("posts");
                            for(int i = 0 ; i <array.length() ; i ++)
                            {
                                JSONObject jo = array.getJSONObject(i);
//                                System.out.println("------------------");
//                                System.out.println("id="+jo.getString("id"));
//                                System.out.println("name="+jo.getString("name"));
//                                if(jo.getString("id")=="1911")
                                if(i==2)
                                {

                                    System.out.println("found it!-->"+jo.getString("id")+jo.getString("excerpt"));
                                }
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute("http://api.kanzhihu.com/getposts");
            }
        });
//        return super.onCreateView(inflater, container, savedInstanceState);
        return root ;
    }
}
