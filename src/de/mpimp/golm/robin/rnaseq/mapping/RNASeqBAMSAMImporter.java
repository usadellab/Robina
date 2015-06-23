/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.data.RNASeqSample;
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import de.mpimp.golm.common.gui.CollapsibleInfoDialog;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.ProgressDialog;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import net.sf.samtools.SAMFileReader;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author marc
 */
public class RNASeqBAMSAMImporter implements ActionListener {

    private RNASeqDataModel dataModel;
    private RobinMainGUI mainGUI;
    private ArrayList<Future> parserStates;
    private ArrayList<RNASeqBAMSAMParser> parsers;
    private Timer pollTimer;
    private ExecutorService executor;
    private static final int POLL_INTERVAL = 500;
    private ProgressDialog progressDialog;
    private RNASeqWorkflowPanel workflowPanel;
    private SAMFileReader.ValidationStringency stringency;
    private boolean importFaied = false;

    public RNASeqBAMSAMImporter(RNASeqDataModel dataModel, RobinMainGUI mainGUI, RNASeqWorkflowPanel workflowPanel) {
        this.dataModel = dataModel;
        this.mainGUI = mainGUI;
        this.workflowPanel = workflowPanel;
        this.executor = Executors.newFixedThreadPool(workflowPanel.getNumberOfParallelProcessesSetting());
    }

    public void importSamples() {

        RNASeqBAMSAMImportDialog dialog = new RNASeqBAMSAMImportDialog(mainGUI, true, dataModel);
        dialog.setVisible(true);

        if (dialog.isCancelled()) {
            dialog.dispose();
            return;
        }

        stringency = dialog.getValidationStringency();

        dialog.dispose();

        progressDialog = new ProgressDialog(mainGUI, true, true);


        SimpleLogger.getLogger(true).logMessage("Starting BAM/SAM import.");

        progressDialog.setText("Reading GFF3 annotation file...");
        progressDialog.setIndeterminate(true);
        progressDialog.repaint(5);
        if (dataModel.getReferenceType() == RNASeqDataModel.REFERENCE_TYPE.GENOME) {

            SwingWorker<GFF3AnnotationProvider, Integer> annoWorker = new SwingWorker<GFF3AnnotationProvider, Integer>() {
                @Override
                protected GFF3AnnotationProvider doInBackground() throws Exception {
                    return new GFF3AnnotationProvider(dataModel.getGFF3annotationFile(),
                            Arrays.asList(RobinConstants.EXTRACT_GFF3_FEATURES_LIST));
                }

                @Override
                protected void done() {
                    try {
                        startImportTasks(get());
                    } catch (InterruptedException ex) {
                        SimpleLogger.getLogger(true).logException(ex);
                    } catch (ExecutionException ex) {
                        SimpleLogger.getLogger(true).logException(ex);
                    }
                }
            };
            annoWorker.execute();
            progressDialog.setVisible(true);
        } else {
            startImportTasks(null);
        }
    }

    private void startImportTasks(GFF3AnnotationProvider anno) {
        // submit all import threads 
        parserStates = new ArrayList<Future>();
        parsers = new ArrayList<RNASeqBAMSAMParser>();

        progressDialog.setText("Importing BAM/SAM alignments...");

        for (RNASeqSample sample : dataModel.getSamples().values()) {
            RNASeqBAMSAMParser parser = new RNASeqBAMSAMParser(sample, anno, stringency);
            parsers.add(parser);
            parserStates.add(executor.submit(parser));
        }
        pollTimer = new Timer(POLL_INTERVAL, this);
        pollTimer.start();
        progressDialog.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        // check whether the import threads are done       
        boolean finished = true;
        float doneTasks = 0;

        for (Future fut : parserStates) {
            if (!fut.isDone()) {
                finished = false;
            } else {
                doneTasks++;
            }
        }

        // update progress
        int prog = (int) ((doneTasks / (float) parsers.size()) * 100);
        if (prog > 0) {
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(prog);
        }


        int records = 0;
        for (RNASeqBAMSAMParser parser : parsers) {
            records += parser.getRecordsRead();
        }

        progressDialog.setText("Importing BAM/SAM alignments... (" + records + " records)");
        progressDialog.repaint(5);
        //DEBUG
        //System.out.println("checking BAMSAMimport done: "+finished+" progress is: "+prog);

        if (!finished) {
            return;
        }

        SimpleLogger.getLogger(true).logMessage("total number of BAM/SAM records read: " + records);

        // all done
        pollTimer.stop();
        finishImport();
    }

    public void finishImport() {

        // first check whether all files were imported without errors
        boolean allOK = true;
        StringBuilder errMsg = new StringBuilder();

        for (RNASeqBAMSAMParser parser : parsers) {
            if (parser.getExceptions().size() != 0) {
                allOK = false;
                for (Exception e : parser.getExceptions()) {
                    errMsg.append("----------------------------------\n");
                    errMsg.append("Sample: " + parser.getSample().toString());
                    errMsg.append("Errors:\n");
                    errMsg.append("-------\n");
                    errMsg.append(ExceptionUtils.getStackTrace(e));                    
                }
            }
        }
        
        if (!allOK) {
            CollapsibleInfoDialog dialog = new CollapsibleInfoDialog(mainGUI, JOptionPane.ERROR_MESSAGE, "BAM / SAM import failed due to\n"
                    + "input file format errors.", errMsg.toString());
            progressDialog.dispose();
            dialog.setVisible(true);
            importFaied = true;
            return;
        }

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                SimpleIntegerDataFrame countsTable = new SimpleIntegerDataFrame();
                File out = new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt");


                try {
                    for (RNASeqBAMSAMParser parser : parsers) {
                        countsTable.addColumn(parser.getCountsTable(), parser.getSample().getSampleName());
                    }
                    countsTable.writeToFile(out);

                } catch (IOException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (Exception e) {
                    SimpleLogger.getLogger(true).logException(e);
                }
                return null;
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                workflowPanel.finishedImportingBamSam();
                SimpleLogger.getLogger(true).logMessage("Finished BAM/SAM import.");
            }
        };

        progressDialog.setText("Generating counts table...");
        progressDialog.setIndeterminate(true);
        worker.execute();
    }

    public boolean importFailed() {
        return importFaied;
    }
}
