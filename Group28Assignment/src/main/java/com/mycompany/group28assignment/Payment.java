/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.group28assignment;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;

public class Payment extends JFrame {

    private static final String BALANCE_FILE = "data/databalance.txt";
    private static final String PAYMENT_FILE = "data/datapayment.txt";
  private final double fee;

    private final String studentID;
    private final String bookingID;

    private JTextField txtclass, txtinstructor, txtdatetime, txtfee;
    private JTextField txtBalancebefore, txtBalanceafter;
    private JButton btnPay;

    public Payment() {
    this("S00001", "BK000", "Yoga", "Coach Ali", "2026-11-25", "10:00 AM", 30.00);
}
   public Payment(String studentID, String bookingID, String className,
               String instructor, String date, String time) {
    this(studentID, bookingID, className, instructor, date, time, 30.00);
}

public Payment(String studentID, String bookingID, String className,
               String instructor, String date, String time, double fee) {
    this.studentID = studentID;
    this.bookingID = bookingID;
    this.fee = fee;
    initComponents();

    txtclass.setText(className);
    txtinstructor.setText(instructor);
    txtdatetime.setText(date + " " + time);
    txtfee.setText(String.format(Locale.US, "RM%.2f", fee));
    refreshBalanceFields();
}
private Runnable onSuccess;

public void setOnSuccess(Runnable onSuccess) {
    this.onSuccess = onSuccess;
}
    private void initComponents() {
        setTitle("Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Payments", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        txtclass = new JTextField(15);
        txtinstructor = new JTextField(15);
        txtdatetime = new JTextField(15);
        txtfee = new JTextField(15);
        txtBalancebefore = new JTextField(15);
        txtBalanceafter = new JTextField(15);
        for (JTextField t : new JTextField[]{txtclass, txtinstructor, txtdatetime,
                txtfee, txtBalancebefore, txtBalanceafter}) {
            t.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        form.add(new JLabel("Class:"));            form.add(txtclass);
        form.add(new JLabel("Instructor:"));       form.add(txtinstructor);
        form.add(new JLabel("Date/Time:"));        form.add(txtdatetime);
        form.add(new JLabel("Fee:"));              form.add(txtfee);
        form.add(new JLabel("Balance (before):")); form.add(txtBalancebefore);
        form.add(new JLabel("Balance (after):"));  form.add(txtBalanceafter);
        add(form, BorderLayout.CENTER);

        JLabel note = new JLabel("Note: No refund after payment.", SwingConstants.CENTER);
        btnPay = new JButton("PayNow");
        btnPay.addActionListener(e -> payNow());
        JPanel bottom = new JPanel(new GridLayout(0, 1, 5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 60, 10, 60));
        bottom.add(note);
        bottom.add(btnPay);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void refreshBalanceFields() {
        double before = getCurrentBalance();
        txtBalancebefore.setText(String.format(Locale.US, "RM%.2f", before));
        txtBalanceafter.setText(String.format(Locale.US, "RM%.2f", before - this.fee));
    }

    private void payNow() {
        double balance = getCurrentBalance();

        if (balance < fee) {
            int open = JOptionPane.showConfirmDialog(this,
                    String.format(Locale.US, "Insufficient balance (RM%.2f).\nOpen Top Up page now?", balance),
                    "Insufficient balance", JOptionPane.YES_NO_OPTION);
            if (open == JOptionPane.YES_OPTION) {
                Topupframe page = new Topupframe(studentID);
                page.setLocationRelativeTo(null);
                page.setVisible(true);
            }
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format(Locale.US, "Confirm payment of RM%.2f?", fee),
                "Payment Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        saveBalance(balance - this.fee);
        recordPayment();
        refreshBalanceFields();
        
        if (onSuccess != null) {
    onSuccess.run();
}

        JOptionPane.showMessageDialog(this, "Payment Successful!");
        dispose();
    }

    private double getCurrentBalance() {
        File file = new File(BALANCE_FILE);
        if (!file.exists()) {
            return 0.0;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length >= 2 && p[0].trim().equals(studentID)) {
                    return Double.parseDouble(p[1].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void saveBalance(double balance) {
        try {
            File file = new File(BALANCE_FILE);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            String newLine = studentID + "|" + String.format(Locale.US, "%.2f", Math.max(0.0, balance));
            List<String> lines = new ArrayList<>();
            boolean found = false;
            if (file.exists()) {
                for (String line : Files.readAllLines(file.toPath())) {
                    if (line.isBlank()) continue;
                    if (line.split("\\|")[0].trim().equals(studentID)) {
                        lines.add(newLine);
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
            if (!found) {
                lines.add(newLine);
            }
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save balance.");
        }
    }

    private String generateNextRecordID() {
        int max = 0;
        File file = new File(PAYMENT_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String id = line.split("\\|")[0].trim();
                    if (id.matches("T\\d+")) {
                        max = Math.max(max, Integer.parseInt(id.substring(1)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return String.format("T%03d", max + 1);
    }

    private void recordPayment() {
        try {
            File file = new File(PAYMENT_FILE);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String line = String.format(Locale.US, "%s|%s|%s|-RM%.2f|Payment %s (%s)",
                    generateNextRecordID(), studentID, now, fee,
                    txtclass.getText().trim(), bookingID);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save payment record.");
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Payment().setVisible(true));
    }
}