
package com.mycompany.group28assignment;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.*;

public class MainFrame extends JFrame {

    private final User currentUser;
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    protected final Deque<String> history = new ArrayDeque<>(); 
    private final Map<String, JPanel> cardMap = new HashMap<>();

    public MainFrame(User currentUser) {
        super("System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        this.currentUser = currentUser;
        add(container);

        if (currentUser instanceof Manager m) {
            // Create panels
            JPanel managerPanel = managerPanel(m);

            // Register each panel under a name
            cardMap.put("Manager", managerPanel);
            cardMap.put("CreateManager", m.createManagerPanel(this));
            cardMap.put("ViewManager", m.loadManagerPanel(this));
            cardMap.put("ViewStaff", m.loadStaffPanel(this));
            cardMap.put("ViewInstructor", m.loadInstructorPanel(this));
            
            container.add(managerPanel, "Manager");
            container.add(m.createManagerPanel(this), "CreateManager");
            container.add(m.loadManagerPanel(this), "ViewManager");
            container.add(m.createAdminPanel(this), "CreateAdmin");
            container.add(m.loadAdminPanel(this), "ViewAdmin");
            container.add(m.loadStaffPanel(this), "ViewStaff");
            container.add(m.loadInstructorPanel(this), "ViewInstructor");
            cards.show(container, "Manager"); 
        }  else if (currentUser instanceof AttendanceOfficer o) {

            JPanel attendanceOfficerPanel = attendance_officerPanel(o);

            container.add(attendanceOfficerPanel, "AttendanceOfficer");
            container.add(o.viewClassPanel(this), "viewClass");
            container.add(o.generateReportPanel(this), "GenerateReport");

            cards.show(container, "AttendanceOfficer");

        } else if (currentUser instanceof InventoryEquipmentManagement f) {

            JPanel facilityOfficerPanel = facility_OfficerPanel(f);
            JPanel equipmentPanel = f.createFacilityOfficerPanel(this);
            JPanel problemPanel = new FacilityProblemManagement(this);
            JPanel stockReportPanel = new EquipmentInventoryStockReport(this);

            cardMap.put("Facility Officer", facilityOfficerPanel);
            cardMap.put("View Equipment Inventory", equipmentPanel);
            cardMap.put("Facility Problem Management", problemPanel);
            cardMap.put("Equipment Stock Report", stockReportPanel);

            container.add(facilityOfficerPanel, "Facility Officer");
            container.add(equipmentPanel, "View Equipment Inventory");
            container.add(problemPanel, "Facility Problem Management");
            container.add(stockReportPanel, "Equipment Stock Report");

            cards.show(container, "Facility Officer");
            
        } else if (currentUser instanceof Instructor i) {

            JPanel instructorPanel = instructorPanel(i);
            JPanel schedulePanel = i.createSchedulePanel(this);
            JPanel facilityProblemPanel = i.createFacilityProblemPanel(this);
            JPanel ratingPanel = i.createRatingPanel(this);

            cardMap.put("Instructor", instructorPanel);
            cardMap.put("Schedule", schedulePanel);
            cardMap.put("Facility Problems", facilityProblemPanel);
            cardMap.put("Ratings", ratingPanel);

            container.add(instructorPanel, "Instructor");
            container.add(schedulePanel, "Schedule");
            container.add(facilityProblemPanel, "Facility Problems");
            container.add(ratingPanel, "Ratings");

            cards.show(container, "Instructor");
        }
    }

  private JPanel managerPanel(Manager m) {
        // Outer panel with soft background
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 247, 250));
        outer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ---- Header ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel title = new JLabel("Manager Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(33, 37, 41));

        JLabel subtitle = new JLabel("Welcome, " + m.getUserName());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(108, 117, 125));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);

        // ---- Card grid for actions ----
        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20));
        grid.setOpaque(false);

        JButton loadManagerBtn = new JButton("Manage Managers");
        loadManagerBtn.addActionListener(e -> showPanel("ViewManager"));

        JButton loadAdminBtn = new JButton("Manage Administrators");
        loadAdminBtn.addActionListener(e -> showPanel("ViewAdmin"));
        
        JButton loadStaffBtn = new JButton("Manage Staff");
        loadStaffBtn.addActionListener(e -> showPanel("ViewStaff"));
        
        JButton loadInstructorBtn = new JButton("View Comments");
        loadInstructorBtn.addActionListener(e -> showPanel("ViewInstructor"));

        styleCardButton(loadManagerBtn, "👥", "Manage manager accounts");
        styleCardButton(loadAdminBtn, "🛡️", "Manage administrator accounts");
        styleCardButton(loadStaffBtn, "🛡️", "Manage Staff");
        styleCardButton(loadInstructorBtn, "🛡️", "View Comments");

        grid.add(loadManagerBtn);
        grid.add(loadAdminBtn);
        grid.add(loadStaffBtn);
        grid.add(loadInstructorBtn);

        JButton logout = logoutBtn();
        styleLogoutButton(logout);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        bottom.add(logout);

        outer.add(header, BorderLayout.NORTH);
        outer.add(grid, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);

        return outer;
    }

