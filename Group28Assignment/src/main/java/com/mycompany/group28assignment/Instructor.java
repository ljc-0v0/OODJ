
package com.mycompany.group28assignment;

import javax.swing.*;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class Instructor extends User {

    public Instructor(String userID) {
        super(userID);

        try {
            List<String> lines = Files.readAllLines(
                    Path.of("users.txt")
            );

            for (String line : lines) {
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 7 && userID.equals(parts[0])) {
                    this.userName = parts[1];
                    this.userEmail = parts[2];
                    this.userRole = parts[5];
                    this.userPhoneNum = parts[4];
                    this.createdBy = parts[6];
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JPanel createSchedulePanel(MainFrame mainFrame) {
        return new InstructorPanel(this, mainFrame, 0);
    }

    public JPanel createFacilityProblemPanel(MainFrame mainFrame) {
        return new InstructorPanel(this, mainFrame, 1);
    }

    public JPanel createRatingPanel(MainFrame mainFrame) {
        return new InstructorPanel(this, mainFrame, 2);
    }
}
