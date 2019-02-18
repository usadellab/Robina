/*
 * RobinMainGUI.java
 *
 * Created on 6. Mai 2008, 15:13
 */
package de.mpimp.golm.robin.GUI;

import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.GUI.affy.AffyWorkflowPanel;
import de.mpimp.golm.robin.GUI.singlechannel.SCWorkflowPanel;
import de.mpimp.golm.robin.GUI.twocolor.TCWorkflowPanel;
import de.mpimp.golm.robin.ProjectType;
import de.mpimp.golm.robin.misc.update.UpdateChecker;
import de.mpimp.golm.common.gui.SimpleErrorMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import com.sun.jna.Library;
import com.sun.jna.Native;
import de.mpimp.golm.common.gui.CollapsibleInfoDialog;
import de.mpimp.golm.common.gui.FixedGlassPane;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.memory.MemoryWarningSystem;
import de.mpimp.golm.common.pdf.viewer.PDFViewerWindow;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;

import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.awt.Color;
import javax.swing.SwingWorker;
import org.gabipd.mapman.commserver.CommunicationServer;
import org.openide.util.Exceptions;


/**
 *
 * @author  marc
 */

/**FIXME 
 * for running !!!!!!!!!!!!!!!!!! add back installpath!
 *
 */

public class RobinMainGUI extends javax.swing.JFrame implements WindowListener {

    private String installPath;
    private AtomicBoolean shutDownComplete;
    public javax.swing.Timer busyIconTimer;
    public Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JFileChooser fileChooser = new JFileChooser();
    private String sPathToR;
    private String sRunR;
    //private String              sEnvForGS;
    private Timer t = null;
    public Properties defaultSettings = new Properties();
    private ExecutorService executor;
    private boolean showReleaseNotes = true;
    // mapman and robin's secret walkie talkie
    private CommunicationServer commServer;
    // constants
    private static final String CHOOSEANALYSISPANEL = "card9";
    private static final String sUserDir = System.getProperty("user.dir");
    private static final String sUserHome = System.getProperty("user.home");
    private static final String sUserTemp = System.getProperty("java.io.tmpdir");
    private static final String sFileSep = System.getProperty("file.separator");
    private RobinWorkflow workflow;
    private boolean configOK = false;
    private FixedGlassPane glassPane;
    private PDFViewerWindow manualViewer;

