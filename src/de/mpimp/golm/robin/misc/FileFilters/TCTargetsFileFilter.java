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
public class TCTargetsFileFilter extends  javax.swing.filechooser.FileFilter {

    public TCTargetsFileFilter() {
    }
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if ((f.getName().toLowerCase().endsWith(".rex")) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Robin experiment file (.rex)";
    }
}
