package com.mycompany.group28assignment;
import java.awt.*;
import java.nio.file.*;
import java.util.List;
import javax.swing.*;


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

        roleInput.setEditable(false);
        createdByInput.setEditable(false);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
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

        JButton submitBtn = new JButton("Create");
        JButton backBtn = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(submitBtn);
        buttons.add(backBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> handleSubmit(
                mainFrame, usernameInput, passwordInput,
                emailInput, phoneInput));
        backBtn.addActionListener(e -> mainFrame.showPanel("Manager"));

        return panel;
    }

    /**
     * Private — the submit logic, separated from UI construction.
     */
    private void handleSubmit(MainFrame mainFrame,
            JTextField usernameInput,
            JPasswordField passwordInput,
            JTextField emailInput,
            JTextField phoneInput) {

        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());
        String email = emailInput.getText().trim();
        String phoneNo = phoneInput.getText().trim();
        String role = "Manager";
        String createdBy = this.userName;

        if (username.isEmpty()) {
            warn(mainFrame, "Username is required.", usernameInput);
            return;
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";
        if (!password.matches(passwordRegex)) {
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
            User newUser = new User(username, email, password, phoneNo, role, createdBy);
            newUser.storeInFile("Manager");

            JOptionPane.showMessageDialog(mainFrame,
                    "Manager created: " + username,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            mainFrame.showPanel("Manager");

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
    
}


