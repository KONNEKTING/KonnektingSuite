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

import de.konnekting.suite.events.EventProjectSave;
import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.deviceconfig.EventDeviceChanged;
import de.konnekting.deviceconfig.utils.Helper;
import de.konnekting.suite.events.EventConsoleMessage;
import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.EventProjectOpened;
import de.konnekting.suite.events.EventSaveSettings;
import de.konnekting.suite.events.StickyDeviceSelected;
import de.konnekting.suite.uicomponents.groupmonitor.GroupMonitorFrame;
import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author achristian
 */
public class Main extends javax.swing.JFrame {

    static {
        String level = System.getProperty("debuglevel", "info");

        System.out.println("ENABLING LOGGING with level: " + level);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(baos);
            osw.write("handlers= java.util.logging.FileHandler, java.util.logging.ConsoleHandler" + "\n");
            osw.write("java.util.logging.FileHandler.pattern = KonnektingSuite.log" + "\n");
            osw.write("java.util.logging.FileHandler.limit = 10000" + "\n");
            osw.write("java.util.logging.FileHandler.count = 10" + "\n");
            osw.write("java.util.logging.FileHandler.formatter = de.root1.logging.JulFormatter" + "\n");
            osw.write("java.util.logging.ConsoleHandler.level = ALL" + "\n");
            osw.write("java.util.logging.ConsoleHandler.formatter = de.root1.logging.JulFormatter" + "\n");

            osw.write(".level= INFO" + "\n");
            osw.write("de.konnekting.level = " + level.toUpperCase() + "\n");
            osw.write("tuwien.auto.calimero.log.LogService.level = " + "ALL" + "\n");
            osw.write("de.root1.slicknx.konnekting.protocol0x00.ProgProtocol0x00Listener.level = ALL\n");
//            osw.write("de.root1.slicknx.level = " + level.toUpperCase() + "\n");
            

            osw.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            LogManager.getLogManager().readConfiguration(bais);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("ENABLING LOGGING *DONE*");
//        JulFormatter.set();
    }
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    private File projectFolder;
    private final RootEventBus eventbus = RootEventBus.getDefault();
    private static Properties properties = new Properties();
    private Properties applicationProperties = new Properties();
    private File propertiesFile = new File(new File(System.getProperty("user.home")),"KonnektingSuite.properties");
    private Knx knx;
    private final GroupMonitorFrame monitor;

    
    public static Properties getProperties() {
        return properties;
    }
     
