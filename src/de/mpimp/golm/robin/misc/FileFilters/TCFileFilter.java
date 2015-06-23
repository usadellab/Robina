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
public class TCFileFilter extends javax.swing.filechooser.FileFilter {

    public TCFileFilter() {
    }
    
    
    @Override
    
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        /*if ((f.getName().toLowerCase().endsWith(".cel")) ) {
        return true;
        }*/ 
        return true;
    }

    @Override
    
    public String getDescription() {
        return "two color data file";
    }

    
}
