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
        return new GuideViewHolder(mLayoutInflater.inflate(R.layout.list_single_answer_item_no_elevation_guide, parent, false));
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
                title = "Q:这是一个什么样的APP？";
                info = "A:这是一个精选知乎答案的APP,每天挑选一些热门答案推荐给你。";
                break;
            case 1:
                title = "Q:每天的三篇推荐都是什么内容？";
                info = "A:凌晨5点发布「昨天」，也就是前一天发表的热门答案；\n" +
                        "中午11点发布「上周」，也就是之前7天内发表、但没在任何一篇「昨日最新」中推荐过的答案；\n" +
                        "傍晚17点发布「往年」，也就是随机挑选了整个知乎历史上的精华内容。\n";
                break;
            case 2:
                title = "Q:推荐的依据是什么？";
                info = "A:主要依据是赞同数，排位顺序也综合考虑了作者本人的一些情况，以优先推荐新人为主。";
                break;
            case 3:
                title = "Q:页面中的链接为什么打不开？";
                info = "A:默认禁用了JavaScript,这样的话页面上很多元素会失效,但阅读起来更清爽。可以在设置中开启。";
                break;
            case 4:
                title = "Q:如何在知乎APP中打开答案？";
                info = "A:可以在应用内阅读的时候在右上角菜单选择用知乎打开;也可以在设置中选择默认打开方式。";
                break;
            case 5:
                title = "Q:在应用内阅读答案的时候,有时候点开会提示登录,没有内容？";
                info = "A:这种情况一般是问题或答案比较敏感,被修改或删除了。可以在右上角选择用知乎查看。";
                break;
            case 6:
                title = "Q:获取数据的速度好像有点慢,是不是加载了很多东西？";
                info = "A:连接kanzhihu.com的速度确实不够快,加载答案列表的数据量很小,每次大约30KB。连接建立之后,再次加载的速度会变快。";
                break;
            case 7:
                title = "Q:这个软件的作者是谁?";
                info = "A:在关于页面可以找到,欢迎发邮件给我。";
                break;
        }
        gv.title.setText(title);
        gv.info.setText(info);
    }

    @Override
    public int getItemCount() {
        return 8;
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
