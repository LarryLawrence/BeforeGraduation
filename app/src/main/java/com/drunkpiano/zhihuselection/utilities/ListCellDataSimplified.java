package com.drunkpiano.zhihuselection.utilities;

/**
 * Created by DrunkPiano on 16/5/3.
 */
public class ListCellDataSimplified {
    private String title ;
    private String summary ;
    private String address ;

    public ListCellDataSimplified( String title, String summary, String address){
        this.title = title ;
        this.summary = summary ;
        this.address = address ;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public void setAddress(String address) {
        this.address = address;
    }
}
