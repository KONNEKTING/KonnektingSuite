/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents;

import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;

/**
 *
 * @author achristian
 */
public abstract class ParameterTextField extends ValidateableTextField implements ParameterDependency {
    
    protected DeviceConfigContainer device;
    protected Parameter param;
    protected ParameterConfiguration conf;

    ParameterTextField(DeviceConfigContainer device, Parameter param, ParameterConfiguration conf) {
        this.device = device;
        this.param = param;
        this.conf = conf;
    }
    
    @Override
    public boolean isParameterVisible() {
        return device.isParameterEnabled(param);
    }
    
}
