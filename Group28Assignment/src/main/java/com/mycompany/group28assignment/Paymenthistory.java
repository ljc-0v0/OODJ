/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Paymenthistory extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Paymenthistory.class.getName());

    private final String studentID;
    private final javax.swing.JFrame parentDashboard;
    private final List<String[]> allRecords = new ArrayList<>();
    private DefaultTableModel tableModel;

    public Paymenthistory() {
        this(null, null);
    }

    public Paymenthistory(String studentID, javax.swing.JFrame parentDashboard) {
        this.studentID = studentID;
        this.parentDashboard = parentDashboard;
        initComponents();

        setTitle("Payment History");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        if (txtTotalPaid != null) {
            txtTotalPaid.setEditable(false);
        }

        setupTableAndEvents();
        loadRecordsFromFile();
        
    }

    private void setupTableAndEvents() {
        String[] columns = {"PaymentID", "BookingID", "Class", "Date", "Method", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblpaymenthistory.setModel(tableModel);
        tblpaymenthistory.getTableHeader().setReorderingAllowed(false);

        if (btnRefresh != null) {
            btnRefresh.addActionListener(e -> refreshPaymentHistory());
        }
        btnBack.addActionListener(e -> goBackToDashboard());
    }
    

    private void goBackToDashboard() {
        dispose();
        if (parentDashboard != null) {
            parentDashboard.setLocationRelativeTo(null);
            parentDashboard.setVisible(true);
        } else if (studentID != null && !studentID.isBlank() && !"GUEST".equals(studentID)) {
            try {
                Student student = new Student(studentID);
                mainStudent dashboard = new mainStudent(student);
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unable to return to dashboard: " + ex.getMessage());
            }
        }
    }

private void loadRecordsFromFile() {
        allRecords.clear();
        File file = new File("data/datapayment.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s*\\|\\s*", -1);
                if (parts.length >= 6) {
                    allRecords.add(new String[]{
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim()
                    });
                } else if (parts.length >= 4) {
                    // 兼容少于 6 列的记录，避免完全空白
                    allRecords.add(new String[]{
                        parts[0].trim(),
                        "-",
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts.length >= 5 ? parts[4].trim() : "0.00"
                    });
                }
            }
        } catch (IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to read data/datapayment.txt", ex);
            JOptionPane.showMessageDialog(this, "Unable to load payment history.");
        }
    }
    
private void refreshPaymentHistory() {
    loadRecordsFromFile();

    if (tableModel == null) {
        return;
    }

    tableModel.setRowCount(0);
    double total = 0.0;

    for (String[] row : allRecords) {
        tableModel.addRow(row);
        String amountCell = findAmountCell(row);
        // 若只想算消费支出（负号金额），可改成：if (amountCell.startsWith("-"))
        total += parseAmount(amountCell);
    }

    if (txtTotalPaid != null) {
        txtTotalPaid.setEditable(false);
        txtTotalPaid.setText(String.format("RM%.2f", total));
    }
}

private String findAmountCell(String[] row) {
    if (row == null) {
        return "0.00";
    }
    for (int i = row.length - 1; i >= 0; i--) {
        if (row[i] != null && row[i].toUpperCase().contains("RM")) {
            return row[i];
        }
    }
    return row.length > 5 && row[5] != null ? row[5] : "0.00";
}
    private double parseAmount(String amountText) {
        if (amountText == null) {
            return 0.0;
        }
        String cleaned = amountText.replace("RM", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private void showSelectedReceipt() {
        int selectedRow = tblpaymenthistory.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment row first.");
            return;
        }

        String receipt = "========== PAYMENT RECEIPT ==========\n"
                + "Payment ID : " + tableModel.getValueAt(selectedRow, 0) + "\n"
                + "Booking ID : " + tableModel.getValueAt(selectedRow, 1) + "\n"
                + "Class      : " + tableModel.getValueAt(selectedRow, 2) + "\n"
                + "Date/Time  : " + tableModel.getValueAt(selectedRow, 3) + "\n"
                + "Method     : " + tableModel.getValueAt(selectedRow, 4) + "\n"
                + "Amount     : " + tableModel.getValueAt(selectedRow, 5) + "\n"
                + "Status     : Paid\n"
                + "====================================";

        JOptionPane.showMessageDialog(
                this,
                receipt,
                "Payment Receipt - " + tableModel.getValueAt(selectedRow, 0),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPaymenthistory = new javax.swing.JLabel();
        btnViewReceipt = new javax.swing.JButton();
        lbltotalpaid = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblpaymenthistory = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        txtTotalPaid = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblPaymenthistory.setText("Payment  history");

        btnViewReceipt.setText("View Receipt ");
        btnViewReceipt.addActionListener(this::btnViewReceiptActionPerformed);

        lbltotalpaid.setText("Total paid:");

        tblpaymenthistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PaymentID", "BookingID", "Class", "Date", "Method", "Amount"
            }
        ));
        jScrollPane1.setViewportView(tblpaymenthistory);

        btnBack.setText("Back");

        btnRefresh.setText("Refresh");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(lbltotalpaid, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(btnViewReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPaymenthistory, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRefresh)
                                .addGap(13, 13, 13)))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPaymenthistory)
                    .addComponent(btnRefresh))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltotalpaid)
                    .addComponent(txtTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnViewReceipt)
                    .addComponent(btnBack))
                .addGap(37, 37, 37))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnViewReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReceiptActionPerformed
        // TODO add your handling code here:
        showSelectedReceipt();
    }//GEN-LAST:event_btnViewReceiptActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new Paymenthistory().setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewReceipt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPaymenthistory;
    private javax.swing.JLabel lbltotalpaid;
    private javax.swing.JTable tblpaymenthistory;
    private javax.swing.JTextField txtTotalPaid;
    // End of variables declaration//GEN-END:variables
}
