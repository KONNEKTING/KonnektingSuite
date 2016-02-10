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
package de.konnekting.suite.uicomponents.groupmonitor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author achristian
 */
public class GroupMonitorTableModel extends DefaultTableModel {

    private final List<GroupAddressEventContainer> events = new ArrayList<>();

    public void addEvent(GroupAddressEventContainer event) {
        events.add(event);
        
        // prevent overflow
        if (events.size()>10000) {
            events.remove(0);
        }
        
        fireTableRowsInserted(events.size(), events.size());
    }

    @Override
    public int getRowCount() {
        if (events == null) {
            return 0;
        }
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int columnIndex) {
        // 0 Nr
        // 1 Timestamp
        // 2 IA
        // 3 Type
        // 4 GA
        // 5 data
        switch (columnIndex) {
            case 0:
                return "Nr";
            case 1:
                return "Zeit";
            case 2:
                return "Sender";
            case 3:
                return "Telegramm";            
            case 4:
                return "Gruppenadresse";
            case 5:
                return "Daten";
            default:
                throw new IllegalArgumentException("Column " + columnIndex + " not known");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // 0 Nr
        // 1 Timestamp
        // 2 IA
        // 3 Type
        // 4 GA
        // 5 data          
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return String.class;
            default:
                throw new IllegalArgumentException("Column " + columnIndex + " not known");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 0 Nr
        // 1 Timestamp
        // 2 IA
        // 3 Type
        // 4 GA
        // 5 data 
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return false;
            default:
                throw new IllegalArgumentException("Column " + columnIndex + " not known");

        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GroupAddressEventContainer gaec = events.get(rowIndex);
        // 0 Nr
        // 1 Timestamp
        // 2 IA
        // 3 Type
        // 4 GA
        // 5 data 
        switch (columnIndex) {
            case 0:
                return gaec.getNumber();
            case 1:
                return gaec.getTimestampString();
            case 2:
                return gaec.getIndividualAddress();
            case 3:
                return gaec.getType();
            case 4:
                return gaec.getGroupAddress();
            case 5:
                return gaec.getDataString();
            default:
                throw new IllegalArgumentException("Column " + columnIndex + " not known");
        }
    }

    void clear() {
        events.clear();
        fireTableDataChanged();
    }
    
    


}
