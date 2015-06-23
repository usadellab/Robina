/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq.mapping;

import de.mpimp.golm.common.help.HelpHandler;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import javax.swing.JPanel;

/**
 *
 * @author marc
 */
public abstract class RNASeqAbstractRefPanel extends JPanel {
    
    protected RNASeqDataModel dataModel;
    protected RNASeqWorkflowPanel mainPanel;
    protected HelpHandler helpHandler;
    
    public RNASeqAbstractRefPanel() {
        super();
        this.helpHandler = HelpHandler.getHandler();
    }
    
    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
    }

    public RNASeqDataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(RNASeqDataModel dataModel) {
        this.dataModel = dataModel;
    }
    
    protected abstract void referenceCheckedReadyToGo();
    public abstract void setReferenceIndex(String referenceindexName);
    public abstract void resetPanel();
}
