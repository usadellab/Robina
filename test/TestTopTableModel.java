
import de.mpimp.golm.robin.GUI.RNASeq.TopTableModel;
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
public class TestTopTableModel extends TestCase {
    
    public void testModel() {
        TopTableModel model = new TopTableModel(new File("/Users/marc/Desktop/ALKJHGDKJHGF/detailed_results/full_table_hans-hhjwhehdjsjhehgqjheh_l.txt"));
        
        System.out.println("0,0 --> "+model.getValueAt(0, 0));
    }
}
