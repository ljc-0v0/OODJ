/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Topupframe extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Topupframe.class.getName());

private static final String BALANCE_FILE = "data/databalance.txt";
private static final String PAYMENT_FILE = "data/datapayment.txt"; 
private final String studentID;

public Topupframe(String studentID) {
    this.studentID = studentID;
    initComponents();

    // 关闭窗口时只关这一页，不退出整个程序
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Back 回到学生主页（MainFrame 一直开着）
    btnBack.addActionListener(e -> dispose());

    // Refresh 刷新余额和记录
    btnRefresh.addActionListener(e -> {
        refreshBalanceDisplay();
        loadTransactionHistory();
    });

    initTopUpPage();
}
private void initTopUpPage() {
    if (txtBalance != null) {
        txtBalance.setEditable(false);
    }
    refreshBalanceDisplay();
    loadTransactionHistory();
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
        logger.log(java.util.logging.Level.WARNING, "Failed to read balance", e);
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
    

private void refreshBalanceDisplay() {
    if (txtBalance != null) {
        txtBalance.setText(String.format(Locale.US, "RM%.2f", getCurrentBalance()));
    }
}

private void handleTopUpEWallet(){
    String input = JOptionPane.showInputDialog(this,
            "Enter Top Up Amount (RM):", "Top Up", JOptionPane.PLAIN_MESSAGE);
    if (input == null) {
        return;
    }

    double amount;
    try {
        amount = Double.parseDouble(input.trim());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        return;
    }

    if (!(amount > 0) || Double.isInfinite(amount)) {
        JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
        return;
    }

    double newBalance = getCurrentBalance() + amount;
    saveBalance(newBalance);
    recordTopUpTransaction(amount);

    refreshBalanceDisplay();
    loadTransactionHistory();

    JOptionPane.showMessageDialog(this, String.format(Locale.US,
            "Top up successful!\nAmount: RM%.2f\nCurrent Balance: RM%.2f", amount, newBalance));
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
            logger.log(java.util.logging.Level.WARNING, "Failed to read record IDs", e);
        }
    }
    return String.format("T%03d", max + 1);
}

private void recordTopUpTransaction(double amount) {
    try {
        File file = new File(PAYMENT_FILE);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        String id = generateNextRecordID();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String line = String.format(Locale.US, "%s|%s|%s|+RM%.2f|Top Up E-Wallet",
                id, studentID, now, amount);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to save transaction record.");
    }
}

private void loadTransactionHistory() {
    DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
    model.setRowCount(0);

    File file = new File(PAYMENT_FILE);
    if (!file.exists()) {
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
    if (line.trim().isEmpty()) continue;
    String[] parts = line.split("\\|");
    if (parts.length >= 5 && parts[1].trim().equals(studentID)) {
        model.addRow(new Object[]{
            parts[0].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()
        });
    }
}
    } catch (IOException e) {
        logger.log(java.util.logging.Level.WARNING, "Failed to load transaction history", e);
    }
}



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   
    
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        refreshBalanceDisplay();
        loadTransactionHistory();
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTopUp = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHistory = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnTopUpEWallet = new javax.swing.JButton();
        lblBalance = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTopUp.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTopUp.setText("TOP UP");

        tblHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "RecordID", "Date/Time", "CashIn/Out", "Description"
            }
        ));
        jScrollPane1.setViewportView(tblHistory);

        btnRefresh.setText("Refresh");

        btnBack.setText("Back");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Transaction History ");

        btnTopUpEWallet.setText("Top Up E-Wallet  ");
        btnTopUpEWallet.addActionListener(this::btnTopUpEWalletActionPerformed);

        lblBalance.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBalance.setText("Balance:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(lblBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTopUp, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addComponent(btnTopUpEWallet, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(234, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(lblTopUp, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBalance))
                .addGap(16, 16, 16)
                .addComponent(btnTopUpEWallet, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnBack))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTopUpEWalletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopUpEWalletActionPerformed
        // TODO add your handling code here:
        handleTopUpEWallet();
    }//GEN-LAST:event_btnTopUpEWalletActionPerformed

    /**
     * @param args the command line arguments
     */
   
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

       java.awt.EventQueue.invokeLater(() -> new Topupframe("S00001").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTopUpEWallet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblTopUp;
    private javax.swing.JTable tblHistory;
    private javax.swing.JTextField txtBalance;
    // End of variables declaration//GEN-END:variables
}
