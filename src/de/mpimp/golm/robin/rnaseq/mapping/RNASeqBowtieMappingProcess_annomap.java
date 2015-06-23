/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.annotation.GFF3.AnnotationMap;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.robin.annotation.GFF3.GenomicLocation;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqBowtieMappingProcess_annomap extends RNASeqAbstractMappingProcess {

    private GFF3AnnotationProvider annotation;
    private BufferedWriter ambiguousReadsWriter;

    public RNASeqBowtieMappingProcess_annomap(RNASeqWorkflowPanel mainPanel, List<String> command) throws IOException {
        this.mainPanel = mainPanel;
        this.command = command;
        if (mainPanel == null) {
            this.ambiguousReadsWriter = new BufferedWriter(
                new FileWriter(
                new File("/Users/marc/Desktop/ambiguous_reads.txt")));
        } else {
            this.ambiguousReadsWriter = new BufferedWriter(
                new FileWriter(
                new File(mainPanel.getDataModel().getDetailedResultsDir(), mainPanel.getDataModel().getExperimentName() + "_ambiguous_reads.txt")));
        }
    }

    public RNASeqBowtieMappingProcess_annomap(
            RNASeqWorkflowPanel mainPanel,
            String name,
            final File bowtiePath,
            final ArrayList<String> args,
            final File index,
            final ArrayList<FastQFile> queryFiles) throws IOException {
        
        this.ambiguousReadsWriter = new BufferedWriter(
                new FileWriter(
                new File(mainPanel.getDataModel().getDetailedResultsDir(), mainPanel.getDataModel().getExperimentName() + "_ambiguous_reads.txt")));

        try {
            this.mainPanel = mainPanel;
            this.name = name;
            this.command = new ArrayList<String>();
            command.add(bowtiePath.getCanonicalPath());
            command.addAll(args);
            command.add(index.getCanonicalPath());

            String queries = null;
            if (Utilities.isMacOSX() || Utilities.isLinux()) {
                ArrayList<String> names = new ArrayList<String>();
                for (FastQFile file : queryFiles) {
                    names.add(file.getCanonicalPath());
                }
                queries = StringUtils.join(names, ",").replaceAll(" ", "\\ ");
            } else if (Utilities.isWindows()) {

                queries = "\"";
                for (int i = 0; i < queryFiles.size(); i++) {
                    queries += queryFiles.get(i).getCanonicalPath();
                    if (i == queryFiles.size() - 1) {
                        break;
                    }
                    queries += "\",\"";
                }
                queries += "\"";
            }
            command.add(queries);
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

    }

    public void run() {
        String cmdline = StringUtils.join(command, " ");

        SimpleLogger.getLogger(true).logMessage("bowtie-cmd: <" + cmdline + ">");

        //procBuilder = new ProcessBuilder(cmdline.split("\\s+"));
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

                    if (wantsToDie.get()) {
                        proc.destroy();
                        proc.waitFor();
                        return;
                    }
                    parseLine(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                proc.destroy();
            }
            proc.waitFor();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException i) {
            Exceptions.printStackTrace(i);
        }
        try {
            ambiguousReadsWriter.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void parseLine(String line) {
        boolean isAmbiguous = false;
        if (line.startsWith("#") || line.startsWith("Reported ")) {
            report.append(line + "\n");
        } else {
            String[] elements = line.split("\t");
            try {
                readsAligned.getAndIncrement();

                if (annotation != null) {
                    // using a genome plus GFF anno as reference - extract hit gene name from GFF anno
                    // First, construct a location from the alignment coordinates - we ignore the strand here
                    GenomicLocation alignLoc = new GenomicLocation("test", Integer.parseInt(elements[3]), elements[4].length() + Integer.parseInt(elements[3]));
                    List<FeatureI> genesAtLoc = annotation.getOverlappingFeatures(elements[2], alignLoc);
                        
                    System.out.println("last:"+lastReadName.get()+" current:"+elements[0]);
                    if (lastReadName.get().equals(elements[0])) {
                        // ambiguously aligning read! write to file and keep
                        // that for later RPKM computation
                        isAmbiguous = true;                        
                    }

                    for (FeatureI hitLoc : genesAtLoc) {
                        String ID = hitLoc.getAttribute("ID");
                        if (ID == null) {
                            new SimpleErrorMessage(null, "Could not extract ID from annotated feature:\n" + hitLoc.toString() + "\n...skipping.");
                            continue;
                        }

                        if (isAmbiguous) {                            
                            ambiguousReadsWriter.write(elements[0] + "\t" + ID + "\n");
                        } else {
                            countsTable.put(ID,
                                    (countsTable.containsKey(ID) ? countsTable.get(ID) + 1 : 1));
                        }
                    }
                } else {
                    countsTable.put(elements[2],
                            (countsTable.containsKey(elements[2]) ? countsTable.get(elements[2]) + 1 : 1));
                }
                lastReadName.getAndSet(elements[0]); // memorize the read name      
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("bad line: <" + line + ">");
                SimpleLogger.getLogger(true).logException(e);
            } catch (Exception e) {
                SimpleLogger.getLogger(true).logException(e);
            }
        }
    }

    public void setAnnotation(GFF3AnnotationProvider GFFAnno) {
        this.annotation = GFFAnno;
    }

    @Override
    public float getPercentAligned() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
