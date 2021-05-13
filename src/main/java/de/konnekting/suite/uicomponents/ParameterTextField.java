/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents;

import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.suite.events.EventParameterChanged;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
import de.root1.rooteventbus.RootEventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (isInputValid()) {
                    RootEventBus.getDefault().post(new EventParameterChanged());
                }
            }
        });

//        addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                if (isInputValid()) {
//                    RootEventBus.getDefault().post(new EventParameterChanged());
//                }
//            }
//        });

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isInputValid()) {
                    RootEventBus.getDefault().post(new EventParameterChanged());
                }
            }

        });
    }

    @Override
    public boolean isParameterVisible() {
        return device.isParameterEnabled(param);
    }

}
