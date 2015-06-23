
import de.mpimp.golm.robin.GUI.Trimmomatic.TMModuleListItem;
import de.mpimp.golm.robin.GUI.Trimmomatic.TMWorkflowAreaPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestTMDnD {
    public static void main(String[] args) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("test");
                frame.setLayout(new FlowLayout());
                
                JPanel listPanel = new JPanel();
                listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
                listPanel.setPreferredSize(new Dimension(100, 300));
                listPanel.setMinimumSize(new Dimension(100, 300));
                listPanel.setVisible(true);
                
                TMModuleListItem tmitempanel = new TMModuleListItem("ILLUMINACLIP", 
                        "Test item", "this is a test item with quite some text let's see how well that works.", null);    
                tmitempanel.setVisible(true);                
                listPanel.add(tmitempanel);                
                frame.add(listPanel);
                
                TMWorkflowAreaPanel workPanel = new TMWorkflowAreaPanel();
                workPanel.setPreferredSize(new Dimension(600,400));                
                workPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                frame.add(workPanel);  
                
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
    }
    
}
