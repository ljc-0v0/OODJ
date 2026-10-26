package com.mycompany.group28assignment;
import java.nio.file.*;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import javax.swing.*;


public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhoneNum;
    private String userRole;
    private String createdBy;
    
    public User(){}
    
    public User(String userID, String userName, String userEmail,
                String userPassword, String userPhoneNum, String userRole,String createdBy) {
        this.userID       = userID;
        this.userName     = userName;
        this.userEmail    = userEmail;
        this.userRole    = userRole;
        this.userPassword = DigestUtils.md5Hex(userPassword);
        this.userPhoneNum = userPhoneNum;
        this.createdBy    = createdBy;
    }

    public String getUserID()    { return userID; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getPhoneNum()  { return userPhoneNum; }
    public String getUserRole() { return userRole; }
    public String getCreatedBy() { return createdBy; }

    public void storeInFile() {
        String line = String.join("|",
                userID, userName, userEmail,
                userPassword, userPhoneNum, userRole, createdBy);

        try {
            Files.writeString(Path.of("users.txt"),
                    line +"\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Failed to save user " + userID + ": " + e.getMessage());
        }
    }
}
