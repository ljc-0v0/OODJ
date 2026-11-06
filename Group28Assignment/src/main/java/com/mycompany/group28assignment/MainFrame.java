
package com.mycompany.group28assignment;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    public MainFrame(User currentUser) {
        super("System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        this.currentUser = currentUser;
        add(container);

        if (currentUser instanceof Manager m) {

            JPanel managerPanel = managerPanel(m);

            container.add(managerPanel, "Manager");
            container.add(m.createManagerPanel(this), "CreateManager");

            cards.show(container, "Manager");

        } else if (currentUser instanceof AttendanceOfficer o) {

            JPanel attendanceOfficerPanel = attendance_officerPanel(o);

            container.add(attendanceOfficerPanel, "AttendanceOfficer");
            container.add(o.viewClassPanel(this), "viewClass");
            container.add(o.generateReportPanel(this), "GenerateReport");

            cards.show(container, "AttendanceOfficer");

        } else if (currentUser instanceof Instructor instructor) {

            container.add(
                new InstructorPanel(this::logout),
                "Instructor"
            );

            cards.show(container, "Instructor");
        }
    }

    private JPanel managerPanel(Manager m) {

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));

        JButton createManagerBtn = new JButton("Add New Manager");

        createManagerBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });

        panel.add(createManagerBtn);

        return panel;
    }

    private JPanel attendance_officerPanel(AttendanceOfficer o) {

        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));

        JButton viewBtn = new JButton("View Attendance Records");
        JButton searchBtn = new JButton("Search Attendance Records");
        JButton reportBtn = new JButton("Generate Attendance Report");
        JButton logoutBtn = new JButton("Log Out");

        viewBtn.addActionListener(e -> {
            showPanel("viewClass");
        });

        searchBtn.addActionListener(e -> {
            showPanel("searchAttendance");
        });

        reportBtn.addActionListener(e -> {
            showPanel("GenerateReport");
        });

        logoutBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });

        panel.add(viewBtn);
        panel.add(searchBtn);
        panel.add(reportBtn);
        panel.add(logoutBtn);

        return panel;
    }

    public void showAttendanceList(String classID) {

        AttendanceOfficer officer =
            (AttendanceOfficer) currentUser;

        JPanel attendancePanel =
            officer.viewAttendanceListPanel(this, classID);

        container.add(attendancePanel, "AttendanceList");
        cards.show(container, "AttendanceList");
    }

    public void showMarkAttendance(String attendanceID) {

        AttendanceOfficer officer =
            (AttendanceOfficer) currentUser;

        JPanel markPanel =
            officer.markAttendance(this, attendanceID);

        container.add(markPanel, "MarkAttendance");
        cards.show(container, "MarkAttendance");

        container.revalidate();
        container.repaint();
    }

    public void showPanel(String name) {
        cards.show(container, name);
    }

    private void logout() {

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Log Out",
            JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            dispose();

            SwingUtilities.invokeLater(() ->
                Group28Assignment.main(new String[0])
            );
        }
    }
}
