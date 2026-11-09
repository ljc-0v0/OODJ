package com.mycompany.group28assignment;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;


public class User {
    protected String userID;
    protected String userName;
    protected String userEmail;
    protected String userPassword;
    protected String userPhoneNum;
    protected String userRole;
    protected String createdBy;
    protected boolean status;
    protected enum Status {
        ACTIVE("Active", true),
        DEACTIVATED("Deactivated", false);

        private final String label;
        private final boolean value;

        Status(String label, boolean value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public boolean getValue() { return value; }

        @Override
        public String toString() { return label; }  
        
        public static Status fromValue(boolean value) {
            return value ? ACTIVE : DEACTIVATED;
        }
        
        public static Status fromString(String s) {
            return fromValue(Boolean.parseBoolean(s == null ? "" : s.trim()));
        }
    }
    
    public User(){}
    
    public User(String userID){
        try{
            List<String> lines = Files.readAllLines(Path.of("users.txt"));
            for (String line : lines){
                String[] parts = line.split("\\|");
                if (!userID.isEmpty() && userID.matches(parts[0])){
                    this.userID = userID;
                    this.userName = parts[1];
                    this.userEmail = parts[2];
                    this.userRole = parts[5];
                    this.userPhoneNum = parts[4];
                    this.createdBy = parts[6];
                    this.status = parts[7].equals("true") ? true : false;
                }
            }
        }catch(Exception ex){
        
        }
    }
    
    public User(String userName, String userEmail,
                String userPassword, String userPhoneNum, String userRole,String createdBy, boolean status) {
//        this.userID       = userID;
        this.userName     = userName;
        this.userEmail    = userEmail;
        this.userRole    = userRole;
        this.userPassword = DigestUtils.md5Hex(userPassword);
        this.userPhoneNum = userPhoneNum;
        this.createdBy    = createdBy;
        this.status = status;
        
    }

    public String getUserID()    { return userID; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getPhoneNum()  { return userPhoneNum; }
    public String getUserRole() { return userRole; }
    public String getCreatedBy() { return createdBy; }
    public boolean getStatus() { return status; }
    
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
            
            String nextUserID =  (nextID == null) ? "UID000001" : nextID;
            String line = String.join("|",
                    nextUserID, userName, userEmail,
                    userPassword, userPhoneNum, userRole, createdBy, Boolean.toString(status));
            Files.writeString(Path.of("users.txt"),
                    line +"\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Failed to save user: " + e.getMessage());
        }
    }
    
    public void editInFile(String userName, String userEmail,
                String userPhoneNum, String createdBy, boolean status) {
        try {
            List<String> lines = Files.readAllLines(Path.of("users.txt"));
            Path editTempFile = Path.of("editTempFile.txt");
            Files.write(editTempFile, new byte[0]);
            for (String line : lines){
                String[] parts = line.split("\\|");
                if (!parts[0].equals(this.userID)){
                    Files.writeString(editTempFile, line + System.lineSeparator(),
                            StandardOpenOption.APPEND);
                }

                if (parts[0].equals(this.userID)){
                    String details = String.join("|",
                            userID, userName, userEmail,
                            parts[3] ,userPhoneNum, userRole, createdBy, Boolean.toString(status));
                    Files.writeString(editTempFile, details + System.lineSeparator(),
                            StandardOpenOption.APPEND);
                }
            }
            Files.move(editTempFile, Path.of("users.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Failed to save user: " + e.getMessage());
        }
    }
    
}
