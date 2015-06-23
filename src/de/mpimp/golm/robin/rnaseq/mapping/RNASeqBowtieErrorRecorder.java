/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.robin.GUI.plots.LineDeviationPlot;
import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 *
 * @author marc
 */
public class RNASeqBowtieErrorRecorder {

    private long alignmentsProcessed;
    private float[] mismatchCounts;
    private float[] mismatchRates;
    private long[] readLengths;

    public RNASeqBowtieErrorRecorder() {
        alignmentsProcessed = 0;
    }

    public void recordAlignment(RNASeqBowtieAlignment alignment) {

        if (mismatchCounts == null) {
            mismatchCounts = new float[alignment.getReadLength()]; // this should be the length of the read
        }

        if (readLengths == null) {
            readLengths = new long[alignment.getReadLength()]; // this should be the length of the read
        }

        // grow arrays if necessary
        if (alignment.getReadLength() >= mismatchCounts.length) {
            float[] newCounts = new float[alignment.getReadLength() + 1];
            System.arraycopy(mismatchCounts, 0, newCounts, 0, mismatchCounts.length);
            mismatchCounts = newCounts;

            long[] newLengths = new long[alignment.getReadLength() + 1];
            System.arraycopy(readLengths, 0, newLengths, 0, readLengths.length);
            readLengths = newLengths;
        }

        // evaluate the mismatch string offset:reference-base>read-base,offset:reference-base>read-base
        for (RNASeqBowtieAlignment.Mismatch mis : alignment.getMismatches()) {
            mismatchCounts[mis.getOffset()]++;
        }

        alignmentsProcessed++;
        readLengths[alignment.getReadLength() - 1]++;
    }

    private void computeMismatchRates() {

        if (mismatchRates == null) {
            mismatchRates = new float[mismatchCounts.length];
        }

        // collapse readLengths
        float c = 0;
        for (int i = readLengths.length - 1; i >= 0; i--) {
            c += readLengths[i];
            mismatchRates[i] = mismatchCounts[i] / c;
        }
    }
    
    public float[] getMismatchRates() {
        if (mismatchRates == null) {
            computeMismatchRates();
        }
        return mismatchRates;
    }

    public ChartPanel getMismatchRatesPlot(Dimension size, String title) {
        
        if (mismatchRates == null) {
            computeMismatchRates();
        }
        
        LineDeviationPlot mismatchPlot = new LineDeviationPlot(
                title,
                "Position [nt]",
                "Fraction",
                0, 0,
                readLengths.length, 1,
                false);

        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        YIntervalSeries dataSeries = new YIntervalSeries("mismatch_rates");
        
        for (int i = 0; i < mismatchRates.length; i++) {
            dataSeries.add(i, mismatchRates[i], 0, mismatchRates[i]);
        } 
        
        dataset.addSeries(dataSeries);
        mismatchPlot.setDataset(dataset); 
        
        mismatchPlot.setColors(new Color[]{new Color(23,139,0)});
        mismatchPlot.setLineWidth(2f);
        
        // make sure the plot is actually rendered not scaled!
        ChartPanel panel = mismatchPlot.getChartPanel(size);
        panel.setMinimumDrawHeight(Integer.MIN_VALUE);
        panel.setMinimumDrawWidth(Integer.MIN_VALUE);
        
        return panel;
    }
}
