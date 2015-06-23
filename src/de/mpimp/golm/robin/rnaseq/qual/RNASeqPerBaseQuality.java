/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCResultPanel;
import de.mpimp.golm.robin.GUI.plots.BoxPlot;
import de.mpimp.golm.robin.GUI.plots.LineDeviationPlot;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 *
 * @author marc
 */
public class RNASeqPerBaseQuality implements RNASeqQualityCheck {

    // if we want box plots instead of this nice deviation thingy...
    private static final boolean DRAW_BOXPLOT = false;
    private static final float QUALITY_THRESHOLD = 13f;
    private QualityMatrix qmatrix;
    private VERSION version;
    private long entriesRead = 0;
    private static final String ID = "QCperBaseQual";

    /*
     * the base call quality checker needs to know the version to be able to
     * apply the proper offset
     */
    public RNASeqPerBaseQuality(VERSION ver) {
        qmatrix = null;
        version = ver;
    }

    public boolean processSeqEntry(FastQEntry entry) {

        String qual = entry.getQuality();

        if (qmatrix == null) {
            qmatrix = new QualityMatrix(RobinConstants.MAX_SEQUENCE_LENGTH, RobinConstants.MAX_QUAL_SCORE);
        }

        for (int i = 0; i < qual.length(); i++) {
            int qualScore = qual.charAt(i) - version.getOffset();
            qmatrix.countQualAtPosition(qualScore, i);
        }
        entriesRead++;
        return true;
    }

    public Object getReport() {
        int[] maxima = qmatrix.getMaxs();

        for (int i = 0; i < maxima.length; i++) {
            System.out.print(maxima[i] + ", ");
        }
        return null;
    }

    private ChartPanel getBoxPlotChart(Dimension size) {

        int[] maxima = qmatrix.getMaxs();
        int[] minima = qmatrix.getMins();
        float[] q1 = qmatrix.getLowerQuarts();
        float[] q3 = qmatrix.getUpperQuarts();
        float[] means = qmatrix.getWeightedMeans();
        float[] medians = qmatrix.getMedians();

        // assemble data for plotting
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        for (int i = 0; i < qmatrix.length; i++) {
            BoxAndWhiskerItem item = new BoxAndWhiskerItem(
                    means[i],
                    medians[i],
                    q1[i],
                    q3[i],
                    minima[i],
                    maxima[i],
                    0, // did not record outliers
                    0,
                    new ArrayList());

            dataset.add(item, 1, i + 1);
        }

        BoxPlot qualityBoxPlot = new BoxPlot("Median base call qualities", "Base position", "Quality score", 0, 41, false);
        qualityBoxPlot.setDataset(dataset);
        qualityBoxPlot.setColor(new Color(0, 0, 255, 128));
        qualityBoxPlot.setShowMeans(true);
        return qualityBoxPlot.getChartPanel(size);

    }

    public ChartPanel getResultChart(Dimension size) {

        if (DRAW_BOXPLOT) {
            return getBoxPlotChart(size);
        }
        
        qmatrix.trimFromEnd();

        float[] q1 = qmatrix.getLowerQuarts();
        float[] q3 = qmatrix.getUpperQuarts();
//        int[] min = qmatrix.getMins();
//        int[] max = qmatrix.getMaxs(); 
        float[] medians = qmatrix.getMedians();

        ArrayList<Marker> badRegions = scanBadRegions(medians, QUALITY_THRESHOLD);

        // assemble data for plotting
        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        YIntervalSeries dataSeries = new YIntervalSeries("quality");
//        YIntervalSeries dataExtremesSeries = new YIntervalSeries("extremes");


        for (int i = 0; i < qmatrix.length; i++) {
            dataSeries.add(i + 1, medians[i], q1[i], q3[i]);
//            dataExtremesSeries.add(i+1, medians[i], min[i], max[i] );
        }
        dataset.addSeries(dataSeries);
//        dataset.addSeries(dataExtremesSeries);

        LineDeviationPlot qualityPlot = new LineDeviationPlot("Base call qualities", "Base position", "Quality score", -1, 0, -1, 40, false);
        qualityPlot.setDataset(dataset);
        qualityPlot.setColors(new Color[]{Color.BLUE});
        for (Marker m : badRegions) {
            qualityPlot.addMarker(m);
        }
        return qualityPlot.getChartPanel(size);

    }

