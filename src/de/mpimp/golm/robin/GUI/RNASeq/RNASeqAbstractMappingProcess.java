/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public abstract class RNASeqAbstractMappingProcess implements Runnable {

    protected RNASeqDataModel dataModel;
    protected RNASeqWorkflowPanel mainPanel;
    protected HashMap<String, Integer> countsTable;
    protected ProcessBuilder procBuilder;
    protected List<String> command;
    protected StringBuilder report;
    protected AtomicLong readsAligned;
    protected AtomicBoolean wantsToDie;
    protected AtomicReference<String> lastReadName;
    protected String name;
    protected boolean saveAlignments;
    protected BufferedWriter alignmentWriter;

    public RNASeqAbstractMappingProcess() {
        this.report = new StringBuilder();
        this.countsTable = new HashMap<String, Integer>();
        this.readsAligned = new AtomicLong();
        this.wantsToDie = new AtomicBoolean(false);
        this.lastReadName = new AtomicReference<String>("");
    }
    
    /**
     * Abstract methods
     */
    protected abstract void parseLine(String line) throws Exception;
    public abstract void run();
    public abstract float getPercentAligned();    
    
    /**
     * Implemented methods
     */
    public void cancel() {
        this.wantsToDie.getAndSet(true);
    }
    
    protected void initAlignmentWriter() throws IOException {
        File alnFile = new File(mainPanel.getDataModel().getDetailedResultsDir(),
            mainPanel.getDataModel().getExperimentName() + "_" + name + "_alignments.txt");
        
        SimpleLogger.getLogger(true).logMessage("writing raw alignments to file: "+alnFile.getCanonicalPath());        
        alignmentWriter  = new BufferedWriter(new FileWriter(alnFile));
    }
    
    public HashMap<String, Integer> getCountsTable() {
        return this.countsTable;
    }
    
    /*necessary for Kallisto as parsing happens once done*/
    public void flushCounts() {
       
    }

    public boolean isSaveAlignments() {
        return saveAlignments;
    }

    public void setSaveAlignments(boolean saveAlignments) {        
        if (alignmentWriter == null) {
            try {
                initAlignmentWriter();
            } catch (IOException ex) {
                SimpleLogger.getLogger(true).logException(ex);
            }
        }        
        this.saveAlignments = saveAlignments;
    }

    public String getReport() {
        return report.toString();
    }

    public synchronized long getReadsAligned() {
        return readsAligned.get();
    }

    public synchronized String getLastReadName() {
        return lastReadName.get();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
