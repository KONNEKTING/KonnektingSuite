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
import de.konnekting.suite.utils.Utils;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author achristian
 */
public class DeviceListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        DeviceConfigContainer device = (DeviceConfigContainer) value;
        
        String description = device.getIndividualAddress()+ " - " + device.getDescription();

        if (description == null || description.length() == 0) {
            description = device.getDeviceName() + " [" + device.getManufacturerName() + "]";
        }

        Component listCellRendererComponent = super.getListCellRendererComponent(list, description, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.

        if (index % 2 == 1 && !isSelected) {
            listCellRendererComponent.setBackground(Utils.brighter(listCellRendererComponent.getBackground()));
        }

        return listCellRendererComponent;
    }

}
