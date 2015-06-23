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
public class MappingFileFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if ((f.getName().toLowerCase().endsWith(".xls")) ||
                (f.getName().toLowerCase().endsWith(".txt")) ||
                (f.getName().toLowerCase().endsWith(".xml")) ||
                (f.getName().toLowerCase().endsWith(".m01")) ||
                (f.getName().toLowerCase().endsWith(".m02"))   ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Mapping file (.txt, .xls, .m02)";
    }

}
