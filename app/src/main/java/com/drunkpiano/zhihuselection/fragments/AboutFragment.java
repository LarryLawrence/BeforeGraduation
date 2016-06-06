/*
 * This adapter is used for displaying the about fragment.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fix it to accord with standard coding disciplines;
 */

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

public class AboutFragment extends Fragment {
    TextView mTextViewZhihu;
    TextView mTextViewGitHub;
    TextView mTextViewSuLiAn;
    ImageView mImageViewIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        mImageViewIcon = (ImageView) rootView.findViewById(R.id.about_image_icon);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.logo_anim_rotate);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mImageViewIcon.startAnimation(anim);
            }
        }, 1000);   //1秒
        mImageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageViewIcon.startAnimation(anim);
            }
        });

        mTextViewZhihu = (TextView) rootView.findViewById(R.id.tv_zhihuan);
        mTextViewZhihu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.zhihu.com/people/larry-lawrence");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        mTextViewGitHub = (TextView) rootView.findViewById(R.id.tv_github);
        mTextViewGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/LarryLawrence");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        mTextViewSuLiAn = (TextView) rootView.findViewById(R.id.tv_sulian);
        SpannableStringBuilder builder =
                new SpannableStringBuilder(mTextViewSuLiAn.getText().toString());
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(blueSpan, 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextViewSuLiAn.setText(builder);
        mTextViewSuLiAn.setOnClickListener(new View.OnClickListener() {
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


