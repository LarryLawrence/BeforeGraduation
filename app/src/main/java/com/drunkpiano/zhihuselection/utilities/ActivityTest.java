package com.drunkpiano.zhihuselection.utilities;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/4/27.
 */
public class ActivityTest extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.fragment_test);
        System.out.println("caocaocaocaocaocaocaocaocao");
        Toast.makeText(ActivityTest.this, "bb" , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        System.out.println("caocaocaocaocaocaocaocaocao");
        Toast.makeText(ActivityTest.this, "bb" , Toast.LENGTH_SHORT).show();
    }
}
