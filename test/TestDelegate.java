
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqDelegate;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestDelegate extends TestCase {
    
    public void testArch() {
        RNASeqDelegate del = new RNASeqDelegate(null);
        System.out.println("arch: "+del.getSysArchString());
    }    
}
