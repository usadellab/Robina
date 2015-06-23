/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCResultPanel;
import de.mpimp.golm.robin.GUI.plots.LinePlot;
import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author marc
 */
public class RNASeqConsecutiveHomopolymers implements RNASeqQualityCheck {
    
    private float[] homoCounts;
    private long entriesRead = 0;
    private static final String ID = "QCconsecHomoPol";

    public RNASeqConsecutiveHomopolymers() {
        homoCounts = null;
    }
    
    
    // Only works with input that has uniform length of sequence
    public boolean processSeqEntry(FastQEntry entry) {
        String seq = entry.getSequence();
        
        if (homoCounts == null) {
            homoCounts = new float[seq.length()];
        }
        
        for (int i = 0; i <= seq.length() -2; i++) {
            if (seq.charAt(i) == seq.charAt(i+1)) {
                
                if (i >= homoCounts.length) {
                    float[] newCounts = new float[i+1];
                    System.arraycopy(homoCounts, 0, newCounts, 0, homoCounts.length);
                    homoCounts = newCounts;
                }
                
                homoCounts[i]++;             
            }
        }
        entriesRead ++;
        return true;
    }
    
    public ChartPanel getResultChart(Dimension size) {
        XYSeries dataSeries = new XYSeries("homopolymer frequency");
        for (int i = 0; i < homoCounts.length-1; i++) {
            dataSeries.add(i+1, homoCounts[i] / entriesRead);
//            homoCounts[i] = homoCounts[i] / entriesRead;
//            System.out.print(homoCounts[i] + ", ");
        }
                
        XYSeriesCollection dataset = new XYSeriesCollection(dataSeries);
        
        LinePlot plot = new LinePlot("Homopolymer content", "Base position", "Fraction", 0, 0, 0, 1, false);
        plot.setDataset(dataset);
        plot.setLineWidth(2f);
        plot.setColors(new Color[] {Color.BLUE});
        return plot.getChartPanel(size);
    
    }

    public Object getReport() {        
        return null;
    }

    public boolean hasTabularData() {
        return false;
    }

    public String getDescription() {
        return  "The occurrence of cycle-wise multiplied calls of the same nucleobase "+
                "is a rarely seen technical problem. If it happens, the base called in "+
                "e.g. cycle 20 to 20+n will be identical within each read across all reads (" + 
                "i.e AAA in read 1, CCC in read 2, CCC in read 3, TTT in read 4 etc). If such "
                + "errors are detected in the input, they will show as peaks reaching up to 100% "
                + "at individual cycles. It is recommended not to use such datasets and "
                + "investigate possible technical sources.";
    }

    public JPanel getResultPanel() {
        RNASeqQCResultPanel qc_view = new RNASeqQCResultPanel(this.getDescription(), ID, this.getResultChart(null));
        return qc_view;
    }
    
    
}
