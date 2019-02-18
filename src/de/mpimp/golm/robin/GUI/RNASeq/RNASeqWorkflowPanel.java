/*/
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RobinWorkflow.java
 *
 * Created on 10.03.2010, 09:04:30
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import de.mpimp.golm.common.R.REngine;
import de.mpimp.golm.common.R.RProcessResult;
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import de.mpimp.golm.common.gui.CollapsibleInfoDialog;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.help.HelpHandler;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.*;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqAbstractRefPanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqAbtractMappingToolSettingsPanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqKallistoSettingsPanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqMappingResultPanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqRefTranscriptomePanel;
import de.mpimp.golm.robin.GUI.RNASeq.mapping.RNASeqReferenceGenomePanel;
import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCResultContainerPanel;
import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCprogressPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.TMModuleListItem;
import de.mpimp.golm.robin.GUI.Trimmomatic.TMProgressDialog;
import de.mpimp.golm.robin.GUI.Trimmomatic.TMWorkflowAreaPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.BarcodeSplitterPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMCropperPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMIlluminaClipperPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMLeadingTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMMinimumLengthTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMModulePanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMSlidingWindowTrimmerPanel;
import de.mpimp.golm.robin.GUI.Trimmomatic.modulepanels.TMTrailingTrimmerPanel;
import de.mpimp.golm.robin.IPC.MapCommunicator;
import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.robin.annotation.GFF3.GFF3Utils;
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.data.RNASeqSample;
import de.mpimp.golm.robin.designer.GUI.AnalysisDesigner;
import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.robin.designer.model.NoConnectionsDesignException;
import de.mpimp.golm.robin.designer.model.RedundantConnectionsDesignException;
import de.mpimp.golm.robin.misc.GUI.TargetedMouseListener;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.robin.rnaseq.RPKM.RNASeqRPKMGenerator;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqAbstractMappingProcess;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBAMSAMImporter;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBowtieMappingProcess;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqKallistoMappingProcess;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqMappingProcessController;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.parser.FastQParser;
import de.mpimp.golm.robin.rnaseq.parser.FastQParserThread;
import de.mpimp.golm.robin.rnaseq.parser.FastQSampleParser;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBaseCallFrequencies;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqBasicStats;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqConsecutiveHomopolymers;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqKmerFrequency;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqOverrepSequences;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerBaseQuality;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqPerSequenceQuality;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TMTrimmerArguments;
import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.commons.lang.StringUtils;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.jfree.chart.ChartPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqWorkflowPanel extends RobinWorkflow {

    private RNASeqDataModel dataModel;
    private RNASeqDelegate delegate = new RNASeqDelegate(this);
    private HelpHandler helpHandler;
    private JPanel qcResultPanel;
    private AtomicBoolean stopRunningTasks = new AtomicBoolean(false);
    private RNASeqAbstractRefPanel referenceDataPanel;
    private RNASeqAbtractMappingToolSettingsPanel mappingToolSettingPanel;
    private RNASeqMappingProcessController mappingController;
    private TMWorkflowAreaPanel trimmoWorkFlowAreaPanel;
    private long mappingStartMillis;
    private HashMap<String, Integer> sampleConditionsTable = new HashMap<String, Integer>();
    private SimpleLogger logger;
    // i am ashamed of this one but it does what i want
    private int lastTextLength = 1;
    private static final String RSIMPORTPANEL = "card2";
    private static final String RSQCPANEL = "card3";
    private static final String RSQCRESULTPANEL = "card4";
    private static final String RSTRIMMOMATICPANEL = "card5";
    private static final String RSLIBCONFPANEL = "card6";
    private static final String RSMAPPINGPANEL = "card7";
    private static final String RSDESTATSPANEL = "card8";
    private static final String RSRESULTSBROWSERPANEL = "card9";
    /**
     * REMEMBER TO SET TO FALSE FOR RELEASES!
     */
    private static boolean TESTMODE = false;
    // to be continued
    private AnalysisDesigner analysisDesignerPanel;
    private boolean trimmoPanelIsReady = false;
    private boolean trimmoSkipped = false;
    private boolean importsCompleteAnalysis = false;
    private boolean DEAnalysisControlsEnabled = true;
    private boolean importBeforeLibconfigDone = false;
    private int importStage = 0;

    /**
     * Creates new form RobinWorkflow
     */
    public RNASeqWorkflowPanel() {
        initComponents();
    }

    public RNASeqWorkflowPanel(RobinMainGUI main, File projectPath) {
        initComponents();
        initWorkflow();
        this.processTimer = new Timer(100, null);
        this.executor = Executors.newFixedThreadPool(2); //FIXME the size of the pool needs to be set with sense
        this.mainGUI = main;
        this.dataModel.setProjectDir(projectPath);
        this.helpHandler = HelpHandler.getHandler();
        this.helpHandler.addHelpResource("de.mpimp.golm.robin.resources.help.rnaseqhelp");
        this.dataModel.setExperimentName(projectPath.getName());

        // set up logger        
        dataModel.getLogDir().mkdir();
        try {
            this.logger = SimpleLogger.getLogger(true);
            this.logger.setLogfile(new File(dataModel.getLogDir(), dataModel.getExperimentName() + ".log"));
            SimpleLogger.setOwner(mainGUI);
            logger.logMessage("Starting RNA-Seq workflow");
        } catch (IOException ex) {
            logger.logException(ex);
        }
        if (TESTMODE) {
            TESTING_simulateInputFiles();
        }
    }

    public boolean isDEAnalysisControlsEnabled() {
        return DEAnalysisControlsEnabled;
    }

    public void setDEAnalysisControlsEnabled(boolean DEAnalysisControlsEnabled) {
        this.DEAnalysisControlsEnabled = DEAnalysisControlsEnabled;
    }

    public synchronized boolean isStopRunningTasks() {
        return stopRunningTasks.get();
    }

    public synchronized void setStopRunningTasks(boolean state) {
        stopRunningTasks.set(state);
    }

    private void initWorkflow() {

        // limma deactivated for now since it's not statistically
        // sound to use it with count data
//        // limmaRadioButton.setVisible(false);
//        gcContentNormMethodBox.setVisible(false);
//        normGCHelpButton.setVisible(false);
        //jLabel26.setVisible(false);
        showAlternativeInputs(false);

        // init custom objects and GUI components
        dataModel = new RNASeqDataModel();
        trimmoWorkFlowAreaPanel = new TMWorkflowAreaPanel();
        TMWorkflowScrollpane.add(trimmoWorkFlowAreaPanel);
        TMWorkflowScrollpane.setViewportView(trimmoWorkFlowAreaPanel);

        analysisTypeButtonGroup.setSelected(edgeRRadioButton.getModel(), true);
    }

    public RobinMainGUI getMainGUI() {
        return mainGUI;
    }

    public RNASeqDelegate getDelegate() {
        return delegate;
    }

    protected RTask prepareTask(File scriptFileName, String scriptCode) {
        // write the file
        try {
            FileWriter out = new FileWriter(scriptFileName);
            out.write(scriptCode);
            out.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(new Exception("File: " + scriptFileName, ex));
        }


        Properties defaults = mainGUI.getDefaultSettings();

        if (TESTMODE) {
            defaults.setProperty("PathToR", "/usr/bin/");
        }


        final RTask qcTask = new RTask(defaults.getProperty("PathToR"),
                defaults.getProperty("CommandToRunR"),
                defaults.getProperty("ArgsToR"), scriptFileName);
        return qcTask;

    }

    public synchronized void updateTimeElapsed() {
        long elapsedMillis = System.currentTimeMillis() - mappingStartMillis;
        this.timeElapsedLabel.setText("Time elapsed: " + Utilities.millisecondsToString(elapsedMillis));
        this.repaint();
    }

    public synchronized void staticAppendToMappingProgressPane(String text, AttributeSet attr) {
        try {
            Document doc = this.mappingProgressPane.getDocument();
            if (lastTextLength != 1) {
                doc.remove(doc.getEndPosition().getOffset() - (lastTextLength + 1), lastTextLength);
            }
            doc.insertString(doc.getEndPosition().getOffset() - 1, text + "\n", attr);
            lastTextLength = text.length() + 1;
            mappingProgressPane.scrollRectToVisible(new Rectangle(0, mappingProgressPane.getHeight(), 1, 1));//
            this.repaint();
        } catch (BadLocationException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }

    public synchronized void resetStaticAppend() {
        lastTextLength = 1;
    }

    public synchronized void appendToMappingProgressPane(String text, AttributeSet attr) {
        try {
            Document doc = this.mappingProgressPane.getDocument();
            doc.insertString(doc.getEndPosition().getOffset(), text + "\n", attr);
            mappingProgressPane.scrollRectToVisible(new Rectangle(0, doc.getLength(), 1, 1));//mappingProgressPane.getHeight()
            this.repaint();
        } catch (BadLocationException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
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

        RSFileChooser = new javax.swing.JFileChooser();
        referenceTypeButtonGroup = new javax.swing.ButtonGroup();
        analysisTypeButtonGroup = new javax.swing.ButtonGroup();
        RNASeqImportPanel = new javax.swing.JPanel();
        stepPanel = new javax.swing.JPanel();
        RSImportNextButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        pipelineVersionBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        importFileList = new javax.swing.JList();
        addImportButton = new javax.swing.JButton();
        removeImportButton = new javax.swing.JButton();
        forceManualIlluBox = new javax.swing.JCheckBox();
        jSeparator13 = new javax.swing.JSeparator();
        sambamLabel = new javax.swing.JLabel();
        importSAMBAMButton = new javax.swing.JButton();
        countsTableLabel = new javax.swing.JLabel();
        importCountsTableButton = new javax.swing.JButton();
        alternativeInputCheckBox = new javax.swing.JCheckBox();
        countsTableImportHelp = new javax.swing.JButton();
        RNASeqQualityCheckPanel = new javax.swing.JPanel();
        stepPanel1 = new javax.swing.JPanel();
        previousStepButton1 = new javax.swing.JButton();
        RSQualityCheckNextButton = new javax.swing.JButton();
        QCFramePanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        RScallQualBox = new javax.swing.JCheckBox();
        RScallQualButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        RSconsecHomoBox = new javax.swing.JCheckBox();
        RSconsecHomoButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        RSKmerBox = new javax.swing.JCheckBox();
        RSKmerButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        RScallFreqBox = new javax.swing.JCheckBox();
        RScallFreqButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        RSoverRepBox = new javax.swing.JCheckBox();
        RSoverRepButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        RSbasicStatsBox = new javax.swing.JCheckBox();
        RSbasicStatsButton = new javax.swing.JButton();
        QCSettingsPane = new javax.swing.JTabbedPane();
        RSQCgeneralSettingsPanel = new javax.swing.JPanel();
        labelxyz = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        freeRAMlabel = new javax.swing.JLabel();
        javaHeapLabel = new javax.swing.JLabel();
        numCPULabel = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        RSQCThreadsSpinner = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        pipelineVersionBox1 = new javax.swing.JComboBox();
        forceManualIlluBox1 = new javax.swing.JCheckBox();
        sysInfoRefreshButton = new javax.swing.JButton();
        RSQCFileSettingsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        RSScanSampleBox = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        RSScanSampleSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        RSKmerSettingsPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        minKmerLengthSpinner = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        maxKmerLengthSpinner = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        maxKmerUniqueSpinner = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        checkAllQCBox = new javax.swing.JCheckBox();
        RNASeqQualityResultsPane = new javax.swing.JPanel();
        stepPanel2 = new javax.swing.JPanel();
        previousStepButton2 = new javax.swing.JButton();
        RSQualResultsNextButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        RSQCResultsScrollPane = new javax.swing.JScrollPane();
        jSeparator3 = new javax.swing.JSeparator();
        RSQCResultViewScrollPane = new javax.swing.JScrollPane();
        RSQCDummyPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        RNASeqTrimmomaticPanel = new javax.swing.JPanel();
        stepPanel3 = new javax.swing.JPanel();
        RSTrimmoPreviousButton = new javax.swing.JButton();
        RSTrimmoNextNutton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        trimmoModuleScrollPane = new javax.swing.JScrollPane();
        trimmoModulePanel = new javax.swing.JPanel();
        TMWorkflowScrollpane = new javax.swing.JScrollPane();
        clearTrimmoWorkflowButton = new javax.swing.JButton();
        addStandardTrimmoModulesButton = new javax.swing.JButton();
        RNASeqLibraryConfiguration = new javax.swing.JPanel();
        stepPanel5 = new javax.swing.JPanel();
        previousStepButton5 = new javax.swing.JButton();
        libConfNextButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        conditionsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        conditionsList = new javax.swing.JList();
        addConditionButton = new javax.swing.JButton();
        removeConditionButton = new javax.swing.JButton();
        newConditionField = new javax.swing.JTextField();
        addSampleLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        samplePanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        removeSampleButton = new javax.swing.JButton();
        inputfilesScrollPane = new javax.swing.JScrollPane();
        trimmedInputFileList = new javax.swing.JList();
        addSampleCombobox = new javax.swing.JComboBox();
        addSampleButton = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        sampleConditionLabel = new javax.swing.JLabel();
        RNASeqMappingPanel = new javax.swing.JPanel();
        stepPanel4 = new javax.swing.JPanel();
        previousStepButton4 = new javax.swing.JButton();
        mappingStepNextButton = new javax.swing.JButton();
        resetMappingControlsButton = new javax.swing.JButton();
        skipMappingButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        chooseRefTypeLabel = new javax.swing.JLabel();
        refTranscriptomeButton = new javax.swing.JRadioButton();
        refGenomeButton = new javax.swing.JRadioButton();
        refGenomeInfo = new javax.swing.JLabel();
        refTransInfo = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        chooseMappingToolLabel = new javax.swing.JLabel();
        mappingToolBox = new javax.swing.JComboBox();
        selectMappingToolLabel = new javax.swing.JLabel();
        mappingToolInfoButton = new javax.swing.JButton();
        specifyRefLabel = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane5 = new javax.swing.JScrollPane();
        mappingProgressPane = new javax.swing.JEditorPane();
        startMappingButton = new javax.swing.JButton();
        referenceDataContainerPanel = new javax.swing.JPanel();
        mappingToolSettingsContainerPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        timeElapsedLabel = new javax.swing.JLabel();
        stopMappingProcessButton = new javax.swing.JButton();
        mappingResultScrollPane = new javax.swing.JScrollPane();
        mappingResultPanel = new javax.swing.JPanel();
        RNASeqDEStatisticsPanel = new javax.swing.JPanel();
        stepPanel6 = new javax.swing.JPanel();
        destatsPanelPreviousButton = new javax.swing.JButton();
        destatsPanelNextButton = new javax.swing.JButton();
        designerScrollPane = new javax.swing.JScrollPane();
        jLabel17 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        analysisSettingsPanel = new javax.swing.JPanel();
        edgeRRadioButton = new javax.swing.JRadioButton();
        deseqRadioButton = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        limmaPCorrBox = new javax.swing.JComboBox();
        gcContWithinMethodBox = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        limmaCutoffBox = new javax.swing.JComboBox();
        writeRPKMBox = new javax.swing.JCheckBox();
        limmaMinLFCBox = new javax.swing.JCheckBox();
        jLabel27 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        normGCHelpButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        dispersionLabel = new javax.swing.JLabel();
        dispersionBox = new javax.swing.JComboBox();
        normGCBox = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        gcContBetweenMethodBox = new javax.swing.JComboBox();
        jSeparator10 = new javax.swing.JSeparator();
        RNASeqResultsBrowserPanel = new javax.swing.JPanel();
        stepPanel7 = new javax.swing.JPanel();
        previousStepButton = new javax.swing.JButton();
        nextStepButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        resultBrowserContainerPanel = new javax.swing.JPanel();

        RSFileChooser.setMultiSelectionEnabled(true);

        setLayout(new java.awt.CardLayout());

        RNASeqImportPanel.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqImportPanel.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel.setLayout(new java.awt.GridBagLayout());

        RSImportNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        RSImportNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSImportNextButton.text")); // NOI18N
        RSImportNextButton.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSImportNextButton.toolTipText")); // NOI18N
        RSImportNextButton.setEnabled(false);
        RSImportNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSImportNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel.add(RSImportNextButton, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel5.text")); // NOI18N

        pipelineVersionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "autodetect", "1.8+", "1.5+", "1.3+", "1.1", " " }));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, forceManualIlluBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), pipelineVersionBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel1.text")); // NOI18N

        importFileList.setBackground(new java.awt.Color(214, 232, 255));
        importFileList.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.importFileList.border.title"))); // NOI18N
        jScrollPane1.setViewportView(importFileList);
        DefaultListModel importListModel = new DefaultListModel();
        ListDataListener l = new ListDataListener() {

            public void intervalAdded(ListDataEvent e) {
                RSImportNextButton.setEnabled(true);
            }

            public void intervalRemoved(ListDataEvent e) {
                if (importFileList.getModel().getSize() == 0) {
                    RSImportNextButton.setEnabled(false);
                } else {
                    RSImportNextButton.setEnabled(true);
                }
            }
            public void contentsChanged(ListDataEvent e) {
            }
        };
        importListModel.addListDataListener(l);
        importFileList.setModel(importListModel);

        addImportButton.setBackground(new java.awt.Color(255, 255, 255));
        addImportButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addImportButton.text")); // NOI18N
        addImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addImportButton.setContentAreaFilled(false);
        addImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImportButtonActionPerformed(evt);
            }
        });

        removeImportButton.setBackground(new java.awt.Color(255, 255, 255));
        removeImportButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.removeImportButton.text")); // NOI18N
        removeImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeImportButton.setContentAreaFilled(false);
        removeImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImportButtonActionPerformed(evt);
            }
        });

        forceManualIlluBox.setBackground(new java.awt.Color(255, 255, 255));
        forceManualIlluBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.forceManualIlluBox.text")); // NOI18N

        sambamLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.sambamLabel.text")); // NOI18N

        importSAMBAMButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/openFile.png"))); // NOI18N
        importSAMBAMButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.importSAMBAMButton.text_1")); // NOI18N
        importSAMBAMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSAMBAMButtonActionPerformed(evt);
            }
        });

        countsTableLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.countsTableLabel.text")); // NOI18N

        importCountsTableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/openFile.png"))); // NOI18N
        importCountsTableButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.importCountsTableButton.text")); // NOI18N
        importCountsTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCountsTableButtonActionPerformed(evt);
            }
        });

        alternativeInputCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        alternativeInputCheckBox.setFont(alternativeInputCheckBox.getFont().deriveFont(alternativeInputCheckBox.getFont().getStyle() | java.awt.Font.BOLD, alternativeInputCheckBox.getFont().getSize()+1));
        alternativeInputCheckBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.alternativeInputCheckBox.text")); // NOI18N
        alternativeInputCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alternativeInputCheckBoxActionPerformed(evt);
            }
        });

        countsTableImportHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        countsTableImportHelp.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.countsTableImportHelp.text_1")); // NOI18N
        countsTableImportHelp.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.countsTableImportHelp.actionCommand")); // NOI18N
        countsTableImportHelp.setBorder(null);
        countsTableImportHelp.setBorderPainted(false);
        countsTableImportHelp.setContentAreaFilled(false);
        countsTableImportHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });

        org.jdesktop.layout.GroupLayout RNASeqImportPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqImportPanel);
        RNASeqImportPanel.setLayout(RNASeqImportPanelLayout);
        RNASeqImportPanelLayout.setHorizontalGroup(
            RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqImportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqImportPanelLayout.createSequentialGroup()
                        .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                                .add(RNASeqImportPanelLayout.createSequentialGroup()
                                    .add(jLabel1)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(forceManualIlluBox)
                                        .add(pipelineVersionBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .add(jSeparator13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 285, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(alternativeInputCheckBox)
                            .add(RNASeqImportPanelLayout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(importSAMBAMButton)
                                    .add(sambamLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(countsTableLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(RNASeqImportPanelLayout.createSequentialGroup()
                                        .add(importCountsTableButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(countsTableImportHelp)))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqImportPanelLayout.createSequentialGroup()
                        .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stepPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
        );
        RNASeqImportPanelLayout.setVerticalGroup(
            RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqImportPanelLayout.createSequentialGroup()
                .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqImportPanelLayout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(pipelineVersionBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(forceManualIlluBox)
                        .add(3, 3, 3)
                        .add(jSeparator13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(alternativeInputCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sambamLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(importSAMBAMButton)
                        .add(24, 24, 24)
                        .add(countsTableLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(countsTableImportHelp)
                            .add(importCountsTableButton)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(stepPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqImportPanel, "card2");

        RNASeqQualityCheckPanel.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqQualityCheckPanel.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel1.setLayout(new java.awt.GridBagLayout());

        previousStepButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        previousStepButton1.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.previousStepButton1.text")); // NOI18N
        previousStepButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStepButton1ActionPerformed(evt);
            }
        });
        stepPanel1.add(previousStepButton1, new java.awt.GridBagConstraints());

        RSQualityCheckNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        RSQualityCheckNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSQualityCheckNextButton.text")); // NOI18N
        RSQualityCheckNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSQualityCheckNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel1.add(RSQualityCheckNextButton, gridBagConstraints);

        QCFramePanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/basequal.png"))); // NOI18N
        jLabel35.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(jLabel35);

        jLabel36.setBackground(new java.awt.Color(214, 232, 255));
        jLabel36.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel36.text")); // NOI18N
        jPanel1.add(jLabel36);

        RScallQualBox.setBackground(new java.awt.Color(255, 255, 255));
        RScallQualBox.setText("Include?");
        RScallQualBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RScallQualBoxActionPerformed(evt);
            }
        });
        jPanel1.add(RScallQualBox);

        RScallQualButton.setBackground(new java.awt.Color(214, 232, 255));
        RScallQualButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RScallQualButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RScallQualButton.text")); // NOI18N
        RScallQualButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RScallQualButton.actionCommand")); // NOI18N
        RScallQualButton.setBorderPainted(false);
        RScallQualButton.setContentAreaFilled(false);
        RScallQualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel1.add(RScallQualButton);

        jPanel2.setBackground(new java.awt.Color(214, 232, 255));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/homopol.png"))); // NOI18N
        jLabel37.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel37);

        jLabel38.setBackground(new java.awt.Color(214, 232, 255));
        jLabel38.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel38.text")); // NOI18N
        jPanel2.add(jLabel38);

        RSconsecHomoBox.setBackground(new java.awt.Color(214, 232, 255));
        RSconsecHomoBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSconsecHomoBox.text")); // NOI18N
        RSconsecHomoBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSconsecHomoBoxActionPerformed(evt);
            }
        });
        jPanel2.add(RSconsecHomoBox);

        RSconsecHomoButton.setBackground(new java.awt.Color(214, 232, 255));
        RSconsecHomoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RSconsecHomoButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSconsecHomoButton.text")); // NOI18N
        RSconsecHomoButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSconsecHomoButton.actionCommand")); // NOI18N
        RSconsecHomoButton.setBorderPainted(false);
        RSconsecHomoButton.setContentAreaFilled(false);
        RSconsecHomoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel2.add(RSconsecHomoButton);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/kmer.png"))); // NOI18N
        jLabel39.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(jLabel39);

        jLabel40.setBackground(new java.awt.Color(214, 232, 255));
        jLabel40.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel40.text")); // NOI18N
        jPanel5.add(jLabel40);

        RSKmerBox.setBackground(new java.awt.Color(255, 255, 255));
        RSKmerBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSKmerBox.text")); // NOI18N
        RSKmerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSKmerBoxActionPerformed(evt);
            }
        });
        jPanel5.add(RSKmerBox);

        RSKmerButton.setBackground(new java.awt.Color(214, 232, 255));
        RSKmerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RSKmerButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSKmerButton.text")); // NOI18N
        RSKmerButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSKmerButton.actionCommand")); // NOI18N
        RSKmerButton.setBorderPainted(false);
        RSKmerButton.setContentAreaFilled(false);
        RSKmerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel5.add(RSKmerButton);

        jPanel6.setBackground(new java.awt.Color(214, 232, 255));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/basefreq.png"))); // NOI18N
        jLabel41.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel6.add(jLabel41);

        jLabel42.setBackground(new java.awt.Color(214, 232, 255));
        jLabel42.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel42.text")); // NOI18N
        jPanel6.add(jLabel42);

        RScallFreqBox.setBackground(new java.awt.Color(214, 232, 255));
        RScallFreqBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RScallFreqBox.text")); // NOI18N
        RScallFreqBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RScallFreqBoxActionPerformed(evt);
            }
        });
        jPanel6.add(RScallFreqBox);

        RScallFreqButton.setBackground(new java.awt.Color(214, 232, 255));
        RScallFreqButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RScallFreqButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RScallFreqButton.text")); // NOI18N
        RScallFreqButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RScallFreqButton.actionCommand")); // NOI18N
        RScallFreqButton.setBorderPainted(false);
        RScallFreqButton.setContentAreaFilled(false);
        RScallFreqButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel6.add(RScallFreqButton);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/overrep.png"))); // NOI18N
        jLabel43.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel7.add(jLabel43);

        jLabel44.setBackground(new java.awt.Color(214, 232, 255));
        jLabel44.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel44.text")); // NOI18N
        jPanel7.add(jLabel44);

        RSoverRepBox.setBackground(new java.awt.Color(255, 255, 255));
        RSoverRepBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSoverRepBox.text")); // NOI18N
        RSoverRepBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSoverRepBoxActionPerformed(evt);
            }
        });
        jPanel7.add(RSoverRepBox);

        RSoverRepButton.setBackground(new java.awt.Color(214, 232, 255));
        RSoverRepButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RSoverRepButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSoverRepButton.text")); // NOI18N
        RSoverRepButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSoverRepButton.actionCommand")); // NOI18N
        RSoverRepButton.setBorderPainted(false);
        RSoverRepButton.setContentAreaFilled(false);
        RSoverRepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel7.add(RSoverRepButton);

        jPanel8.setBackground(new java.awt.Color(214, 232, 255));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/basic.png"))); // NOI18N
        jLabel45.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel8.add(jLabel45);

        jLabel46.setBackground(new java.awt.Color(214, 232, 255));
        jLabel46.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel46.text")); // NOI18N
        jPanel8.add(jLabel46);

        RSbasicStatsBox.setBackground(new java.awt.Color(214, 232, 255));
        RSbasicStatsBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSbasicStatsBox.text")); // NOI18N
        RSbasicStatsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSbasicStatsBoxActionPerformed(evt);
            }
        });
        jPanel8.add(RSbasicStatsBox);

        RSbasicStatsButton.setBackground(new java.awt.Color(214, 232, 255));
        RSbasicStatsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RSbasicStatsButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSbasicStatsButton.text")); // NOI18N
        RSbasicStatsButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSbasicStatsButton.actionCommand")); // NOI18N
        RSbasicStatsButton.setBorderPainted(false);
        RSbasicStatsButton.setContentAreaFilled(false);
        RSbasicStatsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        jPanel8.add(RSbasicStatsButton);

        org.jdesktop.layout.GroupLayout QCFramePanelLayout = new org.jdesktop.layout.GroupLayout(QCFramePanel);
        QCFramePanel.setLayout(QCFramePanelLayout);
        QCFramePanelLayout.setHorizontalGroup(
            QCFramePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        QCFramePanelLayout.setVerticalGroup(
            QCFramePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(QCFramePanelLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        QCSettingsPane.setBackground(new java.awt.Color(255, 255, 255));

        RSQCgeneralSettingsPanel.setBackground(new java.awt.Color(247, 247, 247));
        RSQCgeneralSettingsPanel.setLayout(new java.awt.GridBagLayout());

        labelxyz.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.labelxyz.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        RSQCgeneralSettingsPanel.add(labelxyz, gridBagConstraints);

        jLabel14.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        RSQCgeneralSettingsPanel.add(jLabel14, gridBagConstraints);

        jLabel15.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        RSQCgeneralSettingsPanel.add(jLabel15, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        RSQCgeneralSettingsPanel.add(jSeparator1, gridBagConstraints);

        freeRAMlabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.freeRAMlabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(freeRAMlabel, gridBagConstraints);

        javaHeapLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.javaHeapLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(javaHeapLabel, gridBagConstraints);

        numCPULabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.numCPULabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(numCPULabel, gridBagConstraints);

        jLabel19.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel19.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        RSQCgeneralSettingsPanel.add(jLabel19, gridBagConstraints);

        RSQCThreadsSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 1, 64, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(RSQCThreadsSpinner, gridBagConstraints);

        jLabel20.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        RSQCgeneralSettingsPanel.add(jLabel20, gridBagConstraints);

        pipelineVersionBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "autodetect", "1.8+", "1.5+", "1.3+", "1.1", " " }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, forceManualIlluBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), pipelineVersionBox1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(pipelineVersionBox1, gridBagConstraints);

        forceManualIlluBox1.setBackground(new java.awt.Color(247, 247, 247));
        forceManualIlluBox1.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.forceManualIlluBox1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSQCgeneralSettingsPanel.add(forceManualIlluBox1, gridBagConstraints);

        sysInfoRefreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        sysInfoRefreshButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.sysInfoRefreshButton.text")); // NOI18N
        sysInfoRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sysInfoRefreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        RSQCgeneralSettingsPanel.add(sysInfoRefreshButton, gridBagConstraints);

        QCSettingsPane.addTab(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSQCgeneralSettingsPanel.TabConstraints.tabTitle"), RSQCgeneralSettingsPanel); // NOI18N

        RSQCFileSettingsPanel.setBackground(new java.awt.Color(247, 247, 247));
        RSQCFileSettingsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        RSQCFileSettingsPanel.add(jLabel2, gridBagConstraints);

        RSScanSampleBox.setBackground(new java.awt.Color(247, 247, 247));
        RSScanSampleBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSScanSampleBox.text")); // NOI18N
        RSScanSampleBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSScanSampleBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        RSQCFileSettingsPanel.add(RSScanSampleBox, gridBagConstraints);

        jPanel11.setBackground(new java.awt.Color(247, 247, 247));
        jPanel11.setMinimumSize(new java.awt.Dimension(150, 28));
        jPanel11.setPreferredSize(new java.awt.Dimension(150, 28));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        RSScanSampleSpinner.setModel(new javax.swing.SpinnerNumberModel(100000, 10000, 276447232, 1000));
        RSScanSampleSpinner.setPreferredSize(new java.awt.Dimension(37, 28));
        RSScanSampleSpinner.setValue(100000);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, RSScanSampleBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), RSScanSampleSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 83;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel11.add(RSScanSampleSpinner, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel4.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, RSScanSampleBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel11.add(jLabel4, gridBagConstraints);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, RSScanSampleBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel11.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        RSQCFileSettingsPanel.add(jPanel11, gridBagConstraints);

        QCSettingsPane.addTab(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSQCFileSettingsPanel.TabConstraints.tabTitle"), RSQCFileSettingsPanel); // NOI18N

        RSKmerSettingsPanel.setBackground(new java.awt.Color(247, 247, 247));
        RSKmerSettingsPanel.setLayout(new java.awt.GridBagLayout());

        jPanel9.setBackground(new java.awt.Color(247, 247, 247));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        jPanel9.add(jLabel7, gridBagConstraints);

        minKmerLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(5, 5, 10, 1));
        minKmerLengthSpinner.setPreferredSize(new java.awt.Dimension(35, 28));
        minKmerLengthSpinner.setValue(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel9.add(minKmerLengthSpinner, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel8.text")); // NOI18N
        jPanel9.add(jLabel8, new java.awt.GridBagConstraints());

        maxKmerLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(5, 5, 10, 1));
        maxKmerLengthSpinner.setPreferredSize(new java.awt.Dimension(35, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel9.add(maxKmerLengthSpinner, gridBagConstraints);

        jLabel10.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        jPanel9.add(jLabel10, gridBagConstraints);

        maxKmerUniqueSpinner.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1000000L), Long.valueOf(100000L), Long.valueOf(10000000L), Long.valueOf(1000L)));
        maxKmerUniqueSpinner.setPreferredSize(new java.awt.Dimension(120, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 6);
        jPanel9.add(maxKmerUniqueSpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        RSKmerSettingsPanel.add(jPanel9, gridBagConstraints);

        jLabel9.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        RSKmerSettingsPanel.add(jLabel9, gridBagConstraints);

        QCSettingsPane.addTab(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSKmerSettingsPanel.TabConstraints.tabTitle"), RSKmerSettingsPanel); // NOI18N

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel6.text")); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                .addContainerGap())
        );

        checkAllQCBox.setBackground(new java.awt.Color(255, 255, 255));
        checkAllQCBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.checkAllQCBox.text")); // NOI18N
        checkAllQCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAllQCBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout RNASeqQualityCheckPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqQualityCheckPanel);
        RNASeqQualityCheckPanel.setLayout(RNASeqQualityCheckPanelLayout);
        RNASeqQualityCheckPanelLayout.setHorizontalGroup(
            RNASeqQualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqQualityCheckPanelLayout.createSequentialGroup()
                .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqQualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqQualityCheckPanelLayout.createSequentialGroup()
                        .add(QCSettingsPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(QCFramePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, checkAllQCBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stepPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
        );
        RNASeqQualityCheckPanelLayout.setVerticalGroup(
            RNASeqQualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqQualityCheckPanelLayout.createSequentialGroup()
                .add(RNASeqQualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqQualityCheckPanelLayout.createSequentialGroup()
                        .add(QCFramePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkAllQCBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(QCSettingsPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(stepPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqQualityCheckPanel, "card3");

        RNASeqQualityResultsPane.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqQualityResultsPane.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel2.setLayout(new java.awt.GridBagLayout());

        previousStepButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        previousStepButton2.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.previousStepButton2.text")); // NOI18N
        previousStepButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStepButton2ActionPerformed(evt);
            }
        });
        stepPanel2.add(previousStepButton2, new java.awt.GridBagConstraints());

        RSQualResultsNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        RSQualResultsNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSQualResultsNextButton.text")); // NOI18N
        RSQualResultsNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSQualResultsNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel2.add(RSQualResultsNextButton, gridBagConstraints);

        jLabel12.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel12.text")); // NOI18N
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel12.setPreferredSize(new java.awt.Dimension(300, 69));
        jLabel12.setRequestFocusEnabled(false);

        RSQCResultsScrollPane.setBorder(null);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        RSQCResultViewScrollPane.setBorder(null);

        RSQCDummyPanel.setBackground(new java.awt.Color(255, 255, 255));
        RSQCDummyPanel.setLayout(new java.awt.GridBagLayout());

        jLabel13.setFont(jLabel13.getFont().deriveFont((jLabel13.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, jLabel13.getFont().getSize()+5));
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Click file to show results");
        RSQCDummyPanel.add(jLabel13, new java.awt.GridBagConstraints());

        RSQCResultViewScrollPane.setViewportView(RSQCDummyPanel);

        org.jdesktop.layout.GroupLayout RNASeqQualityResultsPaneLayout = new org.jdesktop.layout.GroupLayout(RNASeqQualityResultsPane);
        RNASeqQualityResultsPane.setLayout(RNASeqQualityResultsPaneLayout);
        RNASeqQualityResultsPaneLayout.setHorizontalGroup(
            RNASeqQualityResultsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqQualityResultsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqQualityResultsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 320, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(RSQCResultsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 320, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RSQCResultViewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .add(4, 4, 4))
            .add(stepPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
        );
        RNASeqQualityResultsPaneLayout.setVerticalGroup(
            RNASeqQualityResultsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqQualityResultsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqQualityResultsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqQualityResultsPaneLayout.createSequentialGroup()
                        .add(RNASeqQualityResultsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(RNASeqQualityResultsPaneLayout.createSequentialGroup()
                                .add(137, 137, 137)
                                .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(RSQCResultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                            .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
                            .add(RSQCResultViewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stepPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(RNASeqQualityResultsPaneLayout.createSequentialGroup()
                        .add(jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(583, Short.MAX_VALUE))))
        );

        add(RNASeqQualityResultsPane, "card4");

        RNASeqTrimmomaticPanel.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqTrimmomaticPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        RNASeqTrimmomaticPanel.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel3.setLayout(new java.awt.GridBagLayout());

        RSTrimmoPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        RSTrimmoPreviousButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSTrimmoPreviousButton.text")); // NOI18N
        RSTrimmoPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSTrimmoPreviousButtonActionPerformed(evt);
            }
        });
        stepPanel3.add(RSTrimmoPreviousButton, new java.awt.GridBagConstraints());

        RSTrimmoNextNutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        RSTrimmoNextNutton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.RSTrimmoNextNutton.text")); // NOI18N
        RSTrimmoNextNutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RSTrimmoNextNuttonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel3.add(RSTrimmoNextNutton, gridBagConstraints);

        jLabel18.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel18.text")); // NOI18N
        jLabel18.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel18.setPreferredSize(new java.awt.Dimension(300, 69));
        jLabel18.setRequestFocusEnabled(false);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        trimmoModuleScrollPane.setBorder(null);

        trimmoModulePanel.setBackground(new java.awt.Color(255, 255, 255));
        trimmoModulePanel.setPreferredSize(new java.awt.Dimension(200, 250));
        trimmoModulePanel.setLayout(new javax.swing.BoxLayout(trimmoModulePanel, javax.swing.BoxLayout.Y_AXIS));
        trimmoModuleScrollPane.setViewportView(trimmoModulePanel);

        TMWorkflowScrollpane.setBorder(null);

        clearTrimmoWorkflowButton.setFont(clearTrimmoWorkflowButton.getFont().deriveFont(clearTrimmoWorkflowButton.getFont().getSize()-2f));
        clearTrimmoWorkflowButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.clearTrimmoWorkflowButton.text_1")); // NOI18N
        clearTrimmoWorkflowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearTrimmoWorkflowButtonActionPerformed(evt);
            }
        });

        addStandardTrimmoModulesButton.setFont(addStandardTrimmoModulesButton.getFont().deriveFont(addStandardTrimmoModulesButton.getFont().getSize()-2f));
        addStandardTrimmoModulesButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addStandardTrimmoModulesButton.text_1")); // NOI18N
        addStandardTrimmoModulesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStandardTrimmoModulesButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout RNASeqTrimmomaticPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqTrimmomaticPanel);
        RNASeqTrimmomaticPanel.setLayout(RNASeqTrimmomaticPanelLayout);
        RNASeqTrimmomaticPanelLayout.setHorizontalGroup(
            RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(trimmoModuleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 301, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 301, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                        .add(addStandardTrimmoModulesButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(clearTrimmoWorkflowButton))
                    .add(TMWorkflowScrollpane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE))
                .addContainerGap())
            .add(stepPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
        );
        RNASeqTrimmomaticPanelLayout.setVerticalGroup(
            RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                .add(RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                        .add(jLabel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(trimmoModuleScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                        .add(RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(jSeparator5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE))
                            .add(RNASeqTrimmomaticPanelLayout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(TMWorkflowScrollpane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(RNASeqTrimmomaticPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(clearTrimmoWorkflowButton)
                                    .add(addStandardTrimmoModulesButton))))
                        .add(9, 9, 9)))
                .add(stepPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqTrimmomaticPanel, "card5");

        RNASeqLibraryConfiguration.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqLibraryConfiguration.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel5.setLayout(new java.awt.GridBagLayout());

        previousStepButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        previousStepButton5.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.previousStepButton5.text")); // NOI18N
        previousStepButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStepButton5ActionPerformed(evt);
            }
        });
        stepPanel5.add(previousStepButton5, new java.awt.GridBagConstraints());

        libConfNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        libConfNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.libConfNextButton.text")); // NOI18N
        libConfNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libConfNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel5.add(libConfNextButton, gridBagConstraints);

        jLabel21.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel21.text")); // NOI18N
        jLabel21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel21.setPreferredSize(new java.awt.Dimension(300, 69));
        jLabel21.setRequestFocusEnabled(false);

        jLabel16.setFont(jLabel16.getFont().deriveFont((jLabel16.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, jLabel16.getFont().getSize()+1));
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/1_green.png"))); // NOI18N
        jLabel16.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel16.text")); // NOI18N

        conditionsPanel.setBackground(new java.awt.Color(255, 255, 255));
        conditionsPanel.setLayout(new java.awt.GridBagLayout());

        conditionsList.setModel(new DefaultListModel());
        conditionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        conditionsList.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.conditionsList.toolTipText")); // NOI18N
        jScrollPane3.setViewportView(conditionsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        conditionsPanel.add(jScrollPane3, gridBagConstraints);

        addConditionButton.setBackground(new java.awt.Color(255, 255, 255));
        addConditionButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addConditionButton.text")); // NOI18N
        addConditionButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addConditionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        conditionsPanel.add(addConditionButton, gridBagConstraints);

        removeConditionButton.setBackground(new java.awt.Color(255, 255, 255));
        removeConditionButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.removeConditionButton.text")); // NOI18N
        removeConditionButton.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.removeConditionButton.toolTipText")); // NOI18N
        removeConditionButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeConditionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.ipady = 6;
        conditionsPanel.add(removeConditionButton, gridBagConstraints);

        newConditionField.setDocument(new StrictRTermFilterDocument());
        newConditionField.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.newConditionField.text")); // NOI18N
        newConditionField.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.newConditionField.toolTipText")); // NOI18N
        newConditionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newConditionFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        conditionsPanel.add(newConditionField, gridBagConstraints);

        addSampleLabel.setFont(addSampleLabel.getFont().deriveFont((addSampleLabel.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, addSampleLabel.getFont().getSize()+1));
        addSampleLabel.setForeground(new java.awt.Color(102, 102, 102));
        addSampleLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addSampleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/2_green.png"))); // NOI18N
        addSampleLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addSampleLabel.text")); // NOI18N
        addSampleLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        addSampleLabel.setEnabled(false);
        addSampleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jScrollPane4.border.title"))); // NOI18N
        jScrollPane4.setEnabled(false);

        samplePanel.setBackground(new java.awt.Color(255, 255, 255));
        samplePanel.setLayout(new javax.swing.BoxLayout(samplePanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane4.setViewportView(samplePanel);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        removeSampleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        removeSampleButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.removeSampleButton.text_1")); // NOI18N
        removeSampleButton.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.removeSampleButton.toolTipText")); // NOI18N
        removeSampleButton.setEnabled(false);
        removeSampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSampleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 7, 0, 0);
        jPanel4.add(removeSampleButton, gridBagConstraints);

        inputfilesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.inputfilesScrollPane.border.title"))); // NOI18N
        inputfilesScrollPane.setEnabled(false);

        trimmedInputFileList.setModel(new DefaultListModel());
        inputfilesScrollPane.setViewportView(trimmedInputFileList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 109;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(inputfilesScrollPane, gridBagConstraints);

        addSampleCombobox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 191;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 0, 0);
        jPanel4.add(addSampleCombobox, gridBagConstraints);

        addSampleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        addSampleButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addSampleButton.text_1")); // NOI18N
        addSampleButton.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.addSampleButton.toolTipText")); // NOI18N
        addSampleButton.setEnabled(false);
        addSampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSampleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 0);
        jPanel4.add(addSampleButton, gridBagConstraints);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        jPanel4.add(jSeparator7, gridBagConstraints);

        sampleConditionLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.sampleConditionLabel.text")); // NOI18N
        sampleConditionLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 177, 0, 0);
        jPanel4.add(sampleConditionLabel, gridBagConstraints);

        org.jdesktop.layout.GroupLayout RNASeqLibraryConfigurationLayout = new org.jdesktop.layout.GroupLayout(RNASeqLibraryConfiguration);
        RNASeqLibraryConfiguration.setLayout(RNASeqLibraryConfigurationLayout);
        RNASeqLibraryConfigurationLayout.setHorizontalGroup(
            RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                        .add(jSeparator6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 218, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel16)
                    .add(conditionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 260, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, addSampleLabel))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stepPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
        );
        RNASeqLibraryConfigurationLayout.setVerticalGroup(
            RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                        .add(addSampleLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 179, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                    .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                        .add(RNASeqLibraryConfigurationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(RNASeqLibraryConfigurationLayout.createSequentialGroup()
                                .add(331, 331, 331)
                                .add(jSeparator6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel16)
                        .add(4, 4, 4)
                        .add(conditionsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)))
                .add(35, 35, 35)
                .add(stepPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqLibraryConfiguration, "card6");

        RNASeqMappingPanel.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqMappingPanel.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel4.setLayout(new java.awt.GridBagLayout());

        previousStepButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        previousStepButton4.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.previousStepButton4.text")); // NOI18N
        previousStepButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStepButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        stepPanel4.add(previousStepButton4, gridBagConstraints);

        mappingStepNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        mappingStepNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingStepNextButton.text")); // NOI18N
        mappingStepNextButton.setEnabled(false);
        mappingStepNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mappingStepNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        stepPanel4.add(mappingStepNextButton, gridBagConstraints);

        resetMappingControlsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        resetMappingControlsButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.resetMappingControlsButton.text_1")); // NOI18N
        resetMappingControlsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetMappingControls(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        stepPanel4.add(resetMappingControlsButton, gridBagConstraints);

        skipMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        skipMappingButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.skipMappingButton.text_1")); // NOI18N
        skipMappingButton.setEnabled(false);
        skipMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipMappingButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        stepPanel4.add(skipMappingButton, gridBagConstraints);

        jLabel11.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel11.text")); // NOI18N

        chooseRefTypeLabel.setFont(chooseRefTypeLabel.getFont().deriveFont((chooseRefTypeLabel.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, chooseRefTypeLabel.getFont().getSize()+1));
        chooseRefTypeLabel.setForeground(new java.awt.Color(102, 102, 102));
        chooseRefTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chooseRefTypeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/1_green.png"))); // NOI18N
        chooseRefTypeLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.chooseRefTypeLabel.text")); // NOI18N

        refTranscriptomeButton.setBackground(new java.awt.Color(255, 255, 255));
        referenceTypeButtonGroup.add(refTranscriptomeButton);
        refTranscriptomeButton.setFont(refTranscriptomeButton.getFont().deriveFont(refTranscriptomeButton.getFont().getStyle() | java.awt.Font.BOLD));
        refTranscriptomeButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.refTranscriptomeButton.text")); // NOI18N
        refTranscriptomeButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.refTranscriptomeButton.actionCommand")); // NOI18N
        refTranscriptomeButton.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        refTranscriptomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                referenceTypeChosen(evt);
            }
        });

        refGenomeButton.setBackground(new java.awt.Color(255, 255, 255));
        referenceTypeButtonGroup.add(refGenomeButton);
        refGenomeButton.setFont(refGenomeButton.getFont().deriveFont(refGenomeButton.getFont().getStyle() | java.awt.Font.BOLD));
        refGenomeButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.refGenomeButton.text")); // NOI18N
        refGenomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                referenceTypeChosen(evt);
            }
        });

        refGenomeInfo.setForeground(new java.awt.Color(51, 51, 51));
        refGenomeInfo.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.refGenomeInfo.text")); // NOI18N
        refGenomeInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        refTransInfo.setForeground(new java.awt.Color(51, 51, 51));
        refTransInfo.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.refTransInfo.text")); // NOI18N
        refTransInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        chooseMappingToolLabel.setFont(chooseMappingToolLabel.getFont().deriveFont((chooseMappingToolLabel.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, chooseMappingToolLabel.getFont().getSize()+1));
        chooseMappingToolLabel.setForeground(new java.awt.Color(102, 102, 102));
        chooseMappingToolLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chooseMappingToolLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/2_green.png"))); // NOI18N
        chooseMappingToolLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.chooseMappingToolLabel.text")); // NOI18N
        chooseMappingToolLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        chooseMappingToolLabel.setEnabled(false);
        chooseMappingToolLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        mappingToolBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kallisto" }));
        mappingToolBox.setEnabled(false);

        selectMappingToolLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.selectMappingToolLabel.text")); // NOI18N
        selectMappingToolLabel.setEnabled(false);

        mappingToolInfoButton.setBackground(new java.awt.Color(214, 232, 255));
        mappingToolInfoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        mappingToolInfoButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingToolInfoButton.text")); // NOI18N
        mappingToolInfoButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingToolInfoButton.actionCommand")); // NOI18N
        mappingToolInfoButton.setBorderPainted(false);
        mappingToolInfoButton.setContentAreaFilled(false);
        mappingToolInfoButton.setEnabled(false);
        mappingToolInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });

        specifyRefLabel.setFont(specifyRefLabel.getFont().deriveFont((specifyRefLabel.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, specifyRefLabel.getFont().getSize()+1));
        specifyRefLabel.setForeground(new java.awt.Color(102, 102, 102));
        specifyRefLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        specifyRefLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/3_green.png"))); // NOI18N
        specifyRefLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.specifyRefLabel.text")); // NOI18N
        specifyRefLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        specifyRefLabel.setEnabled(false);
        specifyRefLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jScrollPane5.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11), new java.awt.Color(0, 51, 153))); // NOI18N
        jScrollPane5.setEnabled(false);

        mappingProgressPane.setContentType(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingProgressPane.contentType")); // NOI18N
        mappingProgressPane.setEditable(false);
        mappingProgressPane.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        mappingProgressPane.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingProgressPane.text")); // NOI18N
        jScrollPane5.setViewportView(mappingProgressPane);

        startMappingButton.setForeground(new java.awt.Color(102, 102, 102));
        startMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/4_green.png"))); // NOI18N
        startMappingButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.startMappingButton.text_1")); // NOI18N
        startMappingButton.setEnabled(false);
        startMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMappingButtonActionPerformed(evt);
            }
        });

        referenceDataContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        referenceDataContainerPanel.setMaximumSize(new java.awt.Dimension(215, 0));
        referenceDataContainerPanel.setMinimumSize(new java.awt.Dimension(200, 0));
        referenceDataContainerPanel.setPreferredSize(new java.awt.Dimension(202, 0));
        referenceDataContainerPanel.setLayout(new javax.swing.BoxLayout(referenceDataContainerPanel, javax.swing.BoxLayout.Y_AXIS));

        mappingToolSettingsContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        mappingToolSettingsContainerPanel.setMaximumSize(new java.awt.Dimension(215, 0));
        mappingToolSettingsContainerPanel.setMinimumSize(new java.awt.Dimension(200, 0));
        mappingToolSettingsContainerPanel.setPreferredSize(new java.awt.Dimension(215, 0));
        mappingToolSettingsContainerPanel.setLayout(new javax.swing.BoxLayout(mappingToolSettingsContainerPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        timeElapsedLabel.setFont(timeElapsedLabel.getFont().deriveFont(timeElapsedLabel.getFont().getStyle() | java.awt.Font.BOLD));
        timeElapsedLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.timeElapsedLabel.text")); // NOI18N
        timeElapsedLabel.setEnabled(false);

        stopMappingProcessButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.stopMappingProcessButton.text_1")); // NOI18N
        stopMappingProcessButton.setEnabled(false);
        stopMappingProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopMappingProcessButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(timeElapsedLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 105, Short.MAX_VALUE)
                .add(stopMappingProcessButton))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                .add(timeElapsedLabel)
                .add(stopMappingProcessButton))
        );

        mappingResultScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.mappingResultScrollPane.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11), new java.awt.Color(0, 51, 153))); // NOI18N

        mappingResultPanel.setBackground(new java.awt.Color(255, 255, 255));
        mappingResultPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        mappingResultPanel.setLayout(new javax.swing.BoxLayout(mappingResultPanel, javax.swing.BoxLayout.Y_AXIS));
        mappingResultScrollPane.setViewportView(mappingResultPanel);

        org.jdesktop.layout.GroupLayout RNASeqMappingPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqMappingPanel);
        RNASeqMappingPanel.setLayout(RNASeqMappingPanelLayout);
        RNASeqMappingPanelLayout.setHorizontalGroup(
            RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 229, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(chooseRefTypeLabel)
                    .add(refTranscriptomeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(refGenomeButton)
                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(refTransInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(refGenomeInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(12, 12, 12)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(chooseMappingToolLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                        .add(selectMappingToolLabel)
                        .add(5, 5, 5)
                        .add(mappingToolBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(4, 4, 4)
                        .add(mappingToolInfoButton))
                    .add(mappingToolSettingsContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(specifyRefLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(referenceDataContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(startMappingButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(jSeparator8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mappingResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stepPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE)
        );
        RNASeqMappingPanelLayout.setVerticalGroup(
            RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                        .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(34, 34, 34)
                        .add(chooseRefTypeLabel)
                        .add(18, 18, 18)
                        .add(refTranscriptomeButton)
                        .add(5, 5, 5)
                        .add(refTransInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(4, 4, 4)
                        .add(refGenomeButton)
                        .add(5, 5, 5)
                        .add(refGenomeInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                .add(jSeparator2)
                                .add(18, 18, 18))
                            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                .add(chooseMappingToolLabel)
                                .add(5, 5, 5)
                                .add(RNASeqMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                        .add(7, 7, 7)
                                        .add(selectMappingToolLabel))
                                    .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                        .add(3, 3, 3)
                                        .add(mappingToolBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(mappingToolInfoButton))
                                .add(7, 7, 7)
                                .add(mappingToolSettingsContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(8, 8, 8)
                                .add(specifyRefLabel)
                                .add(8, 8, 8)
                                .add(referenceDataContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(7, 7, 7)
                                .add(startMappingButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(8, 8, 8))
                            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(mappingResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                            .add(RNASeqMappingPanelLayout.createSequentialGroup()
                                .add(jSeparator8)
                                .add(18, 18, 18)))))
                .add(12, 12, 12)
                .add(stepPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqMappingPanel, "card7");

        RNASeqDEStatisticsPanel.setBackground(new java.awt.Color(255, 255, 255));
        RNASeqDEStatisticsPanel.setPreferredSize(new java.awt.Dimension(800, 550));

        stepPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel6.setLayout(new java.awt.GridBagLayout());

        destatsPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        destatsPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.destatsPanelPreviousButton.text")); // NOI18N
        destatsPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destatsPanelPreviousButtonActionPerformed(evt);
            }
        });
        stepPanel6.add(destatsPanelPreviousButton, new java.awt.GridBagConstraints());

        destatsPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        destatsPanelNextButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.destatsPanelNextButton.text")); // NOI18N
        destatsPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destatsPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel6.add(destatsPanelNextButton, gridBagConstraints);

        designerScrollPane.setBorder(null);

        jLabel17.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel17.text")); // NOI18N

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);

        analysisSettingsPanel.setBackground(new java.awt.Color(255, 255, 255));
        analysisSettingsPanel.setLayout(new java.awt.GridBagLayout());

        edgeRRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        analysisTypeButtonGroup.add(edgeRRadioButton);
        edgeRRadioButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.edgeRRadioButton.text")); // NOI18N
        edgeRRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.edgeRRadioButton.actionCommand")); // NOI18N
        edgeRRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeRRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        analysisSettingsPanel.add(edgeRRadioButton, gridBagConstraints);

        deseqRadioButton.setBackground(new java.awt.Color(255, 255, 255));
        analysisTypeButtonGroup.add(deseqRadioButton);
        deseqRadioButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.deseqRadioButton.text")); // NOI18N
        deseqRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.deseqRadioButton.actionCommand")); // NOI18N
        deseqRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deseqRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        analysisSettingsPanel.add(deseqRadioButton, gridBagConstraints);

        jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel24.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel24.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        analysisSettingsPanel.add(jLabel24, gridBagConstraints);

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        analysisSettingsPanel.add(jSeparator11, gridBagConstraints);

        jLabel25.setFont(jLabel25.getFont().deriveFont(jLabel25.getFont().getSize()-2f));
        jLabel25.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel25.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(jLabel25, gridBagConstraints);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(jLabel26.getFont().deriveFont(jLabel26.getFont().getSize()-2f));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel26.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normGCBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel26, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(jLabel26, gridBagConstraints);

        limmaPCorrBox.setFont(limmaPCorrBox.getFont().deriveFont(limmaPCorrBox.getFont().getSize()-2f));
        limmaPCorrBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BH", "BY", "fdr", "holm", "none" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        analysisSettingsPanel.add(limmaPCorrBox, gridBagConstraints);

        gcContWithinMethodBox.setFont(gcContWithinMethodBox.getFont().deriveFont(gcContWithinMethodBox.getFont().getSize()-2f));
        gcContWithinMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "full", "loess", "median", "upper" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normGCBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), gcContWithinMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gcContWithinMethodBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gcContWithinMethodBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        analysisSettingsPanel.add(gcContWithinMethodBox, gridBagConstraints);

        jLabel30.setFont(jLabel30.getFont().deriveFont(jLabel30.getFont().getSize()-2f));
        jLabel30.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel30.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(jLabel30, gridBagConstraints);

        limmaCutoffBox.setFont(limmaCutoffBox.getFont().deriveFont(limmaCutoffBox.getFont().getSize()-2f));
        limmaCutoffBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.05", "0.025", "0.01", "0.005", "0.0025", "0.0005", "0.00025", "0.00005", "0.1", "0.15", "0.2", "0.25", "0.005" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        analysisSettingsPanel.add(limmaCutoffBox, gridBagConstraints);

        writeRPKMBox.setBackground(new java.awt.Color(255, 255, 255));
        writeRPKMBox.setFont(writeRPKMBox.getFont().deriveFont(writeRPKMBox.getFont().getSize()-2f));
        writeRPKMBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.writeRPKMBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(writeRPKMBox, gridBagConstraints);

        limmaMinLFCBox.setBackground(new java.awt.Color(255, 255, 255));
        limmaMinLFCBox.setFont(limmaMinLFCBox.getFont().deriveFont(limmaMinLFCBox.getFont().getSize()-2f));
        limmaMinLFCBox.setSelected(true);
        limmaMinLFCBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.limmaMinLFCBox.text")); // NOI18N
        limmaMinLFCBox.setToolTipText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.limmaMinLFCBox.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(limmaMinLFCBox, gridBagConstraints);

        jLabel27.setFont(jLabel27.getFont().deriveFont(jLabel27.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel27.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel27.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        analysisSettingsPanel.add(jLabel27, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        jButton1.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton1.text_1")); // NOI18N
        jButton1.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton1.actionCommand")); // NOI18N
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        analysisSettingsPanel.add(jButton1, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        jButton2.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton2.text_1")); // NOI18N
        jButton2.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton2.actionCommand")); // NOI18N
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        analysisSettingsPanel.add(jButton2, gridBagConstraints);

        normGCHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        normGCHelpButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.normGCHelpButton.text_1")); // NOI18N
        normGCHelpButton.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.normGCHelpButton.actionCommand")); // NOI18N
        normGCHelpButton.setBorder(null);
        normGCHelpButton.setBorderPainted(false);
        normGCHelpButton.setContentAreaFilled(false);
        normGCHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        analysisSettingsPanel.add(normGCHelpButton, gridBagConstraints);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        jButton4.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton4.text")); // NOI18N
        jButton4.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton4.actionCommand")); // NOI18N
        jButton4.setBorder(null);
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        analysisSettingsPanel.add(jButton4, gridBagConstraints);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        jButton5.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton5.text")); // NOI18N
        jButton5.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton5.actionCommand")); // NOI18N
        jButton5.setBorder(null);
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        analysisSettingsPanel.add(jButton5, gridBagConstraints);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.png"))); // NOI18N
        jButton6.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton6.text")); // NOI18N
        jButton6.setActionCommand(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jButton6.actionCommand")); // NOI18N
        jButton6.setBorder(null);
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        analysisSettingsPanel.add(jButton6, gridBagConstraints);

        dispersionLabel.setFont(dispersionLabel.getFont().deriveFont(dispersionLabel.getFont().getSize()-2f));
        dispersionLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.dispersionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        analysisSettingsPanel.add(dispersionLabel, gridBagConstraints);

        dispersionBox.setFont(dispersionBox.getFont().deriveFont(dispersionBox.getFont().getSize()-2f));
        dispersionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "auto", "common", "tagwise" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        analysisSettingsPanel.add(dispersionBox, gridBagConstraints);

        normGCBox.setBackground(new java.awt.Color(255, 255, 255));
        normGCBox.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.normGCBox.text")); // NOI18N
        normGCBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normGCBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        analysisSettingsPanel.add(normGCBox, gridBagConstraints);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(jLabel23.getFont().deriveFont(jLabel23.getFont().getSize()-2f));
        jLabel23.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel23.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normGCBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel23, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        analysisSettingsPanel.add(jLabel23, gridBagConstraints);

        gcContBetweenMethodBox.setFont(gcContBetweenMethodBox.getFont().deriveFont(gcContBetweenMethodBox.getFont().getSize()-2f));
        gcContBetweenMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "full", "median", "upper" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normGCBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), gcContBetweenMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        analysisSettingsPanel.add(gcContBetweenMethodBox, gridBagConstraints);

        org.jdesktop.layout.GroupLayout RNASeqDEStatisticsPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqDEStatisticsPanel);
        RNASeqDEStatisticsPanel.setLayout(RNASeqDEStatisticsPanelLayout);
        RNASeqDEStatisticsPanelLayout.setHorizontalGroup(
            RNASeqDEStatisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(stepPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqDEStatisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 229, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(RNASeqDEStatisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(analysisSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .add(jSeparator10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
                .addContainerGap())
        );
        RNASeqDEStatisticsPanelLayout.setVerticalGroup(
            RNASeqDEStatisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, RNASeqDEStatisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqDEStatisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RNASeqDEStatisticsPanelLayout.createSequentialGroup()
                        .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(analysisSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                    .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSeparator9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(stepPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqDEStatisticsPanel, "card8");

        RNASeqResultsBrowserPanel.setBackground(new java.awt.Color(255, 255, 255));

        stepPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        stepPanel7.setLayout(new java.awt.GridBagLayout());

        previousStepButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        previousStepButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.previousStepButton.text")); // NOI18N
        previousStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStepButtonActionPerformed(evt);
            }
        });
        stepPanel7.add(previousStepButton, new java.awt.GridBagConstraints());

        nextStepButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/close_icon.png"))); // NOI18N
        nextStepButton.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.nextStepButton.text")); // NOI18N
        nextStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextStepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        stepPanel7.add(nextStepButton, gridBagConstraints);

        jLabel22.setText(org.openide.util.NbBundle.getMessage(RNASeqWorkflowPanel.class, "RNASeqWorkflowPanel.jLabel22.text")); // NOI18N

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);

        resultBrowserContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        resultBrowserContainerPanel.setLayout(new javax.swing.BoxLayout(resultBrowserContainerPanel, javax.swing.BoxLayout.Y_AXIS));

        org.jdesktop.layout.GroupLayout RNASeqResultsBrowserPanelLayout = new org.jdesktop.layout.GroupLayout(RNASeqResultsBrowserPanel);
        RNASeqResultsBrowserPanel.setLayout(RNASeqResultsBrowserPanelLayout);
        RNASeqResultsBrowserPanelLayout.setHorizontalGroup(
            RNASeqResultsBrowserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(stepPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
            .add(RNASeqResultsBrowserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 229, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultBrowserContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE))
        );
        RNASeqResultsBrowserPanelLayout.setVerticalGroup(
            RNASeqResultsBrowserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(RNASeqResultsBrowserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(RNASeqResultsBrowserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(resultBrowserContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(stepPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(RNASeqResultsBrowserPanel, "card9");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void RSImportNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSImportNextButtonActionPerformed

        // check the input sequences: read in the say first 1000 entries and check
        // both for correctness of format and pipeline version (by reasoning on the
        // quality scores) - present the result to the user and have him confirm the
        // guess.
        DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
        dataModel.setInputFiles(new ArrayList<FastQFile>());
        for (int i = 0; i < importModel.getSize(); i++) {
            SimpleLogger.getLogger(true).logMessage("importing " + i + ": " + importModel.get(i));
            dataModel.getInputFiles().add(new FastQFile((String) importModel.get(i)));
        }

        try {
            VERSION inputVersion = delegate.checkFastQInput(dataModel.getInputFiles());
            dataModel.setQualityEncoding(inputVersion);

            switch (inputVersion) {
                case SOLEXA:
                    pipelineVersionBox.setSelectedItem("1.1");
                    pipelineVersionBox1.setSelectedItem("1.1");
                    break;
                case ILLUMINA_1_3:
                    pipelineVersionBox.setSelectedItem("1.3+");
                    pipelineVersionBox1.setSelectedItem("1.3+");
                    break;
                case ILLUMINA_1_5:
                    pipelineVersionBox.setSelectedItem("1.5+");
                    pipelineVersionBox1.setSelectedItem("1.5+");
                    break;
                case ILLUMINA_1_8:
                    pipelineVersionBox.setSelectedItem("1.8+");
                    pipelineVersionBox1.setSelectedItem("1.8+");
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            SimpleLogger.getLogger(true).logException(e);
            return;
        }

        sysInfoRefreshButtonActionPerformed(null);

        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.RAW_IMPORT);
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSQCPANEL);

}//GEN-LAST:event_RSImportNextButtonActionPerformed

    private void previousStepButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStepButton1ActionPerformed
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSIMPORTPANEL);
}//GEN-LAST:event_previousStepButton1ActionPerformed

    private void setReferenceTypeControlsEnabled(boolean b) {
        chooseRefTypeLabel.setEnabled(b);
        refGenomeButton.setEnabled(b);
        refTranscriptomeButton.setEnabled(b);
        refGenomeInfo.setEnabled(b);
        refTransInfo.setEnabled(b);
    }

    private void setMappingToolControlsEnabled(boolean state) {
        mappingToolBox.setEnabled(state);
        mappingToolInfoButton.setEnabled(state);
        mappingToolSettingsContainerPanel.setEnabled(state);
//        if (mappingToolSettingPanel != null) 
//            mappingToolSettingPanel.setEnabled(state);        
        chooseMappingToolLabel.setEnabled(state);
        selectMappingToolLabel.setEnabled(state);
    }

    private void setReferenceDataControlsEnabled(boolean state) {
        specifyRefLabel.setEnabled(state);
        referenceDataContainerPanel.setEnabled(state);
        referenceDataPanel.setEnabled(state);
    }

    private boolean qcSelected() {
        return RSbasicStatsBox.isSelected()
                || RSconsecHomoBox.isSelected()
                || RScallFreqBox.isSelected()
                || RScallQualBox.isSelected()
                || RSKmerBox.isSelected()
                || RSoverRepBox.isSelected();
    }

    private void RSQualityCheckNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSQualityCheckNextButtonActionPerformed
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.QUALITY_CHECK_SETUP);
        // ... in case there was a cancelled attempt before 
        this.setStopRunningTasks(false);

        if (!qcSelected()) {
            if (JOptionPane.showConfirmDialog(
                    this, "No quality check was selected. Click OK if\n"
                    + "you want to skip quality checking.", "No quality check?",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                //FIXME we have a problem here if we later want to
                // show the number of raw reads per imput file in the
                // result browser - need to incorporate at leat a fast
                // estimation of total raw reads step (maybe do that even earlier
                // in the import step when we read the first 1000 entries
                // to estimate the pipeline version. The estimate would be rather 
                // coarse but better than nothing                

                qcResultPanel = new JPanel();
                setupTrimmomaticPanel();
                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, RSTRIMMOMATICPANEL);
                return;
            }
            return;
        }

        qcResultPanel = new JPanel();
        qcResultPanel.setLayout(new BoxLayout(qcResultPanel, BoxLayout.Y_AXIS));
        qcResultPanel.setBackground(Color.WHITE);
        executor = Executors.newFixedThreadPool(((Number) RSQCThreadsSpinner.getValue()).intValue());

        // set up a parser for each input file and 
        // stick it in a thread
        ArrayList<FastQParserThread> qc_threads = new ArrayList<FastQParserThread>();

        for (FastQFile f : dataModel.getInputFiles()) {
            FastQParser parser;
            if (RSScanSampleBox.isSelected()) {
                if (f.getName().toLowerCase().endsWith(".bz2")
                        || f.getName().toLowerCase().endsWith(".gz")
                        || f.getName().toLowerCase().endsWith(".zip")) {
                    parser = new FastQParser(f);

                    // DEBUG
                    System.out.println(f.getName() + " is compressed - using a full parser although sample mode was chosen");

                } else {

                    parser = new FastQSampleParser(f);
                    int sampleChunkSize = 400;
                    long sampleSize = (((Number) RSScanSampleSpinner.getValue()).intValue() * 4) / sampleChunkSize;

                    //DEBUG
                    System.out.println("using a sample parser with " + sampleSize + " chunks a " + sampleChunkSize + " lines");

                    ((FastQSampleParser) parser).setSampleSize(sampleSize);
                    ((FastQSampleParser) parser).setSampleChunkSize(sampleChunkSize);
                }
            } else {
                parser = new FastQParser(f);
            }

            parser.setScanMode(true);

            if (RSbasicStatsBox.isSelected()) {
                parser.registerQualCheckMethod(new RNASeqBasicStats(parser));
            }

            if (RSconsecHomoBox.isSelected()) {
                parser.registerQualCheckMethod(new RNASeqConsecutiveHomopolymers());
            }

            if (RScallFreqBox.isSelected()) {
                parser.registerQualCheckMethod(new RNASeqBaseCallFrequencies());
            }

            if (RScallQualBox.isSelected()) {
                parser.registerQualCheckMethod(new RNASeqPerBaseQuality(dataModel.getQualityEncoding()));
                parser.registerQualCheckMethod(new RNASeqPerSequenceQuality(dataModel.getQualityEncoding()));
            }

            if (RSKmerBox.isSelected()) {

                parser.registerQualCheckMethod(new RNASeqKmerFrequency(
                        ((Number) minKmerLengthSpinner.getValue()).intValue(),
                        ((Number) maxKmerLengthSpinner.getValue()).intValue(),
                        ((Number) maxKmerUniqueSpinner.getValue()).intValue()));
            }
            if (RSoverRepBox.isSelected()) {
                parser.registerQualCheckMethod(new RNASeqOverrepSequences());
            }

            FastQParserThread thread = new FastQParserThread(parser);
            qc_threads.add(thread);
            Future fut = executor.submit(thread);
            RNASeqQCprogressPanel qpanel = new RNASeqQCprogressPanel(this, f, thread, fut);
            qcResultPanel.add(qpanel);

            Toolkit.getDefaultToolkit().addAWTEventListener(new TargetedMouseListener(qpanel), AWTEvent.MOUSE_EVENT_MASK);
        }
        RSQCResultsScrollPane.add(qcResultPanel);
        RSQCResultsScrollPane.setViewportView(qcResultPanel);
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSQCRESULTPANEL);

}//GEN-LAST:event_RSQualityCheckNextButtonActionPerformed

    private void previousStepButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStepButton2ActionPerformed

        this.setStopRunningTasks(true);

        for (Component c : RSQCResultViewScrollPane.getComponents()) {
            if (c instanceof RNASeqQCResultContainerPanel) {
                RSQCResultViewScrollPane.remove(c);
            }
        }

        RSQCResultViewScrollPane.setViewportView(RSQCDummyPanel);

        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSQCPANEL);
}//GEN-LAST:event_previousStepButton2ActionPerformed

    private ArrayList<FastQFile> exportQualchecks() throws InterruptedException, ExecutionException {

        final ProgressDialog mwin = new ProgressDialog(mainGUI, true, true);
        mwin.setText("Exporting quality check result to PDF...");
        mwin.setIndeterminate(true);

//        mainGUI.setGrayedOut(true);        

        SwingWorker< ArrayList<FastQFile>, Object> exporter = new SwingWorker<ArrayList<FastQFile>, Object>() {
            @Override
            protected ArrayList<FastQFile> doInBackground() throws Exception {
                ArrayList<FastQFile> passedInputFiles = new ArrayList<FastQFile>();
                if (qcResultPanel != null) {
                	System.out.println("hallo");
                    for (Component comp : qcResultPanel.getComponents()) {
                        if (comp instanceof RNASeqQCprogressPanel) {

                            RNASeqQCprogressPanel progpanel = (RNASeqQCprogressPanel) comp;
                            File pdf = new File(dataModel.getPlotsDir(), progpanel.getInputFile().getName() + ".pdf");
                            progpanel.mouseClicked(null);
                            System.out.println(progpanel.getInputFile().getName());
                            // pass the dataModel as a suppository....bit ugly, but works
                            ActionEvent fake = new ActionEvent(dataModel, 1, "fakeClick");
                            progpanel.getResultPanel().fakeMouseClick(fake);
                            if (!progpanel.isFinishedWithErrors()) {
                                passedInputFiles.add(progpanel.getInputFile());
                            } else {
                                progpanel.getInputFile().setComment("parsing errors");
                            }
                        }
                    }
                }
                return passedInputFiles;
            }

            @Override
            protected void done() {
                mwin.dispose();
//                mainGUI.setGrayedOut(false);

            }
        };
        exporter.execute();
        mwin.setVisible(true);
        return exporter.get();
    }

    private void RSQualResultsNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSQualResultsNextButtonActionPerformed
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.QUALITY_CHECK_DONE);
//        // remove broken files from the input        
//        ArrayList<FastQFile> passedInputFiles = new ArrayList<FastQFile>();
//        MessageWindow mwin = new MessageWindow(mainGUI, "Exporting quality check result to PDF...");
//
//        if (qcResultPanel != null) {
//            for (Component comp : qcResultPanel.getComponents()) {
//                if (comp instanceof RNASeqQCprogressPanel) {
//                    RNASeqQCprogressPanel progpanel = (RNASeqQCprogressPanel) comp;
//                    File pdf = new File(dataModel.getPlotsDir(), progpanel.getInputFile().getName() + ".pdf");
//                    progpanel.mouseClicked(null);
//
//                    // pass the dataModel as a suppository....bit ugly, but works
//                    ActionEvent fake = new ActionEvent(dataModel, 1, "fakeClick");
//                    progpanel.getResultPanel().fakeMouseClick(fake);
//                    if (!progpanel.isFinishedWithErrors()) {
//                        passedInputFiles.add(progpanel.getInputFile());
//                    } else {
//                        progpanel.getInputFile().setComment("parsing errors");
//                    }
//                }
//            }
//        }
//        mwin.dispose();

        if (!TESTMODE) {
            try {
                dataModel.setInputFiles(exportQualchecks());
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        //log passed files
        StringBuilder b = new StringBuilder();
        b.append("Input files that passed quality checking:\n");
        for (FastQFile file : dataModel.getInputFiles()) {
            b.append("\tfile: " + file.getName() + " reads:" + file.getReadCount() + "\n");
        }
        SimpleLogger.getLogger(true).logMessage(b.toString());

        setupTrimmomaticPanel();
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSTRIMMOMATICPANEL);
}//GEN-LAST:event_RSQualResultsNextButtonActionPerformed

    private void RSTrimmoPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSTrimmoPreviousButtonActionPerformed
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSQCRESULTPANEL);
}//GEN-LAST:event_RSTrimmoPreviousButtonActionPerformed

    private void RSTrimmoNextNuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSTrimmoNextNuttonActionPerformed
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.TRIMMOMATIC_SETUP);
        int trimmercount = 0;
        for (Component c : trimmoWorkFlowAreaPanel.getComponents()) {
            if (c instanceof TMModulePanel) {
                trimmercount++;
            }
        }

        if (trimmercount < 1) {
            if (JOptionPane.showConfirmDialog(
                    this, "No raw data trimming module was selected. Click OK if\n"
                    + "you are sure that you don't want to trim the data.", "No trimming?",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                trimmoSkipped = true;

                if ((isImporting && (importStage < 5)) || (!isImporting)) {
                    populateTrimmedInputList();
                }
                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, RSLIBCONFPANEL);
                return;
            }
            return;
        }

        dataModel.clearTrimmers();
        for (int i = 0; i < trimmoWorkFlowAreaPanel.getComponentCount(); i++) {
            Component comp = trimmoWorkFlowAreaPanel.getComponent(i);
            if (trimmoWorkFlowAreaPanel.getComponent(i) instanceof TMModulePanel) {

                TMTrimmerArguments args;
                try {
                    args = ((TMModulePanel) comp).getArguments();
                    if (args == null) {
                        return;
                    }
                } catch (Exception ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                    return;
                }
                dataModel.addTrimmer(args);
            }
        }
        TMProgressDialog trimmoDialog = new TMProgressDialog(mainGUI, true, dataModel, executor);
        mainGUI.setGrayedOut(true);
        trimmoDialog.setVisible(true);

        if (trimmoDialog.cancelledByUser()) {
            new SimpleErrorMessage(this, "All Trimmomatic tasks were cancelled by the user");
            mainGUI.setGrayedOut(false);
            clearTrimmoWorkflowButtonActionPerformed(null);
            return;
        }

        trimmoDialog.dispose();

        newConditionField.requestFocusInWindow();

        if (!isImporting
                || (isImporting && importBeforeLibconfigDone)) {
            populateTrimmedInputList();
        }

        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSLIBCONFPANEL);


}//GEN-LAST:event_RSTrimmoNextNuttonActionPerformed

    private void previousStepButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStepButton4ActionPerformed
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSLIBCONFPANEL);
}//GEN-LAST:event_previousStepButton4ActionPerformed

    private void mappingStepNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mappingStepNextButtonActionPerformed
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.MAPPING_CONFIG);
        setupDesignerScene();
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSDESTATSPANEL);
}//GEN-LAST:event_mappingStepNextButtonActionPerformed

    private void setupDesignerScene() {

        //    ArrayList<AbstractGroupModel> groups = new ArrayList<AbstractGroupModel>();
        //    
        //    for (Object groupPanel : groupListPanel.getComponents()) {
        //        if (groupPanel instanceof CelFileGroupPanel) {
        //            String condition = ((CelFileGroupPanel) groupPanel).getGroupName();
        //            conditions.add(condition);
        //            GroupModel mod = new GroupModel(condition, ((CelFileGroupPanel) groupPanel).getFileList());
        //            groups.add(mod);
        //        }
        //    }


        ArrayList<AbstractGroupModel> groups = new ArrayList<AbstractGroupModel>();
        for (String condition : dataModel.getConditions().keySet()) {
            groups.add(new RNASeqSample(condition, null));
        }

        analysisDesignerPanel = new AnalysisDesigner(groups);
        designerScrollPane.setViewportView(analysisDesignerPanel);
    }

    private void addImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImportButtonActionPerformed

        if (isImporting) {
            new SimpleErrorMessage(this, "You are adding new files to an imported project.\n"
                    + "Please make sure that the newly imported files are\n"
                    + "of exactly the same data format as the imported ones.\n"
                    + "If the formats differ, the downstream analysis will fail.\n"
                    + "Please consult the manual section on reloading existing\n"
                    + "projects for details.");
        }
        DefaultListModel importListModel = (DefaultListModel) importFileList.getModel();
        File[] files = null;

        RSFileChooser.setMultiSelectionEnabled(true);
        RSFileChooser.resetChoosableFileFilters();
        RSFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (RSFileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            files = RSFileChooser.getSelectedFiles();
        } else {
            RSImportNextButton.setEnabled(inputIsComplete());
            return;
        }
        for (File f : files) {
            importListModel.addElement(f.getAbsolutePath());
        }
        RSImportNextButton.setEnabled(inputIsComplete());
}//GEN-LAST:event_addImportButtonActionPerformed

    private void TESTING_simulateInputFiles() {

        TESTMODE = true;

        DefaultListModel importListModel = (DefaultListModel) importFileList.getModel();
//        importListModel.addElement("../testdata/NG-5335_1_T5K1TrB.fastq.sample.fastq");
//        importListModel.addElement("../testdata/NG-5335_1_T5K1TrL.fastq.sample.fastq");
//        importListModel.addElement("../testdata/NG-5335_1_T5K1TrM.fastq.sample.fastq");
//        importListModel.addElement("../testdata/NG-5335_1_T5K1WaB.fastq.sample.fastq");
//        importListModel.addElement("../testdata/NG-5335_1_T5K1WaL.fastq.sample.fastq");
//        importListModel.addElement("../testdata/NG-5335_1_T5K1WaM.fastq.sample.fastq");
        importListModel.addElement("../testdata/SRR392118.fastq.sample.1000.fastq");
        importListModel.addElement("../testdata/SRR392119.fastq.sample.1000.fastq");


        RSImportNextButtonActionPerformed(null);
//        checkAllQCBoxActionPerformed(null);
//        RSconsecHomoBox.setSelected(true);
//        RSbasicStatsBox.setSelected(true);
//        RSKmerBox.setSelected(true);
//        RScallFreqBox.setSelected(true);
//        RScallQualBox.setSelected(true);
//        RSoverRepBox.setSelected(true);

        RSQualityCheckNextButtonActionPerformed(null);
        RSQualResultsNextButtonActionPerformed(null);
        RSTrimmoNextNuttonActionPerformed(null);

        newConditionField.setText("control");
        addConditionButtonActionPerformed(null);
        newConditionField.setText("stress");
        addConditionButtonActionPerformed(null);
//        newConditionField.setText("wurst");
//        addConditionButtonActionPerformed(null);


        trimmedInputFileList.setSelectedIndices(new int[]{0});
        addSampleCombobox.setSelectedIndex(0);
        addSampleButtonActionPerformed(null);

        trimmedInputFileList.setSelectedIndices(new int[]{0});
        addSampleCombobox.setSelectedIndex(1);
        addSampleButtonActionPerformed(null);

//        trimmedInputFileList.setSelectedIndices(new int[]{0});
//        addSampleCombobox.setSelectedIndex(1);
//        addSampleButtonActionPerformed(null);
//
//        trimmedInputFileList.setSelectedIndices(new int[]{0});
//        addSampleCombobox.setSelectedIndex(1);
//        addSampleButtonActionPerformed(null);
//
//        trimmedInputFileList.setSelectedIndices(new int[]{0});
//        addSampleCombobox.setSelectedIndex(2);
//        addSampleButtonActionPerformed(null);
//
//        trimmedInputFileList.setSelectedIndices(new int[]{0});
//        addSampleCombobox.setSelectedIndex(2);
//        addSampleButtonActionPerformed(null);

        libConfNextButtonActionPerformed(null);

//        ActionEvent e = new ActionEvent(new Object(), 123, "Transcriptome");
//        referenceTypeChosen(e);
//        mappingToolConfirmed();
//        startMappingButtonActionPerformed(null);
    }

    private void removeImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeImportButtonActionPerformed
        DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
        int[] selectedIndices = importFileList.getSelectedIndices();
        java.util.Arrays.sort(selectedIndices);
        for (int index = selectedIndices.length - 1; index >= 0; index--) {
            importModel.removeElementAt(selectedIndices[index]);
        }
        RSImportNextButton.setEnabled(inputIsComplete());
}//GEN-LAST:event_removeImportButtonActionPerformed

    private void RScallQualBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RScallQualBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_RScallQualBoxActionPerformed

    private void RSconsecHomoBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSconsecHomoBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RSconsecHomoBoxActionPerformed

    private void RSKmerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSKmerBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RSKmerBoxActionPerformed

