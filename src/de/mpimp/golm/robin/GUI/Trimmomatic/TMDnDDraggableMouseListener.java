/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.Trimmomatic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author marc
 */
public class TMDnDDraggableMouseListener extends MouseAdapter {
    
     @Override()
    public void mousePressed(MouseEvent e) {
//        System.out.println("Step 1 of 7: Mouse pressed. Going to export our RandomDragAndDropPanel so that it is draggable.");
        
        JComponent c = (JComponent) e.getSource();
        TransferHandler handler = c.getTransferHandler();
        handler.exportAsDrag(c, e, TransferHandler.COPY);
     }
}