    private ArrayList<Marker> scanBadRegions(float[] values, float threshold) {
        ArrayList<Marker> markers = new ArrayList<Marker>();
        IntervalMarker m = null;

        boolean inRegion = false;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < threshold) {
                //System.out.println("below thr at "+i+" value="+values[i]+" in region="+inRegion);
                if (!inRegion) {
                    m = new IntervalMarker(i, i);
                    m.setPaint(new Color(255, 0, 0, 128));
                    m.setOutlinePaint(Color.BLACK);
                    m.setOutlineStroke(new BasicStroke(0.75f));
                    inRegion = true;
                } else {
                    m.setEndValue(i + 1);
                }
            } else {
                if (inRegion) {
                    markers.add(m);
                    inRegion = false;
                }
            }
        }

        //add last marker to the list
        if (m != null) {
            markers.add(m);
        }

        //System.out.println("found "+markers.size()+" bad regions");
        return markers;
    }

    public boolean hasTabularData() {
        return false;
    }

    public JPanel getResultPanel() {
        RNASeqQCResultPanel qc_view = new RNASeqQCResultPanel(this.getDescription(), ID, this.getResultChart(null));
        return qc_view;
    }

    private class QualityMatrix {

        private long[][] qualData;
        private int length;
        private int maxQual;
        private float[] means;
        private float[] medians;
        private float[] lowerQuarts;
        private float[] upperQuarts;
        private int[] mins;
        private int[] maxs;

        public QualityMatrix(int length, int maxQual) {
            qualData = new long[length][maxQual];
            this.length = length;
            this.maxQual = maxQual;
        }

        public void countQualAtPosition(int quality, int position) {
            if ((position >= qualData.length) || (quality >= qualData[0].length)) {
                new SimpleErrorMessage(null, "ERROR: Nucleotide position or quality score out of range!\n"
                        + "position: " + position + " score: " + quality + "\n"
                        + "maxlength=" + qualData.length + " maxqual=" + qualData[0].length);
                return;
            }
            qualData[position][quality]++;
        }

        // needs to do this:
        // sum( m[,1] * (m[,2] / sum(m[,2])) )
        // in java. With the first column containing the score
        // and the second the counts
        public float[] getWeightedMeans() {
            if (means == null) {
                means = new float[length];
            } else {
                return means;
            }

            for (int i = 0; i < length; i++) {
                for (int j = 0; j < maxQual; j++) {
                    //System.out.println("qualData["+i+"]["+j+"]="+qualData[i][j]);                    
                    means[i] += (float) (j * ((float) qualData[i][j] / (float) entriesRead));
                }
            }
            return means;
        }

        public float[] getMedians() {
            if (medians == null) {
                medians = qmatrix.getPercentile(0.5f);
            }
            return medians;
        }

        public float[] getUpperQuarts() {
            if (upperQuarts == null) {
                upperQuarts = qmatrix.getPercentile(0.75f);
            }
            return upperQuarts;
        }

        public float[] getLowerQuarts() {
            if (lowerQuarts == null) {
                lowerQuarts = qmatrix.getPercentile(0.25f);
            }
            return lowerQuarts;
        }

        private float[] getPercentile(float percent) {
            float[] percs = new float[length];

            for (int i = 0; i < length; i++) {
                long medSum = 0;
                for (int j = 0; j < maxQual; j++) {
                    medSum += qualData[i][j];
                    if (medSum >= entriesRead * percent) {
                        percs[i] = (float) ((j - 1) + (((float) entriesRead * percent + (float) qualData[i][j - 1]) / (float) entriesRead));
                        break;
                    }
                }
            }
            return percs;
        }

        public int[] getMins() {
            if (mins == null) {
                mins = new int[length];
            } else {
                return mins;
            }

            for (int i = 0; i < length; i++) {
                for (int j = 0; j < maxQual; j++) {
                    if (qualData[i][j] > 0) {
                        mins[i] = j;
                        break;
                    }
                }
            }
            return mins;
        }

        public int[] getMaxs() {
            if (maxs == null) {
                maxs = new int[length];
            } else {
                return maxs;
            }

            for (int i = 0; i < length; i++) {
                for (int j = maxQual - 1; j >= 0; j--) {
                    if (qualData[i][j] > 0) {
                        maxs[i] = j;
                        break;
                    }
                }
            }
            return maxs;
        }
        
        /**
         * remove zero count fields starting
         * from the end of the matrix to trim it
         * to the size of the longest read that was
         * recorded
         */        
        private void trimFromEnd() {
            int lastIndex = 0;
            float[] perc = this.getPercentile(1f);
            
            for (int i = perc.length-1; i >= 0 ; i-- ) {
                if (perc[i] == 0.0f) {
                    lastIndex = i;
                }
            }
            
            // now copy the matrix up to lastIndex into a new one
            long[][] newQual = Arrays.copyOf(qualData, lastIndex);
            qualData = newQual;
            length = lastIndex;
        }
    }

    public String getDescription() {
        return "The Sequence quality plots allow an overview of the base call qualities "
                + "assigned to each base by the base caller module of the sequencing pipeline."
                + " The plot shows the median (solid blue line), the 25th percentile and "
                + "the 75th percentile (lower and upper bound of the light blue area) of the "
                + "qualities at each position (cycle). Areas marked in red lie below a quality"
                + " score of 13 (approx. error probability of p=0.05).";
    }
}
