
import de.mpimp.golm.robin.RobinAppDelegate;
import java.io.IOException;
import java.net.URISyntaxException;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestAppDelegate extends TestCase {
    
    public void testDelegate() throws IOException, URISyntaxException {
        RobinAppDelegate.checkInstallationIntegrity();
    }
    
}
