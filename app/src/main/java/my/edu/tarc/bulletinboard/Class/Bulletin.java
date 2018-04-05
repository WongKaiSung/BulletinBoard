package my.edu.tarc.bulletinboard.Class;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by but on 25/1/2018.
 */

public class Bulletin implements Serializable{
    private String bulletinID,bulletinTitle,description;
    private Date PostDate;
    private String postedBy,sendTo,EventBulletinID,EventRegistrationID;

    public Bulletin() {
    }

    public Bulletin(String bulletinID,String bulletinTitle, String description, Date postDate, String postedBy, String sendTo,String EventBulletinID,String EventRegistrationID) {
        this.bulletinID = bulletinID;
        this.bulletinTitle = bulletinTitle;
        this.description = description;
        this.PostDate = postDate;
        this.postedBy = postedBy;
        this.sendTo = sendTo;
        this.EventBulletinID = EventBulletinID;
        this.EventRegistrationID = EventRegistrationID;

    }

    public String getEventBulletinID() {
        return EventBulletinID;
    }

    public void setEventBulletinID(String eventBulletinID) {
        EventBulletinID = eventBulletinID;
    }

    public String getBulletinID() {
        return bulletinID;
    }

    public void setBulletinID(String bulletinID) {
        this.bulletinID = bulletinID;
    }

    public String getBulletinTitle() {
        return bulletinTitle;
    }

    public void setBulletinTitle(String bulletinTitle) {
        this.bulletinTitle = bulletinTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public Date getPostDate() {
        return PostDate;
    }

    public void setPostDate(Date postDate) {
        PostDate = postDate;
    }

    public String getEventRegistrationID() {
        return EventRegistrationID;
    }

    public void setEventRegistrationID(String eventRegistrationID) {
        EventRegistrationID = eventRegistrationID;
    }
}