    /**
     * Creates new form Main
     */
    public Main() {
        
        try {
            properties.load(new FileReader(propertiesFile));
        } catch (FileNotFoundException ex) {
            log.info("Properties file not found. Skip to defaults.");
        } catch (IOException ex) {
            log.error("Error reading setting properties", ex);
        }
        try {
            applicationProperties.load(getClass().getResourceAsStream("/properties/application.properties"));
        } catch (IOException ex) {
            log.error("Error reading application properties", ex);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (knx != null) {
                    knx.close();
                }
                saveSettings();
            }

        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        
        boolean debug = Boolean.getBoolean("de.root1.slicknx.konnekting.debug");
        String versionMsg = "KONNEKTING Suite - Version "+applicationProperties.getProperty("application.version","n/a")+" Build "+applicationProperties.getProperty("application.build","n/a")+(debug?" DEBUG MODE!":"");
        log.info(versionMsg);
        RootEventBus.getDefault().post(new EventConsoleMessage(versionMsg));
        
        removeDeviceButton.setEnabled(false);
        programmAllButton.setEnabled(false);
        programmDataOnlyButton.setEnabled(false);
        addDeviceButton.setEnabled(false);
        eventbus.register(this);
        
        String access = properties.getProperty(SettingsDialog.PROP_ACCESS, SettingsDialog.ACCESS_ROUTING);
        String routingMulticast = properties.getProperty(SettingsDialog.PROP_ROUTING_MULTICASTIP, "224.0.23.12");
        String tunnelingIp = properties.getProperty(SettingsDialog.PROP_TUNNELING_IP, "192.168.0.100");
        String tpuartDevice = properties.getProperty(SettingsDialog.PROP_TPUART_DEVICE, "COM3");
        String individualAddress = properties.getProperty(SettingsDialog.PROP_INDIVIDUALADDRESS, "1.0.254");

        try {
            switch (access.toUpperCase()) {
                case SettingsDialog.ACCESS_ROUTING:
                    knx = new Knx(individualAddress);
                    knx.setLoopbackMode(true);
                    log.info("Starting in ROUTING mode");
                    RootEventBus.getDefault().post(new EventConsoleMessage("KNX Verbindung: IP-Router"));
                    break;
                case SettingsDialog.ACCESS_TUNNELING:
                    knx = new Knx(InetAddress.getByName(tunnelingIp));
                    log.info("Starting in TUNNELING mode");
                    RootEventBus.getDefault().post(new EventConsoleMessage("KNX Verbindung: IP-Interface"));
                    break;
                case SettingsDialog.ACCESS_TPUART:
                    knx = new Knx(Knx.SerialType.TPUART, tpuartDevice);
                    log.info("Starting in TPUART mode");
                    RootEventBus.getDefault().post(new EventConsoleMessage("KNX Verbindung: TPUART"));
                    break;
                default:
                    log.info("Error. Unknown ACCESS TYPE: " + access);
                    System.exit(1);
            }
            
            knx.addGroupAddressListener("*", new GroupAddressListener() {
                @Override
                public void readRequest(GroupAddressEvent event) {
                    process();
                }

                @Override
                public void readResponse(GroupAddressEvent event) {
                    process();
                }

                @Override
                public void write(GroupAddressEvent event) {
                    process();
                }
                
                public void process(){
                    RootEventBus.getDefault().post(new EventConsoleMessage("KNX Telegramm entdeckt. Verbindung scheint in Ordnung zu sein."));
                    knx.removeGroupAddressListener("*", this);
                }
            });
            
            
        } catch (KnxException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Öffnen der KNX Verbindung: " + access, ex));
            log.error("Error creating knx access.", ex);
        } catch (UnknownHostException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Öffnen der KNX Verbindung.", ex));
            log.error("Error creating knx access.", ex);
        }
        
        

        Dimension size = new Dimension();
        size.width = Integer.parseInt(properties.getProperty("windowwidth", "1024"));
        size.height = Integer.parseInt(properties.getProperty("windowheight", "768"));
        super.setSize(size);
        Point location = new Point();
        location.x = Integer.parseInt(properties.getProperty("windowx", "" + Integer.MIN_VALUE));
        location.y = Integer.parseInt(properties.getProperty("windowy", "" + Integer.MIN_VALUE));
        if (location.x == Integer.MIN_VALUE && location.y == Integer.MIN_VALUE) {
            super.setLocationRelativeTo(null);
        } else {
            super.setLocation(location);
        }
        topSplitPane.setDividerLocation(Integer.parseInt(properties.getProperty("topsplitpanedividerlocation", "180")));
        bottomSplitPane.setDividerLocation(Integer.parseInt(properties.getProperty("bottomsplitpanedividerlocation", "300")));
        
        
        monitor = new GroupMonitorFrame(this);
        if (knx!=null) {
            monitor.setKnx(knx);
        }
        
        
        boolean lastFolder = Boolean.parseBoolean(properties.getProperty(SettingsDialog.PROP_STARTUP_LASTFOLDER, "false"));
        boolean askFolder = Boolean.parseBoolean(properties.getProperty(SettingsDialog.PROP_STARTUP_ASKFOLDER, "true"));
        
        if (askFolder) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openProjectButton.doClick();
                }
            });
            
        } else if (lastFolder) {
            projectFolder = new File(properties.getProperty("projectfolder", System.getProperty("user.home")));
            eventbus.post(new EventProjectOpened(projectFolder));
        }
        
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-16x16-Icon.png"));
        setIconImage(icon.getImage());
                
        setVisible(true);
    }

    private void saveSettings() {
        try {
            log.info("Saving settings");
            properties.store(new FileWriter(propertiesFile), "This is KONNEKTING Suite configuration file");
        } catch (IOException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Schreiben der Einstellungen.", ex));
        }
    }

    public void onEvent(EventSaveSettings evt) {
        saveSettings();
    }

    public void onEvent(EventProjectOpened evt) {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/Bundle"); // NOI18N
        setTitle(bundle.getString("MainWindow.Title") + " - " + evt.getProjectFolder().getAbsolutePath());
        addDeviceButton.setEnabled(true);
    }

    public void onEvent(StickyDeviceSelected evt) {

        DeviceConfigContainer deviceConfig = evt.getDeviceConfig();

        // only enable if there is a selection
        removeDeviceButton.setEnabled(deviceConfig != null);
        updateProgButtons();

    }

    private void updateProgButtons() {

        StickyDeviceSelected evt = eventbus.getStickyEvent(StickyDeviceSelected.class);
        if (evt == null) {
            return;
        }
        DeviceConfigContainer deviceConfig = evt.getDeviceConfig();

        boolean programmable = deviceConfig != null && Helper.checkValidPa(deviceConfig.getIndividualAddress());
        programmAllButton.setEnabled(programmable);
        programmDataOnlyButton.setEnabled(programmable);
    }

    public void onEvent(EventDeviceChanged evt) {
        eventbus.post(new EventProjectSave());
        updateProgButtons();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar = new javax.swing.JToolBar();
        openProjectButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        addDeviceButton = new javax.swing.JButton();
        removeDeviceButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        programmAllButton = new javax.swing.JButton();
        programmDataOnlyButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        groupmonitorButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        exitButton = new javax.swing.JButton();
        statusPanel = new de.konnekting.suite.StatusPanel();
        bottomSplitPane = new javax.swing.JSplitPane();
        topSplitPane = new javax.swing.JSplitPane();
        deviceList = new de.konnekting.suite.DeviceList();
        deviceEditor = new de.konnekting.suite.DeviceEditor();
        consolePanel = new de.konnekting.suite.ConsolePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/Bundle"); // NOI18N
        setTitle(bundle.getString("MainWindow.Title")); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                windowResized(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                windowMoved(evt);
            }
        });

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        openProjectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/fileopen.png"))); // NOI18N
        openProjectButton.setToolTipText(bundle.getString("Main.openProjectButton.toolTipText")); // NOI18N
        openProjectButton.setFocusable(false);
        openProjectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openProjectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectButtonActionPerformed(evt);
            }
        });
        jToolBar.add(openProjectButton);
        jToolBar.add(jSeparator1);

        addDeviceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/edit_add.png"))); // NOI18N
        addDeviceButton.setToolTipText(bundle.getString("Main.addDeviceButton.toolTipText")); // NOI18N
        addDeviceButton.setFocusable(false);
        addDeviceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addDeviceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDeviceButtonActionPerformed(evt);
            }
        });
        jToolBar.add(addDeviceButton);

        removeDeviceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/edit_remove.png"))); // NOI18N
        removeDeviceButton.setToolTipText(bundle.getString("Main.removeDeviceButton.toolTipText")); // NOI18N
        removeDeviceButton.setFocusable(false);
        removeDeviceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeDeviceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeDeviceButtonActionPerformed(evt);
            }
        });
        jToolBar.add(removeDeviceButton);
        jToolBar.add(jSeparator2);

        programmAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/ledred.png"))); // NOI18N
        programmAllButton.setToolTipText(bundle.getString("Main.programmAllButton.toolTipText")); // NOI18N
        programmAllButton.setFocusable(false);
        programmAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        programmAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        programmAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programmAllButtonActionPerformed(evt);
            }
        });
        jToolBar.add(programmAllButton);

        programmDataOnlyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/ledorange.png"))); // NOI18N
        programmDataOnlyButton.setToolTipText(bundle.getString("Main.programmDataOnlyButton.toolTipText")); // NOI18N
        programmDataOnlyButton.setFocusable(false);
        programmDataOnlyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        programmDataOnlyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        programmDataOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programmDataOnlyButtonActionPerformed(evt);
            }
        });
        jToolBar.add(programmDataOnlyButton);
        jToolBar.add(jSeparator3);

        groupmonitorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/display.png"))); // NOI18N
        groupmonitorButton.setText(bundle.getString("Main.groupmonitorButton.text")); // NOI18N
        groupmonitorButton.setToolTipText(bundle.getString("Main.groupmonitorButton.toolTipText")); // NOI18N
        groupmonitorButton.setFocusable(false);
        groupmonitorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        groupmonitorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        groupmonitorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupmonitorButtonActionPerformed(evt);
            }
        });
        jToolBar.add(groupmonitorButton);

        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/kcontrol.png"))); // NOI18N
        settingsButton.setText(bundle.getString("Main.settingsButton.text")); // NOI18N
        settingsButton.setToolTipText(bundle.getString("Main.settingsButton.toolTipText")); // NOI18N
        settingsButton.setFocusable(false);
        settingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });
        jToolBar.add(settingsButton);
        jToolBar.add(jSeparator4);
        jToolBar.add(filler1);

        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/exit.png"))); // NOI18N
        exitButton.setText(bundle.getString("Main.exitButton.text")); // NOI18N
        exitButton.setToolTipText(bundle.getString("Main.exitButton.toolTipText")); // NOI18N
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jToolBar.add(exitButton);

        bottomSplitPane.setDividerLocation(300);
        bottomSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        bottomSplitPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bottomSplitPane.setMinimumSize(new java.awt.Dimension(400, 300));
        bottomSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                bottomDividerChange(evt);
            }
        });

        topSplitPane.setDividerLocation(150);
        topSplitPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        topSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                topDividerChange(evt);
            }
        });

        deviceList.setMinimumSize(new java.awt.Dimension(120, 80));
        deviceList.setPreferredSize(new java.awt.Dimension(180, 300));
        topSplitPane.setLeftComponent(deviceList);

        deviceEditor.setMinimumSize(new java.awt.Dimension(300, 300));
        deviceEditor.setPreferredSize(new java.awt.Dimension(400, 300));
        topSplitPane.setRightComponent(deviceEditor);

        bottomSplitPane.setLeftComponent(topSplitPane);

        consolePanel.setMinimumSize(new java.awt.Dimension(180, 27));
        consolePanel.setName(""); // NOI18N
        consolePanel.setPreferredSize(new java.awt.Dimension(80, 300));
        bottomSplitPane.setRightComponent(consolePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
            .addComponent(bottomSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void topDividerChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_topDividerChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            int newValue = (int) evt.getNewValue();
            properties.put("topsplitpanedividerlocation", Integer.toString(newValue));
        }
    }//GEN-LAST:event_topDividerChange

    private void bottomDividerChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_bottomDividerChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            int newValue = (int) evt.getNewValue();
            properties.put("bottomsplitpanedividerlocation", Integer.toString(newValue));
        }
    }//GEN-LAST:event_bottomDividerChange

    private void windowResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_windowResized
        Dimension size = getSize();
        properties.put("windowwidth", Integer.toString(size.width));
        properties.put("windowheight", Integer.toString(size.height));
    }//GEN-LAST:event_windowResized

    private void windowMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_windowMoved
        Point location = getLocation();
        properties.put("windowx", Integer.toString(location.x));
        properties.put("windowy", Integer.toString(location.y));
    }//GEN-LAST:event_windowMoved

    private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectButtonActionPerformed
        JFileChooser jfc;

        if (properties.get("projectfolder") != null) {
            jfc = new JFileChooser(properties.getProperty("projectfolder"));
        } else {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setDialogTitle("Projektordner auswählen");
        int returnVal = jfc.showOpenDialog(this);

        if (returnVal != JFileChooser.ABORT && jfc.getSelectedFile() != null) {

            projectFolder = jfc.getSelectedFile();
            properties.put("projectfolder", projectFolder.getAbsolutePath());
            eventbus.post(new EventProjectOpened(projectFolder));
        }
    }//GEN-LAST:event_openProjectButtonActionPerformed

    private void removeDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDeviceButtonActionPerformed
        try {
            StickyDeviceSelected d = RootEventBus.getDefault().getStickyEvent(StickyDeviceSelected.class);
            deviceList.removeSelectedDevice();
            RootEventBus.getDefault().post(new EventConsoleMessage("Gerät entfernt: " + d.getDeviceConfig()));
        } catch (JAXBException | SAXException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Entfernen des Gerätes.", ex));
        }
    }//GEN-LAST:event_removeDeviceButtonActionPerformed

    private void addDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDeviceButtonActionPerformed
        JFileChooser jfc;

        if (properties.get("projectfolder") != null) {
            jfc = new JFileChooser(properties.getProperty("projectfolder"));
        } else {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle("Gerät hinzufügen");
        jfc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isFile() && (f.getName().endsWith(".kdevice.xml") || f.getName().endsWith(".kconfig.xml"));
            }

            @Override
            public String getDescription() {
                return "Gerätedefinition (.kdevice.xml), Gerätekonfiguration (.kconfig.xml)";
            }
        });
        int returnVal = jfc.showOpenDialog(this);

        if (returnVal != JFileChooser.ABORT && jfc.getSelectedFile() != null) {

            File selectedFile = jfc.getSelectedFile();
            try {
                DeviceConfigContainer device = new DeviceConfigContainer(selectedFile);
                SaveDeviceAsDialog.showDialog(this, projectFolder, device);
                
            } catch (JAXBException | SAXException ex) {
                RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Hinzufügen eines Gerätes.", ex));
            }

        }
    }//GEN-LAST:event_addDeviceButtonActionPerformed

    private void programmAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programmAllButtonActionPerformed
        StickyDeviceSelected selectdDevice = eventbus.getStickyEvent(StickyDeviceSelected.class);
        ProgramDialog pd = new ProgramDialog(this);
        pd.prepare(knx);
        pd.addDeviceToprogram(selectdDevice.getDeviceConfig());
        pd.setVisible(true);
    }//GEN-LAST:event_programmAllButtonActionPerformed

    private void programmDataOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programmDataOnlyButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_programmDataOnlyButtonActionPerformed

    private void groupmonitorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupmonitorButtonActionPerformed
        monitor.setVisible(true);
    }//GEN-LAST:event_groupmonitorButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        SettingsDialog sd = new SettingsDialog(this);
        sd.setLocationRelativeTo(this);
        sd.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    
    static void renderSplashFrame(Graphics2D g, int frame) {
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 400, 300);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.setFont(Font.decode(Font.MONOSPACED));
        String s = "[";

        for (int i = 0; i < frame; i++) {
            s += "\u2589";
        }
        for (int i = frame - 1; i < 10; i++) {
            s += " ";
        }

        s += "]";

        g.drawString(s, 10, 290);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            log.info("No splashscreen available");
        } else {
            Graphics2D g = splash.createGraphics();
            if (g == null) {
                log.info("g is null");
                return;
            }
            for (int i = 0; i < 10; i++) {
                renderSplashFrame(g, i);
                splash.update();
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                }
            }
            splash.close();
        }

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                log.info("LaF Name: '" + info.getName() + "'");
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Error setting Nimbus LaF. Continue with default.");
        }
//        try {
//            // Set System L&F
//            UIManager.setLookAndFeel(
//                    UIManager.getSystemLookAndFeelClassName());
//        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            // handle exception
//        }
        // handle exception
        // handle exception
        // handle exception
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Main();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDeviceButton;
    private javax.swing.JSplitPane bottomSplitPane;
    private de.konnekting.suite.ConsolePanel consolePanel;
    private de.konnekting.suite.DeviceEditor deviceEditor;
    private de.konnekting.suite.DeviceList deviceList;
    private javax.swing.JButton exitButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton groupmonitorButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JButton openProjectButton;
    private javax.swing.JButton programmAllButton;
    private javax.swing.JButton programmDataOnlyButton;
    private javax.swing.JButton removeDeviceButton;
    private javax.swing.JButton settingsButton;
    private de.konnekting.suite.StatusPanel statusPanel;
    private javax.swing.JSplitPane topSplitPane;
    // End of variables declaration//GEN-END:variables
}
