
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
import de.mpimp.golm.robin.annotation.ResultAnnotator;
import de.rzpd.mapman.data.TextMappingProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marc
 */
public class TestRobinResultsAnnotator extends TestCase {

    public void xtestAnnotator() throws FileNotFoundException, Exception {
        File resfile = new File("/Volumes/Backup/patrick_daten/Rohdaten/analyse_patrick_results_4_mapman_import_050613.txt");
        FileReader reader = new FileReader(new File("/Users/marc/sequences/annotation/arabidopsis/TAIR10/Ath_AFFY_STv1.1_TRANSCRIPT_CLUSTER_TAIR10_LOCUS.mapping.txt"));

        TextMappingProvider mapping = new TextMappingProvider("test", reader);
        ResultAnnotator anno = new ResultAnnotator(resfile, mapping);

        System.out.println("processed lines: " + anno.getLineCounter());
        System.out.println("percent not mapped: "
                + anno.getPercentNotMappedFormatted() + "% ("
                + anno.getNotMappedCounter() + "/"
                + anno.getLineCounter() + ")");

        System.out.println("percent mapped: "
                + anno.getPercentMappedFormatted() + "% ("
                + anno.getMappedCounter() + "/"
                + anno.getLineCounter() + ")");

    }

    public void testAnnotatorBatch() throws FileNotFoundException, Exception {
        FileReader reader = new FileReader(new File("/Users/marc/Desktop/Ath_AFFY_ATH1_TAIR10_Aug2012.txt"));

        File resDir = new File("/Users/marc/Desktop/people/Myriam_SnRK1/TPS_KIN10_intersections");
        File[] resFiles = resDir.listFiles();

        TextMappingProvider mapping = new TextMappingProvider("test", reader);

        for (File resFile : resFiles) {
            ResultAnnotator anno = new ResultAnnotator(resFile, mapping);

            System.out.println("processed lines: " + anno.getLineCounter());
            System.out.println("percent not mapped: "
                    + anno.getPercentNotMappedFormatted() + "% ("
                    + anno.getNotMappedCounter() + "/"
                    + anno.getLineCounter() + ")");

            System.out.println("percent mapped: "
                    + anno.getPercentMappedFormatted() + "% ("
                    + anno.getMappedCounter() + "/"
                    + anno.getLineCounter() + ")");
        }

    }

    public void XtestAnnoDialog() throws Exception {
        File resources = new File("/Users/marc/data/mappings/mappings/");
        File resultFile = new File("/Users/marc/TEST/TEST_results.txt");
        ResultAnnotationDialog annoDialog = new ResultAnnotationDialog(null, true, resources, resultFile);
        annoDialog.setVisible(true);
    }
}
