


import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqBowtieSettingsPanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqRefTranscriptomePanel;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqSamplePanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.BarcodeSplitterPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMIlluminaClipperPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMLeadingTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMSlidingWindowTrimmerPanel;
import javax.swing.*;

public class TestPanelInFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hack #59: Image Border");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RNASeqRefTranscriptomePanel panel = new RNASeqRefTranscriptomePanel(null, null);
        

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(200,200);
        frame.setVisible(true);
    }

}


