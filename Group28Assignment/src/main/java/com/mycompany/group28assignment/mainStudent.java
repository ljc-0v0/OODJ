/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.group28assignment;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class mainStudent extends javax.swing.JFrame {
    private Student currentStudent;
  
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(mainStudent.class.getName());

  public mainStudent(Student student) {
    initComponents();

    this.currentStudent = student;

    Namelabel.setText("Welcome, " + student.getUserName());
// Inside mainStudent(Student student) constructor:
    btnPaymentHistory.addActionListener(e -> openPaymentHistory());
    // Rate instructor 按钮：打开评分页面
   btnrateinstructor.addActionListener(e -> {
        InstructorRating page = new InstructorRating(this, currentStudent.getStudentID());
        page.setLocationRelativeTo(null);
        page.setVisible(true);
        this.setVisible(false);   // Back or X on the rating page shows it again
    });
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Namelabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnrateinstructor = new javax.swing.JButton();
        btnbookclasses = new javax.swing.JButton();
        btnmybooking = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnPaymentHistory = new javax.swing.JButton();
        btnTopUpEwallet = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Namelabel.setText("Welcome, Name  ");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnrateinstructor.setText("Rate instructor");
        btnrateinstructor.addActionListener(this::btnrateinstructorActionPerformed);

        btnbookclasses.setText("Book classes");
        btnbookclasses.addActionListener(this::btnbookclassesActionPerformed);

        btnmybooking.setText("My booking");
        btnmybooking.addActionListener(this::btnmybookingActionPerformed);

        btnLogout.setText("Log out ");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        btnPaymentHistory.setText("Payment history ");
        btnPaymentHistory.addActionListener(this::btnPaymentHistoryActionPerformed);

        btnTopUpEwallet.setText("TopUp Ewallet");
        btnTopUpEwallet.addActionListener(this::btnTopUpEwalletActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnmybooking, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnbookclasses, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTopUpEwallet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPaymentHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnrateinstructor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(39, 39, 39))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnrateinstructor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnbookclasses, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnmybooking, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPaymentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTopUpEwallet, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(Namelabel)
                .addGap(23, 23, 23)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
int answer = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to log out?", "Log Out",
        JOptionPane.YES_NO_OPTION);
if (answer == JOptionPane.YES_OPTION) {
    dispose();
    Group28Assignment.login();
}

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnmybookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmybookingActionPerformed
       if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Student information is missing.");
            return;
        }
        MyBookings page = new MyBookings(currentStudent.getStudentID(), this);
        page.setLocationRelativeTo(null);
        page.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnmybookingActionPerformed

    private void btnbookclassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbookclassesActionPerformed
     
    if (currentStudent == null) {
        JOptionPane.showMessageDialog(this, "Student information is missing.");
        return;
    }
    BookClasses page = new BookClasses(currentStudent.getStudentID(),this);
    page.setLocationRelativeTo(null);
    page.setVisible(true);
    this.setVisible(false);


    }//GEN-LAST:event_btnbookclassesActionPerformed

    private void btnrateinstructorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrateinstructorActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_btnrateinstructorActionPerformed

    private void btnTopUpEwalletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopUpEwalletActionPerformed
        // TODO add your handling code here:
    if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Student information is missing.");
            return;
        }
        Topupframe page = new Topupframe(currentStudent.getStudentID());
        page.setLocationRelativeTo(null);
        page.setVisible(true);
    
    }//GEN-LAST:event_btnTopUpEwalletActionPerformed

    private void btnPaymentHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaymentHistoryActionPerformed
        // TODO add your handling code here:
        openPaymentHistory();
    }//GEN-LAST:event_btnPaymentHistoryActionPerformed
private void openPaymentHistory() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Student information is missing.");
            return;
        }
        Paymenthistory page = new Paymenthistory(currentStudent.getStudentID(), this);
        page.setLocationRelativeTo(null);
        page.setVisible(true);
        this.setVisible(false);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       java.awt.EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Please open this page through Student Login.");
        });
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Namelabel;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPaymentHistory;
    private javax.swing.JButton btnTopUpEwallet;
    private javax.swing.JButton btnbookclasses;
    private javax.swing.JButton btnmybooking;
    private javax.swing.JButton btnrateinstructor;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}