///*
// * Copyright (C) 2016 Alexander Christian <alex(at)root1.de>. All rights reserved.
// * 
// * This file is part of KONNEKTING Suite.
// *
// *   KONNEKTING Suite is free software: you can redistribute it and/or modify
// *   it under the terms of the GNU General Public License as published by
// *   the Free Software Foundation, either version 3 of the License, or
// *   (at your option) any later version.
// *
// *   KONNEKTING Suite is distributed in the hope that it will be useful,
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *   GNU General Public License for more details.
// *
// *   You should have received a copy of the GNU General Public License
// *   along with KONNEKTING DeviceConfig.  If not, see <http://www.gnu.org/licenses/>.
// */
//package de.konnekting.suite.uicomponents;
//
//import de.konnekting.deviceconfig.utils.ReadableValue2Bytes;
//import de.konnekting.deviceconfig.utils.Bytes2ReadableValue;
//import de.konnekting.deviceconfig.DeviceConfigContainer;
//import de.konnekting.deviceconfig.utils.Helper;
//import de.konnekting.xml.konnektingdevice.v0.Parameter;
//import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
//import de.konnekting.xml.konnektingdevice.v0.ParameterType;
//import java.awt.HeadlessException;
//import javax.swing.JOptionPane;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author achristian
// */
//public class FloatParameterTextField extends ValidateableTextField {
//    
//    private final Logger log = LoggerFactory.getLogger(getClass());
//
//    private float min = 0;
//    private float max = 0;
//    private final boolean minMaxSet;
//    private final Parameter param;
//    private final ParameterType paramType;
//    private final DeviceConfigContainer device;
//    
//    private String validationError = "";
//    private String lastText;
//    
//        public FloatParameterTextField(DeviceConfigContainer device, Parameter param, ParameterConfiguration conf) {
//        this.device = device;
//        this.param = param;
//
//        String type = param.getValue().getType().toUpperCase();
//        paramType = ParameterType.valueOf(type);
//
//        // if min is set, max must also be set --> ensure and catch exception?!
//        byte[] minRaw = param.getValue().getMin();
//        byte[] maxRaw = param.getValue().getMax();
//        byte[] value = conf.getValue();
//        if (value == null) {
//            value = param.getValue().getDefault();
//            // enforce default value to be set/saved
//            device.setParameterValue(param.getId(), value);
//        }
//
//        minMaxSet = minRaw != null && maxRaw != null;
//
//        // parse/read MIN/MAX values
//        Bytes2ReadableValue b2r = new Bytes2ReadableValue();
//        switch (paramType) {
//            case FLOAT32:
//                if (!checkParamSize(value, param, 4)) {
//                    break;
//                }
//
//                if (minMaxSet) {
//                    min = b2r.convertFLOAT32(minRaw);
//                    max = b2r.convertFLOAT32(maxRaw);
//                } else {
//                    min = Float.MIN_VALUE;
//                    max = Float.MAX_VALUE;
//                }
//                break;
//        }
//
//        
//        setValue(value);
//    }
//
//    @Override
//    public String getValidationErrorMessage() {
//        return validationError;
//    }
//
//    @Override
//    public boolean isInputValid() {
//        
//        boolean valid = false;
//
//            if (Helper.isNumberType(paramType)) {
//                try {
//                    float v = Float.parseFloat(getText());
//                    if (v >= min && v <= max) {
//                        valid = true;
//                    } else {
//                        validationError = "Not in range [" + min + ".." + max+"]";
//                    }
//                } catch (NumberFormatException ex) {
//                    validationError = "Not a valid number: " + getText();
//                }
//            } else {
//                validationError = "Unsupported type?! " + paramType.toString();
//                valid = false;
//            }
//
//        if (valid) {
//            String text = getText();
//            if (text!=null && !text.equals(lastText)) {
//                byte[] value = (byte[]) getValue();
//                device.setParameterValue(param.getId(), value);
//            } 
//            lastText = text;
//        }
//        return valid;
//    }
//
//    @Override
//    public byte[] getValue() {
//        String text = getText();
//        ReadableValue2Bytes r2b = new ReadableValue2Bytes();
//        byte[] value;
//        switch (paramType) {
//            case FLOAT32:
//                value = r2b.convertFLOAT32(Float.parseFloat(text));
//                break;
//            default:
//                value = null;
//        }
//        return value;
//    }
//
//    private void setValue(byte[] value) {
//        if (value == null) {
//            setText("");
//            return;
//        }
//
//        Bytes2ReadableValue b2r = new Bytes2ReadableValue();
//        String readableValue = "";
//
//        switch (paramType) {
//            case FLOAT32:
//                if (!checkParamSize(value, param, 4)) {
//                    break;
//                }
//                readableValue = String.valueOf(b2r.convertFLOAT32(value));
//                break;
//
//        }
//        setText(readableValue);
//    }
//
//
//
//    private boolean checkParamSize(byte[] value, Parameter param, int size) throws HeadlessException {
//        if (value == null) {
//            return true;
//        }
//        if (value.length != size) {
//            JOptionPane.showMessageDialog(getParent(), "Die geladene XML Datei dieses Geräts hat einen Fehler im Abschnitt:\n\n"
//                    + "<Parameter Id=\"" + param.getId() + "\">:\n\n"
//                    + "Der Werte Default und die optionalen Werte Min und Max müssen\n"
//                    + "exakt " + size + " Byte lang sein, sind aber teilw. " + value.length + " Bytes lang.!\n"
//                    + "Bitte informiere den Gerätehersteller.", "XML Datei ungültig", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//        return true;
//    }
//
//}
