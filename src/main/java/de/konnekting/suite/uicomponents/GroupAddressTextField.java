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

import de.konnekting.deviceconfig.utils.Helper;

/**
 *
 * @author achristian
 */
public class GroupAddressTextField extends ValidateableTextField {

    private String validationError;
    
    @Override
    public String getValidationErrorMessage() {
        return validationError;
    }

    @Override
    public boolean isInputValid() {
        if (Helper.checkValidGa(getText()) || getText().isEmpty()) {
            validationError = "";
            return true;
        } else {
            validationError = "Invalid group address. Format [0..15]/[0..7]/[0..255] required";
            return false;
        }
    }

    @Override
    public Object getValue() {
        return getText();
    }

}
