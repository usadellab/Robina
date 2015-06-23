/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI.twocolor;

/**
 *
 * @author marc
 */
public class TCTargetsTableModel extends javax.swing.table.DefaultTableModel {
    private boolean[] canEdit;
    Class[] types;
    public TCTargetsTableModel() {
        super(new Object [][] {},
            new String [] {
                "Labels", "File name", "Green (Cy3) sample", "Red (Cy5) sample"
            });
        
        canEdit = new boolean [] {
            true, false, true, true
        };

        types = new Class [] {
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        
        
            
     }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }
}
    

