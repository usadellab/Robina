/*
 * SCMainPanel.java
 *
 * Created on 9. Oktober 2008, 13:58
 */

package de.mpimp.golm.robin.GUI.singlechannel;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.R.RTaskController;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.GUI.RobinWorkflow;
import de.mpimp.golm.robin.GUI.affy.CelFileGroupPanel;
import de.mpimp.golm.robin.GUI.affy.QCResultListItem;
import de.mpimp.golm.robin.IPC.MapCommunicator;
import de.mpimp.golm.robin.R.RScriptEditor;
import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
import de.mpimp.golm.robin.data.ArrayDataModel.AnalysisStrategy;
import de.mpimp.golm.robin.data.ArrayDataModel.InputDataType;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.robin.designer.GUI.AnalysisDesigner;
import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.designer.model.GroupModel;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.robin.designer.model.NoConnectionsDesignException;
import de.mpimp.golm.robin.designer.model.RedundantConnectionsDesignException;
import de.mpimp.golm.robin.misc.RobinAnalysisSummaryGenerator;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.dom4j.DocumentException;
import org.openide.util.Exceptions;

/**
 *
 * @author  marc
 */
public class SCWorkflowPanel extends RobinWorkflow {
    
    private static final String     IMPORT = "card2";
    private static final String     QCCHOICE = "card10";
    private static final String     QCRESULTS = "card5";
    private static final String     EXPERIMENTDESIGNER = "card6";
    private static final String     SCGROUPCONFIGUREPANEL = "card7";
    private static final String     SCRANKPRODGROUPCONFIGUREPANEL = "card8";

    private String                  sFileSep = System.getProperty("file.separator");
    private ArrayList<String>       conditions = new ArrayList<String>();
    private SCArrayDataModel        dataModel;
    private ArrayList<String>       selectedQC;
    private SCDelegate              delegate;
    private JPanel                  resultPanel;
    private AnalysisDesigner        analysisDesignerPanel;
    
    // flags
    private boolean qcPerformed;

    private JPanel groupListPanel;
    private boolean groupListPanelIsInitialzed = false;
    
    
    