private void RScallFreqBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RScallFreqBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_RScallFreqBoxActionPerformed

private void RSoverRepBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSoverRepBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_RSoverRepBoxActionPerformed

private void RSbasicStatsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSbasicStatsBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_RSbasicStatsBoxActionPerformed

    public int getNumberOfParallelProcessesSetting() {
        return (Integer) RSQCThreadsSpinner.getValue();
    }

private void sysInfoRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysInfoRefreshButtonActionPerformed
    double heapSize = Utilities.longAsMegaBytes(Runtime.getRuntime().totalMemory());
    double heapMaxSize = Utilities.longAsMegaBytes(Runtime.getRuntime().maxMemory());
    double heapFreeSize = Utilities.longAsMegaBytes(Runtime.getRuntime().freeMemory());
    double freeHeapPerc = (heapFreeSize / heapMaxSize) * 100;

    double freePhys = Utilities.longAsMegaBytes(Utilities.getFreePhysicalMemorySize());
    double totalPhys = Utilities.longAsMegaBytes(Utilities.getTotalPhysicalMemorySize());
    double freePhysPerc = (freePhys / totalPhys) * 100;

    NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
    nf.setMaximumFractionDigits(1);

    javaHeapLabel.setText(
            nf.format(heapFreeSize) + "/"
            + nf.format(heapMaxSize) + "MB" + " ("
            + nf.format(freeHeapPerc) + "%)");

    freeRAMlabel.setText(
            nf.format(freePhys) + "/"
            + nf.format(totalPhys) + "MB" + " ("
            + nf.format(freePhysPerc) + "%)");

    numCPULabel.setText(Integer.toString(Utilities.getAvailableProcessors()));

    // don't know whether this makes sense
    // a bit more thinking might be required...
    RSQCThreadsSpinner.setValue(Utilities.getAvailableProcessors());
}//GEN-LAST:event_sysInfoRefreshButtonActionPerformed

