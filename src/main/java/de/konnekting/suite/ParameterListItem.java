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
import de.konnekting.suite.uicomponents.NumberParameterTextField;
import de.konnekting.suite.uicomponents.ParameterCombobox;
import de.konnekting.suite.uicomponents.ParameterDependency;
import de.konnekting.suite.uicomponents.RawParameterTextField;
import de.konnekting.suite.uicomponents.StringParameterTextField;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.Parameter.Value;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
import de.konnekting.xml.konnektingdevice.v0.ParamType;
import java.awt.GridBagConstraints;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class ParameterListItem extends javax.swing.JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JComponent comp;
    private int id;


    /**
     * Creates new form ParameterListItem
     */
    public ParameterListItem() {
        initComponents();
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

        descriptionLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        descriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        descriptionLabel.setMaximumSize(new java.awt.Dimension(1000, 50));
        descriptionLabel.setMinimumSize(new java.awt.Dimension(200, 27));
        descriptionLabel.setName(""); // NOI18N
        descriptionLabel.setPreferredSize(new java.awt.Dimension(230, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        add(descriptionLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    // End of variables declaration//GEN-END:variables

    

    void setParam(int id, DeviceConfigContainer device) {
        this.id = id;

        Parameter param = device.getParameter(id);

        String desc = param.getDescription();
        Value valueObject = param.getValue();
        String options = valueObject.getOptions();

        descriptionLabel.setText("<html>" + desc + "</html>");

        ParameterConfiguration conf = device.getParameterConfig(id);

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        if (options == null || options.isEmpty()) {

            JTextField tfield = null;
            
            ParamType paramType = param.getValue().getType();
            switch (paramType) {
                case STRING_11:
                    tfield = new StringParameterTextField(device, param, conf);
                    break;
                case RAW_1:
                case RAW_2:
                case RAW_3:
                case RAW_4:
                case RAW_5:
                case RAW_6:
                case RAW_7:
                case RAW_8:
                case RAW_9:
                case RAW_10:
                case RAW_11:
                    tfield = new RawParameterTextField(device, param, conf);
                    break;
                case INT_8:
                case UINT_8:
                case INT_16:
                case UINT_16:
                case INT_32:
                case UINT_32:
                default:
                    tfield = new NumberParameterTextField(device, param, conf);
            }
            comp = tfield;
        } else {
            ParameterCombobox combobox = new ParameterCombobox(device, param, conf);
            comp = combobox;
        }

        comp.setMaximumSize(new java.awt.Dimension(230, 27));
        comp.setMinimumSize(new java.awt.Dimension(230, 27));
        comp.setPreferredSize(new java.awt.Dimension(230, 27));

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 1, 3);
        add(comp, gridBagConstraints);

    }

    boolean updateParameterVisibility() {
        if (comp instanceof ParameterDependency) {
            ParameterDependency pd = (ParameterDependency) comp;
            boolean parameterVisible = pd.isParameterVisible();
            
            log.info("Setting param #{} to visible={}", id, parameterVisible);
            setVisible(parameterVisible);
            return parameterVisible;
        }
        return true;
    }
}
