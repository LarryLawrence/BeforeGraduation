package com.drunkpiano.zhihuselection.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.drunkpiano.zhihuselection.HeyApplication;

/**
 * Created by DrunkPiano on 16/3/17.
 */
public class Utilities {
    Context context ;
    String sheet = "";
    public HeyApplication application ;
    int numCount = 30;

//    /**
//     * 检查当前网络是否可用
//     *
//     * @param context
//     * @return
//     */

//    public static boolean isNetworkAvailable(Activity activity)
//    {
//        Context context = activity.getApplicationContext();
//        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager == null)
//        {
//            return false;
//        }
//        else
//        {
//            // 获取NetworkInfo对象
//            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
//
//            if (networkInfo != null && networkInfo.length > 0)
//            {
//                for (int i = 0; i < networkInfo.length; i++)
//                {
//                    System.out.println(i + "===状态===" + networkInfo[i].getState());
//                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
//                    // 判断当前网络状态是否为连接状态
//                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
//                    {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    System.out.println("当前网络是连接的当前网络是连接的当前网络是连接的当前网络是连接的");
                    return true;
                }
                System.out.println("网络不可用网络不可用网络不可用网络不可用网络不可用网络不可用");

            }
        }
        return false;
    }


}