private void RSScanSampleBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RSScanSampleBoxActionPerformed
    // check whether there are zipped files in the input and 
    // issue some info/warning 

    if (!RSScanSampleBox.isSelected()) {
        return;
    }
    boolean hasZip = false;

    for (File f : dataModel.getInputFiles()) {
        if (f.getName().endsWith(".gz") || f.getName().endsWith(".bz2")) {
            hasZip = true;
            break;
        }
    }

    if (hasZip) {
        new SimpleErrorMessage(this, "The input contains compressed files. Scanning samples\n"
                + "of compressed files is not supported.\n"
                + "If you proceed, all compressed files will\n"
                + "be read completely which might take very long.\n"
                + "Alternatively you can decide to unpack the files\n"
                + "and then re-run Robin.");
    }

}//GEN-LAST:event_RSScanSampleBoxActionPerformed

private void checkAllQCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAllQCBoxActionPerformed
    RSKmerBox.setSelected(checkAllQCBox.isSelected());
    RSconsecHomoBox.setSelected(checkAllQCBox.isSelected());
    RSbasicStatsBox.setSelected(checkAllQCBox.isSelected());
    RScallQualBox.setSelected(checkAllQCBox.isSelected());
    RScallFreqBox.setSelected(checkAllQCBox.isSelected());
    RSoverRepBox.setSelected(checkAllQCBox.isSelected());
}//GEN-LAST:event_checkAllQCBoxActionPerformed

