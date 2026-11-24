
package com.mycompany.group28assignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.swing.table.DefaultTableCellRenderer;

public class EquipmentInventoryStockReport extends JPanel {

    private final MainFrame mainFrame;

    private final Path equipmentFile =
            Paths.get("data", "equipment.txt");

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);

    private DefaultTableModel summaryModel;
    private DefaultTableModel detailModel;

    private JTable summaryTable;
    private JTable detailTable;

    private JTextField txtSearch;

    private JLabel lblTotalQuantity;
    private JLabel lblTotalTypes;
    private JLabel lblAvailable;
    private JLabel lblUnavailable;
    private JLabel lblMessage;
    private String activeStatusFilter = "";

    private final java.util.List<Object[]> allRecords =
            new ArrayList<>();

    private final Map<String, java.util.List<Object[]>> groupedRecords =
            new LinkedHashMap<>();

    private final Map<String, String> displayNames =
            new LinkedHashMap<>();

    public EquipmentInventoryStockReport(MainFrame mainFrame) {

        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        initializeGUI();
        loadEquipmentData();
    }

    private void initializeGUI() {

        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));

        JButton btnBack = new JButton("Back");

        btnBack.addActionListener(e ->
                mainFrame.showPanel("Facility Officer")
        );

        JLabel title = new JLabel(
                "Equipment Stock Report",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        topPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        reportPanel.add(topPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 4, 10, 10)
        );

        summaryPanel.setBorder(
                BorderFactory.createTitledBorder("Stock Summary")
        );

        lblTotalQuantity = new JLabel("0", SwingConstants.CENTER);
        lblTotalTypes = new JLabel("0", SwingConstants.CENTER);
        lblAvailable = new JLabel("0", SwingConstants.CENTER);
        lblUnavailable = new JLabel("0", SwingConstants.CENTER);

        summaryPanel.add(createSummaryCard(
                "Total Stock Quantity", lblTotalQuantity,
                Color.WHITE,
                Color.BLACK
                ));

        summaryPanel.add(createSummaryCard(
                "Equipment Types", lblTotalTypes,
                Color.WHITE,
                Color.BLACK
        ));

        JPanel availableCard = createSummaryCard(
                "Available Stock",
                lblAvailable,
                new Color(220, 252, 231),
                new Color(22, 101, 52)
        );

        makeCardClickable(availableCard, () -> {

            if (activeStatusFilter.equals("Available")) {
                activeStatusFilter = "";
            } else {
                activeStatusFilter = "Available";
            }

            displayGroupedRecords(txtSearch.getText().trim());

            lblMessage.setText(
                    activeStatusFilter.isEmpty()
                    ? "Showing all equipment stock."
                    : "Showing Available equipment only."
            );
        });

        summaryPanel.add(availableCard);

        JPanel unavailableCard = createSummaryCard(
                "Unavailable Stock",
                lblUnavailable,
                new Color(254, 226, 226),
                new Color(153, 27, 27)
        );

        makeCardClickable(unavailableCard, () -> {

            if (activeStatusFilter.equals("Unavailable")) {
                activeStatusFilter = "";
            } else {
                activeStatusFilter = "Unavailable";
            }

            displayGroupedRecords(txtSearch.getText().trim());

            lblMessage.setText(
                    activeStatusFilter.isEmpty()
                    ? "Showing all equipment stock."
                    : "Showing Unavailable equipment only."
            );
        });

        summaryPanel.add(unavailableCard);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        txtSearch = new JTextField(20);

        JButton btnSearch = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh");

        searchPanel.add(
                new JLabel("Search by Equipment ID or Name:")
        );

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        JPanel upperPanel = new JPanel(new BorderLayout(5, 5));

        upperPanel.add(summaryPanel, BorderLayout.NORTH);
        upperPanel.add(searchPanel, BorderLayout.SOUTH);

        String[] summaryColumns = {
            "Equipment Name",
            "Total Quantity"
        };

        summaryModel = new DefaultTableModel(
                summaryColumns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        summaryTable = new JTable(summaryModel);

        summaryTable.setRowHeight(30);
        summaryTable.setFont(new Font("Arial", Font.PLAIN, 14));

        summaryTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(300);

        summaryTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(150);

        summaryTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane summaryScroll = new JScrollPane(summaryTable);

        summaryScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Equipment Stock Summary - Double Click to View Details"
                )
        );

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        );

        centerPanel.add(upperPanel, BorderLayout.NORTH);
        centerPanel.add(summaryScroll, BorderLayout.CENTER);

        reportPanel.add(centerPanel, BorderLayout.CENTER);

        lblMessage = new JLabel("Ready");

        lblMessage.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10)
        );

        reportPanel.add(lblMessage, BorderLayout.SOUTH);

        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));

        JButton btnDetailBack = new JButton("Back");

        btnDetailBack.addActionListener(e ->
                pageLayout.show(pageContainer, "Report")
        );

        JLabel detailTitle = new JLabel(
                "Equipment Details",
                SwingConstants.CENTER
        );

        detailTitle.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel detailTopPanel = new JPanel(new BorderLayout());

        detailTopPanel.add(btnDetailBack, BorderLayout.WEST);
        detailTopPanel.add(detailTitle, BorderLayout.CENTER);

        detailTopPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        detailPanel.add(detailTopPanel, BorderLayout.NORTH);

        String[] detailColumns = {
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

        detailModel = new DefaultTableModel(
                detailColumns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(detailModel);

        detailTable.setDefaultRenderer(
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
                        table.getValueAt(row, 5)
                ).trim();

                if (isSelected) {

                    component.setBackground(
                            table.getSelectionBackground()
                    );

                    component.setForeground(
                            table.getSelectionForeground()
                    );

                } else if (status.equalsIgnoreCase("Available")) {

                    component.setBackground(
                            new Color(220, 252, 231)
                    );

                    component.setForeground(
                            new Color(22, 101, 52)
                    );

                } else if (status.equalsIgnoreCase("Unavailable")) {

                    component.setBackground(
                            new Color(254, 226, 226)
                    );

                    component.setForeground(
                            new Color(153, 27, 27)
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

        detailTable.setRowHeight(25);
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int[] widths = {
            100, 160, 110, 200, 80, 110, 100, 110, 120
        };

        for (int i = 0; i < widths.length; i++) {
            detailTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane detailScroll = new JScrollPane(detailTable);

        detailScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Equipment Records"
                )
        );

        JPanel detailCenter = new JPanel(new BorderLayout());

        detailCenter.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10)
        );

        detailCenter.add(detailScroll, BorderLayout.CENTER);

        detailPanel.add(detailCenter, BorderLayout.CENTER);

        pageContainer.add(reportPanel, "Report");
        pageContainer.add(detailPanel, "Details");

        add(pageContainer, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchEquipment());

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

        btnRefresh.addActionListener(e -> {
            activeStatusFilter = "";
            txtSearch.setText("");
            loadEquipmentData();
        });
        
        summaryTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int selectedRow =
                            summaryTable.getSelectedRow();

                    if (selectedRow != -1) {

                        String equipmentName =
                                summaryModel.getValueAt(
                                        selectedRow, 0
                                ).toString();

                        showEquipmentDetails(equipmentName);
                    }
                }
            }
        });
    }

    private JPanel createSummaryCard(
            String title,
            JLabel valueLabel,
            Color backgroundColor,
            Color textColor) {

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.setBackground(backgroundColor);

        panel.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        titleLabel.setForeground(textColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(backgroundColor);

        valueLabel.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        valueLabel.setForeground(textColor);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(backgroundColor);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
    
    private void makeCardClickable(
            JPanel card,
            Runnable action) {

        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        };

        card.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        card.addMouseListener(mouseAdapter);

        for (Component component : card.getComponents()) {

            component.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            component.addMouseListener(mouseAdapter);
        }
    }

    private void loadEquipmentData() {

        allRecords.clear();
        groupedRecords.clear();
        displayNames.clear();

        summaryModel.setRowCount(0);

        int totalQuantity = 0;
        int availableQuantity = 0;
        int unavailableQuantity = 0;

        Set<String> equipmentTypes = new HashSet<>();

        try {

            if (!Files.exists(equipmentFile)) {

                updateSummary(0, 0, 0, 0);

                lblMessage.setText(
                        "equipment.txt was not found."
                );

                JOptionPane.showMessageDialog(
                        this,
                        "equipment.txt was not found!",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            for (String line : Files.readAllLines(
                    equipmentFile,
                    StandardCharsets.UTF_8)) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length < 9) {
                    continue;
                }

                String id = data[0].trim();
                String name = data[1].trim();
                String status = data[5].trim();

                if (name.isEmpty()) {
                    continue;
                }

                int quantity;

                try {
                    quantity = Integer.parseInt(
                            data[4].trim()
                    );
                } catch (NumberFormatException ex) {
                    quantity = 0;
                }

                Object[] record = {
                    id,
                    name,
                    data[2].trim(),
                    data[3].trim(),
                    quantity,
                    status,
                    data[6].trim(),
                    data[7].trim(),
                    data[8].trim()
                };

                allRecords.add(record);

                totalQuantity += quantity;

                if (status.equalsIgnoreCase("Available")) {
                    availableQuantity += quantity;
                } else if (status.equalsIgnoreCase("Unavailable")) {
                    unavailableQuantity += quantity;
                }

                equipmentTypes.add(name.toLowerCase(Locale.ROOT));

                String groupKey = name.toLowerCase(Locale.ROOT);

                groupedRecords.putIfAbsent(
                        groupKey, new ArrayList<>()
                );

                groupedRecords.get(groupKey).add(record);

                displayNames.putIfAbsent(groupKey, name);
            }

            updateSummary(
                    totalQuantity,
                    equipmentTypes.size(),
                    availableQuantity,
                    unavailableQuantity
            );

            displayGroupedRecords("");

            lblMessage.setText(
                    "Successfully loaded "
                    + allRecords.size()
                    + " equipment records."
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error reading equipment.txt:\n"
                    + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );

            lblMessage.setText(
                    "Error loading equipment records."
            );
        }
    }

    private void displayGroupedRecords(String keyword) {

        summaryModel.setRowCount(0);

        String search = keyword.toLowerCase(Locale.ROOT);

        for (String groupKey : groupedRecords.keySet()) {

            java.util.List<Object[]> records =
                    groupedRecords.get(groupKey);

            String equipmentName =
                    displayNames.get(groupKey);

            int total = 0;
            boolean matched = false;

            for (Object[] record : records) {

                int quantity = (int) record[4];

                String id = record[0].toString();

                String status = record[5].toString().trim();

                boolean statusMatched =
                        activeStatusFilter.isEmpty()
                        || status.equalsIgnoreCase(activeStatusFilter);

                boolean searchMatched =
                        search.isEmpty()
                        || id.toLowerCase(Locale.ROOT).contains(search)
                        || equipmentName.toLowerCase(Locale.ROOT)
                                .contains(search);

                if (statusMatched && searchMatched) {

                    total += quantity;
                    matched = true;
                }
            }

            if (matched) {

                summaryModel.addRow(new Object[]{
                    equipmentName,
                    total
                });
            }
        }
    }

    private void searchEquipment() {

        String keyword = txtSearch.getText().trim();

        displayGroupedRecords(keyword);

        if (keyword.isEmpty()) {

            lblMessage.setText(
                    "Showing all equipment stock groups."
            );

        } else {

            lblMessage.setText(
                    summaryModel.getRowCount()
                    + " matching equipment type(s) found."
            );
        }
    }
    
    private void showEquipmentDetails(String equipmentName) {

        detailModel.setRowCount(0);

        String groupKey =
                equipmentName.toLowerCase(Locale.ROOT);

        java.util.List<Object[]> records =
                groupedRecords.get(groupKey);

        if (records == null) {
            return;
        }

        for (Object[] record : records) {

            String status = record[5].toString().trim();

            if (activeStatusFilter.isEmpty()
                    || status.equalsIgnoreCase(activeStatusFilter)) {

                detailModel.addRow(record);
            }
        }

        pageLayout.show(pageContainer, "Details");
    }

    private void updateSummary(
            int total,
            int types,
            int available,
            int unavailable) {

        lblTotalQuantity.setText(
                String.valueOf(total)
        );

        lblTotalTypes.setText(
                String.valueOf(types)
        );

        lblAvailable.setText(
                String.valueOf(available)
        );

        lblUnavailable.setText(
                String.valueOf(unavailable)
        );
    }
}
