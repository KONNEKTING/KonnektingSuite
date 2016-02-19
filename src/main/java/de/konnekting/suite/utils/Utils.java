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
package de.konnekting.suite.utils;

import java.awt.Color;
import com.rits.cloning.Cloner;
import de.konnekting.deviceconfig.DeviceConfigContainer;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author achristian
 */
public class Utils {

    public static double FACTOR = 0.95d;

    public static Color darker(Color c) {
        c = new Color(Math.max((int) (c.getRed() / FACTOR), 0),
                Math.max((int) (c.getGreen() / FACTOR), 0),
                Math.max((int) (c.getBlue() / FACTOR), 0),
                c.getAlpha());
        return c;
    }

    public static Color brighter(Color c) {
        c = new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
        return c;
    }

    public static String getArea(String addr) {
        return addr.substring(0, addr.indexOf("."));
    }

    public static String getLine(String addr) {
        return addr.substring(nthIndexOf(addr, ".".charAt(0), 1)+1, nthIndexOf(addr, ".".charAt(0), 2));
    }

    public static String getMember(String addr) {
        return addr.substring(nthIndexOf(addr, ".".charAt(0), 2)+1);
    }

    public static int nthIndexOf(String text, char needle, int n) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static boolean isLinux() {
        return System.getProperty("os.name").toUpperCase().contains("LINUX");
    }
    
}
