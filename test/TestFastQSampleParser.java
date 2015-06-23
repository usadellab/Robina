
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQSampleParser;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBaseCallFrequencies;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerBaseQuality;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqConsecutiveHomopolymers;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqKmerFrequency;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import junit.framework.TestCase;
import org.jfree.chart.ChartPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestFastQSampleParser extends TestCase {
        
    public void testParser() throws IOException, FileNotFoundException, Exception {
        FastQFile referenceFile = new FastQFile("/Volumes/Backup/SOLEXA_PLAYGROUND/s_1_1_sequence.txt");
//        File referenceFile = new File("/Users/marc/Desktop/sample4000000.fastq");
//        File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq");
        //File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/samples/reads_lane4.fq.gz.sample.fastq");
//        File[] referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/samples/").listFiles(new FilenameFilter () {
//
//            public boolean accept(File dir, String name) {
//                if (name.endsWith("fastq")) return true;
//                return false;
//            }
//        });      
        
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(referenceFile));        
        
        FastQSampleParser parser = new FastQSampleParser(referenceFile);
        parser.setSampleSize(100);
        parser.setScanMode(true);
        
        RNASeqConsecutiveHomopolymers homoCheck = new RNASeqConsecutiveHomopolymers();
        RNASeqBaseCallFrequencies calls = new RNASeqBaseCallFrequencies();
        RNASeqPerBaseQuality quals = new RNASeqPerBaseQuality(VERSION.ILLUMINA_1_5);
        RNASeqKmerFrequency kmers = new RNASeqKmerFrequency(5,5, 1000000 );
        
        parser.registerQualCheckMethod(homoCheck);
        parser.registerQualCheckMethod(calls);
        parser.registerQualCheckMethod(quals);
        parser.registerQualCheckMethod(kmers);
        
        long start = System.currentTimeMillis();        
        parser.parseNow();
        long end = System.currentTimeMillis();
        System.out.println("parse time = "+(double)(end - start)/1000+"s");
        
//        for (FastQEntry e : parser.getEntries()) {
//            System.out.println(e.toString());
//        }
        
        if (!parser.inputHasSameVersions()) {
            System.out.println("WARNING: Different versions of input files!");
            for (VERSION v : parser.getVersions()) {
                System.out.println(v);
            }
        }
        
        System.out.println("read a total of "+parser.getNumberOfSampledEntries()+" entries");
   
        
//        System.out.println("Homopolymers:");
//        homoCheck.getReport();
//        System.out.println("\ncalls freqs:");
//        calls.getReport();
//        System.out.println("Quality means:");
//        quals.getReport();
        
        System.out.println("total entries in file: "+parser.getNumberOfEntries());
        System.out.println("total recorded unique Kmers="+kmers.getTotalRecordedUniqueKmers());
        System.out.println("total Kmer counts="+kmers.getTotalKmerCounts());
//        kmers.getReport();
        
        
    }
    
}
    
