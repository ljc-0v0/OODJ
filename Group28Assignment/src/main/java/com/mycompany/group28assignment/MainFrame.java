package com.mycompany.group28assignment;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    

    public MainFrame(User currentUser) {
        super("System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        this.currentUser = currentUser;

        add(container);
        if (currentUser instanceof Manager m) {
            // Create panels
            JPanel managerPanel = managerPanel(m);

            // Register each panel under a name
            container.add(managerPanel, "Manager");
            container.add(m.createManagerPanel(this), "CreateManager");
            
            cards.show(container, "Manager"); 
        }  
    }
    
    private JPanel managerPanel(Manager m){
        JPanel panel = new JPanel(new GridLayout(2,2,8,8));
        JButton createManagerBtn = new JButton("Add New Manager");
        
        createManagerBtn.addActionListener(e -> {
            showPanel("CreateManager");
        });
        panel.add(createManagerBtn);
        return panel;
    }
    
    public void showPanel(String name) {
        cards.show(container, name);
}
}