    /** Creates new form TCMainPanel
     * @param main
     * @param projectPath
     */
    public SCWorkflowPanel(RobinMainGUI main, File projectPath) {
        super(main,  projectPath);
        initComponents();
        
        this.dataModel = new SCArrayDataModel();
        this.delegate = new SCDelegate();

        // get the tmp file root dir
        File tempRoot;
        try {
            tempRoot = File.createTempFile("robin_", "");
            dataModel.setTempRoot(tempRoot.getCanonicalPath());
            tempRoot.delete();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        try {
            this.dataModel.setOutputDir(projectPath.getCanonicalPath());
            this.dataModel.setExperimentName(Utilities.extractFileNamePathComponent(projectPath.getCanonicalPath()));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, IMPORT);  
        this.setVisible(true);
    }

    public void setImportMode(File importProject, DefaultListModel listModel) {
        //DEBUG
        System.out.println("sc import mode");
        importFileList.setModel(listModel);
        SCImportNextButton.setEnabled(true);
        isImporting = true;
        try {
            this.dataModel.load(importProject);
            this.dataModel.setDataType("generic");
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(ex);
            System.exit(1);
        }

        initGUIFromModel();        
    }

    protected void initGUIFromModel() {
        bgCorrCheckBox.setSelected(dataModel.isDoBackgroundCorrection());
        bgCorrMethodBox.setSelectedItem(dataModel.getBackGroundCorrectionMethod());

        normBetCheckBox.setSelected(dataModel.isDoNormBetween());
        normBetMethodBox.setSelectedItem(dataModel.getNormBetweenArrays());

        scMainPValBox.setSelectedItem(dataModel.getPValCorrectionMethod());
        scMainTestingStrategyBox.setSelectedItem(dataModel.getStatStrategy());
        pValCutoffBox1.setSelectedItem(dataModel.getPValCutoffValue());
        wantsMinLFCBox.setSelected(dataModel.isMinLogFoldChangeOf2());

        if (dataModel.getAnalysisType() == AnalysisStrategy.LIMMA) {
            limmaORrpBox.setSelectedItem("Linear models (package limma)");
        } else {
            limmaORrpBox.setSelectedItem("Rank product (package RankProd)");
        }
    }

    public void finishGracefully() {
        //TODO
        System.out.println("FINISHING GRACEFULLY");
        
        // disable controls until the task has finished
        TCQCResultsNextButton.setEnabled(true);
        TCQCResultsPreviousButton.setEnabled(true);
        TCExperimentDesignerNextButton.setEnabled(true);
        TCExperimentDesignerPreviousButton.setEnabled(true);
        
        if (dataModel.getWarningsHandler().hasWarnings()) {
            if (!dataModel.getWarningsHandler().showWarnings(mainGUI)) {
                
                System.out.println("User cancel after warning messages");
                return;
            }
        }
        
        try {
            // save the summary
            RobinAnalysisSummaryGenerator.writeAnalysisSummary(dataModel);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        // ask whether the user wants to annotate the results
        //File libPath = new File(mainGUI.getInstallPath(), "lib");
        File mappingsPath = new File(mainGUI.getResourcePath(), "mappings");
        File resultFilePath = new File(
                dataModel.getOutputDir(),
                dataModel.getExperimentName().concat("_results.txt"));

        ResultAnnotationDialog annoDialog = new ResultAnnotationDialog(mainGUI, true, mappingsPath, resultFilePath);
        annoDialog.setVisible(true);

        // save the dataModel to the source folder
        File sourcePath = new File(dataModel.getOutputDirFile(), "source");
        try {
            dataModel.store(new File(sourcePath, dataModel.getExperimentName() + "_data.xml"));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }        
        
        int choice = JOptionPane.showOptionDialog(
                            mainGUI,
                            "<html><h2>Finished successfully!</h2><br>Results were written to:<br>"+
                            dataModel.getOutputDir() +
                            "<br>Click \"Modify\" if you want to modify the design<br>"+
                            "and re-run the analysis. Be sure to specify<br>" +
                            "a different name for the output folder unless<br>" +
                            "you want to overwrite the results of the previous<br>" +
                            "analysis run! Clicking \"Exit\" will close Robin<br>" +
                            "If you want to view the results in MapMan please start<br>" +
                            "MapMan now. Robin will try to automatically transfer the<br>" +
                            "analysed data set to MapMan.",
                            "Finished", 
                            JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.QUESTION_MESSAGE, 
                            UIManager.getIcon("OptionPane.questionIcon"),
                            new Object[] { "Restart","Modify", "Exit", "View Data in MapMan"}, //"Restart",
                            "Exit"
                            );
                    
                    
        // Restart
        if (choice == 0) {
            // TODO flush everything and move to card 1
            resetGUI();
            RobinMainGUI newMain = new RobinMainGUI();
            mainGUI.setVisible(false);
            mainGUI.dispose();
            newMain.setVisible(true);
            
        
        // Modify
        } else if (choice == 1) {
            //enableDesignControls(true);
            return;    

        // Exit
        } else if (choice == 2) {
            System.exit(0);
        } else if (choice == 3) {
            
            File resultFile;
            File checkRedundant = resultFile = new File(
                        dataModel.getOutputDir(),
                        dataModel.getExperimentName().concat("_results.noduplicates.txt") );

            if (checkRedundant.exists()) {
                resultFile = checkRedundant;
            } else {
                resultFile = new File(
                    dataModel.getOutputDir(),
                    dataModel.getExperimentName().concat("_results.txt") );
            }

            boolean success = MapCommunicator.postExperimentToMapMan(resultFile, dataModel.getAsProperties());

            if (success) {
                int whatNext = JOptionPane.showOptionDialog(
                mainGUI,
                "<html><h2>Data successfully transferred to MapMan</h2><br>" +
                "<br>Click \"Restart\" if you want to analyse another<br>"+
                "dataset. Clicking \"Exit\" will close Robin.</html>",
                "Data transferred",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[] {"Restart", "Exit"},
                "Restart"
                );

                if (whatNext == 0) {
                    // TODO flush everything and move to card 1
                    resetGUI();
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
                int whatNext = JOptionPane.showOptionDialog(
                mainGUI,
                "<html><h2>MapMan data import failure</h2><br>" +
                "The data could be transferred to MapMan, but the import<br>" +
                "failed. If the problem persists please seek advice in the<br>" +
                "MapMan forum (http://mapman.gabipd.org/web/guest/forum)<br>" +
                "<br>Click \"Restart\" if you want to analyse another<br>"+
                "dataset. Clicking \"Exit\" will close Robin.</html>",
                "Data transfer problem",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[] {"Restart", "Exit"},
                "Restart"
                );

                if (whatNext == 0) {
                    // TODO flush everything and move to card 1
                    resetGUI();
                    RobinMainGUI newMain = new RobinMainGUI();
                    mainGUI.setVisible(false);
                    newMain.setVisible(true);
                    mainGUI.dispose();
                } else if (whatNext == 1) {
                    System.exit(0);
                }
            }
        }
    }
    
    protected void resetGUI() {
        //TODO implement reset method
        
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        SCFileChooser = new javax.swing.JFileChooser();
        importTypeRadioGroup = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        SCImportPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        SCImportNextButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        importFileList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                Object item = getModel().getElementAt(index);

                // Return the tool tip text
                return (String) item;
            }
        };
        addImportButton = new javax.swing.JButton();
        removeImportButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        SCQCPanel = new javax.swing.JPanel();
        scQCControlPanel1 = new javax.swing.JPanel();
        SCQualcheckPanelPreviousButton = new javax.swing.JButton();
        SCQualcheckPanelNextButton = new javax.swing.JButton();
        qcLabel1 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        scqcInfoTextArea = new javax.swing.JTextArea();
        qcChoicePanel1 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        scQCBoxplotBox = new javax.swing.JCheckBox();
        moreRNAdigButton1 = new javax.swing.JButton();
        jPanel32 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        scQCHistBox = new javax.swing.JCheckBox();
        moreHistButton1 = new javax.swing.JButton();
        jPanel34 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        scQCScatterBox = new javax.swing.JCheckBox();
        moreScatterButton1 = new javax.swing.JButton();
        expertSettingsBox1 = new javax.swing.JCheckBox();
        expertSettingsPanel = new javax.swing.JPanel();
        limmaORrpBox = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        pValCorrHelpButton3 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        bgCorrMethodBox = new javax.swing.JComboBox();
        jLabel49 = new javax.swing.JLabel();
        normBetMethodBox = new javax.swing.JComboBox();
        bgCorrCheckBox = new javax.swing.JCheckBox();
        normBetCheckBox = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        scMainTestingStrategyBox = new javax.swing.JComboBox();
        scMainPValBox = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        pValCutoffBox1 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        wantsMinLFCBox = new javax.swing.JCheckBox();
        wantsRawDataBox = new javax.swing.JCheckBox();
        isGuruBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel36 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        scQCHclustBox = new javax.swing.JCheckBox();
        morePCAButton1 = new javax.swing.JButton();
        jPanel38 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        scQCBGImageBox = new javax.swing.JCheckBox();
        moreNUSEButton1 = new javax.swing.JButton();
        scQCIncludeAllBox = new javax.swing.JCheckBox();
        jLabel48 = new javax.swing.JLabel();
        SCQCResultsPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        TCQCResultsPreviousButton = new javax.swing.JButton();
        TCQCResultsNextButton = new javax.swing.JButton();
        TCqualchecklabel1 = new javax.swing.JLabel();
        TCQCResultScrollPane = new javax.swing.JScrollPane();
        SCconfigureGroupsPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        configGroupsPanelPreviousButton = new javax.swing.JButton();
        configGroupsPanelNextButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        groupsScrollPane = new javax.swing.JScrollPane();
        addGroupButton = new javax.swing.JButton();
        deleteGroupButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        scInputFileList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                Object item = getModel().getElementAt(index);

                // Return the tool tip text
                return (String) item;
            }
        };
        saveGroupListButton = new javax.swing.JButton();
        loadGroupListButton = new javax.swing.JButton();
        SCconfigureRankProdGroupsPanel = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        confRPPreviousButton = new javax.swing.JButton();
        confRPNextButton = new javax.swing.JButton();
        groupsPanelRP = new javax.swing.JPanel();
        group1PanelRP = new javax.swing.JPanel();
        addFileToGroup1Button = new javax.swing.JButton();
        removeFileFromGroup1Button = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        group1NameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        group1Table = new javax.swing.JTable();
        group1PanelRP1 = new javax.swing.JPanel();
        addFileToGroup2Button = new javax.swing.JButton();
        removeFileFromGroup2Button = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        group2NameField = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        group2Table = new javax.swing.JTable();
        jPanel24 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        celFileListRP = new javax.swing.JList() {
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                Object item = getModel().getElementAt(index);

                // Return the tool tip text
                return (String) item;
            }
        };
        SCExperimentDesignerPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        TCExperimentDesignerPreviousButton = new javax.swing.JButton();
        TCExperimentDesignerNextButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        designerScrollPane = new javax.swing.JScrollPane();
        jButton8 = new javax.swing.JButton();
        createMetagroupButton = new javax.swing.JButton();
        deleteMetagroupButton = new javax.swing.JButton();

        SCFileChooser.setMultiSelectionEnabled(true);

        setLayout(new java.awt.CardLayout());

        SCImportPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new java.awt.GridBagLayout());

        SCImportNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        SCImportNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.SCImportNextButton.text")); // NOI18N
        SCImportNextButton.setEnabled(false);
        SCImportNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SCImportNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel4.add(SCImportNextButton, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel5.text")); // NOI18N

        jScrollPane3.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Imported files"));

        importFileList.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setViewportView(importFileList);
        DefaultListModel importListModel = new DefaultListModel();
        ListDataListener l = new ListDataListener() {

            public void intervalAdded(ListDataEvent e) {
                SCImportNextButton.setEnabled(true);
            }

            public void intervalRemoved(ListDataEvent e) {
                if (importFileList.getModel().getSize() == 0) {
                    SCImportNextButton.setEnabled(false);
                } else {
                    SCImportNextButton.setEnabled(true);
                }
            }
            public void contentsChanged(ListDataEvent e) {
            }
        };
        importListModel.addListDataListener(l);
        importFileList.setModel(importListModel);

        addImportButton.setBackground(new java.awt.Color(255, 255, 255));
        addImportButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.addImportButton.text")); // NOI18N
        addImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addImportButton.setContentAreaFilled(false);
        addImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImportButtonActionPerformed(evt);
            }
        });

        removeImportButton.setBackground(new java.awt.Color(255, 255, 255));
        removeImportButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.removeImportButton.text")); // NOI18N
        removeImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeImportButton.setContentAreaFilled(false);
        removeImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImportButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jPanel1.border.title"))); // NOI18N

        jRadioButton2.setBackground(new java.awt.Color(255, 255, 255));
        importTypeRadioGroup.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jRadioButton2.text")); // NOI18N
        jRadioButton2.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jRadioButton2.actionCommand")); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jRadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        importTypeRadioGroup.add(jRadioButton1);
        jRadioButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jRadioButton1.text")); // NOI18N
        jRadioButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jRadioButton1.actionCommand")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRadioButton1)
                    .add(jRadioButton2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton2)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout SCImportPanelLayout = new org.jdesktop.layout.GroupLayout(SCImportPanel);
        SCImportPanel.setLayout(SCImportPanelLayout);
        SCImportPanelLayout.setHorizontalGroup(
            SCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCImportPanelLayout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .add(SCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 268, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 739, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCImportPanelLayout.createSequentialGroup()
                .addContainerGap(898, Short.MAX_VALUE)
                .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        SCImportPanelLayout.setVerticalGroup(
            SCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCImportPanelLayout.createSequentialGroup()
                .add(SCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, SCImportPanelLayout.createSequentialGroup()
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(SCImportPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCImportPanel, "card2");

        SCQCPanel.setBackground(new java.awt.Color(255, 255, 255));

        scQCControlPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scQCControlPanel1.setLayout(new java.awt.GridBagLayout());

        SCQualcheckPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        SCQualcheckPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.SCQualcheckPanelPreviousButton.text")); // NOI18N
        SCQualcheckPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SCQualcheckPanelPreviousButtonActionPerformed(evt);
            }
        });
        scQCControlPanel1.add(SCQualcheckPanelPreviousButton, new java.awt.GridBagConstraints());

        SCQualcheckPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        SCQualcheckPanelNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.SCQualcheckPanelNextButton.text")); // NOI18N
        SCQualcheckPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SCQualcheckPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        scQCControlPanel1.add(SCQualcheckPanelNextButton, gridBagConstraints);

        qcLabel1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.qcLabel1.text")); // NOI18N

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jPanel25.border.title"))); // NOI18N

        jScrollPane8.setBorder(null);

        scqcInfoTextArea.setColumns(20);
        scqcInfoTextArea.setEditable(false);
        scqcInfoTextArea.setFont(new java.awt.Font("Arial", 0, 12));
        scqcInfoTextArea.setLineWrap(true);
        scqcInfoTextArea.setRows(5);
        scqcInfoTextArea.setWrapStyleWord(true);
        scqcInfoTextArea.setBorder(null);
        jScrollPane8.setViewportView(scqcInfoTextArea);

        org.jdesktop.layout.GroupLayout jPanel25Layout = new org.jdesktop.layout.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );

        qcChoicePanel1.setBackground(new java.awt.Color(255, 255, 255));
        qcChoicePanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel30.setBackground(new java.awt.Color(214, 232, 255));
        jPanel30.setLayout(new javax.swing.BoxLayout(jPanel30, javax.swing.BoxLayout.LINE_AXIS));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/TCmaplot.png"))); // NOI18N
        jLabel35.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel30.add(jLabel35);

        jLabel36.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel36.text")); // NOI18N
        jPanel30.add(jLabel36);

        jPanel31.setBackground(new java.awt.Color(214, 232, 255));
        jPanel31.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel31.setLayout(new javax.swing.BoxLayout(jPanel31, javax.swing.BoxLayout.LINE_AXIS));

        scQCBoxplotBox.setBackground(new java.awt.Color(214, 232, 255));
        scQCBoxplotBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCBoxplotBox.text")); // NOI18N
        scQCBoxplotBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCBoxplotBoxActionPerformed(evt);
            }
        });
        jPanel31.add(scQCBoxplotBox);

        moreRNAdigButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreRNAdigButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreRNAdigButton1.text")); // NOI18N
        moreRNAdigButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreRNAdigButton1.actionCommand")); // NOI18N
        moreRNAdigButton1.setBorderPainted(false);
        moreRNAdigButton1.setContentAreaFilled(false);
        moreRNAdigButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });
        jPanel31.add(moreRNAdigButton1);

        jPanel30.add(jPanel31);

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setLayout(new javax.swing.BoxLayout(jPanel32, javax.swing.BoxLayout.LINE_AXIS));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/HistSmall.jpg"))); // NOI18N
        jLabel37.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel32.add(jLabel37);

        jLabel38.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel38.text")); // NOI18N
        jPanel32.add(jLabel38);

        jPanel33.setBackground(new java.awt.Color(255, 255, 255));
        jPanel33.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel33.setLayout(new javax.swing.BoxLayout(jPanel33, javax.swing.BoxLayout.LINE_AXIS));

        scQCHistBox.setBackground(new java.awt.Color(255, 255, 255));
        scQCHistBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCHistBox.text")); // NOI18N
        scQCHistBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCHistBoxActionPerformed(evt);
            }
        });
        jPanel33.add(scQCHistBox);

        moreHistButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreHistButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreHistButton1.text")); // NOI18N
        moreHistButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreHistButton1.actionCommand")); // NOI18N
        moreHistButton1.setBorderPainted(false);
        moreHistButton1.setContentAreaFilled(false);
        moreHistButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });
        jPanel33.add(moreHistButton1);

        jPanel32.add(jPanel33);

        jPanel34.setBackground(new java.awt.Color(214, 232, 255));
        jPanel34.setLayout(new javax.swing.BoxLayout(jPanel34, javax.swing.BoxLayout.LINE_AXIS));

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/ScatSmall.jpg"))); // NOI18N
        jLabel39.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel34.add(jLabel39);

        jLabel40.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel40.text")); // NOI18N
        jPanel34.add(jLabel40);

        jPanel35.setBackground(new java.awt.Color(214, 232, 255));
        jPanel35.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel35.setLayout(new javax.swing.BoxLayout(jPanel35, javax.swing.BoxLayout.LINE_AXIS));

        scQCScatterBox.setBackground(new java.awt.Color(214, 232, 255));
        scQCScatterBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCScatterBox.text")); // NOI18N
        scQCScatterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCScatterBoxActionPerformed(evt);
            }
        });
        jPanel35.add(scQCScatterBox);

        moreScatterButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreScatterButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreScatterButton1.text")); // NOI18N
        moreScatterButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreScatterButton1.actionCommand")); // NOI18N
        moreScatterButton1.setBorderPainted(false);
        moreScatterButton1.setContentAreaFilled(false);
        moreScatterButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });
        jPanel35.add(moreScatterButton1);

        jPanel34.add(jPanel35);

        expertSettingsBox1.setBackground(new java.awt.Color(255, 255, 255));
        expertSettingsBox1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.expertSettingsBox1.text")); // NOI18N
        expertSettingsBox1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        expertSettingsBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expertSettingsBox1ActionPerformed(evt);
            }
        });

        expertSettingsPanel.setBackground(new java.awt.Color(255, 255, 255));
        expertSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.expertSettingsPanel.border.title"))); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, expertSettingsBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), expertSettingsPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        limmaORrpBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear models (package limma)", "Rank product (package RankProd)" }));

        jLabel43.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel43.text")); // NOI18N

        pValCorrHelpButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        pValCorrHelpButton3.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.pValCorrHelpButton3.actionCommand")); // NOI18N
        pValCorrHelpButton3.setBorderPainted(false);
        pValCorrHelpButton3.setContentAreaFilled(false);
        pValCorrHelpButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });

        jLabel41.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel41.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bgCorrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel41, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        bgCorrMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "subtract", "half", "minimum", "movingmin", "edwards", "normexp", "none" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bgCorrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), bgCorrMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel49.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel49.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normBetCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel49, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        normBetMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "scale", "quantile", "none" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normBetCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), normBetMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        normBetMethodBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normBetMethodBoxActionPerformed(evt);
            }
        });

        bgCorrCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        bgCorrCheckBox.setSelected(true);
        bgCorrCheckBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.bgCorrCheckBox.text")); // NOI18N

        normBetCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        normBetCheckBox.setSelected(true);
        normBetCheckBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.normBetCheckBox.text")); // NOI18N

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel12.text")); // NOI18N

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel13.text")); // NOI18N

        scMainTestingStrategyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "separate", "nestedF", "global", "hierarchical" }));
        scMainTestingStrategyBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scMainTestingStrategyBoxActionPerformed(evt);
            }
        });

        scMainPValBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BH", "BY", "fdr", "holm", "none" }));

        jLabel14.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel14.text")); // NOI18N

        jLabel30.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel30.text")); // NOI18N

        pValCutoffBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.05", "0.005", "0.0005", "0.00005", "0.1", "0.15", "0.2", "0.25" }));

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel15.text")); // NOI18N

        wantsMinLFCBox.setBackground(new java.awt.Color(255, 255, 255));
        wantsMinLFCBox.setSelected(true);
        wantsMinLFCBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.wantsMinLFCBox.text")); // NOI18N

        wantsRawDataBox.setBackground(new java.awt.Color(255, 255, 255));
        wantsRawDataBox.setSelected(true);
        wantsRawDataBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.wantsRawDataBox.text")); // NOI18N

        isGuruBox.setBackground(new java.awt.Color(255, 255, 255));
        isGuruBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.isGuruBox.text")); // NOI18N

        org.jdesktop.layout.GroupLayout expertSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(expertSettingsPanel);
        expertSettingsPanel.setLayout(expertSettingsPanelLayout);
        expertSettingsPanelLayout.setHorizontalGroup(
            expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(jLabel43)
                        .add(18, 18, 18)
                        .add(limmaORrpBox, 0, 265, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pValCorrHelpButton3))
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(bgCorrCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel41)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bgCorrMethodBox, 0, 245, Short.MAX_VALUE))
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(normBetCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel49)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(normBetMethodBox, 0, 227, Short.MAX_VALUE))
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(jLabel15)
                        .add(330, 330, 330))
                    .add(jSeparator4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(expertSettingsPanelLayout.createSequentialGroup()
                            .add(jLabel30)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pValCutoffBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(jLabel12)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, expertSettingsPanelLayout.createSequentialGroup()
                            .add(jLabel14)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(scMainPValBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(expertSettingsPanelLayout.createSequentialGroup()
                            .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(scMainTestingStrategyBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(wantsMinLFCBox)
                    .add(wantsRawDataBox)
                    .add(isGuruBox))
                .addContainerGap())
        );
        expertSettingsPanelLayout.setVerticalGroup(
            expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertSettingsPanelLayout.createSequentialGroup()
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel12)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(expertSettingsPanelLayout.createSequentialGroup()
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(bgCorrMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(bgCorrCheckBox)
                                    .add(jLabel41))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(normBetMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(normBetCheckBox)
                                    .add(jLabel49))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(14, 14, 14)
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel43)
                                    .add(limmaORrpBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(pValCorrHelpButton3)))
                            .add(expertSettingsPanelLayout.createSequentialGroup()
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(scMainTestingStrategyBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel14)
                                    .add(scMainPValBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel30)
                                    .add(pValCutoffBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(wantsMinLFCBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(wantsRawDataBox)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(isGuruBox))
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel36.setBackground(new java.awt.Color(255, 255, 255));
        jPanel36.setLayout(new javax.swing.BoxLayout(jPanel36, javax.swing.BoxLayout.LINE_AXIS));

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/pca_small.png"))); // NOI18N
        jLabel44.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel36.add(jLabel44);

        jLabel45.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel45.text")); // NOI18N
        jPanel36.add(jLabel45);

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel37.setLayout(new javax.swing.BoxLayout(jPanel37, javax.swing.BoxLayout.LINE_AXIS));

        scQCHclustBox.setBackground(new java.awt.Color(255, 255, 255));
        scQCHclustBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCHclustBox.text")); // NOI18N
        scQCHclustBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCHclustBoxActionPerformed(evt);
            }
        });
        jPanel37.add(scQCHclustBox);

        morePCAButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        morePCAButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.morePCAButton1.text")); // NOI18N
        morePCAButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.morePCAButton1.actionCommand")); // NOI18N
        morePCAButton1.setBorderPainted(false);
        morePCAButton1.setContentAreaFilled(false);
        morePCAButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });
        jPanel37.add(morePCAButton1);

        jPanel36.add(jPanel37);

        jPanel38.setBackground(new java.awt.Color(214, 232, 255));
        jPanel38.setLayout(new javax.swing.BoxLayout(jPanel38, javax.swing.BoxLayout.LINE_AXIS));

        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/image_bg_small.png"))); // NOI18N
        jLabel46.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel38.add(jLabel46);

        jLabel47.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel47.text")); // NOI18N
        jPanel38.add(jLabel47);

        jPanel39.setBackground(new java.awt.Color(214, 232, 255));
        jPanel39.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel39.setLayout(new javax.swing.BoxLayout(jPanel39, javax.swing.BoxLayout.LINE_AXIS));

        scQCBGImageBox.setBackground(new java.awt.Color(214, 232, 255));
        scQCBGImageBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCBGImageBox.text")); // NOI18N
        scQCBGImageBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCBGImageBoxActionPerformed(evt);
            }
        });
        jPanel39.add(scQCBGImageBox);

        moreNUSEButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreNUSEButton1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreNUSEButton1.text")); // NOI18N
        moreNUSEButton1.setActionCommand(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.moreNUSEButton1.actionCommand")); // NOI18N
        moreNUSEButton1.setBorderPainted(false);
        moreNUSEButton1.setContentAreaFilled(false);
        moreNUSEButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handleQCInfoButtonPressed(evt);
            }
        });
        jPanel39.add(moreNUSEButton1);

        jPanel38.add(jPanel39);

        scQCIncludeAllBox.setBackground(new java.awt.Color(255, 255, 255));
        scQCIncludeAllBox.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.scQCIncludeAllBox.text")); // NOI18N
        scQCIncludeAllBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        scQCIncludeAllBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scQCIncludeAllBoxActionPerformed(evt);
            }
        });

        jLabel48.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel48.text")); // NOI18N

        org.jdesktop.layout.GroupLayout qcChoicePanel1Layout = new org.jdesktop.layout.GroupLayout(qcChoicePanel1);
        qcChoicePanel1.setLayout(qcChoicePanel1Layout);
        qcChoicePanel1Layout.setHorizontalGroup(
            qcChoicePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel38, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .add(jPanel36, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .add(jPanel34, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .add(jPanel32, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .add(jPanel30, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, qcChoicePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(expertSettingsBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 291, Short.MAX_VALUE)
                .add(scQCIncludeAllBox)
                .addContainerGap())
            .add(qcChoicePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(expertSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        qcChoicePanel1Layout.setVerticalGroup(
            qcChoicePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcChoicePanel1Layout.createSequentialGroup()
                .add(jPanel30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(qcChoicePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(qcChoicePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(scQCIncludeAllBox)
                        .add(jLabel48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(expertSettingsBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertSettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        expertSettingsPanel.setVisible(false);

        org.jdesktop.layout.GroupLayout SCQCPanelLayout = new org.jdesktop.layout.GroupLayout(SCQCPanel);
        SCQCPanel.setLayout(SCQCPanelLayout);
        SCQCPanelLayout.setHorizontalGroup(
            SCQCPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SCQCPanelLayout.createSequentialGroup()
                .add(8, 8, 8)
                .add(SCQCPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(qcLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel25, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(qcChoicePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(scQCControlPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
        );
        SCQCPanelLayout.setVerticalGroup(
            SCQCPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCQCPanelLayout.createSequentialGroup()
                .add(SCQCPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SCQCPanelLayout.createSequentialGroup()
                        .add(qcLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel25, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(qcChoicePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scQCControlPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCQCPanel, "card10");

        SCQCResultsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel7.setLayout(new java.awt.GridBagLayout());

        TCQCResultsPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCQCResultsPreviousButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.TCQCResultsPreviousButton.text")); // NOI18N
        TCQCResultsPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCResultsPreviousButtonActionPerformed(evt);
            }
        });
        jPanel7.add(TCQCResultsPreviousButton, new java.awt.GridBagConstraints());

        TCQCResultsNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCQCResultsNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.TCQCResultsNextButton.text")); // NOI18N
        TCQCResultsNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCResultsNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel7.add(TCQCResultsNextButton, gridBagConstraints);

        TCqualchecklabel1.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.TCqualchecklabel1.text")); // NOI18N

        TCQCResultScrollPane.setBorder(null);

        org.jdesktop.layout.GroupLayout SCQCResultsPanelLayout = new org.jdesktop.layout.GroupLayout(SCQCResultsPanel);
        SCQCResultsPanel.setLayout(SCQCResultsPanelLayout);
        SCQCResultsPanelLayout.setHorizontalGroup(
            SCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
            .add(SCQCResultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCqualchecklabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TCQCResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addContainerGap())
        );
        SCQCResultsPanelLayout.setVerticalGroup(
            SCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SCQCResultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCqualchecklabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(TCQCResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCQCResultsPanel, "card5");

        SCconfigureGroupsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.GridBagLayout());

        configGroupsPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        configGroupsPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.configGroupsPanelPreviousButton.text")); // NOI18N
        configGroupsPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configGroupsPanelPreviousButtonActionPerformed(evt);
            }
        });
        jPanel5.add(configGroupsPanelPreviousButton, new java.awt.GridBagConstraints());

        configGroupsPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        configGroupsPanelNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.configGroupsPanelNextButton.text")); // NOI18N
        configGroupsPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configGroupsPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel5.add(configGroupsPanelNextButton, gridBagConstraints);

        jLabel10.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel10.text")); // NOI18N

        groupsScrollPane.setBorder(null);

        addGroupButton.setBackground(new java.awt.Color(255, 255, 255));
        addGroupButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.addGroupButton.text")); // NOI18N
        addGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupButtonActionPerformed(evt);
            }
        });

        deleteGroupButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteGroupButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.deleteGroupButton.text")); // NOI18N
        deleteGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteGroupButtonActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jPanel21.border.title"))); // NOI18N

        jScrollPane5.setBorder(null);

        jScrollPane5.setViewportView(scInputFileList);

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );

        saveGroupListButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.saveGroupListButton.text")); // NOI18N
        saveGroupListButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.saveGroupListButton.toolTipText")); // NOI18N
        saveGroupListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGroupListButtonActionPerformed(evt);
            }
        });

        loadGroupListButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.loadGroupListButton.text")); // NOI18N
        loadGroupListButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.loadGroupListButton.toolTipText")); // NOI18N
        loadGroupListButton.setEnabled(false);
        loadGroupListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGroupListButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout SCconfigureGroupsPanelLayout = new org.jdesktop.layout.GroupLayout(SCconfigureGroupsPanel);
        SCconfigureGroupsPanel.setLayout(SCconfigureGroupsPanelLayout);
        SCconfigureGroupsPanelLayout.setHorizontalGroup(
            SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
            .add(SCconfigureGroupsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(SCconfigureGroupsPanelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(saveGroupListButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(loadGroupListButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 305, Short.MAX_VALUE)
                        .add(addGroupButton)
                        .add(deleteGroupButton))
                    .add(groupsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)))
        );
        SCconfigureGroupsPanelLayout.setVerticalGroup(
            SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SCconfigureGroupsPanelLayout.createSequentialGroup()
                .add(SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SCconfigureGroupsPanelLayout.createSequentialGroup()
                        .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(SCconfigureGroupsPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, SCconfigureGroupsPanelLayout.createSequentialGroup()
                        .add(groupsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(SCconfigureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(addGroupButton)
                                .add(saveGroupListButton)
                                .add(loadGroupListButton))
                            .add(deleteGroupButton))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCconfigureGroupsPanel, "card7");

        SCconfigureRankProdGroupsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel22.setLayout(new java.awt.GridBagLayout());

        confRPPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        confRPPreviousButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.confRPPreviousButton.text")); // NOI18N
        confRPPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confRPPreviousButtonActionPerformed(evt);
            }
        });
        jPanel22.add(confRPPreviousButton, new java.awt.GridBagConstraints());

        confRPNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        confRPNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.confRPNextButton.text")); // NOI18N
        confRPNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confRPNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel22.add(confRPNextButton, gridBagConstraints);

        groupsPanelRP.setBackground(new java.awt.Color(255, 255, 255));
        groupsPanelRP.setLayout(new javax.swing.BoxLayout(groupsPanelRP, javax.swing.BoxLayout.Y_AXIS));

        group1PanelRP.setBackground(new java.awt.Color(214, 232, 255));
        group1PanelRP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        addFileToGroup1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        addFileToGroup1Button.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.addFileToGroup1Button.text")); // NOI18N
        addFileToGroup1Button.setName("Group1AddButton"); // NOI18N
        addFileToGroup1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToGroup1ButtonActionPerformed(evt);
            }
        });

        removeFileFromGroup1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        removeFileFromGroup1Button.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.removeFileFromGroup1Button.text")); // NOI18N
        removeFileFromGroup1Button.setName("Group1RmButton"); // NOI18N
        removeFileFromGroup1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileFromGroup1ButtonActionPerformed(evt);
            }
        });

        jLabel11.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel11.text")); // NOI18N

        group1NameField.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.group1NameField.text")); // NOI18N
        group1NameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                group1NameFieldKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                group1NameFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                group1NameFieldKeyReleased(evt);
            }
        });

        group1Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Origin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        group1Table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumn col = group1Table.getColumnModel().getColumn(1);
        col.setPreferredWidth(60);
        col.setMinWidth(60);
        col.setMaxWidth(60);

        // tool tip text
        group1Table.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e){
                Point p = e.getPoint();
                int row = group1Table.rowAtPoint(p);
                int column = group1Table.columnAtPoint(p);
                group1Table.setToolTipText(String.valueOf(group1Table.getValueAt(row,column)));
            }//end MouseMoved
        }); // end MouseMotionAdapter
        jScrollPane1.setViewportView(group1Table);

        org.jdesktop.layout.GroupLayout group1PanelRPLayout = new org.jdesktop.layout.GroupLayout(group1PanelRP);
        group1PanelRP.setLayout(group1PanelRPLayout);
        group1PanelRPLayout.setHorizontalGroup(
            group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRPLayout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addFileToGroup1Button)
                    .add(removeFileFromGroup1Button)
                    .add(group1PanelRPLayout.createSequentialGroup()
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(group1NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
        group1PanelRPLayout.setVerticalGroup(
            group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRPLayout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .add(group1PanelRPLayout.createSequentialGroup()
                        .add(group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(group1NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel11))
                        .add(65, 65, 65)
                        .add(removeFileFromGroup1Button)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addFileToGroup1Button)))
                .addContainerGap())
        );

        groupsPanelRP.add(group1PanelRP);

        group1PanelRP1.setBackground(new java.awt.Color(214, 232, 255));
        group1PanelRP1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        addFileToGroup2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        addFileToGroup2Button.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.addFileToGroup2Button.text")); // NOI18N
        addFileToGroup2Button.setName("Group2AddButton"); // NOI18N
        addFileToGroup2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToGroup2ButtonActionPerformed(evt);
            }
        });

        removeFileFromGroup2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        removeFileFromGroup2Button.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.removeFileFromGroup2Button.text")); // NOI18N
        removeFileFromGroup2Button.setName("Group2RmButton"); // NOI18N
        removeFileFromGroup2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileFromGroup2ButtonActionPerformed(evt);
            }
        });

        jLabel27.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel27.text")); // NOI18N

        group2NameField.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.group2NameField.text")); // NOI18N
        group2NameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                group2NameFieldKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                group2NameFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                group2NameFieldKeyReleased(evt);
            }
        });

        group2Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Origin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        group2Table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumn col2 = group2Table.getColumnModel().getColumn(1);
        col2.setPreferredWidth(60);
        col2.setMinWidth(60);
        col2.setMaxWidth(60);

        // tool tip text
        group2Table.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e){
                Point p = e.getPoint();
                int row = group2Table.rowAtPoint(p);
                int column = group2Table.columnAtPoint(p);
                group2Table.setToolTipText(String.valueOf(group2Table.getValueAt(row,column)));
            }//end MouseMoved
        }); // end MouseMotionAdapter
        jScrollPane7.setViewportView(group2Table);

        org.jdesktop.layout.GroupLayout group1PanelRP1Layout = new org.jdesktop.layout.GroupLayout(group1PanelRP1);
        group1PanelRP1.setLayout(group1PanelRP1Layout);
        group1PanelRP1Layout.setHorizontalGroup(
            group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRP1Layout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(addFileToGroup2Button)
                    .add(removeFileFromGroup2Button)
                    .add(group1PanelRP1Layout.createSequentialGroup()
                        .add(jLabel27)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(group2NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
        group1PanelRP1Layout.setVerticalGroup(
            group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRP1Layout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(group1PanelRP1Layout.createSequentialGroup()
                        .add(group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(group2NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel27))
                        .add(65, 65, 65)
                        .add(removeFileFromGroup2Button)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addFileToGroup2Button)))
                .addContainerGap())
        );

        groupsPanelRP.add(group1PanelRP1);

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel26.text")); // NOI18N

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jPanel23.border.title"))); // NOI18N

        jScrollPane6.setBorder(null);

        jScrollPane6.setViewportView(celFileListRP);

        org.jdesktop.layout.GroupLayout jPanel23Layout = new org.jdesktop.layout.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel24Layout = new org.jdesktop.layout.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel24Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel23, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout SCconfigureRankProdGroupsPanelLayout = new org.jdesktop.layout.GroupLayout(SCconfigureRankProdGroupsPanel);
        SCconfigureRankProdGroupsPanel.setLayout(SCconfigureRankProdGroupsPanelLayout);
        SCconfigureRankProdGroupsPanelLayout.setHorizontalGroup(
            SCconfigureRankProdGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
            .add(SCconfigureRankProdGroupsPanelLayout.createSequentialGroup()
                .add(jPanel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupsPanelRP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE))
        );
        SCconfigureRankProdGroupsPanelLayout.setVerticalGroup(
            SCconfigureRankProdGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCconfigureRankProdGroupsPanelLayout.createSequentialGroup()
                .add(SCconfigureRankProdGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(groupsPanelRP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCconfigureRankProdGroupsPanel, "card8");

        SCExperimentDesignerPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel8.setLayout(new java.awt.GridBagLayout());

        TCExperimentDesignerPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCExperimentDesignerPreviousButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.TCExperimentDesignerPreviousButton.text")); // NOI18N
        TCExperimentDesignerPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCExperimentDesignerPreviousButtonActionPerformed(evt);
            }
        });
        jPanel8.add(TCExperimentDesignerPreviousButton, new java.awt.GridBagConstraints());

        TCExperimentDesignerNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCExperimentDesignerNextButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.TCExperimentDesignerNextButton.text")); // NOI18N
        TCExperimentDesignerNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCExperimentDesignerNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel8.add(TCExperimentDesignerNextButton, gridBagConstraints);

        jLabel25.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jLabel25.text")); // NOI18N

        designerScrollPane.setBorder(null);

        jButton8.setBackground(new java.awt.Color(255, 255, 255));
        jButton8.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        createMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        createMetagroupButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.createMetagroupButton.text")); // NOI18N
        createMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMetagroupButtonActionPerformed(evt);
            }
        });

        deleteMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteMetagroupButton.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCWorkflowPanel.deleteMetagroupButton.text")); // NOI18N
        deleteMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMetagroupButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout SCExperimentDesignerPanelLayout = new org.jdesktop.layout.GroupLayout(SCExperimentDesignerPanel);
        SCExperimentDesignerPanel.setLayout(SCExperimentDesignerPanelLayout);
        SCExperimentDesignerPanelLayout.setHorizontalGroup(
            SCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
            .add(SCExperimentDesignerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(SCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, SCExperimentDesignerPanelLayout.createSequentialGroup()
                        .add(jButton8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 316, Short.MAX_VALUE)
                        .add(createMetagroupButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteMetagroupButton)
                        .addContainerGap())
                    .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)))
        );
        SCExperimentDesignerPanelLayout.setVerticalGroup(
            SCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, SCExperimentDesignerPanelLayout.createSequentialGroup()
                .add(SCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(SCExperimentDesignerPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deleteMetagroupButton)
                    .add(createMetagroupButton)
                    .add(jButton8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(SCExperimentDesignerPanel, "card6");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void SCImportNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SCImportNextButtonActionPerformed
    
//    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
//        if (!dataModel.getDataType().equals("generic")) {
//            dataModel.addInputFile((String) importFileList.getModel().getElementAt(i));
//        }
//        //String label = Utilities.extractFileNamePathComponent((String) importFileList.getModel().getElementAt(i));
//    }

    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, QCCHOICE);
}//GEN-LAST:event_SCImportNextButtonActionPerformed



private void TCQCResultsPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCResultsPreviousButtonActionPerformed
   
    CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
    cl.show(SCWorkflowPanel.this, QCCHOICE);
}//GEN-LAST:event_TCQCResultsPreviousButtonActionPerformed

