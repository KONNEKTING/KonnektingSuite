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

/**
 *
 * @author achristian
 */
public class NumberTextField extends ValidateableTextField {

    private int max;
    private int min;
    private String validationError = "";
    private boolean emptyAllowed;

    public NumberTextField() {
        super();
    }
    
    public void setEmptyAllowed(boolean allowed){
        this.emptyAllowed = allowed;
    }
    
    public void setRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String getValidationErrorMessage() {
        return validationError;
    }

    @Override
    public boolean isInputValid() {
        String text = getText();
        if (emptyAllowed && text.isEmpty()) {
            validationError = "";
            return true;
        }

        try {
            int value = Integer.parseInt(text);
            if (value >=min && value <= max) {
                validationError = "";
                return true;
            }
            validationError = value+" not in range ["+min+".."+max+"]";
            return false;
        } catch (NumberFormatException ex) {
            validationError = "'" + text + "' is not a number";
            return false;
        }
    }

    @Override
    public Object getValue() {
        return getText();
    }

}
