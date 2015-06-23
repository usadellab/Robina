/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.misc.FileFilters;

/**
 *
 * @author marc
 */
import java.io.File;

/**
 *
 * @author marc
 */
public class AffyFileFilter extends javax.swing.filechooser.FileFilter {

    public AffyFileFilter() {
    }

    public boolean accept(File arg0) {
        if (arg0.isDirectory()) return true;
        if ((arg0.getName().toLowerCase().endsWith(".cel")) ) {
             return true;
        }
        return false;
    }

    public String getDescription() {
        return "Affymetrix CEL file";
    }

}

