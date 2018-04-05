package my.edu.tarc.bulletinboard.Class;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by but on 26/2/2018.
 */

public class EventRegistration implements Serializable{
    private String EventRegistrationID,LINK;
    private Date RegistrationStartDate,RegistrationDueDate;

    public EventRegistration() {

    }

    public EventRegistration(String eventRegistrationID, String LINK, Date registrationStartDate, Date registrationDueDate) {
        this.EventRegistrationID = eventRegistrationID;
        this.LINK = LINK;
        this.RegistrationStartDate = registrationStartDate;
        this.RegistrationDueDate = registrationDueDate;
    }

    public String getEventRegistrationID() {
        return EventRegistrationID;
    }

    public void setEventRegistrationID(String eventRegistrationID) {
        EventRegistrationID = eventRegistrationID;
    }

    public String getLINK() {
        return LINK;
    }

    public void setLINK(String LINK) {
        this.LINK = LINK;
    }

    public Date getRegistrationStartDate() {
        return RegistrationStartDate;
    }

    public void setRegistrationStartDate(Date registrationStartDate) {
        RegistrationStartDate = registrationStartDate;
    }

    public Date getRegistrationDueDate() {
        return RegistrationDueDate;
    }

    public void setRegistrationDueDate(Date registrationDueDate) {
        RegistrationDueDate = registrationDueDate;
    }
}