private void initializeGroupListPanel() {
    if (groupListPanelIsInitialzed) return;
    if (isImporting) {
        scInputFileList.setModel(importFileList.getModel());
        try {
            JPanel newList =  RobinUtilities.populateGroupListFromFile(dataModel.getGroupsFile(), scInputFileList);

            if (newList == null) {
                return;
            } else {
                groupListPanel = newList;
            }

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (DocumentException ex) {
            Exceptions.printStackTrace(ex);
        }
    } else {
        System.out.println("Initializing group list panel");
        // initialize the group list with two empty groups
        groupListPanel = new JPanel();
        groupListPanel.setLayout(new BoxLayout(groupListPanel, BoxLayout.Y_AXIS));
        groupListPanel.setBackground(Color.WHITE);

        CelFileGroupPanel p1 = new CelFileGroupPanel("Group1", scInputFileList);
        groupListPanel.add(p1);
        CelFileGroupPanel p2 = new CelFileGroupPanel("Group2", scInputFileList);
        groupListPanel.add(p2);
    }

    groupsScrollPane.add(groupListPanel);
    groupsScrollPane.setViewportView(groupListPanel);

    groupListPanelIsInitialzed = true;
}

private void TCQCResultsNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCResultsNextButtonActionPerformed
    
    //TODO delete input files that the user chose to ignore
    ArrayList<String> excludedFiles = new ArrayList<String>();
    DefaultListModel clearedInputFileModel = new DefaultListModel();
    Component[] qcItems = resultPanel.getComponents();
    for (Component item : qcItems) {
        if (item instanceof QCResultListItem) {
            QCResultListItem qcitem = (QCResultListItem) item;
            
            if (qcitem.isExcluded()) {
                excludedFiles.add(qcitem.getCelFile());                
            }            
        }
    }

    // remove the excluded files from the model
    for (String file : excludedFiles) {
        dataModel.getInputFiles().remove(file);
    }
    
    for (String clearFile : dataModel.getInputFiles()) {
        clearedInputFileModel.addElement(clearFile);
    }

    // show the groups configuration panel
    // now fill the celFileList with whats left
    if (((String)limmaORrpBox.getSelectedItem()).startsWith("Linear")) {
       scInputFileList.setModel(clearedInputFileModel);
        initializeGroupListPanel();
        //stepLabel.setText("Step 4 of 4");
        CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
        cl.show(this, SCGROUPCONFIGUREPANEL);
    } else {
        // user chose RankProduct
        celFileListRP.setModel(clearedInputFileModel);
        CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
        cl.show(SCWorkflowPanel.this, SCRANKPRODGROUPCONFIGUREPANEL);
    }
}//GEN-LAST:event_TCQCResultsNextButtonActionPerformed

