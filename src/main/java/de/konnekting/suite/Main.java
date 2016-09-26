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

import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.deviceconfig.EventDeviceChanged;
import de.konnekting.deviceconfig.utils.Helper;
import de.konnekting.suite.events.EventConsoleMessage;
import de.konnekting.suite.events.EventDeviceAdded;
import de.konnekting.suite.events.EventDeviceRemoved;
import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.EventProjectOpened;
import de.konnekting.suite.events.EventSaveSettings;
import de.konnekting.suite.events.StickyDeviceSelected;
import de.konnekting.suite.uicomponents.groupmonitor.GroupMonitorFrame;
import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 *
 * @author achristian
 */
public class Main extends javax.swing.JFrame {

    static {
        String level = System.getProperty("debuglevel", "info");

        String logFolder = new File(System.getProperty("logfolder", ".")).getAbsolutePath().replace("\\", "/");

        System.out.println("ENABLING LOGGING with level: " + level);
        System.out.println("Using log folder: " + logFolder);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(baos);
            osw.write("handlers= java.util.logging.FileHandler, java.util.logging.ConsoleHandler" + "\n");
            osw.write("java.util.logging.FileHandler.pattern = " + logFolder + "/KonnektingSuite.log" + "\n");
            osw.write("java.util.logging.FileHandler.limit = 10000000" + "\n");
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
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N

    private File projectFolder;
    private final RootEventBus eventbus = RootEventBus.getDefault();
    private static final Properties PROPERTIES = new Properties();
    static Properties applicationProperties = new Properties();
    static File propertiesFile = new File(new File(System.getProperty("user.home")), "KonnektingSuite.properties");
    private Knx knx;
    private final GroupMonitorFrame monitor;
    private final ProjectSaver projectSaver;

    public static Properties getProperties() {
        return PROPERTIES;
    }

    /**
     * Creates new form Main
     */
    public Main() {
        projectSaver = new ProjectSaver(this);

//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // let the exit handle by WindowAdapter
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitButton.doClick();
            }
        });
        initComponents();

        boolean debug = Boolean.getBoolean("de.root1.slicknx.konnekting.debug");
        String versionMsg = "KONNEKTING Suite - Version " + applicationProperties.getProperty("application.version", "n/a") + " Build " + applicationProperties.getProperty("application.build", "n/a") + (debug ? " DEBUG MODE!" : "");
        log.info(versionMsg);
        RootEventBus.getDefault().post(new EventConsoleMessage(versionMsg));
        RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.operatingSystem") + ": " + System.getProperty("os.name")));

        removeDeviceButton.setEnabled(false);
        programmAllButton.setEnabled(false);
        programmDataOnlyButton.setEnabled(false);
        programComObjOnlyButton.setEnabled(false);
        programParamOnlyButton.setEnabled(false);
        addDeviceButton.setEnabled(false);
        eventbus.register(this);

        monitor = new GroupMonitorFrame(this);
        connectKnx();

        Dimension size = new Dimension();
        size.width = Integer.parseInt(PROPERTIES.getProperty("windowwidth", "1024"));
        size.height = Integer.parseInt(PROPERTIES.getProperty("windowheight", "768"));
        super.setSize(size);
        Point location = new Point();
        location.x = Integer.parseInt(PROPERTIES.getProperty("windowx", "" + Integer.MIN_VALUE));
        location.y = Integer.parseInt(PROPERTIES.getProperty("windowy", "" + Integer.MIN_VALUE));
        if (location.x == Integer.MIN_VALUE && location.y == Integer.MIN_VALUE) {
            super.setLocationRelativeTo(null);
        } else {
            super.setLocation(location);
        }
        topSplitPane.setDividerLocation(Integer.parseInt(PROPERTIES.getProperty("topsplitpanedividerlocation", "180")));
        bottomSplitPane.setDividerLocation(Integer.parseInt(PROPERTIES.getProperty("bottomsplitpanedividerlocation", "300")));


        boolean lastFolder = Boolean.parseBoolean(PROPERTIES.getProperty(SettingsDialog.PROP_STARTUP_LASTFOLDER, "false"));
        boolean askFolder = Boolean.parseBoolean(PROPERTIES.getProperty(SettingsDialog.PROP_STARTUP_ASKFOLDER, "true"));

        if (askFolder) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openProjectButton.doClick();
                }
            });

        } else if (lastFolder) {
            projectFolder = new File(PROPERTIES.getProperty("projectfolder", System.getProperty("user.home")));
            eventbus.post(new EventProjectOpened(projectFolder));
        }

        List<Image> iconList = new ArrayList<>();
        try {
            iconList.add(ImageIO.read(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-Suite-16x16-Icon.png")));
            iconList.add(ImageIO.read(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-Suite-32x32-Icon.png")));
            iconList.add(ImageIO.read(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-Suite-64x64-Icon.png")));
            iconList.add(ImageIO.read(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-Suite-128x128-Icon.png")));
            iconList.add(ImageIO.read(getClass().getClassLoader().getResource("de/konnekting/suite/icons/KONNEKTING-Suite-256x256-Icon.png")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setIconImages(iconList);

        setVisible(true);
    }

    private void connectKnx() {
        String access = PROPERTIES.getProperty(SettingsDialog.PROP_ACCESS, SettingsDialog.ACCESS_ROUTING);
        String routingMulticast = PROPERTIES.getProperty(SettingsDialog.PROP_ROUTING_MULTICASTIP, "224.0.23.12");
        String tunnelingIp = PROPERTIES.getProperty(SettingsDialog.PROP_TUNNELING_IP, "192.168.0.100");
        String tpuartDevice = PROPERTIES.getProperty(SettingsDialog.PROP_TPUART_DEVICE, "COM3");
        String individualAddress = PROPERTIES.getProperty(SettingsDialog.PROP_INDIVIDUALADDRESS, "1.0.254");

        try {

            String defaultNi = NetworkInterface.getNetworkInterfaces().nextElement().getName();
            String routingNetworkinterface = PROPERTIES.getProperty(SettingsDialog.PROP_ROUTING_MULTICASTNETWORKINTERFACE, defaultNi);

            switch (access.toUpperCase()) {
                case SettingsDialog.ACCESS_ROUTING:
                    log.info("Starting in ROUTING mode: {}@{} on {}", individualAddress, routingMulticast, routingNetworkinterface);
                    RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.knxConnection") + "IP-Router: " + individualAddress + "@" + routingMulticast + "/" + routingNetworkinterface));
                    knx = new Knx(de.konnekting.suite.utils.Utils.getNetworkinterfaceByName(routingNetworkinterface), InetAddress.getByName(routingMulticast));
                    knx.setLoopbackMode(true);
                    break;
                case SettingsDialog.ACCESS_TUNNELING:
                    log.info("Starting in TUNNELING mode: {}@{}", individualAddress, tunnelingIp);
                    RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.knxConnection") + "IP-Interface: " + individualAddress + "@" + tunnelingIp));
                    knx = new Knx(InetAddress.getByName(tunnelingIp));
                    break;
                case SettingsDialog.ACCESS_TPUART:
                    log.info("Starting in TPUART mode: {}@{}", individualAddress, tpuartDevice);
                    RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.knxConnection") + "TPUART: " + individualAddress + "@" + tpuartDevice));
                    knx = new Knx(Knx.SerialType.TPUART, tpuartDevice);
                    break;
                case SettingsDialog.ACCESS_OFF:
                    log.info("Starting in offline mode");
                    RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.knxConnection") + "OFFLINE"));
                    knx = null;
                    break;
                default:
                    log.info("Error. Unknown ACCESS TYPE: " + access);
                    System.exit(1);
            }

            if (knx != null) {
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

                    public void process() {
                        RootEventBus.getDefault().post(new EventConsoleMessage(bundle.getString("MainWindow.ConsoleMsg.detectedTelegram")));
                        knx.removeGroupAddressListener("*", this);
                    }
                });
                
                monitor.setKnx(knx);
            }

        } catch (KnxException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Öffnen der KNX Verbindung: " + access, ex));
            log.error("Error creating knx access.", ex);
        } catch (KNXIllegalArgumentException | UnknownHostException | SocketException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Öffnen der KNX Verbindung.", ex));
            log.error("Error creating knx access.", ex);
        }
    }

    private void saveSettings() {
        try {
            log.info("Saving settings");
            PROPERTIES.store(new FileWriter(propertiesFile), "This is KONNEKTING Suite configuration file");
        } catch (IOException ex) {
            RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Schreiben der Einstellungen.", ex));
        }
    }

    public void onEvent(EventSaveSettings evt) {
        new BackgroundTask("Saving settings...") {
            @Override
            public void run() {
                setStepsToDo(3);
                saveSettings();
                stepDone();
                if (knx != null) {
                    log.info("Replacing KNX connection NOW");
                    knx.close();
                    stepDone();
                    connectKnx();
                    stepDone();
                    setDone();
                    log.info("Replacing KNX connection NOW *DONE*");
                }
            }
        };
    }

    public void onEvent(EventDeviceAdded evt) {
        log.info("Added device: {}", evt.getDeviceConfig());
        projectSaver.add(evt.getDeviceConfig());
    }

    public void onEvent(EventProjectOpened evt) {

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
        programComObjOnlyButton.setEnabled(programmable);
        programParamOnlyButton.setEnabled(programmable);
    }

    public void onEvent(EventDeviceChanged evt) {
        updateProgButtons();
        if (evt.getDeviceConfig() != null) {
            projectSaver.add(evt.getDeviceConfig());
        }
    }

    public void onEvent(EventDeviceRemoved evt) {
        updateProgButtons();
        projectSaver.remove(evt.getDevice());
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
        programComObjOnlyButton = new javax.swing.JButton();
        programParamOnlyButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        groupmonitorButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        aboutButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        statusPanel = new de.konnekting.suite.StatusPanel();
        bottomSplitPane = new javax.swing.JSplitPane();
        topSplitPane = new javax.swing.JSplitPane();
        deviceList = new de.konnekting.suite.DeviceList();
        deviceEditor = new de.konnekting.suite.DeviceEditor();
        consolePanel = new de.konnekting.suite.ConsolePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N
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

        programComObjOnlyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/ledyellow.png"))); // NOI18N
        programComObjOnlyButton.setToolTipText(bundle.getString("Main.programComObjOnlyButton.toolTipText")); // NOI18N
        programComObjOnlyButton.setFocusable(false);
        programComObjOnlyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        programComObjOnlyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        programComObjOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programComObjOnlyButtonActionPerformed(evt);
            }
        });
        jToolBar.add(programComObjOnlyButton);

        programParamOnlyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/ledgreen.png"))); // NOI18N
        programParamOnlyButton.setToolTipText(bundle.getString("Main.programParamOnlyButton.toolTipText")); // NOI18N
        programParamOnlyButton.setFocusable(false);
        programParamOnlyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        programParamOnlyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        programParamOnlyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programParamOnlyButtonActionPerformed(evt);
            }
        });
        jToolBar.add(programParamOnlyButton);
        jToolBar.add(jSeparator3);

        groupmonitorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/display.png"))); // NOI18N
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

        aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/help.png"))); // NOI18N
        aboutButton.setToolTipText(bundle.getString("Main.aboutButton.toolTipText")); // NOI18N
        aboutButton.setFocusable(false);
        aboutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });
        jToolBar.add(aboutButton);

        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/konnekting/suite/icons/exit.png"))); // NOI18N
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
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            PROPERTIES.put("topsplitpanedividerlocation", Integer.toString(newValue));
        }
    }//GEN-LAST:event_topDividerChange

    private void bottomDividerChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_bottomDividerChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            int newValue = (int) evt.getNewValue();
            PROPERTIES.put("bottomsplitpanedividerlocation", Integer.toString(newValue));
        }
    }//GEN-LAST:event_bottomDividerChange

    private void windowResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_windowResized
        Dimension size = getSize();
        PROPERTIES.put("windowwidth", Integer.toString(size.width));
        PROPERTIES.put("windowheight", Integer.toString(size.height));
    }//GEN-LAST:event_windowResized

    private void windowMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_windowMoved
        Point location = getLocation();
        PROPERTIES.put("windowx", Integer.toString(location.x));
        PROPERTIES.put("windowy", Integer.toString(location.y));
    }//GEN-LAST:event_windowMoved

    private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectButtonActionPerformed
        JFileChooser jfc;

        if (PROPERTIES.get("projectfolder") != null) {
            jfc = new JFileChooser(PROPERTIES.getProperty("projectfolder"));
        } else {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setDialogTitle("Projektordner auswählen");
        int returnVal = jfc.showOpenDialog(this);

        if (returnVal != JFileChooser.ABORT && jfc.getSelectedFile() != null) {

            projectFolder = jfc.getSelectedFile();
            PROPERTIES.put("projectfolder", projectFolder.getAbsolutePath());
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

        if (PROPERTIES.get("projectfolder") != null) {
            jfc = new JFileChooser(PROPERTIES.getProperty("projectfolder"));
        } else {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle("Gerät hinzufügen");
        jfc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return (f.isFile() && (f.getName().endsWith(".kdevice.xml") || f.getName().endsWith(".kconfig.xml"))) || f.isDirectory();
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
        pd.prepare(knx, true, true, true);
        pd.addDeviceToprogram(selectdDevice.getDeviceConfig());
        pd.setVisible(true);
    }//GEN-LAST:event_programmAllButtonActionPerformed

    private void programmDataOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programmDataOnlyButtonActionPerformed
        StickyDeviceSelected selectdDevice = eventbus.getStickyEvent(StickyDeviceSelected.class);
        ProgramDialog pd = new ProgramDialog(this);
        pd.prepare(knx, false, true, true);
        pd.addDeviceToprogram(selectdDevice.getDeviceConfig());
        pd.setVisible(true);
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
        if (knx != null) {
            knx.close();
        }
        projectSaver.setVisible(true);
        saveSettings();
        dispose();
        log.info("SUITE EXITING");
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void programComObjOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programComObjOnlyButtonActionPerformed
        StickyDeviceSelected selectdDevice = eventbus.getStickyEvent(StickyDeviceSelected.class);
        ProgramDialog pd = new ProgramDialog(this);
        pd.prepare(knx, false, true, false);
        pd.addDeviceToprogram(selectdDevice.getDeviceConfig());
        pd.setVisible(true);
    }//GEN-LAST:event_programComObjOnlyButtonActionPerformed

    private void programParamOnlyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programParamOnlyButtonActionPerformed
        StickyDeviceSelected selectdDevice = eventbus.getStickyEvent(StickyDeviceSelected.class);
        ProgramDialog pd = new ProgramDialog(this);
        pd.prepare(knx, false, false, true);
        pd.addDeviceToprogram(selectdDevice.getDeviceConfig());
        pd.setVisible(true);
    }//GEN-LAST:event_programParamOnlyButtonActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        new AboutDialog(this).setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    /**
     * Load/Reload user setting properties
     */
    private static void loadProperties() {
        try {
            PROPERTIES.clear();
            PROPERTIES.load(new FileReader(propertiesFile));

            Iterator<Map.Entry<Object, Object>> iter = PROPERTIES.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Object, Object> entry = iter.next();
                log.info("Property: {}={}", entry.getKey(), entry.getValue());
            }

        } catch (FileNotFoundException ex) {
            log.info("Properties file not found. Skip to defaults.");
        } catch (IOException ex) {
            log.error("Error reading setting properties", ex);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception occured in thread [" + t.getName() + "]", e);
                RootEventBus.getDefault().post(new EventConsoleMessage("Uncaught exception occured in thread [" + t.getName() + "]", e));
            }
        });

        log.info("Locale: {}", Locale.getDefault());
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                log.info("LaF Name: '" + info.getName() + "'");
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Error setting Nimbus LaF. Continue with default.");
        }

        final SplashPanel splashPanel = new SplashPanel();

        Thread t = new Thread("Load properties") {
            @Override
            public void run() {
                loadProperties();
                try {
                    applicationProperties.load(getClass().getResourceAsStream("/properties/application.properties"));
                } catch (IOException ex) {
                    log.error("Error reading application properties", ex);
                }
                splashPanel.setVersionText("Version " + applicationProperties.getProperty("application.version", "n/a") + " Build " + applicationProperties.getProperty("application.build", "n/a") + (Boolean.getBoolean("de.root1.slicknx.konnekting.debug") ? " DEBUG MODE!" : ""));
            }

        };
        t.start();

        for (int i = 1; i <= 100; i++) {
            splashPanel.setProgress(i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Main();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
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
    private javax.swing.JButton programComObjOnlyButton;
    private javax.swing.JButton programParamOnlyButton;
    private javax.swing.JButton programmAllButton;
    private javax.swing.JButton programmDataOnlyButton;
    private javax.swing.JButton removeDeviceButton;
    private javax.swing.JButton settingsButton;
    private de.konnekting.suite.StatusPanel statusPanel;
    private javax.swing.JSplitPane topSplitPane;
    // End of variables declaration//GEN-END:variables
}
