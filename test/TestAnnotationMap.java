
import de.mpimp.golm.robin.annotation.GFF3.AnnotationMap;
//import de.mpimp.golm.robin.annotation.GFF3.AnnotationMap_test;
import de.mpimp.golm.robin.annotation.GFF3.GenomicLocation;
import java.io.File;
import java.io.IOException;
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
public class TestAnnotationMap extends TestCase {
    
    public void testAnnoMap() throws IOException {
        
        //File anno = new File("/Users/marc/sequences/annotation/melon/melon_genome/TAIR10_GFF3_genes.gff");
        File anno = new File("/Users/marc/Desktop/TAIR10_GFF3_genes.gff");
        AnnotationMap map = new AnnotationMap(anno, new ArrayList<String>() { {
                add("exon");
                add("pseudogenic_exon");
            }
        });
        
//        GenomicLocation testLoc = new GenomicLocation("testFull", 5928, 8737); //AT1G01020
        GenomicLocation simLoc = new GenomicLocation("testInternal", 6100, 6145);
        GenomicLocation startOverLoc = new GenomicLocation("testStartOver", 5900, 5945);
        GenomicLocation endOverLoc = new GenomicLocation("testEndOver", 8700, 8745);
        
        GenomicLocation testLoc = new GenomicLocation("testFull", 885623, 887956); //AT1G03550
        
        
        
        System.out.println("full: " + map.getLocationsOverlappingWith("Chr1", testLoc).get(0).getIdentifier());
        System.out.println("internal: " + map.getLocationsOverlappingWith("Chr1", simLoc).get(0).getIdentifier());
        System.out.println("start: " + map.getLocationsOverlappingWith("Chr1", startOverLoc).get(0).getIdentifier());
        System.out.println("end: " + map.getLocationsOverlappingWith("Chr1", endOverLoc).get(0).getIdentifier());
        
        map.writeLengthTable(new File("/Users/marc/Desktop/test.txt"));
        
    }
    
}
