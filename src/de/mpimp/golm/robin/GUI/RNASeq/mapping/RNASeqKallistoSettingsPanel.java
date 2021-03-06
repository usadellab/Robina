/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RNASeqBowtieSettingsPanel.java
 *
 * Created on 30.09.2011, 13:36:00
 */
package de.mpimp.golm.robin.GUI.RNASeq.mapping;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class RNASeqKallistoSettingsPanel extends RNASeqAbtractMappingToolSettingsPanel {

    /** Creates new form RNASeqBowtieSettingsPanel */
    public RNASeqKallistoSettingsPanel(RNASeqDataModel model, RNASeqWorkflowPanel mainpanel) {
        initComponents();
        this.mainPanel = mainpanel;
        this.dataModel = model;
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

        presetBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        moreArgs = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        insertSize = new javax.swing.JSpinner();
        insertSizeSD = new javax.swing.JSpinner();
        mismachQualSum = new javax.swing.JSpinner();
        confirmSettingsButton = new javax.swing.JButton();
        saveAlignmentsButton = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11), new java.awt.Color(0, 51, 102))); // NOI18N
        setMaximumSize(new java.awt.Dimension(200, 2147483647));
        setMinimumSize(new java.awt.Dimension(180, 210));
        setPreferredSize(new java.awt.Dimension(185, 210));
        setLayout(new java.awt.GridBagLayout());

        presetBox.setFont(presetBox.getFont().deriveFont(presetBox.getFont().getSize()-2f));
        presetBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "insert size 200 SD 20", "inser size 200 SD 20", "custom" }));
        presetBox.setMinimumSize(new java.awt.Dimension(60, 27));
        presetBox.setPreferredSize(new java.awt.Dimension(60, 27));
        presetBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetBoxSelectionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 8);
        add(presetBox, gridBagConstraints);

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(jLabel1, gridBagConstraints);

        moreArgs.setFont(moreArgs.getFont().deriveFont(moreArgs.getFont().getSize()-2f));
        moreArgs.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.moreArgs.text")); // NOI18N
        moreArgs.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 4, 8);
        add(moreArgs, gridBagConstraints);

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()-2f));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        add(jLabel2, gridBagConstraints);

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getSize()-2f));
        jLabel3.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.jLabel3.text")); // NOI18N
        jLabel3.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(jLabel3, gridBagConstraints);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()-2f));
        jLabel4.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.jLabel4.text")); // NOI18N
        jLabel4.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(jLabel4, gridBagConstraints);

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getSize()-2f));
        jLabel5.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.jLabel5.text")); // NOI18N
        jLabel5.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(jLabel5, gridBagConstraints);

        insertSize.setFont(insertSize.getFont().deriveFont(insertSize.getFont().getSize()-2f));
        insertSize.setModel(new javax.swing.SpinnerNumberModel(200, 50, null, 1));
        insertSize.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 8);
        add(insertSize, gridBagConstraints);

        insertSizeSD.setFont(insertSizeSD.getFont().deriveFont(insertSizeSD.getFont().getSize()-2f));
        insertSizeSD.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(20), Integer.valueOf(5), null, Integer.valueOf(1)));
        insertSizeSD.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 8);
        add(insertSizeSD, gridBagConstraints);

        mismachQualSum.setFont(mismachQualSum.getFont().deriveFont(mismachQualSum.getFont().getSize()-2f));
        mismachQualSum.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(70), Integer.valueOf(10), null, Integer.valueOf(1)));
        mismachQualSum.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 8);
        add(mismachQualSum, gridBagConstraints);

        confirmSettingsButton.setFont(confirmSettingsButton.getFont().deriveFont(confirmSettingsButton.getFont().getSize()-2f));
        confirmSettingsButton.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.confirmSettingsButton.text")); // NOI18N
        confirmSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmSettingsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 1.0;
        add(confirmSettingsButton, gridBagConstraints);

        saveAlignmentsButton.setBackground(new java.awt.Color(255, 255, 255));
        saveAlignmentsButton.setFont(saveAlignmentsButton.getFont().deriveFont(saveAlignmentsButton.getFont().getSize()-2f));
        saveAlignmentsButton.setText(org.openide.util.NbBundle.getMessage(RNASeqKallistoSettingsPanel.class, "RNASeqBowtieSettingsPanel.saveAlignmentsButton.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        add(saveAlignmentsButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void confirmSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmSettingsButtonActionPerformed
    this.setEnabled(false);
    mainPanel.mappingToolConfirmed();
}//GEN-LAST:event_confirmSettingsButtonActionPerformed

    @Override
    public void setEnabled(boolean bln) {
        insertSize.setEnabled(bln);
        insertSizeSD.setEnabled(bln);
        mismachQualSum.setEnabled(bln);
        moreArgs.setEnabled(bln);
        jLabel1.setEnabled(bln);
        jLabel2.setEnabled(bln);
        jLabel3.setEnabled(bln);
        jLabel4.setEnabled(bln);
        jLabel5.setEnabled(bln);
        presetBox.setEnabled(bln);
        confirmSettingsButton.setEnabled(bln);
    }

    @Override
    public ArrayList<String> getArgs() {
        return new ArrayList<String>() {
            {                
//                if ( (presetBox.getSelectedIndex() == 2) || (presetBox.getSelectedIndex() == 3) ) {
//                    add("-k"+RobinConstants.MAX_VALID_BOWTIE_ALIGNMENTS);
//                } else {s
            /*        add("-a");
                    add("-m1");  */  //TODO RNASEQKALLISTOMAPPING PROCESSs
//                }
                add("-l "+insertSize.getValue());
                add("-s "+insertSizeSD.getValue());
               /* add("-e"+mismachQualSum.getValue());
                add("-p"+mainPanel.getNumberOfParallelProcessesSetting());*/
                //add("-p1");
                //add("--mm"); // memory map the index to enable sharing between several bowtie processes - 
                
                if (!moreArgs.getText().isEmpty()) {
                    add(moreArgs.getText());   
                }                
            }
        };
    }    
    
    public boolean isSaveAlignments() {        
        SimpleLogger.getLogger(true).logMessage("saving raw alignments: " + saveAlignmentsButton.isSelected());        
        return saveAlignmentsButton.isSelected();
    }

private void presetBoxSelectionChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetBoxSelectionChanged

    insertSize.setEnabled(false);
    insertSizeSD.setEnabled(false);
    mismachQualSum.setEnabled(false);
    moreArgs.setEnabled(false);

    switch (presetBox.getSelectedIndex()) {
        case 0:
            insertSize.setValue(Integer.valueOf(200));
            insertSizeSD.setValue(Integer.valueOf(20));
            mismachQualSum.setValue(Integer.valueOf(70));
            break;
        case 1:
            insertSize.setValue(Integer.valueOf(200));
            insertSizeSD.setValue(Integer.valueOf(20));
            mismachQualSum.setValue(Integer.valueOf(100));
            break;
//        case 2:
//            seedMismatch.setValue(Integer.valueOf(0));
//            seedLength.setValue(Integer.valueOf(28));
//            mismachQualSum.setValue(Integer.valueOf(70));
//            break;
//        case 3:
//            seedMismatch.setValue(Integer.valueOf(2));
//            seedLength.setValue(Integer.valueOf(28));
//            mismachQualSum.setValue(Integer.valueOf(100));
//            break;
        case 2:
            insertSize.setEnabled(true);
            insertSizeSD.setEnabled(true);
            mismachQualSum.setEnabled(true);
            moreArgs.setEnabled(true);
            break;
        default:
            insertSize.setEnabled(false);
            insertSizeSD.setEnabled(false);
            mismachQualSum.setEnabled(false);
            moreArgs.setEnabled(false);
            break;
    }
}//GEN-LAST:event_presetBoxSelectionChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton confirmSettingsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSpinner mismachQualSum;
    private javax.swing.JTextField moreArgs;
    private javax.swing.JComboBox presetBox;
    private javax.swing.JCheckBox saveAlignmentsButton;
    private javax.swing.JSpinner insertSizeSD;
    private javax.swing.JSpinner insertSize;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configureWithString(String mappingToolSettings) {
        if (mappingToolSettings.startsWith("-a;-m1;-n2;-l28;-e100;")) {
            presetBox.setSelectedIndex(0);
        } else if (mappingToolSettings.startsWith("-a;-m1;-n0;-l28;-e70;") ) {
            presetBox.setSelectedIndex(1);
        } else {
            presetBox.setSelectedIndex(2);
            String[] e = mappingToolSettings.split(";");
            for (String s : e) {
                if (s.startsWith("-n")) {
                    insertSize.setValue(Integer.parseInt(s.substring(2)));                    
                } else if (s.startsWith("-l")) {
                    insertSizeSD.setValue(Integer.parseInt(s.substring(2)));
                } else if (s.startsWith("-e")) {
                    mismachQualSum.setValue(Integer.parseInt(s.substring(2)));
                }
            }
        }
    }
}
