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
import de.konnekting.suite.events.EventParameterChanged;
import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.StickyParamGroupSelected;
import de.konnekting.suite.utils.Utils;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class ParameterList extends javax.swing.JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private List<Parameter> list;
    private final List<ParameterListItem> parameterListItems = new ArrayList<>();
    private DeviceConfigContainer device;
    private final Color colorNormal = new JPanel().getBackground();
    private final Color colorBrighter = Utils.brighter(colorNormal);

    /**
     * Creates new form ParameterList
     */
    public ParameterList() {
        RootEventBus.getDefault().registerSticky(this);
        initComponents();
    }

    public void onEvent(EventParameterChanged event) {
        refreshParameterVisibility();
    }

    public void onEvent(StickyParamGroupSelected event) {

        list = event.getList();

        removeAll();
        parameterListItems.clear();
        initComponents();
        numberOfParamsLabel.setText(java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language").getString("ParameterList.numberOfParamsLabel.text") + list.size());

        GridBagConstraints gridBagConstraints;

        int i = 0;

        for (Parameter param : list) {
            ParameterListItem item = new ParameterListItem();
            parameterListItems.add(item);
            item.setParam(param.getId(), device);
            if (i % 2 == 1) {
                item.setBackground(colorBrighter);
            } 
            i++;

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 0.1;

            add(item, gridBagConstraints);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767)), gridBagConstraints);

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

        numberOfParamsLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language"); // NOI18N
        numberOfParamsLabel.setText(bundle.getString("ParameterList.numberOfParamsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 16, 0);
        add(numberOfParamsLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel numberOfParamsLabel;
    // End of variables declaration//GEN-END:variables

    void setDevice(DeviceConfigContainer device) {
        this.device = device;
        removeAll();
        initComponents();
        numberOfParamsLabel.setText(java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language").getString("ParameterList.numberOfParamsLabel.text"));
    }

    private void refreshParameterVisibility() {
        log.info("refresing visibility");

        int i = 0;
        for (ParameterListItem item : parameterListItems) {
            boolean visible = item.updateParameterVisibility();
            if (visible) {
                if (i % 2 == 1) {
                    item.setBackground(colorBrighter);
                } else {
                    item.setBackground(colorNormal);
                }
                i++;
            }

        }
        validate();
    }
}
