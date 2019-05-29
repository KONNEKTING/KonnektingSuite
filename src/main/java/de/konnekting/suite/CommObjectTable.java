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
import de.konnekting.deviceconfig.utils.Helper;
import de.konnekting.suite.events.EventParameterChanged;
import de.konnekting.suite.events.StickyDeviceSelected;
import de.konnekting.suite.uicomponents.GroupAddressTextField;
import de.konnekting.xml.konnektingdevice.v0.CommObjectConfiguration;
import de.root1.rooteventbus.RootEventBus;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class CommObjectTable extends javax.swing.JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    class GaEditor extends AbstractCellEditor implements TableCellEditor {

        @Override
        public Object getCellEditorValue() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private final TableCellEditor gaEditor = new GaEditor() {

        JLabel label = new JLabel();
        private CommObjectConfiguration conf;

        // constructor
        {
            label.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        GroupAddressInputDialog dialog = new GroupAddressInputDialog((JFrame) SwingUtilities.getWindowAncestor(label));
                        dialog.setCommObjectConfig(conf);
                        dialog.setVisible(true);

                        updateLabel();

                        fireEditingStopped();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // ignore
                }
            });
        }

        private void updateLabel() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < conf.getGroupAddress().size(); i++) {
                sb.append(conf.getGroupAddress().get(i));
                if (i < conf.getGroupAddress().size() - 1) {
                    sb.append(", ");
                }
            }
            label.setText(sb.toString());
        }

        @Override
        public Object getCellEditorValue() {
            /**
             * this method is called by fireEditingStopped() to get the value from editor and place it into model
             * As the "conf" object is direclty modified by this editor
             * 
             * this is more like a hack, but makes life easier if the underlying data changes (not only GA, but also name?!)
             */
            return ""; 
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            DeviceConfigContainer device = dataModel.getDeviceData();

            int comObjId = (Short)dataModel.getValueAt(row, 0);

            this.conf = device.getCommObjectConfiguration(comObjId);

            updateLabel();

            return label;
        }

    };

    /**
     * Creates new form CommObjectTable
     */
    public CommObjectTable() {
        RootEventBus.getDefault().registerSticky(this);

        initComponents();
        table.setRowHeight(27); // enough space for textfields
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        table.putClientProperty("terminateEditOnFocusLost", true);

    }

    public void onEvent(StickyDeviceSelected ev) {

        // save all current data, end input
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
            log.debug("Stop editing before applying new device to table");
        }

        // set new device data
        dataModel.setDeviceData(ev.getDeviceConfig());
    }

    // Update comobj table when parameter has changed (check dependencies...)
    public void onEvent(EventParameterChanged ev) {
        dataModel.refreshCommObjVisibility();
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

        dataModel = new de.konnekting.suite.CommObjectTableModel();
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(){
            public TableCellEditor getCellEditor(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);
                switch(modelColumn) {
                    case 5:
                    return gaEditor;
                    default:
                    return super.getCellEditor(row, column);
                }
            }
        }
        ;

        setRequestFocusEnabled(false);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {1.0};
        layout.rowWeights = new double[] {1.0};
        setLayout(layout);

        table.setModel(dataModel);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 524;
        gridBagConstraints.ipady = 147;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.konnekting.suite.CommObjectTableModel dataModel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
