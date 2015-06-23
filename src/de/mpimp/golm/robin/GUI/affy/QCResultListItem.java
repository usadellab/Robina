/*
 * QCResultListItem.java
 *
 * Created on 8. Mai 2008, 13:54
 */

package de.mpimp.golm.robin.GUI.affy;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author  marc
 */
public class QCResultListItem extends javax.swing.JPanel implements MouseMotionListener {    

    /** Creates new form QCResultListItem
     * @param wHandler 
     */
    public QCResultListItem(WarningsHandler wHandler) {
        initComponents();
        this.warningsHandler = wHandler;
        this.setVisible(true);
    }
    
    /** Creates new form QCResultListItem
     * @param dataModel
     * @param tcFile
     * @param text
     * @param method
     * @param wHandler
     * @param task 
     */
    public QCResultListItem(TCArrayDataModel dataModel, String tcFile, String text, String method, WarningsHandler wHandler, RTask task ) {
        initComponents();
        this.warningsHandler = wHandler;
        this.method = method;
        this.celFile = tcFile;
        this.rCall = task;
        setOpaque(true);
        
        //Image image;
        //ImageIcon icon = new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        try {
            imagePath = new File(qualDir, dataModel.getExperimentName() + "_" + method + ".png").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }        
        this.setThumbnail(getScaledIcon(imagePath));
        
