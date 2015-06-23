
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import java.io.File;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestMappingPanel {
    
    public static void main(String[] args) {        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RobinMainGUI main = new RobinMainGUI();
                
                main.simulateWorkflow(
                        new RNASeqWorkflowPanel(main, new File("/Users/marc/Desktop/TRIMMOTESTDIR")), 
                        "card7" );
                
                main.setVisible(true);
            }
        });
    }  
    
}
