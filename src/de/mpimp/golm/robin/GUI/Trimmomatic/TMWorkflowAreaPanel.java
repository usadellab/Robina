/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.Trimmomatic;

import java.awt.Color;
import java.awt.dnd.DropTarget;
import javax.swing.JPanel;

/**
 *
 * @author marc
 */
public class TMWorkflowAreaPanel extends JPanel {

    public TMWorkflowAreaPanel() {
        super();
        
        //DnD
        this.setTransferHandler(new TMDnDTransferHandler());
        this.setDropTarget(new DropTarget(this, new TMDnDDropTargetListener(this)));
        
        this.setBackground(Color.WHITE);
        this.setLayout(new OrientableFlowLayout(OrientableFlowLayout.VERTICAL, OrientableFlowLayout.CENTER, OrientableFlowLayout.TOP));
    }
    
}
