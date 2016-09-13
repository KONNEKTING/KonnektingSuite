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

import de.konnekting.deviceconfig.utils.ReflectionIdComparator;
import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.suite.events.EventParameterChanged;
import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.StickyDeviceSelected;
import de.konnekting.suite.events.StickyParamGroupSelected;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class ParameterPanel extends javax.swing.JPanel {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private DeviceConfigContainer device;
    private List<ParameterGroup> paramGroups = new ArrayList<>();
    private boolean alreadyUpdating = true;

    /**
     * Creates new form ParameterPanel
     */
    public ParameterPanel() {
        RootEventBus.getDefault().registerSticky(this);
        initComponents();
    }

    public void onEvent(StickyDeviceSelected ev) {
        
        device = ev.getDeviceConfig();

        if (device==null) {
            deviceDescriptionLabel.setText(java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language").getString("ParameterPanel.deviceDescriptionLabel.text"));  
            groupList.setListData(new String[]{});
            return;
        } 
        parameterList.setDevice(device);
        
        deviceDescriptionLabel.setText(java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language").getString("ParameterPanel.deviceDescriptionLabel.text")+device.getIndividualAddress() +" "+ device.getDescription());

        updateGroupsInList();
    }
    
    public void onEvent(EventParameterChanged event) {
        if (!alreadyUpdating) {
            updateGroupsInList();
        }
    }
    
    private void updateGroupsInList() {
        alreadyUpdating=true;
        log.info("refresing groups in list");
        
        int selectedIndex = groupList.getSelectedIndex();
        
        Object selectedElement = null;
        
        if (selectedIndex!=-1) {
            selectedElement = groupList.getModel().getElementAt(selectedIndex);
        }
        
        List<ParameterGroup> groups = device.getParameterGroups();

        List<String> groupnames = new ArrayList<>();
        paramGroups.clear();
        for (ParameterGroup group : groups) {
            if (device.isParameterGroupEnabled(group)) {
                groupnames.add(group.getName());
            }
        };

        String[] groupNamesArray = groupnames.toArray(new String[0]);
        Arrays.sort(groupNamesArray, new ReflectionIdComparator());
        
        groupList.setListData(groupNamesArray);
        
        
        // start with first group
        if (selectedElement==null) {
            groupList.setSelectedIndex(0);
        } else {
            // select last selected element
            int elements = groupList.getModel().getSize();
            for(int i=0;i<elements;i++){
                Object elementAt = groupList.getModel().getElementAt(i);
                if (elementAt.equals(selectedElement)) {
                    groupList.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        validate();
        alreadyUpdating=false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        paramGroupListCellRenderer = new de.konnekting.suite.ParamGroupListCellRenderer();
        deviceDescriptionLabel = new javax.swing.JLabel();
        groupScrollPanel = new javax.swing.JScrollPane();
        groupList = new javax.swing.JList();
        parameterListScrollPane = new javax.swing.JScrollPane();
        parameterListScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        parameterList = new de.konnekting.suite.ParameterList();

        setName(""); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        deviceDescriptionLabel.setBackground(new java.awt.Color(255, 255, 204));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N
        deviceDescriptionLabel.setText(bundle.getString("ParameterPanel.deviceDescriptionLabel.text")); // NOI18N
        deviceDescriptionLabel.setAlignmentY(0.0F);
        deviceDescriptionLabel.setMaximumSize(new java.awt.Dimension(2000, 15));
        deviceDescriptionLabel.setMinimumSize(new java.awt.Dimension(230, 15));
        deviceDescriptionLabel.setPreferredSize(new java.awt.Dimension(230, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
        add(deviceDescriptionLabel, gridBagConstraints);

        groupScrollPanel.setMaximumSize(null);
        groupScrollPanel.setMinimumSize(new java.awt.Dimension(230, 800));
        groupScrollPanel.setPreferredSize(new java.awt.Dimension(230, 800));

        groupList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        groupList.setCellRenderer(paramGroupListCellRenderer);
        groupList.setMinimumSize(new java.awt.Dimension(180, 90));
        groupList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                groupListValueChanged(evt);
            }
        });
        groupScrollPanel.setViewportView(groupList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.1;
        add(groupScrollPanel, gridBagConstraints);

        parameterListScrollPane.setViewportView(parameterList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(parameterListScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void groupListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_groupListValueChanged
        if (groupList.getSelectedIndex() != -1) {
            String selectedGroup = (String) groupList.getModel().getElementAt(groupList.getSelectedIndex());

            List<Parameter> params = device.getParameterGroup(selectedGroup);
            RootEventBus.getDefault().postSticky(new StickyParamGroupSelected(params));
        }
    }//GEN-LAST:event_groupListValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel deviceDescriptionLabel;
    private javax.swing.JList groupList;
    private javax.swing.JScrollPane groupScrollPanel;
    private de.konnekting.suite.ParamGroupListCellRenderer paramGroupListCellRenderer;
    private de.konnekting.suite.ParameterList parameterList;
    private javax.swing.JScrollPane parameterListScrollPane;
    // End of variables declaration//GEN-END:variables
}
