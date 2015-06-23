/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import de.mpimp.golm.robin.GUI.affy.CelFileGroupPanel;
import javax.swing.JFrame;
import junit.framework.TestCase;

/**
 *
 * @author marc
 */
public class TestCelFileGroupPanel extends TestCase {

    public void testCelFileGroupPanel() throws InterruptedException {
        JFrame frame = new JFrame();
        CelFileGroupPanel panel = new CelFileGroupPanel();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        
        while (frame.isVisible())
            Thread.sleep(1);
    
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}