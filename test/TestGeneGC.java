
import java.io.File;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import matrix.GeneGC;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marc
 */
public class TestGeneGC extends TestCase {

    public void testCalc() throws IOException, Exception {
        GeneGC geneGC = new GeneGC();
        Map<String, GeneGC.Gene> geneMap = geneGC.calc(
                new File("/Users/marc/Desktop/ROBINTEST1/TAIR10_GFF3_genes.gff"),
                //new File("/Users/marc/Desktop/TESTDATA/ROBINA/TAIR10_GFF3_genes_mod.gff"), // removed the "Chr" 
                new File("/Users/marc/Desktop/ROBINTEST1/TAIR10_chr_all.fas"));
                

        if (geneMap != null) {
            for (Map.Entry<String, GeneGC.Gene> entry : geneMap.entrySet()) {
                GeneGC.Gene gene = entry.getValue();
                System.out.println(entry.getKey() + "\t" + gene.getTotalLength() + "\t" + gene.getGcRatio() + "\n");
            }            
        } else {
            System.out.println("barf.");
        }
    }
}
