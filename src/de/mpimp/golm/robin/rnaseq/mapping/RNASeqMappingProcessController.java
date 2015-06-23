/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.ProgressDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqMappingProcessController implements ActionListener {

    private Timer processTimer;
    private RNASeqWorkflowPanel main;
    private RNASeqDataModel dataModel;
    private ArrayList<RNASeqAbstractMappingProcess> processes;
    private Future[] processStati;
    private ExecutorService executor;
    private boolean cancelled = false;
    private SimpleIntegerDataFrame countsTable;
    private boolean allDone = false;
    private static final int POLL_INTERVAL = 100;
    private boolean saveAlignments = false;

    public RNASeqMappingProcessController(RNASeqWorkflowPanel mainPanel, RNASeqDataModel dataModel, ExecutorService executor) {
        this.main = mainPanel;
        this.executor = executor; //Executors.newFixedThreadPool(mainPanel.getNumberOfParallelProcessesSetting()); // we create a new one with the number of threads desired
        this.dataModel = dataModel;
        this.processTimer = new Timer(POLL_INTERVAL, this);
        this.processes = new ArrayList<RNASeqAbstractMappingProcess>();

    }

    public void addProcess(RNASeqAbstractMappingProcess process) {
        this.processes.add(process);
    }

    public void start() {
        if (processes.size() < 1) {
            return;
        }
        this.processStati = new Future[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            main.appendToMappingProgressPane("Scheduling mapping process for sample " + processes.get(i).getName(), RobinConstants.attrBoldBlack);
            Future stat = executor.submit(processes.get(i));
            processStati[i] = stat;
            this.processTimer.start();
        }
        main.appendToMappingProgressPane("\n", null);
    }

    public void actionPerformed(ActionEvent e) {

        allDone = true;
        main.updateTimeElapsed();
        for (int i = 0; i < processStati.length; i++) {
            Future processStatus = processStati[i];

            if (processStatus == null) {
                continue;
            }

            if (!processStatus.isDone()) {
                long reads = processes.get(i).getReadsAligned();
                if (reads == 0) {
                    allDone = false;
                    continue;
                }
                allDone = false;

            } else {
                // MAPPING DONE!
                main.appendToMappingProgressPane("\nfinished mapping reads of sample: " 
                        + processes.get(i).getName(), RobinConstants.attrBoldBlack);
                main.appendToMappingProgressPane(processes.get(i).getReport() + "\n",
                        RobinConstants.attrBoldBlack);
                main.resetStaticAppend();
                processStati[i] = null;
            }
        }

        if (allDone) {
            processTimer.stop();
            allDone = false;
            constructCountsTable();
        }
    }

    public void cancel() {
        for (RNASeqAbstractMappingProcess p : processes) {
            p.cancel();
        }
        cancelled = true;
        main.appendToMappingProgressPane("\nCancelling all processes...", RobinConstants.attrBoldRed);
    }

    public SimpleIntegerDataFrame getCountsTable() {
        if (!allDone) {
            new SimpleErrorMessage(null, "counts table not ready!");
            return null;
        } else if (countsTable == null) {
            constructCountsTable();
        }
        return countsTable;
    }

    public RNASeqAbstractMappingProcess getProcessByName(String name) {
        for (RNASeqAbstractMappingProcess proc : processes) {
            if (proc.getName().equals(name)) {
                return proc;
            }
        }
        return null;
    }

    private void constructCountsTable() {

        final ProgressDialog mw = new ProgressDialog(main.getMainGUI(), true, true);
        mw.setText("Constructing counts table");
        mw.setIndeterminate(true);

        SwingWorker<SimpleIntegerDataFrame, Object> countsTableWorker = new SwingWorker<SimpleIntegerDataFrame, Object>() {
            @Override
            protected SimpleIntegerDataFrame doInBackground() throws Exception {
                SimpleLogger.getLogger(true).logMessage("start constructing counts table");

                SimpleIntegerDataFrame countsTable = new SimpleIntegerDataFrame();
                for (RNASeqAbstractMappingProcess proc : processes) {
                    try {
                        countsTable.addColumn(proc.getCountsTable(), proc.getName());
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                File out = new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt");
                try {
                    countsTable.writeToFile(out);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return countsTable;
            }

            @Override
            protected void done() {
                try {
                    RNASeqMappingProcessController.this.countsTable = this.get();
                    allDone = true;
                } catch (InterruptedException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (ExecutionException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                }
                mw.dispose();
                main.mappingStepFinished(cancelled);
                SimpleLogger.getLogger(true).logMessage("done constructing counts table");
            }
        };
        countsTableWorker.execute();
        mw.setVisible(true);
    }

    public boolean isSaveAlignments() {
        return saveAlignments;
    }

    public void setSaveAlignments(boolean saveAlignments) {
        this.saveAlignments = saveAlignments;

        // set save alignments for all owned processes        
        for (RNASeqAbstractMappingProcess proc : processes) {
            proc.setSaveAlignments(saveAlignments);
        }

    }
}
