/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 *
 * @author marc
 */
public class ComboBoxCellEditor extends DefaultCellEditor {
    public ComboBoxCellEditor(String[] items) {
        super(new JComboBox(items));
    }
}