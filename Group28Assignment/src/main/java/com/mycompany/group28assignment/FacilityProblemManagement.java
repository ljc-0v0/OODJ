package com.mycompany.group28assignment;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FacilityProblemManagement extends JPanel {

    private final Path dataDir = Paths.get("data");
    private final Path problemsFile =
            dataDir.resolve("facility_problems.txt");
    private final Path equipmentFile =
        dataDir.resolve("equipment.txt");
    private JTextField txtSearch;

    private final String[] columns = {
        "Problem ID",
        "Equipment ID",
        "Equipment Name",
        "Location",
        "Description",
        "Date",
        "Status",
        "Created By"
    };

    private final DefaultTableModel model =
            new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    private final JTextField searchField =
            new JTextField(20);

    private final JLabel countLabel =
            new JLabel("Showing: 0 / 0");

    private final JButton backButton =
            new JButton("Back");

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

    private final MainFrame mainFrame;

    public FacilityProblemManagement(MainFrame mainFrame) {

        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15));

        initializeGUI();
        loadRecords();
    }

    private void initializeGUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel(
                "Facility Problem Management",
                SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));

        title.setBorder(BorderFactory.createEmptyBorder(
                5, 10, 15, 10));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        backButton.addActionListener(e ->
                mainFrame.showPanel("Facility Officer")
        );

        txtSearch = new JTextField(20);
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(new JLabel("Search by ID or Name:"));
        buttonPanel.add(searchField);
        buttonPanel.add(btnRefresh);
        

        add(buttonPanel, BorderLayout.CENTER);

        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        
        table.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                Component component =
                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column
                        );

                String status = String.valueOf(
                        table.getValueAt(row, 6)
                ).trim();

                if (isSelected) {

                    component.setBackground(
                            table.getSelectionBackground()
                    );

                    component.setForeground(
                            table.getSelectionForeground()
                    );

                } else if (status.equalsIgnoreCase("In Progress")) {

                    component.setBackground(
                            new Color(254, 226, 226)
                    );

                    component.setForeground(
                            new Color(153, 27, 27)
                    );

                } else if (status.equalsIgnoreCase("Resolved")) {

                    component.setBackground(
                            new Color(220, 252, 231)
                    );

                    component.setForeground(
                            new Color(22, 101, 52)
                    );

                } else {

                    component.setBackground(
                            table.getBackground()
                    );

                    component.setForeground(
                            table.getForeground()
                    );
                }

                return component;
            }
        });

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)) {

                    int selectedRow = table.rowAtPoint(e.getPoint());

                    if (selectedRow >= 0) {

                        table.setRowSelectionInterval(
                                selectedRow, selectedRow);

                        updateStatus();
                    }
                }
            }
        });
        
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadRecords();
        });
    
    

        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Facility Problem Records"));

        JPanel contentPanel = new JPanel(
                new BorderLayout(10, 10));

        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        remove(buttonPanel);
        add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
                new BorderLayout());

        bottomPanel.add(countLabel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterRecords();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterRecords();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterRecords();
            }
        });
    }

    private void loadRecords() {

        model.setRowCount(0);

        try {

            Files.createDirectories(dataDir);

            if (!Files.exists(problemsFile)) {
                updateCount();
                return;
            }

            List<String> lines = Files.readAllLines(
                    problemsFile,
                    StandardCharsets.UTF_8);

            for (String line : lines) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] record = line.split("\\s*\\|\\s*", -1);

                if (record.length >= 8) {

                    model.addRow(Arrays.copyOf(record, 8));

                } else if (record.length == 5) {
                    model.addRow(new String[]{
                        record[0],
                        "",
                        record[1],
                        "",
                        record[2],
                        record[3],
                        record[4],
                        ""
                    });
                }
            }

            updateCount();

        } catch (IOException ex) {

            showError("Cannot load facility problems: "
                    + ex.getMessage());
        }
    }

    private void filterRecords() {

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {

            sorter.setRowFilter(null);

        } else {

            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(keyword),
                            0, 2
                    )
            );
        }

        updateCount();
    }

    private void updateStatus() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a facility problem first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        String problemID = String.valueOf(model.getValueAt(modelRow, 0));
        String equipmentID = String.valueOf(model.getValueAt(modelRow, 1));
        String equipmentName = String.valueOf(model.getValueAt(modelRow, 2));
        String location = String.valueOf(model.getValueAt(modelRow, 3));
        String description = String.valueOf(model.getValueAt(modelRow, 4));
        String date = String.valueOf(model.getValueAt(modelRow, 5));
        String currentStatus = String.valueOf(model.getValueAt(modelRow, 6));
        String createdBy = String.valueOf(model.getValueAt(modelRow, 7));

        JPanel detailPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        detailPanel.add(new JLabel("Problem ID:"));
        JTextField idField = new JTextField(problemID);
        idField.setEditable(false);
        detailPanel.add(idField);

        detailPanel.add(new JLabel("Equipment ID:"));
        JTextField equipmentIDField = new JTextField(equipmentID);
        equipmentIDField.setEditable(false);
        detailPanel.add(equipmentIDField);

        detailPanel.add(new JLabel("Equipment Name:"));
        JTextField nameField = new JTextField(equipmentName);
        nameField.setEditable(false);
        detailPanel.add(nameField);

        detailPanel.add(new JLabel("Location:"));
        JTextField locationField = new JTextField(location);
        locationField.setEditable(false);
        detailPanel.add(locationField);

        detailPanel.add(new JLabel("Description:"));
        JTextField descriptionField = new JTextField(description);
        descriptionField.setEditable(false);
        detailPanel.add(descriptionField);

        detailPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(date);
        dateField.setEditable(false);
        detailPanel.add(dateField);

        detailPanel.add(new JLabel("Status:"));

        String[] statuses = {
            "In Progress",
            "Resolved"
        };

        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setSelectedItem(currentStatus);
        detailPanel.add(statusBox);

        detailPanel.add(new JLabel("Created By:"));
        JTextField createdByField = new JTextField(createdBy);
        createdByField.setEditable(false);
        detailPanel.add(createdByField);

        int answer = JOptionPane.showConfirmDialog(
                this,
                detailPanel,
                "Facility Problem Details - " + problemID,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }
        
        String newStatus = String.valueOf(statusBox.getSelectedItem());

        model.setValueAt(newStatus, modelRow, 6);

        String currentDate = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        model.setValueAt(currentDate, modelRow, 5);

        saveRecords();
    }

    private void saveRecords() {

        try {

            List<String> lines = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {

                String[] record = new String[8];

                for (int j = 0; j < 8; j++) {

                    record[j] = String.valueOf(
                            model.getValueAt(i, j));
                }

                lines.add(String.join(" | ", record));
            }

            Files.write(
                    problemsFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            syncEquipmentStatus();

            JOptionPane.showMessageDialog(
                    this,
                    "Facility problem status updated successfully."
            );

            loadRecords();

        } catch (IOException ex) {

            showError(
                    "Cannot update Facility Problem or Equipment Status:\n"
                    + ex.getMessage()
            );

            loadRecords();
        }
    }
    
    private void updateCount() {

        countLabel.setText(
                "Showing: " + table.getRowCount()
                + " / " + model.getRowCount());
    }

    private void syncEquipmentStatus() throws IOException {

        if (!Files.exists(equipmentFile)) {
            throw new IOException(
                    "Equipment file not found:\n" + equipmentFile
            );
        }

        if (!Files.exists(problemsFile)) {
            throw new IOException(
                    "Facility Problem file not found:\n" + problemsFile
            );
        }

        List<String> problemLines = Files.readAllLines(
                problemsFile,
                StandardCharsets.UTF_8
        );

        List<String> equipmentLines = Files.readAllLines(
                equipmentFile,
                StandardCharsets.UTF_8
        );

        Set<String> unavailableEquipment = new HashSet<>();

        for (String line : problemLines) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] problem = line.split("\\s*\\|\\s*", -1);

            if (problem.length >= 8) {

                String equipmentID = problem[1].trim();
                String problemStatus = problem[6].trim();

                if ("In Progress".equalsIgnoreCase(problemStatus)) {
                    unavailableEquipment.add(equipmentID);
                }
            }
        }

        List<String> updatedEquipmentLines = new ArrayList<>();

        for (String line : equipmentLines) {

            if (line.trim().isEmpty()) {
                updatedEquipmentLines.add(line);
                continue;
            }

            String[] equipment = line.split("\\s*\\|\\s*", -1);

            if (equipment.length == 9) {

                String equipmentID = equipment[0].trim();

                if (unavailableEquipment.contains(equipmentID)) {
                    equipment[5] = "Unavailable";
                } else {
                    equipment[5] = "Available";
                }

                equipment[7] = LocalDate.now().toString();

                line = String.join(" | ", equipment);
            }

            updatedEquipmentLines.add(line);
        }

        Files.write(
                equipmentFile,
                updatedEquipmentLines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }
    
    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Facility Problem Management",
                JOptionPane.WARNING_MESSAGE);
    }
}


