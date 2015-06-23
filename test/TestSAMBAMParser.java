
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBAMSAMParser;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestSAMBAMParser extends TestCase {
    
    public void testParser() throws FileNotFoundException {
        
        ArrayList<FastQFile> files = new ArrayList<FastQFile>() {
            {
                add(new FastQFile("/Users/marc/Desktop/samfiles/TRIMMED_NG-5335_1_T5K1TrB.fastq.sample.fastq.sam"));
                add(new FastQFile("/Users/marc/Desktop/samfiles/TRIMMED_NG-5335_1_T5K1TrL.fastq.sample.fastq.sam"));
                add(new FastQFile("/Users/marc/Desktop/samfiles/TRIMMED_NG-5335_1_T5K1TrM.fastq.sample.fastq.sam"));
            }
        };
        
//        RNASeqBAMSAMParser parser = new RNASeqBAMSAMParser(
//                new File("/Users/marc/Desktop/samfiles/TRIMMED_NG-5335_1_T5K1TrB.fastq.sample.fastq.sam"));
        
//        RNASeqBAMSAMParser parser = new RNASeqBAMSAMParser(files);
//                
//        System.out.println("reads aligning: "+parser.getValidAlignments());
//        System.out.println("reads not aligning: "+parser.getNumberNonAligning());
//        System.out.println("reads total: "+parser.getNumberTotalReads());
//        
//        
//        
//        for (String key : parser.getCountsTable().keySet()) {
//            System.out.println(key+"\t"+parser.getCountsTable().get(key));
//        }
        
        
    }
}
