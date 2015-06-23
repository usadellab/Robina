/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author marc
 */
public class ProgressIndicator extends JLabel {
    
    private int busyAnimationRate = 30;
    private Timer busyIconTimer;
    private Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    
    public ProgressIndicator() {
        busyAnimationRate = 30;
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/busyicons/busy-icon"+i+".png"));
        }
        final ProgressIndicator progressIconLabel = this;
        busyIconTimer = new javax.swing.Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
               progressIconLabel.setIcon(busyIcons[busyIconIndex]);
            }
        }); 
        idleIcon = new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/busyicons/idle-icon.png"));       
    }
    
    
    public int getBusyAnimationRate() {
        return busyAnimationRate;
    }

    public void setBusyAnimationRate(int busyAnimationRate) {
        this.busyAnimationRate = busyAnimationRate;
    }
    
    public void start() {
        busyIconTimer.start();
    }
    
    public void stop() {
        busyIconTimer.stop();
        this.setIcon(idleIcon);
    }
}
