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

import de.konnekting.suite.events.EventConsoleMessage;
import de.root1.rooteventbus.RootEventBus;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author achristian
 */
public abstract class ValidateableTextField extends JTextField implements Validateable {

    private String originalToolTip = null;
    private final List<ChangeListener> listeners = new ArrayList<>();
    private Object lastValidValue;
    private String lastTextValue;

    private void fireChange() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    class ValueInputVerifier extends InputVerifier {

        private ValueInputVerifier() {
        }

        @Override
        public boolean verify(JComponent input) {
            return isInputValid();
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }

    }

    public ValidateableTextField() {
        setInputVerifier(new ValueInputVerifier());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        if (!isInputValid()) {
            Graphics2D g2 = (Graphics2D) g;

            // Paint the red X.
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int s = 8;
            int pad = 4;
            int x = w - pad - s;
            int y = (h - s) / 2;
            g2.setPaint(Color.red);
            g2.fillRect(x, y, s + 1, s + 1);
            g2.setPaint(Color.white);
            g2.drawLine(x, y, x + s, y + s);
            g2.drawLine(x, y + s, x + s, y);

            g2.dispose();
            originalToolTip = getToolTipText();
            setToolTipText(getValidationErrorMessage());
            String textValue = getText();
            if (textValue != null && !textValue.equals(lastTextValue)) {
                // RootEventBus.getDefault().post(new EventConsoleMessage("Ungültige Eingabe: "+getValidationErrorMessage()));
            }
            lastTextValue = textValue;
        } else {
            Object value = getValue();
            if (originalToolTip != null) {
                setToolTipText(originalToolTip);
            }
            if (value != null && !value.equals(lastValidValue)) {
                fireChange();
            }
            lastValidValue = value;
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        this.listeners.remove(listener);
    }
    
}
