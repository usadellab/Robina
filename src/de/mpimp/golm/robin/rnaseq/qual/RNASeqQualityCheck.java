/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author marc
 */
public interface RNASeqQualityCheck {
    
    public boolean hasTabularData();    
    public boolean processSeqEntry(FastQEntry entry);
    public Object getReport();
    public ChartPanel getResultChart(Dimension size);
    public String getDescription();
    public JPanel getResultPanel();
//    public String getHTMLTable();    
//    public PdfPTable getPDFTable();    
//    public String getDescriptionText();
}
