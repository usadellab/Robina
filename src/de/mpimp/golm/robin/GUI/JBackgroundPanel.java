/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author marc
 */
public class JBackgroundPanel extends JPanel {

    Image image; // /de/mpimp/golm/robin/resources/robin_splash_beta.png
    String message = "";

    public JBackgroundPanel(String imageRes) {
        super(null);
    try {
      getFileImage(imageRes);
    }
    catch (Exception ex) {
      message="File load failed: "+ex.getMessage();
    }

    }

    private void getFileImage(String filePath) throws InterruptedException, IOException {
        FileInputStream in = new FileInputStream(filePath);
        byte[] b = new byte[in.available()];
        in.read(b);
        in.close();
        image = Toolkit.getDefaultToolkit().createImage(b);
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        mt.waitForAll();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, this);
            this.getParent().setSize(image.getWidth(null), image.getHeight(null));
        } else {
            g.drawString(message, 40, 40);
        }
    }
}
