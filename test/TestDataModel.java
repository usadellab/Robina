
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
public class TestDataModel extends TestCase {
    
    public void testModel() throws Exception {
        RNASeqDataModel model = new RNASeqDataModel();
        model.load(new File("/Users/marc/Desktop/T9_data.xml"));
        
        System.out.println("loaded");
    }
    
}
