


import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQParser;
import de.mpimp.golm.robin.rnaseq.parser.FastQSampleParser;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBaseCallFrequencies;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerBaseQuality;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqConsecutiveHomopolymers;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerSequenceQuality;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestQualityCheckPlots {
    
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        //File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/s_1_1_sequence.txt");
        FastQFile referenceFile = new FastQFile("/Users/marc/Desktop/RobiNA_testset/SRR392118.fastq.sample.fastq");
//        File referenceFile = new File("/Users/marc/Desktop/sample4000000.fastq");
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
        //parser.setSampleSize(50000);
        parser.setScanMode(true);
        
//        FastQParser parser = new FastQParser(files);        
//        parser.setScanMode(true);
//        parser.setScanDepth(500);
        
//        final RNASeqConsecutiveHomopolymers homoCheck = new RNASeqConsecutiveHomopolymers();
        final RNASeqBaseCallFrequencies calls = new RNASeqBaseCallFrequencies();
//        final RNASeqPerBaseQuality quals = new RNASeqPerBaseQuality(VERSION.ILLUMINA_1_5);
//        final RNASeqPerSequenceQuality seq_quals = new RNASeqPerSequenceQuality(VERSION.ILLUMINA_1_5);
        
//        parser.registerQualCheckMethod(homoCheck);
        parser.registerQualCheckMethod(calls);
//        parser.registerQualCheckMethod(quals);
//        parser.registerQualCheckMethod(seq_quals);
        
        long start = System.currentTimeMillis();        
        parser.parseNow();
        long end = System.currentTimeMillis();
        System.out.println("parse time = "+(end - start)/1000+"s");
        
//        for (FastQEntry e : parser.getEntries()) {
//            System.out.println(e.toString());
//        }
        
        if (!parser.inputHasSameVersions()) {
            System.out.println("WARNING: Different versions of input files!");
            for (VERSION v : parser.getVersions()) {
                System.out.println(v);
            }
        }
        

        
//        seq_quals.getReport();
        //calls.getReport();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("test");
                frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
                
                Dimension size1 = new Dimension(1000,200);
                
//                ChartPanel seq_q_panel = (ChartPanel) seq_quals.getResultChart(size1);
//                seq_q_panel.setVisible(true);
//                frame.add(seq_q_panel);
                
                ChartPanel callspanel = (ChartPanel) calls.getResultChart(size1);
                callspanel.setVisible(true);
                frame.add(callspanel);
                
//                ChartPanel homopanel = (ChartPanel) homoCheck.getResultChart(size1);
//                homopanel.setVisible(true);
//                frame.add(homopanel);
                
//                ChartPanel qualpanel = (ChartPanel) quals.getResultChart(new Dimension(1000, 350));
//                qualpanel.setVisible(true);
//                frame.add(qualpanel);
                
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
        
    }
    
}
    

