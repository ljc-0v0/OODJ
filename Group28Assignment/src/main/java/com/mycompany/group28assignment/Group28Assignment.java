package com.mycompany.group28assignment;
import java.awt.*;
import java.util.*;
import java.util.List;  
import java.nio.file.*;
import org.apache.commons.codec.digest.DigestUtils;
import javax.swing.*;


public class Group28Assignment{

    public static void main(String[] args) {
//        User m1 = new User("string","string","string","string","string","Manager","string");
//        m1.storeInFile();
        
        login();
    }
    
    public static void login(){
        JFrame loginFrame = new JFrame("Login");

        JLabel loginTitle = new JLabel("Welcome to ...", SwingConstants.CENTER);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameInput = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordInput = new JPasswordField(15);

        JPanel usernamePanel = new JPanel();
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameInput);
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordInput);

        // add to frame
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        mainPanel.add(loginTitle);
        mainPanel.add(usernamePanel);
        mainPanel.add(passwordPanel);

        JButton loginBtn = new JButton("Login");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(loginBtn);

        loginFrame.add(mainPanel, BorderLayout.CENTER);
        loginFrame.add(buttons, BorderLayout.SOUTH);

        loginFrame.pack();
        loginFrame.setSize(400, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        loginBtn.addActionListener(e -> {
            try {
                String username = usernameInput.getText().trim();
                String password = passwordInput.getText().trim();
                String passwordHash = DigestUtils.md5Hex(password);
                boolean found = false;

                if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                    List<String> lines = Files.readAllLines(Path.of("users.txt"));
                    for (String line : lines) {
                        if (line.isBlank()) {
                            continue;
                        }

                        String[] parts = line.split("\\|", -1);
                        String storedUsername = parts[1];
                        String storedPassword = parts[3];

                        if (storedUsername.equals(username) && storedPassword.equals(passwordHash)) {
                            loginFrame.dispose();
                            accessControl(parts[0],parts[5]);
                            found = true;
                        }
                    }
                    if (found == false){
                        JOptionPane.showMessageDialog(loginFrame,
                                "Invalid Username or Password !! ",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            } finally {
                passwordInput.setText(null); 
            
            }
        }); 
    }
    
    public static void accessControl(String userID, String userRole){
        try{
            final User currentUser;
            switch (userRole){
                case "Manager" -> {
                    currentUser = new Manager(userID);
                }
                case "AttendanceOfficer" -> {
                    currentUser = new AttendanceOfficer(userID);
                }
                default -> {
                    JOptionPane.showMessageDialog(null,
                            "Unknown role: " + userRole,
                            "Access denied", JOptionPane.ERROR_MESSAGE);
                    return;                             
                }
            }
            
            SwingUtilities.invokeLater(() -> new MainFrame(currentUser).setVisible(true));
            
        } catch(Exception ex){
        
        }
    }
}
