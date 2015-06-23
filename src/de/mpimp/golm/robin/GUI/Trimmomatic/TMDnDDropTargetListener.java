/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.Trimmomatic;

import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.BarcodeSplitterPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMCropperPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMIlluminaClipperPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMLeadingTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMMinimumLengthTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMModulePanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMSlidingWindowTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMTrailingTrimmerPanel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class TMDnDDropTargetListener implements DropTargetListener {
    
    private TMWorkflowAreaPanel rootPanel;
    
    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            notDroppableCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

    public TMDnDDropTargetListener(TMWorkflowAreaPanel rootPanel) {        
        this.rootPanel = rootPanel;
    }    
    

    public void dragEnter(DropTargetDragEvent dtde) {}

    public void dragOver(DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {}

    public void dragExit(DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    public void drop(DropTargetDropEvent dtde) {
//        System.out.println("Step 5 of 7: The user dropped the panel. The drop(...) method will compare the drops location with other panels and reorder the panels accordingly.");
        
        // Done with cursors, dropping
        this.rootPanel.setCursor(Cursor.getDefaultCursor());
        
        // Just going to grab the expected DataFlavor to make sure
        // we know what is being dropped
        DataFlavor dragAndDropPanelFlavor = null;        
        Object transferableObj = null;
        Transferable transferable = null;
        
        try {
            // Grab expected flavor
            dragAndDropPanelFlavor = TMModuleListItem.getDataFlavor();
            
            transferable = dtde.getTransferable();
            DropTargetContext c = dtde.getDropTargetContext();
            
            // What does the Transferable support
            if (transferable.isDataFlavorSupported(dragAndDropPanelFlavor)) {
                transferableObj = dtde.getTransferable().getTransferData(dragAndDropPanelFlavor);
            } 
            
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        
        // If didn't find an item, bail
        if (transferableObj == null) {
            return;
        }
        
        TMModuleListItem droppedItem = (TMModuleListItem) transferableObj;        
        
        // this is where HANS hides....
        
        String ID = droppedItem.getID();
        System.out.println("dropped ID = "+ID);
       
        
        if (ID.equals("BARCODESPLITTER")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            BarcodeSplitterPanel panel = new BarcodeSplitterPanel();
            panel.setID(ID);
            panel.setVisible(true);            
            /** 
             * The BarcodeSplitter must, if added,
             * always be the first Trimmer in the
             * pipeline. Hence we'll have to make 
             * sure that it's first, no matter 
             * how many others are already on the
             * panel.
             */
            ArrayList<TMModulePanel> modulePanels = new ArrayList<TMModulePanel>();   
            modulePanels.add(panel);
                
            while (rootPanel.getComponentCount() != 0) {
                Component comp = rootPanel.getComponent(0);                
                if (comp instanceof TMModulePanel) { 
                    modulePanels.add((TMModulePanel) comp); 
                    rootPanel.remove(comp);
                }
            }
            for (int t = 0; t < modulePanels.size(); t++) {
                rootPanel.add(modulePanels.get(t));
            }            
            rootPanel.revalidate();
            rootPanel.repaint();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
            
        } else if (ID.equals("ILLUMINACLIP")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMIlluminaClipperPanel panel = new TMIlluminaClipperPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        } else if (ID.equals("LEADING")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMLeadingTrimmerPanel panel = new TMLeadingTrimmerPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        }else if (ID.equals("CROP")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMCropperPanel panel = new TMCropperPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        }else if (ID.equals("TRAILING")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMTrailingTrimmerPanel panel = new TMTrailingTrimmerPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        }else if (ID.equals("SLIDINGWINDOW")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMSlidingWindowTrimmerPanel panel = new TMSlidingWindowTrimmerPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        }  else if (ID.equals("MINLEN")) {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            TMMinimumLengthTrimmerPanel panel = new TMMinimumLengthTrimmerPanel();
            panel.setID(ID);
            panel.setVisible(true);
            rootPanel.add(panel);
            rootPanel.revalidate();
            droppedItem.setVisible(false);
            dtde.dropComplete(true);
        } 
    }
    
}
