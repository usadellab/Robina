/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI.affy;

import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author marc
 */
public class QCItemMouseListener implements MouseListener  {
    
    final private Color LIGHT_BLUE = new Color(214, 232, 255);
    
    public QCItemMouseListener() {
        super();        
    }
    
    public void mouseClicked(MouseEvent e) {
        JComponent sourceItem = (JComponent) e.getSource();
        if (sourceItem instanceof JCheckBox) {
            JCheckBox box = (JCheckBox) sourceItem;                        
            System.out.println("Panel "+sourceItem.getParent().getName()+" will be excluded:"+box.isSelected());
            return;
        }
        System.out.println("Panel "+sourceItem.getName()+" clicked");
        // now forward the event to a central showResultDetail() method
        //that opens the fullsize view of the R result in a kind of Dialog
        QCResultListItem item;
        RTask rc = null;
        
        System.out.println("source has class: "+sourceItem.getClass().toString());
        
        if (sourceItem instanceof QCResultListItem) {
            item = (QCResultListItem) sourceItem;
            rc = item.getRCall();
        } else {
            item = (QCResultListItem) sourceItem.getParent();
            rc = item.getRCall();
        } 
        QCDetailFrame detail = new QCDetailFrame(rc, item.getImagePath());
    }

    public void mousePressed(MouseEvent e) {
        JComponent sourceItem = (JComponent) e.getSource();
        sourceItem.setBackground(Color.BLUE);
        if (sourceItem instanceof QCResultListItem) {
            QCResultListItem item = (QCResultListItem) sourceItem;
            item.setTextColor(Color.WHITE);
        }                    
    }

    public void mouseReleased(MouseEvent e) {
        JComponent sourceItem = (JComponent) e.getSource();
        sourceItem.setBackground(LIGHT_BLUE);
        if (sourceItem instanceof QCResultListItem) {
            QCResultListItem item = (QCResultListItem) sourceItem;
            item.setTextColor(Color.BLACK);
        }
    }

    public void mouseEntered(MouseEvent e) {                    
        JComponent sourceItem = (JComponent) e.getSource();
        if (sourceItem instanceof QCResultListItem) {
            sourceItem.setBackground(LIGHT_BLUE);
            ((QCResultListItem) sourceItem).setWarningsButtonBG(LIGHT_BLUE);
        } else {
            sourceItem.getParent().setBackground(LIGHT_BLUE);
        }                                       
    }

    public void mouseExited(MouseEvent e) {  
        JComponent sourceItem = (JComponent) e.getSource();
        if (sourceItem instanceof QCResultListItem) {
            sourceItem.setBackground(Color.WHITE); 
            ((QCResultListItem) sourceItem).setWarningsButtonBG(Color.WHITE);
        } else {
            sourceItem.getParent().setBackground(Color.WHITE);
        }                     
    }

    
       
}
