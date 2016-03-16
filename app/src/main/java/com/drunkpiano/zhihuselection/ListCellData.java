package com.drunkpiano.zhihuselection;

/**
 * Created by DrunkPiano on 16/3/15.
 */
public class ListCellData {
    public ListCellData(String authorname, String title, String summary, String avatarId, String vote){
        this.authorname = authorname ;
        this.title = title ;
        this.avatarId = avatarId ;
        this.vote = vote ;
        this.summary = summary ;

    }
    private String authorname = "";
    private String title = "";
    private String summary = "";
    private String avatarId = "" ;
    private String vote = "0";

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

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }



    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }
}