private void previousStepButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStepButton5ActionPerformed
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, RSTRIMMOMATICPANEL);
}//GEN-LAST:event_previousStepButton5ActionPerformed

private void libConfNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libConfNextButtonActionPerformed

    ArrayList<RNASeqSample> definedSamples = new ArrayList<RNASeqSample>();
    HashMap<String, AtomicInteger> samplesPerCond = new HashMap<String, AtomicInteger>();

    // initialize condition-sample counter
    for (Object con : ((DefaultListModel) conditionsList.getModel()).toArray()) {
        samplesPerCond.put((String) con, new AtomicInteger(0));
    }

    for (Component comp : samplePanel.getComponents()) {
        if (comp instanceof RNASeqSamplePanel) {
            RNASeqSamplePanel p = ((RNASeqSamplePanel) comp);
            definedSamples.add(p.getSample());
            samplesPerCond.get(p.getSample().getCondition()).incrementAndGet();
        }
    }

    for (String condition : samplesPerCond.keySet()) {
        if (samplesPerCond.get(condition).get() < 1) {
            new SimpleErrorMessage(this, "There were no samples defined for condition \"" + condition + "\".\n"
                    + "Please define at least one sample for each condition or\n"
                    + "delete conditions you don't have samples for from the list.");
            return;
        }
    }

    if (definedSamples.size() < 2) {
        new SimpleErrorMessage(this, "Please define at least one sample per condition!");
        return;
    } else {
        for (RNASeqSample sample : definedSamples) {
            dataModel.addSample(sample);
        }
    }

    dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.LIBRARY_CONFIG);

    if (dataModel.isImportBAMSAM()) {
        // generate counts table from BAM/SAM input and
        // switch to the statistics

        RNASeqBAMSAMImporter bamSamImporter = new RNASeqBAMSAMImporter(dataModel, mainGUI, this);
        bamSamImporter.importSamples();
        if (bamSamImporter.importFailed()) {
            CardLayout cl = (CardLayout) this.getLayout();
            cl.show(this, RSIMPORTPANEL);
            return;
        }
    } else {
        this.setMappingToolControlsEnabled(false);
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSMAPPINGPANEL);
    }
}//GEN-LAST:event_libConfNextButtonActionPerformed
    public void finishedImportingBamSam() {
        setupDesignerScene();

        writeRPKMBox.setEnabled(false);
        normGCBox.setEnabled(false);
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSDESTATSPANEL);
    }
