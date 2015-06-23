
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.Timer;
import junit.framework.TestCase;
import org.usadellab.trimmomatic.fastq.trim.BarcodeSplitter;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TrimmomaticProcess;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TrimmomaticThread;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestTrimmomaticProcess extends TestCase {
    
    public void xtestTrimmoProc() throws IOException, Exception {
//        File inputfile = new File("/Users/marc/Desktop/codon/SRR223485.sample.fastq");
        File inputfile = new File("/Users/marc/Desktop/codon/SRR223485.smallsample.fastq");
        File work = new File("/Users/marc/Desktop/codon/SRR223485_TESTSPLIT/");
        work.mkdir();
        
        HashMap<String, String> barcodes = new HashMap<String, String>() {
            {
                put("ATGTCT", "ATGTCT");
                put("AAATT", "AAATT");
                put("AACCT", "AACCT");
                put("ACATAT", "ACATAT");
                put("AGAGTT", "AGAGTT");
                put("AGGGT", "AGGGT");
                put("CACGAT", "CACGAT");
                put("CCACT", "CCACT");
                put("CCGGT", "CCGGT");
                put("CCTTT", "CCTTT");
                put("CGTCTT", "CGTCTT");
                put("CTATGT", "CTATGT");
                put("GCTAT", "GCTAT");
                put("GGATT", "GGATT");
                put("GGCCT", "GGCCT");
                put("GGTGT", "GGTGT");
                put("GTCAGT", "GTCAGT");
                put("GTGCAT", "GTGCAT");
                put("TACACT", "TACACT");
                put("TCCCT", "TCCCT");
                put("TGAAT", "TGAAT");
                put("TGTGCT", "TGTGCT");
                put("TTGGT", "TTGGT");
                put("TTTAT", "TTTAT");
            }
        };
        
        BarcodeSplitter splitter = new BarcodeSplitter(barcodes, 1, true);
//        
//        final TrimmomaticProcess tmprocess = new TrimmomaticProcess(inputfile, work, QualityEncoding.VERSION.ILLUMINA_1_8.getOffset());
//        tmprocess.addTrimmer(splitter);
//        
//        Timer t = new Timer(100, new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("progress = "+tmprocess.getProgress());
//            }
//        } );
//        
//        t.start();
//        tmprocess.process();      
        
    }    
    
    public void testTrimmoThread() throws InterruptedException {
        
        ExecutorService exe = Executors.newFixedThreadPool(1);
//        File inputfile = new File("/Users/marc/Desktop/codon/SRR223485.sample.fastq");
        File inputfile = new File("/Users/marc/Desktop/codon/SRR223485.smallsample.fastq");
        File work = new File("/Users/marc/Desktop/codon/SRR223485_TESTSPLIT/");
        work.mkdir();
        
        HashMap<String, String> barcodes = new HashMap<String, String>() {
            {
                put("ATGTCT", "ATGTCT");
                put("AAATT", "AAATT");
                put("AACCT", "AACCT");
                put("ACATAT", "ACATAT");
                put("AGAGTT", "AGAGTT");
                put("AGGGT", "AGGGT");
                put("CACGAT", "CACGAT");
                put("CCACT", "CCACT");
                put("CCGGT", "CCGGT");
                put("CCTTT", "CCTTT");
                put("CGTCTT", "CGTCTT");
                put("CTATGT", "CTATGT");
                put("GCTAT", "GCTAT");
                put("GGATT", "GGATT");
                put("GGCCT", "GGCCT");
                put("GGTGT", "GGTGT");
                put("GTCAGT", "GTCAGT");
                put("GTGCAT", "GTGCAT");
                put("TACACT", "TACACT");
                put("TCCCT", "TCCCT");
                put("TGAAT", "TGAAT");
                put("TGTGCT", "TGTGCT");
                put("TTGGT", "TTGGT");
                put("TTTAT", "TTTAT");
            }
        };
        
        BarcodeSplitter splitter = new BarcodeSplitter(barcodes, 1, true);
        
//        final TrimmomaticProcess tmprocess = new TrimmomaticProcess(inputfile, work, QualityEncoding.VERSION.ILLUMINA_1_8.getOffset());
//        tmprocess.addTrimmer(splitter);
//        
//        TrimmomaticThread thread = new TrimmomaticThread(tmprocess, null);
//        Future fut = exe.submit(thread);
//        
//        while (!fut.isDone()) {
//            System.out.println("progress: " + thread.getProgress() + "%");
//            Thread.sleep(100);
//        }
    
    }
}
