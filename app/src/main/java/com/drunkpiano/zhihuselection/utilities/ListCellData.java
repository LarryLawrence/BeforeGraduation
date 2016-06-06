/*
 * A data structure to store complex data.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
 */

package com.drunkpiano.zhihuselection.utilities;

public class ListCellData {
    private String mTitle = "";
    private String mSummary = "";
    private String mQuestionId = "";
    private String mAnswerId = "";

    public ListCellData( String mTitle, String mSummary, String mQuestionId, String mAnswerId){
        this.mTitle = mTitle ;
        this.mSummary = mSummary ;
        this.mQuestionId = mQuestionId ;
        this.mAnswerId = mAnswerId ;
    }

    public ListCellData(){}//2nd constructor哪里用到了

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getQuestionid() {
        return mQuestionId;
    }

    public void setQuestionid(String mQuestionId) {
        this.mQuestionId = mQuestionId;
    }

    public String getAnswerid() {
        return mAnswerId;
    }

    public void setAnswerid(String mAnswerId) {
        this.mAnswerId = mAnswerId;
    }

}
