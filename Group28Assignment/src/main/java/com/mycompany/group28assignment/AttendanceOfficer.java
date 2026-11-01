package com.mycompany.group28assignment;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class AttendanceOfficer extends User{
    private MainFrame mainFrame;
    
    public AttendanceOfficer(String userID) {
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
    
    public JPanel viewClassPanel(MainFrame mainFrame) {
        String[] columns = {"Class ID", "Class Name", "Discipline"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<String> lines = Files.readAllLines(Path.of("classInfo.txt"));

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 6) {
                    model.addRow(new Object[]{
                        parts[0],
                        parts[1],
                        parts[2]
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error reading class file: " + ex.getMessage());
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("Class", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton backBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(backBtn, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override          
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 &&
                        SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = table.rowAtPoint(e.getPoint());
                    if (selectedRow != -1) {
                        String classID = table.getValueAt(selectedRow, 0).toString();
                        mainFrame.showAttendanceList(classID);
                    }
                }
            }
        });

        backBtn.addActionListener(e -> {
            mainFrame.showPanel("AttendanceOfficer");
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void checkAttendanceFile() {
        Path path = Path.of("attendanceList.txt");

        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error creating attendance file: " + ex.getMessage());
        }
    }
    
    public JPanel viewAttendanceListPanel(MainFrame mainFrame, String classID) {
        checkAttendanceFile();

        String[] columns = {"Attendance ID", "Class ID", "Date", "Created By"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<String> lines = Files.readAllLines(
                    Path.of("attendanceList.txt"));
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 4 && parts[1].equals(classID)) {
                    model.addRow(new Object[]{
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                    });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error reading attendance file: " + ex.getMessage());
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("Attendance List", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton addBtn = new JButton("Add Attendance");
        JButton backBtn = new JButton("Back");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addBtn);
        bottomPanel.add(backBtn);
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int selectedRow = table.getSelectedRow();

                    if (selectedRow != -1) {

                        String attendanceID = model.getValueAt(
                                selectedRow, 0
                        ).toString();

                        mainFrame.showMarkAttendance(attendanceID);
                    }
                }
            }
        });
        
        addBtn.addActionListener(e -> {
            addAttendanceList(model, classID);
        });

        backBtn.addActionListener(e -> {
            mainFrame.showPanel("viewClass");
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    public void addAttendanceList(DefaultTableModel model, String classID) {

        try {
            Path attendancePath = Path.of("attendanceList.txt");
            Path studentAttendancePath = Path.of("studentAttendance.txt");

            if (Files.notExists(attendancePath)) {
                Files.createFile(attendancePath);
            }

            if (Files.notExists(studentAttendancePath)) {
                Files.createFile(studentAttendancePath);
            }


            Path studentPath = Path.of("Student.txt");

            if (Files.notExists(studentPath)) {
                JOptionPane.showMessageDialog(null,
                        "Student file not found.");
                return;
            }

            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd");
            dateChooser.setDate(new Date());

            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

            inputPanel.add(new JLabel("Class ID:"));
            inputPanel.add(new JLabel(classID));
            inputPanel.add(new JLabel("Attendance Date:"));
            inputPanel.add(dateChooser);
            inputPanel.add(new JLabel("Created By:"));
            inputPanel.add(new JLabel(this.userName));
                    
            int result = JOptionPane.showConfirmDialog(
                    null,
                    inputPanel,
                    "Add Attendance List",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }


            Date selectedDate = dateChooser.getDate();

            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null,
                        "Please select a date.");
                return;
            }
            
            
            String date = new SimpleDateFormat("yyyy-MM-dd")
                    .format(selectedDate);

            List<String> studentLines = Files.readAllLines(studentPath);

            List<String> studentIDs = new ArrayList<>();

            for (String line : studentLines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 4
                        && parts[2].equalsIgnoreCase("Student")
                        && parts[3].equals(classID)) {

                    studentIDs.add(parts[0]);
                }
            }

            if (studentIDs.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No students found in this class.");
                return;
            }

            int nextID = 1;
            List<String> attendanceLines =
                    Files.readAllLines(attendancePath);

            for (String line : attendanceLines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 1 && parts[0].startsWith("A")) {

                    try {
                        int number = Integer.parseInt(
                                parts[0].substring(1));

                        if (number >= nextID) {
                            nextID = number + 1;
                        }
                    } catch (NumberFormatException ex) {
                        
                    }
                }
            }

            String attendanceID = String.format("A%03d", nextID);
            String attendanceRecord = attendanceID + "|" + classID + "|" + date + "|" + this.userName;

            Files.writeString(
                    attendancePath,
                    attendanceRecord + System.lineSeparator(),
                    StandardOpenOption.APPEND
            );

            List<String> studentAttendanceRecords = new ArrayList<>();

            for (String studentID : studentIDs) {
                String record = attendanceID + "|" + studentID + "|Absent";
                studentAttendanceRecords.add(record);
            }

            Files.write(
                    studentAttendancePath,
                    studentAttendanceRecords,
                    StandardOpenOption.APPEND
            );

            model.addRow(new Object[]{attendanceID,classID,date,this.userName});

            JOptionPane.showMessageDialog(null,
                    "Attendance List added successfully!");

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(null,
                    "Error: " + ex.getMessage());
        }
    }
    
    public JPanel markAttendance(MainFrame mainFrame, String attendanceID) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("Mark Attendance - " + attendanceID, JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Store each student's attendance status
        Map<String, String> statusMap = new LinkedHashMap<>();
        // Store each student's row panel
        Map<String, JPanel> rowMap = new HashMap<>();
        // Store student names
        Map<String, String> studentNames = new HashMap<>();

        try {
            Path studentPath = Path.of("student.txt");
            Path studentAttendancePath = Path.of("studentAttendance.txt");

            if (Files.notExists(studentPath)) {
                JOptionPane.showMessageDialog(panel, "Student file not found.");
                return panel;
            }

            if (Files.notExists(studentAttendancePath)) {
                JOptionPane.showMessageDialog(panel, "Student attendance file not found.");
                return panel;
            }


            List<String> studentLines = Files.readAllLines(studentPath);
            for (String line : studentLines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                if (parts.length >= 2) {
                    studentNames.put(parts[0], parts[1]);
                }
            }

            List<String> attendanceLines =
                    Files.readAllLines(studentAttendancePath);
            JPanel studentListPanel = new JPanel();
            studentListPanel.setLayout(new BoxLayout(
                    studentListPanel, BoxLayout.Y_AXIS
            ));

            boolean found = false;
            for (String line : attendanceLines) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                
                if (parts.length < 3 || !parts[0].equals(attendanceID)) {
                    continue;
                }
                found = true;
                String studentID = parts[1];
                String status = parts[2];

                if (status.isEmpty()) {
                    status = "Absent";
                }

                statusMap.put(studentID, status);
                String studentName = studentNames.getOrDefault(
                        studentID, "Unknown Student"
                );

                JPanel studentRow = new JPanel(new BorderLayout(10, 0));
                studentRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                studentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
                studentRow.setOpaque(true);

                JLabel nameLabel = new JLabel(
                        studentName + " (" + studentID + ")"
                );

                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                nameLabel.setOpaque(false);

                JPanel buttonPanel = new JPanel(
                        new FlowLayout(FlowLayout.RIGHT, 5, 0)
                );
                buttonPanel.setOpaque(false);

                JButton absentBtn = new JButton("Absent");
                JButton presentBtn = new JButton("Present");
                JButton lateBtn = new JButton("Late");

                JButton[] buttons = {
                    absentBtn, presentBtn, lateBtn
                };

                for (JButton button : buttons) {
                    button.setFocusPainted(false);
                    button.setOpaque(false);
                    button.setContentAreaFilled(false);
                    button.setBorderPainted(false);
                }

                buttonPanel.add(absentBtn);
                buttonPanel.add(presentBtn);
                buttonPanel.add(lateBtn);

                Runnable updateRowColor = () -> {
                    String currentStatus = statusMap.get(studentID);

                    if ("Absent".equals(currentStatus)) {
                        studentRow.setBackground(Color.RED);
                        nameLabel.setForeground(Color.WHITE);

                    } else if ("Present".equals(currentStatus)) {
                        studentRow.setBackground(Color.GREEN);
                        nameLabel.setForeground(Color.BLACK);

                    } else if ("Late".equals(currentStatus)) {
                        studentRow.setBackground(Color.ORANGE);
                        nameLabel.setForeground(Color.BLACK);
                    }

                    studentRow.repaint();
                };

                absentBtn.addActionListener(e -> {
                    statusMap.put(studentID, "Absent");
                    updateStudentStatus(attendanceID, studentID, "Absent");
                    updateRowColor.run();
                });

                presentBtn.addActionListener(e -> {
                    statusMap.put(studentID, "Present");
                    updateStudentStatus(attendanceID, studentID, "Present");
                    updateRowColor.run();
                });

                lateBtn.addActionListener(e -> {
                    statusMap.put(studentID, "Late");
                    updateStudentStatus(attendanceID, studentID, "Late");
                    updateRowColor.run();
                });

                studentRow.add(nameLabel, BorderLayout.WEST);
                studentRow.add(buttonPanel, BorderLayout.EAST);

                updateRowColor.run();

                rowMap.put(studentID, studentRow);

                studentListPanel.add(studentRow);
            }

            if (!found) {
                JOptionPane.showMessageDialog(
                        panel, "No students found for this attendance."
                );
                return panel;
            }

            JScrollPane scrollPane = new JScrollPane(studentListPanel);

            JButton backBtn = new JButton("Back");

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(backBtn);

            backBtn.addActionListener(e -> {
                mainFrame.showPanel("AttendanceList");
            });

            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(bottomPanel, BorderLayout.SOUTH);

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    panel, "Error: " + ex.getMessage()
            );
        }

        return panel;
    }
    
    private void updateStudentStatus(
            String attendanceID,
            String studentID,
            String newStatus) {

        Path studentAttendancePath = Path.of("studentAttendance.txt");

        try {
            List<String> lines = Files.readAllLines(studentAttendancePath);
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                if (parts.length >= 3
                        && parts[0].equals(attendanceID)
                        && parts[1].equals(studentID)) {

                    updatedLines.add(
                            attendanceID + "|" + studentID + "|" + newStatus
                    );

                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(studentAttendancePath, updatedLines);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error updating attendance: " + ex.getMessage()
            );
        }
    }
    
    public JPanel generateReportPanel(MainFrame mainFrame) {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Attendance Report", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Search field
        JTextField classField = new JTextField(10);
        JButton generateBtn = new JButton("Generate Report");
        JButton backBtn = new JButton("Back");

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Class ID:"));
        searchPanel.add(classField);
        searchPanel.add(generateBtn);

        // Table
        String[] columns = {
            "Student ID",
            "Student Name",
            "Present",
            "Absent",
            "Late"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        // Total summary
        JLabel totalPresentLabel = new JLabel("Total Present: 0");
        JLabel totalAbsentLabel = new JLabel("Total Absent: 0");
        JLabel totalLateLabel = new JLabel("Total Late: 0");

        JPanel summaryPanel = new JPanel(new FlowLayout());

        summaryPanel.add(totalPresentLabel);
        summaryPanel.add(totalAbsentLabel);
        summaryPanel.add(totalLateLabel);

        // Generate report
        generateBtn.addActionListener(e -> {

            String classID = classField.getText().trim();

            if (classID.isEmpty()) {
                JOptionPane.showMessageDialog(
                        panel, "Please enter a Class ID."
                );
                return;
            }

            model.setRowCount(0);

            try {

                Path studentPath = Path.of("student.txt");
                Path attendancePath = Path.of("attendanceList.txt");
                Path studentAttendancePath = Path.of("studentAttendance.txt");

                if (!Files.exists(studentPath)
                        || !Files.exists(attendancePath)
                        || !Files.exists(studentAttendancePath)) {

                    JOptionPane.showMessageDialog(
                            panel, "Required file not found."
                    );
                    return;
                }

                // Student ID -> Student Name
                Map<String, String> studentNames = new LinkedHashMap<>();

                // Attendance ID -> Class ID
                Map<String, String> attendanceClasses = new HashMap<>();

                // Student ID -> Status counts
                Map<String, int[]> studentCounts = new LinkedHashMap<>();

                // Read students belonging to selected class
                List<String> studentLines =
                        Files.readAllLines(studentPath);

                for (String line : studentLines) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\|", -1);

                    if (parts.length >= 4
                            && parts[2].equalsIgnoreCase("Student")
                            && parts[3].equalsIgnoreCase(classID)) {

                        studentNames.put(parts[0], parts[1]);

                        // Index 0 = Present
                        // Index 1 = Absent
                        // Index 2 = Late
                        studentCounts.put(parts[0], new int[3]);
                    }
                }

                if (studentNames.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            panel, "No students found in this class."
                    );
                    return;
                }

                // Read attendance sessions
                List<String> attendanceLines =
                        Files.readAllLines(attendancePath);

                for (String line : attendanceLines) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\|", -1);

                    if (parts.length >= 2
                            && parts[1].equalsIgnoreCase(classID)) {

                        attendanceClasses.put(parts[0], parts[1]);
                    }
                }

                // Read individual attendance statuses
                List<String> studentAttendanceLines =
                        Files.readAllLines(studentAttendancePath);

                for (String line : studentAttendanceLines) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\|", -1);

                    if (parts.length < 3) {
                        continue;
                    }

                    String attendanceID = parts[0];
                    String studentID = parts[1];
                    String status = parts[2];

                    // Only count records from selected class
                    if (!attendanceClasses.containsKey(attendanceID)) {
                        continue;
                    }

                    // Only count students in selected class
                    if (!studentCounts.containsKey(studentID)) {
                        continue;
                    }

                    int[] counts = studentCounts.get(studentID);

                    if (status.equalsIgnoreCase("Present")) {
                        counts[0]++;

                    } else if (status.equalsIgnoreCase("Absent")) {
                        counts[1]++;

                    } else if (status.equalsIgnoreCase("Late")) {
                        counts[2]++;
                    }
                }

                int totalPresent = 0;
                int totalAbsent = 0;
                int totalLate = 0;

                // Display report
                for (String studentID : studentNames.keySet()) {

                    String studentName = studentNames.get(studentID);
                    int[] counts = studentCounts.get(studentID);

                    model.addRow(new Object[]{
                        studentID,
                        studentName,
                        counts[0],
                        counts[1],
                        counts[2]
                    });

                    totalPresent += counts[0];
                    totalAbsent += counts[1];
                    totalLate += counts[2];
                }

                // Update summary
                totalPresentLabel.setText(
                        "Total Present: " + totalPresent
                );

                totalAbsentLabel.setText(
                        "Total Absent: " + totalAbsent
                );

                totalLateLabel.setText(
                        "Total Late: " + totalLate
                );

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        panel, "Error: " + ex.getMessage()
                );
            }
        });

        // Back button
        backBtn.addActionListener(e -> {
            mainFrame.showPanel("AttendanceOfficer");
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);

        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}   
