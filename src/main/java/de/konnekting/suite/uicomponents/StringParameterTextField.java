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
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
import de.konnekting.xml.konnektingdevice.v0.ParamType;
import java.awt.HeadlessException;
import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class StringParameterTextField extends ParameterTextField {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ParamType paramType;

    private String validationError = "";
    private String lastText;

    public StringParameterTextField(DeviceConfigContainer device, Parameter param, ParameterConfiguration conf) {
        super(device, param, conf);

        paramType = param.getValue().getType();

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

        int len = getText().length();
        if (len <= 11) {
            valid = true;
        } else {
            validationError = "String too long. Max 11 chars allowed.";
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
        String text = getText();
        
        ReadableValue2Bytes r2b = new ReadableValue2Bytes();
        byte[] value = new byte[]{
            (int) 0x00, (int) 0x00, (int) 0x00, 
            (int) 0x00, (int) 0x00, (int) 0x00, 
            (int) 0x00, (int) 0x00, (int) 0x00, 
            (int) 0x00, (int) 0x00};
        switch (paramType) {
            case STRING_11: {
                try {
                    value = r2b.convertString11(text);
                } catch (UnsupportedEncodingException ex) {
                    log.error("Error converting String to bytes", ex);
                }
            }
            break;
            default:
                log.error("Param with id {} is no string11 type", param.getId());
        }
        
        return value;
    }

    private void setValue(byte[] value) {
        if (value == null) {
            setText("");
            return;
        }

        Bytes2ReadableValue b2r = new Bytes2ReadableValue();
        String readableValue = "";

        switch (paramType) {
            case STRING_11:
                if (!checkParamSize(value, param, 11)) {
                    break;
                }

                try {
                    readableValue = b2r.convertString11(value);
                } catch (UnsupportedEncodingException ex) {
                    log.error("Error converting bytes to String", ex);
                }
                break;
        }
        setText(readableValue);
    }

    private boolean checkParamSize(byte[] value, Parameter param, int size) throws HeadlessException {
        if (value == null) {
            return true;
        }
        if (value.length != size) {
            JOptionPane.showMessageDialog(getParent(), "Die geladene XML Datei dieses Geräts hat einen Fehler im Abschnitt:\n\n"
                + "<Parameter Id=\"" + param.getId() + "\">:\n\n"
                + "Der Wert Default muss exakt " + size + " Byte lang sein, \n"
                + "ist aber " + value.length + " Bytes lang.!\n"
                + "Bitte informiere den Gerätehersteller.", "XML Datei ungültig", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
