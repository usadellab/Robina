
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqDelegate;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBowtieBuildProcess;
import java.io.File;
import java.util.ArrayList;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestBowtieBuildProcess extends TestCase {
    
    public void testProcess() {
        
        RNASeqDataModel model = new RNASeqDataModel();
        model.setReferenceFile(new File("/Users/marc/sequences/annotation/arabidopsis/TAIR10/TAIR10_cds_20101028"));
        model.setProjectDir(new File("/Users/marc/Desktop/TRIMMOTESTDIR"));
        model.setReferenceType(RNASeqDataModel.REFERENCE_TYPE.TRANSCRIPTOME);
        File indexPath = (new File("index", model.getReferenceFile().getName() + "_" +model.getReferenceType() + "_bwtindex"));        
        
        RNASeqDelegate del = new RNASeqDelegate(null);        
        RNASeqBowtieBuildProcess p = new RNASeqBowtieBuildProcess(model, "bin/bowtie-build_"+del.getSysArchString(), new ArrayList<String>(), indexPath);
        p.run();
    
    }
}
