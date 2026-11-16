package com.mycompany.group28assignment;

import javax.swing.*;
import java.awt.*;

public class SchedulePreview {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Instructor Schedule");
            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            );

            JLabel title = new JLabel(
                "Instructor Schedule",
                SwingConstants.CENTER
            );

            JButton viewButton = new JButton("View Schedule");

            viewButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(
                    frame,
                    "Schedule screen is ready for connection.",
                    "View Schedule",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });

            panel.add(title, BorderLayout.NORTH);
            panel.add(viewButton, BorderLayout.CENTER);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}