        this.setDescription(text);
        checkAndDisplayWarnings(method);
        this.setVisible(true);        
    }
    
    /**
     * 
     * @param dataModel
     * @param tcFile
     * @param text
     * @param method
     * @param number
     * @param wHandler
     * @param task
     */
    public QCResultListItem(TCArrayDataModel dataModel, String tcFile, String text, String method, int number, WarningsHandler wHandler, RTask task ) {
        initComponents();
        this.warningsHandler = wHandler;
        this.method = method;
        this.celFile = tcFile;
        this.rCall = task;
        setOpaque(true);
        
        //Image image;
        //ImageIcon icon = new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));  
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        try {
            imagePath = new File(qualDir, dataModel.getExperimentName() + "_" + method + "_" + number +".png").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        this.setThumbnail(getScaledIcon(imagePath));
        
        this.setDescription(text);
        checkAndDisplayWarnings(method);
        this.setVisible(true);        
    }


    ///////

    public QCResultListItem(SCArrayDataModel dataModel, String tcFile, String text, String method, WarningsHandler wHandler, RTask task ) {
        initComponents();
        this.warningsHandler = wHandler;
        this.method = method;
        this.celFile = tcFile;
        this.rCall = task;
        setOpaque(true);

        //Image image;
        //ImageIcon icon = new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        try {
            imagePath = new File(qualDir, dataModel.getExperimentName() + "_" + method + ".png").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }  
        this.setThumbnail(getScaledIcon(imagePath));

        this.setDescription(text);
        checkAndDisplayWarnings(method);
        this.setVisible(true);
    }

    /**
     *
     * @param dataModel
     * @param tcFile
     * @param text
     * @param method
     * @param number
     * @param wHandler
     * @param task
     */
    public QCResultListItem(SCArrayDataModel dataModel, String tcFile, String text, String method, int number, WarningsHandler wHandler, RTask task ) {
        initComponents();
        this.warningsHandler = wHandler;
        this.method = method;
        this.celFile = tcFile;
        this.rCall = task;
        setOpaque(true);

        //Image image;
        //ImageIcon icon = new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        try {
            imagePath = new File(qualDir, dataModel.getExperimentName() + "_" + method + "_" + number +".png").getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        this.setThumbnail(getScaledIcon(imagePath));

        this.setDescription(text);
        checkAndDisplayWarnings(method);
        this.setVisible(true);
    }

    ///////
    
    /** Creates new form QCResultListItem */
    public QCResultListItem(String text, Image image, WarningsHandler wHandler) {
        initComponents();
        this.warningsHandler = wHandler;
        setOpaque(true);
        ImageIcon icon = new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));        
        this.setThumbnail(icon);
        this.setDescription(text);
        checkAndDisplayWarnings(null);
        this.setVisible(true);        
    }
    
    public QCResultListItem(RTask rcall, WarningsHandler wHandler) {
        initComponents();
        this.warningsHandler = wHandler;
        setOpaque(true);
        this.rCall = rcall;
        
        this.setThumbnail(getScaledIcon(imagePath));
        //this.setDescription(rcall.getMethod()+rcall.getOutputFile());
        checkAndDisplayWarnings(null);
        this.setVisible(true);
    }
    
    public QCResultListItem(RTask rcall, int index, WarningsHandler wHandler) {
        initComponents();
        this.warningsHandler = wHandler;
        setOpaque(true);
        this.rCall = rcall;
        this.imagePath = rcall.getOutputFile()+index+".png";
                
        this.setThumbnail(getScaledIcon(imagePath));
        //this.setDescription(rcall.getMethod()+rcall.getOutputFile());
        checkAndDisplayWarnings(null);
        this.setVisible(true);
    }
    
    public QCResultListItem(RTask rcall, String type, WarningsHandler wHandler) {
        initComponents();
        this.warningsHandler = wHandler;
        setOpaque(true);
        this.rCall = rcall;
        this.imagePath = rcall.getOutputFile()+type+".png";
                
        this.setThumbnail(getScaledIcon(imagePath));
        //this.setDescription(rcall.getMethod()+rcall.getOutputFile());
        checkAndDisplayWarnings(type);
        this.setVisible(true);
    }
    
    public QCResultListItem(RTask rt, String imagePath,  String type, WarningsHandler wHandler, String celFile) {
        initComponents();
        this.warningsHandler = wHandler;
        setOpaque(true);
        this.rCall = rt;
        this.imagePath = imagePath;
        this.celFile = celFile;
                
        this.setThumbnail(getScaledIcon(imagePath));
        //this.setDescription(rcall.getMethod()+rcall.getOutputFile());
        checkAndDisplayWarnings(type);
        this.setVisible(true);
    }
    
    private ImageIcon getScaledIcon(String path) {
        if (this.getRCall() != null) {
            System.out.println(this.getRCall().getMethod()+" loading "+path); 
        } else {
            System.out.println("loading image: "+path); 
        }
                       
        BufferedImage rawImage = null;           
        try {
            rawImage = ImageIO.read(new File(path));            
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(new Exception("Could not read image.",ex));      
        }
        
        //System.out.println("scaling...");
        Image scaledImage = rawImage.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        //System.out.println("creating new buffered image...");
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB); 
        //System.out.println("drawing...");       
        icon.getGraphics().drawImage(scaledImage, 0, 0, null);
        
        //FIXME is this the right way to tell the GC to dispose of this
        // object at once?
        //System.out.println("disposing of transient objects...");
        scaledImage.flush();
        rawImage.flush();
        System.gc();
        
        //System.out.println("returning scaled instance...");
        return new ImageIcon(icon);
    }
    
    private void checkAndDisplayWarnings(String type) {
        if (this.rCall.hasWarning()) {
            for (Warning w : this.rCall.getWarnings()) { 
                System.out.println("checking warning:"+w.getType()+"\nmsg:\n"+w.getMessage());
                
                // do we have a cel file -> means this is a single chip item?
                if (this.celFile != null) {
                    System.out.println("hascelfile:"+this.celFile);
                    if (w.getType().toLowerCase().contains(type.toLowerCase()) &&
                            w.getMessage().toLowerCase().contains(Utilities.extractFileNamePathComponent(this.celFile.toLowerCase()))) {
                        System.out.println("msg contains celfile:"+Utilities.extractFileNamePathComponent(this.celFile.toLowerCase())+"\nmsg:\n"+w.getMessage());
                        warningsButton.setVisible(true);   
                        warnings = w.getMessage();
                        warningsHandler.addWarning(w);                         
                    }
                    //System.out.println("msg does not contain celfile:"+this.celFile);
                    return;
                } else if (w.getType().toLowerCase().contains(type.toLowerCase())) {
                    System.out.println("NO CEL FILE:"+this.imagePath);
                    warningsButton.setVisible(true);   
                    warnings = w.getMessage();
                    warningsHandler.addWarning(w);                     
                }
            }
        }
    }
    
    public void setWarningsButtonBG(Color c) {
        warningsButton.setBackground(c);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);        
        excludeBox.addMouseListener(l);
    }
    public void setTextColor(Color tc) {
        descriptionLabel.setForeground(tc);
        excludeBox.setForeground(tc);
    } 
    
    public void setThumbnail(Icon newIcon) {
        thumbnailLabel.setIcon(newIcon);
    }
    
    public void setDescription(String text) {
        descriptionLabel.setText(text);
    }
    
    public boolean isExcluded() {
        return excludeBox.isSelected();
    }
    

    public RTask getRCall() {
        return rCall;
    }
    
    public void setExcludable(boolean ex) {
        excludeBox.setVisible(ex);
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCelFile() {
        return celFile;
    }

    public void setCelFile(String celFile) {
        this.celFile = celFile;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    
        
    public void setWarningsHandler(WarningsHandler warningsHandler) {
        this.warningsHandler = warningsHandler;
    }   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        thumbnailLabel = new javax.swing.JLabel();
        excludeBox = new javax.swing.JCheckBox();
        descriptionLabel = new javax.swing.JLabel();
        warningsButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setMaximumSize(new java.awt.Dimension(32767, 80));
        setMinimumSize(new java.awt.Dimension(0, 80));
        setPreferredSize(new java.awt.Dimension(0, 80));

        excludeBox.setBackground(new java.awt.Color(255, 255, 255));
        excludeBox.setText("Exclude");

        warningsButton.setBackground(new java.awt.Color(255, 255, 255));
        warningsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/warnings.png"))); // NOI18N
        warningsButton.setText("Warning");
        warningsButton.setToolTipText("There are warnings. Please click for details.");
        warningsButton.setBorderPainted(false);
        warningsButton.setContentAreaFilled(false);
        warningsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        warningsButton.setOpaque(true);
        warningsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        warningsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(thumbnailLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(warningsButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(excludeBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, warningsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, excludeBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .add(descriptionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                .add(2, 2, 2))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, thumbnailLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
        );

        warningsButton.setVisible(false);

        this.addMouseMotionListener(this);
    }// </editor-fold>//GEN-END:initComponents

private void warningsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningsButtonActionPerformed
    
    //TODO make a nice custom warnings window that pops up at the mouse position
    // maybe has no decorations, a culstom warnings icon and white BG
    
    Point coords = new Point(mouseX, mouseY);
    SwingUtilities.convertPointToScreen(coords, this);
    QCWarningDialog qcwarn = new QCWarningDialog(null, coords.x, coords.y, warnings);
    
    
    /*JOptionPane msgWin = new JOptionPane();
    msgWin.setBackground(Color.white);
    JOptionPane.showMessageDialog(this, warnings, "Quality check warning", JOptionPane.WARNING_MESSAGE);*/    
}//GEN-LAST:event_warningsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JCheckBox excludeBox;
    private javax.swing.JLabel thumbnailLabel;
    private javax.swing.JButton warningsButton;
    // End of variables declaration//GEN-END:variables
    private RTask rCall = null; 
    private int numberOfInputs = 0;  
    private String imagePath = null;
    private String celFile = null;
    String warnings = null;
    private int mouseX = 0;
    private int mouseY = 0;
    private WarningsHandler warningsHandler;
    private String method;

    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent e) {
        // feels dumm but i need this to place the QCWarning where i want it
        mouseX = e.getX();
        mouseY = e.getY();
    }

   

}
