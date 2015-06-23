/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.trimmomatic;

import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.usadellab.trimmomatic.fastq.trim.Trimmer;

/**
 *
 * @author marc
 */
public class TrimmomaticThread implements Runnable {
    
    private TrimmomaticProcess tmProcess;
    private Object owner;

    public TrimmomaticThread(FastQFile input, File workDir, ArrayList<Trimmer> modules, int phredOffset, Object owner) {
        this.tmProcess = new TrimmomaticProcess(input, workDir, modules, phredOffset);
        this.owner = owner;
    }
    

    @Override
    public void run() {
        try {
            tmProcess.process();            
        } catch (IOException ex) {
            Logger.getLogger(TrimmomaticThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TrimmomaticThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getProgress() {
        return this.tmProcess.getProgress();
    }
    
    public synchronized long getEntriesProcessed() {
        return tmProcess.getEntriesProcessed();
    }

    public synchronized long getEntriesSurvived() {
        return tmProcess.getEntriesSurvived();
    }

    public synchronized long getNucleotidesProcessed() {
        return tmProcess.getNucleotidesProcessed();
    }

    public synchronized long getNucleotidesSurvived() {
        return tmProcess.getNucleotidesSurvived();
    }

    public synchronized void cancel() {
        this.tmProcess.cancel();
    }
    
    public synchronized ArrayList<File> getOutputFiles() {        
        return tmProcess.getOutputFiles();
    }

    public synchronized float getPercentageUnknown() {
        return tmProcess.getPercentageUnknown();
    }
}