private void destatsPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destatsPanelPreviousButtonActionPerformed
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, RSMAPPINGPANEL);
}//GEN-LAST:event_destatsPanelPreviousButtonActionPerformed

private void destatsPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destatsPanelNextButtonActionPerformed
    dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.STATS_CONFIG);
    AnalysisDesignModel designModel = analysisDesignerPanel.getDesignModel();
    dataModel.setContrastTerms(designModel.getContrastTerms());
    dataModel.setContrastTable(designModel.getContrastTable());

    if (normGCBox.isSelected()) {
        dataModel.setNormGCMethod((String) gcContWithinMethodBox.getSelectedItem() + ";" + gcContBetweenMethodBox.getSelectedItem());
        File indexDir = new File(mainGUI.getResourcePath(), "index");
        try {
            Utilities.copyFile(new File(indexDir, dataModel.getReferenceindexName() + "_" + dataModel.getReferenceType() + ".lengths"),
                    new File(dataModel.getInputDir(), "gene.lengths"));
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }

    try {
        designModel.validateModel();
    } catch (NoConnectionsDesignException ex) {
        SimpleLogger.getLogger(true).logException(new Exception("Please draw at least one connection between two groups\n"
                + "to define which comparisons shall be computed.", ex));
        return;
    } catch (RedundantConnectionsDesignException ex) {
        SimpleLogger.getLogger(true).logException(ex);
        return;
    }

    if (writeRPKMBox.isSelected()) {
        SimpleLogger.getLogger(true).logMessage("computing RPKM values");
        final ProgressDialog m = new ProgressDialog(mainGUI, true, true);
        m.setText("Computing RPKM values...");
        m.setIndeterminate(true);
        mainGUI.stopBusyAnimation();

        SwingWorker RPKMWorker = new SwingWorker() {
            @Override
            protected void done() {
                m.dispose();
//                mainGUI.setGrayedOut(false);
                startMainAnalysis();
                mainGUI.stopBusyAnimation();
            }

            @Override
            protected Object doInBackground() throws Exception {
                if (dataModel.getReferenceType() == RNASeqDataModel.REFERENCE_TYPE.GENOME) {
                    GFF3Reader reader = new GFF3Reader();
                    FeatureList anno = null;
                    try {
                        anno = reader.read(dataModel.getGFF3annotationFile().getCanonicalPath());
                        RNASeqRPKMGenerator RPKMgenerator = new RNASeqRPKMGenerator(mainGUI, anno, dataModel);
                        RPKMgenerator.computeAmbiguousRPKMCountsTable();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {
                    RNASeqRPKMGenerator RPKMgenerator = new RNASeqRPKMGenerator(mainGUI, null, dataModel);
                    RPKMgenerator.computeAmbiguousRPKMCountsTable();
                }
                return null;
            }
        };
        RPKMWorker.execute();
//        mainGUI.setGrayedOut(true);
        m.setVisible(true);
    } else {
        startMainAnalysis();
    }

}//GEN-LAST:event_destatsPanelNextButtonActionPerformed

private void clearTrimmoWorkflowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearTrimmoWorkflowButtonActionPerformed
    for (Component c : trimmoWorkFlowAreaPanel.getComponents()) {
        if (c instanceof TMModulePanel) {
            trimmoWorkFlowAreaPanel.remove(c);
        }
    }
    for (Component c : trimmoModulePanel.getComponents()) {
        if (c instanceof TMModuleListItem) {
            ((TMModuleListItem) c).setVisible(true);
        }
    }
    trimmoModulePanel.revalidate();
    trimmoWorkFlowAreaPanel.revalidate();
    trimmoWorkFlowAreaPanel.repaint();
}//GEN-LAST:event_clearTrimmoWorkflowButtonActionPerformed

private void addStandardTrimmoModulesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStandardTrimmoModulesButtonActionPerformed
    clearTrimmoWorkflowButtonActionPerformed(null);
    for (Component c : trimmoModulePanel.getComponents()) {
        if (c instanceof TMModuleListItem) {
            TMModuleListItem item = (TMModuleListItem) c;
            if (item.getID().equals("ILLUMINACLIP") || item.getID().equals("SLIDINGWINDOW")) {
                item.setVisible(false);
            }
        }
    }
    TMIlluminaClipperPanel illupanel = new TMIlluminaClipperPanel();
    illupanel.setVisible(true);
    TMSlidingWindowTrimmerPanel winpanel = new TMSlidingWindowTrimmerPanel();
    winpanel.setVisible(true);
    trimmoWorkFlowAreaPanel.add(illupanel);
    trimmoWorkFlowAreaPanel.add(winpanel);
    trimmoWorkFlowAreaPanel.revalidate();
    trimmoWorkFlowAreaPanel.repaint();
}//GEN-LAST:event_addStandardTrimmoModulesButtonActionPerformed

