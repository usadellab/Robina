
import de.mpimp.golm.robin.misc.Seq4Packing;
import de.mpimp.golm.robin.misc.Seq4Packing.PaganUtilException;
import de.mpimp.golm.robin.rnaseq.qual.Kmer;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestSeqPacker extends TestCase {
    
    public void xtestPacker() throws PaganUtilException {
        String seq = "AGCTAAGANAGN";        
        byte[] arr = Seq4Packing.pack(seq.getBytes());
        
        System.out.println("seq "+seq+" unpacked "+Seq4Packing.unpack(arr)+" packed2string " + new String(arr));
        
        
        // ... also test Kmer
        Kmer k1 = new Kmer("AGCATTATGAG");
        Kmer k2 = new Kmer("AGtATTATGAg");
        
        System.out.println(k1.equals(k2));
        System.out.println("k1 hash="+k1.hashCode()+" k2 hash="+k2.hashCode());
        
    }
    
    public void testRegex() {
        String test = "/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq.gz.bimbo.fastq.bz2";
        
        System.out.println("vorher: <"+test+">");
        test = test.replaceAll("^(.+)\\.(gz|bz2)$", "$1");
        
        System.out.println("nachher: <"+test+">");
        
        
    }
    
}
