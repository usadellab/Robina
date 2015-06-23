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
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author marc
 */
public class RNASeqPerSequenceQuality implements RNASeqQualityCheck {
    
    private long[] qualities = new long[0];
    private QualityEncoding.VERSION encoding;
    private long entriesRead;    
    private float averageQual;
    private static final String ID = "QCperSeqQual";

    public RNASeqPerSequenceQuality(QualityEncoding.VERSION encoding) {        
        this.encoding = encoding;        
    }

    public boolean processSeqEntry(FastQEntry entry) {
        
        int qualSum = 0;
        for (char s : entry.getQuality().toCharArray()) {            
            qualSum += s - encoding.getOffset();
        }        
        int bin = Math.round((float) (qualSum / entry.getSequence().length()) );
        
        if (bin >= qualities.length) {
            long[] newQual = new long[bin + 1 ];
            System.arraycopy(qualities, 0, newQual, 0, qualities.length);
            qualities = newQual;
        }
        
        qualities[bin]++;
        entriesRead++;
        
        return true;
    }

    public Object getReport() {
        getFractions();
        return null;
    }
    
    private float[] getFractions() {
        float[] fractions = new float[qualities.length];
        long qualSum = 0;
        for (int i = 0; i < qualities.length; i++) {
            fractions[i] = (float) ((float)qualities[i] / (float)entriesRead);            
            qualSum += qualities[i] * i;
        }
        averageQual = (float) ( (float)qualSum / (float)entriesRead );
        return fractions;
    }

    public ChartPanel getResultChart(Dimension size) {         
        
        XYSeries dataSeries = new XYSeries("average read qualities");
        float[] fractions = getFractions();
        for (int i = 0; i < fractions.length-1; i++) {
            dataSeries.add(i, fractions[i]);
        }
                
        XYSeriesCollection dataset = new XYSeriesCollection(dataSeries);
        
        LinePlot plot = new LinePlot("Per sequence quality", "Quality", "Fraction", 0, -1, 0, -1, false);
        plot.setDataset(dataset);
        plot.setLineWidth(2f);
        plot.setColors(new Color[] { Color.BLUE });
        
        IntervalMarker marker = new IntervalMarker(averageQual-0.05, averageQual+0.05);
        marker.setPaint(new Color(255,0,0,128));
        //marker.setOutlinePaint(Color.BLACK);
        //marker.setOutlineStroke(new BasicStroke(1.5f));        
        plot.addMarker(marker);
        return plot.getChartPanel(size);
    }

    public boolean hasTabularData() {
        return false;
    }

    public String getDescription() {
        NumberFormat nf = new java.text.DecimalFormat("##.#");
        getFractions();
        return " The plot shows the distribution of qualities averaged across the reads. In"
                + " the file at hand, the average quality score is " + nf.format(averageQual) + " (indicated by the red line). The "
                + "distribution should be unimodal - several peaks would imply the existence "
                + "of (lower quality) subpopulations of reads indicating technical problems "
                + "in the data";
    }

    public JPanel getResultPanel() {
        RNASeqQCResultPanel qc_view = new RNASeqQCResultPanel(this.getDescription(), ID, this.getResultChart(null));
        return qc_view;
    }
    
}
