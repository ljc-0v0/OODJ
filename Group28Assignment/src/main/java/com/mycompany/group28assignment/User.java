package com.mycompany.group28assignment;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import javax.swing.*;


public class User {
    protected String userID;
    protected String userName;
    protected String userEmail;
    protected String userPassword;
    protected String userPhoneNum;
    protected String userRole;
    protected String createdBy;
    
    public User(){}
    
    public User(String userID){
        try{
            List<String> lines = Files.readAllLines(Path.of("users.txt"));
            for (String line : lines){
                String[] parts = line.split("\\|");
                if (!userID.isEmpty() && userID.matches(parts[0])){
                    this.userName = parts[1];
                    this.userEmail = parts[2];
                    this.userRole = parts[5];
                    this.userPhoneNum = parts[4];
                    this.createdBy = parts[6];
                }
            }
        }catch(Exception ex){
        
        }
    }
    
    public User(String userName, String userEmail,
                String userPassword, String userPhoneNum, String userRole,String createdBy) {
//        this.userID       = userID;
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
    
    public void storeInFile(String role) {
        try {
            List<String> lines = Files.readAllLines(Path.of("users.txt"));
            String last = null;
            for (int i = lines.size() - 1; i >= 0; i--) {
                if (!lines.get(i).isBlank()) {
                    last = lines.get(i);
                    break;   
                }
            }
            
            String[] parts = last.split("\\|");
            String lastUserID = parts[0];
            int n = Integer.parseInt(lastUserID.replaceAll("\\D", "")); // D means ALL NON DIGITS
            String nextID = String.format("UID%06d", n + 1);
            
            String nextUserID =  (nextID == null) ? "UID000001" : "UID" + nextID;
            String line = String.join("|",
                    nextUserID, userName, userEmail,
                    userPassword, userPhoneNum, userRole, createdBy);
            Files.writeString(Path.of("users.txt"),
                    line +"\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Failed to save user " + userID + ": " + e.getMessage());
        }
    }
}
