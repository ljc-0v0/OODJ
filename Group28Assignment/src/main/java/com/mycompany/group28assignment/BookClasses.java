/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class BookClasses extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(BookClasses.class.getName());

    private final String studentID;
    private final javax.swing.JFrame parentDashboard;
    private final Path schedulesFile = Paths.get("data", "schedules.txt");
    private final Path bookingsFile = Paths.get("data", "bookings.txt");

   public BookClasses() {
        this("GUEST", null); 
    }

    public BookClasses(String studentID) { 
        this(studentID, null); 
    }
    public BookClasses(String studentID, javax.swing.JFrame parentDashboard) { // [ADDED]
        this.studentID = studentID;
        this.parentDashboard = parentDashboard; 
        initComponents();

        setTitle("Book Classes");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setDefaultEditor(Object.class, null);

        btnSearch.addActionListener(e -> loadClasses());
        btnReset.addActionListener(e -> {
            SearchField.setText("");
            DateField.setText("");
            loadClasses();
        });
        btnBook.addActionListener(e -> bookClass());

        loadClasses();
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
private void loadClasses() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String keyword = SearchField.getText().trim().toLowerCase();
        String dateFilter = DateField.getText().trim();

        try {
            Files.createDirectories(Paths.get("data"));
            if (!Files.exists(schedulesFile)) {
                Files.createFile(schedulesFile);
            }

            Map<String, Integer> bookedCounts = getBookedCounts();
            List<String> lines = Files.readAllLines(schedulesFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\s*\\|\\s*", -1);
                if (parts.length < 7) {
                    continue;
                }

                String classID = parts[0];
                String className = parts[1];
                String discipline = parts[2];
                String date = parts[3];
                String time = parts[4];
                String capacityStr = parts[5];
                String status = parts[6];
String fee = (parts.length >= 8 && !parts[7].isBlank()) ? parts[7] : "30.00";
                if (!status.equalsIgnoreCase("Available")) {
                    continue;
                }
// Only display classes scheduled for today or a future date
try {
    LocalDate classDate = LocalDate.parse(date);

    if (classDate.isBefore(LocalDate.now())) {
        continue;
    }
} catch (Exception e) {
    continue;
}
                if (!keyword.isEmpty()) {
                    boolean matchesKeyword = classID.toLowerCase().contains(keyword)
                            || className.toLowerCase().contains(keyword)
                            || discipline.toLowerCase().contains(keyword);
                    if (!matchesKeyword) {
                        continue;
                    }
                }

                if (!dateFilter.isEmpty() && !date.contains(dateFilter)) {
                    continue;
                }

                int totalCapacity;
                try {
                    totalCapacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                int currentBooked = bookedCounts.getOrDefault(classID, 0);
                int remaining = Math.max(0, totalCapacity - currentBooked);
                String capacityDisplay = remaining + " / " + totalCapacity;

                model.addRow(new Object[]{
                    classID,
                    className,
                    discipline,
                    date,
                    time,
                    capacityDisplay,
                    fee
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot load classes: " + ex.getMessage());
        }
    }

    private Map<String, Integer> getBookedCounts() {
        Map<String, Integer> counts = new HashMap<>();
        if (!Files.exists(bookingsFile)) {
            return counts;
        }

        try {
            List<String> bookings = Files.readAllLines(bookingsFile, StandardCharsets.UTF_8);
            for (String booking : bookings) {
                String[] parts = booking.split("\\s*\\|\\s*", -1);
                if (parts.length >= 6 && parts[5].equalsIgnoreCase("Booked")) {
                    String cID = parts[1];
                    counts.put(cID, counts.getOrDefault(cID, 0) + 1);
                }
            }
        } catch (Exception ignored) {
        }
        return counts;
    }

  private boolean paymentOpen = false;

public void bookClass() {
    if (paymentOpen) {
        JOptionPane.showMessageDialog(this, "Please finish or close the payment window first.");
        return;
    }

    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a class first.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    String classID = model.getValueAt(selectedRow, 0).toString();
    String className = model.getValueAt(selectedRow, 1).toString();
    String instructor = model.getValueAt(selectedRow, 2).toString();
    String date = model.getValueAt(selectedRow, 3).toString();
    String time = model.getValueAt(selectedRow, 4).toString();
    String slotsText = model.getValueAt(selectedRow, 5).toString();

    double fee;
    try {
        fee = Double.parseDouble(model.getValueAt(selectedRow, 6).toString().trim());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid fee for this class.");
        return;
    }

    int remainingSlots;
    try {
        remainingSlots = Integer.parseInt(slotsText.split("/")[0].trim());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid class capacity format.");
        return;
    }
    if (remainingSlots <= 0) {
        JOptionPane.showMessageDialog(this, "Sorry, this class is fully booked.");
        return;
    }

    String conflict = findConflict(classID, date, time);
    if (conflict != null) {
        JOptionPane.showMessageDialog(this, conflict);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirm booking for:\nClass: %s (%s)\nDate: %s at %s\nFee: RM%.2f\n\nProceed to payment?",
                    className, classID, date, time, fee),
            "Confirm Booking", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    paymentOpen = true;
    Payment payment = new Payment(studentID, classID, className, instructor, date, time, fee);
    payment.setOnSuccess(() -> saveBooking(classID, className, date, time));
    payment.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            paymentOpen = false;
        }
    });
    payment.setLocationRelativeTo(null);
    payment.setVisible(true);
}

// 检查: 同一个课重复预约 / 同一时间冲突。没有问题返回 null
private String findConflict(String classID, String date, String time) {
    if (!Files.exists(bookingsFile)) {
        return null;
    }
    try {
        for (String booking : Files.readAllLines(bookingsFile, StandardCharsets.UTF_8)) {
            if (booking.isBlank()) continue;
            String[] p = booking.split("\\s*\\|\\s*", -1);
            if (p.length >= 6 && p[5].equalsIgnoreCase("Booked") && p[0].equals(studentID)) {
                if (p[1].equals(classID)) {
                    return "You have already booked this class.";
                }
                if (p[3].equals(date) && p[4].equals(time)) {
                    return "Time clash! You already booked '" + p[2] + "' on " + date + " at " + time + ".";
                }
            }
        }
    } catch (Exception ignored) { }
    return null;
}

// 付款成功后才调用
private void saveBooking(String classID, String className, String date, String time) {
    if (findConflict(classID, date, time) != null) {
        loadClasses();
        return;   // 保险: 已有记录就不重复写入
    }
    try {
        Files.createDirectories(Paths.get("data"));
        String record = String.join(" | ", studentID, classID, className, date, time, "Booked");
        Files.write(bookingsFile, List.of(record), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Failed to save booking: " + ex.getMessage());
    }
    loadClasses();
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        SearchField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        DateField = new javax.swing.JTextField();
        btnBook = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Search:");

        jLabel2.setText("Date:");

        btnSearch.setText("Search");

        btnReset.setText("Reset");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ClassID", "ClassName", "Instructor", "Date", "Time", "Capacity", "Fee"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnBook.setText("Book");

        btnBack.setText("Back");
        btnBack.addActionListener(this::btnBackActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DateField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnReset)
                        .addGap(25, 25, 25))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(68, 68, 68)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnSearch)
                    .addComponent(btnReset)
                    .addComponent(DateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBook)
                    .addComponent(btnBack)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        goBackToDashboard();
    }//GEN-LAST:event_btnBackActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new BookClasses().setVisible(true));
    }

    // V

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DateField;
    private javax.swing.JTextField SearchField;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

