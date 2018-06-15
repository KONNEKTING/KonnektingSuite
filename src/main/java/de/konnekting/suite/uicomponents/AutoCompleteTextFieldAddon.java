/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.uicomponents;

import de.root1.knxprojparser.GroupAddress;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author achristian
 */
public class AutoCompleteTextFieldAddon<V>  {

    private AutoCompleteValueRenderer renderer;
    private AutoCompleteCompare<V> comparer;
    private final List<V> values;
    private JComboBox acBox;
    final DefaultComboBoxModel model = new DefaultComboBoxModel();
    private final JTextField tf;
    private ActionListener actionListener;

    public AutoCompleteTextFieldAddon(JTextField tf, List<V> values) {
        this.tf = tf;

        this.values = values;
        tf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                inputCompletion();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                inputCompletion();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                inputCompletion();
            }
        });

        acBox = new JComboBox(model) {

            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };

        acBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdjusting()) {
                    if (acBox.getSelectedItem() != null) {
                        tf.setText(renderer.renderSelectionResult(acBox.getSelectedItem()));
                        if (actionListener!=null) {
                            actionListener.actionPerformed(new ActionEvent(tf, ActionEvent.ACTION_PERFORMED, ""));
                        }
                    }
                }
            }
        });

        tf.setLayout(new BorderLayout());
        tf.add(acBox, BorderLayout.SOUTH);

        tf.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (acBox.isPopupVisible() && acBox.getSelectedIndex()!=-1) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(acBox);
                    acBox.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && acBox.getSelectedIndex()!=-1) {
                        tf.setText(renderer.renderSelectionResult(acBox.getSelectedItem()));
                        acBox.setPopupVisible(false);
                        if (actionListener!=null) {
                            actionListener.actionPerformed(new ActionEvent(tf, ActionEvent.ACTION_PERFORMED, ""));
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    acBox.setPopupVisible(false);
                }
                setAdjusting(false);
            }
        });

    }

    private void setAdjusting(boolean adjusting) {
        acBox.putClientProperty("is_adjusting", adjusting);
    }

    private boolean isAdjusting() {
        if (acBox.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) acBox.getClientProperty("is_adjusting");
        }
        return false;
    }

    private void inputCompletion() {
        setAdjusting(true);
        acBox.setPopupVisible(false);
        String inputString = tf.getText();
        if (inputString.isEmpty()) {
            return;
        }

        model.removeAllElements();
        for (V value : values) {
            if (comparer.match(inputString, value)) {
                model.addElement(value);
            }
        }

        acBox.setSelectedItem(null);

        if (model.getSize() != 0) {
            System.out.println(model.getSize() + " do match");
            acBox.setPopupVisible(true);
        } else {
            System.out.println("no match. clear");
            acBox.setPopupVisible(false);
        }
        setAdjusting(false);

    }

    public void setValueRenderer(AutoCompleteValueRenderer<V> renderer) {
        this.renderer = renderer;
        acBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(renderer.renderSuggestion((V) value));
                return label;
            }

        });
    }

    public void setValueComparer(AutoCompleteCompare<V> comparer) {
        this.comparer = comparer;
    }
    
    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    public interface AutoCompleteValueRenderer<V> {

        String renderSuggestion(V value);

        String renderSelectionResult(V value);

    }

    public interface AutoCompleteCompare<V> {

        boolean match(String input, V value);
    }

    // #########################################################################################################################
    public static void main(String[] args) {

        JFrame j = new JFrame();
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setSize(500, 400);

        List<de.root1.knxprojparser.GroupAddress> list = new ArrayList<>();
        list.add(new GroupAddress("1/1/1", "test ga1", ""));
        list.add(new GroupAddress("1/2/1", "test ga2", ""));
        list.add(new GroupAddress("1/2/3", "test ga3", ""));

        JTextField tf = new JTextField();
        
        AutoCompleteTextFieldAddon<de.root1.knxprojparser.GroupAddress> actf = new AutoCompleteTextFieldAddon<>(tf, list);
        actf.setValueComparer(new AutoCompleteCompare<GroupAddress>() {
            @Override
            public boolean match(String input, GroupAddress value) {
                return value.getAddress().contains(input) || value.getName().contains(input);
            }
        });
        actf.setValueRenderer(new AutoCompleteValueRenderer<GroupAddress>() {
            @Override
            public String renderSuggestion(GroupAddress value) {
                if (value != null) {
                    return value.getAddress() + "  -  " + value.getName();
                } else {
                    return "";
                }
            }

            @Override
            public String renderSelectionResult(GroupAddress value
            ) {
                if (value != null) {
                    return value.getAddress();
                } else {
                    return "";
                }
            }
        }
        );

        tf.setLocation(
                5, 20);
        tf.setSize(
                180, 21);
        j.setLayout(
                null);

        j.add(tf);

        j.setVisible(
                true);
    }
}
