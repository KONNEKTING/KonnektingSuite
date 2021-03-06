/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JDialog;

/**
 *
 * @author achristian
 */
public class AboutDialog extends javax.swing.JDialog {

    /**
     * Creates new form AboutDialog
     */
    public AboutDialog(java.awt.Frame parent) {
        super(parent, false);
        initComponents();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(400,430);
        versionLabel.setText("Version " + Main.applicationProperties.getProperty("application.version", "n/a") + " Build " + Main.applicationProperties.getProperty("application.build", "n/a") + (Boolean.getBoolean("de.root1.slicknx.konnekting.debug") ? " DEBUG MODE!" : ""));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        versionLabel = new javax.swing.JLabel();
        donateButton = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();
        websiteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(null);
        setMinimumSize(null);
        setResizable(false);
        getContentPane().setLayout(null);

        versionLabel.setFont(new java.awt.Font("Monospaced", 0, 9)); // NOI18N
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N
        versionLabel.setText(bundle.getString("AboutDialog.versionLabel.text")); // NOI18N
        getContentPane().add(versionLabel);
        versionLabel.setBounds(10, 280, 380, 12);

        donateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/btn_donateCC_LG.gif"))); // NOI18N
        donateButton.setMaximumSize(new java.awt.Dimension(140, 47));
        donateButton.setMinimumSize(new java.awt.Dimension(140, 47));
        donateButton.setPreferredSize(new java.awt.Dimension(140, 47));
        donateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                donateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(donateButton);
        donateButton.setBounds(10, 310, 120, 60);

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/splash/splash.png"))); // NOI18N
        getContentPane().add(logoLabel);
        logoLabel.setBounds(0, 0, 400, 300);

        websiteButton.setText(bundle.getString("AboutDialog.websiteButton.text")); // NOI18N
        websiteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                websiteButtonActionPerformed(evt);
            }
        });
        getContentPane().add(websiteButton);
        websiteButton.setBounds(140, 310, 250, 60);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void donateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_donateButtonActionPerformed

        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4CSJC7P3PVUYN"));
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_donateButtonActionPerformed

    private void websiteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteButtonActionPerformed
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI("http://www.konnekting.de"));
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_websiteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton donateButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JButton websiteButton;
    // End of variables declaration//GEN-END:variables
}
