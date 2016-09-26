/*
 * Copyright (C) 2016 Alexander Christian <alex(at)root1.de>. All rights reserved.
 * 
 * This file is part of KONNEKTING Suite.
 *
 *   KONNEKTING Suite is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   KONNEKTING Suite is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with KONNEKTING DeviceConfig.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.konnekting.suite;

import de.konnekting.suite.events.EventSaveSettings;
import de.konnekting.suite.utils.SelectionItem;
import de.konnekting.suite.utils.Utils;
import de.root1.rooteventbus.RootEventBus;
import de.root1.slicknx.KnxInterfaceDevice;
import de.root1.slicknx.KnxInterfaceDeviceType;
import de.root1.slicknx.KnxRoutingDevice;
import de.root1.slicknx.KnxTunnelingDevice;
import java.awt.Component;
import java.awt.Frame;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class SettingsDialog extends javax.swing.JDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Properties p = Main.getProperties();

    public static final String ACCESS_OFF = "OFF";
    public static final String ACCESS_ROUTING = "ROUTING";
    public static final String ACCESS_TUNNELING = "TUNNELING";
    public static final String ACCESS_TPUART = "TPUART";

    public static final String PROP_STARTUP_LASTFOLDER = "startup.lastfolder";
    public static final String PROP_STARTUP_ASKFOLDER = "startup.askfolder";

    public static final String PROP_ACCESS = "knx.access";
    public static final String PROP_ROUTING_MULTICASTIP = "knx.routing.multicast";
    public static final String PROP_ROUTING_MULTICASTNETWORKINTERFACE = "knx.routing.networkinterface";
    public static final String PROP_TUNNELING_IP = "knx.tunneling.ip";
    public static final String PROP_TPUART_DEVICE = "knx.tpuart.device";
    public static final String PROP_INDIVIDUALADDRESS = "knx.individualaddress";

    private final List<NetworkInterfaceItem> networkInterfaces = new ArrayList<>();

    /**
     * Creates new form SettingsDialog
     *
     * @param parent
     */
    public SettingsDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();

        boolean lastFolder = Boolean.parseBoolean(p.getProperty(PROP_STARTUP_LASTFOLDER, "false"));
        boolean askFolder = Boolean.parseBoolean(p.getProperty(PROP_STARTUP_ASKFOLDER, "true"));

        String access = p.getProperty(PROP_ACCESS, ACCESS_OFF);
        String routingMulticast = p.getProperty(PROP_ROUTING_MULTICASTIP, "224.0.23.12");
        String tunnelingIp = p.getProperty(PROP_TUNNELING_IP, "192.168.0.100");
        String tpuartDevice = p.getProperty(PROP_TPUART_DEVICE, "COM3");
        String individualAddress = p.getProperty(PROP_INDIVIDUALADDRESS, "1.0.254");

        switch (access.toUpperCase()) {
            case ACCESS_OFF:
                offlineRadioButton.setSelected(true);
                break;
            case ACCESS_ROUTING:
                ipRoutingRadioButton.setSelected(true);
                break;
            case ACCESS_TUNNELING:
                ipTunnelingRadioButton.setSelected(true);
                break;
            case ACCESS_TPUART:
                tpuartRadioButton.setSelected(true);
                break;
        }

        ipRoutingMulticasttextField.setText(routingMulticast);
        ipTunnelingIpTextField.setText(tunnelingIp);
        tpuartDevicetextField.setText(tpuartDevice);

        individualAddressTextField.setText(individualAddress);

        setEnableAll(offlinePanel, offlineRadioButton.isSelected());
        setEnableAll(ipRoutingPanel, ipRoutingRadioButton.isSelected());
        setEnableAll(ipTunnelingPanel, ipTunnelingRadioButton.isSelected());
        setEnableAll(tpuartPanel, tpuartRadioButton.isSelected());

        askFolderCheckbox.setSelected(askFolder);
        lastFolderCheckbox.setSelected(lastFolder);

        try {
            Enumeration<NetworkInterface> networkInterfaces1 = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces1.hasMoreElements()) {
                networkInterfaces.add(new NetworkInterfaceItem(networkInterfaces1.nextElement()));
            }

        } catch (SocketException ex) {
        }

        ipRoutingNetworkCombobox.setModel(new DefaultComboBoxModel(networkInterfaces.toArray()));

        // make default selection, based in properties
        String niName = Main.getProperties().getProperty(PROP_ROUTING_MULTICASTNETWORKINTERFACE, "nodefaultvalueavailable");
        for (int i = 0; i < networkInterfaces.size(); i++) {
            NetworkInterfaceItem nii = (NetworkInterfaceItem) ipRoutingNetworkCombobox.getModel().getElementAt(i);
            if (nii.getNetworkInterface().getName().equals(niName)) {
                ipRoutingNetworkCombobox.setSelectedIndex(i);
                break;
            }
        }

        if (Utils.isLinux()) {
            tpuartRadioButton.setEnabled(false);
            tpuartRadioButton.setText(tpuartRadioButton.getText()+" -> not yet available on linux");
            setEnableAll(tpuartPanel, false);
        }

        updateOpenCheckboxes();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        settingsTabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        lastFolderCheckbox = new javax.swing.JCheckBox();
        askFolderCheckbox = new javax.swing.JCheckBox();
        knxPanel = new javax.swing.JPanel();
        offlinePanel = new javax.swing.JPanel();
        noconnectionLabel = new javax.swing.JLabel();
        ipRoutingPanel = new javax.swing.JPanel();
        multicastipLabel = new javax.swing.JLabel();
        ipRoutingMulticasttextField = new javax.swing.JTextField();
        ipRoutingNetworkCombobox = new javax.swing.JComboBox();
        multicastNetworkOn = new javax.swing.JLabel();
        ipRoutingRadioButton = new javax.swing.JRadioButton();
        ipTunnelingRadioButton = new javax.swing.JRadioButton();
        ipTunnelingPanel = new javax.swing.JPanel();
        ipAddressLabel = new javax.swing.JLabel();
        ipTunnelingIpTextField = new javax.swing.JTextField();
        tpuartRadioButton = new javax.swing.JRadioButton();
        tpuartPanel = new javax.swing.JPanel();
        tpuartinterfaceLabel = new javax.swing.JLabel();
        tpuartDevicetextField = new javax.swing.JTextField();
        individualAddressLabel = new javax.swing.JLabel();
        individualAddressTextField = new javax.swing.JTextField();
        offlineRadioButton = new javax.swing.JRadioButton();
        ipRoutingAutodetectButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        settingsTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N
        settingsTabbedPane.setToolTipText(bundle.getString("SettingsDialog.settingsTabbedPane.toolTipText")); // NOI18N

        lastFolderCheckbox.setText(bundle.getString("SettingsDialog.lastFolderCheckbox.text")); // NOI18N
        lastFolderCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastFolderCheckboxActionPerformed(evt);
            }
        });

        askFolderCheckbox.setText(bundle.getString("SettingsDialog.askFolderCheckbox.text")); // NOI18N
        askFolderCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                askFolderCheckboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastFolderCheckbox)
                    .addComponent(askFolderCheckbox))
                .addContainerGap(466, Short.MAX_VALUE))
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lastFolderCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(askFolderCheckbox)
                .addContainerGap(426, Short.MAX_VALUE))
        );

        settingsTabbedPane.addTab(bundle.getString("SettingsDialog.generalPanel.TabConstraints.tabTitle"), generalPanel); // NOI18N

        offlinePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        noconnectionLabel.setText(bundle.getString("SettingsDialog.noconnectionLabel.text")); // NOI18N

        javax.swing.GroupLayout offlinePanelLayout = new javax.swing.GroupLayout(offlinePanel);
        offlinePanel.setLayout(offlinePanelLayout);
        offlinePanelLayout.setHorizontalGroup(
            offlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(offlinePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noconnectionLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        offlinePanelLayout.setVerticalGroup(
            offlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(offlinePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(noconnectionLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ipRoutingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        multicastipLabel.setText(bundle.getString("SettingsDialog.multicastipLabel.text")); // NOI18N

        ipRoutingMulticasttextField.setText("224.0.23.12"); // NOI18N

        ipRoutingNetworkCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        multicastNetworkOn.setText(bundle.getString("SettingsDialog.multicastNetworkOn.text")); // NOI18N

        javax.swing.GroupLayout ipRoutingPanelLayout = new javax.swing.GroupLayout(ipRoutingPanel);
        ipRoutingPanel.setLayout(ipRoutingPanelLayout);
        ipRoutingPanelLayout.setHorizontalGroup(
            ipRoutingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipRoutingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(multicastipLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipRoutingMulticasttextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multicastNetworkOn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipRoutingNetworkCombobox, 0, 450, Short.MAX_VALUE)
                .addContainerGap())
        );
        ipRoutingPanelLayout.setVerticalGroup(
            ipRoutingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipRoutingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ipRoutingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multicastipLabel)
                    .addComponent(ipRoutingMulticasttextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ipRoutingNetworkCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(multicastNetworkOn))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttonGroup1.add(ipRoutingRadioButton);
        ipRoutingRadioButton.setText(bundle.getString("SettingsDialog.ipRoutingRadioButton.text")); // NOI18N
        ipRoutingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipRoutingRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(ipTunnelingRadioButton);
        ipTunnelingRadioButton.setText(bundle.getString("SettingsDialog.ipTunnelingRadioButton.text")); // NOI18N
        ipTunnelingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipTunnelingRadioButtonActionPerformed(evt);
            }
        });

        ipTunnelingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ipAddressLabel.setText(bundle.getString("SettingsDialog.ipAddressLabel.text")); // NOI18N

        ipTunnelingIpTextField.setText("192.168.0.100"); // NOI18N

        javax.swing.GroupLayout ipTunnelingPanelLayout = new javax.swing.GroupLayout(ipTunnelingPanel);
        ipTunnelingPanel.setLayout(ipTunnelingPanelLayout);
        ipTunnelingPanelLayout.setHorizontalGroup(
            ipTunnelingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipTunnelingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ipAddressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTunnelingIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ipTunnelingPanelLayout.setVerticalGroup(
            ipTunnelingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipTunnelingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ipTunnelingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipAddressLabel)
                    .addComponent(ipTunnelingIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonGroup1.add(tpuartRadioButton);
        tpuartRadioButton.setText(bundle.getString("SettingsDialog.tpuartRadioButton.text")); // NOI18N
        tpuartRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpuartRadioButtonActionPerformed(evt);
            }
        });

        tpuartPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tpuartinterfaceLabel.setText(bundle.getString("SettingsDialog.tpuartinterfaceLabel.text")); // NOI18N

        tpuartDevicetextField.setText("COM3"); // NOI18N

        javax.swing.GroupLayout tpuartPanelLayout = new javax.swing.GroupLayout(tpuartPanel);
        tpuartPanel.setLayout(tpuartPanelLayout);
        tpuartPanelLayout.setHorizontalGroup(
            tpuartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpuartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpuartinterfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpuartDevicetextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tpuartPanelLayout.setVerticalGroup(
            tpuartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tpuartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tpuartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tpuartinterfaceLabel)
                    .addComponent(tpuartDevicetextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        individualAddressLabel.setText(bundle.getString("SettingsDialog.individualAddressLabel.text")); // NOI18N

        individualAddressTextField.setText("1.0.255"); // NOI18N

        buttonGroup1.add(offlineRadioButton);
        offlineRadioButton.setSelected(true);
        offlineRadioButton.setText(bundle.getString("SettingsDialog.offlineRadioButton.text")); // NOI18N
        offlineRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offlineRadioButtonActionPerformed(evt);
            }
        });

        ipRoutingAutodetectButton.setText(bundle.getString("SettingsDialog.multicastNetworkDetectButton.text")); // NOI18N
        ipRoutingAutodetectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipRoutingAutodetectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout knxPanelLayout = new javax.swing.GroupLayout(knxPanel);
        knxPanel.setLayout(knxPanelLayout);
        knxPanelLayout.setHorizontalGroup(
            knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(knxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(knxPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipTunnelingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tpuartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ipRoutingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(offlinePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(knxPanelLayout.createSequentialGroup()
                        .addGroup(knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipTunnelingRadioButton)
                            .addComponent(tpuartRadioButton)
                            .addGroup(knxPanelLayout.createSequentialGroup()
                                .addComponent(individualAddressLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(individualAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(offlineRadioButton)
                            .addComponent(ipRoutingRadioButton)
                            .addComponent(ipRoutingAutodetectButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        knxPanelLayout.setVerticalGroup(
            knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(knxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(offlineRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(offlinePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipRoutingRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipRoutingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTunnelingRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTunnelingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpuartRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpuartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipRoutingAutodetectButton)
                .addGap(20, 20, 20)
                .addGroup(knxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(individualAddressLabel)
                    .addComponent(individualAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(113, Short.MAX_VALUE))
        );

        settingsTabbedPane.addTab(bundle.getString("SettingsDialog.knxPanel.TabConstraints.tabTitle"), knxPanel); // NOI18N

        saveButton.setText(bundle.getString("SettingsDialog.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsTabbedPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lastFolderCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastFolderCheckboxActionPerformed
        updateOpenCheckboxes();
    }//GEN-LAST:event_lastFolderCheckboxActionPerformed

    private void setEnableAll(JComponent c, boolean enabled) {
        c.setEnabled(enabled);
        Component[] components = c.getComponents();
        for (Component component : components) {
            component.setEnabled(enabled);
        }
    }

    private void tpuartRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tpuartRadioButtonActionPerformed
        setEnableAll(offlinePanel, false);
        setEnableAll(ipRoutingPanel, false);
        setEnableAll(ipTunnelingPanel, false);
        setEnableAll(tpuartPanel, true);
        p.setProperty(PROP_ACCESS, ACCESS_TPUART);
    }//GEN-LAST:event_tpuartRadioButtonActionPerformed

    private void ipTunnelingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipTunnelingRadioButtonActionPerformed
        setEnableAll(offlinePanel, false);
        setEnableAll(ipRoutingPanel, false);
        setEnableAll(ipTunnelingPanel, true);
        setEnableAll(tpuartPanel, false);
        p.setProperty(PROP_ACCESS, ACCESS_TUNNELING);
    }//GEN-LAST:event_ipTunnelingRadioButtonActionPerformed

    private void ipRoutingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipRoutingRadioButtonActionPerformed
        setEnableAll(offlinePanel, false);
        setEnableAll(ipRoutingPanel, true);
        setEnableAll(ipTunnelingPanel, false);
        setEnableAll(tpuartPanel, false);
        p.setProperty(PROP_ACCESS, ACCESS_ROUTING);
    }//GEN-LAST:event_ipRoutingRadioButtonActionPerformed

    private void doSave() {
        p.setProperty(PROP_ROUTING_MULTICASTIP, ipRoutingMulticasttextField.getText());

        NetworkInterfaceItem nii = (NetworkInterfaceItem) ipRoutingNetworkCombobox.getSelectedItem();

        p.setProperty(PROP_ROUTING_MULTICASTNETWORKINTERFACE, nii.getNetworkInterface().getName());
        p.setProperty(PROP_TPUART_DEVICE, tpuartDevicetextField.getText());
        p.setProperty(PROP_TUNNELING_IP, ipTunnelingIpTextField.getText());
        p.setProperty(PROP_INDIVIDUALADDRESS, individualAddressTextField.getText());

        p.setProperty(PROP_STARTUP_LASTFOLDER, lastFolderCheckbox.isSelected() + "");
        p.setProperty(PROP_STARTUP_ASKFOLDER, askFolderCheckbox.isSelected() + "");

        RootEventBus.getDefault().post(new EventSaveSettings());
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        doSave();
        dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void askFolderCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_askFolderCheckboxActionPerformed
        updateOpenCheckboxes();
    }//GEN-LAST:event_askFolderCheckboxActionPerformed

    private void offlineRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offlineRadioButtonActionPerformed
        setEnableAll(offlinePanel, true);
        setEnableAll(ipRoutingPanel, false);
        setEnableAll(ipTunnelingPanel, false);
        setEnableAll(tpuartPanel, false);
        p.setProperty(PROP_ACCESS, ACCESS_OFF);
    }//GEN-LAST:event_offlineRadioButtonActionPerformed

    private void ipRoutingAutodetectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipRoutingAutodetectButtonActionPerformed
        KnxAutoDiscoverProgress kadp = new KnxAutoDiscoverProgress((Frame) getParent(), true);

        // start progress
        kadp.setVisible(true);

        List<KnxInterfaceDevice> deviceList = kadp.getDeviceList();

        if (!deviceList.isEmpty()) {

            SelectionItem[] items = new SelectionItem[deviceList.size()];

            int selectedIndex = 0;

            for (int i = 0; i < items.length; i++) {
                KnxInterfaceDevice kid = deviceList.get(i);

                InetAddress addr;

                if (kid.getType() == KnxInterfaceDeviceType.ROUTING) {
                    KnxRoutingDevice r = (KnxRoutingDevice) kid;
                    addr = r.getMulticastAddress();
                    selectedIndex = i;
                } else {
                    KnxTunnelingDevice t = (KnxTunnelingDevice) kid;
                    addr = t.getIp();
                }

                items[i] = new SelectionItem("[" + kid.getType().name() + "]  " + kid.getName() + " - " + addr.getHostAddress() + " (MAC: " + kid.getMac() + ")", kid);
            }

            SelectionItem selected = (SelectionItem) JOptionPane.showInputDialog(null, "Choose now...",
                    "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
                    // default
                    // icon
                    items, // Array of choices
                    items[selectedIndex]); // Initial choice
            log.info("Selected knxconnection: {}", selected);

            if (selected != null) {
                KnxInterfaceDevice kid = (KnxInterfaceDevice) selected.getObject();

                switch (kid.getType()) {
                    case ROUTING:
                        ipRoutingRadioButton.doClick();
                        KnxRoutingDevice r = (KnxRoutingDevice) selected.getObject();
                        ipRoutingMulticasttextField.setText(r.getMulticastAddress().getHostAddress());
                        ipRoutingNetworkCombobox.setSelectedIndex(0);

                        for (int i = 0; i < networkInterfaces.size(); i++) {
                            NetworkInterfaceItem nii = (NetworkInterfaceItem) ipRoutingNetworkCombobox.getModel().getElementAt(i);
                            if (nii.getNetworkInterface().getName().equals(kid.getNetworkInterface().getName())) {
                                ipRoutingNetworkCombobox.setSelectedIndex(i);
                                break;
                            }
                        }

                        break;

                    case TUNNELING:
                        ipTunnelingRadioButton.doClick();
                        KnxTunnelingDevice t = (KnxTunnelingDevice) selected.getObject();
                        ipTunnelingIpTextField.setText(t.getIp().getHostAddress());
                        break;

                }
            }

        } else {
            JOptionPane.showMessageDialog((Frame) getParent(),
                    "Nichts gefunden.");
        }
//        if (ni!=null) {
//            ipRoutingNetworkCombobox.setSelectedItem(new NetworkInterfaceItem(ni));
//            ipRoutingMulticasttextField.setText(kadp.getMulticast());
//        }
    }//GEN-LAST:event_ipRoutingAutodetectButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox askFolderCheckbox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JLabel individualAddressLabel;
    private javax.swing.JTextField individualAddressTextField;
    private javax.swing.JLabel ipAddressLabel;
    private javax.swing.JButton ipRoutingAutodetectButton;
    private javax.swing.JTextField ipRoutingMulticasttextField;
    private javax.swing.JComboBox ipRoutingNetworkCombobox;
    private javax.swing.JPanel ipRoutingPanel;
    private javax.swing.JRadioButton ipRoutingRadioButton;
    private javax.swing.JTextField ipTunnelingIpTextField;
    private javax.swing.JPanel ipTunnelingPanel;
    private javax.swing.JRadioButton ipTunnelingRadioButton;
    private javax.swing.JPanel knxPanel;
    private javax.swing.JCheckBox lastFolderCheckbox;
    private javax.swing.JLabel multicastNetworkOn;
    private javax.swing.JLabel multicastipLabel;
    private javax.swing.JLabel noconnectionLabel;
    private javax.swing.JPanel offlinePanel;
    private javax.swing.JRadioButton offlineRadioButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTabbedPane settingsTabbedPane;
    private javax.swing.JTextField tpuartDevicetextField;
    private javax.swing.JPanel tpuartPanel;
    private javax.swing.JRadioButton tpuartRadioButton;
    private javax.swing.JLabel tpuartinterfaceLabel;
    // End of variables declaration//GEN-END:variables

    private void updateOpenCheckboxes() {
        if (askFolderCheckbox.isSelected()) {
            lastFolderCheckbox.setEnabled(false);
            lastFolderCheckbox.setSelected(false);
        } else if (lastFolderCheckbox.isSelected()) {
            askFolderCheckbox.setEnabled(false);
            askFolderCheckbox.setSelected(false);
        } else {
            askFolderCheckbox.setEnabled(true);
            lastFolderCheckbox.setEnabled(true);
        }
    }
}
