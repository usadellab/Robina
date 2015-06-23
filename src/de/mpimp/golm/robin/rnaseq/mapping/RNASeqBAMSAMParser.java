/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import de.mpimp.golm.common.gui.CollapsibleInfoDialog;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.robin.data.RNASeqSample;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFrame;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMFormatException;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.Location;

/**
 *
 * @author marc
 */
public class RNASeqBAMSAMParser implements Runnable {

    protected HashMap<String, Integer> countsTable;
    private SAMFileReader reader;
    private long numberNonAligning = 0;
    private long validAlignments = 0;
    private long numberTotalReads = 0;
    private GFF3AnnotationProvider annotation;
    private RNASeqSample sample;
    private int badRecords;
    private AtomicInteger recordsRead;
    private ValidationStringency validationStringency;
    private ArrayList<Exception> exceptions;

    public RNASeqBAMSAMParser(RNASeqSample sample, GFF3AnnotationProvider anno, ValidationStringency stringency) {
        this.annotation = anno;
        this.sample = sample;
        this.validationStringency = stringency;
        this.recordsRead = new AtomicInteger(0);
        this.exceptions = new ArrayList<Exception>();
    }

    public void run() {
        badRecords = 0;
        for (File inFile : sample.getInputFiles()) {
            try {
                SAMFileReader.setDefaultValidationStringency(validationStringency);
                this.reader = new SAMFileReader(inFile);
                readData();
            } catch (Exception e) {
                SimpleLogger.getLogger(true).logException(e);
                exceptions.add(e);
            } finally {
                return;
            }
        }
    }

    private void readData() throws net.sf.samtools.SAMFormatException {
        countsTable = new HashMap<String, Integer>();
        SAMRecordIterator iter = reader.iterator();

        while (iter.hasNext()) {
            SAMRecord record = null;
            recordsRead.getAndIncrement();
            try {
                record = iter.next();

            } catch (SAMFormatException samex) {

                // Gag the logger  - otherwise it will spam the user with a flood of dialogs....
                SimpleLogger.getLogger(true).setLogLevel(SimpleLogger.GUILogLevel.NONE);
                SimpleLogger.getLogger(true).logException(samex);
                SimpleLogger.getLogger(true).setLogLevel(SimpleLogger.GUILogLevel.EXCEPTIONS_ONLY);
                badRecords++;
                if (badRecords >= RobinConstants.MAX_BAD_SAM_ENTRIES) {
//                    new SimpleErrorMessage(null, "Found more than " 
//                            + RobinConstants.MAX_BAD_SAM_ENTRIES 
//                            + " bad entries. Cancelling import.");
                    break;
                }
                throw samex;
                //continue;
            }

            numberTotalReads++;
            String hitRefName = record.getReferenceName();

            if (hitRefName.equals("*")) {
                numberNonAligning++;
                continue;
            }


            // if a GFF3 annotation is provided we work with genome reference
            // aligned data. We have to extract the name of the transcript annotated
            // at the alignment position
            if (annotation != null) {
                Location loc = new Location(record.getAlignmentStart(), record.getAlignmentEnd());
                List<FeatureI> hits = annotation.getOverlappingFeatures(hitRefName, loc);
                if (hits.size() > 1) {
                    // several genes annotated at same location - skip
                    continue;
                } else if (hits.size() < 1) {
                    // no annotation found at this position.
                    // ideally these alignments would be collected to 
                    // later generate a pile-up and from this predict
                    // possibly new transcribed areas. For now we just
                    // flag them as "no_annotation" for each chromosome/contig
                    hitRefName += "_no_annotation";
                }

                for (FeatureI feature : hits) {
                    // this should be the way exons are annotated.... 
                    // the code breaks it there is no Parent key
                    String ID = (feature.hasAttribute("Parent") ? feature.getAttribute("Parent") : null);
                    if (ID == null) {
                        ID = (feature.hasAttribute("ID") ? feature.getAttribute("ID") : null);
                    }

                    if (ID == null) {
                        new SimpleErrorMessage(null, "Could not extract ID from annotated feature:\n" + feature.toString() + "\n...skipping.");
                        continue;
                    }
                    hitRefName = ID;
                }
            }

            countsTable.put(hitRefName,
                    (countsTable.containsKey(hitRefName) ? countsTable.get(hitRefName) + 1 : 1));
            validAlignments++;
        }
        SimpleLogger.getLogger(true).logMessage("finished parsing BAM/SAM data for sample:" + sample.getSampleName() + "\n"
                + "number of bad records: " + badRecords);

        if (badRecords > recordsRead.get() * 0.5) {
            new SimpleErrorMessage(null, "More than 50% of the records of sample: \n"
                    + sample.getSampleName() + "\n"
                    + "were not readable due to format errors.");
        }

    }

    public ArrayList<Exception> getExceptions() {
        return exceptions;
    }

    public HashMap<String, Integer> getCountsTable() {
        return countsTable;
    }

    public long getNumberNonAligning() {
        return numberNonAligning;
    }

    public long getNumberTotalReads() {
        return numberTotalReads;
    }

    public long getValidAlignments() {
        return validAlignments;
    }

    public RNASeqSample getSample() {
        return sample;
    }

    public int getRecordsRead() {
        return recordsRead.get();
    }
}
