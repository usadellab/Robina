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
public class GenepixFileFilter extends javax.swing.filechooser.FileFilter {

    public GenepixFileFilter() {
    }
    
    
    @Override    
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if ((f.getName().toLowerCase().endsWith(".gpr")) ) {
        return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Genepix results file (.gpr)";
    }

    
}
