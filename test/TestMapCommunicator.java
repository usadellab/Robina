

import de.mpimp.golm.robin.IPC.MapCommunicator;
import de.mpimp.golm.robin.misc.RobinUtilities;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestMapCommunicator extends TestCase {

    public void testFindFiles() throws IOException {
        //MapCommunicator.findMapMan();
        //File path = Utilities.findFileRecursively("2818.pdf", new File("/Users/marc"));
        //System.out.println("->"+path.getCanonicalPath());
        Properties testProps = new Properties();
        testProps.setProperty("chipID", "hans");
        MapCommunicator.postExperimentToMapMan(new File("/Users/marc/Desktop"), testProps);
    }

}
