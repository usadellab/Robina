
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
public class TestPLAYGROUND extends TestCase {
    
    public void testSomething() {
        System.out.println("arch="+System.getProperty("os.arch"));
        RNASeqDelegate del = new RNASeqDelegate(null);
        
        System.out.println("del says: "+del.getSysArchString());
    
    }
}
