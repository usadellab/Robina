/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author marc
 */
class KmerTableModel extends AbstractTableModel {
    
    ArrayList<Kmer> kmers;

    public KmerTableModel(ArrayList<Kmer> k) {
        this.kmers = k;        
    }

    public int getRowCount() {
        return kmers.size();
    }

    public int getColumnCount() {
        return 4;
    }
    
    @Override
    public String getColumnName (int columnIndex) {
        switch (columnIndex) {
            case 0: return "Sequence";
            case 1: return "total count";
            case 2: return "overall enrichment";
            case 3: return "max. enrichment";
            default : return null;
        }        
    }
    
    public Class<?> getColumnClass (int columnIndex) {
        switch (columnIndex) {
            case 0: return String.class;
            case 1: return Integer.class;
            case 2: return Float.class;
            case 3: return Float.class;
            default: return null;
        }
    }

    public Object getValueAt(int i, int i1) {
        switch (i) {
            case 0: return kmers.get(i).getSequence();
            case 1: return kmers.get(i).getTotalCount();
            case 2: return kmers.get(i).getEnrichment();
            case 3: return kmers.get(i).getMaximalPositionalEnrichment();
            default: return null;						
        }
    }
        
}
