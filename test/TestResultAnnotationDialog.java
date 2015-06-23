
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
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
public class TestResultAnnotationDialog {
    
    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        frame.setVisible(true);
        ResultAnnotationDialog annoDialog = new ResultAnnotationDialog(null, true, 
                new File("/Users/marc/sequences/annotation/melon/mappings/CM_protein_v3.5"),
                new File("/Users/marc/Desktop/melon_isoem/detailed_results_robina_strict/full_table_ED-EN.txt") );
        annoDialog.setVisible(true);
    }
    
}
