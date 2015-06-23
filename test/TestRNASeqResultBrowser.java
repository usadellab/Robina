
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqResultBrowser;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.openide.util.Exceptions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestRNASeqResultBrowser {
    
    public static void main(String[] args) {        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                RNASeqDataModel data = new RNASeqDataModel();
                data.setProjectDir(new File("/Users/marc/Desktop/BIMBAMBINO_2_lumberjack"));
                try {
                    data.load(new File("/Users/marc/Desktop/BIMBAMBINO_2_lumberjack/source/BIMBAMBINO_2_lumberjack_data.xml"));
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                RNASeqResultBrowser browser = new RNASeqResultBrowser(null, data);
                browser.saveAsPDF(new File("/Users/marc/Desktop/BIMBAMBINO_2_lumberjack/summary.pdf"));
                browser.saveAsRTF(new File("/Users/marc/Desktop/BIMBAMBINO_2_lumberjack/summary.rtf"));
//                browser.saveAsHTML(new File("/Users/marc/Desktop/BIMBAMBINO_2/summary.html"));
                frame.add(browser);
                frame.pack();         
                frame.setVisible(true);
            }
        });
    }     
}
