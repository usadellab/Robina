/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqMappingResultPanel;
import de.mpimp.golm.robin.GUI.plots.LineDeviationPlot;
import de.mpimp.golm.robin.GUI.plots.LinePlot;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.data.RNASeqSample;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import de.mpimp.golm.robin.GUI.ProgressDialog;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqMappingProcessController;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQParser;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqDelegate {

    private RNASeqWorkflowPanel workflowPanel;

    public RNASeqDelegate(RNASeqWorkflowPanel panel) {
        this.workflowPanel = panel;
    }

    public VERSION checkFastQInput(final ArrayList<FastQFile> files) throws FileNotFoundException, IOException, Exception {

        final ProgressDialog mwin = new ProgressDialog(workflowPanel.getMainGUI(), true, true);
        mwin.setText("Scanning input files. Please be patient...");
        mwin.setIndeterminate(true);


        SwingWorker<VERSION, Integer> scanWorker = new SwingWorker<VERSION, Integer>() {
            @Override
            protected VERSION doInBackground() throws Exception {
                FastQParser parser = new FastQParser(files);
                try {
                    parser.setScanDepth(RobinConstants.INPUTCHECK_SCANDEPTH);
                    parser.setScanMode(true);
                    parser.parseNow();
                    if (!parser.inputHasSameVersions()) {
                        mwin.dispose();
                        throw new Exception("Different quality encoding versions in input");
                    }

                } catch (FileNotFoundException ex) {
                    mwin.dispose();
                    //SimpleLogger.getLogger(true).logException(ex);
                    throw ex;
                } catch (IOException ex) {
                    mwin.dispose();
//                    SimpleLogger.getLogger(true).logException(ex);
                    throw ex;
                } catch (Exception ex) {
                    mwin.dispose();
//                    SimpleLogger.getLogger(true).logException(ex);
                    throw ex;
                }
                mwin.dispose();
                return (VERSION) parser.getVersions().toArray()[0];
            }
        };
        scanWorker.execute();
        mwin.setVisible(true);
        return scanWorker.get();
    }

    public void decompressAllInputFiles() {

        final ProgressDialog m = new ProgressDialog(workflowPanel.getMainGUI(), true, true);
        m.setText("Decompressing all input files ...");
        m.setIndeterminate(true);

        SwingWorker decompressWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                RNASeqDataModel data = workflowPanel.getDataModel();
                ArrayList<FastQFile> decFiles = new ArrayList<FastQFile>();

                for (RNASeqSample sample : data.getSamples().values()) {

                    for (int i = sample.getInputFiles().size() - 1; i >= 0; i--) {

                        if (sample.getInputFiles().get(i).getName().toLowerCase().endsWith(".gz")
                                || sample.getInputFiles().get(i).getName().toLowerCase().endsWith(".bz2")
                                || sample.getInputFiles().get(i).getName().toLowerCase().endsWith(".zip")) {
                            try {
                                FastQFile outdir = new FastQFile(data.getProjectDir(), "input");
                                FastQFile outfile = new FastQFile(outdir, sample.getInputFiles().get(i).getName().replaceAll("(?i)^(.+)\\.(gz|bz2|zip)$", "$1"));
                                SimpleLogger.getLogger(true).logMessage("Decompressing " + sample.getInputFiles().get(i).getName());
                                Utilities.decompressFile(sample.getInputFiles().get(i), outfile);
                                //mwin.dispose();
                                sample.getInputFiles().remove(i);
                                decFiles.add(outfile);
                            } catch (IOException ex) {
                                try {                                    
                                SimpleLogger.getLogger(true).logException(
                                        new Exception("Problems decompressing file:\n" 
                                        + sample.getInputFiles().get(i).getCanonicalPath(), ex));
                                } catch (IOException ex1) {
                                    SimpleLogger.getLogger(true).logException(ex1);
                                }
                            }
                        }
                    }
                    sample.getInputFiles().addAll(decFiles);
                }
                return null;
            }

            @Override
            protected void done() {
                m.dispose();
            }
        };

        decompressWorker.execute();
        m.setVisible(true);
    }

    public void computeMappingOverviewStats(final SimpleIntegerDataFrame countsTable) {

        final ProgressDialog m = new ProgressDialog(workflowPanel.getMainGUI(), true, true);
        m.setText("Computing mapping overview stats");
        m.setIndeterminate(true);
        SimpleLogger.getLogger(true).logMessage("Computing mapping overview stats - delegate");


        //DEBUG
        System.out.println("counts table dim rows: " + countsTable.getDimensions()[0]
                + "\t col:" + countsTable.getDimensions()[1]);


        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                //DEBUG
                System.out.println("in counts table background worker");

                HashMap<String, Long> colsums = countsTable.getColumnSums();
                for (String colname : colsums.keySet()) {
                    SimpleLogger.getLogger(true).logMessage("Counts in sample " + colname + ": " + colsums.get(colname));
                }
                SimpleLogger.getLogger(true).logMessage("Counts total across all samples: " + countsTable.getGrandSum());
                for (int a = 0; a < countsTable.getDimensions()[1]; a++) {
                    float[] dist = countsTable.getBinnedCountFreqsForColumn(a, 50);
                }
                m.dispose();
                return null;
            }
        };
        worker.execute();
        m.setVisible(true);
    }

    public ChartPanel getCountFreqDistributionLineChartForColumn(final int colindex, final SimpleIntegerDataFrame data, final Dimension size) {

//        SwingWorker worker = new SwingWorker() {
//            @Override
//            protected void done() {
//                return get();
//            }
//
//            @Override
//            protected Object doInBackground() throws Exception {
        LinePlot distPlot = new LinePlot(
                data.getColname(colindex),
                "Counts",
                "No.genes",
                data.getColMin(colindex), 0,
                data.getColMax(colindex), 1,
                false);
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries dataseries = new XYSeries("freqs");

        float[] freqs = data.getBinnedCountFreqsForColumn(colindex, 50);
        for (int i = 0; i < freqs.length; i++) {
            dataseries.add(i, freqs[i]);
        }
        dataset.addSeries(dataseries);
        distPlot.setDataset(dataset);
        distPlot.setColors(new Color[]{Color.BLUE});
        distPlot.setLineWidth(2f);
        return distPlot.getChartPanel(size);
//            }
//        };
    }

    public ChartPanel getCountFreqDistributionChartForColumn(int colindex, SimpleIntegerDataFrame data, Dimension size) {

        int BINNUMBER = 50;
        LineDeviationPlot distPlot = new LineDeviationPlot(
                "Count frequencies",
                "",
                "Fraction",
                -1, 0,
                -1, -1,
                false);


        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        YIntervalSeries dataSeries = new YIntervalSeries("quality");

        float[] freqs = data.getBinnedCountFreqsForColumn(colindex, BINNUMBER);
        int colmax = data.getColMax(colindex);
        int colmin = data.getColMin(colindex);
        int step = (colmax - colmin) / BINNUMBER;

        for (int i = 0; i < freqs.length; i++) {
            dataSeries.add(i * step, freqs[i], 0, freqs[i]);
        }

        dataset.addSeries(dataSeries);
        distPlot.setDataset(dataset);

        distPlot.setColors(new Color[]{Color.BLUE});
        distPlot.setLineWidth(2f);
        
        // make sure the plot is actually rendered not scaled!
        ChartPanel panel = distPlot.getChartPanel(size);
        panel.setMinimumDrawHeight(Integer.MIN_VALUE);
        panel.setMinimumDrawWidth(Integer.MIN_VALUE);
        
        return panel;
    }

    public String getSysArchString() {
        String archstring = null;
        if (Utilities.isLinux()) {
            archstring = "linux-";
            Process p;
            String retVal = null, line = null;
            try {
                p = Runtime.getRuntime().exec(new String[]{"uname", "-p"});
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = br.readLine()) != null) {
                    retVal = line;
                }
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return archstring + retVal;
        } else if (Utilities.isMacOSX()) {
            archstring = "mac-" + System.getProperty("os.arch");
        } else if (Utilities.isWindows()) {
            // there is no 64bit version of bowtie for windows
            // ...only the bowtie-build version i was bold enough to
            // compile using MinGW.
            archstring = "windows-" + System.getProperty("os.arch") + ".exe";
            return archstring;
        }
        return archstring;
    }

