/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.misc.GUI;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCprogressPanel;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 *
 * @author marc
 */
public class TargetedMouseListener implements AWTEventListener {

    private RNASeqQCprogressPanel targetComponent;
    private boolean hasExited = true;

    public TargetedMouseListener(RNASeqQCprogressPanel p2) {
        targetComponent = p2;
    }

    @Override
    public void eventDispatched(AWTEvent e) {
        if (e instanceof MouseEvent) {
            MouseEvent m = (MouseEvent) e;
            
            Point p = SwingUtilities.convertPoint(
                        (Component) e.getSource(),
                        m.getPoint(),
                        targetComponent);

            if (m.getID() == MouseEvent.MOUSE_ENTERED && targetComponent.contains(p)) {
                if (hasExited) {
//                    System.out.println("Entered " + targetComponent.getName());
                    targetComponent.setActive();
                    hasExited = false;
                }
            } else if (m.getID() == MouseEvent.MOUSE_EXITED && !targetComponent.contains(p)) {

//                System.out.println("Exited" + targetComponent.getName());
                targetComponent.setInactive();
                hasExited = true;
            }
        }
    }
}