private void addConditionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addConditionButtonActionPerformed
    String newCon = newConditionField.getText().replaceAll("\\s", "_");
    if (newCon.equals("")) {
        return;
    } else {
        DefaultListModel condListModel = (DefaultListModel) conditionsList.getModel();
        condListModel.addElement(newCon);

        addSampleCombobox.addItem(newCon);

        dataModel.addCondition(newCon);
        newConditionField.setText("");
        newConditionField.repaint();
    }

    if (dataModel.getConditions().size() > 1) {
        addSampleButton.setEnabled(true);
        addSampleCombobox.setEnabled(true);
        addSampleLabel.setEnabled(true);
        sampleConditionLabel.setEnabled(true);
        trimmedInputFileList.setEnabled(true);
        inputfilesScrollPane.setEnabled(true);
    }
}//GEN-LAST:event_addConditionButtonActionPerformed

private void removeConditionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConditionButtonActionPerformed
    DefaultListModel conditionsListModel = (DefaultListModel) conditionsList.getModel();
    int[] selectedIndices = conditionsList.getSelectedIndices();
    java.util.Arrays.sort(selectedIndices);

    ArrayList<String> deletedConditions = new ArrayList<String>();

    for (int index = selectedIndices.length - 1; index >= 0; index--) {
        String delCond = (String) conditionsListModel.getElementAt(selectedIndices[index]);
        deletedConditions.add(delCond);
        conditionsListModel.removeElementAt(selectedIndices[index]);
        addSampleCombobox.removeItem(delCond);
    }

    // 
    dataModel.getConditions().clear();
    ((DefaultComboBoxModel) addSampleCombobox.getModel()).removeAllElements();
    for (int i = 0; i <= conditionsListModel.getSize() - 1; i++) {
        dataModel.addCondition((String) conditionsListModel.getElementAt(i));
        ((DefaultComboBoxModel) addSampleCombobox.getModel()).addElement((String) conditionsListModel.getElementAt(i));
    }

    if (dataModel.getConditions().size() <= 1) {
        addSampleButton.setEnabled(false);
        addSampleCombobox.setEnabled(false);
        addSampleLabel.setEnabled(false);
        sampleConditionLabel.setEnabled(false);
        trimmedInputFileList.setEnabled(false);
        inputfilesScrollPane.setEnabled(false);
    }
}//GEN-LAST:event_removeConditionButtonActionPerformed

private void newConditionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newConditionFieldActionPerformed
    addConditionButtonActionPerformed(evt);
}//GEN-LAST:event_newConditionFieldActionPerformed

private void addSampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSampleButtonActionPerformed

    int[] indices = trimmedInputFileList.getSelectedIndices();

    if (indices.length < 1) {
        return;
    }

    String condition = (String) addSampleCombobox.getSelectedItem();
    ArrayList<FastQFile> files = new ArrayList<FastQFile>();

    for (int i = indices.length - 1; i >= 0; i--) {
        files.add((FastQFile) trimmedInputFileList.getModel().getElementAt(indices[i]));
        ((DefaultListModel) trimmedInputFileList.getModel()).removeElementAt(indices[i]);
    }
    trimmedInputFileList.revalidate();

    sampleConditionsTable.put(condition,
            (sampleConditionsTable.containsKey(condition))
            ? (sampleConditionsTable.get(condition) + 1) : 1);

    RNASeqSample newSample = new RNASeqSample(condition, files);
    newSample.setSampleName(condition + "_" + sampleConditionsTable.get(condition));
    RNASeqSamplePanel sPanel = new RNASeqSamplePanel(newSample);

    this.samplePanel.add(sPanel);
    this.samplePanel.revalidate();
    removeSampleButton.setEnabled(true);
    libConfNextButton.setEnabled(true);
}//GEN-LAST:event_addSampleButtonActionPerformed

private void removeSampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSampleButtonActionPerformed
    int samplecount = 0;
    for (Component comp : samplePanel.getComponents()) {
        if (comp instanceof RNASeqSamplePanel) {
            RNASeqSamplePanel sampan = (RNASeqSamplePanel) comp;

            //salvage all files before removing the sample
            if (sampan.isSelected()) {

                String cond = sampan.getSample().getCondition();
                this.sampleConditionsTable.put(cond, sampleConditionsTable.get(cond) - 1);

                for (FastQFile file : sampan.getSampleFiles()) {
                    ((DefaultListModel) trimmedInputFileList.getModel()).addElement(file);
                }
                samplePanel.remove(comp);
                samplePanel.revalidate();
                samplePanel.repaint();
            } else {
                samplecount++;
                continue;
            }
        }
    }

    if (samplecount < 1) {
        removeSampleButton.setEnabled(false);
        libConfNextButton.setEnabled(false);
    }
}//GEN-LAST:event_removeSampleButtonActionPerformed

private void referenceTypeChosen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_referenceTypeChosen

    for (Component comp : referenceDataContainerPanel.getComponents()) {
        if (comp instanceof RNASeqAbstractRefPanel) {
            referenceDataContainerPanel.remove(comp);
        }
    }

    for (Component comp : mappingToolSettingsContainerPanel.getComponents()) {
        if (comp instanceof RNASeqAbtractMappingToolSettingsPanel) {
            mappingToolSettingsContainerPanel.remove(comp);
        }
    }

    if (evt.getActionCommand().equals("Transcriptome")) {
        dataModel.setReferenceType(RNASeqDataModel.REFERENCE_TYPE.TRANSCRIPTOME);
        referenceDataPanel = new RNASeqRefTranscriptomePanel(dataModel, this);
        referenceDataPanel.setEnabled(false);
        referenceDataContainerPanel.add(referenceDataPanel);
    } else {
        dataModel.setReferenceType(RNASeqDataModel.REFERENCE_TYPE.GENOME);
        referenceDataPanel = new RNASeqReferenceGenomePanel(dataModel, this);
        referenceDataPanel.setEnabled(false);
        referenceDataContainerPanel.add(referenceDataPanel);
    }

    mappingToolSettingPanel = new RNASeqKallistoSettingsPanel(dataModel, this);
    mappingToolSettingsContainerPanel.add(mappingToolSettingPanel);
    mappingToolSettingsContainerPanel.revalidate();

    this.setReferenceTypeControlsEnabled(false);

    this.setMappingToolControlsEnabled(true);
    referenceDataContainerPanel.revalidate();

}//GEN-LAST:event_referenceTypeChosen

private void resetMappingControls(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetMappingControls

    this.timeElapsedLabel.setText("Time elapsed:");
    this.timeElapsedLabel.setEnabled(false);

    for (Component comp : referenceDataContainerPanel.getComponents()) {
        if (comp instanceof RNASeqAbstractRefPanel) {
            referenceDataContainerPanel.remove(comp);
            ((RNASeqAbstractRefPanel) comp).resetPanel();
        }
    }
    for (Component comp : mappingToolSettingsContainerPanel.getComponents()) {
        if (comp instanceof RNASeqAbtractMappingToolSettingsPanel) {
            mappingToolSettingsContainerPanel.remove(comp);
        }
    }
    for (Component comp : mappingResultPanel.getComponents()) {
        if (comp instanceof RNASeqMappingResultPanel) {
            mappingResultPanel.remove(comp);
        }
    }

    this.setReferenceTypeControlsEnabled(true);
    this.referenceTypeButtonGroup.clearSelection();
    this.setReferenceDataControlsEnabled(false);
    this.setMappingToolControlsEnabled(false);
    this.startMappingButton.setEnabled(false);
    this.stopMappingProcessButton.setEnabled(true);
    try {
        this.mappingProgressPane.getDocument().remove(0, this.mappingProgressPane.getDocument().getLength());
    } catch (BadLocationException ex) {
        SimpleLogger.getLogger(true).logException(ex);
    }
    this.repaint();
}//GEN-LAST:event_resetMappingControls


/** this starts the mapping process **/
private void startMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMappingButtonActionPerformed
    this.timeElapsedLabel.setEnabled(true);
    this.stopMappingProcessButton.setEnabled(true);
    this.setMappingToolControlsEnabled(false);
    this.setReferenceDataControlsEnabled(false);
    this.startMappingButton.setEnabled(false);
    this.resetMappingControlsButton.setEnabled(false);
    this.startMappingButton.setEnabled(false);
    this.skipMappingButton.setEnabled(false);

    previousStepButton4.setEnabled(false);

    dataModel.setMappingTool((String) mappingToolBox.getSelectedItem());
    dataModel.setMappingToolSettings(StringUtils.join(mappingToolSettingPanel.getArgs(), ";"));

    File instDir = new File(mainGUI.getInstallPath());
    File binDir = new File(instDir, "bin");
    File indexDir = new File(mainGUI.getResourcePath(), "index");

    dataModel.setReferenceFile(new File(indexDir, dataModel.getReferenceindexName()));

    /**
     * before running Bowtie, we need to make sure that all input files are
     * decompressed. This should at the trimming step; But if the user chose to
     * skip that, we need to decompress everything now.
     */
    mainGUI.startBusyAnimation("mapping reads...");
    mappingStartMillis = System.currentTimeMillis();

    //FIXME this should happen in a worker thread - if at all
    
    delegate.decompressAllInputFiles();

    if (dataModel.getReferenceType() == RNASeqDataModel.REFERENCE_TYPE.GENOME) {

        final ProgressDialog m = new ProgressDialog(mainGUI, true, true);
        m.setText("Reading GFF3 annotation file...");
        m.setIndeterminate(true);
        SwingWorker annoWorker = new SwingWorker() {
            @Override
            protected void done() {
                super.done();
                GFF3AnnotationProvider GFFAnno = null;
                try {
                    GFFAnno = (GFF3AnnotationProvider) get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                executor = Executors.newFixedThreadPool(1);
                mappingController = new RNASeqMappingProcessController(RNASeqWorkflowPanel.this, dataModel, executor);
                File instDir = new File(mainGUI.getInstallPath());
                File binDir = new File(instDir, "bin");
                File indexDir = new File(mainGUI.getResourcePath(), "index");

                for (RNASeqSample sample : dataModel.getSamples().values()) {
                    RNASeqBowtieMappingProcess process = null;

                    process = new RNASeqBowtieMappingProcess(
                            RNASeqWorkflowPanel.this,
                            sample.getSampleName(),
                            new File(binDir, "bowtie_" + delegate.getSysArchString()),
                            mappingToolSettingPanel.getArgs(),
                            new File(indexDir, dataModel.getReferenceindexName() + "_" + dataModel.getReferenceType() + "_bwtindex"),
                            sample.getInputFiles());

                    process.setAnnotation(GFFAnno);
                    mappingController.addProcess(process);
                }

                mappingController.start();
            }

            @Override
            protected Object doInBackground() {
                // for now, we are only interested in full gene regions and don't care
                // about the intron/exon/UTR substructure
                GFF3AnnotationProvider annotation = new GFF3AnnotationProvider();
                try {
                    GFF3Reader reader = new GFF3Reader();
                    FeatureList anno = reader.read(dataModel.getGFF3annotationFile().getCanonicalPath());
                    for (String featureType : RobinConstants.EXTRACT_GFF3_FEATURES_LIST) {
                        SimpleLogger.getLogger(true).logMessage("extracting feature type: " + featureType);
                        annotation.addFeatures(anno.selectByType(featureType));
                    }
                    anno = null;
                    GFF3Utils.writeLengthTableExonbased(annotation.getFeatureList(),
                            new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_lengthtable.txt"));

                } catch (IOException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                }
                m.dispose();
                return annotation;
            }
        };
        annoWorker.execute();
        m.setVisible(true);
    } else {
        executor = Executors.newFixedThreadPool(1);
        mappingController = new RNASeqMappingProcessController(this, dataModel, executor);

        File lengthsFile = new File(indexDir, dataModel.getReferenceFile().getName() + "_" + dataModel.getReferenceType() + ".lengths");
        File targetLengthsFile = new File(dataModel.getInputDir(), "gene.lengths");

        try {
            Utilities.copyFile(lengthsFile, targetLengthsFile);
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(new Exception(
                    "Could not copy the gene information file:\n"
                    + ex.getMessage()
                    + "\nIt seems like the index is corrupted.\n"
                    + "Deleting corrupted index.", ex));

            // delete everything related to this index to make sure that
            // no half-cooked index is left and tempts the user to click it
            // again later and cause problems.
            File[] badIndexFiles = indexDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.contains(dataModel.getReferenceFile().getName())) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            for (File f : badIndexFiles) {
                f.delete();
            }
            return;
        }

        for (RNASeqSample sample : dataModel.getSamples().values()) {
            RNASeqKallistoMappingProcess process = null;
            File indexFile = new File(indexDir, dataModel.getReferenceindexName());
            System.out.println("Bindirectory"+binDir);
            System.out.println("sysarch"+delegate.getSysArchString());

            process = new RNASeqKallistoMappingProcess(
                    this,
                    sample.getSampleName(),
                    /**FIXME to bindir**/
                    new File(binDir, "kallisto.exe"),
                    mappingToolSettingPanel.getArgs(),
                    new File(indexDir, dataModel.getReferenceindexName() + "_" + dataModel.getReferenceType() + "_bwtindex"),
                    sample.getInputFiles());

            //process.setAnnotation(GFFAnno);            
            mappingController.addProcess(process);
        }
        mainGUI.startBusyAnimation("mapping...");
        mappingController.setSaveAlignments(mappingToolSettingPanel.isSaveAlignments());
        mappingController.start();
    }
}//GEN-LAST:event_startMappingButtonActionPerformed

private void stopMappingProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopMappingProcessButtonActionPerformed
    mappingController.cancel();
    mainGUI.stopBusyAnimation();
}//GEN-LAST:event_stopMappingProcessButtonActionPerformed

