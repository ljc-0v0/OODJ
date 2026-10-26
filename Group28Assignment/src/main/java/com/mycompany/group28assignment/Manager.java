package com.mycompany.group28assignment;
import java.nio.file.*;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import javax.swing.*;


public class Manager extends User{
//    private String managerID;
//    private String managerName;
//    private String managerEmail;
//    private String managerPassword;
//    private String managerPhoneNum;
//    private String createdBy;
//    
//    public Manager(String managerID,String managerName,String managerEmail,String managerPassword,String managerPhoneNum,String createdBy)
//    {
//        this.managerID = managerID;
//        this.managerName = managerName;
//        this.managerEmail = managerEmail;
//        this.managerPassword = DigestUtils.md5Hex(managerPassword);
//        this.managerPhoneNum = managerPhoneNum;
//        this.createdBy = createdBy;
//    }
//    
//    public String getManagerID()    { return managerID; }
//    public String getManagerName()  { return managerName; }
//    public String getManagerEmail() { return managerEmail; }
//    public String getPhoneNum() { return managerPhoneNum; }
//    public String getCreatedBy() { return createdBy; }
//    
//    
//    
//    public void storeInFile() {
//        try {
//            List<String> lines = Files.readAllLines(Path.of("managers.txt"));
//            String managerID = null;
//            String line = String.join("|",
//                    managerID, managerName, managerEmail,
//                    managerPassword, managerPhoneNum, createdBy);
//            
//            Files.writeString(Path.of("managers.txt"),
//                    line + "\n",
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.APPEND);
//        } catch (Exception  e) {
//            System.err.println("Failed to save manager " + managerID + ": " + e.getMessage());
//        }
//    }
    
    public void createManager(String createdBy){
        
    }
    
    
}


