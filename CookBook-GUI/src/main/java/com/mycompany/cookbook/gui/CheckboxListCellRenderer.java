/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *
 * @author Dominik
 */
public class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, 
            boolean isSelected, boolean cellHasFocus) {
/*
        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setSelected(isSelected);
        setEnabled(list.isEnabled());

        setText(value == null ? "" : value.toString());  

        return this;*/
        JCheckBox checkbox = new JCheckBox((String) value);
        //checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
        //checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
        checkbox.setSelected(!isSelected);
        checkbox.setEnabled(isEnabled());
        checkbox.setFont(getFont());
        checkbox.setFocusPainted(false);
        checkbox.setBorderPainted(true);
        //checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        return checkbox;
    }
}
