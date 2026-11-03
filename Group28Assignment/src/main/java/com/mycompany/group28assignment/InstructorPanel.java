package com.mycompany.group28assignment;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

/** Instructor GUI. Uses UTF-8 text files in the project working directory. */
public class InstructorPanel extends JPanel {
    private final Path dataDir = Paths.get("data");
    private final Path schedules = dataDir.resolve("schedules.txt");
    private final Path problems = dataDir.resolve("facility_problems.txt");
    private final Path ratings = dataDir.resolve("ratings.txt");
    private final JTextArea output = new JTextArea();
    private final JComboBox<String> category = new JComboBox<>(new String[]{"Schedules", "Facility Problems", "Ratings"});
    private final java.util.List<String> entries = new ArrayList<>();

    public InstructorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel heading = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));
        add(heading, BorderLayout.NORTH);
        JPanel controls = new JPanel(new GridLayout(2, 4, 8, 8));
        JButton view = new JButton("View");
        JButton search = new JButton("Search");
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton remove = new JButton("Cancel / Delete");
        controls.add(category);
        controls.add(view);
        controls.add(search);
        controls.add(add);
        controls.add(edit);
        controls.add(remove);
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.add(controls, BorderLayout.NORTH);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        main.add(new JScrollPane(output), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
        category.addActionListener(e -> { updateButtons(add, edit, remove); refresh(); });
        view.addActionListener(e -> refresh());
        search.addActionListener(e -> search());
        add.addActionListener(e -> addEntry());
        edit.addActionListener(e -> editEntry());
        remove.addActionListener(e -> removeEntry());
        updateButtons(add, edit, remove);
        refresh();
    }

    private boolean isRatings() { return category.getSelectedIndex() == 2; }
    private Path currentFile() { return category.getSelectedIndex() == 0 ? schedules : category.getSelectedIndex() == 1 ? problems : ratings; }
    private void updateButtons(JButton add, JButton edit, JButton remove) {
        add.setEnabled(!isRatings()); edit.setEnabled(!isRatings()); remove.setEnabled(!isRatings());
        remove.setText(category.getSelectedIndex() == 0 ? "Cancel Schedule" : "Delete Problem");
    }
    private void refresh() {
        try {
            entries.clear();
            if (Files.exists(currentFile())) entries.addAll(Files.readAllLines(currentFile(), StandardCharsets.UTF_8));
            showEntries(entries);
        } catch (IOException ex) { error("Could not read data: " + ex.getMessage()); }
    }
    private void showEntries(java.util.List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) sb.append(i + 1).append(". ").append(lines.get(i)).append('\n');
        output.setText(sb.length() == 0 ? "No records found." : sb.toString());
    }
    private String[] fields() {
        if (category.getSelectedIndex() == 0) return new String[]{"Class Name", "Date (YYYY-MM-DD)", "Time (HH:MM)"};
        return new String[]{"Facility Name", "Problem Description", "Date (YYYY-MM-DD)"};
    }
    private String promptEntry(String previous) {
        String[] labels = fields();
        String[] old = previous == null ? new String[0] : previous.split(" \\| ", -1);
        JPanel form = new JPanel(new GridLayout(labels.length, 2, 6, 6));
        JTextField[] inputs = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            inputs[i] = new JTextField(i < old.length ? old[i] : "", 18);
            form.add(new JLabel(labels[i])); form.add(inputs[i]);
        }
        if (JOptionPane.showConfirmDialog(this, form, "Record Details", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return null;
        String[] values = new String[labels.length];
        for (int i = 0; i < labels.length; i++) {
            values[i] = inputs[i].getText().trim();
            if (values[i].isEmpty() || values[i].contains("|") || values[i].contains("\n")) {
                error("All fields are required; do not use the | character."); return null;
            }
        }
        int dateIndex = category.getSelectedIndex() == 0 ? 1 : 2;
        try { java.time.LocalDate.parse(values[dateIndex]); }
        catch (Exception ex) { error("Enter a valid date (YYYY-MM-DD)."); return null; }
        if (category.getSelectedIndex() == 0) {
            try { java.time.LocalTime.parse(values[2]); }
            catch (Exception ex) { error("Enter a valid time (HH:MM, 24-hour)."); return null; }
        }
        return String.join(" | ", values);
    }
    private void save() {
        try { Files.createDirectories(dataDir); Files.write(currentFile(), entries, StandardCharsets.UTF_8); refresh(); }
        catch (IOException ex) { error("Could not save: " + ex.getMessage()); }
    }
    private void addEntry() { if (isRatings()) return; String s = promptEntry(null); if (s != null) { entries.add(s); save(); } }
    private int chooseIndex() {
        if (entries.isEmpty()) { error("No records found."); return -1; }
        String value = JOptionPane.showInputDialog(this, "Enter record number (1-" + entries.size() + "):");
        if (value == null) return -1;
        try { int i = Integer.parseInt(value.trim()) - 1; if (i >= 0 && i < entries.size()) return i; }
        catch (NumberFormatException ignored) { }
        error("Invalid record number."); return -1;
    }
    private void editEntry() { if (isRatings()) return; int i = chooseIndex(); if (i < 0) return; String s = promptEntry(entries.get(i)); if (s != null) { entries.set(i, s); save(); } }
    private void removeEntry() { if (isRatings()) return; int i = chooseIndex(); if (i < 0) return; if (JOptionPane.showConfirmDialog(this, "Confirm removal?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { entries.remove(i); save(); } }
    private void search() {
        String q = JOptionPane.showInputDialog(this, "Search keyword:");
        if (q == null || q.trim().isEmpty()) return;
        java.util.List<String> matches = new ArrayList<>();
        for (String s : entries) if (s.toLowerCase(Locale.ROOT).contains(q.trim().toLowerCase(Locale.ROOT))) matches.add(s);
        showEntries(matches);
    }
    private void error(String message) { JOptionPane.showMessageDialog(this, message, "Instructor", JOptionPane.WARNING_MESSAGE); }
}
