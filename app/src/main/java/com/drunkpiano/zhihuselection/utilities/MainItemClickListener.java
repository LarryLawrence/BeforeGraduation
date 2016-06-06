/*
 * A listener.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
 */

package com.drunkpiano.zhihuselection.utilities;

public interface MainItemClickListener {

    //这个position起初是用来scrollToPosition的,后来发现不精确
    void onMainItemClick(ListCellData answer);
    void onEndImageClick();
}
