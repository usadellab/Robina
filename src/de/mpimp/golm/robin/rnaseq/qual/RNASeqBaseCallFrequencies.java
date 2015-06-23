/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCResultPanel;
import de.mpimp.golm.robin.GUI.plots.LinePlot;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author marc
 */
public class RNASeqBaseCallFrequencies implements RNASeqQualityCheck {

    private float[] aCalls;
    private float[] cCalls;
    private float[] gCalls;
    private float[] tCalls;
    private float[] nCalls;
    private long[] entriesReadAtPos;
    private static final String ID = "QCbaseCallFreqs";

    public RNASeqBaseCallFrequencies() {
        // initialize
        aCalls = new float[RobinConstants.MAX_SEQUENCE_LENGTH];
        cCalls = new float[RobinConstants.MAX_SEQUENCE_LENGTH];
        gCalls = new float[RobinConstants.MAX_SEQUENCE_LENGTH];
        tCalls = new float[RobinConstants.MAX_SEQUENCE_LENGTH];
        nCalls = new float[RobinConstants.MAX_SEQUENCE_LENGTH];
    }

    public boolean processSeqEntry(FastQEntry entry) {
        String seq = entry.getSequence();  
        
        if (entriesReadAtPos == null) {
            entriesReadAtPos = new long[seq.length() + 1];
        }
        
        // do we have to grow a longer ?
        if (seq.length() >= entriesReadAtPos.length) {
            long[] newEntries = Arrays.copyOf(entriesReadAtPos, seq.length() + 1);
            entriesReadAtPos = newEntries;
        }


        for (int i = 0; i <= seq.length() - 1; i++) {
            switch (seq.toUpperCase().charAt(i)) {
                case 'A':
                    aCalls[i]++;
                    break;
                case 'C':
                    cCalls[i]++;
                    break;
                case 'G':
                    gCalls[i]++;
                    break;
                case 'T':
                    tCalls[i]++;
                    break;
                case 'N':
                    nCalls[i]++;
                    break;
            }
            entriesReadAtPos[i]++;
        }
        
        return true;
    }

    public Object getReport() {
        for (int i = 0; i < entriesReadAtPos.length - 1; i++) {
            aCalls[i] = aCalls[i] / entriesReadAtPos[i];
            cCalls[i] = cCalls[i] / entriesReadAtPos[i];
            gCalls[i] = gCalls[i] / entriesReadAtPos[i];
            tCalls[i] = tCalls[i] / entriesReadAtPos[i];
            nCalls[i] = nCalls[i] / entriesReadAtPos[i];
        }

        for (int i = 0; i < aCalls.length; i++) {
            System.out.print("A --> " + aCalls[i] + ", ");
        }
        System.out.println();

        for (int i = 0; i < aCalls.length; i++) {
            System.out.print("C --> " + cCalls[i] + ", ");
        }
        System.out.println();

        for (int i = 0; i < aCalls.length; i++) {
            System.out.print("G --> " + gCalls[i] + ", ");
        }
        System.out.println();

        for (int i = 0; i < aCalls.length; i++) {
            System.out.print("T --> " + tCalls[i] + ", ");
        }
        System.out.println();

        for (int i = 0; i < aCalls.length; i++) {
            System.out.print("N --> " + nCalls[i] + ", ");
        }
        System.out.println();

        return null;
    }

    public ChartPanel getResultChart(Dimension size) {

        XYSeries aDataSeries = new XYSeries("A");
        XYSeries cDataSeries = new XYSeries("C");
        XYSeries gDataSeries = new XYSeries("G");
        XYSeries tDataSeries = new XYSeries("T");
        XYSeries nDataSeries = new XYSeries("N");


        for (int i = 0; i < entriesReadAtPos.length - 1; i++) {
            aDataSeries.add(i + 1, aCalls[i] / entriesReadAtPos[i]);
            cDataSeries.add(i + 1, cCalls[i] / entriesReadAtPos[i]);
            gDataSeries.add(i + 1, gCalls[i] / entriesReadAtPos[i]);
            tDataSeries.add(i + 1, tCalls[i] / entriesReadAtPos[i]);
            nDataSeries.add(i + 1, nCalls[i] / entriesReadAtPos[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(aDataSeries);
        dataset.addSeries(cDataSeries);
        dataset.addSeries(gDataSeries);
        dataset.addSeries(tDataSeries);
        dataset.addSeries(nDataSeries);

        LinePlot plot = new LinePlot("Base call frequencies", "Base position", "Frequency", 1, 0, 0, 1, true);
        plot.setDataset(dataset);
        plot.setLineWidth(2f);
        plot.setColors(new Color[]{
                    Color.BLUE,
                    Color.GREEN,
                    Color.RED,
                    Color.BLACK,
                    Color.MAGENTA
                });

        return plot.getChartPanel(size);
    }

    public boolean hasTabularData() {
        return false;
    }

    public String getDescription() {
        return "The base call frequency plot helps identifying positional biases in the "
                + "call frequency for each base. In principle, if the reads were no generated"
                + "from preselected DNA/RNA that was specifically enriched in certain bases, "
                + "one would expect no positional differences in the occurrences of each base."
                + "Biases can be cause by e.g. unclipped barcode sequences, adapters etc.";
    }

    public JPanel getResultPanel() {
        RNASeqQCResultPanel qc_view = new RNASeqQCResultPanel(this.getDescription(), ID, this.getResultChart(null));
        return qc_view;
    }
}
