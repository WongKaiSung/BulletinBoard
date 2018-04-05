package my.edu.tarc.bulletinboard.Class;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by but on 26/2/2018.
 */

public class EventBulletin implements Serializable {
    private String EventBulletinID,Attachment;
    private Date EventStartDate,EventDueDate,PaymentDueDate;
    private double Fees;

    public EventBulletin() {

    }
    public EventBulletin(String eventBulletinID, String attachment, Date eventStartDate, Date eventDueDate, Date paymentDueDate, double fees) {
        EventBulletinID = eventBulletinID;
        Attachment = attachment;
        EventStartDate = eventStartDate;
        EventDueDate = eventDueDate;
        PaymentDueDate = paymentDueDate;
        Fees = fees;
    }

    public String getEventBulletinID() {
        return EventBulletinID;
    }

    public void setEventBulletinID(String eventBulletinID) {
        EventBulletinID = eventBulletinID;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String attachment) {
        Attachment = attachment;
    }

    public Date getEventStartDate() {
        return EventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        EventStartDate = eventStartDate;
    }

    public Date getEventDueDate() {
        return EventDueDate;
    }

    public void setEventDueDate(Date eventDueDate) {
        EventDueDate = eventDueDate;
    }

    public Date getPaymentDueDate() {
        return PaymentDueDate;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        PaymentDueDate = paymentDueDate;
    }

    public double getFees() {
        return Fees;
    }

    public void setFees(double fees) {
        Fees = fees;
    }
}
