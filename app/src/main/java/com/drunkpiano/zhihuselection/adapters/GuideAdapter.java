package com.drunkpiano.zhihuselection.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drunkpiano.zhihuselection.R;

/**
 * Created by DrunkPiano on 16/5/12.
 */
public class GuideAdapter extends RecyclerView.Adapter {
    Context context;
    private final LayoutInflater mLayoutInflater;


    public GuideAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        addData();
        return new GuideViewHolder(mLayoutInflater.inflate(R.layout.list_single_answer_item_card_view, parent, false));
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_single_answer_item_card_view, parent, false);
//        return new GuideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GuideViewHolder gv = (GuideViewHolder) holder;
        String title = "";
        String info = "";
        switch (position) {
            case 0:
                title = "这是什么地方？";
                info = "这是一个知乎的作者兼读者——也就是我建设的个人网站，目的就是为其他读者推荐更多更好更新的答案。";
                break;
            case 1:
                title = "每天的三篇推荐都是什么内容？";
                info = "凌晨5点发布昨日最新，也就是前一天发表的热门答案；\n" +
                        "\n" +
                        "中午11点发布近日热门，也就是之前7天内发表、但没在任何一篇「昨日最新」中推荐过的答案；\n" +
                        "\n" +
                        "傍晚17点发布历史精华，也就是随机挑选了整个知乎历史上的精华内容。\n" +
                        "\n";
                break;
            case 2:
                title = "推荐的依据是什么？";
                info = "主要依据是赞同数，排位顺序也综合考虑了作者本人的一些情况，以优先推荐新人为主。";
                break;
        }
        gv.title.setText(title);
        gv.info.setText(info);
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class GuideViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView info;
        View rootView;

        public GuideViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            info = (TextView) view.findViewById(R.id.info);
            rootView = view.findViewById(R.id.card_no_ele);
        }
    }
}
