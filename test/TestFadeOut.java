
import de.mpimp.golm.common.gui.ProgressDialog;
import java.awt.Color;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestFadeOut.java
 *
 * Created on 04.06.2012, 17:47:46
 */
/**
 *
 * @author marc
 */
public class TestFadeOut extends javax.swing.JFrame {
    private final FixedGlassPane glassPane;

    /** Creates new form TestFadeOut */
    public TestFadeOut() {
        initComponents();
        
        // glass Pane
        glassPane = new FixedGlassPane(null, this);
        glassPane.setOpaque(true);
        glassPane.setBackground(new Color(0, 0, 0, 100));
        this.setGlassPane(glassPane);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText(org.openide.util.NbBundle.getMessage(TestFadeOut.class, "TestFadeOut.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(147, 147, 147)
                .add(jButton1)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(127, 127, 127)
                .add(jButton1)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        final ProgressDialog p = new ProgressDialog(this, true, true);
        p.setText("progress is being made ....");
        p.setIndeterminate(true);
        
        
        javax.swing.SwingWorker w = new javax.swing.SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(3000);
                return null;
            }
            
            @Override
            protected void done() {
                p.setVisible(false);
                p.dispose();        
            }
        };
        
        w.execute();
        p.setVisible(true);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestFadeOut().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