// ---- Helper: nice card-style button ----
    private void styleCardButton(JButton btn, String icon, String description) {
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(new Color(33, 37, 41));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));

        JLabel textLabel = new JLabel("<html><div style='width:160px;'>"
                + btn.getText()
                + "<br><span style='font-weight:normal;font-size:11px;color:#6c757d;'>"
                + description + "</span></div></html>");

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(iconLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(textLabel);

        btn.removeAll();
        btn.add(content, BorderLayout.CENTER);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(248, 249, 250));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(13, 110, 253), 1, true),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(222, 226, 230), 1, true),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)));
            }
        });
    }

// ---- Helper: subtle logout button ----
    private void styleLogoutButton(JButton btn) {
        btn.setBackground(new Color(220, 53, 69));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setOpaque(true);
    }
    
    public void showEditManagerPanel(String uid) {

        Manager manager = (Manager) currentUser;

        JPanel editManagerPanel
                = manager.editManagerPanel(this, uid);

        container.add(editManagerPanel, "EditManagerPanel");
        cardMap.put("EditManagerPanel", editManagerPanel);
        cards.show(container, "EditManagerPanel");
    }
    
    public void showEditAdminPanel(String uid) {

        Manager manager = (Manager) currentUser;

        JPanel editAdminPanel
                = manager.editAdminPanel(this, uid);

        container.add(editAdminPanel, "EditAdminPanel");
        cardMap.put("EditAdminPanel", editAdminPanel);
        cards.show(container, "EditAdminPanel");
    }

    public void showInstructorDetailPanel(String instructorUID) {
        Manager manager = (Manager) currentUser;
        JPanel detail = manager.instructorDetailPanel(this, instructorUID);

        cardMap.put("InstructorDetail", detail);
        container.add(detail, "InstructorDetail");
        cards.show(container, "InstructorDetail");
    }

    private JPanel attendance_officerPanel(AttendanceOfficer o){
        JPanel panel = new JPanel(new GridLayout(3,3,8,8));
        JButton viewBtn = new JButton("View Attendance Records");
        JButton reportBtn = new JButton("Generate Attendance Report");
        JButton logoutBtn = new JButton("Log Out");
        
        viewBtn.addActionListener(e -> {
            showPanel("viewClass");
        });
        reportBtn.addActionListener(e -> {
            showPanel("GenerateReport");
        });
        logoutBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });
        
        panel.add(viewBtn);
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

    private JPanel facility_OfficerPanel(InventoryEquipmentManagement f) {

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JLabel title = new JLabel(
                "Facility Officer Dashboard",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton loadFacility_OfficerBtn =
                new JButton("View Equipment Inventory");

        JButton problemBtn =
                new JButton("Facility Problem Management");

        JButton stockReportBtn =
                new JButton("Equipment Stock Report");

        loadFacility_OfficerBtn.addActionListener(e ->
                showPanel("View Equipment Inventory")
        );

        problemBtn.addActionListener(e ->
                showPanel("Facility Problem Management")
        );

        stockReportBtn.addActionListener(e ->
                showPanel("Equipment Stock Report")
        );

        panel.add(title);
        panel.add(loadFacility_OfficerBtn);
        panel.add(problemBtn);
        panel.add(stockReportBtn);
        panel.add(logoutBtn());

        return panel;
    }
    
    private JPanel instructorPanel(Instructor i) {

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JLabel title = new JLabel(
                "Instructor Dashboard",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton scheduleBtn = new JButton("Manage Schedule");
        JButton facilityBtn = new JButton("Manage Facility Problems");
        JButton ratingBtn = new JButton("View Ratings");

        scheduleBtn.addActionListener(e -> {
            showPanel("Schedule");
        });

        facilityBtn.addActionListener(e -> {
            showPanel("Facility Problems");
        });

        ratingBtn.addActionListener(e -> {
            showPanel("Ratings");
        });

        panel.add(title);
        panel.add(scheduleBtn);
        panel.add(facilityBtn);
        panel.add(ratingBtn);
        panel.add(logoutBtn());

        return panel;
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

    public void refresh(String name, Supplier<JPanel> builder) {
        JPanel old = cardMap.get(name);
        if (old != null) {
            container.remove(old);
        }
        JPanel fresh = builder.get();    
        cardMap.put(name, fresh);
        container.add(fresh, name);
        cards.show(container, name);
        container.revalidate();
        container.repaint();
    }
}