private void previousStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStepButtonActionPerformed
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, RSDESTATSPANEL);
}//GEN-LAST:event_previousStepButtonActionPerformed

private void nextStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextStepButtonActionPerformed
        try {
            analysisFinished();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
}//GEN-LAST:event_nextStepButtonActionPerformed

private void gcContWithinMethodBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gcContWithinMethodBoxActionPerformed
    dataModel.setNormGCMethod((String) gcContWithinMethodBox.getSelectedItem());
}//GEN-LAST:event_gcContWithinMethodBoxActionPerformed

private void importSAMBAMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSAMBAMButtonActionPerformed
    addImportButtonActionPerformed(evt);
    this.dataModel.setImportBAMSAM(true);

    // put files in the dataModel .... they're NOT fastq files, but it shouldn't matter
    DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
    dataModel.setInputFiles(new ArrayList<FastQFile>());
    for (int i = 0; i < importModel.getSize(); i++) {
        System.out.println(importModel.get(i) + "i=" + i);
        FastQFile in = new FastQFile((String) importModel.get(i));
        in.setComment("BAM/SAM import");
        dataModel.getInputFiles().add(in);
    }

    // add the aligned reads files as trimmed input files
    ((DefaultListModel) trimmedInputFileList.getModel()).removeAllElements();
    for (FastQFile f : dataModel.getInputFiles()) {
        ((DefaultListModel) trimmedInputFileList.getModel()).addElement(f);//.getCanonicalPath()); HANS
    }
    newConditionField.requestFocusInWindow();

    CardLayout cl = (CardLayout) this.getLayout();
    previousStepButton5.setEnabled(false);
    cl.show(this, RSLIBCONFPANEL);
}//GEN-LAST:event_importSAMBAMButtonActionPerformed

private void alternativeInputCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alternativeInputCheckBoxActionPerformed
    showAlternativeInputs(alternativeInputCheckBox.isSelected());
}//GEN-LAST:event_alternativeInputCheckBoxActionPerformed

private void handleHelpButtonClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handleHelpButtonClick
    helpHandler.showHelpDialogForKey("rnaseqhelp." + evt.getActionCommand(), mainGUI);   
}//GEN-LAST:event_handleHelpButtonClick

    private void edgeRRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeRRadioButtonActionPerformed
        dispersionBox.removeAllItems();
        for (String method : new String[]{"auto", "common", "tagwise"}) { // removed trended since it gave errors
            dispersionBox.addItem(method);
        }
    }//GEN-LAST:event_edgeRRadioButtonActionPerformed

    private void deseqRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deseqRadioButtonActionPerformed
        dispersionBox.removeAllItems();
        for (String method : new String[]{"pooled", "per-condition", "blind"}) {
            dispersionBox.addItem(method);
        }
    }//GEN-LAST:event_deseqRadioButtonActionPerformed

    private void normGCBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normGCBoxActionPerformed
        if (normGCBox.isSelected()) {
            dataModel.setNormGCMethod((String) gcContWithinMethodBox.getSelectedItem());
        } else {
            dataModel.setNormGCMethod("none");
        }
    }//GEN-LAST:event_normGCBoxActionPerformed

