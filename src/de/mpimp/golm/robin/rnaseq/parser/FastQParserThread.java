/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCprogressPanel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class FastQParserThread implements Runnable {

    private FastQParser parser;    
    private RNASeqQCprogressPanel owner;    

    public FastQParserThread(FastQParser p) {
        parser = p;
    }

    public void setOwner(RNASeqQCprogressPanel own) {
        owner = own;
    }
    
    public synchronized long getEntriesRead() {
        return parser.getNumberOfEntries();
    }

    @Override
    public void run() {
        try {
            parser.parseNow();
            owner.setIndeterminate(true);
            owner.generateResultPLots();
        } catch (FileNotFoundException ex) {            
            owner.addException(ex);            
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            owner.addException(ex); 
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            owner.addException(ex); 
            Exceptions.printStackTrace(ex);
        }

    }

    public int getProgress() throws IOException {
        return this.parser.getProgress();
    }

    public FastQParser getParser() {
        return this.parser;
    }
    
    public void cancel() {
        this.parser.cancel();
    }

}
