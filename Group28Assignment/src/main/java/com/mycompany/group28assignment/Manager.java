package com.mycompany.group28assignment;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


public class Manager extends User{
    private MainFrame mainFrame;
    
    public Manager(String userID) {
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
    
    public JPanel createManagerPanel(MainFrame mainFrame) {

        JTextField usernameInput = new JTextField(15);
        JPasswordField passwordInput = new JPasswordField(15);
        JTextField emailInput = new JTextField(15);
        JTextField roleInput = new JTextField("Manager", 15);
        JTextField phoneInput = new JTextField(15);
        JTextField createdByInput = new JTextField(this.userName, 15);
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());

        roleInput.setEditable(false);
        createdByInput.setEditable(false);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("Username:"));
        form.add(usernameInput);
        form.add(new JLabel("Password:"));
        form.add(passwordInput);
        form.add(new JLabel("Email:"));
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        form.add(phoneInput);
        form.add(new JLabel("Created By:"));
        form.add(createdByInput);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton submitBtn = new JButton("Create");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(submitBtn);
        buttons.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmit(
                mainFrame,null , usernameInput, passwordInput,
                emailInput,roleInput, phoneInput,statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewManager"));

        return panel;
    }
    
    public JPanel editManagerPanel(MainFrame mainFrame, String uid) {

        JTextField uidInput = new JTextField(15);
        JTextField usernameInput = new JTextField(15);
        JPasswordField passwordInput = new JPasswordField(15);
        JTextField emailInput = new JTextField(15);
        JTextField roleInput = new JTextField("Manager", 15);
        JTextField phoneInput = new JTextField(15);
        JTextField createdByInput = new JTextField(this.userName, 15);
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());

        uidInput.setEditable(false);
        roleInput.setEditable(false);
        createdByInput.setEditable(false);
        passwordInput.setEditable(false);
        
        String details = loadEditUser(uid);
        String parts[] = details.split("\\|");

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("User ID:"));
        uidInput.setText(parts[0]);
        form.add(uidInput);
        form.add(new JLabel("Username:"));
        usernameInput.setText(parts[1]);
        form.add(usernameInput);
        form.add(new JLabel("Password:"));
        passwordInput.setText("*******");
        form.add(passwordInput);
        form.add(new JLabel("Email:"));
        emailInput.setText(parts[2]);
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        roleInput.setText(parts[5]);
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        phoneInput.setText(parts[4]);
        form.add(phoneInput);
        form.add(new JLabel("Created By:"));
        createdByInput.setText(parts[6]);
        form.add(createdByInput);
        form.add(new JLabel("Status:"));
        statusBox.setSelectedItem(Status.fromString(parts[7]));
        form.add(statusBox);

        JButton submitBtn = new JButton("Edit");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(submitBtn);
        buttons.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmit(
                mainFrame, uidInput, usernameInput, passwordInput,
                emailInput,roleInput, phoneInput, statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewManager"));

        return panel;
    }
    
    private String loadEditUser(String uid) {
        Path path = Path.of("users.txt");
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 1 && parts[0].equals(uid)) {
                    return line;                       
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Could not load: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;                                   
    }


    private void handleSubmit(MainFrame mainFrame,
            JTextField uidInput,
            JTextField usernameInput,
            JPasswordField passwordInput,
            JTextField emailInput,
            JTextField roleInput,
            JTextField phoneInput,
            JComboBox<Status> statusInput) {

        String uid = (uidInput == null || uidInput.getText().trim().isEmpty())
                ? null
                : uidInput.getText().trim();
        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());
        String email = emailInput.getText().trim();
        String phoneNo = phoneInput.getText().trim();
        String role = roleInput.getText().trim();
        String createdBy = this.userName;
        Status sel = (Status) statusInput.getSelectedItem();
        boolean status = sel != null && sel.getValue();
        
        if (username.isEmpty()) {
            warn(mainFrame, "Username is required.", usernameInput);
            return;
       }
