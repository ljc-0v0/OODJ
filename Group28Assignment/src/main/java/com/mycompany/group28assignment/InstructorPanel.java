
package com.mycompany.group28assignment;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private final Path equipmentFile = Paths.get("equipment.txt");
    private final Path schedules = dataDir.resolve("schedules.txt");
    private final Path problems = dataDir.resolve("facility_problems.txt");
    private final Path ratings = dataDir.resolve("ratings.txt");
    private final Path idCounters = dataDir.resolve("instructor_id_counters.properties");

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private static final Color BLUE = new Color(42, 93, 151);
    private static final Color HOVER = new Color(65, 125, 190);
    private static final Color GREEN = new Color(220, 245, 224);
    private static final Color RED = new Color(255, 225, 225);

    private final String[] scheduleColumns = {
        "Class ID", "Class Name", "Discipline",
        "Date", "Time", "Capacity", "Status",
        "Fee", "Duration", "Created By"
    };

    private final String[] problemColumns = {
        "Problem ID", "Equipment ID", "Equipment Name",
        "Location", "Description", "Date", "Status",
        "Created By"
    };

    private final String[] ratingColumns = {
        "Rating ID", "Student", "Instructor",
        "Class", "Rating", "Comment"
    };

    private final Runnable logoutAction;
    private final String creatorName;
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);

    private int section = 0;

    private final JTextField searchField = new JTextField(18);
    private final JLabel countLabel = new JLabel("Showing: 0 / 0");
    private final JLabel sectionTitle = new JLabel();

    private final JButton addButton = new JButton();
    private final JButton editButton = new JButton("Update");
    private final JButton deleteButton = new JButton();
    private final JButton backButton = new JButton("Back to Menu");

    private final DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

    private final List<String[]> records = new ArrayList<>();

    private static class Equipment {
        final String id;
        final String name;
        final String location;

        Equipment(String id, String name, String location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        @Override
        public String toString() {
            return id + " - " + name + " (" + location + ")";
        }
    }

    public InstructorPanel() {
        this(null, "");
    }

    public InstructorPanel(Runnable logoutAction) {
        this(logoutAction, "");
    }

    public InstructorPanel(Runnable logoutAction, String creatorName) {
        this.logoutAction = logoutAction;
        this.creatorName = creatorName == null ? "" : creatorName.trim();

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
                "Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        menu.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(
                new GridLayout(4, 1, 0, 15));
        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 65, 25, 65));

        JButton schedulesButton = new JButton("Class Schedules");
        JButton problemsButton = new JButton("Facility Problems");
        JButton ratingsButton = new JButton("Instructor Ratings");
        JButton logoutButton = new JButton("Log Out");

        JButton[] buttons = {
            schedulesButton, problemsButton,
            ratingsButton, logoutButton
        };

        for (JButton button : buttons) {
            styleButton(button);
            button.setFont(new Font("SansSerif", Font.BOLD, 17));
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
                        this, "Log Out is available from MainFrame.");
            }
        });

        menu.add(buttonPanel, BorderLayout.CENTER);
        return menu;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Instructor Dashboard", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        workspace.add(heading, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8));

        styleButton(backButton);
        backButton.addActionListener(e -> cards.show(pages, "MENU"));

        sectionTitle.setFont(
                new Font("SansSerif", Font.BOLD, 17));

        top.add(backButton);
        top.add(Box.createHorizontalStrut(10));
        top.add(sectionTitle);
        top.add(Box.createHorizontalStrut(15));
        top.add(new JLabel("Search ID / Name:"));
        top.add(searchField);

        main.add(top, BorderLayout.NORTH);

        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(
                Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focused, int row, int column) {

                Component component =
                        super.getTableCellRendererComponent(
                                t, value, selected,
                                focused, row, column);

                if (!selected) {
                    component.setForeground(Color.BLACK);
                    component.setBackground(Color.WHITE);

                    if (section == 0) {
                        int modelRow = t.convertRowIndexToModel(row);
                        String status = String.valueOf(
                                model.getValueAt(modelRow, 6));

                        if ("Available".equalsIgnoreCase(status)) {
                            component.setBackground(GREEN);
                        } else if ("Cancelled".equalsIgnoreCase(status)) {
                            component.setBackground(RED);
                        }
                    }
                }

                return component;
            }
        });

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JButton[] actionButtons = {
            addButton, editButton, deleteButton
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
        });

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
                BorderFactory.createEmptyBorder(12, 16, 12, 16));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

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
    }

    private List<Equipment> loadEquipment() throws IOException {
        if (!Files.isRegularFile(equipmentFile)) {
            throw new IOException(
                    "equipment.txt was not found in the project root. "
                    + "Get the latest file from your teammate's GitHub repository.");
        }

        List<Equipment> result = new ArrayList<>();
        Set<String> ids = new HashSet<>();

        for (String line : Files.readAllLines(
                equipmentFile, StandardCharsets.UTF_8)) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] p = line.split("\\|", -1);

            if (p.length < 7) {
                continue;
            }

            String id = p[0].trim();
            String name = p[1].trim();
            String location = p[6].trim();

            if (!id.isEmpty() && !name.isEmpty()
                    && ids.add(id)) {
                result.add(new Equipment(id, name, location));
            }
        }

        if (result.isEmpty()) {
            throw new IOException(
                    "No valid equipment records found in equipment.txt.");
        }

        return result;
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
                        currentFile(), StandardCharsets.UTF_8);

                int number = 1;

                for (String line : lines) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\s*\\|\\s*", -1);
                    String[] record = normalize(parts, number);

                    if (record != null) {
                        records.add(record);
                    }

                    number++;
                }
            }

            displayRecords();

        } catch (IOException ex) {
            showError("Cannot load records: " + ex.getMessage());
        }
    }

    private String[] normalize(String[] p, int number) {
        if (section == 0) {
            if (p.length >= 7) {
                String[] row = Arrays.copyOf(p, 10);
                if (row[7] == null || row[7].isBlank()) row[7] = "0";
                if (row[8] == null || row[8].isBlank()) row[8] = "60";
                if (row[9] == null) row[9] = "";
                return row;
            }

            if (p.length == 3) {
                return new String[]{
                    String.format("C%03d", number),
                    p[0], "General", p[1], p[2],
                    "20", "Available", "0", "60", ""
                };
            }
        }

        if (section == 1) {
            if (p.length >= 7) {
                String[] row = Arrays.copyOf(p, 8);
                if (row[7] == null) row[7] = "";
                return row;
            }

            if (p.length == 5) {
                return new String[]{
                    p[0], "", p[1], "",
                    p[2], p[3], p[4], ""
                };
            }

            if (p.length == 3) {
                return new String[]{
                    String.format("P%03d", number),
                    "", p[0], "", p[1], p[2], "Open", ""
                };
            }
        }

        if (section == 2) {
            if (p.length == 6) {
                return Arrays.copyOf(p, 6);
            }

            String[] result = new String[6];
            Arrays.fill(result, "");
            result[0] = String.format("R%03d", number);

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

            if (section == 0) {
                display[3] = formatDate(record[3]);
            } else if (section == 1) {
                display[5] = formatDate(record[5]);
            }

            model.addRow(display);
        }

        filter();
    }

    private String formatDate(String value) {
        try {
            return LocalDate.parse(value).format(DISPLAY_DATE);
        } catch (Exception ex) {
            return value;
        }
    }

    private void filter() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(
                    "(?i)" + java.util.regex.Pattern.quote(keyword)));
        }

        countLabel.setText(
                "Showing: " + table.getRowCount()
                + " / " + records.size());
    }

    private int highestExistingID(String prefix) {
        int max = 0;
        Path file = "C".equals(prefix) ? schedules : problems;
        try {
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String id = line.split("\\|", -1)[0].trim();
                    if (id.matches(prefix + "\\d+")) {
                        max = Math.max(max, Integer.parseInt(id.substring(1)));
                    }
                }
            }
        } catch (IOException ex) {
            showError("Cannot read existing IDs: " + ex.getMessage());
        }
        return max;
    }

    private int savedCounter(String prefix) throws IOException {
        Properties properties = new Properties();
        if (Files.exists(idCounters)) {
            try (java.io.InputStream in = Files.newInputStream(idCounters)) {
                properties.load(in);
            }
        }
        // Respect counters from earlier project versions as well.
        Path legacyCounters = Paths.get("id_counters.properties");
        if (Files.exists(legacyCounters)) {
            Properties legacy = new Properties();
            try (java.io.InputStream in = Files.newInputStream(legacyCounters)) {
                legacy.load(in);
            }
            String oldValue = legacy.getProperty(prefix);
            if (oldValue != null) {
                try {
                    int oldMax = Integer.parseInt(oldValue.trim());
                    int newMax = Integer.parseInt(properties.getProperty(prefix, "0"));
                    return Math.max(oldMax, newMax);
                } catch (NumberFormatException ex) {
                    throw new IOException("Invalid legacy ID counter", ex);
                }
            }
        }
        try {
            return Integer.parseInt(properties.getProperty(prefix, "0"));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid saved ID counter for " + prefix, ex);
        }
    }

    private String nextID() {
        String prefix = section == 0 ? "C" : "P";
        try {
            int highest = Math.max(highestExistingID(prefix), savedCounter(prefix));
            return String.format("%s%03d", prefix, highest + 1);
        } catch (IOException ex) {
            showError("Cannot generate ID: " + ex.getMessage());
            return null;
        }
    }

    private void rememberID(String id) throws IOException {
        if (id == null || !id.matches("[CP]\\d+")) {
            throw new IOException("Invalid record ID");
        }
        Files.createDirectories(dataDir);
        Properties properties = new Properties();
        if (Files.exists(idCounters)) {
            try (java.io.InputStream in = Files.newInputStream(idCounters)) {
                properties.load(in);
            }
        }
        String prefix = id.substring(0, 1);
        int previous;
        try {
            previous = Integer.parseInt(properties.getProperty(prefix, "0"));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid ID counter", ex);
        }
        int next = Integer.parseInt(id.substring(1));
        properties.setProperty(prefix, String.valueOf(Math.max(previous, next)));
        try (java.io.OutputStream out = Files.newOutputStream(idCounters)) {
            properties.store(out, "Highest allocated Instructor record IDs - do not reset");
        }
    }

    private JSpinner createDateSpinner(String value) {
        Date initialDate = new Date();

        if (value != null && !value.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(value);
                initialDate = Date.from(
                        date.atStartOfDay(
                                ZoneId.systemDefault()).toInstant());
            } catch (Exception ignored) {
            }
        }

        SpinnerDateModel dateModel = new SpinnerDateModel(
                initialDate, null, null, Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(dateModel);
        spinner.setEditor(
                new JSpinner.DateEditor(spinner, "dd-MM-yyyy"));

        return spinner;
    }

    private JSpinner createTimeSpinner(String value) {
        Date initialDate = new Date();

        if (value != null && !value.isBlank()) {
            try {
                LocalTime time = LocalTime.parse(value);
                LocalDateTime dateTime = LocalDate.now().atTime(time);
                initialDate = Date.from(
                        dateTime.atZone(
                                ZoneId.systemDefault()).toInstant());
            } catch (Exception ignored) {
            }
        }

        SpinnerDateModel timeModel = new SpinnerDateModel(
                initialDate, null, null, Calendar.MINUTE);

        JSpinner spinner = new JSpinner(timeModel);
        spinner.setEditor(
                new JSpinner.DateEditor(spinner, "HH:mm"));

        return spinner;
    }

    private String[] showScheduleForm(String[] existing) {
        if (creatorName.isBlank()) {
            showError("Current login username is unavailable. Please reconnect login to MainFrame.");
            return null;
        }
        String[] labels = scheduleColumns;
        if (existing == null && nextID() == null) return null;

        JPanel form = new JPanel(
                new GridLayout(labels.length, 2, 8, 8));

        JComponent[] inputs = new JComponent[labels.length];

        for (int i = 0; i < labels.length; i++) {
            String value = existing == null ? "" : existing[i];

            if (i == 0) {
                JTextField idField = new JTextField(
                        existing == null ? nextID() : value, 20);
                idField.setEditable(false);
                inputs[i] = idField;

            } else if (i == 3) {
                inputs[i] = createDateSpinner(value);

            } else if (i == 4) {
                inputs[i] = createTimeSpinner(value);

            } else if (i == 9) {
                JTextField creator = new JTextField(existing == null ? creatorName : value, 20);
                creator.setEditable(false);
                inputs[i] = creator;

            } else if (i == 6) {
                JComboBox<String> status = new JComboBox<>(
                        new String[]{"Available", "Cancelled"});
                status.setSelectedItem(
                        existing == null ? "Available" : value);
                inputs[i] = status;

            } else {
                if (existing == null && i == 5) {
                    value = "20";
                } else if (existing == null && i == 7) {
                    value = "0";
                } else if (existing == null && i == 8) {
                    value = "60";
                }
                inputs[i] = new JTextField(value, 20);
            }

            form.add(new JLabel(labels[i] + ":"));
            form.add(inputs[i]);
        }

        Object[] options = existing == null
                ? new Object[]{"Save", "Cancel"}
                : new Object[]{"Save", "Delete", "Cancel"};

        while (true) {
            int answer = JOptionPane.showOptionDialog(
                    this, form,
                    existing == null
                            ? "Add New Class" : "Update Class",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (answer < 0 || answer == options.length - 1) {
                return null;
            }

            if (existing != null && answer == 1) {
                deleteExisting(existing);
                return null;
            }

            String[] values = new String[labels.length];

            try {
                for (int i = 0; i < labels.length; i++) {
                    if (inputs[i] instanceof JSpinner) {
                        JSpinner spinner = (JSpinner) inputs[i];
                        spinner.commitEdit();

                        Date date = (Date) spinner.getValue();

                        if (i == 3) {
                            values[i] = date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate().toString();
                        } else {
                            values[i] = date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalTime()
                                    .format(DateTimeFormatter
                                            .ofPattern("HH:mm"));
                        }

                    } else if (inputs[i] instanceof JComboBox) {
                        values[i] = String.valueOf(
                                ((JComboBox<?>) inputs[i])
                                        .getSelectedItem());
                    } else {
                        values[i] = ((JTextField) inputs[i])
                                .getText().trim();
                    }

                    if (i != 9) {
                        validateField(values[i]);
                    }
                }

                LocalDate classDate = LocalDate.parse(values[3]);
                LocalTime.parse(values[4]);
                if (classDate.isBefore(LocalDate.now())
                        && (existing == null || !values[3].equals(existing[3]))) {
                    throw new IllegalArgumentException(
                            "Class date cannot be before today.");
                }

                int capacity = Integer.parseInt(values[5]);

                if (capacity <= 0) {
                    throw new IllegalArgumentException(
                            "Capacity must be positive.");
                }

                double fee = Double.parseDouble(values[7]);
                int duration = Integer.parseInt(values[8]);
                if (fee < 0 || duration <= 0) {
                    throw new IllegalArgumentException("Fee must be non-negative and duration must be positive.");
                }
                return values;

            } catch (Exception ex) {
                showError(ex.getMessage() == null
                        ? "Please check the input values."
                        : ex.getMessage());
            }
        }
    }

    private String[] showProblemForm(String[] existing) {
        if (creatorName.isBlank()) {
            showError("Current login username is unavailable. Please reconnect login to MainFrame.");
            return null;
        }
        List<Equipment> equipment;

        try {
            equipment = loadEquipment();
        } catch (IOException ex) {
            showError(ex.getMessage());
            return null;
        }

        if (existing == null && nextID() == null) return null;
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));

        JTextField idField = new JTextField(
                existing == null ? nextID() : existing[0]);
        idField.setEditable(false);

        JComboBox<Equipment> choose = new JComboBox<>(
                equipment.toArray(new Equipment[0]));

        JLabel nameLabel = new JLabel("-");
        JLabel locationLabel = new JLabel("-");

        JTextField descriptionField = new JTextField(
                existing == null ? "" : existing[4], 20);

        JSpinner dateSpinner = createDateSpinner(
                existing == null ? LocalDate.now().toString() : existing[5]);
        dateSpinner.setEnabled(false);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"Open", "In Progress", "Resolved"});

        if (existing != null) {
            statusBox.setSelectedItem(existing[6]);
        }

        choose.addActionListener(e -> {
            Equipment selected =
                    (Equipment) choose.getSelectedItem();

            nameLabel.setText(
                    selected == null ? "-" : selected.name);
            locationLabel.setText(
                    selected == null ? "-" : selected.location);
        });

        if (existing != null) {
            choose.setSelectedIndex(-1);

            for (Equipment item : equipment) {
                if (item.id.equals(existing[1])) {
                    choose.setSelectedItem(item);
                    break;
                }
            }
        } else {
            choose.setSelectedIndex(-1);
        }

        form.add(new JLabel("Problem ID:"));
        form.add(idField);

        form.add(new JLabel("Select Equipment ID:"));
        form.add(choose);

        form.add(new JLabel("Equipment Name:"));
        form.add(nameLabel);

        form.add(new JLabel("Location:"));
        form.add(locationLabel);

        form.add(new JLabel("Description:"));
        form.add(descriptionField);

        form.add(new JLabel("Date:"));
        form.add(dateSpinner);

        JPanel completeForm = new JPanel(new BorderLayout(8, 8));
        completeForm.add(form, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Status:"));
        statusPanel.add(statusBox);
        completeForm.add(statusPanel, BorderLayout.SOUTH);

        if (existing != null && existing[1].isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "This is an older problem record.\n"
                    + "Please select its correct Equipment ID.\n"
                    + "The equipment name alone is not enough.",
                    "Link Equipment",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        Object[] options = existing == null
                ? new Object[]{"Save", "Cancel"}
                : new Object[]{"Save", "Delete", "Cancel"};

        while (true) {
            int answer = JOptionPane.showOptionDialog(
                    this, completeForm,
                    existing == null
                            ? "Add Facility Problem"
                            : "Update Facility Problem",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (answer < 0 || answer == options.length - 1) {
                return null;
            }

            if (existing != null && answer == 1) {
                deleteExisting(existing);
                return null;
            }

            try {
                Equipment selected =
                        (Equipment) choose.getSelectedItem();

                if (selected == null) {
                    throw new IllegalArgumentException(
                            "Please select the exact Equipment ID.");
                }

                String description =
                        descriptionField.getText().trim();
                validateField(description);

                String dateValue = existing == null
                        ? LocalDate.now().toString() : existing[5];

                String status = String.valueOf(
                        statusBox.getSelectedItem());

                return new String[]{
                    idField.getText(),
                    selected.id,
                    selected.name,
                    selected.location,
                    description,
                    dateValue,
                    status,
                    existing == null ? creatorName : existing[7]
                };

            } catch (Exception ex) {
                showError(ex.getMessage() == null
                        ? "Please check the input values."
                        : ex.getMessage());
            }
        }
    }

    private void validateField(String value) {
        if (value == null || value.isBlank()
                || value.contains("|")
                || value.contains("\n")
                || value.contains("\r")) {
            throw new IllegalArgumentException(
                    "Complete all fields. Do not use |.");
        }
    }

    private void addRecord() {
        if (section == 2) {
            return;
        }

        String[] result = section == 0
                ? showScheduleForm(null)
                : showProblemForm(null);

        if (result != null) {
            records.add(result);
            if (saveRecords()) {
                try {
                    rememberID(result[0]);
                } catch (IOException ex) {
                    showError("Record saved, but ID counter could not be updated: "
                            + ex.getMessage());
                }
            } else {
                records.remove(result);
            }
        }
    }

    private int selectedIndex() {
        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            showError("Please select a row first.");
            return -1;
        }

        return table.convertRowIndexToModel(viewRow);
    }

    private void editRecord() {
        if (section == 2) {
            return;
        }

        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String[] original = records.get(index);
        if (!canEdit(original)) return;

        String[] updated = section == 0
                ? showScheduleForm(original)
                : showProblemForm(original);

        if (updated != null) {
            records.set(index, updated);
            saveRecords();
        }
    }

    private boolean canEdit(String[] record) {
        int ownerIndex = section == 0 ? 9 : 7;
        if (section == 2 || record.length <= ownerIndex) return false;
        String owner = record[ownerIndex] == null ? "" : record[ownerIndex].trim();
        if (creatorName.isBlank() || owner.isBlank()
                || !owner.equalsIgnoreCase(creatorName)) {
            showError("Only the creator of this record can modify it.");
            return false;
        }
        return true;
    }

    private void deleteExisting(String[] existing) {
        if (!canEdit(existing)) return;
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete " + existing[0] + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            records.remove(existing);
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

        String[] record = records.get(index);
        deleteExisting(record);
    }

    private void viewRatingDetails() {
        int index = selectedIndex();

        if (index < 0) {
            return;
        }

        String[] record = records.get(index);
        StringBuilder details = new StringBuilder();

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
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean saveRecords() {
        try {
            Files.createDirectories(dataDir);

            List<String> lines = new ArrayList<>();

            for (String[] record : records) {
                lines.add(String.join(" | ", record));
            }

            Files.write(
                    currentFile(),
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            loadRecords();

            JOptionPane.showMessageDialog(
                    this, "Record saved successfully.");
            return true;

        } catch (IOException ex) {
            showError(
                    "Cannot save records: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Instructor Dashboard",
                JOptionPane.WARNING_MESSAGE);
    }
}
