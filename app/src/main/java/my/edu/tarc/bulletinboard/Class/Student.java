package my.edu.tarc.bulletinboard.Class;

/**
 * Created by but on 21/2/2018.
 */

public class Student {
    private String UserID,Name,ICNum,Contact,Email,Address,
            UserType,UserPassword,ProgrammeID,TutorialGroup,INTAKE;

    public Student() {
    }

    public Student(String userID, String name, String ICNum, String contact, String email, String address, String userType, String userPassword, String programmeID, String tutorialGroup, String INTAKE) {
        this.UserID = userID;
        this.Name = name;
        this.ICNum = ICNum;
        this.Contact = contact;
        this.Email = email;
        this.Address = address;
        this.UserType = userType;
        this.UserPassword = userPassword;
        this.ProgrammeID = programmeID;
        this.TutorialGroup = tutorialGroup;
        this.INTAKE = INTAKE;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getICNum() {
        return ICNum;
    }

    public void setICNum(String ICNum) {
        this.ICNum = ICNum;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getProgrammeID() {
        return ProgrammeID;
    }

    public void setProgrammeID(String programmeID) {
        ProgrammeID = programmeID;
    }

    public String getTutorialGroup() {
        return TutorialGroup;
    }

    public void setTutorialGroup(String tutorialGroup) {
        TutorialGroup = tutorialGroup;
    }

    public String getINTAKE() {
        return INTAKE;
    }

    public void setINTAKE(String INTAKE) {
        this.INTAKE = INTAKE;
    }
}
