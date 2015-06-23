
import de.mpimp.golm.robin.rnaseq.parser.RNASeqReferenceParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import junit.framework.TestCase;
import org.gabipd.parser.ParserException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestRNASeqParser  {
    
    public static void main(String[] args) throws ParserException, FileNotFoundException {
//        File referenceFile = new File("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_chr_all.fas");
//        File referenceFile = new File("/Users/marc/Desktop/NC_002944_mod.fasta");
        File referenceFile = new File("/Users/marc/Desktop/NC_002944.fasta.cleaned");
        RNASeqReferenceParser fastaParser = new RNASeqReferenceParser(new FileInputStream(referenceFile), 0);            
        fastaParser.parseNow();
        System.out.println("N50:"+fastaParser.getN50());
        System.out.println("entries:"+fastaParser.getNumberEntriesRead());
        System.out.println("nucleotides:"+fastaParser.getNumberSymbolsRead());
        System.out.println("N:"+fastaParser.getNcontent());
    }    
}
