
package com.mycompany.group28assignment;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.Supplier;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);

    protected final Deque<String> history = new ArrayDeque<>();
    private final Map<String, JPanel> cardMap = new HashMap<>();

    public MainFrame(User currentUser) {
        super("System");

        this.currentUser = currentUser;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        add(container);

        if (currentUser instanceof Manager m) {

            JPanel managerPanel = managerPanel(m);

            addPanel("Manager", managerPanel);
            addPanel("CreateManager", m.createManagerPanel(this));

            showPanel("Manager");

        } else if (currentUser instanceof AttendanceOfficer o) {

            JPanel attendancePanel = attendanceOfficerPanel();

            addPanel("AttendanceOfficer", attendancePanel);
            addPanel("viewClass", o.viewClassPanel(this));
            addPanel("GenerateReport", o.generateReportPanel(this));

            showPanel("AttendanceOfficer");

        } else if (currentUser instanceof Instructor i) {

            addPanel("Instructor", instructorPanel());
            addPanel("Schedule", i.createSchedulePanel(this));
            addPanel("Facility Problems",
                    i.createFacilityProblemPanel(this));
            addPanel("Ratings", i.createRatingPanel(this));

            showPanel("Instructor");
        }
    }

    private void addPanel(String name, JPanel panel) {
        cardMap.put(name, panel);
        container.add(panel, name);
    }

    private JPanel managerPanel(Manager m) {

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JButton createBtn = new JButton("Add New Manager");

        createBtn.addActionListener(e ->
                showPanel("CreateManager"));

        panel.add(createBtn);
        panel.add(logoutBtn());

        return panel;
    }

    private JPanel attendanceOfficerPanel() {

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JButton viewBtn =
                new JButton("View Attendance Records");

        JButton searchBtn =
                new JButton("Search Attendance Records");

        JButton reportBtn =
                new JButton("Generate Attendance Report");

        viewBtn.addActionListener(e ->
                showPanel("viewClass"));

        searchBtn.addActionListener(e ->
                showPanel("searchAttendance"));

        reportBtn.addActionListener(e ->
                showPanel("GenerateReport"));

        panel.add(viewBtn);
        panel.add(searchBtn);
        panel.add(reportBtn);
        panel.add(logoutBtn());

        return panel;
    }

    private JPanel instructorPanel() {

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JLabel title = new JLabel(
                "Instructor Dashboard",
                SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton scheduleBtn =
                new JButton("Manage Schedule");

        JButton facilityBtn =
                new JButton("Manage Facility Problems");

        JButton ratingBtn =
                new JButton("View Ratings");

        scheduleBtn.addActionListener(e ->
                showPanel("Schedule"));

        facilityBtn.addActionListener(e ->
                showPanel("Facility Problems"));

        ratingBtn.addActionListener(e ->
                showPanel("Ratings"));

        panel.add(title);
        panel.add(scheduleBtn);
        panel.add(facilityBtn);
        panel.add(ratingBtn);
        panel.add(logoutBtn());

        return panel;
    }

    public void showAttendanceList(String classID) {

        AttendanceOfficer officer =
                (AttendanceOfficer) currentUser;

        JPanel panel =
                officer.viewAttendanceListPanel(this, classID);

        addPanel("AttendanceList", panel);
        showPanel("AttendanceList");
    }

    public void showMarkAttendance(String attendanceID) {

        AttendanceOfficer officer =
                (AttendanceOfficer) currentUser;

        JPanel panel =
                officer.markAttendance(this, attendanceID);

        addPanel("MarkAttendance", panel);
        showPanel("MarkAttendance");
    }

    public void showPanel(String name) {
        cards.show(container, name);
    }

    public void back() {
        if (!history.isEmpty()) {
            showPanel(history.pop());
        }
    }

    public void refresh(String name, Supplier<JPanel> builder) {

        JPanel old = cardMap.get(name);

        if (old != null) {
            container.remove(old);
        }

        JPanel fresh = builder.get();

        addPanel(name, fresh);
        showPanel(name);

        container.revalidate();
        container.repaint();
    }

    private JButton logoutBtn() {

        JButton button = new JButton("Log Out");

        button.addActionListener(e -> logout());

        return button;
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
                    Group28Assignment.main(new String[0]));
        }
    }
}
