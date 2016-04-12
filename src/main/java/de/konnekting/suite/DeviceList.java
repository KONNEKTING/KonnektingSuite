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
import de.konnekting.suite.events.EventAddDevice;
import de.konnekting.suite.events.EventConsoleMessage;
import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.EventDeviceAdded;
import de.konnekting.suite.events.EventDeviceListRefresh;
import de.konnekting.suite.events.EventProjectOpened;
import de.konnekting.suite.events.EventProjectSave;
import de.konnekting.suite.events.StickyDeviceSelected;
import java.io.File;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author achristian
 */
public class DeviceList extends javax.swing.JPanel {
    
    private Logger log = LoggerFactory.getLogger(getClass());

    private final RootEventBus eventBus = RootEventBus.getDefault();
    private File projectFolder;
    private int oldSelectedIndex = -1;

    /**
     * Creates new form DeviceList
     */
    public DeviceList() {
        initComponents();
        eventBus.register(this);

    }

    public void onEvent(EventDeviceListRefresh event) {
        deviceList.invalidate();
        repaint();
    }

    public void onEvent(EventAddDevice event) {
        DeviceConfigContainer device = event.getDeviceConfig();
        deviceListModel.addElement(device);
        eventBus.post(new EventDeviceAdded(device));
        eventBus.post(new EventConsoleMessage("Gerät hinzugefügt: "+device.getIndividualAddress()+" "+device.getDescription()));
        deviceList.invalidate();
        repaint();
    }

    public void onEvent(EventProjectOpened projectOpened) {
        projectFolder = projectOpened.getProjectFolder();
        new BackgroundTask("Opening project '" + projectFolder.getName() + "'", Thread.NORM_PRIORITY) {

            @Override
            public void run() {
                deviceListModel.clear();
                File[] deviceFiles = projectFolder.listFiles((File pathname) -> pathname.isFile() && pathname.getName().endsWith(".kconfig.xml"));

                setStepsToDo(deviceFiles.length);
                for (File deviceFile : deviceFiles) {
                    try {
                        DeviceConfigContainer device = new DeviceConfigContainer(deviceFile);
                        if (device.hasConfiguration()) {
                            eventBus.post(new EventDeviceAdded(device));
                            deviceListModel.addElement(device);
                        }
                        stepDone();
                    } catch (JAXBException | SAXException ex) {
                        RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Lesen der Datei ", ex));
                    }

                }
                setDone();

            }
        };
    }

    public void onEvent(EventProjectSave event) {
        deviceListModel.saveAllToDisk();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        deviceListCellRenderer = new de.konnekting.suite.DeviceListCellRenderer();
        deviceListModel = new de.konnekting.suite.DeviceListModel();
        jScrollPane = new javax.swing.JScrollPane();
        deviceList = new javax.swing.JList();

        deviceList.setModel(deviceListModel);
        deviceList.setCellRenderer(deviceListCellRenderer);
        deviceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                selectionChanged(evt);
            }
        });
        jScrollPane.setViewportView(deviceList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectionChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_selectionChanged

        int selectedIndex = deviceList.getSelectedIndex();

        log.trace("Device selection changed: " + selectedIndex);

        if (oldSelectedIndex != selectedIndex) {
            if (selectedIndex == -1) {
                eventBus.postSticky(new StickyDeviceSelected(null));
            } else {
                DeviceConfigContainer selectedDevice = deviceListModel.getElementAt(selectedIndex);
                eventBus.postSticky(new StickyDeviceSelected(selectedDevice));
            }
        }
        oldSelectedIndex = selectedIndex;
    }//GEN-LAST:event_selectionChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList deviceList;
    private de.konnekting.suite.DeviceListCellRenderer deviceListCellRenderer;
    private de.konnekting.suite.DeviceListModel deviceListModel;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables

    void removeSelectedDevice() throws JAXBException, SAXException {
        int selectedIndex = deviceList.getSelectedIndex();
        DeviceConfigContainer device = deviceListModel.get(selectedIndex);
        // kleinster Index wird als Button ganz rechts angeordnet, größter ganz links
        Object[] options = {
            /* 0 */"Abbrechen",
            /* 1 */ "Löschen",
            };
        int result = JOptionPane.showOptionDialog(getParent(),
            "Soll die Gerätekonfiguration gelöscht werden?",
            "Löschen bestätigen",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options, options[0]);

        switch (result) {
            case 1:
                device.remove();
                deviceListModel.remove(selectedIndex);
                break;
            case 0:
            case JOptionPane.CLOSED_OPTION:
                // just do nothing
                break;
        }
    }
}
