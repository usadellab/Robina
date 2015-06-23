/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author marc
 */
public class StringTableModel extends AbstractTableModel {
    
    protected String[][] data;
    protected String[] colNames;
    protected int columns;
    protected int rows;

    public StringTableModel() {
        
    }

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }
    
}
