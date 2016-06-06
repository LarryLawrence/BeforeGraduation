/*
 * A simplified data structure.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
 */

package com.drunkpiano.zhihuselection.utilities;

public class ListCellDataSimplified {
    private String mTitle;
    private String mSummary;
    private String mAddress;

    public ListCellDataSimplified(String mTitle, String mSummary, String mAddress) {
        this.mTitle = mTitle;
        this.mSummary = mSummary;
        this.mAddress = mAddress;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getAddress() {
        return mAddress;
    }
}
