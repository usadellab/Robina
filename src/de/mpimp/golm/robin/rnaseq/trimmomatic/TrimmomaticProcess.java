/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.trimmomatic;

import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.usadellab.trimmomatic.fastq.FastqParser;
import org.usadellab.trimmomatic.fastq.FastqRecord;
import org.usadellab.trimmomatic.fastq.FastqSerializer;
import org.usadellab.trimmomatic.fastq.trim.BarcodeSplitter;
import org.usadellab.trimmomatic.fastq.trim.Trimmer;

/**
 *
 * @author marc
 */
public class TrimmomaticProcess {

    private File input;
    private File workDir;
    private ArrayList<Trimmer> trimmers = new ArrayList<Trimmer>();
    private int phredOffset;
    private File trimLog;
    private HashMap<String, FastqSerializer> outputSerializers;
    private FastqParser parser;
    private long entriesProcessed = 0, entriesSurvived = 0;
    private long nucleotidesProcessed = 0, nucleotidesSurvived = 0;
    private long entriesUnknown = 0;
    private boolean barcodeSplittingMode = false;
    private boolean isInitialized = false;
    private boolean wantsToDie = false;

    public TrimmomaticProcess(FastQFile input, File workDir, ArrayList<Trimmer> modules, int phredOffset) {
        this.input = input;
        this.workDir = workDir;
        this.phredOffset = phredOffset;
        this.trimmers = modules;
    }

    public TrimmomaticProcess(FastQFile input, File workDir, int phredOffset) {
        this.input = input;
        this.workDir = workDir;
        this.phredOffset = phredOffset;
    }

    private void initialize() throws IOException, Exception {

        if (trimmers.size() < 1) {
            throw new Exception("No trimmers added; cancelling.");
        }

        this.outputSerializers = new HashMap<String, FastqSerializer>();

        File outputDir = new File(workDir, "input");
        this.trimLog = new File(outputDir, input.getName() + ".trimlog");

        // the barcode splitter, if chosen, will always be the first "trimmer" module
        if (this.trimmers.get(0) instanceof BarcodeSplitter) {
            barcodeSplittingMode = true;

            FastqSerializer serializer = new FastqSerializer();
            File out = new File(outputDir, "UNKNOWN_" + input.getName().replaceAll("(?i)^(.+)\\.(gz|bz2|zip)$", "$1"));
            serializer.open(new PrintStream(out));
            serializer.setInputFile(out);
            outputSerializers.put("UNKNOWN", serializer);

            // no barcode splitting    
        } else {
            FastqSerializer serializer = new FastqSerializer();
            File out;
            if (input.getName().startsWith("TRIMMED_")) {
                out = new File(outputDir, input.getName().replaceAll("(?i)^(.+)\\.(gz|bz2|zip)$", "$1"));
            } else {
                out = new File(outputDir, "TRIMMED_" + input.getName().replaceAll("(?i)^(.+)\\.(gz|bz2|zip)$", "$1"));
            }
            serializer.open(new PrintStream(out));
            serializer.setInputFile(out);
            outputSerializers.put("default", serializer);
        }
        isInitialized = true;
    }

    public synchronized void cancel() {
        this.wantsToDie = true;
    }

    public void addTrimmer(Trimmer t) {
        this.trimmers.add(t);
    }

    public void process() throws IOException, Exception {

        if (!isInitialized) {
            initialize();
        }

        parser = new FastqParser(phredOffset);
        parser.parse(input);

        PrintStream trimLogStream = null;
        if (trimLog != null) {
            trimLogStream = new PrintStream(trimLog);
        }

        FastqRecord recs[] = new FastqRecord[1];
        FastqRecord originalRecs[] = new FastqRecord[1];

        while (parser.hasNext()) {

            if (wantsToDie) {
                break;
            }

            originalRecs[0] = recs[0] = parser.next();
            entriesProcessed++;
            nucleotidesProcessed += recs[0].getSequence().length();

            for (Trimmer tr : trimmers) {
                recs = tr.processRecords(recs);
            }

            if (recs[0] != null) {
                if (barcodeSplittingMode) {
                    /* create output files only if 
                     * there are matching sequences 
                     * in the input file
                     */
                    if (outputSerializers.containsKey(recs[0].getBarcodeLabel())) {
                        outputSerializers.get(recs[0].getBarcodeLabel()).writeRecord(recs[0]);
                    } else {
                        FastqSerializer serializer = new FastqSerializer();
                        File outputDir = new File(workDir, "input");
                        File out = new File(outputDir, recs[0].getBarcodeLabel() + "_" + input.getName().replaceAll("(?i)^(.+)\\.(gz|bz2|zip)$", "$1"));
                        serializer.open(new PrintStream(out));
                        serializer.setInputFile(out);
                        serializer.writeRecord(recs[0]);
                        outputSerializers.put(recs[0].getBarcodeLabel(), serializer);
                    }

                    if (recs[0].getBarcodeLabel().equals("UNKNOWN")) {
                        entriesUnknown++;
                    }

                } else {
                    outputSerializers.get("default").writeRecord(recs[0]);
                }
                entriesSurvived++;
                nucleotidesSurvived += recs[0].getSequence().length();
            } else {
                // entry clipped into nothingness                
            }

            if (trimLogStream != null) {
                for (int i = 0; i < originalRecs.length; i++) {
                    int length = 0;
                    int startPos = 0;
                    int endPos = 0;
                    int trimTail = 0;

                    if (recs[i] != null) {
                        length = recs[i].getSequence().length();
                        startPos = recs[i].getHeadPos();
                        endPos = length + startPos;
                        trimTail = originalRecs[i].getSequence().length() - endPos;
                    }
                    trimLogStream.printf("%s %d %d %d %d\n", originalRecs[i].getName(), length, startPos, endPos, trimTail);
                }
            }
        }


        // close all serializers
        for (FastqSerializer ser : outputSerializers.values()) {
            ser.close();
        }

        if (trimLogStream != null) {
            trimLogStream.close();
        }
    }

    public int getProgress() {
        if (parser == null) {
            return 0;
        } else {
            return parser.getProgress();
        }
    }

    public synchronized long getEntriesProcessed() {
        return entriesProcessed;
    }

    public synchronized long getEntriesSurvived() {
        return entriesSurvived;
    }

    public synchronized long getNucleotidesProcessed() {
        return nucleotidesProcessed;
    }

    public synchronized long getNucleotidesSurvived() {
        return nucleotidesSurvived;
    }

    public synchronized ArrayList<File> getOutputFiles() {
        ArrayList<File> files = new ArrayList<File>();
        for (FastqSerializer ser : outputSerializers.values()) {
            files.add(ser.getInputFile());
        }
        return files;
    }

    public synchronized float getPercentageUnknown() {
        if (!barcodeSplittingMode) {
            return 0.0f;
        }
        return (float) ((float) entriesUnknown / (float) entriesProcessed) * 100;
    }
}