private void enableQCPanelControls(boolean state) {
    scQCBGImageBox.setEnabled(state);
    scQCHistBox.setEnabled(state);
    scQCHclustBox.setEnabled(state);
    scQCScatterBox.setEnabled(state);
    scQCBoxplotBox.setEnabled(state);
    scQCIncludeAllBox.setEnabled(state);

    expertSettingsBox1.setEnabled(state);
    bgCorrCheckBox.setEnabled(state);
    bgCorrMethodBox.setEnabled(state);
    normBetCheckBox.setEnabled(state);
    normBetMethodBox.setEnabled(state);
    limmaORrpBox.setEnabled(state);
    scMainPValBox.setEnabled(state);
    scMainTestingStrategyBox.setEnabled(state);
    pValCutoffBox1.setEnabled(state);
    wantsMinLFCBox.setEnabled(state);
    wantsRawDataBox.setEnabled(state);
    isGuruBox.setEnabled(state);    
}

protected void startMainAnalysis() {

    //DEBUG
    System.out.println("starting main analysis");
    dataModel.setAnalysisType(AnalysisStrategy.LIMMA);
    AnalysisDesignModel designModel =  analysisDesignerPanel.getDesignModel();

    try {
        designModel.validateModel();
    } catch (NoConnectionsDesignException ex) {
        Exceptions.printStackTrace(ex);
        //TODO nicer text
        new SimpleErrorMessage(this, "You have to draw at least one connection between two groups\n"+
                "to tell me what comparisons i shall make.");
        return;
    } catch (RedundantConnectionsDesignException ex) {
        SimpleLogger.getLogger(true).logException(ex);
        //Exceptions.printStackTrace(ex);
        return;
    }
    
    // disable controls until the task has finished
    TCQCResultsNextButton.setEnabled(false);
    TCQCResultsPreviousButton.setEnabled(false);
    TCExperimentDesignerNextButton.setEnabled(false);
    TCExperimentDesignerPreviousButton.setEnabled(false);
    
    RScriptGenerator generator = new RScriptGenerator();
    String code = generator.generateSingleColorMainScript(dataModel, designModel );
    
    // open a script editor if the user chose the guru mode
    // are you guru?
    if (isGuruBox.isSelected()) {
        //TODO show script editor dialog
        RScriptEditor scriptEditor = new RScriptEditor(mainGUI, code);
        if (scriptEditor.getUserChoice() == RScriptEditor.USER_ACCEPT) {
            code = scriptEditor.getModifiedScript();
        } else {
             scriptEditor.dispose();
             System.out.println("Guru bail-out");
             return;
        }            
    }
    
    // save the main script file

    File sourceDir = new File(dataModel.getOutputDir(), "source");
    File tcScriptFileName = new File(sourceDir, dataModel.getExperimentName() + "_main_analysis.R");
    
    //System.out.println(tcqcscript);

    final RTask task = prepareTask(tcScriptFileName, code);
    RTaskController listener = null;
    //processTimer = new Timer(100, listener);
    
    //TODO for some blasted reason the progress indicator does not start running before the task starts.....
    processTimer.setDelay(100);
    if (qcPerformed) processTimer.removeActionListener(processTimer.getActionListeners()[0]);
    mainGUI.startBusyAnimation("running analysis...");
    executor = Executors.newFixedThreadPool(1);

    Future taskres = null;
    try {
        taskres = executor.submit(task);
        //executor.execute(task);
    } catch (RejectedExecutionException rej) {
        
        System.out.println("REJECTED EXECUTION FUCKUP - TRYING IT AGAIN");
        try {
            // DEBUG
            while (executor.awaitTermination(100, TimeUnit.SECONDS)) {
                System.out.println("is done? " + taskres.isDone());
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }


    } catch (Exception ex) {
        ex.printStackTrace();
    }
    listener =  new RTaskController(dataModel, task, this, executor, processTimer);
    processTimer.addActionListener(listener);
    processTimer.start();    
    executor.shutdown();    
}

public void startBusyAnimation(String statusMsg) {
    this.mainGUI.startBusyAnimation(statusMsg);
}

public void stopBusyAnimation() {
    this.mainGUI.stopBusyAnimation();
}

private void setupDesignerScene() {
    
    ArrayList<AbstractGroupModel> groups = new ArrayList<AbstractGroupModel>();
    
    for (Object groupPanel : groupListPanel.getComponents()) {
        if (groupPanel instanceof CelFileGroupPanel) {
            String condition = ((CelFileGroupPanel) groupPanel).getGroupName();
            conditions.add(condition);
            GroupModel mod = new GroupModel(condition, ((CelFileGroupPanel) groupPanel).getFileList());
            groups.add(mod);
        }
    }

    analysisDesignerPanel = new AnalysisDesigner(groups);
    designerScrollPane.setViewportView(analysisDesignerPanel);
}

private void TCExperimentDesignerPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCExperimentDesignerPreviousButtonActionPerformed
    if (qcPerformed) {
        CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
        cl.show(SCWorkflowPanel.this, QCRESULTS);
    } else {
        CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
        cl.show(SCWorkflowPanel.this, QCCHOICE);
    }
    
}//GEN-LAST:event_TCExperimentDesignerPreviousButtonActionPerformed

