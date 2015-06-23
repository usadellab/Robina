/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RNASeqRefTranscriptomePanel.java
 *
 * Created on 28.09.2011, 19:33:35
 */
package de.mpimp.golm.robin.GUI.RNASeq.mapping;

import de.mpimp.golm.common.gui.CollapsibleInfoDialog;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.ProgressDialog;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBowtieBuildProcess;
import de.mpimp.golm.robin.rnaseq.parser.RNASeqReferenceParser;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author marc
 */
public class RNASeqRefTranscriptomePanel extends RNASeqAbstractRefPanel {

    private File indexDir;
    private Timer tim;

    /**
     * Creates new form RNASeqRefTranscriptomePanel
     */
    public RNASeqRefTranscriptomePanel(RNASeqDataModel model, RNASeqWorkflowPanel main) {
        super();
        initComponents();
        this.dataModel = model;
        this.mainPanel = main;
        indexDir = new File(mainPanel.getMainGUI().getResourcePath(), "index");
        populateReferenceBox();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        refSeqPanel = new javax.swing.JPanel();
        chooseReferenceFileButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        refSeqNumberLabel = new javax.swing.JLabel();
        refSeqAvgLenLabel = new javax.swing.JLabel();
        refN50Label = new javax.swing.JLabel();
        refNcontentLabel = new javax.swing.JLabel();
        refAmbSeqsLabel = new javax.swing.JLabel();
        referenceIndexBox = new javax.swing.JComboBox();

        setMaximumSize(new java.awt.Dimension(205, 32767));
        setMinimumSize(new java.awt.Dimension(185, 0));
        setPreferredSize(new java.awt.Dimension(185, 198));

        refSeqPanel.setBackground(new java.awt.Color(255, 255, 255));
        refSeqPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refSeqPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11), new java.awt.Color(0, 51, 102))); // NOI18N
        refSeqPanel.setMaximumSize(new java.awt.Dimension(155, 2147483647));
        refSeqPanel.setLayout(new java.awt.GridBagLayout());

        chooseReferenceFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/openFile.png"))); // NOI18N
        chooseReferenceFileButton.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.chooseReferenceFileButton.text")); // NOI18N
        chooseReferenceFileButton.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.chooseReferenceFileButton.toolTipText")); // NOI18N
        chooseReferenceFileButton.setBorder(null);
        chooseReferenceFileButton.setBorderPainted(false);
        chooseReferenceFileButton.setContentAreaFilled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), chooseReferenceFileButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        chooseReferenceFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseReferenceFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 0);
        refSeqPanel.add(chooseReferenceFileButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        refSeqPanel.add(jSeparator2, gridBagConstraints);

        refSeqNumberLabel.setFont(refSeqNumberLabel.getFont().deriveFont(refSeqNumberLabel.getFont().getSize()-2f));
        refSeqNumberLabel.setForeground(new java.awt.Color(102, 102, 102));
        refSeqNumberLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refSeqNumberLabel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), refSeqNumberLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 0, 0);
        refSeqPanel.add(refSeqNumberLabel, gridBagConstraints);

        refSeqAvgLenLabel.setFont(refSeqAvgLenLabel.getFont().deriveFont(refSeqAvgLenLabel.getFont().getSize()-2f));
        refSeqAvgLenLabel.setForeground(new java.awt.Color(102, 102, 102));
        refSeqAvgLenLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refSeqAvgLenLabel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), refSeqAvgLenLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 0, 0);
        refSeqPanel.add(refSeqAvgLenLabel, gridBagConstraints);

        refN50Label.setFont(refN50Label.getFont().deriveFont(refN50Label.getFont().getSize()-2f));
        refN50Label.setForeground(new java.awt.Color(102, 102, 102));
        refN50Label.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refN50Label.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), refN50Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 0, 0);
        refSeqPanel.add(refN50Label, gridBagConstraints);

        refNcontentLabel.setFont(refNcontentLabel.getFont().deriveFont(refNcontentLabel.getFont().getSize()-2f));
        refNcontentLabel.setForeground(new java.awt.Color(102, 102, 102));
        refNcontentLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refNcontentLabel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), refNcontentLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 16, 0, 0);
        refSeqPanel.add(refNcontentLabel, gridBagConstraints);

        refAmbSeqsLabel.setFont(refAmbSeqsLabel.getFont().deriveFont(refAmbSeqsLabel.getFont().getSize()-2f));
        refAmbSeqsLabel.setForeground(new java.awt.Color(102, 102, 102));
        refAmbSeqsLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.refAmbSeqsLabel.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), refAmbSeqsLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 6, 0);
        refSeqPanel.add(refAmbSeqsLabel, gridBagConstraints);

        referenceIndexBox.setFont(referenceIndexBox.getFont().deriveFont(referenceIndexBox.getFont().getSize()-1f));
        referenceIndexBox.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqRefTranscriptomePanel.class, "RNASeqRefTranscriptomePanel.referenceIndexBox.toolTipText")); // NOI18N
        referenceIndexBox.setMaximumSize(new java.awt.Dimension(150, 27));
        referenceIndexBox.setMinimumSize(new java.awt.Dimension(50, 27));
        referenceIndexBox.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), referenceIndexBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        referenceIndexBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                referenceIndexBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 5, 3);
        refSeqPanel.add(referenceIndexBox, gridBagConstraints);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(refSeqPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(refSeqPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void chooseReferenceFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseReferenceFileButtonActionPerformed

    java.awt.FileDialog pdChooser = new java.awt.FileDialog(new Frame(), "Please choose reference transcriptome", FileDialog.LOAD);
    pdChooser.setDirectory(System.getProperty("user.home"));
    // this might fix it on a mac
    System.setProperty("apple.awt.fileDialogForDirectories", "false");
    pdChooser.setVisible(true);
    if (pdChooser.getFile() == null) {
        return;
    }
    File referenceFile = new File(pdChooser.getDirectory(), pdChooser.getFile());
    dataModel.setReferenceFile(referenceFile);
    dataModel.setReferenceType(RNASeqDataModel.REFERENCE_TYPE.TRANSCRIPTOME);

    if (this.checkReferenceFile(referenceFile)) {
        referenceIndexBox.addItem(referenceFile.getName());
        //referenceIndexBox.setSelectedItem(referenceFile.getName());
        referenceCheckedReadyToGo();
    } else {
        SimpleLogger.getLogger(true).logMessage("Reference file check failed");
        return;
    }

}//GEN-LAST:event_chooseReferenceFileButtonActionPerformed

private void referenceIndexBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_referenceIndexBoxActionPerformed
    dataModel.setReferenceindexName((String) referenceIndexBox.getSelectedItem());

    //DEBUG
    System.out.println("chosen refindex="+dataModel.getReferenceindexName());

    mainPanel.readyToStartMapping();
}//GEN-LAST:event_referenceIndexBoxActionPerformed

    private boolean checkReferenceFile(final File referenceFile) {

        // read in the reference file and check it on the way    
        boolean checkOK = true;
        final ProgressDialog dial = new ProgressDialog(mainPanel.getMainGUI(), true, true);
        dial.setText("Reading reference file ...");
        dial.setIndeterminate(true);

        //dataModel.setReferenceindexName((String) referenceIndexBox.getSelectedItem());

        SwingWorker<RNASeqReferenceParser, Integer> refChecker = new SwingWorker<RNASeqReferenceParser, Integer>() {
            @Override
            protected RNASeqReferenceParser doInBackground() {
                RNASeqReferenceParser fastaParser = null;
                try {
                    fastaParser = new RNASeqReferenceParser(new FileInputStream(referenceFile), 0);
                    fastaParser.parseNow();

                } catch (Exception e) {
                    CollapsibleInfoDialog dial =
                            new CollapsibleInfoDialog(mainPanel.getMainGUI(), "Could not read reference sequence file.", e);
                    dial.setVisible(true);
                    fastaParser = null;
                }                
                return fastaParser;
            }

            @Override
            protected void done() {
                dial.dispose();
                try {
                    RNASeqReferenceParser fastaParser = get();
                    if (fastaParser == null) {
                        SimpleLogger.getLogger(true).logMessage("FASTA reference parser failed. Import reference cancelled");
                        mainPanel.getMainGUI().stopBusyAnimation();
                        return;
                    }

                    refSeqAvgLenLabel.setText("Avg. length: " + fastaParser.getNumberSymbolsRead() / fastaParser.getNumberEntriesRead());
                    refSeqNumberLabel.setText("Sequences: " + fastaParser.getNumberEntriesRead());
                    refNcontentLabel.setText("N content: " + fastaParser.getNcontent());
                    refAmbSeqsLabel.setText("Ambiguous Sequences: " + fastaParser.getNumberOfAmbiguousSequences());
                    refN50Label.setText("N50: " + fastaParser.getN50());

                    File indexPath = new File(indexDir, dataModel.getReferenceFile().getName() + "_" + dataModel.getReferenceType() + ".lengths");
                    fastaParser.writeInfoTable(indexPath);

                    if (fastaParser.hasAmbiguous()) {
                        SimpleLogger.getLogger(true).logMessage("Reference contains " 
                                + fastaParser.getNumberOfAmbiguousSequences() + " ambiguous sequences (" 
                                + DecimalFormat.getPercentInstance().format(
                                    (float)fastaParser.getNumberOfAmbiguousSequences() / (float) fastaParser.getNumberEntriesRead() ) 
                                + "%)");
                        JOptionPane.showMessageDialog(RNASeqRefTranscriptomePanel.this,
                                "The reference contains sequences with ambiguous nucleotides\n"
                                + "Alignments to these sequences will be considered invalid\n"
                                + "by BOWTIE when aligning the reads to the reference.",
                                "Ambiguous nucleotides in reference", JOptionPane.WARNING_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(RNASeqRefTranscriptomePanel.this,
                            "Robin will now prepare the reference sequences for\n"
                            + "bowtie - this may take a while depending on the size\n"
                            + "of the file you supplied.", "Bulding reference index", JOptionPane.WARNING_MESSAGE);

                    buildNewReferenceIndex();
                } catch (InterruptedException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (ExecutionException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (IOException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (Exception e) {                    
                    SimpleLogger.getLogger(true).logException(e);
                }
            }
        };

        mainPanel.getMainGUI().startBusyAnimation("building index...");
//        mainPanel.getMainGUI().setGrayedOut(true);
        refChecker.execute();
        dial.setVisible(true);
        try {
            if (refChecker.get() == null) {
                return false;
            } 
        } catch (Exception e) {
            // ignore
        } 
        return true;
    }

    @Override
    protected void referenceCheckedReadyToGo() {
        populateReferenceBox();
        mainPanel.readyToStartMapping();
    }

    private void buildNewReferenceIndex() throws IOException {

        mainPanel.appendToMappingProgressPane("Building Bowtie index", RobinConstants.attrBoldBlack);     // for "+dataModel.getReferenceindexName()

        final ProgressDialog progress = new ProgressDialog(mainPanel.getMainGUI(), true, true);
        progress.setText("Building bowtie index...");
        progress.setIndeterminate(true);
//        mainPanel.getMainGUI().setGrayedOut(true);


        File indexPath = new File(indexDir, dataModel.getReferenceFile().getName() + "_" + dataModel.getReferenceType() + "_bwtindex");

        String arch = mainPanel.getDelegate().getSysArchString();
        File instDir = new File(mainPanel.getMainGUI().getInstallPath());
        File binDir = new File(instDir, "bin");
        File bt_build = new File(binDir, "bowtie-build_" + arch);

        SimpleLogger.getLogger(true).logMessage("bowtie_build_cmd: <" + bt_build.getCanonicalPath() + ">");

        final RNASeqBowtieBuildProcess p = new RNASeqBowtieBuildProcess(
                dataModel,
                bt_build.getCanonicalPath(),
                new ArrayList<String>(), // no args
                indexPath);

        ExecutorService exe = mainPanel.getExecutor();
        final Future fut = exe.submit(p);

        tim = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fut.isDone()) {
                    if (p.getExitValue() != 0) {
                        new SimpleErrorMessage(mainPanel, "Bowtie-build process failed. Exit value:" + p.getExitValue());
                        progress.dispose();
                        badBuild();
                        return;
                    } else {
                        progress.dispose();
                        mainPanel.getMainGUI().stopBusyAnimation();
                        referenceCheckedReadyToGo();
                    }
                }
            }
        });
        tim.start();
        progress.setVisible(true);
    }

    private void badBuild() {
        tim.stop();
    }

    private void populateReferenceBox() {
        File[] files = indexDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getName().contains("TRANSCRIPTOME")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (files == null) {
            return;
        }

        HashMap<String, Integer> uniqNames = new HashMap<String, Integer>();
        for (File file : files) {
            String[] elems = file.getName().split("_TRANSCRIPTOME");
            uniqNames.put(elems[0], 1);
        }
        referenceIndexBox.removeAllItems();
        for (String refindex : uniqNames.keySet()) {
            referenceIndexBox.addItem(refindex);
        }
        
        if (dataModel.getReferenceindexName() != null) {
            referenceIndexBox.setSelectedItem((String)dataModel.getReferenceindexName());
        }
        //referenceIndexBoxActionPerformed(null);
    }
//    public static void main(String[] args) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                
//                RNASeqRefTranscriptomePanel refpanel = new RNASeqRefTranscriptomePanel(new RNASeqDataModel());
//                
//                JFrame frame = new JFrame();
//                frame.add(refpanel);
//                frame.pack();
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setLocation(500, 500); 
//                frame.setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chooseReferenceFileButton;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel refAmbSeqsLabel;
    private javax.swing.JLabel refN50Label;
    private javax.swing.JLabel refNcontentLabel;
    private javax.swing.JLabel refSeqAvgLenLabel;
    private javax.swing.JLabel refSeqNumberLabel;
    private javax.swing.JPanel refSeqPanel;
    private javax.swing.JComboBox referenceIndexBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setReferenceIndex(String referenceindexName) {
        referenceIndexBox.setSelectedItem(referenceindexName);
    }

    @Override
    public void resetPanel() {
        populateReferenceBox();
    }
}
