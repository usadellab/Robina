

import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQParser;
import de.mpimp.golm.robin.rnaseq.parser.FastQParserThread;
import de.mpimp.golm.robin.rnaseq.parser.FastQParserWorker;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBaseCallFrequencies;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBasicStats;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqConsecutiveHomopolymers;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerBaseQuality;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerSequenceQuality;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartPanel;
import org.openide.util.Exceptions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestFastQParser {
    
    public static void testParser() throws IOException, FileNotFoundException, Exception {
//        File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/s_1_1_sequence.txt");
//        File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq.gz");
//        File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq");
        FastQFile referenceFile = new FastQFile("/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq");
        //File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq.bz2");
//        File[] referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/samples/").listFiles(new FilenameFilter () {
//
//            public boolean accept(File dir, String name) {
//                if (name.endsWith("fastq")) return true;
//                return false;
//            }
//        });      
        
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(referenceFile));        
        
        FastQParser parser = new FastQParser(referenceFile);
//        parser.setScanDepth(500000);
        parser.setScanMode(true);
        
        final RNASeqBasicStats basic = new RNASeqBasicStats(parser);
        parser.registerQualCheckMethod(basic);
        
        long start = System.currentTimeMillis();        
        parser.parseNow();
        long end = System.currentTimeMillis();
        System.out.println("parse time = "+(double)(end - start)/1000+"s");
        System.out.println("file length="+referenceFile.length());
        
        for (FastQEntry e : parser.getEntries()) {
            System.out.println(e.toString());
        }
        
        if (!parser.inputHasSameVersions()) {
            System.out.println("WARNING: Different versions of input files!");
        }
      
        
        System.out.println("total entries in file: "+parser.getNumberOfEntries());
}   
    
    public static void main(String[] args) throws InterruptedException, IOException, FileNotFoundException, Exception {
        
//        testParser();
//        System.exit(0);
        
        FastQFile referenceFile = new FastQFile("/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq.gz");
//        File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq");
//        File referenceFile = new File("/Users/marc/Desktop/sample4000000.fastq");
        
        final FastQParser parser = new FastQParser(referenceFile);        
        parser.setScanMode(true);
//        parser.setScanDepth(100000);
//        
//        final RNASeqConsecutiveHomopolymers homoCheck = new RNASeqConsecutiveHomopolymers();
//        final RNASeqBaseCallFrequencies calls = new RNASeqBaseCallFrequencies();
//        final RNASeqPerBaseQuality quals = new RNASeqPerBaseQuality(VERSION.ILLUMINA_1_5);
//        final RNASeqPerSequenceQuality seq_quals = new RNASeqPerSequenceQuality(VERSION.ILLUMINA_1_5);
        final RNASeqBasicStats basic = new RNASeqBasicStats(parser);
//        final RNASeqKmerFrequency kmers = new RNASeqKmerFrequency(5,5,1000000);
        
//        parser.registerQualCheckMethod(homoCheck);
//        parser.registerQualCheckMethod(calls);
//        parser.registerQualCheckMethod(quals);
//        parser.registerQualCheckMethod(seq_quals);
//        parser.registerQualCheckMethod(kmers);
        parser.registerQualCheckMethod(basic);
        
        final FastQParserThread parseThread = new FastQParserThread(parser);        
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        
        ActionListener progressListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {    
                    if (!executor.isTerminated())
                        System.out.println("progress = "+ parser.getProgress());                    
                    
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
//                System.out.println("timer fired");
            }
        };
        
        Timer processTimer = new Timer(100, progressListener);
        
        Future result = null;        
        try {
            result = executor.submit(parseThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        processTimer.start();    
        executor.shutdown();
        
        while (!result.isDone()) {            
            Thread.sleep(500);
            System.out.println("waiting...future is "+result.isDone());            
        }
        processTimer.stop();   
        
//        for (Integer i : parser.getSeqLengths().keySet()) {
//            System.out.println("len= "+i+" count="+parser.getSeqLengths().get(i));
//        }
////        
//        System.out.println("total recorded unique Kmers="+kmers.getTotalRecordedUniqueKmers());
//        System.out.println("total Kmer counts="+kmers.getTotalKmerCounts());
//        kmers.getReport();
        basic.getReport();
//        System.exit(0);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("test");
                frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
                
                Dimension size1 = new Dimension(1000,200);
//                
//                ChartPanel seq_q_panel = (ChartPanel) seq_quals.getResultChart(size1);
//                seq_q_panel.setVisible(true);
//                frame.add(seq_q_panel);
//                
//                ChartPanel callspanel = (ChartPanel) calls.getResultChart(size1);
//                callspanel.setVisible(true);
//                frame.add(callspanel);
//                
//                ChartPanel homopanel = (ChartPanel) homoCheck.getResultChart(size1);
//                homopanel.setVisible(true);
//                frame.add(homopanel);
                
                JPanel qualpanel = basic.getResultPanel();
                qualpanel.setVisible(true);
                frame.add(qualpanel);
                
//                ChartPanel kmerpanel = (ChartPanel) kmers.getResultChart(new Dimension(1000, 350));
//                kmerpanel.setVisible(true);
//                frame.add(kmerpanel);
               
//                
//                int i = 0;
//                for (RNASeqQualityCheck q : parser.getQualChecks()) {
//                    try {
//                        ChartUtils.saveChartAsPNG(new File("/Users/marc/Desktop/pngtest"+i+".png"), 
//                                ((ChartPanel)q.getResultChart(new Dimension(200, 200))).getChart() , 200, 200);
//                    } catch (IOException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                    i++;
//                }
//                
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
        System.out.println("final progress: "+parser.getProgress());
    }
    
    public static void xmain(String[] args) throws InterruptedException, IOException, FileNotFoundException, Exception {        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("test");
                frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));                
                
                //File referenceFile = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample4000000.fastq");
                FastQFile referenceFile = new FastQFile("/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq");

                final FastQParser parser = new FastQParser(referenceFile);        
                parser.setScanMode(true);
                parser.setScanDepth(50);

                final RNASeqConsecutiveHomopolymers homoCheck = new RNASeqConsecutiveHomopolymers();
                final RNASeqBaseCallFrequencies calls = new RNASeqBaseCallFrequencies();
                final RNASeqPerBaseQuality quals = new RNASeqPerBaseQuality(VERSION.ILLUMINA_1_5);
                final RNASeqPerSequenceQuality seq_quals = new RNASeqPerSequenceQuality(VERSION.ILLUMINA_1_5);

                parser.registerQualCheckMethod(homoCheck);
                parser.registerQualCheckMethod(calls);
                parser.registerQualCheckMethod(quals);
                parser.registerQualCheckMethod(seq_quals);

                FastQParserWorker worker = new FastQParserWorker(parser);        
//                worker.execute();
//                while (!worker.isDone()) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                    System.out.println("running... "+worker.getParserProgress());                    
//                }
                
                ExecutorService exe = Executors.newFixedThreadPool(1);
                exe.submit(worker);

               
                Dimension size1 = new Dimension(1000,200);
                
                ChartPanel seq_q_panel = (ChartPanel) seq_quals.getResultChart(size1);
                seq_q_panel.setVisible(true);
                frame.add(seq_q_panel);
                
                ChartPanel callspanel = (ChartPanel) calls.getResultChart(size1);
                callspanel.setVisible(true);
                frame.add(callspanel);
                
                ChartPanel homopanel = (ChartPanel) homoCheck.getResultChart(size1);
                homopanel.setVisible(true);
                frame.add(homopanel);
                
                ChartPanel qualpanel = (ChartPanel) quals.getResultChart(new Dimension(1000, 350));
                qualpanel.setVisible(true);
                frame.add(qualpanel);
                
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }     
}
