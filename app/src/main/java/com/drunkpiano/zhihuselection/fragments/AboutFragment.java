package com.drunkpiano.zhihuselection.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/3/10.
 */
public class AboutFragment extends Fragment {
    TextView tv_zhihu;
    TextView tv_github;
    TextView tv_sulian;
    ImageView iv_icon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        iv_icon = (ImageView) rootView.findViewById(R.id.about_image_icon);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_logo);
//        LinearInterpolator lir = new LinearInterpolator();
//        anim.setInterpolator(lir);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                iv_icon.startAnimation(anim);
            }
        }, 1000);   //1秒
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_icon.startAnimation(anim);
            }
        });

        tv_zhihu = (TextView) rootView.findViewById(R.id.tv_zhihuan);
        tv_zhihu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.zhihu.com/people/larry-lawrence");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });


        tv_github = (TextView) rootView.findViewById(R.id.tv_github);
        tv_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/LarryLawrence");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        tv_sulian = (TextView) rootView.findViewById(R.id.tv_sulian);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_sulian.getText().toString());
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(blueSpan, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_sulian.setText(builder);
        tv_sulian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.zhihu.com/people/aton");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        return rootView;
    }

}


