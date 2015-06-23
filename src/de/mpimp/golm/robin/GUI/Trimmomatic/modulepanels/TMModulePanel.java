/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels;

import de.mpimp.golm.robin.GUI.Trimmomatic.TMDnDDraggableMouseListener;
import de.mpimp.golm.robin.GUI.Trimmomatic.TMDnDTransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JPanel;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TMTrimmerArguments;

/**
 *
 * @author marc
 */
public abstract class TMModulePanel extends JPanel implements Transferable {
    
    private TMTrimmerArguments args;
    protected String ID;
    
    public TMModulePanel() {
        super();
        
         //DnD
        // Add the listener which will export this panel for dragging
        this.addMouseListener(new TMDnDDraggableMouseListener());
        
        // Add the handler, which negotiates between drop target and this 
        // draggable panel
        this.setTransferHandler(new TMDnDTransferHandler());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    

    public DataFlavor[] getTransferDataFlavors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public abstract TMTrimmerArguments getArguments() throws Exception;
    public abstract void setArguments(TMTrimmerArguments args);
    
    
    
}
