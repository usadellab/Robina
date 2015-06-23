
import de.mpimp.golm.robin.RobinConstants;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import junit.framework.TestCase;
import org.biojava3.genome.parsers.gff.Feature;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.biojava3.genome.parsers.gff.Location;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marc
 */
public class TestGFF3Reader extends TestCase {

    public void testReader_2() throws IOException, Exception {
        GFF3Reader reader = new GFF3Reader();
        FeatureList annotation = new FeatureList();
        //FeatureList anno = reader.read("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_GFF3_genes.gff");
        FeatureList anno = reader.read("/Users/marc/Desktop/Rattus_norvegicus.RGSC3.4.67.gtf");
//        for (String featureType : RobinConstants.EXTRACT_GFF3_FEATURES_LIST) {
//            annotation.add(anno.selectByType(featureType));
//        }
//        Iterator<FeatureI> iter = anno.iterator();
//        
//        int c = 0;
//        
//        HashSet<String> seenTypes = new HashSet<String>();
//       
//        
//        while (iter.hasNext()) {
//            FeatureI f = iter.next();
//            seenTypes.add(f.type());
//            
//            if (! (f.hasAttribute("gene_id") && f.hasAttribute("transcript_id")) ) {
//                System.out.println("entry missing gene_id or transcript_id.\n"+f);
//            }
//            
////            System.out.println("seqname: "+f.seqname());
////            System.out.println("gene_name: "+f.getAttribute("gene_name"));
////            System.out.println("type: "+f.type());
////            System.out.println("location: "+f.location().plus());
////            System.out.println("complete: "+f+"\n\n");
////            c++;
//           // if (c > 20) break;
//        }
//        
//        for (String s : seenTypes) {
//            System.out.println(s);
//        }
        
        

        long time1 = System.currentTimeMillis();
        FeatureList overlapList = anno.selectOverlapping("X", new Location(130000, 150000), true);
        long time2 = System.currentTimeMillis();
        for (FeatureI feature : overlapList) {
            System.out.println(feature);
        }
        System.out.println("q1 time: " + (time2 - time1));

//        for (int i = 1; i <= 10; i++) {
//            time1 = System.currentTimeMillis();
//            overlapList = annotation.selectOverlapping("2", new Location(0, 100000), true);
//            time2 = System.currentTimeMillis();
//            for (FeatureI feature : overlapList) {
//                System.out.println(feature);
//            }
//            System.out.println("q2 time: " + (time2 - time1));
//        }
    }

    public void XtestReader() throws IOException, Exception {
        GFF3Reader reader = new GFF3Reader();

        System.out.println("start reading GFF");
        FeatureList list = reader.read("/Users/marc/Desktop/projects/RobinLab/RobiNA/TAIR10_GFF3_genes.gff");
        System.out.println("done reading GFF");

        FeatureList overlapList = list.selectOverlapping("Chr1", new Location(500, 600), true);

        for (FeatureI feature : overlapList) {
            System.out.println("chromosome: " + feature.seqname()
                    + " start:" + feature.location().bioStart()
                    + " end:" + feature.location().bioEnd()
                    + " type:" + feature.type()
                    + " ID:" + (feature.hasAttribute("ID") ? feature.getAttribute("ID") : "none"));
        }






//        FeatureList list = reader.read("/Users/marc/sequences/annotation/melon/melon_genome/TAIR10_GFF3_genes.gff");

//        Location l = new Location(3631, 3671);
//        
//        Feature testfeat = new Feature(
//                "Chr1",
//                "",
//                "gene",
//                l,
//                Double.NaN,
//                1,
//                "");

//        Iterator iter = list.iterator();
//        while (iter.hasNext()) {
//            FeatureI feature = (FeatureI) iter.next();
//            if (!feature.type().equals("gene")) {
//                continue;
//            }
//
//            if (feature.seqname().equals(testfeat.seqname()) && 
//                    feature.location().plus().overlaps( testfeat.location().plus() ) &&
//                    feature.location().plus().isSameStrand( testfeat.location().plus() )) {
//
//                System.out.println("chromosome: " + feature.seqname()
//                        + " start:" + feature.location().bioStart()
//                        + " end:" + feature.location().bioEnd()
//                        + " type:" + feature.type()
//                        + " ID:" + (feature.hasAttribute("ID") ? feature.getAttribute("ID") : "none"));
//            }
//        }
    }
}