private void importCountsTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCountsTableButtonActionPerformed

    dataModel.setImportCountsTable(true);
    dataModel.setReferenceType(RNASeqDataModel.REFERENCE_TYPE.NONE);

    File cTable = null;

    RSFileChooser.setMultiSelectionEnabled(false);
    RSFileChooser.resetChoosableFileFilters();
    RSFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    if (RSFileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        cTable = RSFileChooser.getSelectedFile();
        dataModel.setImportCountsTableFile(cTable);
    } else {
        RSImportNextButton.setEnabled(false);
        return;
    }

    try {
        if (!delegate.importCountsTable(dataModel)) {
            return;
        }
        // GC normalization not possible with counts data
        normGCBox.setEnabled(false);
    } catch (FileNotFoundException ex) {
        Exceptions.printStackTrace(ex);
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }
    
    dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.MAPPING_CONFIG);
    setupDesignerScene();
    destatsPanelPreviousButton.setEnabled(false);
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, RSDESTATSPANEL);
}//GEN-LAST:event_importCountsTableButtonActionPerformed

    private void skipMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipMappingButtonActionPerformed
        dataModel.setImportCountsTable(true);
        dataModel.setImportCountsTableFile(new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt"));

        try {
            if (!delegate.importCountsTable(dataModel)) {
                return;
            }
            // GC normalization not possible with counts data
            normGCBox.setEnabled(false);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        setupDesignerScene();
        destatsPanelPreviousButton.setEnabled(false);
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, RSDESTATSPANEL);


    }//GEN-LAST:event_skipMappingButtonActionPerformed

    private void showAlternativeInputs(boolean show) {
        sambamLabel.setVisible(show);
        importSAMBAMButton.setVisible(show);
        importCountsTableButton.setVisible(show);
        countsTableLabel.setVisible(show);
        countsTableImportHelp.setVisible(show);
    }

    private boolean inputIsComplete() {
        DefaultListModel importListModel = (DefaultListModel) importFileList.getModel();
        if ((importListModel.getSize() > 0)) {
            return true;
        } else {
            return false;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel QCFramePanel;
    private javax.swing.JTabbedPane QCSettingsPane;
    private javax.swing.JPanel RNASeqDEStatisticsPanel;
    private javax.swing.JPanel RNASeqImportPanel;
    private javax.swing.JPanel RNASeqLibraryConfiguration;
    private javax.swing.JPanel RNASeqMappingPanel;
    private javax.swing.JPanel RNASeqQualityCheckPanel;
    private javax.swing.JPanel RNASeqQualityResultsPane;
    private javax.swing.JPanel RNASeqResultsBrowserPanel;
    private javax.swing.JPanel RNASeqTrimmomaticPanel;
    private javax.swing.JFileChooser RSFileChooser;
    private javax.swing.JButton RSImportNextButton;
    private javax.swing.JCheckBox RSKmerBox;
    private javax.swing.JButton RSKmerButton;
    private javax.swing.JPanel RSKmerSettingsPanel;
    private javax.swing.JPanel RSQCDummyPanel;
    private javax.swing.JPanel RSQCFileSettingsPanel;
    public javax.swing.JScrollPane RSQCResultViewScrollPane;
    private javax.swing.JScrollPane RSQCResultsScrollPane;
    private javax.swing.JSpinner RSQCThreadsSpinner;
    private javax.swing.JPanel RSQCgeneralSettingsPanel;
    private javax.swing.JButton RSQualResultsNextButton;
    private javax.swing.JButton RSQualityCheckNextButton;
    private javax.swing.JCheckBox RSScanSampleBox;
    private javax.swing.JSpinner RSScanSampleSpinner;
    private javax.swing.JButton RSTrimmoNextNutton;
    private javax.swing.JButton RSTrimmoPreviousButton;
    private javax.swing.JCheckBox RSbasicStatsBox;
    private javax.swing.JButton RSbasicStatsButton;
    private javax.swing.JCheckBox RScallFreqBox;
    private javax.swing.JButton RScallFreqButton;
    private javax.swing.JCheckBox RScallQualBox;
    private javax.swing.JButton RScallQualButton;
    private javax.swing.JCheckBox RSconsecHomoBox;
    private javax.swing.JButton RSconsecHomoButton;
    private javax.swing.JCheckBox RSoverRepBox;
    private javax.swing.JButton RSoverRepButton;
    private javax.swing.JScrollPane TMWorkflowScrollpane;
    private javax.swing.JButton addConditionButton;
    private javax.swing.JButton addImportButton;
    private javax.swing.JButton addSampleButton;
    private javax.swing.JComboBox addSampleCombobox;
    private javax.swing.JLabel addSampleLabel;
    private javax.swing.JButton addStandardTrimmoModulesButton;
    private javax.swing.JCheckBox alternativeInputCheckBox;
    private javax.swing.JPanel analysisSettingsPanel;
    private javax.swing.ButtonGroup analysisTypeButtonGroup;
    private javax.swing.JCheckBox checkAllQCBox;
    private javax.swing.JLabel chooseMappingToolLabel;
    private javax.swing.JLabel chooseRefTypeLabel;
    private javax.swing.JButton clearTrimmoWorkflowButton;
    private javax.swing.JList conditionsList;
    private javax.swing.JPanel conditionsPanel;
    private javax.swing.JButton countsTableImportHelp;
    private javax.swing.JLabel countsTableLabel;
    private javax.swing.JRadioButton deseqRadioButton;
    private javax.swing.JScrollPane designerScrollPane;
    private javax.swing.JButton destatsPanelNextButton;
    private javax.swing.JButton destatsPanelPreviousButton;
    private javax.swing.JComboBox dispersionBox;
    private javax.swing.JLabel dispersionLabel;
    private javax.swing.JRadioButton edgeRRadioButton;
    private javax.swing.JCheckBox forceManualIlluBox;
    private javax.swing.JCheckBox forceManualIlluBox1;
    private javax.swing.JLabel freeRAMlabel;
    private javax.swing.JComboBox gcContBetweenMethodBox;
    private javax.swing.JComboBox gcContWithinMethodBox;
    private javax.swing.JButton importCountsTableButton;
    private javax.swing.JList importFileList;
    private javax.swing.JButton importSAMBAMButton;
    private javax.swing.JScrollPane inputfilesScrollPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel javaHeapLabel;
    private javax.swing.JLabel labelxyz;
    private javax.swing.JButton libConfNextButton;
    private javax.swing.JComboBox limmaCutoffBox;
    private javax.swing.JCheckBox limmaMinLFCBox;
    private javax.swing.JComboBox limmaPCorrBox;
    private javax.swing.JEditorPane mappingProgressPane;
    private javax.swing.JPanel mappingResultPanel;
    private javax.swing.JScrollPane mappingResultScrollPane;
    private javax.swing.JButton mappingStepNextButton;
    private javax.swing.JComboBox mappingToolBox;
    private javax.swing.JButton mappingToolInfoButton;
    private javax.swing.JPanel mappingToolSettingsContainerPanel;
    private javax.swing.JSpinner maxKmerLengthSpinner;
    private javax.swing.JSpinner maxKmerUniqueSpinner;
    private javax.swing.JSpinner minKmerLengthSpinner;
    private javax.swing.JTextField newConditionField;
    private javax.swing.JButton nextStepButton;
    private javax.swing.JCheckBox normGCBox;
    private javax.swing.JButton normGCHelpButton;
    private javax.swing.JLabel numCPULabel;
    private javax.swing.JComboBox pipelineVersionBox;
    private javax.swing.JComboBox pipelineVersionBox1;
    private javax.swing.JButton previousStepButton;
    private javax.swing.JButton previousStepButton1;
    private javax.swing.JButton previousStepButton2;
    private javax.swing.JButton previousStepButton4;
    private javax.swing.JButton previousStepButton5;
    private javax.swing.JRadioButton refGenomeButton;
    private javax.swing.JLabel refGenomeInfo;
    private javax.swing.JLabel refTransInfo;
    private javax.swing.JRadioButton refTranscriptomeButton;
    private javax.swing.JPanel referenceDataContainerPanel;
    private javax.swing.ButtonGroup referenceTypeButtonGroup;
    private javax.swing.JButton removeConditionButton;
    private javax.swing.JButton removeImportButton;
    private javax.swing.JButton removeSampleButton;
    private javax.swing.JButton resetMappingControlsButton;
    private javax.swing.JPanel resultBrowserContainerPanel;
    private javax.swing.JLabel sambamLabel;
    private javax.swing.JLabel sampleConditionLabel;
    private javax.swing.JPanel samplePanel;
    private javax.swing.JLabel selectMappingToolLabel;
    private javax.swing.JButton skipMappingButton;
    private javax.swing.JLabel specifyRefLabel;
    private javax.swing.JButton startMappingButton;
    private javax.swing.JPanel stepPanel;
    private javax.swing.JPanel stepPanel1;
    private javax.swing.JPanel stepPanel2;
    private javax.swing.JPanel stepPanel3;
    private javax.swing.JPanel stepPanel4;
    private javax.swing.JPanel stepPanel5;
    private javax.swing.JPanel stepPanel6;
    private javax.swing.JPanel stepPanel7;
    private javax.swing.JButton stopMappingProcessButton;
    private javax.swing.JButton sysInfoRefreshButton;
    private javax.swing.JLabel timeElapsedLabel;
    private javax.swing.JList trimmedInputFileList;
    private javax.swing.JPanel trimmoModulePanel;
    private javax.swing.JScrollPane trimmoModuleScrollPane;
    private javax.swing.JCheckBox writeRPKMBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void finishGracefully() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void initGUIFromModel() {

        // stage 1 : only raw data was loaded
        if (dataModel.getWorkflowStage().value() < 1) {
            return;
        }

        DefaultListModel input = new DefaultListModel();
        for (FastQFile f : dataModel.getInputFiles()) {
            try {
                input.addElement(f.getCanonicalPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        importFileList.setModel(input);
        RSImportNextButton.setEnabled(true);

        // stage 5 : trimmo done - we can init trimmo from the model
        if (dataModel.getWorkflowStage().value() < 5) {
            return;
        }

        initializeTrimmomaticFromModel();

        if (dataModel.getWorkflowStage().value() < 6) {
            return;
        }

        initializeLibConfigFromModel();

        if (dataModel.getWorkflowStage().value() < 7) {
            return;
        }

        initializeMappingConfigFromModel();

        if (dataModel.getWorkflowStage().value() < 8) {
            return;
        }

        initializeStatsSettingsFromModel();
    }

    @Override
    public void setImportMode(File importProject, DefaultListModel listModel) {
        SimpleLogger.getLogger(true).logMessage("import file received: " + importProject.getAbsolutePath());
        this.isImporting = true;
        String name = dataModel.getExperimentName();

        try {
            // init model from previous file
            dataModel.load(importProject);
            importStage = dataModel.getWorkflowStage().value();

            if (dataModel.getWorkflowStage().value() < 7) {
                importBeforeLibconfigDone = true;
            }

            if (dataModel.getWorkflowStage() == RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.COMPLETE_ANALYSIS) {
                importsCompleteAnalysis = true;
                //browseResultsOnly();
            }
            dataModel.setExperimentName(name);
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        // nor we need to initialize all GUI elements with what we find in the
        // loaded data model and then set the stage back to 1?
        initGUIFromModel();
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.RAW_IMPORT);
    }

    @Override
    protected void resetGUI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void startMainAnalysis() {

        RScriptGenerator generator = new RScriptGenerator();
        String code = null;

//        mainGUI.setGrayedOut(true);

        dataModel.setMinLogFoldChangeOf2(limmaMinLFCBox.isSelected());
        dataModel.setPValCorrectionMethod((String) limmaPCorrBox.getSelectedItem());
        dataModel.setPValCutoffValue((String) limmaCutoffBox.getSelectedItem());
        dataModel.setWriteRawExprs(writeRPKMBox.isSelected());

        switch (Integer.parseInt(analysisTypeButtonGroup.getSelection().getActionCommand())) {
            case 0:
                dataModel.setAnalysisType(RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.LIMMA);
                dataModel.setStatStrategy((String) gcContWithinMethodBox.getSelectedItem());
                code = generator.generateRNASeqLimmaScript(dataModel);
                break;
            case 1:
                dataModel.setAnalysisType(RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.EDGER);
                dataModel.setDispersion((String) dispersionBox.getSelectedItem());

                // use generalized linear models if there are more than 2 conditions
                if (dataModel.getConditions().keySet().size() > 2) {
                    SimpleLogger.getLogger(true).logMessage("Using edgeR GLM approach");
                    code = generator.generateRNASeqEdgeRScript(dataModel, true);
                } else {
                    SimpleLogger.getLogger(true).logMessage("Using edgeR exactTest approach");
                    code = generator.generateRNASeqEdgeRScript(dataModel, false);
                }
                break;
            case 2:
                dataModel.setAnalysisType(RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.DESEQ);
                dataModel.setDispersion((String) dispersionBox.getSelectedItem());
                code = generator.generateRNASeqDESeqScript(dataModel);
                break;
            default:
                try {
                    throw new Exception("you should never end up here");
                } catch (Exception e) {
                    SimpleLogger.getLogger(true).logException(e);
                }
        }


        SimpleLogger.getLogger(true).logMessage("Ran main RNA-Seq analysis with settings:\n"
                + dataModel.getMainAnalysisSettingsAsString());

        // save the main script file
        File sourceDir = new File(dataModel.getProjectDir(), "source");
        File mainScriptFileName =
                new File(sourceDir, dataModel.getExperimentName() + "_" + dataModel.getAnalysisType() + "_main_analysis.R");
        mainGUI.startBusyAnimation("running analysis...");
        final RTask task = prepareTask(mainScriptFileName, code);
        Properties defaults = mainGUI.getDefaultSettings();
        List<String> args = Arrays.asList(defaults.getProperty("ArgsToR").split("\\|"));

        String R_path = defaults.getProperty("PathToR") + defaults.getProperty("CommandToRunR");

        //DEBUG
        //System.out.println("R path from defaults: <" + R_path + ">");


        REngine engine = new REngine(R_path, mainGUI);
        try {
            RProcessResult result = engine.runRScript(mainScriptFileName, args, mainGUI, "Running main analysis...");

            if (result.getExitValue() == 0) {
                mainGUI.stopBusyAnimation();
                this.RTaskFinished();
            } else {
                new VerboseWarningDialog(mainGUI,
                        "R process failure",
                        "Exit code:" + result.getExitValue(), result.getOutput());
                System.exit(1);
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private void setupTrimmomaticPanel() {
        if (this.trimmoPanelIsReady) {
            return;
        }
        String[] modules = {
            "BARCODESPLITTER",
            "ILLUMINACLIP",
            "LEADING",
            "CROP",
            "TRAILING",
            "SLIDINGWINDOW",
            "MINLEN"
        };

        trimmoModulePanel = new JPanel();
        trimmoModulePanel.setLayout(new BoxLayout(trimmoModulePanel, BoxLayout.Y_AXIS));
        trimmoModulePanel.setBackground(Color.WHITE);

        for (String mod : modules) {
            String title = org.openide.util.NbBundle.getMessage(TMModuleListItem.class, "TMModule.title." + mod);
            String desc = org.openide.util.NbBundle.getMessage(TMModuleListItem.class, "TMModule.desc." + mod);

            TMModuleListItem item = new TMModuleListItem(mod, title, desc, (RobinMainGUI) this.getMainGUI());
            trimmoModulePanel.add(new JSeparator(JSeparator.HORIZONTAL));
            trimmoModulePanel.add(item);
        }
        trimmoModulePanel.add(new JSeparator(JSeparator.HORIZONTAL));
        trimmoModulePanel.revalidate();
        trimmoModulePanel.setVisible(true);
        trimmoModuleScrollPane.setViewportView(trimmoModulePanel);
        this.trimmoPanelIsReady = true;
    }

    private void populateTrimmedInputList() {

        ((DefaultListModel) trimmedInputFileList.getModel()).removeAllElements();
        for (FastQFile f : dataModel.getInputFiles()) {
            if (trimmoSkipped) {
                f.setFilteredReadCount(f.getReadCount());
            }
            ((DefaultListModel) trimmedInputFileList.getModel()).addElement(f);//.getCanonicalPath()); HANS
        }
        newConditionField.requestFocusInWindow();
    }

    public void mappingToolConfirmed() {
        specifyRefLabel.setEnabled(true);
        referenceDataPanel.setEnabled(true);
        this.setMappingToolControlsEnabled(false);
    }

    public void readyToStartMapping() {
        this.startMappingButton.setEnabled(true);
        this.startMappingButton.requestFocusInWindow();
    }

    public RNASeqDataModel getDataModel() {
        return this.dataModel;
    }

    public void mappingStepFinished(final boolean userCancel) {

        if (userCancel) {
            this.appendToMappingProgressPane(
                    "------------------------------------\n"
                    + "All mapping tasks cancelled by user.\n"
                    + "------------------------------------\n", RobinConstants.attrBoldRed);

        } else {
            this.appendToMappingProgressPane(
                    "----------------------------\n"
                    + "All mapping tasks finished.\n"
                    + "----------------------------\n", RobinConstants.attrBoldGreen);
        }

        final SimpleIntegerDataFrame countsTable = mappingController.getCountsTable();



        if (countsTable.getDimensions()[0] <= 1) {
            this.appendToMappingProgressPane("\nNo valid alignments were found using the parameters chosen.\n"
                    + "Please rerun the mapping step with relaxed parameters or\n"
                    + "using a different reference sequence", RobinConstants.attrBoldRed);

            new SimpleErrorMessage(this, "No valid alignments were found using the parameters chosen.\n"
                    + "Please rerun the mapping step with relaxed parameters or\n"
                    + "using a different reference sequence");

            SimpleLogger.getLogger(true).logMessage("No valid alignments found - cancelling mapping step.");
            resetMappingControls(null);
            mainGUI.stopBusyAnimation();
            return;
        }

        this.appendToMappingProgressPane("\nThe reads could be mapped to a total"
                + " of " + countsTable.getDimensions()[0] + " different transcripts.", RobinConstants.attrBoldBlack);

        SimpleLogger.getLogger(true).logMessage("The reads could be mapped to a total"
                + " of " + countsTable.getDimensions()[0] + " different transcripts.");

        delegate.computeMappingOverviewStats(countsTable);

        final ProgressDialog m = new ProgressDialog(mainGUI, true, true);
        m.setText("Computing mapping overview stats values");
        m.setIndeterminate(true);

        SimpleLogger.getLogger(true).logMessage("Computing mapping overview stats");

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                ArrayList<RNASeqAbstractMappingProcess> badSamples = new ArrayList<RNASeqAbstractMappingProcess>();

                JPanel mappingResultPanel = new JPanel();
                mappingResultPanel.setBackground(Color.WHITE);
                mappingResultPanel.setLayout(new BoxLayout(mappingResultPanel, BoxLayout.Y_AXIS));

                for (int i = 0; i < countsTable.getDimensions()[1]; i++) {

                    RNASeqAbstractMappingProcess process = mappingController.getProcessByName(countsTable.getColname(i));


                    // get charts
                    ChartPanel panel = delegate.getCountFreqDistributionChartForColumn(i, countsTable, new Dimension(100, 50));
                  /* ChartPanel mismatchPanel = ((RNASeqBowtieMappingProcess) process).getErrorRecorder()
                            .getMismatchRatesPlot(new Dimension(100, 50), "Positional alignment mismatch");
*/
                    /**TODO remove mismtach panel **/
                    RNASeqMappingResultPanel rPan =
                            new RNASeqMappingResultPanel(countsTable.getColname(i), panel, panel /*mismatchPanel*/, process.getReport());

                    if (process.getPercentAligned() < RobinConstants.PERCENTAGE_READS_ALIGNED_WARNING_THRESHOLD) {
                        badSamples.add(process);
                        rPan.setWarning(true);
                    }

                    mappingResultPanel.add(rPan);

                    JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
                    sep.setBackground(Color.WHITE);
                    mappingResultPanel.add(sep);
                }

                // at least one sample had a low percentage of aligned reads.
                // show a warning
                if (badSamples.size() > 0) {

                    String msg = "In one or more samples, less than 10% of the reads could\n"
                            + "be aligned to the chosen reference. Please make sure that\n"
                            + "you use the correct reference genome / transcriptome.\n\n"
                            + "Another reason why this can happen are too strict BOWTIE\n"
                            + "settings. Please consider relaxing the settings to allow more\n"
                            + "mismatches and or shorten the seed length. When re-running\n"
                            + "BOWTIE with more permissive settings you should also inspect\n"
                            + "the mismatch rate plots for each sample. If one sample shows\n"
                            + "an accumulation of alignment mismatches at the start of the\n"
                            + "reads you should consider trimming off the first few bases.\n\n"
                            + "The affected samples are:\n";

                    for (RNASeqAbstractMappingProcess p : badSamples) {
                        msg += p.getName() + "  " + p.getPercentAligned() + "% reads aligned\n";
                    }

                    CollapsibleInfoDialog dialog =
                            new CollapsibleInfoDialog(mainGUI, JOptionPane.WARNING_MESSAGE,
                            "Less than 10% of the reads aligned\nin one or more samples", msg);
                    dialog.setVisible(true);

                }
                return mappingResultPanel;
            }

            @Override
            protected void done() {
                try {
                    mappingResultPanel = (JPanel) get();
                    mappingResultScrollPane.add(mappingResultPanel);
                    mappingResultScrollPane.setViewportView(mappingResultPanel);
                    mainGUI.stopBusyAnimation();
                    if (!userCancel) {
                        mappingStepNextButton.setEnabled(true);
                    }
                    stopMappingProcessButton.setEnabled(false);
                    startMappingButton.setEnabled(false);
                    resetMappingControlsButton.setEnabled(true);
                    m.dispose();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        };
        worker.execute();
        m.setVisible(true);
    }

    @Override
    public void RTaskFinished() {

        int choice = JOptionPane.showOptionDialog(
                mainGUI,
                "Analysis of differential gene expression finished sucessfully.\n"
                + "in the next step you can annotate the results with\n"
                + "MapMan bins if there is a mapping available for your\n"
                + "input data.",
                "Analysis finished successfully",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[]{"Annotate", "Skip"},
                "Annotate");

        if (choice == 0) {
            // Annotatation wanted?
            File mappingsPath = new File(mainGUI.getResourcePath(), "mappings");
            File resultFilePath = new File(
                    dataModel.getProjectDir(),
                    dataModel.getExperimentName().concat("_results.txt"));

            ResultAnnotationDialog annoDialog = new ResultAnnotationDialog(mainGUI, true, mappingsPath, resultFilePath);
            annoDialog.setVisible(true);
        }


        // initialize result browser in a worker thread        
        final ProgressDialog mwin = new ProgressDialog(mainGUI, true, true);
        mwin.setText("Generating project summary...");
        mwin.setIndeterminate(true);
        mainGUI.startBusyAnimation("working...");

        SwingWorker<RNASeqResultBrowser, Object> resultWorker = new SwingWorker<RNASeqResultBrowser, Object>() {
            @Override
            protected void done() {
                for (Component c : resultBrowserContainerPanel.getComponents()) {
                    if (c instanceof RNASeqResultBrowser) {
                        resultBrowserContainerPanel.remove(c);
                    }
                }
                try {
                    resultBrowserContainerPanel.add(get());
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                resultBrowserContainerPanel.revalidate();
                resultBrowserContainerPanel.repaint();

                //super.done();
                mainGUI.stopBusyAnimation();
                mwin.dispose();
                CardLayout cl = (CardLayout) RNASeqWorkflowPanel.this.getLayout();
                cl.show(RNASeqWorkflowPanel.this, RSRESULTSBROWSERPANEL);
            }

            @Override
            protected RNASeqResultBrowser doInBackground() throws Exception {

                // generate PDF summary 
                RNASeqPDFSummaryGenerator pdfGen = new RNASeqPDFSummaryGenerator(dataModel, mainGUI);

                RNASeqResultBrowser browser = new RNASeqResultBrowser(RNASeqWorkflowPanel.this, dataModel);
                browser.setVisible(true);
                return browser;
            }
        };

        resultWorker.execute();
        mwin.setVisible(true);
    }

    private void analysisFinished() throws IOException {
        int choice = 0;
        dataModel.setWorkflowStage(RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.COMPLETE_ANALYSIS);
        try {
            choice = JOptionPane.showOptionDialog(
                    mainGUI,
                    "<html><h2>Finished successfully!</h2><br>Results were written to:<br>"
                    + dataModel.getProjectDir().getCanonicalPath()
                    + "<br>Click \"Restart\" to import new data<br>"
                    + "and re-run the analysis.<br>Clicking \"Exit\" will close Robin",
                    "Finished",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    UIManager.getIcon("OptionPane.questionIcon"),
                    new Object[]{"Restart", "Modify", "Exit", "View data in MapMan"},
                    "Restart");

        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        //System.out.println("choice is: " + choice);
        // Restart
        if (choice == 0) {
            // TODO flush everything and move to card 1
            //flushAndResetVariables();
            RobinMainGUI newMain = new RobinMainGUI();
            mainGUI.setVisible(false);
            mainGUI.dispose();
            newMain.setVisible(true);

            // Modify
        } else if (choice == 1) {
            // go back to the statistics panel
            CardLayout cl = (CardLayout) RNASeqWorkflowPanel.this.getLayout();
            cl.show(RNASeqWorkflowPanel.this, RSDESTATSPANEL);
        } else if (choice == 2) {
            System.exit(0);
        } else if (choice == 3) {
            File resultFile = new File(
                    dataModel.getOutputDir(),
                    dataModel.getExperimentName().concat("_results.txt"));

            boolean success = MapCommunicator.postExperimentToMapMan(resultFile, mainGUI.getDefaultSettings());

            if (success) {
                int whatNext = JOptionPane.showOptionDialog(
                        mainGUI,
                        "<html><h2>Data successfully transferred to MapMan</h2><br>"
                        + "<br>Click \"Restart\" if you want to analyse anohter<br>"
                        + "dataset. Clicking \"Exit\" will close Robin",
                        "Data transferred",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        UIManager.getIcon("OptionPane.questionIcon"),
                        new Object[]{"Restart", "Exit"},
                        "Restart");

                if (whatNext == 0) {
                    // TODO flush everything and move to card 1
                    //flushAndResetVariables();
                    RobinMainGUI newMain = new RobinMainGUI();
                    mainGUI.setVisible(false);
                    newMain.setVisible(true);
                    mainGUI.dispose();
                } else if (whatNext == 1) {
                    System.exit(0);
                }
            } else {
                // OOPS the secret walkie talkie did not work
                System.out.println("Robin can't phone MapMan");
            }
        }
    }

    private void initializeTrimmomaticFromModel() {
        setupTrimmomaticPanel();
        clearTrimmoWorkflowButtonActionPerformed(null);

        for (TMTrimmerArguments argSet : dataModel.getTrimmers()) {
            // take component from the drag list
            for (Component c : trimmoModulePanel.getComponents()) {
                if (c instanceof TMModuleListItem) {
                    TMModuleListItem item = (TMModuleListItem) c;
                    if (item.getID().equals(argSet.getIdentifier())) {
                        item.setVisible(false);
                    }
                }
            }
            String ID = argSet.getIdentifier();
            TMModulePanel panel = null;

            if (ID.equals("BARCODESPLITTER")) {
                panel = new BarcodeSplitterPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("ILLUMINACLIP")) {
                panel = new TMIlluminaClipperPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("LEADING")) {
                panel = new TMLeadingTrimmerPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("CROP")) {
                panel = new TMCropperPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("TRAILING")) {
                panel = new TMTrailingTrimmerPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("SLIDINGWINDOW")) {
                panel = new TMSlidingWindowTrimmerPanel();
                panel.setArguments(argSet);
            } else if (ID.equals("MINLEN")) {
                panel = new TMMinimumLengthTrimmerPanel();
                panel.setArguments(argSet);
            }
            panel.setVisible(true);
            trimmoWorkFlowAreaPanel.add(panel);
        }
        trimmoWorkFlowAreaPanel.revalidate();
        trimmoWorkFlowAreaPanel.repaint();
    }

    private void initializeLibConfigFromModel() {
        populateTrimmedInputList();

        for (String con : dataModel.getConditions().keySet()) {
            newConditionField.setText(con);
            addConditionButtonActionPerformed(null);
        }

        for (RNASeqSample sample : dataModel.getSamples().values()) {
            ArrayList<Integer> indices = new ArrayList<Integer>();
            for (FastQFile file : sample.getInputFiles()) {
                indices.add(((DefaultListModel) trimmedInputFileList.getModel()).indexOf(file));
            }



            for (int i = indices.size() - 1; i >= 0; i--) {
                ((DefaultListModel) trimmedInputFileList.getModel()).removeElementAt(i);
            }

            trimmedInputFileList.revalidate();

            sampleConditionsTable.put(sample.getCondition(),
                    (sampleConditionsTable.containsKey(sample.getCondition()))
                    ? (sampleConditionsTable.get(sample.getCondition()) + 1) : 1);

            RNASeqSamplePanel sPanel = new RNASeqSamplePanel(sample);
            this.samplePanel.add(sPanel);
            this.samplePanel.revalidate();
            removeSampleButton.setEnabled(true);
        }
    }

    private void initializeMappingConfigFromModel() {
        String cmd;
        if (dataModel.getReferenceType() == RNASeqDataModel.REFERENCE_TYPE.TRANSCRIPTOME) {
            cmd = "Transcriptome";
        } else {
            cmd = "Genome";
        }
        ActionEvent e = new ActionEvent(new Object(), 123, cmd);
        referenceTypeChosen(e);
        mappingToolSettingPanel.configureWithString(dataModel.getMappingToolSettings());
        referenceDataPanel.setReferenceIndex(dataModel.getReferenceindexName());

        if (new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_raw_countstable.txt").exists()) {
            skipMappingButton.setEnabled(true);
        }
    }

    private void initializeStatsSettingsFromModel() {

        if (dataModel.getAnalysisType() == RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.EDGER) {
            edgeRRadioButton.setSelected(true);
        } else if (dataModel.getAnalysisType() == RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.DESEQ) {
            deseqRadioButton.setSelected(true);
        } else if (dataModel.getAnalysisType() == RNASeqDataModel.RNASEQ_ANALYSIS_TYPE.LIMMA) {
            //limmaRadioButton.setSelected(true);
        }

        limmaCutoffBox.setSelectedItem(dataModel.getPValCutoffValue());
        limmaPCorrBox.setSelectedItem(dataModel.getPValCorrectionMethod());
        limmaMinLFCBox.setSelected(dataModel.isMinLogFoldChangeOf2());
        writeRPKMBox.setSelected(dataModel.isWriteRawExprs());

        if (dataModel.getNormGCMethod().equals("none")) {
            normGCBox.setSelected(false);
        } else {
            normGCBox.setSelected(true);
            gcContWithinMethodBox.setSelectedItem(dataModel.getNormGCMethod());
        }
    }

    private void browseResultsOnly() {


        int choice = JOptionPane.showOptionDialog(
                mainGUI,
                "You are importing a completed analysis.\n"
                + "Click \"Browse\" if you just want to browse the\n"
                + "results or \"Rerun\" if you want modify the settings\n"
                + "and rerun the analysis",
                "Browse results?",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[]{"Browse", "Rerun"},
                "Browse");

        if (choice != 0) {
            return;
        }

        // initialize result browser in a worker thread        
        final ProgressDialog mwin = new ProgressDialog(mainGUI, true, true);
        mwin.setText("Generating project summary...");
        mwin.setIndeterminate(TESTMODE);
        mainGUI.startBusyAnimation("working...");

        SwingWorker<RNASeqResultBrowser, Object> resultWorker = new SwingWorker<RNASeqResultBrowser, Object>() {
            @Override
            protected void done() {
                for (Component c : resultBrowserContainerPanel.getComponents()) {
                    if (c instanceof RNASeqResultBrowser) {
                        resultBrowserContainerPanel.remove(c);
                    }
                }
                try {
                    resultBrowserContainerPanel.add(get());
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                resultBrowserContainerPanel.revalidate();
                resultBrowserContainerPanel.repaint();

                //super.done();
                mainGUI.stopBusyAnimation();
                mwin.dispose();
                CardLayout cl = (CardLayout) RNASeqWorkflowPanel.this.getLayout();
                cl.show(RNASeqWorkflowPanel.this, RSRESULTSBROWSERPANEL);
            }

            @Override
            protected RNASeqResultBrowser doInBackground() throws Exception {

                RNASeqResultBrowser browser = new RNASeqResultBrowser(RNASeqWorkflowPanel.this, dataModel);
                browser.setVisible(true);
                return browser;
            }
        };

        resultWorker.execute();
        mwin.setVisible(true);
    }
}
