/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


public class MyBookings extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(MyBookings.class.getName());

    private final String studentID;
    private final javax.swing.JFrame parentDashboard;
    private final Path bookingsFile = Paths.get("data", "bookings.txt");
    private final Path schedulesFile = Paths.get("data", "schedules.txt");

    /**
     * Creates new form MyBookings
     */
    public MyBookings() {
        this("GUEST", null);
    }

    public MyBookings(String studentID) {
        this(studentID, null);
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
public MyBookings(String studentID, javax.swing.JFrame parentDashboard) {
        this.studentID = studentID;
        this.parentDashboard = parentDashboard;
        initComponents();

        setTitle("My Bookings");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (txtTotalBookings != null) {
            txtTotalBookings.setEditable(false);
        }

        bookingtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingtable.setDefaultEditor(Object.class, null);

        btnsearch.addActionListener(e -> loadBookings());
        btnreset.addActionListener(e -> {
            txtSearch.setText("");
            loadBookings();
        });
        if (btnRefresh != null) {
            btnRefresh.addActionListener(e -> loadBookings());
        }
        btnCancel.addActionListener(e -> {
            cancelSelectedBooking();
            loadBookings();
        });
        btnBack.addActionListener(e -> goBackToDashboard());

        loadBookings();
    }

    private java.util.Map<String, String> loadInstructorMap() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (!Files.exists(schedulesFile)) {
            return map;
        }
        try {
            for (String line : Files.readAllLines(schedulesFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] s = line.split("\\s*\\|\\s*", -1);
                if (s.length >= 3) {
                    map.put(s[0].trim(), s[2].trim()); // classID -> instructor
                    map.put(s[1].trim().toLowerCase(), s[2].trim()); // className -> instructor
                }
            }
        } catch (Exception ignored) {
        }
        return map;
    }
    private void loadBookings() {
        DefaultTableModel model = (DefaultTableModel) bookingtable.getModel();
        model.setRowCount(0);

        String keyword = (txtSearch != null) ? txtSearch.getText().trim().toLowerCase() : "";
        java.util.Map<String, String> instructorMap = loadInstructorMap();
        int activeTotal = 0;

        if (!Files.exists(bookingsFile)) {
            if (txtTotalBookings != null) {
                txtTotalBookings.setText("0");
            }
            return;
        }

        try {
            List<String> lines = Files.readAllLines(bookingsFile, StandardCharsets.UTF_8);
            int rowIndex = 1;
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split("\\s*\\|\\s*", -1);
                if (p.length < 6 || !p[0].trim().equals(studentID)) continue;

                String bookingID = "B" + String.format("%03d", rowIndex++);
                String classID = p[1].trim();
                String className = p[2].trim();
                String instructor = (p.length >= 8 && !p[3].trim().isEmpty())
                        ? p[3].trim()
                        : instructorMap.getOrDefault(classID,
                                instructorMap.getOrDefault(className.toLowerCase(), "-"));
                String date = (p.length >= 8) ? p[4].trim() : p[3].trim();
                String time = (p.length >= 8) ? p[5].trim() : p[4].trim();
                String fee = (p.length >= 8) ? p[6].trim() : "30.00";
                String status = (p.length >= 8) ? p[7].trim() : p[5].trim();

                if (!"Cancelled".equalsIgnoreCase(status)) {
                    activeTotal++;
                }

                if (!keyword.isEmpty()) {
                    boolean match = bookingID.toLowerCase().contains(keyword)
                            || className.toLowerCase().contains(keyword)
                            || instructor.toLowerCase().contains(keyword)
                            || date.toLowerCase().contains(keyword)
                            || status.toLowerCase().contains(keyword);
                    if (!match) continue;
                }

                model.addRow(new Object[]{bookingID, className, instructor, date, time, fee, status});
            }

            if (txtTotalBookings != null) {
                txtTotalBookings.setText(String.valueOf(activeTotal));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + ex.getMessage());
        }
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingtable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) bookingtable.getModel();
        String className = model.getValueAt(selectedRow, 1).toString();
        String date = model.getValueAt(selectedRow, 3).toString();
        String time = model.getValueAt(selectedRow, 4).toString();
        String status = model.getValueAt(selectedRow, 6).toString();

        if ("Cancelled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Cancel booking for '%s' on %s at %s?", className, date, time),
                "Cancel Booking", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(bookingsFile, StandardCharsets.UTF_8);
            List<String> updated = new ArrayList<>();
            boolean changed = false;

            for (String line : lines) {
                if (line.isBlank()) {
                    updated.add(line);
                    continue;
                }
                String[] p = line.split("\\s*\\|\\s*", -1);
                if (!changed && p.length >= 6 && p[0].trim().equals(studentID)) {
                    String lineClass = p[2].trim();
                    String lineDate = (p.length >= 8) ? p[4].trim() : p[3].trim();
                    String lineTime = (p.length >= 8) ? p[5].trim() : p[4].trim();
                    String lineStatus = (p.length >= 8) ? p[7].trim() : p[5].trim();

                    if (lineClass.equals(className) && lineDate.equals(date)
                            && lineTime.equals(time) && !"Cancelled".equalsIgnoreCase(lineStatus)) {
                        if (p.length >= 8) {
                            p[7] = "Cancelled";
                        } else {
                            p[5] = "Cancelled";
                        }
                        line = String.join(" | ", p);
                        changed = true;
                    }
                }
                updated.add(line);
            }

            Files.write(bookingsFile, updated, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            loadBookings();
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to cancel booking: " + ex.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bookingtable = new javax.swing.JTable();
        btnCancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnsearch = new javax.swing.JButton();
        btnreset = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        txtTotalBookings = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Search:");

        bookingtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "BookingID", "Class ", "Intructor", "Date", "Time", "Fee", "Status"
            }
        ));
        jScrollPane1.setViewportView(bookingtable);

        btnCancel.setText("Cancel");

        jLabel2.setText("Total Bookings:");

        btnsearch.setText("Search");

        btnreset.setText("Reset ");

        btnBack.setText("Back");

        btnRefresh.setText("Refresh ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnsearch)
                        .addGap(32, 32, 32)
                        .addComponent(btnreset)
                        .addGap(50, 50, 50))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalBookings, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(88, 88, 88)
                                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(176, 176, 176)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnsearch)
                            .addComponent(btnreset))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnRefresh))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalBookings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBack)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

       java.awt.EventQueue.invokeLater(() -> {
        JOptionPane.showMessageDialog(null, "Please open this page through Student Login.");
    });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bookingtable;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnreset;
    private javax.swing.JButton btnsearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTotalBookings;
    // End of variables declaration//GEN-END:variables
}
