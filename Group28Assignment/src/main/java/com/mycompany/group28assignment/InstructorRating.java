/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


public class InstructorRating extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(InstructorRating.class.getName());

    private final ButtonGroup ratingGroup = new ButtonGroup();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Map<String, String> ratedBookingToRatingId = new HashMap<>();
    private static final String RATING_FILE = "data/ratings.txt";
    private final Path bookingsPath = Paths.get("data", "bookings.txt");
    private final Path schedulesPath = Paths.get("data", "schedules.txt");

    private javax.swing.JFrame previousFrame;
    private String studentID;
    
public InstructorRating(JFrame parent, String studentID) {
        this(parent);
        this.studentID = studentID;
        reloadBookingsTable();
        loadRatedBookings();
        setRatingEnabled(false);
    }

public InstructorRating(javax.swing.JFrame previous) {
        this.previousFrame = previous;
        initComponents();
        setupRating();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (previousFrame != null) {
                    previousFrame.setLocationRelativeTo(null);
                    previousFrame.setVisible(true);
                }
            }
        });
    }
    private void setupRating() {
        JRadioButton[] radios = {btn1, btn2, btn3, btn4, btn5};
        for (JRadioButton r : radios) {
            ratingGroup.add(r);
        }

        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reloadBookingsTable();

        loadRatedBookings();
        setRatingEnabled(false);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                checkCanRate();
            }
        });

        btnsubmit.addActionListener(e -> submitRating());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> goBack());
    }
    
  private void reloadBookingsTable() {
        String[] cols = {"BookingID", "Class", "Instructor", "Date", "Attendance"};
        List<Object[]> rows = loadStudentBookingsFromFiles();
        Object[][] data = rows.toArray(new Object[0][]);

        jTable1.setModel(new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
    }

   private List<Object[]> loadStudentBookingsFromFiles() {
    List<Object[]> result = new ArrayList<>();
    if (!Files.exists(bookingsPath)) {
        return result;
    }

    Map<String, String> instructorMap = new HashMap<>();
    if (Files.exists(schedulesPath)) {
        try {
            for (String line : Files.readAllLines(schedulesPath, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] s = line.split("\\s*\\|\\s*", -1);
                if (s.length >= 3) {
                    instructorMap.put(s[0].trim(), s[2].trim());
                    instructorMap.put(s[1].trim().toLowerCase(), s[2].trim());
                }
            }
        } catch (IOException ignored) {
        }
    }

    try {
        int idx = 1;
        for (String line : Files.readAllLines(bookingsPath, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length < 6) {
                continue;
            }
            if (studentID != null && !studentID.isBlank() && !p[0].trim().equals(studentID)) {
                continue;
            }

            String bookingID = "B" + String.format("%03d", idx++);
            String classID = p[1].trim();
            String className = p[2].trim();
            String instructor = instructorMap.getOrDefault(
                    className.toLowerCase(),
                    instructorMap.getOrDefault(classID, "-")
            );
            String datePart = p[3].trim();
            String timePart = p[4].trim();
            String status = p[5].trim();

            String formattedDate = formatBookingDateTime(datePart, timePart);
            result.add(new Object[]{bookingID, className, instructor, formattedDate, status});
        }
    } catch (IOException ignored) {
    }
    return result;
}

    private String formatBookingDateTime(String dateStr, String timeStr) {
        try {
            LocalDate d = LocalDate.parse(dateStr);
            DateTimeFormatter[] timePatterns = {
                DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH),
                DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.ENGLISH),
                DateTimeFormatter.ofPattern("HH:mm")
            };
            for (DateTimeFormatter tf : timePatterns) {
                try {
                    LocalTime t = LocalTime.parse(timeStr.toUpperCase(java.util.Locale.ENGLISH), tf);
                    return LocalDateTime.of(d, t).format(FMT);
                } catch (Exception ignored) {
                }
            }
            return d + " 00:00";
        } catch (Exception ex) {
            return dateStr + " " + timeStr;
        }
    }
