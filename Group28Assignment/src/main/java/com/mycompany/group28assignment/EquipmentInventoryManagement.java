package oodjgroupassignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private JComboBox<String> cmbCategory;
    private JTextArea txtDescription;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbLocation;
    private JTextField txtUpdatedDate;
    private JTextField txtAddedBy;
    private JTextField txtSearch;

    // Current filter selections
    private String selectedCategoryFilter = "All";
    private String selectedStatusFilter = "All";
    private String selectedLocationFilter = "All";

    private JTable equipmentTable;
    private DefaultTableModel tableModel;
    private JLabel lblMessage;
    private JPanel formPanel;
    private JDialog addDialog;
    private JSpinner spnNumberOfItems;
    private JLabel lblNumberOfItems;

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
        formPanel = new JPanel(new GridBagLayout());

        formPanel.setBorder(BorderFactory.createTitledBorder(
                "Equipment Information"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(15);
        txtId.setEditable(false);

        txtName = new JTextField(15);

        String[] categories = {
            "Clothes",
            "Protection",
            "Training"
        };

        cmbCategory = new JComboBox<>(categories);

        txtDescription = new JTextArea(3, 15);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        String[] statuses = {
            "Available",
            "Unavailable"
        };

        cmbStatus = new JComboBox<>(statuses);
        spnNumberOfItems = new JSpinner(
                new SpinnerNumberModel(1, 1, 1000, 1)
        );
        lblNumberOfItems = new JLabel("Number of Items:");
        cmbLocation = createLocationComboBox();

        txtUpdatedDate = new JTextField(15);
        txtUpdatedDate.setEditable(false);

        txtAddedBy = new JTextField(15);

        addFormField(formPanel, gbc, 0, "Equipment ID:", txtId);
        addFormField(formPanel, gbc, 1, "Equipment Name:", txtName);
        addFormComboField(formPanel, gbc, 2, "Category:", cmbCategory);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(new JScrollPane(txtDescription), gbc);

        addFormComboField(formPanel, gbc, 4, "Status:", cmbStatus);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        formPanel.add(lblNumberOfItems, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(spnNumberOfItems, gbc);

        addFormComboField(formPanel, gbc, 6, "Location:", cmbLocation);
        addFormField(formPanel, gbc, 7, "Updated Date:", txtUpdatedDate);
        addFormField(formPanel, gbc, 8, "Added By:", txtAddedBy);

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

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        txtSearch = new JTextField(20);

        JButton btnSearch = new JButton("Search");
        JButton btnFilter = new JButton("Filter");
        JButton btnAdd = new JButton("Add");

        buttonPanel.add(new JLabel("Search by ID or Name:"));
        buttonPanel.add(txtSearch);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnFilter);
        buttonPanel.add(btnAdd);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        rightPanel.add(buttonPanel, BorderLayout.NORTH);
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

        contentPanel.add(rightPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        add(lblMessage, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        btnAdd.addActionListener(e -> showAddDialog(false));
        btnSearch.addActionListener(e -> searchEquipment());
        btnFilter.addActionListener(e -> showFilterDialog());

        txtSearch.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(
                    javax.swing.event.DocumentEvent e) {
                searchEquipment();
            }

            @Override
            public void removeUpdate(
                    javax.swing.event.DocumentEvent e) {
                searchEquipment();
            }

            @Override
            public void changedUpdate(
                    javax.swing.event.DocumentEvent e) {
                searchEquipment();
            }
        });

        equipmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = equipmentTable.rowAtPoint(e.getPoint());

                if (viewRow >= 0) {
                    int modelRow = equipmentTable.convertRowIndexToModel(viewRow);
                    Object idValue = tableModel.getValueAt(modelRow, 0);

                    if (idValue != null) {
                        Equipment item = findEquipmentById(idValue.toString());
                        if (item != null) {
                            selectedEquipment = item;
                            showAddDialog(true);
                        }
                    }
                }
            }
        });
    }

    // ==================== SHOW ADD / UPDATE EQUIPMENT DIALOG ====================

    private void showAddDialog(boolean isUpdate) {

        if (isUpdate) {
            if (selectedEquipment == null) {
                return;
            }

            txtId.setText(selectedEquipment.id);
            txtName.setText(selectedEquipment.name);
            cmbCategory.setSelectedItem(selectedEquipment.category);
            txtDescription.setText(selectedEquipment.description);
            cmbStatus.setSelectedItem(selectedEquipment.status);
            cmbLocation.setSelectedItem(selectedEquipment.location);
            txtUpdatedDate.setText(selectedEquipment.updatedDate);
            txtAddedBy.setText(selectedEquipment.addedBy);

        } else {
            selectedEquipment = null;
            txtId.setText("");
            txtName.setText("");
            cmbCategory.setSelectedIndex(0);
            txtDescription.setText("");
            cmbStatus.setSelectedItem("Available");
            cmbLocation.setSelectedIndex(0);
            txtUpdatedDate.setText(LocalDate.now().toString());
            txtAddedBy.setText("");
        }

        addDialog = new JDialog(
                this,
                isUpdate ? "Update Equipment" : "Add Equipment",
                true
        );
        addDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addDialog.setLayout(new BorderLayout(10, 10));

        JPanel dialogButtonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton btnSave = new JButton(isUpdate ? "Update" : "Save");
        JButton btnDelete = new JButton("Delete");

        if (isUpdate) {
            dialogButtonPanel.add(btnDelete);
        }

        dialogButtonPanel.add(btnSave);

        lblNumberOfItems.setVisible(!isUpdate);
        spnNumberOfItems.setVisible(!isUpdate);
        if (!isUpdate) {
            spnNumberOfItems.setValue(1);
        }

        addDialog.add(formPanel, BorderLayout.CENTER);
        addDialog.add(dialogButtonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (isUpdate) {
                updateEquipment();
            } else {
                addEquipment();
            }
        });

        btnDelete.addActionListener(e -> {
            if (isUpdate) {
                deleteEquipment();
            }
        });

        addDialog.pack();
        addDialog.setMinimumSize(new Dimension(450, 480));
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
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

    private void addFormComboField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComboBox<String> comboBox) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(comboBox, gbc);
    }

    // ==================== LOCATION DROPDOWN ====================

    private JComboBox<String> createLocationComboBox() {

        String[] locations = new String[45];
        int index = 0;

        for (char section = 'A'; section <= 'E'; section++) {

            for (int row = 1; row <= 3; row++) {

                for (int shelf = 1; shelf <= 3; shelf++) {

                    locations[index] =
                            section + "" + row + "-" + shelf;

                    index++;
                }
            }
        }

        return new JComboBox<>(locations);
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

                String cleanId = item.id.replace("E", "");
                int currentId = Integer.parseInt(cleanId);

                if (currentId >= nextId) {
                    nextId = currentId + 1;
                }

            } catch (NumberFormatException ex) {
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

        return String.format("E%03d", nextId);
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

        int numberOfItems = 1;

        if (spnNumberOfItems != null) {
            numberOfItems = (Integer) spnNumberOfItems.getValue();
        }

        List<Equipment> newlyAddedItems = new ArrayList<>();

        for (int i = 0; i < numberOfItems; i++) {

            String id = generateEquipmentId();

            Equipment item = new Equipment(
                    id,
                    txtName.getText().trim(),
                    cmbCategory.getSelectedItem().toString(),
                    txtDescription.getText().trim(),
                    1,
                    cmbStatus.getSelectedItem().toString(),
                    cmbLocation.getSelectedItem().toString(),
                    LocalDate.now().toString(),
                    txtAddedBy.getText().trim()
            );

            equipmentList.add(item);
            newlyAddedItems.add(item);
        }

        if (saveToFile()) {

            applyFilters();
            clearForm();

            if (addDialog != null && addDialog.isVisible()) {
                addDialog.dispose();
            }

            selectedEquipment = null;

            lblMessage.setText(
                    numberOfItems + " equipment record(s) added successfully."
            );

            String firstId = newlyAddedItems.get(0).id;
            String lastId = newlyAddedItems.get(newlyAddedItems.size() - 1).id;

            JOptionPane.showMessageDialog(
                    this,
                    numberOfItems + " equipment record(s) added successfully!\n"
            );

        } else {

            equipmentList.removeAll(newlyAddedItems);
        }
    }

    // ==================== DISPLAY ALL RECORDS ====================

    private void displayAllRecords() {

        selectedEquipment = null;
        tableModel.setRowCount(0);

        for (Equipment item : equipmentList) {
            addTableRow(item);
        }

        lblMessage.setText("");
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

    // ==================== SEARCH AND FILTER EQUIPMENT ====================

    private void searchEquipment() {
        applyFilters();
    }

    private void applyFilters() {

        String keyword = txtSearch.getText().trim().toLowerCase();

        selectedEquipment = null;
        tableModel.setRowCount(0);

        for (Equipment item : equipmentList) {

            boolean matchesSearch =
                    keyword.isEmpty()
                    || item.id.toLowerCase().contains(keyword)
                    || item.name.toLowerCase().contains(keyword);

            boolean matchesCategory =
                    selectedCategoryFilter.equals("All")
                    || item.category.equalsIgnoreCase(selectedCategoryFilter);

            boolean matchesStatus =
                    selectedStatusFilter.equals("All")
                    || item.status.equalsIgnoreCase(selectedStatusFilter);

            boolean matchesLocation =
                    selectedLocationFilter.equals("All")
                    || item.location.equalsIgnoreCase(selectedLocationFilter);

            if (matchesSearch && matchesCategory
                    && matchesStatus && matchesLocation) {
                addTableRow(item);
            }
        }

        lblMessage.setText(
                "Matching records: " + tableModel.getRowCount()
        );
    }

    // ==================== FILTER DIALOG ====================

    private void showFilterDialog() {

        JDialog filterDialog = new JDialog(
                this,
                "Filter Equipment",
                true
        );

        filterDialog.setLayout(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        String[] categoryOptions = {
            "All", "Clothes", "Protection", "Training"
        };
        JComboBox<String> filterCategory =
                new JComboBox<>(categoryOptions);
        filterCategory.setSelectedItem(selectedCategoryFilter);

        String[] statusOptions = {
            "All", "Available", "Unavailable"
        };
        JComboBox<String> filterStatus =
                new JComboBox<>(statusOptions);
        filterStatus.setSelectedItem(selectedStatusFilter);

        JComboBox<String> filterLocation = new JComboBox<>();
        filterLocation.addItem("All");

        for (int i = 0; i < cmbLocation.getItemCount(); i++) {
            filterLocation.addItem(cmbLocation.getItemAt(i));
        }
        filterLocation.setSelectedItem(selectedLocationFilter);

        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(filterCategory);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(filterStatus);

        filterPanel.add(new JLabel("Location:"));
        filterPanel.add(filterLocation);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton btnApply = new JButton("Apply");
        JButton btnReset = new JButton("Reset");

        buttonPanel.add(btnApply);
        buttonPanel.add(btnReset);

        filterDialog.add(filterPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);

        btnApply.addActionListener(e -> {

            selectedCategoryFilter =
                    filterCategory.getSelectedItem().toString();

            selectedStatusFilter =
                    filterStatus.getSelectedItem().toString();

            selectedLocationFilter =
                    filterLocation.getSelectedItem().toString();

            applyFilters();
            filterDialog.dispose();
        });

        btnReset.addActionListener(e -> {

            selectedCategoryFilter = "All";
            selectedStatusFilter = "All";
            selectedLocationFilter = "All";

            applyFilters();
            filterDialog.dispose();
        });

        filterDialog.pack();
        filterDialog.setMinimumSize(new Dimension(350, 220));
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    // ==================== UPDATE EQUIPMENT ====================

    private void updateEquipment() {

        if (selectedEquipment == null) {
            JOptionPane.showMessageDialog(this, "Please select an equipment record.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        Equipment backup = copyEquipment(selectedEquipment);

        selectedEquipment.name = txtName.getText().trim();
        selectedEquipment.category = cmbCategory.getSelectedItem().toString();
        selectedEquipment.description = txtDescription.getText().trim();
        selectedEquipment.quantity = 1;
        selectedEquipment.status = cmbStatus.getSelectedItem().toString();
        selectedEquipment.location = cmbLocation.getSelectedItem().toString();
        selectedEquipment.updatedDate = LocalDate.now().toString();
        selectedEquipment.addedBy = txtAddedBy.getText().trim();

        if (saveToFile()) {
            applyFilters();

            if (addDialog != null && addDialog.isVisible()) {
                addDialog.dispose();
            }
            selectedEquipment = null;
            lblMessage.setText("Equipment updated successfully.");
            JOptionPane.showMessageDialog(this, "Equipment updated successfully!");
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
            JOptionPane.showMessageDialog(this, "Please select an equipment record to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                addDialog,
                "Delete this equipment?\nID: " + selectedEquipment.id
                        + "\nName: " + selectedEquipment.name,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Equipment itemToDelete = selectedEquipment;
        int oldIndex = equipmentList.indexOf(itemToDelete);
        equipmentList.remove(itemToDelete);

        if (saveToFile()) {
            applyFilters();

            if (addDialog != null && addDialog.isVisible()) {
                addDialog.dispose();
            }
            selectedEquipment = null;
            lblMessage.setText("Equipment deleted successfully.");
            JOptionPane.showMessageDialog(this, "Equipment deleted successfully!");
        } else {
            if (oldIndex >= 0 && oldIndex <= equipmentList.size()) {
                equipmentList.add(oldIndex, itemToDelete);
            } else {
                equipmentList.add(itemToDelete);
            }
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
    
    // ==================== CLEAR FORM ====================

    private void clearForm() {

        equipmentTable.clearSelection();
        selectedEquipment = null;

        txtId.setText("");
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtDescription.setText("");
        cmbStatus.setSelectedItem("Available");
        cmbLocation.setSelectedIndex(0);
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
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            1,
                            data[5],
                            data[6],
                            data[7],
                            data[8]
                    );

                    equipmentList.add(item);

                } catch (IllegalArgumentException ex) {
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

                    writer.write(data[i]);
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

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            EquipmentInventoryManagement app =
                    new EquipmentInventoryManagement();

            app.setVisible(true);
        });
    }
}
