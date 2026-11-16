
package com.mycompany.group28assignment;

import java.awt.*;
import java.nio.file.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.codec.digest.DigestUtils;

public class Group28Assignment {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> login());
    }

    public static void login() {

        JFrame loginFrame = new JFrame("Login");

        JLabel loginTitle = new JLabel(
                "Welcome to ...", SwingConstants.CENTER);

        loginTitle.setFont(
                new Font("SansSerif", Font.BOLD, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameInput = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordInput =
                new JPasswordField(15);

        JPanel usernamePanel = new JPanel();
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameInput);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordInput);

        JPanel mainPanel =
                new JPanel(new GridLayout(3, 1, 8, 8));

        mainPanel.add(loginTitle);
        mainPanel.add(usernamePanel);
        mainPanel.add(passwordPanel);

        JButton loginBtn = new JButton("Login");

        JPanel buttons =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttons.add(loginBtn);

        loginFrame.add(mainPanel, BorderLayout.CENTER);
        loginFrame.add(buttons, BorderLayout.SOUTH);

        loginFrame.setSize(400, 250);
        loginFrame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        loginBtn.addActionListener(e -> {

            try {
                String username =
                        usernameInput.getText().trim();

                String password =
                        new String(passwordInput.getPassword());

                if (username.isEmpty()
                        || password.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            loginFrame,
                            "Please enter username and password.",
                            "Login",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String passwordHash =
                        DigestUtils.md5Hex(password);

                Path usersFile = Path.of("users.txt");

                System.out.println(
                        "DEBUG USERS FILE: "
                        + usersFile.toAbsolutePath());

                if (!Files.exists(usersFile)) {

                    JOptionPane.showMessageDialog(
                            loginFrame,
                            "users.txt file not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<String> lines =
                        Files.readAllLines(usersFile);

                boolean found = false;

                for (String line : lines) {

                    if (line.isBlank()) {
                        continue;
                    }

                    String[] parts =
                            line.split("\\|", -1);

                    if (parts.length < 7) {
                        continue;
                    }

                    String storedUsername =
                            parts[1].trim();

                    String storedPassword =
                            parts[3].trim();

                    if (storedUsername.equals(username)
                            && storedPassword.equals(passwordHash)) {

                        String userID = parts[0].trim();
                        String userRole = parts[5].trim();

                        System.out.println(
                                "LOGIN DEBUG: ID="
                                + userID
                                + ", ROLE=["
                                + userRole
                                + "], FILE="
                                + usersFile.toAbsolutePath());

                        found = true;

                        if (accessControl(userID, userRole)) {
                            loginFrame.dispose();
                        }

                        break;
                    }
                }

                if (!found) {

                    JOptionPane.showMessageDialog(
                            loginFrame,
                            "Invalid Username or Password!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        loginFrame,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

            } finally {
                passwordInput.setText("");
            }
        });
    }

    public static boolean accessControl(
            String userID, String userRole) {

        try {
            final User currentUser;

            String role = userRole == null
                    ? "" : userRole.trim();

            System.out.println(
                    "ACCESS CONTROL DEBUG: ID="
                    + userID
                    + ", ROLE=["
                    + role
                    + "]");

            switch (role) {

                case "Manager" -> {
                    currentUser = new Manager(userID);
                }

                case "Instructor" -> {
                    currentUser = new Instructor(userID);
                }

                case "AttendanceOfficer" -> {
                    currentUser =
                            new AttendanceOfficer(userID);
                }

                default -> {

                    JOptionPane.showMessageDialog(
                            null,
                            "Unknown role: [" + role + "]",
                            "Access denied",
                            JOptionPane.ERROR_MESSAGE);

                    return false;
                }
            }

            System.out.println(
                    "CREATED OBJECT: "
                    + currentUser.getClass().getSimpleName());

            new MainFrame(currentUser).setVisible(true);

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Access Control Error: "
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }
    }
}
