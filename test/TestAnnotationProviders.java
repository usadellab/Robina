
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.robin.annotation.GTF.GTFAnnotationProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import junit.framework.TestCase;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.Location;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marc
 */
public class TestAnnotationProviders extends TestCase {

    public void testGFF3Provider() {
        GFF3AnnotationProvider provider = new GFF3AnnotationProvider(
                //new File("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_GFF3_genes.gff"),
                new File("/Users/marc/Desktop/people/beaumont_guillaume/MultiFasta_with_GO.gff"),
               //new File("/Users/marc/Desktop/people/beaumont_guillaume/MultiFasta_with_GO_corrected.gff"),
               //new File("/Users/marc/Desktop/people/beaumont_guillaume/MultiFasta_with_first_GO_not_filled.gff"),
                
                Arrays.asList(RobinConstants.EXTRACT_GFF3_FEATURES_LIST));
        
        

        Location loc = new Location(10, 10000);
        long time1 = System.currentTimeMillis();
        List<FeatureI> hits = provider.getOverlappingFeatures("CMER", loc);
        long time2 = System.currentTimeMillis();

        for (FeatureI hit : hits) {
            System.out.println(hit);
        }

        System.out.println("q1 time: " + (time2 - time1));

        for (int i = 1; i <= 10; i++) {

            loc = new Location(0, 10000);
            time1 = System.currentTimeMillis();
            hits = provider.getOverlappingFeatures("CMER", loc);
            time2 = System.currentTimeMillis();

            for (FeatureI hit : hits) {
                System.out.println(hit);
            }

            System.out.println("q2 time: " + (time2 - time1));
        }
    }
    
    
    
    public void xtestGTFProvider() {
        GTFAnnotationProvider provider = new GTFAnnotationProvider(
                //new File("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_GFF3_genes.gff"));
                new File("/Users/marc/Desktop/Rattus_norvegicus.RGSC3.4.67.gtf"));

        Location loc = new Location(130000, 150000);
        long time1 = System.currentTimeMillis();
        List<FeatureI> hits = provider.getOverlappingFeatures("X", loc);
        long time2 = System.currentTimeMillis();

        for (FeatureI hit : hits) {
            System.out.println(hit);
        }

        System.out.println("q1 time: " + (time2 - time1));

//        for (int i = 1; i <= 10; i++) {
//
//            loc = new Location(130000, 150000);
//            time1 = System.currentTimeMillis();
//            hits = provider.getOverlappingFeatures("X", loc);
//            time2 = System.currentTimeMillis();
//
//            for (FeatureI hit : hits) {
//                System.out.println(hit);
//            }
//
//            System.out.println("q2 time: " + (time2 - time1));
//        }
    }
}
