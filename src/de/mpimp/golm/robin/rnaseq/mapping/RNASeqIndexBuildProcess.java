/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqIndexBuildProcess implements Runnable {

    private RNASeqDataModel dataModel;
    private ProcessBuilder procBuilder;
    private ArrayList<String> command;
    private int exitVal;

    /**
     * 
     * @param dataModel
     * @param index_build_path  where is the file to build 
     * @param args  argument
     * @param indexPath  path to the fasta file to be indexed
     */
    public RNASeqIndexBuildProcess(final RNASeqDataModel dataModel, final String index_build_path, final List<String> args, final String indexswitch, final File indexPath) {
        
        this.dataModel = dataModel;
        try {
            this.command = new ArrayList<String>() {
                {
                    add(index_build_path);
                    addAll(args);
                    add(dataModel.getReferenceFile().getCanonicalPath());
                    if (indexswitch !=null) add(indexswitch);
                    add(indexPath.getCanonicalPath());
                }
            };
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void run() {
        SimpleLogger.getLogger(true).logMessage("index build command [bowite|kallisto]: < " + StringUtils.join(command, " ") + " >");
        
        procBuilder = new ProcessBuilder(command);
        procBuilder.redirectErrorStream(true);
        try {
            final Process proc = procBuilder.start();

            InputStream is = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bReader = new BufferedReader(reader, 10000000);

            String line;
            try {
                while ((line = bReader.readLine()) != null) {
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                proc.destroy();
            }
            proc.waitFor();
            exitVal = proc.exitValue();
            
            SimpleLogger.getLogger(true).logMessage("index build command [bowtie|kallisto]: exit code: " + exitVal);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException i) {
            Exceptions.printStackTrace(i);
        }
    }

    public int getExitValue() {
        return exitVal;
    }
}
