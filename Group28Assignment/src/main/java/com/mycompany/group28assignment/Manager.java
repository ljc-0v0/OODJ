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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Manager extends User {

    private MainFrame mainFrame;

    public Manager(String userID) {
        super(userID);
    }

    // ---------- Refresh button helper ----------
    private JButton makeRefreshButton(Runnable onRefresh) {
        JButton btn = new JButton("Refresh");
        btn.setBackground(new Color(180, 240, 200));
        btn.addActionListener(e -> onRefresh.run());
        return btn;
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

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("Username:"));
        form.add(usernameInput);
        form.add(new JLabel("Password:"));
        form.add(passwordInput);
        form.add(Box.createVerticalStrut(10));
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordInput.setEchoChar((char) 0);
            } else {
                passwordInput.setEchoChar('•');
            }
        });
        form.add(showPassword);
        form.add(new JLabel("Email:"));
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        form.add(phoneInput);
        form.add(new JLabel("Modified By:"));
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
                mainFrame, null, usernameInput, passwordInput,
                emailInput, roleInput, phoneInput, statusBox));
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
        form.add(new JLabel("Modified By:"));
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
                emailInput, roleInput, phoneInput, statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewManager"));

        return panel;
    }

    private String loadEditUser(String uid) {
        Path path = Path.of("data/users.txt");
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
        String createdBy = this.userID;
        Status sel = (Status) statusInput.getSelectedItem();
        boolean status = sel != null && sel.getValue();

        if (username.isEmpty()) {
            warn(mainFrame, "Username is required.", usernameInput);
            return;
        } else if (!username.isEmpty() && uid == null) {
            try {
                List<String> lines = Files.readAllLines(Path.of("data/users.txt"));
                for (String line : lines) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] parts = line.split("\\|");
                    if (parts[1].equalsIgnoreCase(username)) {
                        warn(mainFrame,
                                "username '" + username + "' already exsits!!",
                                usernameInput);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Read Failed: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

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
                    List<String> lines = Files.readAllLines(Path.of("data/users.txt"));
                    for (String line : lines) {
                        if (line.isBlank()) {
                            continue;
                        }
                        String[] parts = line.split("\\|");
                        if (parts[0].equalsIgnoreCase(uid)) {
                            User editUser = new User(uid);
                            editUser.editInFile(username, email, phoneNo, createdBy, status);

                            JOptionPane.showMessageDialog(mainFrame,
                                    role + " updated: " + username,
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Read failed: " + e.getMessage());
                }
            } else {
                User newUser = new User(username, email, password, phoneNo, role, createdBy, status);
                newUser.storeInFile(role);

                JOptionPane.showMessageDialog(mainFrame,
                        role + " created: " + username,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (role.equals("Manager")) {
                mainFrame.refresh("ViewManager", () -> loadManagerPanel(mainFrame));
            } else if (role.equals("Admin")) {
                mainFrame.refresh("ViewAdmin", () -> loadAdminPanel(mainFrame));
            }

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

    // ============================================================
    // MANAGER TABLE
    // ============================================================
    public JPanel loadManagerPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Phone No.", "Modified By", "Status"};
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
        String[] fields = {"All", "ID", "Name", "Email", "Phone No.", "Modified By"};
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

        JButton refreshBtn = makeRefreshButton(()
                -> mainFrame.refresh("ViewManager", () -> loadManagerPanel(mainFrame)));

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
        searchBar.add(refreshBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        applyFilter.run();
        return panel;
    }

    // ============================================================
    // SHARED LOADER (users.txt filtered by role)
    // ============================================================
    private Object[][] loadUser(String role) {
        List<Object[]> rows = new ArrayList<>();
        Path file = Path.of("data/users.txt");
        StringTokenizer st = new StringTokenizer(role, ",");
        List<String> roles = new ArrayList<>();
        while (st.hasMoreTokens()) {
            roles.add(st.nextToken());
        }

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
                if (!roles.contains(p[5])) {
                    continue;
                }
                if (roles.size() > 1) {
                    User u1 = new User(p[6]);
                    String createdByName = u1.getUserName();
                    rows.add(new Object[]{
                        p[0],
                        p[1],
                        p[2],
                        p[4],
                        createdByName,
                        p[5],
                        p[7].equals("true") ? "Active" : "Deactivated"
                    });
                    u1 = null;

                } else {
                    User u1 = new User(p[6]);
                    String createdByName = u1.getUserName();
                    rows.add(new Object[]{
                        p[0],
                        p[1],
                        p[2],
                        p[4],
                        createdByName,
                        p[7].equals("true") ? "Active" : "Deactivated"
                    });
                    u1 = null;
                }

            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return rows.toArray(new Object[0][]);
    }

    // ============================================================
    // CREATE ADMIN
    // ============================================================
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
        form.add(Box.createVerticalStrut(10));
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordInput.setEchoChar((char) 0);
            } else {
                passwordInput.setEchoChar('•');
            }
        });
        form.add(showPassword);
        form.add(new JLabel("Email:"));
        form.add(emailInput);
        form.add(new JLabel("Role:"));
        form.add(roleInput);
        form.add(new JLabel("Phone No:"));
        form.add(phoneInput);
        form.add(new JLabel("Modified By:"));
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
                mainFrame, null, usernameInput, passwordInput,
                emailInput, roleInput, phoneInput, statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewAdmin"));

        return panel;
    }

    // ============================================================
    // EDIT ADMIN
    // ============================================================
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
        form.add(new JLabel("Modified By:"));
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
                emailInput, roleInput, phoneInput, statusBox));
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewAdmin"));

        return panel;
    }

    // ============================================================
    // ADMIN TABLE
    // ============================================================
    public JPanel loadAdminPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Phone No.", "Modified By", "Status"};
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
        String[] fields = {"All", "ID", "Name", "Email", "Phone No.", "Modified By"};
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

        JButton refreshBtn = makeRefreshButton(()
                -> mainFrame.refresh("ViewAdmin", () -> loadAdminPanel(mainFrame)));

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
        searchBar.add(refreshBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        applyFilter.run();
        return panel;
    }

    // ============================================================
    // STAFF TABLE
    // ============================================================
    public JPanel loadStaffPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Email", "Phone No.", "Modified By", "Role", "Status"};
        Object[][] data = loadUser("Instructor,Attendance Officer,Facility Officer");
        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    boolean active = "Active".equals(
                            getModel().getValueAt(convertRowIndexToModel(row), 6));
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

        JTextField searchField = new JTextField(30);
        String[] fields = {"All", "ID", "Name", "Email", "Phone No.", "Modified By"};
        JLabel fieldLabel = new JLabel("Fillter By:");
        JComboBox<String> columnBox = new JComboBox<>(fields);
        String[] statusFields = {"All", "Active", "Deactivated"};
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(statusFields);
        String[] roleFields = {"All", "Instructor", "Attendance Officer", "Facility Officer"};
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleBox = new JComboBox<>(roleFields);

        statusBox.setSelectedIndex(1);

        Runnable applyFilter = () -> {
            String search = searchField.getText().trim();
            int colIndex = columnBox.getSelectedIndex() - 1;
            int statusIdx = statusBox.getSelectedIndex();
            int roleIdx = roleBox.getSelectedIndex();

            java.util.List<RowFilter<TableModel, Integer>> filters = new java.util.ArrayList<>();
            if (!search.isEmpty()) {
                String pat = "(?i)" + java.util.regex.Pattern.quote(search);
                if (colIndex < 0) {
                    filters.add(RowFilter.regexFilter(pat, 0, 1, 2, 3, 4));
                } else {
                    filters.add(RowFilter.regexFilter(pat, colIndex));
                }
            }

            if (roleIdx > 0) {
                String wanted = (String) roleBox.getSelectedItem();
                filters.add(RowFilter.regexFilter("^" + wanted + "$", 5));
            }

            if (statusIdx > 0) {
                String wanted = (statusIdx == 1) ? "Active" : "Deactivated";
                filters.add(RowFilter.regexFilter("^" + wanted + "$", 6));
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        statusBox.addActionListener(e -> applyFilter.run());
        roleBox.addActionListener(e -> applyFilter.run());

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
            roleBox.setSelectedIndex(0);
            sorter.setRowFilter(null);
            searchField.requestFocus();
        });

        JButton refreshBtn = makeRefreshButton(()
                -> mainFrame.refresh("ViewStaff", () -> loadStaffPanel(mainFrame)));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showPanel("Manager"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBar.add(backBtn);
        searchBar.add(statusLabel);
        searchBar.add(statusBox);
        searchBar.add(roleLabel);
        searchBar.add(roleBox);
        searchBar.add(fieldLabel);
        searchBar.add(columnBox);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);
        searchBar.add(refreshBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        applyFilter.run();
        return panel;
    }

    // ============================================================
    // INSTRUCTOR TABLE
    // ============================================================
    public JPanel loadInstructorPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "Average Rate", "Total Class"};
        Object[][] data = loadInstructor();
        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {

                    Object rateValue = getModel().getValueAt(convertRowIndexToModel(row), 2);
                    double rate = 0.0;
                    try {
                        rate = Double.parseDouble(rateValue.toString().trim());
                    } catch (NumberFormatException | NullPointerException ex) {
                        rate = 0.0;
                    }

                    if (rate < 2.0) {
                        c.setBackground(new Color(255, 225, 225));
                    } else {
                        c.setBackground(new Color(220, 255, 220));
                    }
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = table.rowAtPoint(e.getPoint());
                    if (viewRow < 0) {
                        return;
                    }

                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String instructorUID = (String) table.getModel().getValueAt(modelRow, 0);

                    mainFrame.showInstructorDetailPanel(instructorUID);
                }
            }
        });
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        JTextField searchField = new JTextField(30);
        String[] fields = {"All", "ID", "Name"};
        JLabel fieldLabel = new JLabel("Fillter By:");
        JComboBox<String> columnBox = new JComboBox<>(fields);

        Runnable applyFilter = () -> {
            String search = searchField.getText().trim();
            int colIndex = columnBox.getSelectedIndex() - 1;

            java.util.List<RowFilter<TableModel, Integer>> filters = new java.util.ArrayList<>();
            if (!search.isEmpty()) {
                String pat = "(?i)" + java.util.regex.Pattern.quote(search);
                if (colIndex < 0) {
                    filters.add(RowFilter.regexFilter(pat, 0, 1, 2));
                } else {
                    filters.add(RowFilter.regexFilter(pat, colIndex));
                }
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

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
            sorter.setRowFilter(null);
            searchField.requestFocus();
        });

        JButton refreshBtn = makeRefreshButton(()
                -> mainFrame.refresh("ViewInstructor", () -> loadInstructorPanel(mainFrame)));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showPanel("Manager"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBar.add(backBtn);
        searchBar.add(fieldLabel);
        searchBar.add(columnBox);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);
        searchBar.add(refreshBtn);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        applyFilter.run();
        return panel;
    }

    // ============================================================
    // INSTRUCTOR DATA LOADER
    // ============================================================
    private Object[][] loadInstructor() {
        List<Object[]> rows = new ArrayList<>();

        Path usersFile = Path.of("data/users.txt");
        Path ratingsFile = Path.of("data/ratings.txt");
        Path classesFile = Path.of("data/schedules.txt");

        Map<String, String> instructors = new LinkedHashMap<>();
        if (Files.exists(usersFile)) {
            try {
                for (String line : Files.readAllLines(usersFile)) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] p = line.split("\\|", -1);
                    if (p.length < 8) {
                        continue;
                    }
                    if (!"Instructor".equals(p[5].trim())) {
                        continue;
                    }
                    instructors.put(p[0].trim(), p[1].trim());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error reading users: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        Map<String, double[]> ratingSum = new HashMap<>();
        if (Files.exists(ratingsFile)) {
            try {
                for (String line : Files.readAllLines(ratingsFile)) {
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] p = line.split("\\|", -1);
                    if (p.length < 5) {
                        continue;
                    }
                    String instructorUID = p[3].trim();
                    if (!instructors.containsKey(instructorUID)) {
                        continue;
                    }

                    try {
                        double rating = Double.parseDouble(p[4].trim());
                        double[] acc = ratingSum.computeIfAbsent(
                                instructorUID, k -> new double[2]);
                        acc[0] += rating;
                        acc[1] += 1;
                    } catch (NumberFormatException ignored) {
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error reading ratings: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        Map<String, Integer> classCount = new HashMap<>();
        LocalDate today = LocalDate.now();

        if (Files.exists(classesFile)) {
            try {
                for (String line : Files.readAllLines(classesFile)) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] p = line.split("\\|", -1);
                    if (p.length < 10) {
                        continue;
                    }

                    String status = p[6].trim();
                    String dateStr = p[3].trim();
                    String instructorUID = p[9].trim();

                    if (!"Available".equalsIgnoreCase(status)) {
                        continue;
                    }
                    if (!instructors.containsKey(instructorUID)) {
                        continue;
                    }

                    try {
                        LocalDate classDate = LocalDate.parse(dateStr);
                        if (!classDate.isAfter(today)) {
                            classCount.merge(instructorUID, 1, Integer::sum);
                        }
                    } catch (DateTimeParseException ignored) {
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error reading classes: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        for (Map.Entry<String, String> e : instructors.entrySet()) {
            String uid = e.getKey();
            String name = e.getValue();

            double[] acc = ratingSum.get(uid);
            double avg = (acc != null && acc[1] > 0) ? acc[0] / acc[1] : 0.0;

            int totalClasses = classCount.getOrDefault(uid, 0);

            rows.add(new Object[]{
                uid,
                name,
                String.format("%.1f", avg),
                totalClasses
            });
        }

        return rows.toArray(new Object[0][]);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String getInstructorName(String uid) {
        Path usersFile = Path.of("data/users.txt");
        if (!Files.exists(usersFile)) {
            return uid;
        }
        try {
            for (String line : Files.readAllLines(usersFile)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split("\\|", -1);
                if (p.length >= 2 && p[0].trim().equals(uid)) {
                    return p[1].trim();
                }
            }
        } catch (IOException ignored) {
        }
        return uid;
    }

    public JPanel instructorDetailPanel(MainFrame mainFrame, String instructorUID) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        String instructorName = getInstructorName(instructorUID);
        JLabel title = new JLabel("Ratings & Comments — " + instructorName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        List<Object[]> rows = new ArrayList<>();
        double sum = 0;
        int count = 0;

        Path ratingsFile = Path.of("data/ratings.txt");
        if (Files.exists(ratingsFile)) {
            try {
                List<String> lines = Files.readAllLines(ratingsFile);

                // Iterate from LAST to FIRST so the newest record is added first
                for (int i = lines.size() - 1; i >= 0; i--) {
                    String line = lines.get(i);
                    if (line.isBlank()) {
                        continue;
                    }
                    String[] p = line.split("\\|", -1);
                    if (p.length < 6) {
                        continue;
                    }

                    String instructor = p[3].trim();
                    if (!instructor.equals(instructorUID)) {
                        continue;
                    }

                    String studentUID = p[1].trim();
                    String bookingID = p[2].trim();
                    String ratingStr = p[4].trim();
                    String comment = p[5].trim();

                    int rating = 0;
                    try {
                        rating = Integer.parseInt(ratingStr);
                    } catch (NumberFormatException ignored) {
                    }
                    sum += rating;
                    count++;

                    // Store the numeric rating separately so sorting by "Rating" works correctly
                    rows.add(new Object[]{studentUID, bookingID, rating, comment});
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error reading ratings: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        double avg = count > 0 ? sum / count : 0.0;
        JLabel summary = new JLabel(String.format(
                "Average Rating: %.1f / 5   (%d rating%s)",
                avg, count, count == 1 ? "" : "s"));
        summary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summary.setForeground(new Color(108, 117, 125));

        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(summary, BorderLayout.SOUTH);

        String[] columns = {"Student UID", "Booking ID", "Rating", "Comment"};

        // Custom model so "Rating" column is numeric (sortable correctly) and displayed as "x / 5"
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2) {
                    return Integer.class;   // sort "Rating" as numbers
                }
                return String.class;
            }
        };
        for (Object[] r : rows) {
            model.addRow(r);
        }

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);

                int modelRow = convertRowIndexToModel(row);
                int rating = (Integer) getModel().getValueAt(modelRow, 2);

                if (!isRowSelected(row)) {
                    if (rating <= 2) {
                        c.setBackground(new Color(255, 200, 200));  
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (value != null) {
                    setText(value + " / 5");
                }
                return this;
            }
        });

        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(3).setPreferredWidth(400);

        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = makeRefreshButton(()
                -> mainFrame.refresh("InstructorDetail",
                        () -> instructorDetailPanel(mainFrame, instructorUID)));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showPanel("ViewInstructor"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshBtn);
        bottom.add(backBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

}
