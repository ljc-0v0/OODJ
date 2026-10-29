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
        setSize(500, 400);
        setLocationRelativeTo(null);

        this.currentUser = currentUser;

        add(container);
        if (currentUser instanceof Manager m) {
            // Create panels
            JPanel managerPanel = managerPanel(m);

            // Register each panel under a name
            container.add(managerPanel, "Manager");
            container.add(m.createManagerPanel(this), "CreateManager");
            
            cards.show(container, "Manager"); 
        }else if (currentUser instanceof AttendanceOfficer o) {
         
            JPanel attendance_officerPanel = attendance_officerPanel(o);

            container.add(attendance_officerPanel, "AttendanceOfficer");
            
            
            cards.show(container, "AttendanceOfficer");
        }  
    }
    
    private JPanel managerPanel(Manager m){
        JPanel panel = new JPanel(new GridLayout(2,2,8,8));
        JButton createManagerBtn = new JButton("Add New Manager");
        
        createManagerBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });
        panel.add(createManagerBtn);
        return panel;
    }
    
    private JPanel attendance_officerPanel(AttendanceOfficer o){
        JPanel panel = new JPanel(new GridLayout(4,4,8,8));
        JButton viewBtn = new JButton("View Attendance Records");
        JButton searchBtn = new JButton("Search Attendance Records");
        JButton reportBtn = new JButton("Generate Attendance Report");
        JButton logoutBtn = new JButton("Log Out");
        
        viewBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });
        searchBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });
        reportBtn.addActionListener(e -> {
            showPanel("CreateManager");
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
    
    public void showPanel(String name) {
        cards.show(container, name);
    }
}