private void TCExperimentDesignerNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCExperimentDesignerNextButtonActionPerformed
    // now we should be able to start the main script
    String contrasts = analysisDesignerPanel.getDesignModel().getContrastTerms();

    //DEBUG
    AnalysisDesignModel designModel = analysisDesignerPanel.getDesignModel();
    System.out.println("groups=" + designModel.getGroups());
    System.out.println("connections=" + designModel.getConnections());

    dataModel.setContrastTerms(contrasts);
    dataModel.setGroupsTerm(analysisDesignerPanel.getDesignModel().getGroupsTerm());
    dataModel.setDesignTerm(analysisDesignerPanel.getDesignModel().getModelMatrixTerm());
    System.out.println("CONSTRASTS: "+contrasts);
    startMainAnalysis();
    //System.exit(0);
}//GEN-LAST:event_TCExperimentDesignerNextButtonActionPerformed

private void populateQCResultsList(ArrayList<QCResultListItem> results) {
    resultPanel = new JPanel();
    resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    resultPanel.setBackground(Color.WHITE);
    
    for (QCResultListItem item : results) {
        resultPanel.add(item);
    }
    
    resultPanel.setMaximumSize(new Dimension(700,10000));
    TCQCResultScrollPane.add(resultPanel);
    TCQCResultScrollPane.setViewportView(resultPanel);
}


