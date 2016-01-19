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

import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.suite.events.EventConsoleMessage;
import de.konnekting.suite.events.EventProjectSaved;
import de.root1.rooteventbus.RootEventBus;
import javax.swing.DefaultListModel;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;


/**
 *
 * @author achristian
 */
public class DeviceListModel extends DefaultListModel<DeviceConfigContainer>{

    void saveAllToDisk() {
        boolean allok = true;
        for(int i=0;i<getSize();i++) {
            DeviceConfigContainer device = getElementAt(i);
            try {
                device.writeConfig();
            } catch (JAXBException | SAXException ex) {
                RootEventBus.getDefault().post(new EventConsoleMessage("Fehler beim Speichern von "+device, ex));
                allok=false;
            }
        }
        if (allok) {
            RootEventBus.getDefault().post(new EventProjectSaved());
        }
    }
    
}
