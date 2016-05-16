package com.drunkpiano.zhihuselection.utilities;

/**
 * Created by DrunkPiano on 16/5/10.
 */
public interface MainItemClickListener {
    //这个position起初是用来scrollToPosition的,后来发现不精确
    void onMainItemClick(ListCellData answer);
    void onEndImageClick();
}
