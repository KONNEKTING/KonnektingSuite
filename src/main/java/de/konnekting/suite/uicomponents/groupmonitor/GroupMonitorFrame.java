/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents.groupmonitor;

import de.konnekting.suite.Main;
import de.konnekting.suite.events.EventSaveSettings;
import de.root1.rooteventbus.RootEventBus;
import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFrame;

/**
 *
 * @author achristian
 */
public class GroupMonitorFrame extends javax.swing.JFrame {

    private Knx knx;

    public final AtomicInteger count = new AtomicInteger(0);
    private Properties properties = Main.getProperties();

    private final GroupAddressListener gal = new GroupAddressListener() {

        @Override
        public void readRequest(GroupAddressEvent event) {
            process(event);
        }

        @Override
        public void readResponse(GroupAddressEvent event) {
            process(event);
        }

        @Override
        public void write(GroupAddressEvent event) {
            process(event);
        }

        private void process(GroupAddressEvent gae) {
            if (startButton.isSelected() && isVisible()) {
                groupMonitorTableModel1.addEvent(new GroupAddressEventContainer(count.getAndIncrement(), new Date(), gae));
                updateCountLabel();
            }
        }
    };

    private void updateCountLabel() {
        telegramCount.setText(String.format("Telegramme: % 6d", groupMonitorTableModel1.getRowCount()));
    }

    /**
     * Creates new form GroupMonitorFrame
     */
    public GroupMonitorFrame(JFrame main) {
        initComponents();
        
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // autoscroll feature
        eventTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (autoscrollButton.isSelected()) {
                    eventTable.scrollRectToVisible(eventTable.getCellRect(eventTable.getRowCount() - 1, 0, true));
                }
            }

        });

        setSize(Integer.parseInt(properties.getProperty("groupmonitor.windowwidth", "700")), Integer.parseInt(properties.getProperty("groupmonitor.windowheight", "400")));

        Point groupMonitorLocation = new Point();
        groupMonitorLocation.x = Integer.parseInt(properties.getProperty("groupmonitor.windowx", "" + Integer.MIN_VALUE));
        groupMonitorLocation.y = Integer.parseInt(properties.getProperty("groupmonitor.windowy", "" + Integer.MIN_VALUE));
        if (groupMonitorLocation.x == Integer.MIN_VALUE && groupMonitorLocation.y == Integer.MIN_VALUE) {
            setLocationRelativeTo(main);
        } else {
            setLocation(groupMonitorLocation);
        }

        startButton.setSelected(Boolean.parseBoolean(properties.getProperty("groupmonitor.startSelected", "false")));
        autoscrollButton.setSelected(Boolean.parseBoolean(properties.getProperty("groupmonitor.autoscrollSelected", "true")));
    }

    public void setKnx(Knx knx) {
        this.knx = knx;
        count.set(0);

        knx.addGroupAddressListener("*", gal);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            saveSettings();
            startButton.setSelected(false);
            RootEventBus.getDefault().post(new EventSaveSettings());
        } else {
            startButton.setSelected(Boolean.parseBoolean(properties.getProperty("groupmonitor.startSelected", "false")));
        }
    }

    @Override
    public void dispose() {
        knx.removeGroupAddressListener("*", gal);
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupMonitorTableModel1 = new de.konnekting.suite.uicomponents.groupmonitor.GroupMonitorTableModel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        startButton = new javax.swing.JToggleButton();
        stopButton = new javax.swing.JToggleButton();
        clearButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        autoscrollButton = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        telegramCount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gruppenmonitor");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });

        eventTable.setModel(groupMonitorTableModel1);
        jScrollPane1.setViewportView(eventTable);

        jToolBar1.setRollover(true);

        buttonGroup1.add(startButton);
        startButton.setText("Start");
        startButton.setFocusable(false);
        startButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(startButton);

        buttonGroup1.add(stopButton);
        stopButton.setSelected(true);
        stopButton.setText("Stop");
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(stopButton);

        clearButton.setText("Löschen");
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(clearButton);
        jToolBar1.add(jSeparator1);

        autoscrollButton.setSelected(true);
        autoscrollButton.setText("Automatisch scrollen");
        autoscrollButton.setFocusable(false);
        autoscrollButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autoscrollButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        autoscrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoscrollButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(autoscrollButton);
        jToolBar1.add(jSeparator2);

        telegramCount.setText("Telegramme:");
        jToolBar1.add(telegramCount);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        synchronized (count) {
            groupMonitorTableModel1.clear();
            count.set(0);
            updateCountLabel();
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        saveSettings();
    }//GEN-LAST:event_formComponentMoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        saveSettings();
    }//GEN-LAST:event_formComponentResized

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        saveSettings();
    }//GEN-LAST:event_startButtonActionPerformed

    private void autoscrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoscrollButtonActionPerformed
        saveSettings();
    }//GEN-LAST:event_autoscrollButtonActionPerformed

    private void saveSettings() {
        Point location = getLocation();
        properties.put("groupmonitor.windowx", Integer.toString(location.x));
        properties.put("groupmonitor.windowy", Integer.toString(location.y));
        Dimension size = getSize();
        properties.put("groupmonitor.windowwidth", Integer.toString(size.width));
        properties.put("groupmonitor.windowheight", Integer.toString(size.height));

        properties.put("groupmonitor.startSelected", startButton.isSelected() + "");
        properties.put("groupmonitor.autoscrollSelected", autoscrollButton.isSelected() + "");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton autoscrollButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton clearButton;
    private javax.swing.JTable eventTable;
    private de.konnekting.suite.uicomponents.groupmonitor.GroupMonitorTableModel groupMonitorTableModel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToggleButton startButton;
    private javax.swing.JToggleButton stopButton;
    private javax.swing.JLabel telegramCount;
    // End of variables declaration//GEN-END:variables
}
