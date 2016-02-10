/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents.groupmonitor;

import de.konnekting.deviceconfig.utils.Helper;
import de.root1.slicknx.GroupAddressEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author achristian
 */
public class GroupAddressEventContainer {
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd hh:mm:ss.SSS");
    
    private final int number;
    private final Date timestamp;
    private final GroupAddressEvent gae;

    GroupAddressEventContainer(int number, Date timestamp, GroupAddressEvent gae) {
        this.number = number;
        this.timestamp = timestamp;
        this.gae = gae;
    }

    int getNumber() {
        return number;
    }

    String getTimestampString() {
        return sdf.format(timestamp);
    }

    String getIndividualAddress() {
        return gae.getSource();
    }

    String getType() {
        return gae.getType().toString();
    }

    String getGroupAddress() {
        return gae.getDestination();
    }

    String getDataString() {
        return Helper.bytesToHex(gae.getData(), true);
    }
    
}
