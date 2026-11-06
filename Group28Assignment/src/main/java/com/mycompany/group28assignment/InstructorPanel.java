
package com.mycompany.group28assignment;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class InstructorPanel extends JPanel {

    private final Path dataDir = Paths.get("data");
    private final Path schedules = dataDir.resolve("schedules.txt");
    private final Path problems = dataDir.resolve("facility_problems.txt");
    private final Path ratings = dataDir.resolve("ratings.txt");

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private static final Color BLUE = new Color(42, 93, 151);
    private static final Color HOVER = new Color(65, 125, 190);
    private static final Color GREEN = new Color(220, 245, 224);
    private static final Color RED = new Color(255, 225, 225);

    private final String[] scheduleColumns = {
        "Class ID", "Class Name", "Discipline",
        "Date", "Time", "Capacity", "Status"
    };

    private final String[] problemColumns = {
        "Problem ID", "Facility", "Description",
        "Date", "Status"
    };

    private final String[] ratingColumns = {
        "Rating ID", "Student", "Instructor",
        "Class", "Rating", "Comment"
    };

    private final Runnable logoutAction;

    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);

    private int section = 0;

    private final JTextField searchField = new JTextField(18);
    private final JLabel countLabel = new JLabel("Showing: 0 / 0");
    private final JLabel sectionTitle = new JLabel();

    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton("Update");
    private final JButton cancelButton = new JButton("Cancel Class");
    private final JButton deleteButton = new JButton();
    private final JButton backButton = new JButton("Back to Menu");

    private final DefaultTableModel model =
            new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

    private final List<String[]> records = new ArrayList<>();

    public InstructorPanel() {
        this(null);
    }

    public InstructorPanel(Runnable logoutAction) {

        this.logoutAction = logoutAction;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pages.add(createMenu(), "MENU");
        pages.add(createWorkspace(), "WORKSPACE");

        add(pages, BorderLayout.CENTER);

        cards.show(pages, "MENU");
    }

    private JPanel createMenu() {

        JPanel menu = new JPanel(new BorderLayout(15, 25));

        JLabel title = new JLabel(
            "Instructor Dashboard",
            SwingConstants.CENTER
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        menu.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(
            new GridLayout(4, 1, 0, 15)
        );

        buttonPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 65, 25, 65)
        );

        JButton schedulesButton =
            new JButton("Class Schedules");

        JButton problemsButton =
            new JButton("Facility Problems");

        JButton ratingsButton =
            new JButton("Instructor Ratings");

        JButton logoutButton =
            new JButton("Log Out");

        JButton[] buttons = {
            schedulesButton,
            problemsButton,
            ratingsButton,
            logoutButton
        };

        for (JButton button : buttons) {
            styleButton(button);
            button.setFont(
                new Font("SansSerif", Font.BOLD, 17)
            );
            buttonPanel.add(button);
        }

        schedulesButton.addActionListener(e -> openSection(0));
        problemsButton.addActionListener(e -> openSection(1));
        ratingsButton.addActionListener(e -> openSection(2));

        logoutButton.addActionListener(e -> {
            if (logoutAction != null) {
                logoutAction.run();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Log Out is available from MainFrame."
                );
            }
        });

        menu.add(buttonPanel, BorderLayout.CENTER);

        return menu;
    }

    private JPanel createWorkspace() {

        JPanel workspace = new JPanel(
            new BorderLayout(10, 10)
        );

        JLabel heading = new JLabel(
            "Instructor Dashboard",
            SwingConstants.CENTER
        );

        heading.setFont(
            new Font("SansSerif", Font.BOLD, 22)
        );

        workspace.add(heading, BorderLayout.NORTH);

        JPanel main = new JPanel(
            new BorderLayout(10, 10)
        );

        JPanel top = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 8)
        );

        styleButton(backButton);
        backButton.addActionListener(e ->
            cards.show(pages, "MENU")
        );

        sectionTitle.setFont(
            new Font("SansSerif", Font.BOLD, 17)
        );

        top.add(backButton);
        top.add(Box.createHorizontalStrut(10));
        top.add(sectionTitle);
        top.add(Box.createHorizontalStrut(15));
        top.add(new JLabel("Search ID / Name:"));
        top.add(searchField);

        main.add(top, BorderLayout.NORTH);

        table.setRowHeight(30);
        table.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(
            Object.class,
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t,
                        Object value,
                        boolean selected,
                        boolean focused,
                        int row,
                        int column) {

                    Component component =
                        super.getTableCellRendererComponent(
                            t, value, selected,
                            focused, row, column
                        );

                    if (!selected) {

                        component.setForeground(Color.BLACK);
                        component.setBackground(Color.WHITE);

                        if (section == 0) {

                            int modelRow =
                                t.convertRowIndexToModel(row);

                            String status = String.valueOf(
                                model.getValueAt(modelRow, 6)
                            );

                            if ("Available".equalsIgnoreCase(status)) {
                                component.setBackground(GREEN);
                            } else if ("Cancelled".equalsIgnoreCase(status)) {
                                component.setBackground(RED);
                            }
                        }
                    }

                    return component;
                }
            }
        );

        main.add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        JPanel bottom = new JPanel(new BorderLayout());

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        JButton[] actionButtons = {
            addButton,
            editButton,
            cancelButton,
            deleteButton
        };

        for (JButton button : actionButtons) {
            styleButton(button);
            actions.add(button);
        }

        bottom.add(actions, BorderLayout.WEST);
        bottom.add(countLabel, BorderLayout.EAST);

        main.add(bottom, BorderLayout.SOUTH);
        workspace.add(main, BorderLayout.CENTER);

        addButton.addActionListener(e -> addRecord());
        editButton.addActionListener(e -> editRecord());
        cancelButton.addActionListener(e -> cancelClass());
        deleteButton.addActionListener(e -> deleteRecord());

        searchField.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    filter();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filter();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    filter();
                }
            }
        );

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2
                        && table.getSelectedRow() >= 0) {

                    if (section == 2) {
                        viewRatingDetails();
                    } else {
                        editRecord();
                    }
                }
            }
        });

        return workspace;
    }

    private void styleButton(JButton button) {

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createEmptyBorder(
                12, 16, 12, 16
            )
        );

        button.setCursor(
            Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
            )
        );

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BLUE);
            }
        });
    }

    private void openSection(int selectedSection) {

        section = selectedSection;

        loadRecords();

        cards.show(pages, "WORKSPACE");
    }

    private Path currentFile() {

        if (section == 0) {
            return schedules;
        }

        if (section == 1) {
            return problems;
        }

        return ratings;
    }

    private String[] columns() {

        if (section == 0) {
            return scheduleColumns;
        }

        if (section == 1) {
            return problemColumns;
        }

        return ratingColumns;
    }

    private void updateButtons() {

        if (section == 0) {

            sectionTitle.setText("Class Schedules");

            addButton.setText("Add Class");
            deleteButton.setText("Delete Class");

        } else if (section == 1) {

            sectionTitle.setText("Facility Problems");

            addButton.setText("Add Problem");
            deleteButton.setText("Delete Problem");

        } else {

            sectionTitle.setText("Instructor Ratings");
        }

        boolean editable = section != 2;

        addButton.setVisible(editable);
        editButton.setVisible(editable);
        deleteButton.setVisible(editable);

        cancelButton.setVisible(section == 0);
    }

    private void loadRecords() {

        records.clear();
        model.setRowCount(0);
        model.setColumnIdentifiers(columns());

        searchField.setText("");

        updateButtons();

        try {

            Files.createDirectories(dataDir);

            if (Files.exists(currentFile())) {

                List<String> lines = Files.readAllLines(
                    currentFile(),
                    StandardCharsets.UTF_8
                );

                int number = 1;

                for (String line : lines) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts =
                        line.split("\\s*\\|\\s*", -1);

                    String[] record =
                        normalize(parts, number);

                    if (record != null) {
                        records.add(record);
                    }

                    number++;
                }
            }

            displayRecords();

        } catch (IOException ex) {

            showError(
                "Cannot load records: " + ex.getMessage()
            );
        }
    }

    private String[] normalize(String[] p, int number) {

        if (section == 0) {

            if (p.length >= 7) {
                return Arrays.copyOf(p, 7);
            }

            if (p.length == 3) {

                return new String[]{
                    String.format("C%03d", number),
                    p[0],
                    "General",
                    p[1],
                    p[2],
                    "20",
                    "Available"
                };
            }
        }

        if (section == 1) {

            if (p.length >= 5) {
                return Arrays.copyOf(p, 5);
            }

            if (p.length == 3) {

                return new String[]{
                    String.format("P%03d", number),
                    p[0],
                    p[1],
                    p[2],
                    "Open"
                };
            }
        }

        if (section == 2) {

            if (p.length == 6) {
                return Arrays.copyOf(p, 6);
            }

            String[] result = new String[6];
            Arrays.fill(result, "");

            result[0] =
                String.format("R%03d", number);

            int limit = Math.min(p.length, 5);

            for (int i = 0; i < limit; i++) {
                result[i + 1] = p[i];
            }

            return result;
        }

        return null;
    }

    private void displayRecords() {

        model.setRowCount(0);

        for (String[] record : records) {

            String[] display = record.clone();

            if (section == 0 || section == 1) {
                display[3] = formatDate(record[3]);
            }

            model.addRow(display);
        }

        filter();
    }

    private String formatDate(String value) {

        try {

            return LocalDate.parse(value)
                    .format(DISPLAY_DATE);

        } catch (Exception ex) {

            return value;
        }
    }

    private void filter() {

        String keyword =
            searchField.getText().trim();

        if (keyword.isEmpty()) {

            sorter.setRowFilter(null);

        } else {

            sorter.setRowFilter(
                RowFilter.regexFilter(
                    "(?i)"
                    + java.util.regex.Pattern.quote(keyword)
                )
            );
        }

        countLabel.setText(
            "Showing: " + table.getRowCount()
            + " / " + records.size()
        );
    }

    private String nextID() {

        String prefix =
            section == 0 ? "C" : "P";

        int max = 0;

        for (String[] record : records) {

            String id = record[0];

            if (id.matches(prefix + "\\d+")) {

                try {

                    int number =
                        Integer.parseInt(id.substring(1));

                    max = Math.max(max, number);

                } catch (NumberFormatException ignored) {
                }
            }
        }

        return String.format(
            "%s%03d",
            prefix,
            max + 1
        );
    }

    private JSpinner createDateSpinner(String value) {

        Date initialDate = new Date();

        if (value != null && !value.isBlank()) {

            try {

                LocalDate date =
                    LocalDate.parse(value);

                initialDate = Date.from(
                    date.atStartOfDay(
                        ZoneId.systemDefault()
                    ).toInstant()
                );

            } catch (Exception ignored) {
            }
        }

        SpinnerDateModel dateModel =
            new SpinnerDateModel(
                initialDate,
                null,
                null,
                Calendar.DAY_OF_MONTH
            );

        JSpinner spinner =
            new JSpinner(dateModel);

        spinner.setEditor(
            new JSpinner.DateEditor(
                spinner,
                "dd-MM-yyyy"
            )
        );

        return spinner;
    }

    private JSpinner createTimeSpinner(String value) {

        Date initialDate = new Date();

        if (value != null && !value.isBlank()) {

            try {

                LocalTime time =
                    LocalTime.parse(value);

                LocalDateTime dateTime =
                    LocalDate.now().atTime(time);

                initialDate = Date.from(
                    dateTime.atZone(
                        ZoneId.systemDefault()
                    ).toInstant()
                );

            } catch (Exception ignored) {
            }
        }

        SpinnerDateModel timeModel =
            new SpinnerDateModel(
                initialDate,
                null,
                null,
                Calendar.MINUTE
            );

        JSpinner spinner =
            new JSpinner(timeModel);

        spinner.setEditor(
            new JSpinner.DateEditor(
                spinner,
                "HH:mm"
            )
        );

        return spinner;
    }

    private String[] showForm(String[] existing) {

        String[] labels = columns();

        JPanel form = new JPanel(
            new GridLayout(
                labels.length,
                2,
                8,
                8
            )
        );

        JComponent[] inputs =
            new JComponent[labels.length];

        for (int i = 0; i < labels.length; i++) {

            String value =
                existing == null ? "" : existing[i];

            if (i == 0) {

                JTextField idField =
                    new JTextField(
                        existing == null
                            ? nextID()
                            : value,
                        20
                    );

                idField.setEditable(false);
                inputs[i] = idField;

            } else if (i == 3) {

                inputs[i] =
                    createDateSpinner(value);

            } else if (section == 0 && i == 4) {

                inputs[i] =
                    createTimeSpinner(value);

            } else if (section == 0 && i == 6) {

                JComboBox<String> status =
                    new JComboBox<>(
                        new String[]{
                            "Available",
                            "Cancelled"
                        }
                    );

                status.setSelectedItem(
                    existing == null
                        ? "Available"
                        : value
                );

                inputs[i] = status;

            } else if (section == 1 && i == 4) {

                JComboBox<String> status =
                    new JComboBox<>(
                        new String[]{
                            "Open",
                            "In Progress",
                            "Resolved"
                        }
                    );

                status.setSelectedItem(
                    existing == null
                        ? "Open"
                        : value
                );

                inputs[i] = status;

            } else {

                if (existing == null
                        && section == 0
                        && i == 5) {

                    value = "20";
                }

                inputs[i] =
                    new JTextField(value, 20);
            }

            form.add(
                new JLabel(labels[i] + ":")
            );

            form.add(inputs[i]);
        }

        Object[] options =
            existing == null
                ? new Object[]{"Save", "Cancel"}
                : new Object[]{"Save", "Delete", "Cancel"};

        while (true) {

            int answer = JOptionPane.showOptionDialog(
                this,
                form,
                existing == null
                    ? "Add New Record"
                    : "Update Record",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
            );

            if (answer < 0
                    || answer == options.length - 1) {

                return null;
            }

            if (existing != null && answer == 1) {

                int confirm =
                    JOptionPane.showConfirmDialog(
                        this,
                        "Permanently delete "
                        + existing[0] + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                    );

                if (confirm == JOptionPane.YES_OPTION) {

                    int index =
                        records.indexOf(existing);

                    if (index >= 0) {
                        records.remove(index);
                        saveRecords();
                    }

                    return null;
                }

                continue;
            }

            String[] values =
                new String[labels.length];

            try {

                for (int i = 0; i < labels.length; i++) {

                    if (inputs[i] instanceof JSpinner) {

                        JSpinner spinner =
                            (JSpinner) inputs[i];

                        spinner.commitEdit();

                        Date date =
                            (Date) spinner.getValue();

                        if (i == 3) {

                            values[i] = date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString();

                        } else {

                            values[i] = date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalTime()
                                .format(
                                    DateTimeFormatter.ofPattern(
                                        "HH:mm"
                                    )
                                );
                        }

                    } else if (inputs[i] instanceof JComboBox) {

                        values[i] = String.valueOf(
                            ((JComboBox<?>) inputs[i])
                                .getSelectedItem()
                        );

                    } else {

                        values[i] =
                            ((JTextField) inputs[i])
                                .getText()
                                .trim();
                    }

                    if (values[i].isEmpty()
                            || values[i].contains("|")
                            || values[i].contains("\n")
                            || values[i].contains("\r")) {

                        throw new IllegalArgumentException(
                            "Complete all fields. Do not use |."
                        );
                    }
                }

                LocalDate.parse(values[3]);

                if (section == 0) {

                    LocalTime.parse(values[4]);

                    int capacity =
                        Integer.parseInt(values[5]);

                    if (capacity <= 0) {

                        throw new IllegalArgumentException(
                            "Capacity must be positive."
                        );
                    }
                }

                return values;

            } catch (Exception ex) {

                showError(
                    ex.getMessage() == null
                        ? "Please check the input values."
                        : ex.getMessage()
                );
            }
        }
    }

    private void addRecord() {

        if (section == 2) {
            return;
        }

        String[] result = showForm(null);

        if (result != null) {

            records.add(result);
            saveRecords();
        }
    }

    private int selectedIndex() {

        int viewRow =
            table.getSelectedRow();

        if (viewRow < 0) {

            showError(
                "Please select a row first."
            );

            return -1;
        }

        return table.convertRowIndexToModel(
            viewRow
        );
    }

    private void editRecord() {

        if (section == 2) {
            return;
        }

        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String[] original =
            records.get(index);

        String[] updated =
            showForm(original);

        if (updated != null) {

            records.set(index, updated);
            saveRecords();
        }
    }

    private void cancelClass() {

        if (section != 0) {
            return;
        }

        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String[] record =
            records.get(index);

        if ("Cancelled".equalsIgnoreCase(record[6])) {

            showError(
                "This class is already cancelled."
            );

            return;
        }

        int answer =
            JOptionPane.showConfirmDialog(
                this,
                "Cancel class " + record[0] + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
            );

        if (answer == JOptionPane.YES_OPTION) {

            record[6] = "Cancelled";
            saveRecords();
        }
    }

    private void deleteRecord() {

        if (section == 2) {
            return;
        }

        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String id =
            records.get(index)[0];

        int answer =
            JOptionPane.showConfirmDialog(
                this,
                "Permanently delete " + id + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );

        if (answer == JOptionPane.YES_OPTION) {

            records.remove(index);
            saveRecords();
        }
    }

    private void viewRatingDetails() {

        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String[] record =
            records.get(index);

        StringBuilder details =
            new StringBuilder();

        for (int i = 0; i < ratingColumns.length; i++) {

            details.append(ratingColumns[i])
                .append(": ")
                .append(record[i])
                .append("\n");
        }

        JOptionPane.showMessageDialog(
            this,
            details.toString(),
            "Rating Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void saveRecords() {

        try {

            Files.createDirectories(dataDir);

            List<String> lines =
                new ArrayList<>();

            for (String[] record : records) {

                lines.add(
                    String.join(" | ", record)
                );
            }

            Files.write(
                currentFile(),
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            loadRecords();

            JOptionPane.showMessageDialog(
                this,
                "Record saved successfully."
            );

        } catch (IOException ex) {

            showError(
                "Cannot save records: "
                + ex.getMessage()
            );
        }
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Instructor Dashboard",
            JOptionPane.WARNING_MESSAGE
        );
    }
}
