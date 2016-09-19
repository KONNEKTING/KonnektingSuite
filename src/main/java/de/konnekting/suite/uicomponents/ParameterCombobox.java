/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents;

import de.konnekting.deviceconfig.DeviceConfigContainer;
import de.konnekting.deviceconfig.utils.Helper;
import de.konnekting.suite.events.EventParameterChanged;
import de.konnekting.xml.konnektingdevice.v0.Parameter;
import de.konnekting.xml.konnektingdevice.v0.ParameterConfiguration;
import de.root1.rooteventbus.RootEventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class ParameterCombobox extends JComboBox<ParameterCombobox.ComboboxItem> implements ParameterDependency {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ParameterConfiguration conf;
    private final Parameter param;
    private final DeviceConfigContainer device;

    public ParameterCombobox(DeviceConfigContainer device, Parameter param, ParameterConfiguration conf) {
        this.device = device;
        this.param = param;
        this.conf = conf;

        Parameter.Value valueObject = param.getValue();
        String options = valueObject.getOptions();

        byte[] currentValRaw = conf.getValue();

        if (currentValRaw == null) {
            // set to default
            currentValRaw = valueObject.getDefault();
            log.info("Setting param #{} to default value '{}'", param.getId(), Helper.bytesToHex(currentValRaw));
        }

        List<ComboboxItem> cbitems = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(options, "=|");
        int i = -1;
        int selectedIndex = -1;
        while (st.hasMoreTokens()) {
            i++;
            String val = st.nextToken();
            String representation = st.nextToken();
            ComboboxItem cbi = new ComboboxItem(val, representation);
            cbitems.add(cbi);
            if (val.equals(Helper.bytesToHex(currentValRaw))) {
                selectedIndex = i;
            }
        }

        setModel(new DefaultComboBoxModel<ComboboxItem>(cbitems.toArray(new ComboboxItem[]{})));
        setSelectedIndex(selectedIndex);
        save(); // ensure that default value is stored

        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                save();
                RootEventBus.getDefault().post(new EventParameterChanged());
            }
        });
    }

    @Override
    public boolean isParameterVisible() {
        return device.isParameterEnabled(param);
    }

    class ComboboxItem {

        private final String representation;
        private final String value;

        private ComboboxItem(String value, String representation) {
            this.value = value;
            this.representation = representation;
        }

        @Override
        public String toString() {
            return representation;
        }

        public String getValue() {
            return value;
        }

    }

    private void save() {
        int selectedIndex = getSelectedIndex();
        ComboboxItem cbi = getItemAt(selectedIndex);
        log.info("Changing combo param #" + param.getId() + " to '" + cbi.getValue() + "'");
        device.setParameterValue(param.getId(), Helper.hexToBytes(cbi.getValue()));
    }

}