    /** Creates new form RobinMainGUI */
    public RobinMainGUI() {

        checkPlatform();
        initComponents();
        customInitComponents();
        checkForOldConfigurationFile();
        if (!configOK) {
            checkEnvironment();
        }

        //Deactivated for now...
        //checkForUpdates();
        //System.out.println("------>"+defaultSettings.toString());

        this.addWindowListener(this);

        /*
         * Set up the memory warning system to gracefully 
         * die when running out of memory
         */

        MemoryWarningSystem.setPercentageUsageThreshold(0.9);
        MemoryWarningSystem mws = new MemoryWarningSystem();

        mws.addListener(new MemoryWarningSystem.Listener() {

            public void memoryUsageLow(long usedMemory, long maxMemory) {

                JOptionPane.showMessageDialog(null,
                        "RobiNA is running out of memory and might become\n"
                        + "unstable or freeze completely. We stronly recommend\n"
                        + "closing the program an re-running it after configuring\n"
                        + "it to use more memory if your computer supports that.\n"
                        + "Please see the FAQ section of the manual for detailed\n"
                        + "instructions.",
                        "Low memory warning",
                        JOptionPane.WARNING_MESSAGE);

                double percentageUsed = ((double) usedMemory) / maxMemory;
                SimpleLogger.getLogger(true).logMessage("Memory low! percentageUsed = " + percentageUsed);
                MemoryWarningSystem.setPercentageUsageThreshold(0.95);
            }
        });

        /*
         * Register a VM shutdown hook to handle apple-Q events on macs...
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                
                if (RobinMainGUI.this.shutDownComplete.get()) {
                    //DEBUG
                    System.out.println("window closing handler has already wrapped up");
                    return;
                }
                //DEBUG
                System.out.println("user quit shutdown hook triggered.");
                RobinMainGUI.this.userQuitRobin(false);
                return;
            }
        });
    }

    public void setStatusText(String text) {
        stepLabel.setText(text);
    }

    public void setProgressLabelText(String text) {
        progressLabel.setText(text);
    }

    public void showReleaseNotes() {
        if (showReleaseNotes) {
            new RobinReleaseNotesDialog(this, true);
        }
    }

    public boolean isShowReleaseNotes() {
        return showReleaseNotes;
    }

    public void setShowReleaseNotes(boolean showReleaseNotes) {
        this.showReleaseNotes = showReleaseNotes;
        defaultSettings.setProperty("showReleaseNotes", Boolean.toString(this.showReleaseNotes));
    }

    public void checkForUpdates() {

        UpdateChecker checker = new UpdateChecker(this);
        Properties build = new Properties();
        int buildnumber = 0;
        Date lastCheck = null, current;
        GregorianCalendar cal = new GregorianCalendar();
        current = cal.getTime();
        SimpleDateFormat sdfToDate = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm:ss");

        InputStream is = (InputStream) getClass().getResourceAsStream("/de/mpimp/golm/robin/build.number");
        try {
            build.load(is);
            buildnumber = Integer.parseInt(build.getProperty("build.number"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //DEBUG
        System.out.println("INSTALLED BUILD NUMBER IS : " + buildnumber);

        if (defaultSettings.getProperty("last.update.check") != null) {

            // read last check date from settings file
            try {
                lastCheck = sdfToDate.parse(defaultSettings.getProperty("last.update.check"));
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }

            //DEBUG
            System.out.println("LAST CHECK:" + lastCheck.toString());

            // add seven days to lastCheck
            cal.setTime(lastCheck);

            //BUCKWHEAT

            cal.add(Calendar.MINUTE,
                    Integer.parseInt(defaultSettings.getProperty("update.interval")));

            if (current.after(cal.getTime())) {
                checker.checkForUpdates(buildnumber);
            } else {

                System.out.println("LAST CHECK LESS THAN "
                        + defaultSettings.getProperty("update.interval")
                        + " DAYS AGO: "
                        + lastCheck.toString());
                return;
            }
        } else {

            // first time checking
            checker.checkForUpdates(buildnumber);
            defaultSettings.setProperty("update.interval", "7");
            defaultSettings.setProperty("last.update.check", sdfToDate.format(current));
        }
    }

    public void customInitComponents() {
        // initialize shutdown flag
        shutDownComplete = new AtomicBoolean(false);

        // initialize the status animation
        int busyAnimationRate = 30;
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/busyicons/busy-icon" + i + ".png"));
        }
        busyIconTimer = new javax.swing.Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                progressIconLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

        idleIcon = new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/busyicons/idle-icon.png"));
        progressIconLabel.setIcon(idleIcon);

        stepLabel.setText("Welcome to RobiNA");
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension framesize = this.getSize();

        int xCenterPos = (screensize.width / 2) - (framesize.width / 2);
        int yCenterPos = (screensize.height / 2) - (framesize.height / 2);

        this.setLocation(xCenterPos, yCenterPos);

        //TODO these buttons must remain hidden until we implemented the functionality....
        DGEButton.setVisible(true);
        TilingArrayButton.setVisible(false);
        settingsButton.setVisible(false);

        // initialize the secret walkie talkie
        commServer = new CommunicationServer();
        
        // manual viewer        
        // switch off image scaling to improve display quality
        System.getProperties().put("org.icepdf.core.scaleImages", "false");
        InputStream stream;
        stream = getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/help/manual/robina_manual.pdf");
        try {
            manualViewer = new PDFViewerWindow(this, false, stream);
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        manualViewer.setTitle("Robin and RobiNA manual");


        // glass Pane
        glassPane = new FixedGlassPane(null, this);
        glassPane.setOpaque(true);
        glassPane.setBackground(new Color(0, 0, 0, 100));
        this.setGlassPane(glassPane);

        mainPanel.setVisible(true);
        this.repaint();
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    public void setGrayedOut(final boolean state) {

        // i want the glass pane to fade in and out....
       
        glassPane.setVisible(state);
        
//        boolean fadeOut = !state;
//        
//        Timer fadeTimer = new Timer(100, null);
//        fadeTimer.removeActionListener(null);
//        fadeTimer.addActionListener(new FadeTimerActionListener( glassPane, fadeOut, fadeTimer ));
//        fadeTimer.setRepeats(false);
//        fadeTimer.start();
    }

    public String getInstallPath() {
        return installPath;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void reInitComponents() {
        mainPanel.removeAll();
        initComponents();
        mainPanel.revalidate();
        mainPanel.repaint();
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

        qcResultListItem = new javax.swing.JPanel();
        thunbnail = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jCheckBox1 = new javax.swing.JCheckBox();
        statusBar = new javax.swing.JPanel();
        stepLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();
        progressIconLabel = new javax.swing.JLabel();
        progressLabel = new javax.swing.JLabel();
        settingsButton = new javax.swing.JButton();
        manualButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        chooseAnalysisTypePanel = new javax.swing.JPanel();
        affyButton = new javax.swing.JButton();
        twoColorButton = new javax.swing.JButton();
        DGEButton = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        genericSCButton = new javax.swing.JButton();
        TilingArrayButton = new javax.swing.JButton();

        qcResultListItem.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane5.setBorder(null);

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        jScrollPane5.setViewportView(descriptionTextArea);

        jCheckBox1.setText("Exclude?");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout qcResultListItemLayout = new org.jdesktop.layout.GroupLayout(qcResultListItem);
        qcResultListItem.setLayout(qcResultListItemLayout);
        qcResultListItemLayout.setHorizontalGroup(
            qcResultListItemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcResultListItemLayout.createSequentialGroup()
                .add(thunbnail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox1)
                .addContainerGap())
        );
        qcResultListItemLayout.setVerticalGroup(
            qcResultListItemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcResultListItemLayout.createSequentialGroup()
                .add(29, 29, 29)
                .add(jCheckBox1)
                .addContainerGap(33, Short.MAX_VALUE))
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
            .add(thunbnail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RobiNA - The transcriptomics data preprocessor. Version "+this.getVersionString());
        getContentPane().setLayout(new java.awt.GridBagLayout());

        statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusBar.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        statusBar.setPreferredSize(new java.awt.Dimension(900, 40));
        statusBar.setLayout(new java.awt.GridBagLayout());

        stepLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        statusBar.add(stepLabel, gridBagConstraints);

        progressIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/busyicons/idle-icon.png"))); // NOI18N

        progressLabel.setText("idle");

        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/settings_20.png"))); // NOI18N

        manualButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/manual_24.png"))); // NOI18N
        manualButton.setText("Manual");
        manualButton.setBorderPainted(false);
        manualButton.setContentAreaFilled(false);
        manualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout progressPanelLayout = new org.jdesktop.layout.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(progressPanelLayout.createSequentialGroup()
                .add(0, 655, Short.MAX_VALUE)
                .add(progressIconLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(26, 26, 26)
                .add(settingsButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(manualButton)
                .addContainerGap())
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(progressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                .add(progressIconLabel)
                .add(progressLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(settingsButton)
                .add(manualButton))
        );

        progressPanelLayout.linkSize(new java.awt.Component[] {progressIconLabel, progressLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        statusBar.add(progressPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(statusBar, gridBagConstraints);

        mainPanel.setMinimumSize(new java.awt.Dimension(980, 650));
        mainPanel.setPreferredSize(new java.awt.Dimension(980, 650));
        mainPanel.setRequestFocusEnabled(false);
        mainPanel.setLayout(new java.awt.GridBagLayout());

        chooseAnalysisTypePanel.setBackground(new java.awt.Color(214, 232, 255));
        chooseAnalysisTypePanel.setMinimumSize(new java.awt.Dimension(980, 650));
        chooseAnalysisTypePanel.setPreferredSize(new java.awt.Dimension(980, 650));
        chooseAnalysisTypePanel.setLayout(new java.awt.GridBagLayout());

        affyButton.setBackground(new java.awt.Color(255, 255, 255));
        affyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/affymetrix_genechip.jpg"))); // NOI18N
        affyButton.setText("<html><h1>Affymetrix GeneChip<sup>&reg</sup><br>microarray experiment</h1>\n</html>");
        affyButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        affyButton.setIconTextGap(10);
        affyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                affyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 142;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(18, 68, 0, 0);
        chooseAnalysisTypePanel.add(affyButton, gridBagConstraints);

        twoColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/two_col.png"))); // NOI18N
        twoColorButton.setText("<html><h1>Two-color microarray<br>experiment</h1></html>");
        twoColorButton.setAutoscrolls(true);
        twoColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        twoColorButton.setIconTextGap(10);
        twoColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 159;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(18, 68, 0, 0);
        chooseAnalysisTypePanel.add(twoColorButton, gridBagConstraints);

        DGEButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/dge_mat90.png"))); // NOI18N
        DGEButton.setText("<html><h1>RNA Sequencing <br>experiment</h1>e.g. RNA-Seq using Illumina sequencing</html>");
        DGEButton.setToolTipText("<html>This module will support differential gene expression analyses<br>\nbased on deep sequencing data. It is currently still under construction<br>\nbut will be included in the first update of Robin</html>");
        DGEButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        DGEButton.setIconTextGap(10);
        DGEButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DGEButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 161;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 26, 0, 0);
        chooseAnalysisTypePanel.add(DGEButton, gridBagConstraints);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("<html><h1>Please choose the type of experiment you want to analyze:</h1></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 807;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 34);
        chooseAnalysisTypePanel.add(jLabel30, gridBagConstraints);

        genericSCButton.setBackground(new java.awt.Color(255, 255, 255));
        genericSCButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/one_col.png"))); // NOI18N
        genericSCButton.setText("<html><h1>Generic single channel<br>microarray experiment</h1>\n<small>e.g. Agilent and custom microarrays etc.</small>\n</html>");
        genericSCButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        genericSCButton.setIconTextGap(10);
        genericSCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericSCButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 159;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 26, 0, 0);
        chooseAnalysisTypePanel.add(genericSCButton, gridBagConstraints);

        TilingArrayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/dge_mat90.png"))); // NOI18N
        TilingArrayButton.setText("<html><h1>Tiling array<br>experiment</h1></html>");
        TilingArrayButton.setToolTipText("<html>This module will support tiling arrays<br>It is currently still under construction<br>\nbut will be included in the first update of Robin</html>");
        TilingArrayButton.setEnabled(false);
        TilingArrayButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TilingArrayButton.setIconTextGap(10);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 155;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(18, 26, 0, 0);
        chooseAnalysisTypePanel.add(TilingArrayButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(chooseAnalysisTypePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(mainPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jCheckBox1ActionPerformed

    public Properties getDefaultSettings() {
        return defaultSettings;
    }

private void affyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_affyButtonActionPerformed
    System.out.println("affymetrix analysis");
    File projectPath = null;

    // set uip the project directory
    SetupProjectDirectoryDialog setupDialog = new SetupProjectDirectoryDialog(this, true, ProjectType.TYPE.AFFYMETRIX);
    setupDialog.setVisible(true);

    if (setupDialog.getUserChoice() != SetupProjectDirectoryDialog.response.ACCEPT_RESPONSE) {
        setupDialog.dispose();
        return;
    }
    projectPath = setupDialog.getProjectPath();
    if (projectPath == null) {
        return;
    }

    //FIXME write a hidden type file - should be done using a project file
    File sourcePath = new File(projectPath, "source");
    File typeFile = new File(sourcePath, "affy.type");
    try {
        typeFile.createNewFile();
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }

    mainPanel.setVisible(false);
    AffyWorkflowPanel affyMain = new AffyWorkflowPanel(this, projectPath);

    if (setupDialog.isImporting()) {

        DefaultListModel listModel = new DefaultListModel();
        for (String importFile : setupDialog.getImportFiles()) {
            listModel.addElement(importFile);
        }
        affyMain.setImportMode(setupDialog.getImportProjectFile(), listModel);
    }

    runWorkFlow(affyMain);
}//GEN-LAST:event_affyButtonActionPerformed

private void twoColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoColorButtonActionPerformed
    System.out.println("two color analysis");
    File projectPath = null;
    // set uip the project directory
    SetupProjectDirectoryDialog setupDialog = new SetupProjectDirectoryDialog(this, true, ProjectType.TYPE.TWO_COLOR);
    setupDialog.setVisible(true);

    if (setupDialog.getUserChoice() != SetupProjectDirectoryDialog.response.ACCEPT_RESPONSE) {
        setupDialog.dispose();
        return;
    }
    projectPath = setupDialog.getProjectPath();
    if (projectPath == null) {
        return;
    }


    //FIXME write a hidden type file - should be done using a project file
    File sourcePath = new File(projectPath, "source");
    File typeFile = new File(sourcePath, "generic_tc.type");
    try {
        typeFile.createNewFile();
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }

    mainPanel.setVisible(false);
    TCWorkflowPanel tcmain = new TCWorkflowPanel(this, projectPath);
    if (setupDialog.isImporting()) {
        DefaultListModel listModel = new DefaultListModel();
        for (String importFile : setupDialog.getImportFiles()) {
            listModel.addElement(importFile);
        }
        tcmain.setImportMode(setupDialog.getImportProjectFile(), listModel);
    }

    runWorkFlow(tcmain);
}//GEN-LAST:event_twoColorButtonActionPerformed

private void genericSCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericSCButtonActionPerformed
    System.out.println("single channel analysis");

    File projectPath = null;
    // set uip the project directory
    SetupProjectDirectoryDialog setupDialog = new SetupProjectDirectoryDialog(this, true, ProjectType.TYPE.SINGLE_CHANNEL);
    setupDialog.setVisible(true);

    if (setupDialog.getUserChoice() != SetupProjectDirectoryDialog.response.ACCEPT_RESPONSE) {
        setupDialog.dispose();
        return;
    }
    projectPath = setupDialog.getProjectPath();
    if (projectPath == null) {
        return;
    }

    //FIXME write a hidden type file - should be done using a project file
    File sourcePath = new File(projectPath, "source");
    File typeFile = new File(sourcePath, "generic_sc.type");
    try {
        typeFile.createNewFile();
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }

    mainPanel.setVisible(false);
    SCWorkflowPanel scmain = new SCWorkflowPanel(this, projectPath);
    if (setupDialog.isImporting()) {
        DefaultListModel listModel = new DefaultListModel();
        for (String importFile : setupDialog.getImportFiles()) {
            listModel.addElement(importFile);
        }
        scmain.setImportMode(setupDialog.getImportProjectFile(), listModel);
    }
    runWorkFlow(scmain);
}//GEN-LAST:event_genericSCButtonActionPerformed

private void DGEButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DGEButtonActionPerformed
    System.out.println("RNA-Seq analysis");


    // set uip the project directory
    final SetupProjectDirectoryDialog setupDialog = new SetupProjectDirectoryDialog(this, true, ProjectType.TYPE.RNA_SEQ);
    setupDialog.setVisible(true);

    if (setupDialog.getUserChoice() != SetupProjectDirectoryDialog.response.ACCEPT_RESPONSE) {
        setupDialog.dispose();
        return;
    }
    final File projectPath = setupDialog.getProjectPath();
    if (projectPath == null) {
        return;
    }

    //FIXME write a hidden type file - should be done using a project file
    File sourcePath = new File(projectPath, "source");
    File typeFile = new File(sourcePath, "rna_seq.type");
    try {
        typeFile.createNewFile();
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }

    final ProgressDialog progDia = new ProgressDialog(this, true, true);
    progDia.setIndeterminate(true);
    progDia.setText("Setting up workflow");
    mainPanel.setVisible(false);
    statusBar.setVisible(false);

    SwingWorker<RNASeqWorkflowPanel, Object> setupWorker = new SwingWorker<RNASeqWorkflowPanel, Object>() {

        @Override
        protected RNASeqWorkflowPanel doInBackground() throws Exception {
            RNASeqWorkflowPanel rna_main = new RNASeqWorkflowPanel(RobinMainGUI.this, projectPath);
            if (setupDialog.isImporting()) {
                rna_main.setImportMode(setupDialog.getImportProjectFile(), new DefaultListModel());
            }
            return rna_main;
        }

        @Override
        protected void done() {
            try {
                workflow = get();
                progDia.dispose();
//                RobinMainGUI.this.setGrayedOut(false);
                runWorkFlow(workflow);
            } catch (InterruptedException ex) {
                SimpleLogger.getLogger(true).logException(ex);
            } catch (ExecutionException ex) {
                SimpleLogger.getLogger(true).logException(ex);

            }
        }
    };

    setupWorker.execute();

    progDia.setVisible(true);
}//GEN-LAST:event_DGEButtonActionPerformed

    private void manualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualButtonActionPerformed
        this.manualViewer.setVisible(true);
    }//GEN-LAST:event_manualButtonActionPerformed

    private void runWorkFlow(RobinWorkflow workFlow) {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(workFlow, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE).add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup().add(workFlow, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(statusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        this.repaint();
        workFlow.revalidate();
        statusBar.setVisible(true);
        workFlow.setVisible(true);
    }

    public void simulateWorkflow(RobinWorkflow workflow, String panel) {
        workflow.simulatePanel(panel);
        mainPanel.setVisible(false);
        runWorkFlow(workflow);
    }

    public void startBusyAnimation(String statusMsg) {
        this.busyIconTimer.start();
        this.progressLabel.setText(statusMsg);
    }

    public void stopBusyAnimation() {
        this.busyIconTimer.stop();
        this.progressLabel.setText("idle");
        this.progressIconLabel.setIcon(idleIcon);
    }

    private void checkPlatform() {
        // Which platform are we running on?  

        System.out.println("isWindows:" + org.openide.util.Utilities.isWindows());
        System.out.println("isMac:" + org.openide.util.Utilities.isMac());
        System.out.println("isUnix:" + org.openide.util.Utilities.isUnix());
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Robin");
        // set the system look and feel
        //setSystemLAF();
        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

        // set the Quaqua Look and Feel in the UIManager
        try {
            UIManager.setLookAndFeel(
                    "ch.randelshofer.quaqua.QuaquaLookAndFeel");
            // set UI manager properties here that affect Quaqua

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Utilities.isWindows() || Utilities.isLinux()) {
            Utilities.setSystemLAF();
        }
    }

    private void checkEnvironment() {

        String sep = System.getProperty("file.separator");

        defaultSettings.setProperty("version", this.getVersionString());

        if (org.openide.util.Utilities.isWindows()) {
            defaultSettings.setProperty("PathToR", "\"" + installPath + "R" + sep + "bin" + sep);
            defaultSettings.setProperty("CommandToRunR", "R.exe" + "\"");
            defaultSettings.setProperty("ArgsToR", "--slave|--vanilla|--file=");
            System.out.println("Win detected");

        }
        if (org.openide.util.Utilities.isMac()) {

            //DEBUG
            final String runRmode = "embedded";

            // installed R            
            if (runRmode.equals("test")) {
                defaultSettings.setProperty("PathToR", "/Library/Frameworks/R.framework/Resources/bin/");
            }

            // embedded R external version
            if (runRmode.equals("external")) {
                defaultSettings.setProperty("PathToR", "/Users/marc/Desktop/R_embedded_maci386_leo/bin/");
            }

            //real embedded R
            if (runRmode.equals("embedded")) {
                defaultSettings.setProperty("PathToR", installPath + "R/bin/");
            }

            // just for testing manual R setup
            if (runRmode.equals("manual")) {
                defaultSettings.setProperty("PathToR", "/usr/bin/kokok");
            }

            System.out.println("RUN_MODE:" + runRmode);

            defaultSettings.setProperty("CommandToRunR", "R");
            defaultSettings.setProperty("ArgsToR", "--slave|--vanilla|--file=");

            // setup the paths in the embedded R's start script to point to
            // the current base dir of the embedded R. This should be done
            // every time Robin is started because the user might have moved
            // the app bundle around
            if (runRmode.equals("embedded")) {
                // first, load the R script
                String RSTART = Utilities.loadString(defaultSettings.getProperty("PathToR") + ".R_START_TEMPLATE");
                RSTART = RSTART.replaceAll("__R_HOME_PATH__", installPath + "R/lib/R");

                // now (over)write the R script with this string
                try {
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(
                            new FileOutputStream(defaultSettings.getProperty("PathToR") + "R"), Charset.forName("UTF-8")));
                    out.write(RSTART);
                    out.close();
                    CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);
                    libc.chmod(defaultSettings.getProperty("PathToR") + "R", 0755);
                    
                    BufferedWriter outLibBin = new BufferedWriter(
                            new OutputStreamWriter(
                            new FileOutputStream(installPath + "R/lib/R/bin/R"), Charset.forName("UTF-8")));
                    outLibBin.write(RSTART);
                    outLibBin.close();                    
                    libc.chmod(installPath + "R/lib/R/bin/R", 0755);
                    

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Mac detected");

        }
        if (Utilities.isLinux()) { // sOS.substring(0,3).equalsIgnoreCase("lin")
            System.out.println("Linux detected");

            defaultSettings.setProperty("PathToR", installPath + "R/bin/");
            defaultSettings.setProperty("CommandToRunR", "R");
            defaultSettings.setProperty("ArgsToR", "--slave|--vanilla|--file=");
            String RSTART = Utilities.loadString(defaultSettings.getProperty("PathToR") + "/.R_START_TEMPLATE");
            RSTART = RSTART.replaceAll("__R_HOME_PATH__", installPath + "R/lib/R");
            //System.out.println(RSTART);

            // now (over)write the R script with this string
//            try {
//                //FIXME we need to OVERWRITE here no matter if the file exists or not!
//                // on a mac it seems to do that by default.....
//                BufferedWriter outBin = new BufferedWriter(
//                        new OutputStreamWriter(
//                        new FileOutputStream(defaultSettings.getProperty("PathToR") + "/R"), Charset.forName("UTF-8")));
//                outBin.write(RSTART);
//                outBin.close(); 
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }

        if (!Utilities.isMacOSX()) {
            if (!configOK) {
                checkR("print (\"Hello Robin\", quote=F)");
            }
        }
        defaultSettings.setProperty("failed.R.check", "false");
    }

    private void checkForOldConfigurationFile() {

        // this will throw an error when in test mode on a mac
        if (Utilities.isWindows()) {
            installPath = Utilities.getInstallPath(this, "Robin2.exe");
            /**FIXME **/ 
            if (installPath == "") { 
            installPath="C:\\Program Files\\RobiNA";}
            
        } else {
            installPath = Utilities.getInstallPath(this, "Robin2.jar");
        }
        SimpleLogger.getLogger(true).logMessage("installPath"+installPath);
        File confFile = new java.io.File(this.getResourcePath(), "robin.conf");
        SimpleLogger.getLogger(true).logMessage(confFile.getAbsolutePath());
        // do we already have a config file?
        if (confFile.exists()) {
            try {
                defaultSettings.load(new FileInputStream(confFile));
            } catch (FileNotFoundException ex) {
                SimpleLogger.getLogger(true).logException(ex);
            } catch (IOException ioex) {
                SimpleLogger.getLogger(true).logException(ioex);
            }

            String failedRcheck = defaultSettings.getProperty("failed.R.check");
            SimpleLogger.getLogger(true).logMessage("FAILED_R_CHECK:" + failedRcheck);

            if (failedRcheck != null && failedRcheck.equals("true")) {
                new SimpleErrorMessage(this, "The R engine check failed last time you started Robin, meaning that\n"
                        + "your installation of Robin probably is in an inconsistent state.\n"
                        + "Robin will now run the R engine check again. If the problem persists\n"
                        + "please contact lohse@mpimp-golm.mpg.de");
                //if (!Utilities.isMacOSX()) {
                checkR("print (\"Hello Robin\", quote=F)");
                //}
                defaultSettings.setProperty("failed.R.check", "false");
                configOK = true;
            }

            String currentVersion = this.getVersionString();//org.openide.util.NbBundle.getMessage(RobinMainGUI.class, "Robin.version");

            SimpleLogger.getLogger(true).logMessage("current version:" + currentVersion);
            SimpleLogger.getLogger(true).logMessage("config version:" + defaultSettings.getProperty("version"));

            if (!currentVersion.equals(defaultSettings.getProperty("version"))) {

                //check whether there's an outdated robin_conf
                SimpleLogger.getLogger(true).logMessage("found outdated config file. config version is:"
                        + defaultSettings.getProperty("version")
                        + "\ncurrent version is:" + currentVersion
                        + "\nDeleting old conf.");
                confFile.delete();
                return;

//                if (!Utilities.isMacOSX()) {
//                    checkR("print (\"Hello Robin\", quote=F)");
//                }
//                defaultSettings.setProperty("failed.R.check", "false");
            }

            System.out.println(defaultSettings.values().toString());
            //sPathToR = defaultSettings.getProperty("PathToR").replaceAll("\\\\", "\\\\\\\\");
            sPathToR = defaultSettings.getProperty("PathToR").replaceAll("\\\\", "/");
            sRunR = defaultSettings.getProperty("CommandToRunR");

            if (defaultSettings.getProperty("showReleaseNotes") == null) {
                showReleaseNotes = true;
            } else {
                showReleaseNotes = Boolean.parseBoolean(defaultSettings.getProperty("showReleaseNotes"));
            }
            configOK = true;

        } else {
            return;
        }

    }

    private void checkR(String testCode) {
        // checK whether R is where it's supposed to be

        File testScript = null;
        try {
            testScript = File.createTempFile("robin_", "_test.R");
            FileWriter out = new FileWriter(testScript);
            out.write(testCode);
            out.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(new Exception("File: " + testScript, ex));            
        }

        final RTask testR = new RTask(defaultSettings.getProperty("PathToR"),
                defaultSettings.getProperty("CommandToRunR"),
                defaultSettings.getProperty("ArgsToR"), testScript);

        // deactivate all import panel controls
        // and tell the user what we're doing

        busyIconTimer.start();
        progressLabel.setText("checking R engine...");

        executor = Executors.newFixedThreadPool(1);
        executor.execute(testR);
        executor.shutdown();

        ActionListener l = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (executor.isTerminated()) {
                    t.stop();
                    t.removeActionListener(this);
                    System.out.println("retval:" + testR.getExitValue() + "\nmesg:" + testR.getOutputMessage());
                    if (testR.getExitValue() != 0) {
                        new SimpleErrorMessage(null, "You are probably starting Robin for the first time - to begin using Robin\n"
                                + "you'll first have to specify where to find the R engine that you want to use.\n\n"
                                + "If you are not starting for the first time the R engine is either broken or not\n"
                                + "found at the specified path\n\n"
                                + testR.getOutputMessage() + "\n\n"
                                + "Please specify a valid path to the 'bin' directory of an R installation.\n"
                                + "E.g. /usr/local/R/bin");
                        manuallySetupR();
                        return;

                    } else if (!testR.getOutputMessage().equals("[1] Hello Robin\n")) {
                        new SimpleErrorMessage(null, "The path to R is correct, but R is not working correctly\noutput:"
                                + testR.getOutputMessage() + "\n"
                                + "Please specify valid path to the 'bin' directory of an R installation.\n"
                                + "E.g. /usr/local/R/bin");
                        manuallySetupR();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(RobinMainGUI.this, "Found working R binary at:\n"
                                + defaultSettings.getProperty("PathToR") + "\n"
                                + "Will now check libraries and try to download and install missing\n"
                                + "required R packages. This requires an internet connection,\n"
                                + "might take quite a while and possibly also requires root privileges...\n"
                                + "please be patient...",
                                "R setup successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        // save the path in the robin.conf file
                        try {
                            File confFile = new java.io.File(RobinMainGUI.this.getResourcePath(), "robin.conf");
                            confFile.createNewFile();
                            java.io.FileOutputStream out = new java.io.FileOutputStream(confFile);

                            // DEBUG
                            System.out.println("saving R path=" + defaultSettings.getProperty("PathToR"));

                            // save the version to be able to check after an update whether the found
                            // config file belongs to an older version


                            defaultSettings.store(out, "Robin settings");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                        System.out.println(defaultSettings.values().toString());
                        //sPathToR = defaultSettings.getProperty("PathToR").replaceAll("\\\\", "\\\\\\\\");
                        sPathToR = defaultSettings.getProperty("PathToR").replaceAll("\\\\", "/");
                        sRunR = defaultSettings.getProperty("CommandToRunR");


                        /* this migt have to be deactivated, because it seems to lead to
                         * problems if impatient people with a slow internet
                         * connection are using Robin. It also causes update
                         * downloads of already present packages, which is NOT
                         * what i want.*/
                        progressLabel.setText("setting up R...");
                        setupRpackages();
                        return;
                    }
                }
            }
        };

        t = new Timer(100, l);
        t.start();

    }

    private void manuallySetupR() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);

        String chosenRpath = null;

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                chosenRpath = fc.getSelectedFile().getCanonicalPath() + sFileSep;
            } catch (IOException ex) {
                SimpleLogger.getLogger(true).logException(ex);
            }
        } else {
            new SimpleErrorMessage(this, "No valid R engine path specified. Exiting.");
            System.exit(1);
        }

        System.out.println("chosenPath=" + chosenRpath);
        defaultSettings.setProperty("PathToR", chosenRpath);

        System.out.println("new defaul R path = " + defaultSettings.getProperty("PathToR"));

        // check the entered path
        checkR("print (\"Hello Robin\", quote=F)");
        return;
    }

    private void setupRpackages() {
        // if this is pointing to a working R let's see if we have all needed packages...

        final ProgressDialog dial = new ProgressDialog(this, true, true);
        dial.setIndeterminate(true);
        dial.setText("Checking whether your R installation meets all requirements...");

        SwingWorker<RTask, Object> testRWorker = new SwingWorker<RTask, Object>() {

            @Override
            protected RTask doInBackground() throws Exception {
                String packageCheckScript = Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/lib/checkAndDownloadPackages.R"));
                File tmpPath = null;
                try {
                    tmpPath = File.createTempFile("robin_", "_checkndowntest.R");
                    FileWriter out = new FileWriter(tmpPath);
                    out.write(packageCheckScript);
                    out.close();
                } catch (IOException ex) {
                    SimpleLogger.getLogger(true).logException(
                            new Exception(RobinMainGUI.this.getTitle() + "\n" + ex.toString() + "\nFile: " + tmpPath, ex));
                }
                RTask testR = new RTask(defaultSettings.getProperty("PathToR"),
                        defaultSettings.getProperty("CommandToRunR"),
                        defaultSettings.getProperty("ArgsToR"), tmpPath);
                testR.start();

                while (testR.isAlive()) {
                    Thread.sleep(1000);
                }
                return testR;
            }

            @Override
            protected void done() {
                RTask testR = null;
                try {
                    testR = get();
                } catch (InterruptedException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                } catch (ExecutionException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                }
                if (testR.getExitValue() != 0) {
                    new SimpleErrorMessage(null, "There was an error trying to download and install\n"
                            + "required R packages. Please check whether your internet\n"
                            + "connection is working.\n");
                    defaultSettings.setProperty("failed.R.check", "true");
                    userQuitRobin(true);
                }

                // display warning
                if (testR.hasWarning()) {
                    JOptionPane.showMessageDialog(RobinMainGUI.this,
                            testR.getWarningMessage(),
                            "R version mismatch",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    dial.dispose();
                    JOptionPane.showMessageDialog(RobinMainGUI.this,
                            "Your installation of R seems to meet all requirements",
                            "R successfully set up",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                dial.dispose();
                busyIconTimer.stop();
                progressLabel.setText("idle");
                progressIconLabel.setIcon(idleIcon);

                //enableImportControls(true);
                return;

            }
        };

        testRWorker.execute();
        dial.setVisible(true);


//        ActionListener ll = new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                if (!testR.isAlive()) {
//                    t.stop();
//                    t.removeActionListener(this);
//                    if (testR.getExitValue() != 0) {
//                        new SimpleErrorMessage(null, "There was an error trying to download and install\n"
//                                + "required R packages. Please check whether your internet\n"
//                                + "connection is working.\n");
//                        defaultSettings.setProperty("failed.R.check", "true");
//                        userQuitRobin(true);
//                    }
//
//                    // display warning
//                    if (testR.hasWarning()) {
//                        JOptionPane.showMessageDialog(RobinMainGUI.this,
//                                testR.getWarningMessage(),
//                                "R version mismatch",
//                                JOptionPane.WARNING_MESSAGE);
//
//                    } else {
//                        mswin.dispose();
//                        JOptionPane.showMessageDialog(RobinMainGUI.this,
//                                "Your installation of R seems to meet all requirements",
//                                "R successfully set up",
//                                JOptionPane.INFORMATION_MESSAGE);
//                    }
//                    mswin.dispose();
//                    busyIconTimer.stop();
//                    progressLabel.setText("idle");
//                    progressIconLabel.setIcon(idleIcon);
//                    //enableImportControls(true);
//                    return;
//
//                }
//            }
//        };
//
//        t = new Timer(100, ll);
//        
//        t.start();


        // (opt) search some standard locations??
        // ask the user for a R binary path if "which R"
        // does not give us anything useful


        // try running R with a dummy script that uses all the
        // prerequisite libs
        // if this fails run a setup script that loads all+
        // the BioC libs from the internet


        // if all of the above fails - exit verbosely
    }

    // window listener
    public void windowOpened(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowClosing(WindowEvent e) {
        userQuitRobin(true);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeiconified(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowActivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void windowDeactivated(WindowEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getResourcePath() {
        if (Utilities.isMacOSX()) {
            File indexDir = new File(this.getInstallPath(), "index");
            if (!indexDir.exists()) {
                indexDir.mkdir();
            }
            return new File(this.getInstallPath());
        } else {
            File home = new File(System.getProperty("user.home"));
            File confDir = new File(home, ".robindata");
            File indexDir = new File(confDir, "index");
            if (!indexDir.exists()) {
                indexDir.mkdir();
            }
            return confDir;
        }
    }

    public String getVersionString() {
        try {
            Properties props = new Properties();
            props.load(RobinMainGUI.class.getResourceAsStream("/de/mpimp/golm/robin/Version.properties"));
            return props.getProperty("Robin.version");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private void userQuitRobin(boolean exit) {
        SimpleLogger.getLogger(true).logMessage("User quit");
        // save settings
        try {
            //File confFile = new java.io.File(System.getProperty("user.home") + "/.robin.conf");
            File confFile = new java.io.File(this.getResourcePath(), "robin.conf");
            confFile.createNewFile();
            java.io.FileOutputStream out = new java.io.FileOutputStream(confFile);

            // DEBUG
            System.out.println("saving R path=" + defaultSettings.getProperty("PathToR"));

            defaultSettings.setProperty("showReleaseNotes", Boolean.toString(this.showReleaseNotes));
            defaultSettings.store(out, "Robin settings");

            if (workflow instanceof RNASeqWorkflowPanel) {
                RNASeqDataModel model = null;
                try {
                    SimpleLogger.getLogger(true).logMessage("saving project data.");
                    model = ((RNASeqWorkflowPanel) workflow).getDataModel();
                    model.store(null);
                } catch (Exception ex) {                    
                    //CollapsibleInfoDialog info = new CollapsibleInfoDialog(this, "Could not save project file.", ex);
                    // delete the file  - it'll be an empty dummy anyway
                    new File(model.getSourceDir(), model.getExperimentName() + "_data.xml").delete();
                    
                    // in this context (shutdown hook...) there seems to be a problem opening the 
                    // warning dialog from within the logger.       
                    
                    SimpleLogger.getLogger(true).setLogLevel(SimpleLogger.GUILogLevel.EXCEPTIONS_ONLY);
                    SimpleLogger.getLogger(true).logException(ex);
                    //info.setVisible(true);
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //DEBUG
        System.out.println("...exiting.");
        if (shutDownComplete.compareAndSet(false, true)) {
            //DEBUG
            System.out.println("shutdown flag set to " + shutDownComplete.get());
        } else {
            System.out.println("shutdown flag was already true... this should not happen");
        }
        if (exit) {
            System.exit(0);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DGEButton;
    private javax.swing.JButton TilingArrayButton;
    private javax.swing.JButton affyButton;
    private javax.swing.JPanel chooseAnalysisTypePanel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton genericSCButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton manualButton;
    public javax.swing.JLabel progressIconLabel;
    public javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JPanel qcResultListItem;
    private javax.swing.JButton settingsButton;
    private javax.swing.JPanel statusBar;
    private javax.swing.JLabel stepLabel;
    private javax.swing.JLabel thunbnail;
    private javax.swing.JButton twoColorButton;
    // End of variables declaration//GEN-END:variables
}
interface CLibrary extends Library {
    public int chmod(String path, int mode);
}
