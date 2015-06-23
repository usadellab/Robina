/*
 * AffyCelFileInfoFrame.java
 *
 * Created on 19. Mai 2008, 14:02
 */

package de.mpimp.golm.robin.GUI.affy;

import affymetrix.fusion.cel.FusionCELData;
import java.util.List;

/**
 *
 * @author  marc
 */
public class AffyCelFileInfoFrame extends javax.swing.JFrame {

    /** Creates new form AffyCelFileInfoFrame */
    public AffyCelFileInfoFrame() {
        initComponents();
    }
    
    /** Creates new form AffyCelFileInfoFrame */
    public AffyCelFileInfoFrame(List<String> files) {
        initComponents();        
        FusionCELData celFileData = new FusionCELData();      
    
        for (String file : files) {
            celFileData.setFileName(file);
            System.out.println("file in infoFrame:"+file);
            InfoPanel.add(new de.mpimp.golm.robin.GUI.affy.CelFileInfoPanel(celFileData));
        }        
        InfoPanel.doLayout();
        this.setTitle("Showing details for " + files.size() + " files");
        pack();
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        InfoPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        InfoPanel.setBackground(new java.awt.Color(255, 255, 255));
        InfoPanel.setLayout(new javax.swing.BoxLayout(InfoPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(InfoPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(closeButton))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
    
    public void run() {
    new AffyCelFileInfoFrame().setVisible(true);
    }
    });    
    }*/
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
