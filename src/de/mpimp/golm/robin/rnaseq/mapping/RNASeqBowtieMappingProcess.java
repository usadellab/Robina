/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.annotation.AbstractAnnotationProvider;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.Location;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqBowtieMappingProcess extends RNASeqAbstractMappingProcess {

    private AbstractAnnotationProvider annotation;
    private BufferedWriter ambiguousReadsWriter;
    private BufferedWriter uniqueReadsWriter;
    private long uniqueHits = 0, isoFormHits = 0, ambiguousHits = 0, overLapGenesHits = 0, unannotatedHits = 0;
    private int exitCode; 
    private ArrayList<FastQFile> inputFiles; 
    private RNASeqBowtieErrorRecorder errorRecorder;

    public RNASeqBowtieMappingProcess(RNASeqWorkflowPanel mainPanel, List<String> command) {
        this.mainPanel = mainPanel;
        this.command = command;
        this.errorRecorder = new RNASeqBowtieErrorRecorder();
        try {
            initAmbiguousWriter();            
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }

    public RNASeqBowtieMappingProcess(
            RNASeqWorkflowPanel mainPanel,
            String name,
            final File bowtiePath,
            final ArrayList<String> args,
            final File index,
            final ArrayList<FastQFile> queryFiles) {

        try {
            this.mainPanel = mainPanel;
            this.name = name;
            this.command = new ArrayList<String>();
            this.inputFiles = queryFiles;
            this.errorRecorder = new RNASeqBowtieErrorRecorder();
            initAmbiguousWriter();

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

    private void initAmbiguousWriter() throws IOException {
        File ambFile = new File(mainPanel.getDataModel().getDetailedResultsDir(), 
                mainPanel.getDataModel().getExperimentName() + "_" + name + "_ambiguous_reads.txt");
            ambiguousReadsWriter = new BufferedWriter(new FileWriter(ambFile));

        File uniqFile = new File(mainPanel.getDataModel().getDetailedResultsDir(), 
                mainPanel.getDataModel().getExperimentName() + "_" + name + "_unique_reads.txt");
            uniqueReadsWriter = new BufferedWriter(new FileWriter(uniqFile));
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
                SimpleLogger.getLogger(true).logMessage("destroying bowtie process "+proc);
                proc.destroy();
            }
            proc.waitFor();
            
            ambiguousReadsWriter.close();
            uniqueReadsWriter.close();
            if (isSaveAlignments()) alignmentWriter.close();
            
            this.exitCode = proc.exitValue();
            
            if (exitCode != 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("Bowtie mapping process for input file(s)\n");
                for (FastQFile file : inputFiles) {
                    builder.append(file.getName()+"\n");
                }
                builder.append("finished with errors (exit code "+exitCode+")");           
                SimpleLogger.getLogger(true).logException(
                        new Exception(builder.toString() + "\ncmd: <"+cmdline+">\n"));
            }

        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);            
        } catch (InterruptedException i) {
            SimpleLogger.getLogger(true).logException(i);            
        }
    }

    protected void parseLine(String line) throws Exception {
        
        if (isSaveAlignments()) {
            alignmentWriter.write(line + "\n");
        }

        if (line.startsWith("#") || line.startsWith("Reported ")) {
            report.append(line + "\n");
        } else {
            String[] elements = line.split("\t");
            try {
                readsAligned.getAndIncrement();
                String readname = elements[0];
                
                // record mismatches/positions for later creation of a mismatch rate per position plot
                RNASeqBowtieAlignment alignment = new RNASeqBowtieAlignment(elements);
                errorRecorder.recordAlignment(alignment);

                if (annotation != null) {
                    // using a genome plus GFF anno as reference - extract hit gene name from GFF anno
                    // First, construct a location from the alignment coordinates - we ignore the strand here
                    Location alignLoc = new Location(Integer.parseInt(elements[3]), 
                            elements[4].length() + Integer.parseInt(elements[3]));
                    countReadUsingGenomicAnnotation_geneOnly(alignLoc, elements[2], readname);
                } else {
                    // count read using transcriptome ref                    
                    // same read having more than one alignment?
                    if (readname.equals(lastReadName.get()) && !lastReadName.get().equals("")) {
                        // ambiguously aligning read
                        ambiguousReadsWriter.write(readname + "\t" + elements[2] + "\n");
                    } else {
                        countsTable.put(elements[2],
                                (countsTable.containsKey(elements[2]) ? countsTable.get(elements[2]) + 1 : 1));
                        uniqueReadsWriter.write(readname + "\t" + elements[2] + "\n");
                    }
                }
                lastReadName.getAndSet(readname); // record the read name
            } catch (ArrayIndexOutOfBoundsException e) {
                SimpleLogger.getLogger(true).logMessage("skipping bad read: <" + line + ">");                
            } catch (Exception e) {                
                StringBuilder builder = new StringBuilder();
                builder.append("Bowtie mapping process for input file(s)\n");
                for (FastQFile file : inputFiles) {
                    builder.append(file.getName()+"\n");
                }
                builder.append("failed with an unexpected error:"+e.getMessage());
                builder.append("while processing line:\n");
                builder.append("<" + line + ">");                       
                SimpleLogger.getLogger(true).logException(new Exception(builder.toString(), e));
                throw(e);
            }
        }
    }

    public void setAnnotation(GFF3AnnotationProvider annotation) {
        this.annotation = annotation;
    }

    private void countReadUsingGenomicAnnotation_exonGene(Location alignLoc, String chromosome, String readname) throws Exception {
        List<FeatureI> rawGenesAtLoc = annotation.getOverlappingFeatures(chromosome, alignLoc);
        FeatureList genesAtLoc = new FeatureList();

        // select only 75% overlapping features
        for (FeatureI f : rawGenesAtLoc) {
            if (f.location().plus().percentOverlap(alignLoc.plus()) > 0.75D) {
                genesAtLoc.add(f);
            }
        }

        int numberExonsAtLoc = genesAtLoc.selectByType("exon").size();
        int numberGenesAtLoc = genesAtLoc.selectByType("gene").size();

        //DEBUG
        //System.out.println(readname + "\texons: "+numberExonsAtLoc+ "\tgenes:"+numberGenesAtLoc);

        // more than one feature annotated at this location? e.g. exon that is included in >1 isoform?
        if ((numberExonsAtLoc > 1) && (numberGenesAtLoc == 1)) {
            // same gene, same exon, different splicing isoforms
//                        System.out.println("\tsame gene, same exon, different splicing isoforms");
            isoFormHits++;
        } else if ((numberExonsAtLoc > 1) && (numberGenesAtLoc > 1)) {
            // same location, different splicing isoforms, different genes???? (only possible if genes overlap)
            System.out.println("\tsame location, different splicing isoforms, different genes???? (only possible if genes overlap)\n"
                    + "alignLoc: " + alignLoc + "\n"
                    + "genesAtLoc: " + genesAtLoc + "\n");
            overLapGenesHits++;
        } else if ((numberExonsAtLoc == 1) && (numberGenesAtLoc > 1)) {
            // again overlapping genes sharing the same exon that is annotated only once (unlikely)
            System.out.println("\tagain overlapping genes sharing the same exon that is annotated only once (unlikely)\n"
                    + "alignLoc: " + alignLoc + "\n"
                    + "genesAtLoc: " + genesAtLoc + "\n");
            overLapGenesHits++;
        } else if ((numberExonsAtLoc == 1) && (numberGenesAtLoc == 1)) {
            // gene with only one splicing isoform - they should all be like this....
//                        System.out.println("\tgene with only one splicing isoform - they should all be like this....");
            uniqueHits++;
        } else if (numberGenesAtLoc == 0) {
            // hits in unannotated space
            unannotatedHits++;
        }

        for (FeatureI feature : genesAtLoc) {
            // this should be the way exons are annotated.... 
            // the code breaks it there is no Parent key
            String ID = null;
            
            for (String idKey : new String[] {"Parent", "ID", "gene_id"} ) { //gene_id is mandatory for GTF
                ID = (feature.hasAttribute( idKey ) ? feature.getAttribute( idKey ) : null);
                if (ID != null) break;
            }

            if (ID == null) {
                new SimpleErrorMessage(null, "Could not extract ID from annotated feature:\n" + feature.toString() + "\n...skipping.");
                continue;
            }

            // same read having more than one alignment?
            if (readname.equals(lastReadName.get()) && !lastReadName.get().equals("")) {
                // ambiguously aligning read
                ambiguousReadsWriter.write(readname + "\t" + ID + "\n");
                ambiguousHits++;
            } else {
                countsTable.put(ID,
                        (countsTable.containsKey(ID) ? countsTable.get(ID) + 1 : 1));
                uniqueReadsWriter.write(readname + "\t" + ID + "\n");
            }
        }
    }

    private void countReadUsingGenomicAnnotation_geneOnly(Location alignLoc, String chromosome, String readname) throws Exception {
        List<FeatureI> rawGenesAtLoc = annotation.getOverlappingFeatures(chromosome, alignLoc);
        FeatureList genesAtLoc = new FeatureList();

        // select only 75% overlapping features
        for (FeatureI f : rawGenesAtLoc) {
            if (alignLoc.plus().percentOverlap(f.location().plus()) > 0.75D) {
                genesAtLoc.add(f);
            }
        }

        if (genesAtLoc.size() > 1) {
            // in most cases these are genes annotated on opposite strands
            
        }

        for (FeatureI feature : genesAtLoc) {
            
            // this should be the way exons are annotated.... 
            // the code breaks it there is no Parent key            
            String ID = null;
            
            for (String idKey : new String[] {"Parent", "ID", "gene_id"} ) { //gene_id is mandatory for GTF
                ID = (feature.hasAttribute( idKey ) ? feature.getAttribute( idKey ) : null);
                if (ID != null) break;
            }

            if (ID == null) {
                new SimpleErrorMessage(null, 
                        "Could not extract ID from annotated feature:\n" 
                        + feature.toString() 
                        + "\n...skipping.");
                continue;
            }

            // same read having more than one alignment?
            if ( (readname.equals(lastReadName.get()) && !lastReadName.get().equals("")) || (genesAtLoc.size() > 1)) {
                // ambiguously aligning read
                ambiguousReadsWriter.write(readname + "\t" + ID + "\n");
                ambiguousHits++;
            } else {
                countsTable.put(ID,
                        (countsTable.containsKey(ID) ? countsTable.get(ID) + 1 : 1));
                uniqueReadsWriter.write(readname + "\t" + ID + "\n");
            }
        }
    }

    public int getExitCode() {
        return exitCode;
    }

    public RNASeqBowtieErrorRecorder getErrorRecorder() {
        return errorRecorder;
    }

    @Override
    /**
     * Needs to extract the percentage of reads with at least one valid alignment
     * from the aligment report of a format like:
     * # reads processed: 100000
     * # reads with at least one reported alignment: 41668 (41.67%)
     * # reads that failed to align: 53211 (53.21%)
     * # reads with alignments suppressed due to -m: 5121 (5.12%)
     * Reported 41668 alignments to 1 output stream(s)
     */
    public float getPercentAligned() {
        String report = this.getReport();
        
        Pattern percPattern;
        percPattern = Pattern.compile("^# reads with at least one reported"
                + " alignment: \\d+ \\((\\d+.\\d+)%\\)$", Pattern.MULTILINE);
        Matcher matcher = percPattern.matcher(report);
        if (!matcher.find()) {
            Exception badRep = new Exception("Could not extract percentage "
                    + "of aligned reads - unknown report format:\n"+ report);
            SimpleLogger.getLogger(true).logException(badRep);
            //throw badRep;
        }
        
        String perc = matcher.group(1);
        return Float.parseFloat(perc);        
    }
    
}
