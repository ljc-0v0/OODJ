package oodjgroupassignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EquipmentInventoryManagement extends JFrame {

    private final Path dataFile =
            Paths.get("equipment.txt").toAbsolutePath();

    private final Path nextIdFile =
            Paths.get("equipment_next_id.txt").toAbsolutePath();

    private final List<Equipment> equipmentList = new ArrayList<>();

    private Equipment selectedEquipment;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextArea txtDescription;
    private JTextField txtStatus;
    private JTextField txtLocation;
    private JTextField txtUpdatedDate;
    private JTextField txtAddedBy;
    private JTextField txtSearch;

    private JTable equipmentTable;
    private DefaultTableModel tableModel;
    private JLabel lblMessage;

    // ==================== EQUIPMENT CLASS ====================

    private static class Equipment {

        String id;
        String name;
        String category;
        String description;
        int quantity;
        String status;
        String location;
        String updatedDate;
        String addedBy;

        Equipment(String id, String name, String category,
                  String description, int quantity, String status,
                  String location, String updatedDate, String addedBy) {

            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.quantity = 1;
            this.status = status;
            this.location = location;
            this.updatedDate = updatedDate;
            this.addedBy = addedBy;
        }
    }

    // ==================== CONSTRUCTOR ====================

    public EquipmentInventoryManagement() {

        setTitle("Equipment Inventory Management");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadFromFile();
        initializeGUI();
        saveToFile();
        displayAllRecords();
    }

    // ==================== GUI ====================

    private void initializeGUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "Equipment Inventory Management",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(
                15, 10, 15, 10
        ));

        add(title, BorderLayout.NORTH);

        // FORM PANEL
        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBorder(BorderFactory.createTitledBorder(
                "Equipment Information"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(15);
        txtId.setEditable(false);

        txtName = new JTextField(15);
        txtCategory = new JTextField(15);

        txtDescription = new JTextArea(3, 15);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        txtStatus = new JTextField("Available", 15);
        txtLocation = new JTextField(15);

        txtUpdatedDate = new JTextField(15);
        txtUpdatedDate.setEditable(false);

        txtAddedBy = new JTextField(15);

        addFormField(formPanel, gbc, 0, "Equipment ID:", txtId);
        addFormField(formPanel, gbc, 1, "Equipment Name:", txtName);
        addFormField(formPanel, gbc, 2, "Category:", txtCategory);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(new JScrollPane(txtDescription), gbc);

        addFormField(formPanel, gbc, 4, "Status:", txtStatus);
        addFormField(formPanel, gbc, 5, "Location:", txtLocation);
        addFormField(formPanel, gbc, 6, "Updated Date:", txtUpdatedDate);
        addFormField(formPanel, gbc, 7, "Added By:", txtAddedBy);

        // BUTTON PANEL
        // View All, View Individual Records and Clear Search removed.

        JPanel buttonPanel = new JPanel(
                new GridLayout(0, 2, 8, 8)
        );

        JButton btnAdd = new JButton("Add Equipment");
        JButton btnUpdate = new JButton("Update Equipment");
        JButton btnDelete = new JButton("Delete Equipment");
        JButton btnClear = new JButton("Clear Form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 10));

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(380, 500));

        // TABLE
        String[] columns = {
            "Equipment ID",
            "Equipment Name",
            "Category",
            "Description",
            "Quantity",
            "Status",
            "Location",
            "Updated Date",
            "Added By"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        equipmentTable = new JTable(tableModel);

        equipmentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        equipmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        equipmentTable.setRowHeight(24);

        int[] widths = {
            100, 150, 110, 200, 70, 100, 130, 110, 120
        };

        for (int i = 0; i < widths.length; i++) {

            equipmentTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane tableScrollPane = new JScrollPane(equipmentTable);

        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                "Equipment Records"
        ));

        // SEARCH PANEL
        // Search button is beside the search field.
        // Clear Search button has been removed.

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        txtSearch = new JTextField(20);

        JButton btnSearch = new JButton("Search");

        searchPanel.add(new JLabel("Search by ID or Name:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        // STATUS MESSAGE
        lblMessage = new JLabel("Ready");

        lblMessage.setBorder(BorderFactory.createEmptyBorder(
                5, 10, 10, 10
        ));

        JPanel contentPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        contentPanel.setBorder(BorderFactory.createEmptyBorder(
                0, 10, 0, 10
        ));

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        add(lblMessage, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        btnAdd.addActionListener(e -> addEquipment());
        btnSearch.addActionListener(e -> searchEquipment());
        btnUpdate.addActionListener(e -> updateEquipment());
        btnDelete.addActionListener(e -> deleteEquipment());
        btnClear.addActionListener(e -> clearForm());

        txtSearch.addActionListener(e -> searchEquipment());

        equipmentTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        fillFormFromTable();
                    }
                });
    }

    // ==================== FORM FIELD HELPER ====================

    private void addFormField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(field, gbc);
    }

    // ==================== GENERATE EQUIPMENT ID ====================

    private String generateEquipmentId() {

        int nextId = 1;

        try {

            if (Files.exists(nextIdFile)) {

                String saved = new String(
                        Files.readAllBytes(nextIdFile),
                        StandardCharsets.UTF_8
                ).trim();

                if (!saved.isEmpty()) {
                    nextId = Integer.parseInt(saved);
                }
            }

        } catch (IOException | NumberFormatException ex) {

            nextId = 1;
        }

        for (Equipment item : equipmentList) {

            try {

                int currentId = Integer.parseInt(item.id);

                if (currentId >= nextId) {
                    nextId = currentId + 1;
                }

            } catch (NumberFormatException ex) {
                // Ignore non-numeric IDs.
            }
        }

        try {

            Files.write(
                    nextIdFile,
                    String.valueOf(nextId + 1)
                            .getBytes(StandardCharsets.UTF_8)
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save the next equipment ID."
            );
        }

        return String.valueOf(nextId);
    }

    // ==================== VALIDATE INPUT ====================

    private boolean validateInput() {

        if (txtName.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the equipment name."
            );

            txtName.requestFocus();
            return false;
        }

        if (txtCategory.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the equipment category."
            );

            txtCategory.requestFocus();
            return false;
        }

        if (txtAddedBy.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the name of the person responsible."
            );

            txtAddedBy.requestFocus();
            return false;
        }

        return true;
    }

    // ==================== ADD EQUIPMENT ====================

    private void addEquipment() {

        if (!validateInput()) {
            return;
        }

        String id = generateEquipmentId();

        Equipment item = new Equipment(
                id,
                txtName.getText().trim(),
                txtCategory.getText().trim(),
                txtDescription.getText().trim(),
                1,
                txtStatus.getText().trim().isEmpty()
                        ? "Available"
                        : txtStatus.getText().trim(),
                txtLocation.getText().trim(),
                LocalDate.now().toString(),
                txtAddedBy.getText().trim()
        );

        equipmentList.add(item);

        if (saveToFile()) {

            displayAllRecords();
            clearForm();

            lblMessage.setText(
                    "Equipment added successfully."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Equipment added successfully!\n"
                            + "Equipment ID: " + id
                            + "\nQuantity: 1"
            );

        } else {

            equipmentList.remove(item);
        }
    }

    // ==================== DISPLAY ALL RECORDS ====================

    private void displayAllRecords() {

        selectedEquipment = null;
        tableModel.setRowCount(0);

        for (Equipment item : equipmentList) {
            addTableRow(item);
        }

        lblMessage.setText(
                "Showing all individual equipment records."
        );
    }

    // ==================== ADD TABLE ROW ====================

    private void addTableRow(Equipment item) {

        tableModel.addRow(new Object[]{
            item.id,
            item.name,
            item.category,
            item.description,
            1,
            item.status,
            item.location,
            item.updatedDate,
            item.addedBy
        });
    }

    // ==================== SEARCH EQUIPMENT ====================

    private void searchEquipment() {

        String keyword = txtSearch.getText()
                .trim()
                .toLowerCase();

        selectedEquipment = null;
        tableModel.setRowCount(0);

        if (keyword.isEmpty()) {

            displayAllRecords();
            return;
        }

        for (Equipment item : equipmentList) {

            if (item.id.toLowerCase().contains(keyword)
                    || item.name.toLowerCase().contains(keyword)) {

                addTableRow(item);
            }
        }

        lblMessage.setText(
                "Matching records: " + tableModel.getRowCount()
        );

        if (tableModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No matching equipment found."
            );
        }
    }

    // ==================== UPDATE EQUIPMENT ====================

    private void updateEquipment() {

        if (selectedEquipment == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an equipment record."
            );

            return;
        }

        if (!validateInput()) {
            return;
        }

        Equipment backup = copyEquipment(selectedEquipment);

        selectedEquipment.name = txtName.getText().trim();

        selectedEquipment.category =
                txtCategory.getText().trim();

        selectedEquipment.description =
                txtDescription.getText().trim();

        selectedEquipment.quantity = 1;

        selectedEquipment.status =
                txtStatus.getText().trim().isEmpty()
                        ? "Available"
                        : txtStatus.getText().trim();

        selectedEquipment.location =
                txtLocation.getText().trim();

        selectedEquipment.updatedDate =
                LocalDate.now().toString();

        selectedEquipment.addedBy =
                txtAddedBy.getText().trim();

        if (saveToFile()) {

            if (txtSearch.getText().trim().isEmpty()) {
                displayAllRecords();
            } else {
                searchEquipment();
            }

            clearForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Equipment updated successfully!"
            );

        } else {

            selectedEquipment.name = backup.name;
            selectedEquipment.category = backup.category;
            selectedEquipment.description = backup.description;
            selectedEquipment.quantity = 1;
            selectedEquipment.status = backup.status;
            selectedEquipment.location = backup.location;
            selectedEquipment.updatedDate = backup.updatedDate;
            selectedEquipment.addedBy = backup.addedBy;
        }
    }

    // ==================== DELETE EQUIPMENT ====================

    private void deleteEquipment() {

        if (selectedEquipment == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an equipment record to delete."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete this equipment?\nID: "
                        + selectedEquipment.id
                        + "\nName: " + selectedEquipment.name,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Equipment itemToDelete = selectedEquipment;

        equipmentList.remove(itemToDelete);

        if (saveToFile()) {

            displayAllRecords();
            clearForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Equipment deleted successfully!"
            );

        } else {

            equipmentList.add(itemToDelete);
            displayAllRecords();
        }
    }

    // ==================== FIND EQUIPMENT BY ID ====================

    private Equipment findEquipmentById(String id) {

        for (Equipment item : equipmentList) {

            if (item.id.equals(id)) {
                return item;
            }
        }

        return null;
    }

    // ==================== COPY EQUIPMENT ====================

    private Equipment copyEquipment(Equipment item) {

        return new Equipment(
                item.id,
                item.name,
                item.category,
                item.description,
                1,
                item.status,
                item.location,
                item.updatedDate,
                item.addedBy
        );
    }

    // ==================== FILL FORM FROM TABLE ====================

    private void fillFormFromTable() {

        int row = equipmentTable.getSelectedRow();

        if (row < 0) {

            selectedEquipment = null;
            return;
        }

        Object idValue = tableModel.getValueAt(row, 0);

        if (idValue == null) {

            selectedEquipment = null;
            return;
        }

        selectedEquipment =
                findEquipmentById(idValue.toString());

        if (selectedEquipment == null) {
            return;
        }

        txtId.setText(selectedEquipment.id);
        txtName.setText(selectedEquipment.name);
        txtCategory.setText(selectedEquipment.category);
        txtDescription.setText(selectedEquipment.description);
        txtStatus.setText(selectedEquipment.status);
        txtLocation.setText(selectedEquipment.location);
        txtUpdatedDate.setText(selectedEquipment.updatedDate);
        txtAddedBy.setText(selectedEquipment.addedBy);
    }

    // ==================== CLEAR FORM ====================

    private void clearForm() {

        equipmentTable.clearSelection();
        selectedEquipment = null;

        txtId.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtDescription.setText("");
        txtStatus.setText("Available");
        txtLocation.setText("");
        txtUpdatedDate.setText(LocalDate.now().toString());
        txtAddedBy.setText("");
    }

    // ==================== LOAD FROM TXT FILE ====================

    private void loadFromFile() {

        equipmentList.clear();

        if (!Files.exists(dataFile)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                dataFile,
                StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {

                    String[] data = line.split("\\|", -1);

                    if (data.length != 9) {
                        continue;
                    }

                    Equipment item = new Equipment(
                            decode(data[0]),
                            decode(data[1]),
                            decode(data[2]),
                            decode(data[3]),
                            1,
                            decode(data[5]),
                            decode(data[6]),
                            decode(data[7]),
                            decode(data[8])
                    );

                    equipmentList.add(item);

                } catch (IllegalArgumentException ex) {

                    // Skip invalid records.
                }
            }

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading equipment file:\n"
                            + ex.getMessage()
            );
        }
    }

    // ==================== SAVE TO TXT FILE ====================

    private boolean saveToFile() {

        try (BufferedWriter writer = Files.newBufferedWriter(
                dataFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            for (Equipment item : equipmentList) {

                String[] data = {
                    item.id,
                    item.name,
                    item.category,
                    item.description,
                    "1",
                    item.status,
                    item.location,
                    item.updatedDate,
                    item.addedBy
                };

                for (int i = 0; i < data.length; i++) {

                    if (i > 0) {
                        writer.write("|");
                    }

                    writer.write(encode(data[i]));
                }

                writer.newLine();
            }

            return true;

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving equipment file:\n"
                            + ex.getMessage()
            );

            return false;
        }
    }

    // ==================== ENCODE DATA ====================

    private String encode(String value) {

        if (value == null) {
            value = "";
        }

        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8)
        );
    }

    // ==================== DECODE DATA ====================

    private String decode(String value) {

        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            EquipmentInventoryManagement app =
                    new EquipmentInventoryManagement();

            app.setVisible(true);
        });
    }
}