private void addImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImportButtonActionPerformed

    if (isImporting) {
        new SimpleErrorMessage(this, "You are adding new files to an imported project.\n" +
                "Please make sure that the newly imported files are\n" +
                "of exactly the same data format as the imported ones.\n" +
                "If the formats differ, the downstream analysis will fail.\n" +
                "Please consult the manual section on reloading existing\n" +
                "projects for details.");
    }
    DefaultListModel importListModel = (DefaultListModel) importFileList.getModel();
    File[] files = null;
            
    SCFileChooser.setMultiSelectionEnabled(true);
    SCFileChooser.resetChoosableFileFilters();
    SCFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    // deactivated this since it was making problems when trying
    // to load data of different GenePix format versions
    /*SCFileChooser.addChoosableFileFilter(new GenericFileFilter());
    SCFileChooser.addChoosableFileFilter(new AgilentFileFilter());*/
    //TCFileChooser.addChoosableFileFilter(null);//TODO add more filters
    if (SCFileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        files = SCFileChooser.getSelectedFiles();
    } else {
        return;
    }    
    for (File f : files) {
        importListModel.addElement(f.getAbsolutePath());
    }    
    
    //DEBUG
    System.out.println("Generic file type imported.");
    dataModel.setDataType("generic");

    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
        dataModel.addInputFile((String) importFileList.getModel().getElementAt(i));
    }

    String  inputType = importTypeRadioGroup.getSelection().getActionCommand();

    // show the dialog for import of generic data and pass it the data model to
    // get the required data fields filled...
    SCGenericImportDialog singleChannelImporter =
            new SCGenericImportDialog(mainGUI, true, files, dataModel, importFileList, inputType);

    if (singleChannelImporter.getUserChoice() == SCGenericImportDialog.response.CANCEL_RESPONSE) {
        singleChannelImporter.dispose();
        return;
    } else {
        // directly switch to the groups designer panel to avoid inconsistencies
        SCImportNextButtonActionPerformed(null);
    }        
}//GEN-LAST:event_addImportButtonActionPerformed

private void removeImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeImportButtonActionPerformed
DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
    int[] selectedIndices = importFileList.getSelectedIndices();
    java.util.Arrays.sort(selectedIndices);    
    for (int index = selectedIndices.length-1; index >= 0; index--) {
        importModel.removeElementAt(selectedIndices[index]);
    }
}//GEN-LAST:event_removeImportButtonActionPerformed

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    setupDesignerScene();
}//GEN-LAST:event_jButton8ActionPerformed

private void createMetagroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMetagroupButtonActionPerformed
    try {
            analysisDesignerPanel.createMetaNodeOfSelectedNodes();
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(ex);
            return;                    
        }
    analysisDesignerPanel.revalidate();
}//GEN-LAST:event_createMetagroupButtonActionPerformed

private void deleteMetagroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMetagroupButtonActionPerformed
    analysisDesignerPanel.deleteSelectedMetaNodes();
}//GEN-LAST:event_deleteMetagroupButtonActionPerformed

private void SCQualcheckPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SCQualcheckPanelPreviousButtonActionPerformed
    CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
    cl.show(SCWorkflowPanel.this, IMPORT);
}//GEN-LAST:event_SCQualcheckPanelPreviousButtonActionPerformed

private void SCQualcheckPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SCQualcheckPanelNextButtonActionPerformed
    // read the user chosen settings from the GUI panel and write them into the model
    if (bgCorrCheckBox.isSelected()) {
        dataModel.setBackGroundCorrectionMethod( (String) bgCorrMethodBox.getSelectedItem());
        dataModel.setDoBackgroundCorrection(true);
    } else {
        dataModel.setBackGroundCorrectionMethod("none");
        dataModel.setDoBackgroundCorrection(false);
    }

    if (normBetCheckBox.isSelected()) {
        dataModel.setNormBetweenArrays( (String) normBetMethodBox.getSelectedItem());
        dataModel.setDoNormBetween(true);
    } else {
        dataModel.setNormBetweenArrays("none");
        dataModel.setDoNormBetween(false);
    }

    dataModel.setPValCorrectionMethod( (String) scMainPValBox.getSelectedItem());
    dataModel.setPValCutoffValue( (String) pValCutoffBox1.getSelectedItem());

    if ( ( (String)limmaORrpBox.getSelectedItem()).startsWith("Linear")) {
        dataModel.setAnalysisType(AnalysisStrategy.LIMMA);
    } else {
        dataModel.setAnalysisType(AnalysisStrategy.RANKPROD);
    }

    dataModel.setStatStrategy( (String) scMainTestingStrategyBox.getSelectedItem());
    dataModel.setMinLogFoldChangeOf2(wantsMinLFCBox.isSelected());
    dataModel.setWriteRawExprs(wantsRawDataBox.isSelected());

     // no qc desired?
    if (    !scQCBGImageBox.isSelected() &&
            !scQCBoxplotBox.isSelected() &&
            !scQCScatterBox.isSelected() &&
            !scQCHclustBox.isSelected() &&
            !scQCHistBox.isSelected()    ) {
        System.out.println("skipping quality checks");
        qcPerformed = false;

        // move on to the groups configuration panel
//        initializeGroupListPanel();

        // populate the input file list for the groups panel
        DefaultListModel scInputFileListModel = new DefaultListModel();
        for (String file : dataModel.getInputFiles()) {
            scInputFileListModel.addElement(file);
        }

        // show the groups configuration panel
        // now fill the celFileList with whats left
        if (((String)limmaORrpBox.getSelectedItem()).startsWith("Linear")) {
           scInputFileList.setModel(scInputFileListModel);
            initializeGroupListPanel();
            //stepLabel.setText("Step 4 of 4");
            CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
            cl.show(this, SCGROUPCONFIGUREPANEL);
        } else {
            // user chose RankProduct

            // at this point, the importFileList's model will be empty
            // if the user has seen the limma group conf panel first
            // but then decided to redo the analysis using rankprod
            // we need to savage all files from the limma groups to prevent this
            // from happening

            if (/*isImporting &&*/ groupListPanelIsInitialzed) {
                for (Component c : groupListPanel.getComponents()) {
                    if (c instanceof CelFileGroupPanel) {
                        ((CelFileGroupPanel) c).salvageFileEntries();
                    }
                }
            }

            celFileListRP.setModel(scInputFileListModel);
            CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
            cl.show(SCWorkflowPanel.this, SCRANKPRODGROUPCONFIGUREPANEL);
        }
        return;
    }

    // user wants QC so here we go...

    // get selected QC functions
    selectedQC = new ArrayList<String>();
    if (scQCBGImageBox.isSelected()) {selectedQC.add("sc_bground");}
    if (scQCHistBox.isSelected()) {selectedQC.add("sc_density");}
    if (scQCBoxplotBox.isSelected()) {selectedQC.add("sc_maplot");}
    if (scQCHclustBox.isSelected() && (dataModel.getInputFiles().size() > 2) ) {
        selectedQC.add("sc_pcaplot");
        selectedQC.add("sc_hclust");
    }
    if (scQCScatterBox.isSelected()) {selectedQC.add("sc_scatter");}

    RScriptGenerator generator = new RScriptGenerator();
    String tcqcscript = generator.generateSingleColorQCScript(selectedQC, dataModel);

    File sourceDir = new File(dataModel.getOutputDir(), "source");
    File tcqcScriptFileName = new File(sourceDir, dataModel.getExperimentName() + "_qualityChecks.R");
    
    
    if (isGuruBox.isSelected()) {
        //TODO show script editor dialog
        RScriptEditor scriptEditor = new RScriptEditor(mainGUI, tcqcscript);
        if (scriptEditor.getUserChoice() == RScriptEditor.USER_ACCEPT) {
            tcqcscript = scriptEditor.getModifiedScript();
        } else {
             scriptEditor.dispose();
             System.out.println("Guru bail-out");
             return;
        }
    }

    final RTask qcTask = prepareTask(tcqcScriptFileName, tcqcscript);

    ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (executor.isTerminated()) {
                    //DEBUG
                    System.out.println("FINISHED.");

                    if (qcTask.getExitValue() != 0) {
                        // the task has a problem
                        processTimer.stop();
                        mainGUI.busyIconTimer.stop();
                        mainGUI.progressLabel.setText("error");
                        new VerboseWarningDialog(   mainGUI,
                                                    "Quality check process failure",
                                                    "QC process failed!\nExit code:"+qcTask.getExitValue(), qcTask.getOutputMessage(),
                                                    dataModel);
                        SCWorkflowPanel.this.enableQCPanelControls(true);
                    } else {
                        // the sun is shining
                        // compile the qc results list and show all results

                        ArrayList<QCResultListItem> qcItems = delegate.generateQCResultItems(dataModel, selectedQC, qcTask);

                        populateQCResultsList(qcItems);
                        mainGUI.busyIconTimer.stop();
                        mainGUI.progressLabel.setText("idle");
                        mainGUI.progressIconLabel.setIcon(mainGUI.idleIcon);
                        processTimer.stop();
                        qcPerformed = true;
                        SCWorkflowPanel.this.enableQCPanelControls(true);
                        // switch to the results list
                        CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
                        cl.show(SCWorkflowPanel.this, QCRESULTS);
                    }
                }
            }
        };


    processTimer.setDelay(100);
    if (qcPerformed) processTimer.removeActionListener(processTimer.getActionListeners()[0]);
    mainGUI.busyIconTimer.start();
    mainGUI.progressLabel.setText("running analysis...");
    executor = Executors.newFixedThreadPool(1);
    processTimer.addActionListener(al);
    this.enableQCPanelControls(false);
    executor.execute(qcTask);
    processTimer.start();
    executor.shutdown();
}//GEN-LAST:event_SCQualcheckPanelNextButtonActionPerformed

private void scQCBoxplotBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCBoxplotBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scQCBoxplotBoxActionPerformed

private void scQCHistBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCHistBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scQCHistBoxActionPerformed

private void scQCScatterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCScatterBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scQCScatterBoxActionPerformed

private void expertSettingsBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expertSettingsBox1ActionPerformed
    expertSettingsPanel.setVisible(expertSettingsBox1.isSelected());
}//GEN-LAST:event_expertSettingsBox1ActionPerformed

private void scQCHclustBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCHclustBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scQCHclustBoxActionPerformed

private void scQCBGImageBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCBGImageBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scQCBGImageBoxActionPerformed

private void scQCIncludeAllBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scQCIncludeAllBoxActionPerformed
    scQCBGImageBox.setSelected(scQCIncludeAllBox.isSelected());
    scQCHistBox.setSelected(scQCIncludeAllBox.isSelected());
    scQCBoxplotBox.setSelected(scQCIncludeAllBox.isSelected());
    scQCHclustBox.setSelected(scQCIncludeAllBox.isSelected());
    scQCScatterBox.setSelected(scQCIncludeAllBox.isSelected());
}//GEN-LAST:event_scQCIncludeAllBoxActionPerformed

