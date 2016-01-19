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
package de.konnekting.suite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 *
 * @author achristian
 */
public class ReflectionIdComparator implements Comparator<Object> {

    public ReflectionIdComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        try {
            Method m1 = o1.getClass().getDeclaredMethod("getId", (Class<?>) null);
            Method m2 = o2.getClass().getDeclaredMethod("getId", (Class<?>) null);
            
            byte id1 = (byte) m1.invoke(o1, (Object) null);
            byte id2 = (byte) m2.invoke(o2, (Object) null);
            
            if (id1 < id2) {
                    return -1;
                } else if (id1 > id2) {
                    return +1;
                } else {
                    return 0;
                }
            
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return 0;
        }
    }




}
