/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.events;

import de.konnekting.deviceconfig.DeviceConfigContainer;

/**
 *
 * @author achristian
 */
public class EventDeviceRemoved {

    private final DeviceConfigContainer device;

    public EventDeviceRemoved(DeviceConfigContainer device) {
        this.device = device;
    }

    public DeviceConfigContainer getDevice() {
        return device;
    }
    
}