private void configGroupsPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configGroupsPanelPreviousButtonActionPerformed
    //FIXME steplabel missing
    //stepLabel.setText("Step 3 of 4");
    CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
    if (qcPerformed) {
        cl.show(SCWorkflowPanel.this, QCRESULTS);
    } else {
        cl.show(SCWorkflowPanel.this, QCCHOICE);
    }
}//GEN-LAST:event_configGroupsPanelPreviousButtonActionPerformed

private void configGroupsPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configGroupsPanelNextButtonActionPerformed

    // Any files left in the input files list?
    if (scInputFileList.getModel().getSize() > 0) {
        int response = JOptionPane.showConfirmDialog(this,
                "There are ungrouped input files left in the\n"+
                "input file list. Do you really want to exclude\n"+
                "them from further analyses?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return;
        }
    }

    if (!RobinUtilities.validateGroupList(groupListPanel, dataModel.getWarningsHandler())) {
        return;
    }
    File sourcePath = new File(dataModel.getOutputDirFile(), "source");
    File groupListFile = new File(sourcePath, dataModel.getExperimentName() + "_groupList.xml" );
    try {
        RobinUtilities.saveGroupList(groupListPanel, groupListFile, dataModel);
        dataModel.setGroupsFile(groupListFile);
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }
    setupDesignerScene();

    CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
    cl.show(SCWorkflowPanel.this, EXPERIMENTDESIGNER);

    // set values to previously chosen ones to ensure
    // consistency
    //FIXME expert settings for statistics on QC panel
    //mainPvalBox.setSelectedItem(scMainPValBox.getSelectedItem());
    //mainNormBox.setSelectedItem(normMethodBox.getSelectedItem());
}//GEN-LAST:event_configGroupsPanelNextButtonActionPerformed

private void addGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupButtonActionPerformed
    CelFileGroupPanel newPanel = new CelFileGroupPanel("New Group",scInputFileList);
    groupListPanel.add(newPanel);
    groupListPanel.revalidate();
}//GEN-LAST:event_addGroupButtonActionPerformed

private void deleteGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteGroupButtonActionPerformed
    Component[] groupPanels = groupListPanel.getComponents();

    if (groupPanels.length <= 2) {
        new SimpleErrorMessage(this, "You need at least two group to do a comparison");
        return;
    }

    for (Component comp : groupPanels) {
        if (comp instanceof CelFileGroupPanel) {
            CelFileGroupPanel cfgPanel = (CelFileGroupPanel) comp;
            if (cfgPanel.isMarkedForDeletion()) {
                // before removing the panel we must salvage its contents
                // otherwise the files will be lost, too!
                cfgPanel.salvageFileEntries();
                groupListPanel.remove(comp);
                //groupListPanel.getLayout().removeLayoutComponent(comp);
                //groupListPanel.doLayout();
                groupListPanel.repaint();
            }
        }
    }
}//GEN-LAST:event_deleteGroupButtonActionPerformed

private void confRPPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confRPPreviousButtonActionPerformed
    salvageRPfiles();
    if (isImporting) groupListPanelIsInitialzed = false;
    CardLayout cl = (CardLayout) SCWorkflowPanel.this.getLayout();
    if (qcPerformed) {
        cl.show(SCWorkflowPanel.this, QCRESULTS);
    } else {
        cl.show(SCWorkflowPanel.this, QCCHOICE);
    }
}//GEN-LAST:event_confRPPreviousButtonActionPerformed

    private void salvageRPfiles() {
        flushRPtable(group1Table, (DefaultTableModel) group1Table.getModel());
        flushRPtable(group2Table, (DefaultTableModel) group2Table.getModel());
    }

    private void flushRPtable(JTable fileTable, DefaultTableModel groupTableModel) {
        DefaultListModel sourceListModel  = (DefaultListModel) celFileListRP.getModel();
        int rowCount = groupTableModel.getRowCount();
        for (int i = rowCount-1; i >= 0; i-- ) {
            String celFile = (String) groupTableModel.getValueAt(i, 0);
            groupTableModel.removeRow(i);
            System.out.println("salvaging file "+celFile);
            sourceListModel.addElement(celFile);
        }
    }

private void startRankProduktAnalysis(
    ArrayList<String> files,
    ArrayList<Integer> classes,
    ArrayList<Integer> origins) {

    // start the RankProd main script
    mainGUI.startBusyAnimation("running RankProd analysis...");
    dataModel.setAnalysisType(AnalysisStrategy.RANKPROD);
    //DEBUG
    System.out.println("starting main analysis");
    // disable controls until the task has finished
    TCQCResultsNextButton.setEnabled(false);
    TCQCResultsPreviousButton.setEnabled(false);
    TCExperimentDesignerNextButton.setEnabled(false);
    TCExperimentDesignerPreviousButton.setEnabled(false);

    RScriptGenerator generator = new RScriptGenerator();
    String rankProdCode;

    rankProdCode = generator.generateSingleColorRankProdScript(files, classes, origins, dataModel);

     // open a script editor if the user chose the guru mode
    // are you guru?
    if (isGuruBox.isSelected()) {
        //TODO show script editor dialog
        RScriptEditor scriptEditor = new RScriptEditor(mainGUI, rankProdCode);
        if (scriptEditor.getUserChoice() == RScriptEditor.USER_ACCEPT) {
            rankProdCode = scriptEditor.getModifiedScript();
        } else {
             scriptEditor.dispose();
             System.out.println("Guru bail-out");
             return;
        }
    }

    // save the main script file
    File sourceDir = new File(dataModel.getOutputDir(), "source");
    File tcScriptFileName = new File(sourceDir, dataModel.getExperimentName() + "_main_rankprod_analysis.R");

    final RTask task = prepareTask(tcScriptFileName, rankProdCode);
    RTaskController listener = null;
    //processTimer = new Timer(100, listener);

    //TODO for some blasted reason the progress indicator does not start running before the task starts.....
    processTimer.setDelay(100);
    if (qcPerformed) processTimer.removeActionListener(processTimer.getActionListeners()[0]);
    mainGUI.startBusyAnimation("running rank product analysis...");
    executor = null;
    executor = Executors.newFixedThreadPool(1);    
    executor.execute(task);
    listener =  new RTaskController(dataModel, task, this, executor, processTimer);
    processTimer.addActionListener(listener);
    processTimer.start();
    executor.shutdown();
}

private void confRPNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confRPNextButtonActionPerformed
    // disable controls
    confRPNextButton.setEnabled(false);
    confRPPreviousButton.setEnabled(false);

    // some consistency checking first
    String group1Name = group1NameField.getText();
    String group2Name = group2NameField.getText();

    DefaultTableModel group1TableModel = (DefaultTableModel) group1Table.getModel();
    DefaultTableModel group2TableModel = (DefaultTableModel) group2Table.getModel();

    ArrayList<String> files = new ArrayList<String>();
    ArrayList<Integer> classes = new ArrayList<Integer>();
    ArrayList<Integer> origin = new ArrayList<Integer>();

    // are there input files left in the celfilelist?
    if (celFileListRP.getModel().getSize() > 0) {
        int response = JOptionPane.showConfirmDialog(this,
                "There are ungrouped input files left in the\n"+
                "input file list. Do you really want to exclude\n"+
                "them from further analyses?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return;
        }
    }

    // do the groups have different names
    if (group1Name.equals(group2Name)) {
        new SimpleErrorMessage(this, "Please choose different names for the groups.");
        return;
    }
    // do both groups have at least one file
    if ( (group1TableModel.getRowCount() == 0) || (group2TableModel.getRowCount() == 0) ) {
        new SimpleErrorMessage(this, "Both groups need to contain at least one input file.");
        return;
    }
    // do the originlabels make sense?


    // now extract the important info for the R script
    for (int i = 0; i <= group1TableModel.getRowCount()-1; i++) {
        classes.add(new Integer(0));
        files.add((String) group1TableModel.getValueAt(i, 0));
        origin.add((Integer) group1TableModel.getValueAt(i, 1));
    }

    for (int i = 0; i <= group2TableModel.getRowCount()-1; i++) {
        classes.add(new Integer(1));
        files.add((String) group2TableModel.getValueAt(i, 0));
        origin.add((Integer) group2TableModel.getValueAt(i, 1));
    }

    this.startRankProduktAnalysis(files, classes, origin);
}//GEN-LAST:event_confRPNextButtonActionPerformed

private void addFileToGroup1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileToGroup1ButtonActionPerformed
    // add all selected items to the group
    DefaultListModel sourceListModel  = (DefaultListModel) celFileListRP.getModel();
    DefaultTableModel groupTableModel;

    System.out.println("source "+evt.getSource());

    if (((JButton) evt.getSource()).getName().contains("Group1")) {
        groupTableModel = (DefaultTableModel) group1Table.getModel();
    } else {
        groupTableModel = (DefaultTableModel) group2Table.getModel();
    }

    for (Object sourceItem : celFileListRP.getSelectedValues() ) {
        groupTableModel.addRow(new Object[] {sourceItem, new Integer(1)});
    }

    //... and remove them from the source list
    int len = celFileListRP.getSelectedIndices().length;
    for (int i = len-1; i >= 0; i-- ) {
        sourceListModel.removeElementAt(celFileListRP.getSelectedIndices()[i]);
    }
}//GEN-LAST:event_addFileToGroup1ButtonActionPerformed

private void removeFileFromGroup1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileFromGroup1ButtonActionPerformed
    // add all selected items to the group
    DefaultListModel sourceListModel  = (DefaultListModel) celFileListRP.getModel();
    DefaultTableModel groupTableModel;
    JTable fileTable;

    if (((JButton) evt.getSource()).getName().contains("Group1")) {
        groupTableModel = (DefaultTableModel) group1Table.getModel();
        fileTable = group1Table;
    } else {
        groupTableModel = (DefaultTableModel) group2Table.getModel();
        fileTable = group2Table;
    }

    int rowCount = fileTable.getSelectedRows().length;
    for (int i = rowCount-1; i >= 0; i-- ) {
        String celFile = (String) groupTableModel.getValueAt(fileTable.getSelectedRows()[i], 0);
        groupTableModel.removeRow(fileTable.getSelectedRows()[i]);
        System.out.println("salvaging file "+celFile);
        sourceListModel.addElement(celFile);
    }
}//GEN-LAST:event_removeFileFromGroup1ButtonActionPerformed

private void group1NameFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group1NameFieldKeyTyped

}//GEN-LAST:event_group1NameFieldKeyTyped

