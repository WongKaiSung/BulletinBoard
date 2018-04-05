package my.edu.tarc.bulletinboard.Class;

import java.io.Serializable;

/**
 * Created by but on 23/2/2018.
 */

public class BulletinDetail implements Serializable{
    private String BulletinID;
    private int BookmarkStatus,ReadStatus,DeleteStatus;

    public BulletinDetail() {
    }
    public BulletinDetail(String bulletinID) {
        BulletinID = bulletinID;
    }

    public BulletinDetail(String bulletinID, int bookmarkStatus, int readStatus, int deleteStatus) {
        BulletinID = bulletinID;
        BookmarkStatus = bookmarkStatus;
        ReadStatus = readStatus;
        DeleteStatus = deleteStatus;
    }

    public String getBulletinID() {
        return BulletinID;
    }

    public void setBulletinID(String bulletinID) {
        BulletinID = bulletinID;
    }

    public int getBookmarkStatus() {
        return BookmarkStatus;
    }

    public void setBookmarkStatus(int bookmarkStatus) {
        BookmarkStatus = bookmarkStatus;
    }

    public int getReadStatus() {
        return ReadStatus;
    }

    public void setReadStatus(int readStatus) {
        ReadStatus = readStatus;
    }

    public int getDeleteStatus() {
        return DeleteStatus;
    }

    public void setDeleteStatus(int deleteStatus) {
        DeleteStatus = deleteStatus;
    }
}
