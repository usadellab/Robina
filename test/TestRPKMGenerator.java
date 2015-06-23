
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.rnaseq.RPKM.RNASeqRPKMGenerator;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestRPKMGenerator extends TestCase {
    
    public static void testLengthTable() throws IOException {
        GFF3Reader reader = new GFF3Reader();
        FeatureList all_annotation =  reader.read("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_GFF3_genes.gff");
        //FeatureList all_annotation =  reader.read("/Users/marc/Desktop/TAIR10_GFF3_genes.gff");
        
        RNASeqDataModel model = new RNASeqDataModel();
        model.setProjectDir(new File("/Users/marc/Desktop/TESTPROJECT"));
        
        RNASeqRPKMGenerator generator = new RNASeqRPKMGenerator(null, all_annotation, model);
        //generator.generateLengthTable(); 
        //generator.computeAmbiguousRPKMCountsTable();
    }
}