private void checkGroupNameEntry (java.awt.event.KeyEvent evt) {

    JTextField sourceTextField = (JTextField)evt.getSource();
    String groupName = null;

    if ( (String.valueOf(evt.getKeyChar()).equals("+")) ||
        (String.valueOf(evt.getKeyChar()).equals("-")) ||
        (String.valueOf(evt.getKeyChar()).equals("(")) ||
        (String.valueOf(evt.getKeyChar()).equals(")")) ||
        (String.valueOf(evt.getKeyChar()).equals("#")) ||
        (String.valueOf(evt.getKeyChar()).equals("\"")) ||
        (String.valueOf(evt.getKeyChar()).equals("\\")) ||
        (String.valueOf(evt.getKeyChar()).equals("'")) ||
        (String.valueOf(evt.getKeyChar()).equals("\"")) ||
        (String.valueOf(evt.getKeyChar()).equals(",")) ||
        (String.valueOf(evt.getKeyChar()).equals(";")) ||
        (String.valueOf(evt.getKeyChar()).equals("~")) ||
        (String.valueOf(evt.getKeyChar()).equals(".")) ||
        (String.valueOf(evt.getKeyChar()).equals("*")) ||
        (String.valueOf(evt.getKeyChar()).equals("/")) // this list might have to be extended
        ) {
        //System.out.println("BAD CHAR");
        int currentPos = sourceTextField.getCaretPosition();
        new SimpleErrorMessage(this, "Please do not use special characters (+-*/#\"') in the group names");

        String fieldContent = sourceTextField.getText();
        String start, end;
        start = fieldContent.substring(0, currentPos);
        end = fieldContent.substring(currentPos+1, fieldContent.length());

        System.out.println("start:"+start+"\tend:"+end+"currentpos:"+currentPos);
        groupName = start+end;
        sourceTextField.setText(groupName);
        sourceTextField.setCaretPosition((currentPos == 0) ? 0: currentPos);

        System.out.println("Groupname: "+groupName);
        return;

    } else if (sourceTextField.getText().matches("\\d+.*")) {
        new SimpleErrorMessage(this, "Group names must not start with a number.");
        sourceTextField.setText("");
        evt.consume();
        System.out.println("Groupname: "+groupName);
        return;
    }
}

private void group1NameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group1NameFieldKeyPressed
    checkGroupNameEntry(evt);
}//GEN-LAST:event_group1NameFieldKeyPressed

private void group1NameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group1NameFieldKeyReleased
    if (    (String.valueOf(evt.getKeyChar()).equals("+")) ||
            (String.valueOf(evt.getKeyChar()).equals("-")) ||
            (String.valueOf(evt.getKeyChar()).equals("#")) ||
            (String.valueOf(evt.getKeyChar()).equals("\"")) ||
            (String.valueOf(evt.getKeyChar()).equals("\\")) ||
            (String.valueOf(evt.getKeyChar()).equals("'")) ||
            (String.valueOf(evt.getKeyChar()).equals("*")) ||
            (String.valueOf(evt.getKeyChar()).equals("/")) // this list might have to be extended
            ) {
        System.out.println("BAD CHAR");
        new SimpleErrorMessage(this, "Please do not use special characters (+-*/#\"') in the group names");
        //groupNameField.setText(this.getGroupName());
        return;

    } else if (group1NameField.getText().matches("\\d+.*")) {
        new SimpleErrorMessage(this, "Group names must not start with a number.");
        group1NameField.setText("");
        return;
    } else {
        //this.setGroupName(groupNameField.getText());
    }
}//GEN-LAST:event_group1NameFieldKeyReleased

private void addFileToGroup2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileToGroup2ButtonActionPerformed
    addFileToGroup1ButtonActionPerformed(evt);
}//GEN-LAST:event_addFileToGroup2ButtonActionPerformed

private void removeFileFromGroup2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileFromGroup2ButtonActionPerformed
    removeFileFromGroup1ButtonActionPerformed(evt);
}//GEN-LAST:event_removeFileFromGroup2ButtonActionPerformed

private void group2NameFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group2NameFieldKeyTyped
    // TODO add your handling code here:
}//GEN-LAST:event_group2NameFieldKeyTyped

private void group2NameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group2NameFieldKeyPressed
    checkGroupNameEntry(evt);
}//GEN-LAST:event_group2NameFieldKeyPressed

private void group2NameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group2NameFieldKeyReleased
    // TODO add your handling code here:
}//GEN-LAST:event_group2NameFieldKeyReleased

private void scMainTestingStrategyBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scMainTestingStrategyBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_scMainTestingStrategyBoxActionPerformed

private void handleQCInfoButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handleQCInfoButtonPressed
    scqcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(SCWorkflowPanel.class, "SCMainPanel." + evt.getActionCommand() + ".infoText"));
}//GEN-LAST:event_handleQCInfoButtonPressed

private void saveGroupListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGroupListButtonActionPerformed
    // Any files left in the input files list?
    if (scInputFileList.getModel().getSize() > 0) {
        int response = JOptionPane.showConfirmDialog(this,
                "There are ungrouped input files left in the\n"+
                "input file list. Do you really want to exclude\n"+
                "them from further analyses?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return;
        }
    }

    if (!RobinUtilities.validateGroupList(groupListPanel, dataModel.getWarningsHandler())) {
        return;
    }

    File groupListFile = null;
    JFileChooser choose = new JFileChooser(System.getProperty("user.home"));
    choose.setMultiSelectionEnabled(false);
    choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (choose.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        try {
            groupListFile = new File(choose.getSelectedFile().getCanonicalPath() + ".xml");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    } else {
        return;
    }
    try {
        RobinUtilities.saveGroupList(groupListPanel, groupListFile, dataModel);
    } catch (IOException ex) {
        Exceptions.printStackTrace(ex);
    }
}//GEN-LAST:event_saveGroupListButtonActionPerformed

private void loadGroupListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGroupListButtonActionPerformed

    JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(false);

    File chosenPath = null;

    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        chosenPath = chooser.getSelectedFile();
    } else {
        return;
    }
    try {
        JPanel newList =  RobinUtilities.populateGroupListFromFile(chosenPath, scInputFileList);

        if (newList == null) {
            return;
        } else {
            groupListPanel = newList;
        }

    } catch (FileNotFoundException ex) {
        Exceptions.printStackTrace(ex);
    } catch (DocumentException ex) {
        Exceptions.printStackTrace(ex);
    }
    groupsScrollPane.add(groupListPanel);
    groupsScrollPane.setViewportView(groupListPanel);
}//GEN-LAST:event_loadGroupListButtonActionPerformed

private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
    dataModel.setInputType(InputDataType.AGILENT);
    dataModel.setIDcolumnStart("ProbeName");
}//GEN-LAST:event_jRadioButton1ActionPerformed

private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
    dataModel.setInputType(InputDataType.GENERIC);
}//GEN-LAST:event_jRadioButton2ActionPerformed

private void normBetMethodBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normBetMethodBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_normBetMethodBoxActionPerformed

    @Override
    public void RTaskFinished() {
        this.finishGracefully();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SCExperimentDesignerPanel;
    private javax.swing.JFileChooser SCFileChooser;
    private javax.swing.JButton SCImportNextButton;
    private javax.swing.JPanel SCImportPanel;
    private javax.swing.JPanel SCQCPanel;
    private javax.swing.JPanel SCQCResultsPanel;
    private javax.swing.JButton SCQualcheckPanelNextButton;
    private javax.swing.JButton SCQualcheckPanelPreviousButton;
    private javax.swing.JPanel SCconfigureGroupsPanel;
    private javax.swing.JPanel SCconfigureRankProdGroupsPanel;
    private javax.swing.JButton TCExperimentDesignerNextButton;
    private javax.swing.JButton TCExperimentDesignerPreviousButton;
    private javax.swing.JScrollPane TCQCResultScrollPane;
    private javax.swing.JButton TCQCResultsNextButton;
    private javax.swing.JButton TCQCResultsPreviousButton;
    private javax.swing.JLabel TCqualchecklabel1;
    private javax.swing.JButton addFileToGroup1Button;
    private javax.swing.JButton addFileToGroup2Button;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JButton addImportButton;
    private javax.swing.JCheckBox bgCorrCheckBox;
    private javax.swing.JComboBox bgCorrMethodBox;
    private javax.swing.JList celFileListRP;
    private javax.swing.JButton confRPNextButton;
    private javax.swing.JButton confRPPreviousButton;
    private javax.swing.JButton configGroupsPanelNextButton;
    private javax.swing.JButton configGroupsPanelPreviousButton;
    private javax.swing.JButton createMetagroupButton;
    private javax.swing.JButton deleteGroupButton;
    private javax.swing.JButton deleteMetagroupButton;
    private javax.swing.JScrollPane designerScrollPane;
    private javax.swing.JCheckBox expertSettingsBox1;
    private javax.swing.JPanel expertSettingsPanel;
    private javax.swing.JTextField group1NameField;
    private javax.swing.JPanel group1PanelRP;
    private javax.swing.JPanel group1PanelRP1;
    private javax.swing.JTable group1Table;
    private javax.swing.JTextField group2NameField;
    private javax.swing.JTable group2Table;
    private javax.swing.JPanel groupsPanelRP;
    private javax.swing.JScrollPane groupsScrollPane;
    private javax.swing.JList importFileList;
    private javax.swing.ButtonGroup importTypeRadioGroup;
    private javax.swing.JCheckBox isGuruBox;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JComboBox limmaORrpBox;
    private javax.swing.JButton loadGroupListButton;
    private javax.swing.JButton moreHistButton1;
    private javax.swing.JButton moreNUSEButton1;
    private javax.swing.JButton morePCAButton1;
    private javax.swing.JButton moreRNAdigButton1;
    private javax.swing.JButton moreScatterButton1;
    private javax.swing.JCheckBox normBetCheckBox;
    private javax.swing.JComboBox normBetMethodBox;
    private javax.swing.JButton pValCorrHelpButton3;
    private javax.swing.JComboBox pValCutoffBox1;
    private javax.swing.JPanel qcChoicePanel1;
    private javax.swing.JLabel qcLabel1;
    private javax.swing.JButton removeFileFromGroup1Button;
    private javax.swing.JButton removeFileFromGroup2Button;
    private javax.swing.JButton removeImportButton;
    private javax.swing.JButton saveGroupListButton;
    private javax.swing.JList scInputFileList;
    private javax.swing.JComboBox scMainPValBox;
    private javax.swing.JComboBox scMainTestingStrategyBox;
    private javax.swing.JCheckBox scQCBGImageBox;
    private javax.swing.JCheckBox scQCBoxplotBox;
    private javax.swing.JPanel scQCControlPanel1;
    private javax.swing.JCheckBox scQCHclustBox;
    private javax.swing.JCheckBox scQCHistBox;
    private javax.swing.JCheckBox scQCIncludeAllBox;
    private javax.swing.JCheckBox scQCScatterBox;
    private javax.swing.JTextArea scqcInfoTextArea;
    private javax.swing.JCheckBox wantsMinLFCBox;
    private javax.swing.JCheckBox wantsRawDataBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

   

}
