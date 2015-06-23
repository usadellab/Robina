


import de.mpimp.golm.robin.GUI.JBackgroundPanel;
import javax.swing.JFrame;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestJBackgroundPanel {   

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("test");
                JBackgroundPanel panel = new JBackgroundPanel("/Users/marc/Pictures/misc/alfred_e_neuman.jpg");
                panel.setVisible(true);
                frame.add(panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
