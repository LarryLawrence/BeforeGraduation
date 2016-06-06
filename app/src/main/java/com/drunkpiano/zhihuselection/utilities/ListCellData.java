/*
 * A data structure to store complex data.
 * @author DrunkPiano
 * @version 1.1.2
 * Modifying History:
 * Modifier: DrunkPiano, June 3rd 2016, fixed to accord it with standard coding disciplines.
 */

package com.drunkpiano.zhihuselection.utilities;

public class ListCellData {
    private String title = "";
    private String summary = "";
    private String questionid = "";
    private String answerid = "";

    public ListCellData( String title, String summary, String questionid, String answerid){
        this.title = title ;
        this.summary = summary ;
        this.questionid = questionid ;
        this.answerid = answerid ;
    }

    public ListCellData(){}//2nd constructor哪里用到了

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getAnswerid() {
        return answerid;
    }

    public void setAnswerid(String answerid) {
        this.answerid = answerid;
    }

}
