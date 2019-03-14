package examples.sdk.android.clover.com.appointo.sqlite;

import java.sql.Blob;

public class User {
    private String userName;
    private String emailId;
    private String collegeId;
    private byte[] userPic;

    public User() {
    }

    public User(String userName, String emailId, String collegeId, byte[] userPic) {
        this.userName = userName;
        this.emailId = emailId;
        this.collegeId = collegeId;
        this.userPic = userPic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public byte[] getUserPic() {
        return userPic;
    }

    public void setUserPic(byte[] userPic) {
        this.userPic = userPic;
    }
}
