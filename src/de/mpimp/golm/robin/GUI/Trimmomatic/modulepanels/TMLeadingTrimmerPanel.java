/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TMLeadingTrimmerPanel.java
 *
 * Created on 13.09.2011, 16:26:01
 */
package de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels;

import de.mpimp.golm.common.help.HelpHandler;
import de.mpimp.golm.robin.misc.GUI.WorkflowModuleBorder;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TMTrimmerArguments;

/**
 *
 * @author marc
 */
public class TMLeadingTrimmerPanel extends TMModulePanel {

    /** Creates new form TMLeadingTrimmerPanel */
    public TMLeadingTrimmerPanel() {
        initComponents();
        this.setBorder(new WorkflowModuleBorder(WorkflowModuleBorder.BLUE_SHADOW));
    }
    
    public int getMinQual() {
        return ((Integer) minQualSpinner.getValue() ).intValue();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        minQualSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        helpLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(212, 232, 255));
        setMaximumSize(new java.awt.Dimension(2147483647, 150));
        setMinimumSize(new java.awt.Dimension(300, 120));
        setPreferredSize(new java.awt.Dimension(350, 120));
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(new java.awt.Color(212, 232, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(400, 80));
        jPanel1.setMinimumSize(new java.awt.Dimension(350, 70));
        jPanel1.setPreferredSize(new java.awt.Dimension(350, 70));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()-2f));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(TMLeadingTrimmerPanel.class, "TMLeadingTrimmerPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setBackground(new java.awt.Color(212, 232, 255));
        jLabel2.setFont(jLabel2.getFont().deriveFont((jLabel2.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, jLabel2.getFont().getSize()+1));
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(TMLeadingTrimmerPanel.class, "TMLeadingTrimmerPanel.jLabel2.text")); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(400, 80));
        jLabel2.setMinimumSize(new java.awt.Dimension(300, 17));
        jLabel2.setPreferredSize(new java.awt.Dimension(300, 17));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel2, gridBagConstraints);

        minQualSpinner.setFont(minQualSpinner.getFont().deriveFont(minQualSpinner.getFont().getSize()-2f));
        minQualSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(20), Integer.valueOf(0), null, Integer.valueOf(1)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 10, 4);
        jPanel1.add(minQualSpinner, gridBagConstraints);

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getSize()-2f));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(TMLeadingTrimmerPanel.class, "TMLeadingTrimmerPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel3, gridBagConstraints);

        helpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        helpLabel.setText(org.openide.util.NbBundle.getMessage(TMLeadingTrimmerPanel.class, "TMLeadingTrimmerPanel.helpLabel.text")); // NOI18N
        helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel1.add(helpLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void helpLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpLabelMouseClicked
    HelpHandler.getHandler().showHelpDialogForKey("rnaseqhelp."+ID, null);
}//GEN-LAST:event_helpLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel helpLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner minQualSpinner;
    // End of variables declaration//GEN-END:variables

    @Override
    public TMTrimmerArguments getArguments() {        
        TMTrimmerArguments args = new TMTrimmerArguments("LEADING");        
        args.put("qual", minQualSpinner.getValue());              
        return args;
    }

    @Override
    public void setArguments(TMTrimmerArguments args) {
        this.minQualSpinner.setValue( Integer.parseInt(args.get("qual").toString()) );
    }
}
