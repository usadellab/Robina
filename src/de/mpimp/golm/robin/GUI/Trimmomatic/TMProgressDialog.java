/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TMProgressDialog.java
 *
 * Created on 22.09.2011, 11:23:57
 */
package de.mpimp.golm.robin.GUI.Trimmomatic;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import org.openide.util.Exceptions;
import org.usadellab.trimmomatic.fastq.trim.Trimmer;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TMTrimmerArguments;
import de.mpimp.golm.robin.rnaseq.trimmomatic.RobinTrimmerFactory;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TrimmomaticThread;

/**
 *
 * @author marc
 */
public class TMProgressDialog extends javax.swing.JDialog implements ActionListener {
    
    private RNASeqDataModel dataModel;
    private ExecutorService executor;
    private RobinTrimmerFactory factory;
    private boolean stopTasks = false;
    private ArrayList<Future> taskStats; 
    private boolean finished = false;
    private Timer globalProcTimer;
    private ArrayList<TrimmomaticThread> threads;
    private boolean userCancel = false;
    private RobinMainGUI mainGUI;
    
    
    private static final int GLOBAL_POLL_INTERVAL = 500;

    /** Creates new form TMProgressDialog */
    public TMProgressDialog(RobinMainGUI parent, boolean modal, RNASeqDataModel model, ExecutorService exec) {
        super(parent, modal);
        this.setUndecorated(true);
        initComponents();  
        centerOnParent();
        
        this.mainGUI = parent;
        this.dataModel = model;
        this.executor = exec;
        this.factory = new RobinTrimmerFactory();
        this.taskStats = new ArrayList<Future>();
        this.threads = new ArrayList<TrimmomaticThread>();
        globalProcTimer = new Timer(GLOBAL_POLL_INTERVAL, this);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        try {            
            startTrimmoTasks();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
        private void centerOnParent() {
        Dimension parentDim = getParent().getSize();
        Point parentLoc = getParent().getLocation();
        Dimension myDim = getSize();

        int myX = (parentDim.width / 2 + parentLoc.x) - myDim.width / 2;
        int myY = (parentDim.height / 2 + parentLoc.y) - myDim.height / 2;

        setLocation(myX, myY);
    }
    
    public void actionPerformed(ActionEvent e) {
        finished = true;
        for (Future f : taskStats) {
            if (!f.isDone()) {
                finished = false;
//                System.out.println("not done");
                return;
            }
        }
        wrapUp();
        
    }
    
    private void wrapUp() {
        System.out.println("done");
        globalProcTimer.stop();
        continueButton.setEnabled(finished);
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.TRIMMOMATIC_DONE);
//        showReportButton.setEnabled(finished);
        cancelButton.setEnabled(false);
        
        // collect all the output files and replace the original input files in the model  
        ArrayList<FastQFile> outfiles = new ArrayList<FastQFile>();
        for (TrimmomaticThread thread : threads) {
            for (File out : thread.getOutputFiles()) {
                try {
                    SimpleLogger.getLogger(true).logMessage("trimmed file: "+out.getCanonicalPath());                    
                    FastQFile outFile = new FastQFile(out.getCanonicalPath());
                    outFile.setReadCount(thread.getEntriesProcessed());
                    outFile.setFilteredReadCount(thread.getEntriesSurvived());
                    
                    //FIXME here we would want to add the number of reads that
                    // survived trimming into the fastqfile
                    // unfortunately we do not keep the info 
                    // separately for each of the split barcode files....
                    
                    outfiles.add(outFile);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }            
        }
        dataModel.setInputFiles(outfiles);
    }
    
    private void startTrimmoTasks() throws IOException {
        progressListPanel = new JPanel();
        progressListPanel.setBackground(Color.WHITE);
        progressListPanel.setLayout(new BoxLayout(progressListPanel, BoxLayout.Y_AXIS));
        
        for (FastQFile in : dataModel.getInputFiles()) {
            ArrayList<Trimmer> modules = new ArrayList<Trimmer>();            
            for (TMTrimmerArguments arg : dataModel.getTrimmers()) {
                modules.add(factory.makeRobinTrimmer(arg));
            }            
            TrimmomaticThread thread = new TrimmomaticThread(in, dataModel.getProjectDir(), modules, dataModel.getQualityEncoding().getOffset(), this);
            Future fut = executor.submit(thread);
            taskStats.add(fut);
            threads.add(thread);
            TMFileProgressPanel progPanel = new TMFileProgressPanel(this, in, thread, fut);
            progressListPanel.add(progPanel);
        }
        progressListPanel.revalidate();
        progressListScrollPane.add(progressListPanel);
        progressListScrollPane.setViewportView(progressListPanel);
        globalProcTimer.start();
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
        cancelButton = new javax.swing.JButton();
        title = new javax.swing.JLabel();
        continueButton = new javax.swing.JButton();
        progressListScrollPane = new javax.swing.JScrollPane();
        progressListPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getSize()-2f));
        cancelButton.setText(org.openide.util.NbBundle.getMessage(TMProgressDialog.class, "TMProgressDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(cancelButton, gridBagConstraints);

        title.setFont(title.getFont().deriveFont((title.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, title.getFont().getSize()+1));
        title.setText(org.openide.util.NbBundle.getMessage(TMProgressDialog.class, "TMProgressDialog.title.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        jPanel1.add(title, gridBagConstraints);

        continueButton.setFont(continueButton.getFont().deriveFont(continueButton.getFont().getSize()-2f));
        continueButton.setText(org.openide.util.NbBundle.getMessage(TMProgressDialog.class, "TMProgressDialog.continueButton.text")); // NOI18N
        continueButton.setEnabled(false);
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(continueButton, gridBagConstraints);

        progressListScrollPane.setBorder(null);

        progressListPanel.setBackground(new java.awt.Color(255, 255, 255));
        progressListPanel.setMaximumSize(new java.awt.Dimension(32768, 32768));
        progressListPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        progressListPanel.setLayout(new javax.swing.BoxLayout(progressListPanel, javax.swing.BoxLayout.Y_AXIS));
        progressListScrollPane.setViewportView(progressListPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 20, 3, 20);
        jPanel1.add(progressListScrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 20, 0, 20);
        jPanel1.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 462;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.insets = new java.awt.Insets(3, 20, 0, 20);
        jPanel1.add(jSeparator2, gridBagConstraints);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    System.out.println("sure you'd like to stop that - but YOU CAN'T");
    for (TrimmomaticThread t : threads) {
        t.cancel();
    }
    userCancel = true;
    this.setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed


    public boolean cancelledByUser() {
        return userCancel;
    }
private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
    mainGUI.setGrayedOut(false);
    this.setVisible(false);    
}//GEN-LAST:event_continueButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {

//            public void run() {
//                
//                RNASeqDataModel dmodel = new RNASeqDataModel();
//                dmodel.setInputFiles(new ArrayList<FastQFile>() { 
//                    { 
//                        add(new FastQFile("/Users/marc/Desktop/codon/SRR223485.smallsample.fastq"));
////                        add(new FastQFile("/Users/marc/Desktop/codon/SRR223485.sample.fastq"));
////                        add(new FastQFile("/Users/marc/Desktop/fastq/sample80000.fastq"));
////                        add(new FastQFile("/Users/marc/Desktop/fastq/sample4000000.fastq"));
//                        add(new FastQFile("/Volumes/Backup/SOLEXA_PLAYGROUND/AS2.fq"));
//                        
//                        
//                    }
//                });
//                dmodel.setProjectDir(new File("/Users/marc/Desktop/TRIMMOTESTDIR"));                  
//                HashMap<String, String> barcodes = new HashMap<String, String>() {
//                    {
//                        put("ATGTCT", "ATGTCT");
//                        put("AAATT", "AAATT");
//                        put("AACCT", "AACCT");
//                        put("ACATAT", "ACATAT");
//                        put("AGAGTT", "AGAGTT");
//                        put("AGGGT", "AGGGT");
//                        put("CACGAT", "CACGAT");
//                        put("CCACT", "CCACT");
//                        put("CCGGT", "CCGGT");
//                        put("CCTTT", "CCTTT");
//                        put("CGTCTT", "CGTCTT");
//                        put("CTATGT", "CTATGT");
//                        put("GCTAT", "GCTAT");
//                        put("GGATT", "GGATT");
//                        put("GGCCT", "GGCCT");
//                        put("GGTGT", "GGTGT");
//                        put("GTCAGT", "GTCAGT");
//                        put("GTGCAT", "GTGCAT");
//                        put("TACACT", "TACACT");
//                        put("TCCCT", "TCCCT");
//                        put("TGAAT", "TGAAT");
//                        put("TGTGCT", "TGTGCT");
//                        put("TTGGT", "TTGGT");
//                        put("TTTAT", "TTTAT");
//                    }
//                };
//
////                TMTrimmerArguments args2 = new TMTrimmerArguments("BARCODESPLITTER");        
////                args2.put("barcodes", barcodes);
////                args2.put("mism", 1);
////                args2.put("clip", false);
////                
////                dmodel.addTrimmer(args2);
//                dmodel.setQualityEncoding(VERSION.ILLUMINA_1_8);
//                
//                TMTrimmerArguments args = new TMTrimmerArguments("CROP");
//                args.put("len", 20);
//                dmodel.addTrimmer(args);
//                
//                ExecutorService ex = Executors.newFixedThreadPool(4);
//                JFrame frame = new JFrame();
//                frame.setLocation(500, 500);
//                TMProgressDialog dialog = new TMProgressDialog(frame, true, dmodel, ex);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                
//                frame.setVisible(true);
//                dialog.setVisible(true);
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton continueButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel progressListPanel;
    private javax.swing.JScrollPane progressListScrollPane;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables

    public boolean isStopRunningTasks() {
        return stopTasks;
    }

    
}
