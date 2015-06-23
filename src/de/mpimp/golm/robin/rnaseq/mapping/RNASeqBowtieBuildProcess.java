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
public class RNASeqBowtieBuildProcess implements Runnable {

    private RNASeqDataModel dataModel;
    private ProcessBuilder procBuilder;
    private ArrayList<String> command;
    private int exitVal;

    public RNASeqBowtieBuildProcess(final RNASeqDataModel dataModel, final String bowtie_build_path, final List<String> args, final File indexPath) {
        
        this.dataModel = dataModel;
        try {
            this.command = new ArrayList<String>() {
                {
                    add(bowtie_build_path);
                    addAll(args);
                    add(dataModel.getReferenceFile().getCanonicalPath());
                    add(indexPath.getCanonicalPath());
                }
            };
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void run() {
        SimpleLogger.getLogger(true).logMessage("bowtie-build-cmd: <" + StringUtils.join(command, " ") + ">");
        
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
            
            SimpleLogger.getLogger(true).logMessage("bowtie-build-cmd exit code: " + exitVal);

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
