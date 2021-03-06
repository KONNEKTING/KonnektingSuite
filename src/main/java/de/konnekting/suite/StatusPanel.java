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

import de.root1.rooteventbus.RootEventBus;
import de.konnekting.suite.events.EventBackgroundThread;
import de.konnekting.suite.events.EventDeviceAdded;
import de.konnekting.suite.events.StickyDeviceSelected;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class StatusPanel extends javax.swing.JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RootEventBus eventBus = RootEventBus.getDefault();
    private AtomicBoolean dirty = new AtomicBoolean(false);
    private final Timer t = new Timer();
    private final Object LOCK = new Object();
    private final TimerTask tt = new TimerTask() {
        @Override
        public void run() {
            synchronized(LOCK) {
                boolean old = dirty.getAndSet(false);
                if (old) {
                    log.info("Clear status message");
                    lastStatusMsgLabel.setText("");
                }
            }
        }
    };
    
    /**
     * Creates new form StatusPanel
     */
    public StatusPanel() {
        initComponents();
        eventBus.register(this);
        eventBus.registerSticky(new StickyDeviceSelected());
        t.schedule(tt, 5000, 10000);
    }
    
    public void onEvent(EventDeviceAdded event) {
        showText("Added: "+event.getDeviceConfig().getDescription()+" ("+event.getDeviceConfig().getIndividualAddress()+")");
    }
    
    public void onEvent(EventBackgroundThread event) {
        showText(event.getTask().getAction()+" "+String.format("% 3d",(int)(event.getTask().getProgress()*100d))+"%");
    }
    
    private void showText(String text) {
        synchronized(LOCK) {
            lastStatusMsgLabel.setText(text);
            dirty.set(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lastStatusMsgLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lastStatusMsgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(196, 196, 196))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator2)
            .addComponent(lastStatusMsgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lastStatusMsgLabel;
    // End of variables declaration//GEN-END:variables
}
