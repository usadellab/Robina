
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqPDFSummaryGenerator;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.io.File;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestPDFGenerator extends TestCase {
    
    
    public static void testGenerator () throws Exception {
        RNASeqDataModel model = new RNASeqDataModel();
        model.load(new File("/Users/marc/Desktop/TESTP/source/TESTP_data.xml"));
        model.setProjectDir(new File("/Users/marc/Desktop/TESTP"));
        RNASeqPDFSummaryGenerator pdfGen = new RNASeqPDFSummaryGenerator(model, null);
    }
    
}
