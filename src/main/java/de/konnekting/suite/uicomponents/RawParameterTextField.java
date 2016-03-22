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
package de.konnekting.suite.uicomponents;

import de.konnekting.deviceconfig.utils.ReadableValue2Bytes;
import de.konnekting.deviceconfig.utils.Bytes2ReadableValue;
import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.deviceconfig.utils.Helper;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
import de.konnekting.xml.konnektingdevice.v0.ParameterType;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class RawParameterTextField extends ValidateableTextField {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Parameter param;
    private final ParameterType paramType;
    private final DeviceConfigContainer device;

    private String validationError = "";
    private String lastText;
    private int length;

    public RawParameterTextField(DeviceConfigContainer device, Parameter param, ParameterConfiguration conf) {
        this.device = device;
        this.param = param;

        String type = param.getValue().getType().toUpperCase();
        paramType = ParameterType.valueOf(type);

        switch (paramType) {
            case RAW1:
                length = 1;
                break;
            case RAW2:
                length = 2;
                break;
            case RAW3:
                length = 3;
                break;
            case RAW4:
                length = 4;
                break;
            case RAW5:
                length = 5;
                break;
            case RAW6:
                length = 6;
                break;
            case RAW7:
                length = 7;
                break;
            case RAW8:
                length = 8;
                break;
            case RAW9:
                length = 9;
                break;
            case RAW10:
                length = 10;
                break;
            case RAW11:
            default:
                length = 11;
                break;
        }

        // if min is set, max must also be set --> ensure and catch exception?!
        byte[] value = conf.getValue();
        if (value == null) {
            value = param.getValue().getDefault();
            // enforce default value to be set/saved
            device.setParameterValue(param.getId(), value);
        }

        setValue(value);
    }

    @Override
    public String getValidationErrorMessage() {
        return validationError;
    }

    @Override
    public boolean isInputValid() {

        boolean valid = false;

        if (Helper.isRawType(paramType)) {
            try {
                
                
                byte[] hexToBytes = Helper.hexToBytes(getText());
                if (hexToBytes.length!=length) {
                    if (hexToBytes.length<length) {
                        validationError = "HEX value is too short. ";
                    } else if (hexToBytes.length>length) {
                        validationError = "HEX value is too long. ";
                    }
                    validationError+="Required: "+(length*2)+" characters/"+length+" bytes. Found: "+(hexToBytes.length*2)+" characters/"+hexToBytes.length+" bytes!";
                    valid=false;
                } else {
                    valid=true;
                }
            } catch (NumberFormatException ex) {
                validationError = "Not a hex value: [" + getText()+"]";
            }
        } else {
            validationError = "Unsupported type?! " + paramType.toString();
            valid = false;
        }

        if (valid) {
            String text = getText();
            if (text != null && !text.equals(lastText)) {
                byte[] value = (byte[]) getValue();
                device.setParameterValue(param.getId(), value);
            }
            lastText = text;
        }
        return valid;
    }

    @Override
    public byte[] getValue() {
        byte[] value = Helper.hexToBytes(getText());
        return value;
    }

    private void setValue(byte[] value) {
        if (value == null) {
            setText("");
            return;
        }
        
        String readableValue = Helper.bytesToHex(value);
        setText(readableValue);
    }

}
