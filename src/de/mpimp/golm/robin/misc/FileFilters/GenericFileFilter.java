/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.misc.FileFilters;

import java.io.File;

/**
 *
 * @author marc
 */
public class GenericFileFilter extends javax.swing.filechooser.FileFilter {

    public GenericFileFilter() {
    }
    
    
    @Override
    
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if ((f.getName().toLowerCase().endsWith(".csv")) ||
                (f.getName().toLowerCase().endsWith(".txt")) ||
                (f.getName().toLowerCase().endsWith(".tab")) ||
                (f.getName().toLowerCase().endsWith(".tbl"))  ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    
    public String getDescription() {
        return "Generic data file (.txt, .tab, .tbl)";
    }

}
