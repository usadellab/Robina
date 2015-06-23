/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI;

import de.mpimp.golm.common.gui.FixedGlassPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author marc
 */
public class FadeTimerActionListener implements ActionListener {

    private int grayLevel;
    private FixedGlassPane glassPane;
    private final boolean fadeOut;
    private final Timer timer;

    public FadeTimerActionListener(FixedGlassPane glassPane, boolean fadeOut, Timer timer) {
        this.glassPane = glassPane;
        this.fadeOut = fadeOut;
        this.timer = timer;

        // init grayLevel
        if (glassPane.isVisible()) {
            grayLevel = 100;
        } else {
            grayLevel = 0;
        }
    }

    public void actionPerformed(ActionEvent e) {

        System.out.println("fadetimer action fired: graylevel:" + grayLevel);

        boolean done = false;
        if (fadeOut) {
            // fade out            
            grayLevel--;
            if (grayLevel < 1) {
                done = true;

            }
        } else {
            // fade in           

            grayLevel++;
            if (grayLevel > 100) {
                done = true;
            }
        }
        glassPane.setBackground(new Color(0, 0, 0, grayLevel ));

        if (!done) {
            timer.restart();
        }
    }
}
