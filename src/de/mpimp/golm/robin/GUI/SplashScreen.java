

package de.mpimp.golm.robin.GUI;

import de.mpimp.golm.robin.GUI.RobinMainGUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class SplashScreen extends JFrame implements MouseListener  {

    private Image img;
    private int x, y;
    private Timer timer;
    private RobinMainGUI mainGUI;

    public SplashScreen(int x, int y, long millis) {
        
        mainGUI = new RobinMainGUI();
        System.setProperty("swing.aatext", "true"); 

        this.setSize(x, y);

        this.addMouseListener(this);


        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(d.width / 2 - x / 2, d.height / 2 - y / 2);

        this.setUndecorated(true);

        ImageIcon i = new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/robin-NGS-splash.png"));
        img = i.getImage();

        MediaTracker mt = new MediaTracker(this);


        if (img == null)
                img = this.createImage(x, y);


        mt.addImage(img, 0);

        try {
                mt.waitForAll();
        } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }	

        timer = new Timer();
        timer.schedule(new ExitSplashTask(this), millis);
        this.setVisible(true);
    }
        
    public void mouseClicked(MouseEvent e) {
        //System.out.println("mouse clicked"); 
        this.setVisible(false);
        this.dispose();
        timer.cancel();
        runMainClass();
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    
    private void runMainClass() {
         //System.exit(0); 
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainGUI.setVisible(true);
                mainGUI.showReleaseNotes();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (img != null)
            g.drawImage(img, 0, 0, this);
    }

    class ExitSplashTask extends TimerTask {

        private JFrame frm;

        public ExitSplashTask(JFrame frm) {
                this.frm = frm;
        }
        
        public void run() {           
           
            frm.setVisible(false);
            frm.dispose();
            
            runMainClass();
        }

    }

    
}