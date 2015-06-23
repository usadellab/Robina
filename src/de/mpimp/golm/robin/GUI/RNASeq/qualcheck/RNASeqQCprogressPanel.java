/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RNASeqQCprogressPanel.java
 *
 * Created on 08.09.2011, 14:51:10
 */
package de.mpimp.golm.robin.GUI.RNASeq.qualcheck;

import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQParserThread;
import de.mpimp.golm.robin.rnaseq.parser.FastQSampleParser;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqQualityCheck;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Future;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqQCprogressPanel extends javax.swing.JPanel implements ActionListener, MouseListener {

    private FastQParserThread thread;
    private FastQFile inputFile;
    private Timer timer;
    private Future execStatus;
    private RNASeqQCResultContainerPanel resultFrame;
    private boolean isReady = false;
    private boolean finishedWithErrors = false;
    private ArrayList<Exception> exceptions;
    private RNASeqWorkflowPanel workFlowPanel;
    
    private static final int POLL_INTERVAL = 100;

    /** Creates new form RNASeqQCprogressPanel */
    public RNASeqQCprogressPanel(RNASeqWorkflowPanel sourcePanel, FastQFile f, FastQParserThread t, Future fu) {
        initComponents();
        inputFile = f;
        thread = t;
        thread.setOwner(this);
        fileNameLabel.setText(inputFile.getName());
        timer = new Timer(POLL_INTERVAL, this);
        execStatus = fu;
        timer.start();
        exceptions = new ArrayList<Exception>();
        workFlowPanel = sourcePanel;
        this.addMouseListener(this);
        
        for (Component kid : this.getComponents()) {
            kid.addMouseListener(this);
        }
        
    }

    public boolean isFinishedWithErrors() {
        return finishedWithErrors;
    }

    public FastQFile getInputFile() {
        return inputFile;
    }
    
    private void qualCheckResultsReady() {
        timer.stop();
        progressBar.setValue(100);
        progressBar.setEnabled(false);
        progressBar.setIndeterminate(false);
        
        if (exceptions.size() > 0) {
            statusLabel.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/warnings.png")));
            statusLabel.setToolTipText("Results are ready. But there were errors.");
            statusTextLabel.setText("Finished with errors");
            this.setBackground(RobinConstants.redInactive);
            finishedWithErrors = true;  
            isReady = true;
            //exceptions = thread.getExceptions();
        } else {        
            statusLabel.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/magni26.png")));
            statusLabel.setToolTipText("Results are ready. Click me!");
            statusTextLabel.setText("Finished");
        }
        
        this.thread.getParser().flushMemory();
        this.thread = null;
        this.revalidate();
    }
    
    public void setIndeterminate(boolean val) {
        progressBar.setIndeterminate(val);
    }
    
    public RNASeqQCResultContainerPanel getResultPanel() {
        if (resultFrame == null) {
            generateResultPLots();
        }
        resultFrame.validate();        
        return resultFrame;        
    }    
    
    public synchronized void generateResultPLots() {
        
        resultFrame = new RNASeqQCResultContainerPanel(inputFile, isFinishedWithErrors());
        if (isFinishedWithErrors()) return;
        Dimension size = new Dimension(150, 250); // the standard size for charts
        
        for (RNASeqQualityCheck check : this.thread.getParser().getQualChecks()) {
            JPanel qc_view = check.getResultPanel();
            resultFrame.addQCView(qc_view);
        }
        
        
        if (thread.getParser() instanceof FastQSampleParser) {
            long sampled = ((FastQSampleParser) thread.getParser()).getNumberOfSampledEntries();
            long total = ((FastQSampleParser) thread.getParser()).getNumberOfEntries();
            float perc =(float)(((float)sampled / (total))*100);
            NumberFormat nf = new DecimalFormat("##.##");
            entriesReadLabel.setText("sampled "+sampled+" entries (ca. "+nf.format(perc)+"%)");
        } else {
            entriesReadLabel.setText(thread.getEntriesRead() + " entries read");
        }
        isReady = true;
    }
        
    private void showErrorDialog() {
        StringBuilder excToString = new StringBuilder();
        
        for (Exception e : exceptions) {
            excToString.append(e.getMessage()).append("<br>");
        }        
        VerboseWarningDialog wdialog = new VerboseWarningDialog(null, "Errors parsing input file", inputFile.getName(), excToString.toString(), true);  
    }
    
    public synchronized void addException(Exception e) {
        this.exceptions.add(e);
    }

    public void actionPerformed(ActionEvent ae) {
        try {            
            if (workFlowPanel.isStopRunningTasks()) {
                System.out.println("sending cancel call to "+thread.toString());
                thread.cancel();
            }
            
            if (!execStatus.isDone()) {
                progressBar.setValue(thread.getProgress());
                entriesReadLabel.setText("reading... "+thread.getProgress()+"%");
            } else {                
                qualCheckResultsReady();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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

        statusTextLabel = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        entriesReadLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(2147483647, 60));
        setMinimumSize(new java.awt.Dimension(190, 60));
        setPreferredSize(new java.awt.Dimension(250, 60));
        setRequestFocusEnabled(false);
        setVerifyInputWhenFocusTarget(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        statusTextLabel.setFont(statusTextLabel.getFont().deriveFont((statusTextLabel.getFont().getStyle() | java.awt.Font.ITALIC), statusTextLabel.getFont().getSize()-2));
        statusTextLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqQCprogressPanel.class, "RNASeqQCprogressPanel.statusTextLabel.text")); // NOI18N
        statusTextLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusTextLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        add(statusTextLabel, gridBagConstraints);

        fileNameLabel.setFont(fileNameLabel.getFont().deriveFont(fileNameLabel.getFont().getSize()-2f));
        fileNameLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqQCprogressPanel.class, "RNASeqQCprogressPanel.fileNameLabel.text")); // NOI18N
        fileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileNameLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        add(fileNameLabel, gridBagConstraints);

        progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                progressBarMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = -10;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 3, 4);
        add(progressBar, gridBagConstraints);

        statusLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/alarm_clock_time_wait_icon.png"))); // NOI18N
        statusLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqQCprogressPanel.class, "RNASeqQCprogressPanel.statusLabel.text")); // NOI18N
        statusLabel.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqQCprogressPanel.class, "RNASeqQCprogressPanel.statusLabel.toolTipText")); // NOI18N
        statusLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(statusLabel, gridBagConstraints);

        entriesReadLabel.setFont(entriesReadLabel.getFont().deriveFont((entriesReadLabel.getFont().getStyle() | java.awt.Font.ITALIC), entriesReadLabel.getFont().getSize()-2));
        entriesReadLabel.setForeground(new java.awt.Color(102, 102, 102));
        entriesReadLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqQCprogressPanel.class, "RNASeqQCprogressPanel.entriesReadLabel.text")); // NOI18N
        entriesReadLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                entriesReadLabelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 0);
        add(entriesReadLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void statusLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusLabelMouseClicked
    if (!isReady) return;
    if (finishedWithErrors) {// && (evt != null)) {
        showErrorDialog();
    } else {
        resultFrame.setVisible(true);
        workFlowPanel.RSQCResultViewScrollPane.add(resultFrame);
        workFlowPanel.RSQCResultViewScrollPane.setViewportView(resultFrame);
    }
}//GEN-LAST:event_statusLabelMouseClicked

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    statusLabelMouseClicked(null);
}//GEN-LAST:event_formMouseClicked

