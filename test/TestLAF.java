
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.openide.util.Exceptions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestLAF {
    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel(
//                            "net.sourceforge.mlf.metouia");
//            
//            UIManager.setLookAndFeel(
//                        "ch.randelshofer.quaqua.QuaquaLookAndFeel");
            
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());        
        } catch (UnsupportedLookAndFeelException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        JFileChooser ch = new JFileChooser();
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ch.setMultiSelectionEnabled(false);
        if (ch.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {          
        } 
    }
    
    
}
