package com.drunkpiano.zhihuselection;

/**
 * Created by DrunkPiano on 16/3/15.
 */
public class ListCellData {
    public ListCellData( String title,String time,String summary,String questionid, String answerid,
                         String authorname, String authorhash, String avatar, String vote){
        this.title = title ;
        this.time = time ;
        this.summary = summary ;
        this.questionid = questionid ;
        this.answerid = answerid ;
        this.authorname = authorname ;
        this.authorhash = authorhash ;
        this.avatar = avatar ;
        this.vote = vote ;

    }
    public ListCellData(){}//2nd constructor哪里用到了

    private String title = "";
    private String time = "";
    private String summary = "";
    private String questionid = "";
    private String answerid = "";
    private String authorname = "";
    private String authorhash = "";
    private String avatar = "" ;
    private String vote = "0";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getAuthorhash() {
        return authorhash;
    }

    public void setAuthorhash(String authorhash) {
        this.authorhash = authorhash;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