//             else if(!username.isEmpty() && uid == null){
//            try {
//                List<String> lines = Files.readAllLines(Path.of("users.txt"));
//                for (String line : lines) {
//                    if (line.isBlank()) {
//                        continue;
//                    }
//                    String[] parts = line.split("\\|");
//                    if(parts[1].equalsIgnoreCase(username)){
//                        warn(mainFrame,
//                                "username '" + username + "' already exsits!!",
//                                usernameInput);
//                    }
//                }
//            } catch (IOException e) {
//                JOptionPane.showMessageDialog(mainFrame,
//                        "Read Failed: " + e.getMessage(),
//                        "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";
        if (!password.matches(passwordRegex) && uid == null) {
            warn(mainFrame,
                    "Password must be at least 8 characters and contain\n"
                    + "an uppercase letter, a lowercase letter, and a digit.",
                    passwordInput);
            return;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            warn(mainFrame, "Email must look like name@domain.com.", emailInput);
            return;
        }

        if (!phoneNo.matches("\\d{9}")) {
            warn(mainFrame, "Phone number must be exactly 9 digits.", phoneInput);
            return;
        }

        try {
            
            if (uid != null && !uid.isEmpty()) {
                try {
                    List<String> lines = Files.readAllLines(Path.of("users.txt"));
                    for (String line : lines) {
                        if (line.isBlank()) {
                            continue;
                        }
                        String[] parts = line.split("\\|");
                        if (parts[0].equalsIgnoreCase(uid)) {
                            User editUser = new User(uid);
                            editUser.editInFile(username,email,phoneNo,createdBy,status);

                            JOptionPane.showMessageDialog(mainFrame,
                                    role + " updated: " + username,
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Read failed: " + e.getMessage());
                }
            }else{
                User newUser = new User(username, email, password, phoneNo, role, createdBy,status);
                newUser.storeInFile(role);

                JOptionPane.showMessageDialog(mainFrame,
                        role + " created: " + username,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }            
           
            if (role.equals("Manager")){
                mainFrame.refresh("LoadManagerPanel", () -> loadManagerPanel(mainFrame));
            }else if (role.equals("Admin")){
                 mainFrame.refresh("LoadAdminPanel", () -> loadAdminPanel(mainFrame));
            }
//            loadManagerPanel(mainFrame);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Could not save: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void warn(MainFrame mainFrame, String message, JTextField focusField) {
        JOptionPane.showMessageDialog(mainFrame, message,
            "Validation Error", JOptionPane.WARNING_MESSAGE);
        focusField.requestFocus();
    }
    
    public JPanel loadManagerPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Phone No.", "Modified By","Status"};
        Object[][] data = loadUser("Manager");
        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    boolean active = "Active".equals(
                            getModel().getValueAt(convertRowIndexToModel(row), 5));
                    c.setBackground(active ? new Color(220, 255, 220)
                            : new Color(255, 225, 225));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = table.rowAtPoint(e.getPoint());
                    if (viewRow < 0) {
                        return;                        
                    }
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String uid = (String) table.getModel().getValueAt(modelRow, 0);
                    mainFrame.refresh("EditManagerPanel", () -> editManagerPanel(mainFrame, uid));
                    mainFrame.showEditManagerPanel(uid);
                }
            }
        });

        JTextField searchField = new JTextField(30);
        String[] fields = {"All", "ID", "Name", "Email", "Phone No.", "Created By"};
        JLabel fieldLabel = new JLabel("Fillter By:");
        JComboBox<String> columnBox = new JComboBox<>(fields);
        String[] statusFields = {"All", "Active", "Deactivated"};
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(statusFields);
        statusBox.setSelectedIndex(1);
        

        Runnable applyFilter = () -> {
            String search = searchField.getText().trim();
            int colIndex = columnBox.getSelectedIndex() - 1;  
            int statusIdx = statusBox.getSelectedIndex();        

            java.util.List<RowFilter<TableModel, Integer>> filters = new java.util.ArrayList<>();
            if (!search.isEmpty()) {
                String pat = "(?i)" + java.util.regex.Pattern.quote(search);
                if (colIndex < 0) {
                    filters.add(RowFilter.regexFilter(pat, 0, 1, 2, 3, 4)); 
                } else {
                    filters.add(RowFilter.regexFilter(pat, colIndex));
                }
            }

            if (statusIdx > 0) {
                String wanted = (statusIdx == 1) ? "Active" : "Deactivated";
                filters.add(RowFilter.regexFilter("^" + wanted + "$", 5));   
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));            
            }
        };
        
        statusBox.addActionListener(e -> applyFilter.run());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applyFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                applyFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                applyFilter.run();
            }
        });

        columnBox.addActionListener(e -> applyFilter.run());

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(160, 231, 255));
        searchBtn.addActionListener(e -> applyFilter.run());

        JButton clearBtn = new JButton("Reset");
        clearBtn.setBackground(new Color(248, 152, 152));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            columnBox.setSelectedIndex(0);
            statusBox.setSelectedIndex(1);
            sorter.setRowFilter(null);
            searchField.requestFocus();
        });
        
        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(5, 144, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            mainFrame.showPanel("CreateManager");
        });

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showPanel("Manager"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBar.add(backBtn);
        searchBar.add(statusLabel);
        searchBar.add(statusBox);
        searchBar.add(fieldLabel);
        searchBar.add(columnBox);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);
        searchBar.add(addBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        applyFilter.run();   
        return panel;
    }
    
    private Object[][] loadUser(String role) {
        List<Object[]> rows = new ArrayList<>();
        Path file = Path.of("users.txt");

        if (!Files.exists(file)) {
            return new Object[0][0];
        }

        try {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) {
                    continue;
                }

                String[] p = line.split("\\|", -1);
                if (p.length < 8) {
                    continue;              
                }
                if (!role.equals(p[5])) {
                    continue;  
                }
                rows.add(new Object[]{
                    p[0], 
                    p[1], 
                    p[2],
                    p[4], 
                    p[6],
                    p[7].equals("true") ? "Active" : "Deactivated"
                });
            }
        } catch (IOException ex) {
            System.err.println("Read failed: " + ex.getMessage());
        }

        return rows.toArray(new Object[0][]);   
    }
    
    public JPanel createAdminPanel(MainFrame mainFrame) {

        JTextField usernameInput = new JTextField(15);
        JPasswordField passwordInput = new JPasswordField(15);
        JTextField emailInput = new JTextField(15);
        JTextField roleInput = new JTextField("Admin", 15);
        JTextField phoneInput = new JTextField(15);
        JTextField createdByInput = new JTextField(this.userName, 15);
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());

        roleInput.setEditable(false);
        createdByInput.setEditable(false);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("Username:"));
        form.add(usernameInput);
        form.add(new JLabel("Password:"));
        form.add(passwordInput);
        form.add(new JLabel("Email:"));
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        form.add(phoneInput);
        form.add(new JLabel("Created By:"));
        form.add(createdByInput);
        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton submitBtn = new JButton("Create");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(submitBtn);
        buttons.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmit(
                mainFrame,null , usernameInput, passwordInput,
                emailInput,roleInput, phoneInput,statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewAdmin"));

        return panel;
    }
    
    public JPanel editAdminPanel(MainFrame mainFrame, String uid) {

        JTextField uidInput = new JTextField(15);
        JTextField usernameInput = new JTextField(15);
        JPasswordField passwordInput = new JPasswordField(15);
        JTextField emailInput = new JTextField(15);
        JTextField roleInput = new JTextField("Admin", 15);
        JTextField phoneInput = new JTextField(15);
        JTextField createdByInput = new JTextField(this.userName, 15);
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());

        uidInput.setEditable(false);
        roleInput.setEditable(false);
        createdByInput.setEditable(false);
        passwordInput.setEditable(false);
        
        String details = loadEditUser(uid);
        String parts[] = details.split("\\|");

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("User ID:"));
        uidInput.setText(parts[0]);
        form.add(uidInput);
        form.add(new JLabel("Username:"));
        usernameInput.setText(parts[1]);
        form.add(usernameInput);
        form.add(new JLabel("Password:"));
        passwordInput.setText("*******");
        form.add(passwordInput);
        form.add(new JLabel("Email:"));
        emailInput.setText(parts[2]);
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        roleInput.setText(parts[5]);
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        phoneInput.setText(parts[4]);
        form.add(phoneInput);
        form.add(new JLabel("Created By:"));
        createdByInput.setText(parts[6]);
        form.add(createdByInput);
        form.add(new JLabel("Status:"));
        statusBox.setSelectedItem(Status.fromString(parts[7]));
        form.add(statusBox);

        JButton submitBtn = new JButton("Edit");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(submitBtn);
        buttons.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmit(
                mainFrame, uidInput, usernameInput, passwordInput,
                emailInput,roleInput, phoneInput, statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewAdmin"));

        return panel;
    }
    
    public JPanel loadAdminPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Phone No.", "Modified By","Status"};
        Object[][] data = loadUser("Admin");
        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    boolean active = "Active".equals(
                            getModel().getValueAt(convertRowIndexToModel(row), 5));
                    c.setBackground(active ? new Color(220, 255, 220)
                            : new Color(255, 225, 225));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = table.rowAtPoint(e.getPoint());
                    if (viewRow < 0) {
                        return;                        
                    }
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String uid = (String) table.getModel().getValueAt(modelRow, 0);
                    mainFrame.refresh("EditAdminPanel", () -> editAdminPanel(mainFrame, uid));
                    mainFrame.showEditAdminPanel(uid);
                }
            }
        });

        JTextField searchField = new JTextField(30);
        String[] fields = {"All", "ID", "Name", "Email", "Phone No.", "Created By"};
        JLabel fieldLabel = new JLabel("Fillter By:");
        JComboBox<String> columnBox = new JComboBox<>(fields);
        String[] statusFields = {"All", "Active", "Deactivated"};
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(statusFields);
        statusBox.setSelectedIndex(1);
        

        Runnable applyFilter = () -> {
            String search = searchField.getText().trim();
            int colIndex = columnBox.getSelectedIndex() - 1;  
            int statusIdx = statusBox.getSelectedIndex();        

            java.util.List<RowFilter<TableModel, Integer>> filters = new java.util.ArrayList<>();
            if (!search.isEmpty()) {
                String pat = "(?i)" + java.util.regex.Pattern.quote(search);
                if (colIndex < 0) {
                    filters.add(RowFilter.regexFilter(pat, 0, 1, 2, 3, 4)); 
                } else {
                    filters.add(RowFilter.regexFilter(pat, colIndex));
                }
            }

            if (statusIdx > 0) {
                String wanted = (statusIdx == 1) ? "Active" : "Deactivated";
                filters.add(RowFilter.regexFilter("^" + wanted + "$", 5));   
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));            
            }
        };
        
        statusBox.addActionListener(e -> applyFilter.run());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applyFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                applyFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                applyFilter.run();
            }
        });

        columnBox.addActionListener(e -> applyFilter.run());

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(160, 231, 255));
        searchBtn.addActionListener(e -> applyFilter.run());

        JButton clearBtn = new JButton("Reset");
        clearBtn.setBackground(new Color(248, 152, 152));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            columnBox.setSelectedIndex(0);
            statusBox.setSelectedIndex(1);
            sorter.setRowFilter(null);
            searchField.requestFocus();
        });
        
        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(5, 144, 255));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            mainFrame.showPanel("CreateAdmin");
        });

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showPanel("Manager"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBar.add(backBtn);
        searchBar.add(statusLabel);
        searchBar.add(statusBox);
        searchBar.add(fieldLabel);
        searchBar.add(columnBox);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);
        searchBar.add(addBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        applyFilter.run();   
        return panel;
    }
    
}


