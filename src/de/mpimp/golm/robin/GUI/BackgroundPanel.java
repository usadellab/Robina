/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;

import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author marc
 */
public class BackgroundPanel extends JPanel
{
  Image image;
  
  public BackgroundPanel() {  
  }
  
  public BackgroundPanel(String imageResource) {
    try
    {   
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(imageResource));
        image = icon.getImage();
    }
    catch (Exception e) { /*handled in paintComponent()*/ }
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g); 
    if (image != null)
      g.drawImage(image, 0,0,this.getWidth(),this.getHeight(),this);
  }
}