private void progressBarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressBarMouseClicked
    statusLabelMouseClicked(null);
}//GEN-LAST:event_progressBarMouseClicked

private void statusTextLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statusTextLabelMouseClicked
    statusLabelMouseClicked(null);
}//GEN-LAST:event_statusTextLabelMouseClicked

private void fileNameLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileNameLabelMouseClicked
    statusLabelMouseClicked(null);
}//GEN-LAST:event_fileNameLabelMouseClicked

private void entriesReadLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_entriesReadLabelMouseClicked
    statusLabelMouseClicked(null);
}//GEN-LAST:event_entriesReadLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel entriesReadLabel;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusTextLabel;
    // End of variables declaration//GEN-END:variables
    
    
    public void setActive() {
        if (finishedWithErrors) {
            this.setBackground(RobinConstants.redActive);
        } else {
            this.setBackground(RobinConstants.LIGHT_BLUE);
        }
    }
    
    public void setInactive() {
        if (finishedWithErrors) {
            this.setBackground(RobinConstants.redInactive);
        } else {
            this.setBackground(Color.white);
        }
    }
    
    
    public void mouseClicked(MouseEvent e) {
        statusLabelMouseClicked(null);
    }

    public void mouseEntered(MouseEvent e) {        
        if (this.contains(e.getPoint())) {
            this.setActive();
        }    
        e.consume();
    }

    public void mouseExited(MouseEvent e) {        
        if (!this.contains(e.getPoint())) {
            this.setInactive();
        }    
        e.consume();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}


}