private void goBack() {
 
    dispose();
}
private void setRatingEnabled(boolean enabled) {
    btn1.setEnabled(enabled);
    btn2.setEnabled(enabled);
    btn3.setEnabled(enabled);
    btn4.setEnabled(enabled);
    btn5.setEnabled(enabled);
    txtfeedback.setEnabled(enabled);
    btnClear.setEnabled(enabled);
}



 private void checkCanRate() {
    int row = jTable1.getSelectedRow();
    if (row < 0) {
        setRatingEnabled(false);
        return;
    }

    Object idObj = jTable1.getValueAt(row, 0);
    Object dateObj = jTable1.getValueAt(row, 3);
    Object attObj = jTable1.getValueAt(row, 4);

    if (idObj == null || dateObj == null || attObj == null) {
        setRatingEnabled(false);
        return;
    }

    String id = idObj.toString().trim();
    String dateStr = dateObj.toString().trim();
    String attendance = attObj.toString().trim();

    LocalDateTime classEnd;
    try {
        classEnd = LocalDateTime.parse(dateStr, FMT);
    } catch (Exception ex) {
        setRatingEnabled(false);
        JOptionPane.showMessageDialog(this, "Invalid date format: " + dateStr);
        return;
    }

    String existingRatingId = ratedBookingToRatingId.get(id);
    if (existingRatingId != null) {
        setRatingEnabled(false);
        JOptionPane.showMessageDialog(this,
                "You have already rated this class (Rating ID: " + existingRatingId + ").");
    } else if (LocalDateTime.now().isBefore(classEnd)) {
        setRatingEnabled(false);
        JOptionPane.showMessageDialog(this,
                "The class has not ended yet.\nYou can rate after " + dateStr + ".");
    } else if (!attendance.equalsIgnoreCase("Attended")) {
        setRatingEnabled(false);
        JOptionPane.showMessageDialog(this, "Only attended classes can be rated.");
    } else {
        setRatingEnabled(true);
    }
}

 private void submitRating() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }
        if (ratingGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Please choose a rating star (1-5).");
            return;
        }

        String ratingStar = "";
        for (JRadioButton r : new JRadioButton[]{btn1, btn2, btn3, btn4, btn5}) {
            if (r.isSelected()) {
                ratingStar = r.getText().trim();
            }
        }

        String ratingId = generateNextRatingId();
        String bookingId = jTable1.getValueAt(row, 0).toString().trim();
        String instructor = jTable1.getValueAt(row, 2).toString().trim();
        String description = txtfeedback.getText().trim().replace("|", "/");
        String studentPart = (studentID != null && !studentID.isBlank()) ? studentID.trim() : "GUEST";

        File outFile = new File(RATING_FILE);
        if (outFile.getParentFile() != null && !outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }

        // Format: RatingID|StudentID|BookingID|Instructor|RatingStar|Description
        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile, true))) {
            pw.println(ratingId + "|" + studentPart + "|" + bookingId + "|" + instructor + "|" + ratingStar + "|" + description);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
            return;
        }

        ratedBookingToRatingId.put(bookingId, ratingId);
        JOptionPane.showMessageDialog(this,
                "Thank you! Rating ID " + ratingId + " linked to Booking ID " + bookingId + " has been saved.\nFile: "
                        + outFile.getAbsolutePath());
        clearForm();
        setRatingEnabled(false);
    }
private void clearForm() {
    ratingGroup.clearSelection();
    txtfeedback.setText("");
}
private String generateNextRatingId() {
    int maxNum = 0;
    File f = new File(RATING_FILE);
    if (f.exists()) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String firstCol = line.split("\\|")[0].trim();
                if (firstCol.matches("R\\d+")) {
                    int num = Integer.parseInt(firstCol.substring(1));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
    return String.format("R%03d", maxNum + 1);
}

private void loadRatedBookings() {
    ratedBookingToRatingId.clear();
    File f = new File(RATING_FILE);
    if (!f.exists()) {
        return;
    }

    int fallbackIndex = 1;
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            String ratingId;
            String bookingId = "";

            if (parts.length >= 6 && parts[0].trim().matches("R\\d+")) {
                ratingId = parts[0].trim();
                bookingId = parts[2].trim();
            } else if (parts.length >= 5 && parts[0].trim().matches("R\\d+")) {
                // 兼容格式：RatingID | BookingID | Instructor | RatingStar | Description
                ratingId = parts[0].trim();
                bookingId = parts[1].trim();
            } else if (parts.length >= 2 && parts[0].trim().startsWith("S")) {
                // 兼容上一版：StudentID | BookingID | Instructor | RatingStar | Description
                ratingId = String.format("R%03d", fallbackIndex++);
                bookingId = parts[1].trim();
            } else if (parts.length >= 1) {
                // 最早格式：BookingID | Instructor | RatingStar | Description
                ratingId = String.format("R%03d", fallbackIndex++);
                bookingId = parts[0].trim();
            } else {
                continue;
            }

            if (!bookingId.isEmpty()) {
                ratedBookingToRatingId.put(bookingId, ratingId);
            }
        }
    } catch (IOException ignored) {
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnClear = new javax.swing.JButton();
        btnsubmit = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btn1 = new javax.swing.JRadioButton();
        btn2 = new javax.swing.JRadioButton();
        btn4 = new javax.swing.JRadioButton();
        btn5 = new javax.swing.JRadioButton();
        btn3 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        txtfeedback = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Instructor Rating ");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "BookingID", "Class", "Instructor", "Data"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnClear.setText("Clear");

        btnsubmit.setText("Submit");

        jLabel5.setText("Rating:");

        btn1.setText("1");

        btn2.setText("2");
        btn2.addActionListener(this::btn2ActionPerformed);

        btn4.setText("4");

        btn5.setText("5");

        btn3.setText("3");

        jLabel6.setText("My feedback");

        txtfeedback.addActionListener(this::txtfeedbackActionPerformed);

        btnBack.setText("Back");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnsubmit)
                .addGap(18, 18, 18)
                .addComponent(btnClear)
                .addGap(34, 34, 34))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(145, 145, 145)
                                .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnBack)
                            .addComponent(txtfeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnsubmit)
                            .addComponent(btnClear)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(btn1)
                            .addComponent(btn2)
                            .addComponent(btn3)
                            .addComponent(btn4)
                            .addComponent(btn5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)))
                .addGap(18, 18, 18)
                .addComponent(txtfeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(btnBack)
                .addGap(514, 514, 514))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn2ActionPerformed

    private void txtfeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfeedbackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfeedbackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() ->
                JOptionPane.showMessageDialog(null, "Please open this page through Student Login."));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btn1;
    private javax.swing.JRadioButton btn2;
    private javax.swing.JRadioButton btn3;
    private javax.swing.JRadioButton btn4;
    private javax.swing.JRadioButton btn5;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnsubmit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField txtfeedback;
    // End of variables declaration//GEN-END:variables
}
