/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq.mapping;

import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author marc
 */
public abstract class RNASeqAbtractMappingToolSettingsPanel extends JPanel {
    
    protected RNASeqDataModel dataModel;
    protected RNASeqWorkflowPanel mainPanel;

    public RNASeqDataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(RNASeqDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    public abstract ArrayList<String> getArgs();
    public abstract boolean isSaveAlignments();
    public abstract void configureWithString(String mappingToolSettings);
}
