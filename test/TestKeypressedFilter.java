
import de.mpimp.golm.robin.GUI.StrictRTermFilterDocument;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.common.gui.SimpleErrorMessage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestKeypressedFilter.java
 *
 * Created on 02.04.2012, 15:55:55
 */
/**
 *
 * @author marc
 */
public class TestKeypressedFilter extends javax.swing.JFrame {

    /** Creates new form TestKeypressedFilter */
    public TestKeypressedFilter() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newConditionField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        newConditionField.setDocument(new StrictRTermFilterDocument());
        newConditionField.setText(org.openide.util.NbBundle.getMessage(TestKeypressedFilter.class, "TestKeypressedFilter.newConditionField.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(newConditionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(newConditionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestKeypressedFilter().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField newConditionField;
    // End of variables declaration//GEN-END:variables
}
