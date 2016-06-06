/*
 * A simplified data structure.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
 */

package com.drunkpiano.zhihuselection.utilities;

public class ListCellDataSimplified {
    private String title;
    private String summary;
    private String address;

    public ListCellDataSimplified(String title, String summary, String address) {
        this.title = title;
        this.summary = summary;
        this.address = address;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }
}
