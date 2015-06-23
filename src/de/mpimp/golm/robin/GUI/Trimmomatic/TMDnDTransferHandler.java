/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.Trimmomatic;

import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMModulePanel;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author marc
 */
public class TMDnDTransferHandler extends TransferHandler implements DragSourceMotionListener {

    public void dragMouseMoved(DragSourceDragEvent dsde) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override()
    public Transferable createTransferable(JComponent c) {

//        System.out.println("Step 3 of 7: Casting the RandomDragAndDropPanel as Transferable. The Transferable RandomDragAndDropPanel will be queried for acceptable DataFlavors as it enters drop targets, as well as eventually present the target with the Object it transfers.");
        
       
        if (c instanceof TMModuleListItem) {
            Transferable tip = (TMModuleListItem) c;
            return tip;
        } else if (c instanceof TMModulePanel) {
            Transferable tip = (TMModulePanel) c;
            return tip;
        }

        // Not found
        return null;
    }

  

    /**
     * <p>This is queried to see whether the component can be copied, moved, both or neither. We are only concerned with copying.</p>
     * @param c
     * @return
     */
    @Override()
    public int getSourceActions(JComponent c) {
        
//        System.out.println("Step 2 of 7: Returning the acceptable TransferHandler action. Our RandomDragAndDropPanel accepts Copy only.");
        
        if (c instanceof TMModuleListItem) {
            return TransferHandler.COPY;
        } else if (c instanceof TMModulePanel) {
            return TransferHandler.MOVE;
        }
        
        return TransferHandler.NONE;
    }
    
}
