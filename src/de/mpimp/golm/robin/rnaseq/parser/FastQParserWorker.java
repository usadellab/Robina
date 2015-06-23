/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class FastQParserWorker extends SwingWorker {
    
    FastQParser parser;

    public FastQParserWorker(FastQParser parser) {
        super();
        this.parser = parser;
    }
    

    @Override
    protected Object doInBackground() throws Exception {
        try {
            parser.parseNow();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public int getParserProgress() {
        try {
            return parser.getProgress();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return 0;
    }
    
    @Override
    protected void done() {
        System.out.println("done");
    }
}
