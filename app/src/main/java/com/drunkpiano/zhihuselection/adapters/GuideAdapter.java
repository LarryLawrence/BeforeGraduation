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
            return new GuideViewHolder(mLayoutInflater.inflate(R.layout.list_single_answer_item_no_elevation_guide, parent, false));
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GuideViewHolder gv = (GuideViewHolder) holder;
        String title = "";
        String info = "";
        switch (position) {
            case 0:
                title = "Q: 这个APP是干什么用的？";
                info = "A: 这是一个精选知乎答案的APP，每天挑选一些热门答案推荐给你。它只是挑选了一些知乎的答案列表，把大家引导到知乎的网页端或客户端去。\n   数据的来源是@苏莉安的「看知乎」。";
                break;
            case 1:
                title = "Q: 每天的三篇推荐都是什么内容？";
                info = "A: 凌晨5点发布「昨天」，是前一天发表的热门答案；\n" +
                        "   中午11点发布「上周」，是之前7天内发表、但没在任何一篇「昨天」中推荐过的答案；\n" +
                        "   傍晚17点发布「往年」，随机挑选了整个知乎历史上的精华内容。";
                break;
            case 2:
                title = "Q: 推荐的依据是什么？";
                info = "A: 主要依据是赞同数，算法得出的排位顺序也综合考虑了作者本人的一些情况，以优先推荐新人为主。";
                break;
            case 3:
                title = "Q: 主页右上角的「时光机」按钮是随机到某一天的吗?";
                info = "A: 是的,每次点击会随机来到2014年9月19日到今天之间的某一天。";
                break;
            case 4:
                title = "Q: 有时候点进去之后会提示登录？";
                info = "A: Android WebView的loadUrl()函数有时候会把原本正确的地址转换成urlEncode编码，这似乎是Android源码的问题，目前我还没有找到解决办法。出现这种情况时，烦请移驾右上角「使用知乎客户端打开」。";
                break;
            case 5:
                title = "Q: 如何在知乎APP中打开答案？";
                info = "A: 可以在应用内阅读的时候在右上角菜单选择用知乎打开; 也可以在设置中选择默认打开方式。";
                break;
            case 6:
                title = "Q: 怎么回到历史上具体的某一天?";
                info = "A: 点击右上角菜单中的「回到过去」。";
                break;
            case 7:
                title = "Q: 获取数据的速度好像有点慢，是不是加载了很多东西？";
                info = "A: 连接kanzhihu.com的速度确实不够快，但加载答案列表的数据量很小，是纯文字，每次大约30KB。4G网络下首次建立连接大约需要8秒，连接建立之后，再次加载的速度只需要不到1秒。";
                break;
            case 8:
                title = "Q: 答案页底部的奇怪符号是什么?";
                info = "A: 是乐谱中的「四分休止符」，拿来表示结束用的，哈哈。点击它可以回到页面顶端。";
                break;
            case 9:
                title = "Q: 开发者是谁?";
                info = "A: 在「关于」页面可以找到，欢迎发邮件给我。\n\nv1.0版本发布当天收到了很多酷安网的朋友们的的鼓励，也有几位朋友给我提了宝贵意见 我都有采纳，在此谢谢各位啦。";
                break;
        }
        gv.title.setText(title);
        gv.info.setText(info);
    }

    @Override
    public int getItemCount() {
        return 10;
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
