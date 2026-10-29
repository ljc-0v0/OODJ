package com.mycompany.group28assignment;
import java.awt.*;
import java.nio.file.*;
import java.util.List;
import javax.swing.*;

public class AttendanceOfficer extends User{
    private MainFrame mainFrame;
    
    public AttendanceOfficer(String userID) {
        try {
            List<String> lines = Files.readAllLines(Path.of("users.txt"));
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (!userID.isEmpty() && userID.equals(parts[0])) {
                    this.userName = parts[1];
                    this.userEmail = parts[2];
                    this.userRole = parts[5];
                    this.userPhoneNum = parts[4];
                    this.createdBy = parts[6];
                    break;
                }
            }
        } catch (Exception ex) {

        }
    }   
    
    
}   
