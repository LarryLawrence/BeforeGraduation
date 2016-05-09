package com.drunkpiano.zhihuselection.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by DrunkPiano on 16/5/9.
 */
public class MailUtils {



    public static void sendMail(String path,Context c)
    {
        File file = new File(path); //附件文件地址

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", file.getName()); //
        intent.putExtra("body", "Email from CodePad"); //正文
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); //添加附件，附件为file对象
        if (file.getName().endsWith(".gz")) {
            intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
        } else if (file.getName().endsWith(".txt")) {
            intent.setType("text/plain"); //纯文本则用text/plain的mime
        } else {
            intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
        }
        c.startActivity(intent); //调用系统的mail客户端进行发送}
    }

    public static void mailContact(Context c,String mailAdress)
    {
        Intent it = new Intent(Intent.ACTION_SEND);

        String[] receiver;
        receiver=new String[]{mailAdress};
        it.putExtra(Intent.EXTRA_EMAIL, receiver);
        it.putExtra("subject", "About CodePad");
        it.putExtra(Intent.EXTRA_TEXT, "/*Thanks advance for any tips.*/");

        it.setType("text/plain");
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        c.startActivity(Intent.createChooser(it, "Choose Email Client")); //调用系统的mail客户端进行发送
    }

}