//    private void populateMappingSummaryPanel(final SimpleIntegerDataFrame countsTable, final RNASeqMappingProcessController mappingController, final JPanel resultPanel) {
//
//        final MessageWindow m = new MessageWindow("Computing mapping overview stats");
//        SimpleLogger.getLogger(true).logMessage("Computing mapping overview stats");
//
//        SwingWorker worker = new SwingWorker() {
//
//            @Override
//            protected Object doInBackground() throws Exception {
//                JPanel mappingResultPanel = new JPanel();
//                mappingResultPanel.setBackground(Color.WHITE);
//                mappingResultPanel.setLayout(new BoxLayout(mappingResultPanel, BoxLayout.Y_AXIS));
//
//                for (int i = 0; i < countsTable.getDimensions()[1]; i++) {
//                    ChartPanel panel = RNASeqDelegate.this.getCountFreqDistributionChartForColumn(i, countsTable, new Dimension(100, 50));
//
//                    String report = mappingController.getProcessByName(countsTable.getColname(i)).getReport();
//
//                    RNASeqMappingResultPanel rPan = new RNASeqMappingResultPanel(countsTable.getColname(i), panel, report);
//                    mappingResultPanel.add(rPan);
//                    JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
//                    sep.setBackground(Color.WHITE);
//                    mappingResultPanel.add(sep);
//                }
//                return mappingResultPanel;
//            }
//            
//            @Override
//            protected void done() {
//                try {
//                    resultPanel = (JPanel) get();
//                } catch (InterruptedException ex) {
//                    Exceptions.printStackTrace(ex);
//                } catch (ExecutionException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//                
//            }};
//        }
    public boolean importCountsTable(RNASeqDataModel dataModel) throws FileNotFoundException, IOException {
        if (!dataModel.isImportCountsTable()) {
            return false;
        }

        // read the header and extract conditions and replicates
        BufferedReader reader = new BufferedReader(new FileReader(dataModel.getImportCountsTableFile()));
        String firstLine = reader.readLine();

        if (!firstLine.toUpperCase().startsWith("ID")) {
            new SimpleErrorMessage(workflowPanel, "The header row of the imported counts table does\n"
                    + "not match the format requirements. Please\n"
                    + "click the quick help button for details.");
            return false;
        }

        String[] elems = firstLine.split("\\t");
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        for (String elem : elems) {
            String[] sample = elem.split("_");

            if (sample[0].toUpperCase().startsWith("ID")) {
                continue;
            }

            if (m.containsKey(sample[0])) {
                m.put(sample[0], m.get(sample[0]) + 1);
            } else {
                m.put(sample[0], 1);
            }
        }
        dataModel.setConditions(m);

        
        for (Map.Entry<String, Integer> en : m.entrySet()) {
            System.out.println(en.getKey() + "\t" + en.getValue());
        }
        
        // check data format consistency of the table
        // this might be costly but necessary...
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.matches("^[\\w\\._]+(\\t\\d+)+$")) {
                new SimpleErrorMessage(workflowPanel, "Counts table format contains incompatible data:\n<" + line + ">\n");
                reader.close();
                return false;
            }
        }
        reader.close();

        if (!new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt").exists()) {
            Utilities.copyFile(dataModel.getImportCountsTableFile(),
                    new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt"));
        }
        return true;
    }
}
