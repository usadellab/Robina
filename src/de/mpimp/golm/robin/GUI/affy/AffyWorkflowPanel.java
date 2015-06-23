/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AffyMainPanel.java
 *
 * Created on 09.03.2010, 23:25:27
 */

package de.mpimp.golm.robin.GUI.affy;


import affymetrix.fusion.cel.FusionCELData;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.GUI.RobinWorkflow;
import de.mpimp.golm.robin.IPC.MapCommunicator;
import de.mpimp.golm.robin.R.RScriptEditor;
import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.R.RTaskErrorMessageEvaluator;
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
import de.mpimp.golm.robin.data.AffyArrayDataModel;
import de.mpimp.golm.robin.data.ArrayDataModel.AnalysisStrategy;
import de.mpimp.golm.robin.data.ArrayDataModel.InputDataType;
import de.mpimp.golm.robin.designer.GUI.AnalysisDesigner;
import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.robin.designer.model.NoConnectionsDesignException;
import de.mpimp.golm.robin.designer.model.RedundantConnectionsDesignException;
import de.mpimp.golm.robin.misc.FileFilters.AffyFileFilter;
import de.mpimp.golm.robin.misc.RobinAnalysisSummaryGenerator;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author marc
 */
public class AffyWorkflowPanel extends RobinWorkflow {

    private static final String     GREETINGPANEL = "card2";
    private static final String     IMPORTPANEL = "card3";
    private static final String     QCPANEL = "card4";
    private static final String     QCSHOWPANEL = "card5";
    private static final String     CONFPANEL = "card6";
    private static final String     DESIGNPANEL = "card7";
    private static final String     RANKPRODPANEL = "card8";
    private static final String     SCQCPANEL = "card10";
    private static final int        R_JPEGRESOLUTION = 120;

    private AffyArrayDataModel      dataModel;
    private JPanel                  groupListPanel;
    private JPanel                  resultPanel;
    private AnalysisDesigner        analysisDesignerPanel;
    private File                    importGroupListFile;
    private Timer                   qcProcTimer;
    private File                    outputPathFile;
    private String                  sFileSep = System.getProperty("file.separator");

    //flags
    private boolean qcPerformed = false;
    private boolean groupListPanelIsInitialzed = false;
    

    /** Creates new form AffyMainPanel
     * @param main
     * @param projectPath 
     */
    public AffyWorkflowPanel(RobinMainGUI main, File projectPath) {
        super(main,  projectPath);
        initComponents();
        dataModel = new AffyArrayDataModel();
        dataModel.setInputType(InputDataType.AFFYMETRIX);

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
        cl.show(this, IMPORTPANEL);  
        this.setVisible(true);
        outputPathFile = projectPath;
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

        importFilesPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        importPanelNextButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        filePanel = new javax.swing.JPanel();
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
        }
        ;
        addImportButton = new javax.swing.JButton();
        removeImportButton = new javax.swing.JButton();
        celFileInfoButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        importCDFbox = new javax.swing.JCheckBox();
        importCDFButton = new javax.swing.JButton();
        importCDFFileField = new javax.swing.JTextField();
        qualityCheckPanel = new javax.swing.JPanel();
        qcControlPanel = new javax.swing.JPanel();
        qualcheckPanelPreviousButton = new javax.swing.JButton();
        qualcheckPanelNextButton = new javax.swing.JButton();
        qcLabel = new javax.swing.JLabel();
        qcChoicePanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        boxPlotBox = new javax.swing.JCheckBox();
        moreBoxPlotButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        PLMBox = new javax.swing.JCheckBox();
        morePLMButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        RNAdigBox = new javax.swing.JCheckBox();
        moreRNAdigButton = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        histBox = new javax.swing.JCheckBox();
        moreHistButton = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        scatterBox = new javax.swing.JCheckBox();
        moreScatterButton = new javax.swing.JButton();
        expertSettingsBox = new javax.swing.JCheckBox();
        expertSettingsPanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        normMethodBox = new javax.swing.JComboBox();
        pValCorrMethodBox = new javax.swing.JComboBox();
        normHelpButton = new javax.swing.JButton();
        pValCorrHelpButton = new javax.swing.JButton();
        limmaORrpBox = new javax.swing.JComboBox();
        jLabel28 = new javax.swing.JLabel();
        pValCorrHelpButton1 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        pcaBox = new javax.swing.JCheckBox();
        morePCAButton = new javax.swing.JButton();
        PCchooserBox = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        nuseBox = new javax.swing.JCheckBox();
        moreNUSEButton = new javax.swing.JButton();
        qcIncludeAllBox = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        qcInfoTextArea = new javax.swing.JTextArea();
        showQCResultsPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        showQCPanelPreviousButton = new javax.swing.JButton();
        showQCPanelNextButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        qcResultScrollPane = new javax.swing.JScrollPane();
        configureGroupsPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        configGroupsPanelPreviousButton = new javax.swing.JButton();
        configGroupsPanelNextButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        groupsScrollPane = new javax.swing.JScrollPane();
        addGroupButton = new javax.swing.JButton();
        deleteGroupButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        celFileList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                Object item = getModel().getElementAt(index);

                // Return the tool tip text
                return (String) item;
            }
        }
        ;
        saveGroupsTableButton = new javax.swing.JButton();
        loadGroupsTableButton = new javax.swing.JButton();
        configureAnalysisPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        confAnalysisPreviousButton = new javax.swing.JButton();
        confAnalysisNextButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        experimentDesignerScrollPane = new javax.swing.JScrollPane();
        createMetagroupButton = new javax.swing.JButton();
        deleteMetagroupButton = new javax.swing.JButton();
        resetDesignButton = new javax.swing.JButton();
        mainExpertBox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        mainExpertPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        mainWriteRawBox = new javax.swing.JCheckBox();
        guruBox = new javax.swing.JCheckBox();
        mainNormBox = new javax.swing.JComboBox();
        mainPvalBox = new javax.swing.JComboBox();
        mainTestingStrategyBox = new javax.swing.JComboBox();
        LFC2Box = new javax.swing.JCheckBox();
        pValCutoffBox = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        configureRankProdGroups = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        confRPPreviousButton = new javax.swing.JButton();
        confRPNextButton = new javax.swing.JButton();
        groupsPanelRP = new javax.swing.JPanel();
        group1PanelRP = new javax.swing.JPanel();
        addFileToGroup1Button = new javax.swing.JButton();
        removeFileFromGroup1Button = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
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

        setLayout(new java.awt.CardLayout());

        importFilesPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.GridBagLayout());

        importPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        importPanelNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.importPanelNextButton.text")); // NOI18N
        importPanelNextButton.setEnabled(false);
        importPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel2.add(importPanelNextButton, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel5.text")); // NOI18N

        filePanel.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane3.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jScrollPane3.border.title"))); // NOI18N

        importFileList.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setViewportView(importFileList);
        DefaultListModel importListModel = new DefaultListModel();
        ListDataListener l = new ListDataListener() {

            public void intervalAdded(ListDataEvent e) {
                importPanelNextButton.setEnabled(true);
            }

            public void intervalRemoved(ListDataEvent e) {
                if (importFileList.getModel().getSize() == 0) {
                    importPanelNextButton.setEnabled(false);
                } else {
                    importPanelNextButton.setEnabled(true);
                }
            }
            public void contentsChanged(ListDataEvent e) {
            }
        };
        importListModel.addListDataListener(l);
        importFileList.setModel(importListModel);

        addImportButton.setBackground(new java.awt.Color(255, 255, 255));
        addImportButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.addImportButton.text")); // NOI18N
        addImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addImportButton.setContentAreaFilled(false);
        addImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImportButtonActionPerformed(evt);
            }
        });

        removeImportButton.setBackground(new java.awt.Color(255, 255, 255));
        removeImportButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.removeImportButton.text")); // NOI18N
        removeImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeImportButton.setContentAreaFilled(false);
        removeImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImportButtonActionPerformed(evt);
            }
        });

        celFileInfoButton.setBackground(new java.awt.Color(255, 255, 255));
        celFileInfoButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.celFileInfoButton.text")); // NOI18N
        celFileInfoButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        celFileInfoButton.setContentAreaFilled(false);
        celFileInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                celFileInfoButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout filePanelLayout = new org.jdesktop.layout.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, filePanelLayout.createSequentialGroup()
                .addContainerGap(469, Short.MAX_VALUE)
                .add(addImportButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(celFileInfoButton)
                .addContainerGap())
        );

        filePanelLayout.linkSize(new java.awt.Component[] {addImportButton, celFileInfoButton, removeImportButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, filePanelLayout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addImportButton)
                    .add(celFileInfoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        filePanelLayout.linkSize(new java.awt.Component[] {addImportButton, celFileInfoButton, removeImportButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jLabel31.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel31.text")); // NOI18N

        importCDFbox.setBackground(new java.awt.Color(255, 255, 255));
        importCDFbox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.importCDFbox.text")); // NOI18N
        importCDFbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCDFboxActionPerformed(evt);
            }
        });

        importCDFButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/openfolderHS.png"))); // NOI18N
        importCDFButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.importCDFButton.text")); // NOI18N
        importCDFButton.setBorder(null);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, importCDFbox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), importCDFButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        importCDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCDFButtonActionPerformed(evt);
            }
        });

        importCDFFileField.setEditable(false);
        importCDFFileField.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.importCDFFileField.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, importCDFbox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), importCDFFileField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout importFilesPanelLayout = new org.jdesktop.layout.GroupLayout(importFilesPanel);
        importFilesPanel.setLayout(importFilesPanelLayout);
        importFilesPanelLayout.setHorizontalGroup(
            importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .add(importFilesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(importCDFbox)
                    .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(importFilesPanelLayout.createSequentialGroup()
                            .add(importCDFFileField)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(importCDFButton))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 263, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(filePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        importFilesPanelLayout.setVerticalGroup(
            importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, importFilesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(importFilesPanelLayout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(importCDFbox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(importFilesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(importCDFButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(importCDFFileField)))
                    .add(filePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(importFilesPanel, "card3");

        qualityCheckPanel.setBackground(new java.awt.Color(255, 255, 255));

        qcControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        qcControlPanel.setLayout(new java.awt.GridBagLayout());

        qualcheckPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        qualcheckPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.qualcheckPanelPreviousButton.text")); // NOI18N
        qualcheckPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qualcheckPanelPreviousButtonActionPerformed(evt);
            }
        });
        qcControlPanel.add(qualcheckPanelPreviousButton, new java.awt.GridBagConstraints());

        qualcheckPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        qualcheckPanelNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.qualcheckPanelNextButton.text")); // NOI18N
        qualcheckPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qualcheckPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        qcControlPanel.add(qualcheckPanelNextButton, gridBagConstraints);

        qcLabel.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.qcLabel.text")); // NOI18N

        qcChoicePanel.setBackground(new java.awt.Color(255, 255, 255));
        qcChoicePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel5.setBackground(new java.awt.Color(214, 232, 255));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/Boxsmall.jpg"))); // NOI18N
        jLabel9.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel6.text")); // NOI18N
        jLabel6.setIconTextGap(10);
        jLabel6.setPreferredSize(new java.awt.Dimension(850, 14));

        jPanel7.setBackground(new java.awt.Color(214, 232, 255));
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        boxPlotBox.setBackground(new java.awt.Color(214, 232, 255));
        boxPlotBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.boxPlotBox.text")); // NOI18N
        boxPlotBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxPlotBoxActionPerformed(evt);
            }
        });
        jPanel7.add(boxPlotBox);

        moreBoxPlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreBoxPlotButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.moreBoxPlotButton.text")); // NOI18N
        moreBoxPlotButton.setBorderPainted(false);
        moreBoxPlotButton.setContentAreaFilled(false);
        moreBoxPlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreBoxPlotButtonActionPerformed(evt);
            }
        });
        jPanel7.add(moreBoxPlotButton);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
            .add(jLabel9)
            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/PLMSmall.jpg"))); // NOI18N
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel11.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel11.text")); // NOI18N

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        PLMBox.setBackground(new java.awt.Color(255, 255, 255));
        PLMBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.PLMBox.text")); // NOI18N
        PLMBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PLMBoxActionPerformed(evt);
            }
        });
        jPanel8.add(PLMBox);

        morePLMButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        morePLMButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.morePLMButton.text")); // NOI18N
        morePLMButton.setBorderPainted(false);
        morePLMButton.setContentAreaFilled(false);
        morePLMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                morePLMButtonActionPerformed(evt);
            }
        });
        jPanel8.add(morePLMButton);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel11)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(214, 232, 255));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/RnaSmall.jpg"))); // NOI18N
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel13.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel13.text")); // NOI18N

        jPanel10.setBackground(new java.awt.Color(214, 232, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.LINE_AXIS));

        RNAdigBox.setBackground(new java.awt.Color(214, 232, 255));
        RNAdigBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.RNAdigBox.text")); // NOI18N
        RNAdigBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RNAdigBoxActionPerformed(evt);
            }
        });
        jPanel10.add(RNAdigBox);

        moreRNAdigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreRNAdigButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.moreRNAdigButton.text")); // NOI18N
        moreRNAdigButton.setBorderPainted(false);
        moreRNAdigButton.setContentAreaFilled(false);
        moreRNAdigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreRNAdigButtonActionPerformed(evt);
            }
        });
        jPanel10.add(moreRNAdigButton);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel13)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/HistSmall.jpg"))); // NOI18N
        jLabel14.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel15.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel15.text")); // NOI18N

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel12.setLayout(new javax.swing.BoxLayout(jPanel12, javax.swing.BoxLayout.LINE_AXIS));

        histBox.setBackground(new java.awt.Color(255, 255, 255));
        histBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.histBox.text")); // NOI18N
        histBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                histBoxActionPerformed(evt);
            }
        });
        jPanel12.add(histBox);

        moreHistButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreHistButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.moreHistButton.text")); // NOI18N
        moreHistButton.setBorderPainted(false);
        moreHistButton.setContentAreaFilled(false);
        moreHistButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreHistButtonActionPerformed(evt);
            }
        });
        jPanel12.add(moreHistButton);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel15)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(214, 232, 255));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/ScatSmall.jpg"))); // NOI18N
        jLabel16.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel17.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel17.text")); // NOI18N

        jPanel14.setBackground(new java.awt.Color(214, 232, 255));
        jPanel14.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));

        scatterBox.setBackground(new java.awt.Color(214, 232, 255));
        scatterBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.scatterBox.text")); // NOI18N
        scatterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scatterBoxActionPerformed(evt);
            }
        });
        jPanel14.add(scatterBox);

        moreScatterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreScatterButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.moreScatterButton.text")); // NOI18N
        moreScatterButton.setBorderPainted(false);
        moreScatterButton.setContentAreaFilled(false);
        moreScatterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreScatterButtonActionPerformed(evt);
            }
        });
        jPanel14.add(moreScatterButton);

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(jLabel16)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .add(22, 22, 22)
                .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
            .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
            .add(jLabel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        expertSettingsBox.setBackground(new java.awt.Color(255, 255, 255));
        expertSettingsBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.expertSettingsBox.text")); // NOI18N
        expertSettingsBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        expertSettingsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expertSettingsBoxActionPerformed(evt);
            }
        });

        expertSettingsPanel.setBackground(new java.awt.Color(255, 255, 255));
        expertSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.expertSettingsPanel.border.title"))); // NOI18N

        jLabel18.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel18.text")); // NOI18N

        jLabel19.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel19.text")); // NOI18N

        normMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "rma", "gcrma", "mas5", "justPlier" }));
        normMethodBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normMethodBoxActionPerformed(evt);
            }
        });

        pValCorrMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BH", "holm", "hochberg", "hommel", "bonferroni", "BY", "fdr", "none" }));
        pValCorrMethodBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pValCorrMethodBoxActionPerformed(evt);
            }
        });

        normHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        normHelpButton.setBorderPainted(false);
        normHelpButton.setContentAreaFilled(false);
        normHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normHelpButtonActionPerformed(evt);
            }
        });

        pValCorrHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        pValCorrHelpButton.setBorderPainted(false);
        pValCorrHelpButton.setContentAreaFilled(false);
        pValCorrHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pValCorrHelpButtonActionPerformed(evt);
            }
        });

        limmaORrpBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear models (package limma)", "Rank product (package RankProd)" }));

        jLabel28.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel28.text")); // NOI18N

        pValCorrHelpButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        pValCorrHelpButton1.setBorderPainted(false);
        pValCorrHelpButton1.setContentAreaFilled(false);
        pValCorrHelpButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pValCorrHelpButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout expertSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(expertSettingsPanel);
        expertSettingsPanel.setLayout(expertSettingsPanelLayout);
        expertSettingsPanelLayout.setHorizontalGroup(
            expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertSettingsPanelLayout.createSequentialGroup()
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(jLabel18)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(normMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(normHelpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel19)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pValCorrMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(expertSettingsPanelLayout.createSequentialGroup()
                        .add(jLabel28)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(limmaORrpBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 314, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pValCorrHelpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pValCorrHelpButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18))
        );

        expertSettingsPanelLayout.linkSize(new java.awt.Component[] {pValCorrHelpButton, pValCorrHelpButton1}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        expertSettingsPanelLayout.setVerticalGroup(
            expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel19)
                        .add(pValCorrMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pValCorrHelpButton)
                    .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel18)
                        .add(normMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(normHelpButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel28)
                        .add(limmaORrpBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pValCorrHelpButton1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/pca_small.png"))); // NOI18N
        jLabel20.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel21.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel21.text")); // NOI18N

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel17.setLayout(new javax.swing.BoxLayout(jPanel17, javax.swing.BoxLayout.LINE_AXIS));

        pcaBox.setBackground(new java.awt.Color(255, 255, 255));
        pcaBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.pcaBox.text")); // NOI18N
        pcaBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pcaBoxActionPerformed(evt);
            }
        });
        jPanel17.add(pcaBox);

        morePCAButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        morePCAButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.morePCAButton.text")); // NOI18N
        morePCAButton.setBorderPainted(false);
        morePCAButton.setContentAreaFilled(false);
        morePCAButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                morePCAButtonActionPerformed(evt);
            }
        });
        jPanel17.add(morePCAButton);

        PCchooserBox.setFont(PCchooserBox.getFont().deriveFont(PCchooserBox.getFont().getSize()-3f));
        PCchooserBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PC 1 & 2", "PC 1 to 4", "PC 1 to 6" }));
        PCchooserBox.setPreferredSize(new java.awt.Dimension(100, 24));
        PCchooserBox.setSize(new java.awt.Dimension(96, 24));
        PCchooserBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PCchooserBoxActionPerformed(evt);
            }
        });

        jLabel30.setFont(jLabel30.getFont().deriveFont(jLabel30.getFont().getSize()-3f));
        jLabel30.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel30.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .add(jLabel20)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel30)
                .add(5, 5, 5)
                .add(PCchooserBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel20)
            .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .add(PCchooserBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel30))
            .add(jPanel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
        );

        jPanel18.setBackground(new java.awt.Color(214, 232, 255));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/nuse_small.png"))); // NOI18N
        jLabel22.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel23.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel23.text")); // NOI18N

        jPanel19.setBackground(new java.awt.Color(214, 232, 255));
        jPanel19.setPreferredSize(new java.awt.Dimension(150, 0));
        jPanel19.setLayout(new javax.swing.BoxLayout(jPanel19, javax.swing.BoxLayout.LINE_AXIS));

        nuseBox.setBackground(new java.awt.Color(214, 232, 255));
        nuseBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.nuseBox.text")); // NOI18N
        nuseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuseBoxActionPerformed(evt);
            }
        });
        jPanel19.add(nuseBox);

        moreNUSEButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        moreNUSEButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.moreNUSEButton.text")); // NOI18N
        moreNUSEButton.setBorderPainted(false);
        moreNUSEButton.setContentAreaFilled(false);
        moreNUSEButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreNUSEButtonActionPerformed(evt);
            }
        });
        jPanel19.add(moreNUSEButton);

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .add(jLabel22)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel23, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel23, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
            .add(jLabel22)
            .add(jPanel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
        );

        qcIncludeAllBox.setBackground(new java.awt.Color(255, 255, 255));
        qcIncludeAllBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.qcIncludeAllBox.text")); // NOI18N
        qcIncludeAllBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        qcIncludeAllBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcIncludeAllBoxActionPerformed(evt);
            }
        });

        jLabel24.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel24.text")); // NOI18N

        org.jdesktop.layout.GroupLayout qcChoicePanelLayout = new org.jdesktop.layout.GroupLayout(qcChoicePanel);
        qcChoicePanel.setLayout(qcChoicePanelLayout);
        qcChoicePanelLayout.setHorizontalGroup(
            qcChoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(qcChoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(expertSettingsBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, qcChoicePanelLayout.createSequentialGroup()
                .addContainerGap(522, Short.MAX_VALUE)
                .add(qcIncludeAllBox)
                .add(124, 124, 124))
        );
        qcChoicePanelLayout.setVerticalGroup(
            qcChoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcChoicePanelLayout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(qcIncludeAllBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(qcChoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(qcChoicePanelLayout.createSequentialGroup()
                        .add(expertSettingsBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(expertSettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(51, 51, 51))
        );

        expertSettingsPanel.setVisible(false);

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jPanel20.border.title"))); // NOI18N

        jScrollPane2.setBorder(null);

        qcInfoTextArea.setColumns(20);
        qcInfoTextArea.setEditable(false);
        qcInfoTextArea.setFont(new java.awt.Font("Arial", 0, 12));
        qcInfoTextArea.setLineWrap(true);
        qcInfoTextArea.setRows(5);
        qcInfoTextArea.setWrapStyleWord(true);
        qcInfoTextArea.setBorder(null);
        jScrollPane2.setViewportView(qcInfoTextArea);

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout qualityCheckPanelLayout = new org.jdesktop.layout.GroupLayout(qualityCheckPanel);
        qualityCheckPanel.setLayout(qualityCheckPanelLayout);
        qualityCheckPanelLayout.setHorizontalGroup(
            qualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qualityCheckPanelLayout.createSequentialGroup()
                .add(11, 11, 11)
                .add(qualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(qcLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(qcChoicePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(qcControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
        );
        qualityCheckPanelLayout.setVerticalGroup(
            qualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, qualityCheckPanelLayout.createSequentialGroup()
                .add(qualityCheckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(qualityCheckPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(qcLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(qcChoicePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(qcControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(qualityCheckPanel, "card4");

        showQCResultsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new java.awt.GridBagLayout());

        showQCPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        showQCPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.showQCPanelPreviousButton.text")); // NOI18N
        showQCPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showQCPanelPreviousButtonActionPerformed(evt);
            }
        });
        jPanel4.add(showQCPanelPreviousButton, new java.awt.GridBagConstraints());

        showQCPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        showQCPanelNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.showQCPanelNextButton.text")); // NOI18N
        showQCPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showQCPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel4.add(showQCPanelNextButton, gridBagConstraints);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel7.text")); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        qcResultScrollPane.setMaximumSize(new java.awt.Dimension(700, 32767));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(qcResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(qcResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout showQCResultsPanelLayout = new org.jdesktop.layout.GroupLayout(showQCResultsPanel);
        showQCResultsPanel.setLayout(showQCResultsPanelLayout);
        showQCResultsPanelLayout.setHorizontalGroup(
            showQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .add(showQCResultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        showQCResultsPanelLayout.setVerticalGroup(
            showQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, showQCResultsPanelLayout.createSequentialGroup()
                .add(showQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, showQCResultsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(showQCResultsPanel, "card5");

        configureGroupsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridBagLayout());

        configGroupsPanelPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        configGroupsPanelPreviousButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.configGroupsPanelPreviousButton.text")); // NOI18N
        configGroupsPanelPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configGroupsPanelPreviousButtonActionPerformed(evt);
            }
        });
        jPanel3.add(configGroupsPanelPreviousButton, new java.awt.GridBagConstraints());

        configGroupsPanelNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        configGroupsPanelNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.configGroupsPanelNextButton.text")); // NOI18N
        configGroupsPanelNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configGroupsPanelNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel3.add(configGroupsPanelNextButton, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel8.text")); // NOI18N

        groupsScrollPane.setBorder(null);

        addGroupButton.setBackground(new java.awt.Color(255, 255, 255));
        addGroupButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.addGroupButton.text")); // NOI18N
        addGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupButtonActionPerformed(evt);
            }
        });

        deleteGroupButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteGroupButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.deleteGroupButton.text")); // NOI18N
        deleteGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteGroupButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jPanel21.border.title"))); // NOI18N

        jScrollPane4.setBorder(null);

        jScrollPane4.setViewportView(celFileList);

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        saveGroupsTableButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.saveGroupsTableButton.text")); // NOI18N
        saveGroupsTableButton.setToolTipText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.saveGroupsTableButton.toolTipText")); // NOI18N
        saveGroupsTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGroupsTableButtonActionPerformed(evt);
            }
        });

        loadGroupsTableButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.loadGroupsTableButton.text")); // NOI18N
        loadGroupsTableButton.setToolTipText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.loadGroupsTableButton.toolTipText")); // NOI18N
        loadGroupsTableButton.setEnabled(false);
        loadGroupsTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGroupsTableButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout configureGroupsPanelLayout = new org.jdesktop.layout.GroupLayout(configureGroupsPanel);
        configureGroupsPanel.setLayout(configureGroupsPanelLayout);
        configureGroupsPanelLayout.setHorizontalGroup(
            configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .add(configureGroupsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(configureGroupsPanelLayout.createSequentialGroup()
                        .add(saveGroupsTableButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(loadGroupsTableButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 280, Short.MAX_VALUE)
                        .add(addGroupButton)
                        .add(deleteGroupButton))
                    .add(groupsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)))
        );
        configureGroupsPanelLayout.setVerticalGroup(
            configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configureGroupsPanelLayout.createSequentialGroup()
                .add(configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(configureGroupsPanelLayout.createSequentialGroup()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(configureGroupsPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, configureGroupsPanelLayout.createSequentialGroup()
                        .add(groupsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(configureGroupsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(addGroupButton)
                                .add(saveGroupsTableButton)
                                .add(loadGroupsTableButton))
                            .add(deleteGroupButton))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(configureGroupsPanel, "card6");

        configureAnalysisPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel15.setLayout(new java.awt.GridBagLayout());

        confAnalysisPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        confAnalysisPreviousButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.confAnalysisPreviousButton.text")); // NOI18N
        confAnalysisPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confAnalysisPreviousButtonActionPerformed(evt);
            }
        });
        jPanel15.add(confAnalysisPreviousButton, new java.awt.GridBagConstraints());

        confAnalysisNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        confAnalysisNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.confAnalysisNextButton.text")); // NOI18N
        confAnalysisNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confAnalysisNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel15.add(confAnalysisNextButton, gridBagConstraints);

        jLabel25.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel25.text")); // NOI18N

        experimentDesignerScrollPane.setBorder(null);

        createMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        createMetagroupButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.createMetagroupButton.text")); // NOI18N
        createMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMetagroupButtonActionPerformed(evt);
            }
        });

        deleteMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteMetagroupButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.deleteMetagroupButton.text")); // NOI18N
        deleteMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMetagroupButtonActionPerformed(evt);
            }
        });

        resetDesignButton.setBackground(new java.awt.Color(255, 255, 255));
        resetDesignButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.resetDesignButton.text")); // NOI18N
        resetDesignButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDesignButtonActionPerformed(evt);
            }
        });

        mainExpertBox.setBackground(new java.awt.Color(255, 255, 255));
        mainExpertBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.mainExpertBox.text")); // NOI18N
        mainExpertBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainExpertBoxActionPerformed(evt);
            }
        });

        mainExpertPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainExpertPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.mainExpertPanel.border.title"))); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel2.text")); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel3.text")); // NOI18N

        mainWriteRawBox.setBackground(new java.awt.Color(255, 255, 255));
        mainWriteRawBox.setSelected(true);
        mainWriteRawBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.mainWriteRawBox.text")); // NOI18N
        mainWriteRawBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        guruBox.setBackground(new java.awt.Color(255, 255, 255));
        guruBox.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.guruBox.text")); // NOI18N
        guruBox.setToolTipText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.guruBox.toolTipText")); // NOI18N
        guruBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        guruBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        mainNormBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "rma", "gcrma", "mas5", "justPlier" }));

        mainPvalBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BH", "BY", "fdr", "holm", "none" }));

        mainTestingStrategyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "nestedF", "global", "separate", "hierarchical" }));

        LFC2Box.setBackground(new java.awt.Color(255, 255, 255));
        LFC2Box.setSelected(true);
        LFC2Box.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.LFC2Box.text")); // NOI18N
        LFC2Box.setToolTipText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.LFC2Box.toolTipText")); // NOI18N

        pValCutoffBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.05", "0.025", "0.01", "0.005", "0.0025", "0.0005", "0.00025", "0.00005", "0.1", "0.15", "0.2", "0.25", "0.005" }));

        jLabel29.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel29.text")); // NOI18N

        org.jdesktop.layout.GroupLayout mainExpertPanelLayout = new org.jdesktop.layout.GroupLayout(mainExpertPanel);
        mainExpertPanel.setLayout(mainExpertPanelLayout);
        mainExpertPanelLayout.setHorizontalGroup(
            mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainExpertPanelLayout.createSequentialGroup()
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(mainExpertPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainTestingStrategyBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPvalBox, 0, 129, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainNormBox, 0, 129, Short.MAX_VALUE))
                .add(17, 17, 17))
            .add(mainExpertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(LFC2Box)
                .addContainerGap(67, Short.MAX_VALUE))
            .add(mainExpertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainWriteRawBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(guruBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
            .add(mainExpertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel29)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pValCutoffBox, 0, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainExpertPanelLayout.setVerticalGroup(
            mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainExpertPanelLayout.createSequentialGroup()
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(mainNormBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(mainPvalBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mainTestingStrategyBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainWriteRawBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guruBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(LFC2Box)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                .add(mainExpertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(pValCutoffBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout configureAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(configureAnalysisPanel);
        configureAnalysisPanel.setLayout(configureAnalysisPanelLayout);
        configureAnalysisPanelLayout.setHorizontalGroup(
            configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .add(configureAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(configureAnalysisPanelLayout.createSequentialGroup()
                        .add(mainExpertBox)
                        .add(98, 98, 98))
                    .add(jSeparator2)
                    .add(mainExpertPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel25))
                .add(configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(configureAnalysisPanelLayout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(resetDesignButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 253, Short.MAX_VALUE)
                        .add(createMetagroupButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(deleteMetagroupButton))
                    .add(configureAnalysisPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(experimentDesignerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))))
        );
        configureAnalysisPanelLayout.setVerticalGroup(
            configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, configureAnalysisPanelLayout.createSequentialGroup()
                .add(configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, configureAnalysisPanelLayout.createSequentialGroup()
                        .add(experimentDesignerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(configureAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(deleteMetagroupButton)
                            .add(createMetagroupButton)
                            .add(resetDesignButton)))
                    .add(configureAnalysisPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainExpertBox)
                        .add(18, 18, 18)
                        .add(mainExpertPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(configureAnalysisPanel, "card7");

        configureRankProdGroups.setBackground(new java.awt.Color(255, 255, 255));

        jPanel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel22.setLayout(new java.awt.GridBagLayout());

        confRPPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        confRPPreviousButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.confRPPreviousButton.text")); // NOI18N
        confRPPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confRPPreviousButtonActionPerformed(evt);
            }
        });
        jPanel22.add(confRPPreviousButton, new java.awt.GridBagConstraints());

        confRPNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        confRPNextButton.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.confRPNextButton.text")); // NOI18N
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
        addFileToGroup1Button.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.addFileToGroup1Button.text")); // NOI18N
        addFileToGroup1Button.setName("Group1AddButton"); // NOI18N
        addFileToGroup1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToGroup1ButtonActionPerformed(evt);
            }
        });

        removeFileFromGroup1Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        removeFileFromGroup1Button.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.removeFileFromGroup1Button.text")); // NOI18N
        removeFileFromGroup1Button.setName("Group1RmButton"); // NOI18N
        removeFileFromGroup1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileFromGroup1ButtonActionPerformed(evt);
            }
        });

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel4.text")); // NOI18N

        group1NameField.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.group1NameField.text")); // NOI18N
        group1NameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                group1NameFieldKeyPressed(evt);
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
        // set the column sizes
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
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(group1NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );
        group1PanelRPLayout.setVerticalGroup(
            group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRPLayout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .add(group1PanelRPLayout.createSequentialGroup()
                        .add(group1PanelRPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(group1NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
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
        addFileToGroup2Button.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.addFileToGroup2Button.text")); // NOI18N
        addFileToGroup2Button.setName("Group2AddButton"); // NOI18N
        addFileToGroup2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToGroup2ButtonActionPerformed(evt);
            }
        });

        removeFileFromGroup2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        removeFileFromGroup2Button.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.removeFileFromGroup2Button.text")); // NOI18N
        removeFileFromGroup2Button.setName("Group2RmButton"); // NOI18N
        removeFileFromGroup2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileFromGroup2ButtonActionPerformed(evt);
            }
        });

        jLabel27.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel27.text")); // NOI18N

        group2NameField.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.group2NameField.text")); // NOI18N
        group2NameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                group2NameFieldKeyPressed(evt);
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
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );
        group1PanelRP1Layout.setVerticalGroup(
            group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(group1PanelRP1Layout.createSequentialGroup()
                .addContainerGap()
                .add(group1PanelRP1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
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

        jLabel26.setText(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jLabel26.text")); // NOI18N

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AffyWorkflowPanel.class, "AffyWorkflowPanel.jPanel23.border.title"))); // NOI18N

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
                .add(jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout configureRankProdGroupsLayout = new org.jdesktop.layout.GroupLayout(configureRankProdGroups);
        configureRankProdGroups.setLayout(configureRankProdGroupsLayout);
        configureRankProdGroupsLayout.setHorizontalGroup(
            configureRankProdGroupsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE)
            .add(configureRankProdGroupsLayout.createSequentialGroup()
                .add(jPanel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupsPanelRP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE))
        );
        configureRankProdGroupsLayout.setVerticalGroup(
            configureRankProdGroupsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, configureRankProdGroupsLayout.createSequentialGroup()
                .add(configureRankProdGroupsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(groupsPanelRP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(configureRankProdGroups, "card8");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private boolean isHomogeneousAffyChipImport() {
        // check the chip type identifiers and make sure all files are of the same type
        DefaultListModel celFileListModel = (DefaultListModel) importFileList.getModel();
        FusionCELData celFileData = new FusionCELData();
        HashSet<String> types = new HashSet<String>();

        for (Object fileName : celFileListModel.toArray() ) {
            if (fileName instanceof String) {
                //DEBUG
                System.out.print("checking file type of file: " + fileName);
                celFileData.setFileName( (String) fileName);
                celFileData.readHeader();
                String chipType = celFileData.getChipType();
                System.out.println(" type is " + chipType);
                types.add(chipType);
            }
        }

        if (types.size() != 1) {
            new SimpleErrorMessage(this,    "The CEL files you're trying to import\n" +
                    "originate from different Affymetrix\n" +
                    "chip platforms. Mixed chip types cannot\n" +
                    "be analyzed together. Please make sure you\n" +
                    "import data generated on one platform.");
            return false;
        } else {
            dataModel.setChipType((String) types.toArray()[0]);
        }
        return true;
    }


    private void importPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importPanelNextButtonActionPerformed
        if (!isImporting && !groupListPanelIsInitialzed) {
             if (!isHomogeneousAffyChipImport()) return;
        }


        // these will not work (like mas5calls) without the probe info package
        // so deactivate them to maintain stability
        if ( this.dataModel.isCustomCDFimport() ) {
            nuseBox.setEnabled(false);
            PLMBox.setEnabled(false);
        }

        mainGUI.setStatusText("Step 2 of 4");
        CardLayout cl = (CardLayout) this.getLayout();    
        cl.show(this, QCPANEL);
}//GEN-LAST:event_importPanelNextButtonActionPerformed

    private void addImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImportButtonActionPerformed

        if (isImporting) {
            // check whether all the files are of the same chip type

        }

        DefaultListModel importListModel = (DefaultListModel) importFileList.getModel();
        File[] files = null;

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new AffyFileFilter());
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
        } else {
            return;
        }

        for (File f : files) {
            importListModel.addElement(f.getAbsolutePath());
        }
        
        // check whether the chip types fit
        if (!isHomogeneousAffyChipImport()) {
            for (File f : files) {
                importListModel.removeElement(f.getAbsolutePath());
            }
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

    private void celFileInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_celFileInfoButtonActionPerformed

        DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
        int[] selectedIndices = importFileList.getSelectedIndices();
        ArrayList<String> files = new ArrayList<String>();

        for (int index : selectedIndices) {
            String selectedFile = importModel.getElementAt(index).toString();
            if (selectedFile != null) {
                files.add(selectedFile);
            }
        }
        AffyCelFileInfoFrame infoFrame = new AffyCelFileInfoFrame(files);
        infoFrame.setVisible(true);
    }//GEN-LAST:event_celFileInfoButtonActionPerformed

    private void qualcheckPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qualcheckPanelPreviousButtonActionPerformed
        importFilesPanel.setVisible(true);
        if (isImporting || groupListPanelIsInitialzed) {
            importPanelNextButton.setEnabled(true);
        }
        mainGUI.setStatusText("Step 1 of 4");
        qualityCheckPanel.setVisible(false);
}//GEN-LAST:event_qualcheckPanelPreviousButtonActionPerformed

    private void populateRPGroupsFromFile() throws FileNotFoundException, DocumentException {
        //load the group definition from the file
        FileReader reader = new FileReader(dataModel.getGroupsFile());
        Element groupDef = new SAXReader().read(reader).getRootElement();
        ArrayList<String> addedPaths = new ArrayList<String>();

        Iterator<Element> groupIter = groupDef.elementIterator("group");

        //group1
        Element group1 = groupIter.next();
        //DEBUG
        String group1Name = group1.attributeValue("name");
        System.out.println("group1 = " + group1Name);
        group1NameField.setText(group1Name);

        Iterator<Element> fileIter = group1.elementIterator("file");
        while (fileIter.hasNext()) {
            Element file = fileIter.next();
            //DEBUG
            String path = file.attributeValue("path"); 
            Integer origin;
            if (file.attributeValue("source") == null) {
                origin = 1;
            } else {
                origin = Integer.parseInt(file.attributeValue("source"));
            }
            System.out.println("-->" + path);
            if ( ((DefaultListModel)celFileListRP.getModel()).contains(path) ) {
                ((DefaultTableModel)group1Table.getModel()).addRow(new Object[] {path, origin});
                addedPaths.add(path);
            }
        }

        //group2
        Element group2 = groupIter.next();
        //DEBUG
        String group2Name = group2.attributeValue("name");
        System.out.println("group2 = " + group2Name);
        group2NameField.setText(group2Name);

        Iterator<Element> fileIter2 = group2.elementIterator("file");
        while (fileIter2.hasNext()) {
            Element file = fileIter2.next();
            //DEBUG
            String path = file.attributeValue("path");
            Integer origin;
            if (file.attributeValue("source") == null) {
                origin = 1;
            } else {
                origin = Integer.parseInt(file.attributeValue("source"));
            }
            System.out.println("-->" + path);

            if ( ((DefaultListModel)celFileListRP.getModel()).contains(path) ) {
                ((DefaultTableModel)group2Table.getModel()).addRow(new Object[] {path, origin});
                addedPaths.add(path);
            }
            
        }

        // delete all imported input files from the source list
        for (String file : addedPaths) {
            ((DefaultListModel)celFileListRP.getModel()).removeElement(file);
        }


    }
   

    private void qualcheckPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qualcheckPanelNextButtonActionPerformed

        // feed settings into model
        dataModel.setAffyNormalizationMethod((String) normMethodBox.getSelectedItem());
        dataModel.setPValCorrectionMethod((String) pValCorrMethodBox.getSelectedItem());
        

        if (    !boxPlotBox.isSelected() &&
                !PLMBox.isSelected() &&
                !RNAdigBox.isSelected() &&
                !histBox.isSelected() &&
                !pcaBox.isSelected() &&
                !nuseBox.isSelected() &&
                !scatterBox.isSelected() ) {
            // no QC selected or QC already performed - directly go to the configure groups panel
            System.out.println("skipping QC");

            // decide which method will be used
            if (((String)limmaORrpBox.getSelectedItem()).startsWith("Linear") ) {
                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, CONFPANEL);
                celFileList.setModel(importFileList.getModel());
                initializeGroupListPanel();
                return;
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

                celFileListRP.setModel(importFileList.getModel());

                if (this.isImporting) {
                    try {
                        populateRPGroupsFromFile();
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (DocumentException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, RANKPRODPANEL);
                return;
            }


        } else if (qcPerformed) {
            //TODO check wheter there are new files in the import file
            // list and ask whether the QC shall be startet again including
            // the new files else just show the previous results
            int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to re-run the chosen quality check\n"+
                    "analyses? Choosing 'yes' will override previous results.",
                    "Re-run quality checks?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                System.out.println("skipping QC because it was already run");
                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, QCSHOWPANEL);
                return;
            }
        }

        // start QC analyses
        setQCcontrolsEnabled(false);
        mainGUI.startBusyAnimation("running quality checks...");
        mainGUI.setProgressLabelText("running quality checks");
        qcControlPanel.setEnabled(false);
        qualcheckPanelNextButton.setEnabled(false);
        qualcheckPanelPreviousButton.setEnabled(false);

        final ArrayList<RTask> qcProcs = new ArrayList<RTask>();
        final ArrayList<String> methods = new ArrayList<String>();
        final ArrayList<String> templates = new ArrayList<String>();
        int nrOfInputFiles = importFileList.getModel().getSize();

        // do the quality checking stuff and fill the showQCpanel
        if (boxPlotBox.isSelected()) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("boxplot"));
            methods.add("boxplot");
            templates.add("boxplot");
            methods.add("maplot");
        }
        if (PLMBox.isSelected()) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("plm"));
            methods.add("plm");
            templates.add("plm");
        }
        if (RNAdigBox.isSelected()) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("rna"));
            methods.add("rna");
            templates.add("rna");
        }
        if (histBox.isSelected()) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("hist"));
            methods.add("hist");
            templates.add("hist");
        }
        if (scatterBox.isSelected() && (nrOfInputFiles > 1)) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("scat"));
            methods.add("scat");
            templates.add("scat");
        }
        if (pcaBox.isSelected() && (nrOfInputFiles > 1)) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("pca"));
            methods.add("pca");
            if (nrOfInputFiles > 2) methods.add("hclust");
            templates.add("pca");
        }
        if (nuseBox.isSelected()) {
            //if (SEPARATE_QC_PROCS) qcProcs.add(generateQCProccess("nuse"));
            methods.add("nuse");
            methods.add("rle");
            templates.add("nuse");
        }

        // generate all-in-one QC script
        qcProcs.add(generateBatchQCScript(templates));        

        for (RTask qcProc : qcProcs) {
            System.out.println("starting task:"+qcProc.getMethod());
            executor.execute(qcProc);
            //qcProc.start();
        }
        executor.shutdown();


        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (executor.isTerminated()) {

                    qcProcTimer.stop();
                    mainGUI.setProgressLabelText("idle");
                    mainGUI.stopBusyAnimation();
                    mainGUI.setStatusText("Step 3 of 4");
                    qcControlPanel.setEnabled(true);
                    qcPerformed = true;

                    // check whether all processes terminated smoothly
                    for (RTask proc : qcProcs) {
                        if (proc.getExitValue() != 0) {

                            RTaskErrorMessageEvaluator eval = new RTaskErrorMessageEvaluator(proc, AffyWorkflowPanel.this);
                            eval.evaluateError();
                            return;
                        }
                    }

                    //if (SEPARATE_QC_PROCS) {
                    //    showQCResults(qcProcs);
                    //} else {
                        showBatchQCResults(qcProcs.get(0), methods);
                    //}
                    qualcheckPanelNextButton.setEnabled(true);
                    qualcheckPanelPreviousButton.setEnabled(true);
                    setQCcontrolsEnabled(true);
                    CardLayout cl = (CardLayout) AffyWorkflowPanel.this.getLayout();
                    cl.show(AffyWorkflowPanel.this, QCSHOWPANEL);
                }
            }
        };

        // start a timer polling the executor every 100ms
        //
        qcProcTimer = new Timer(100, al);
        qcProcTimer.start();
}//GEN-LAST:event_qualcheckPanelNextButtonActionPerformed

    private void boxPlotBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxPlotBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_boxPlotBoxActionPerformed

    private void moreBoxPlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreBoxPlotButtonActionPerformed
        qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.boxPlotInfoText"));
}//GEN-LAST:event_moreBoxPlotButtonActionPerformed

    private void PLMBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PLMBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_PLMBoxActionPerformed

    private void morePLMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_morePLMButtonActionPerformed
        qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.plmInfoText"));
}//GEN-LAST:event_morePLMButtonActionPerformed

    private void RNAdigBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RNAdigBoxActionPerformed

}//GEN-LAST:event_RNAdigBoxActionPerformed

    private void moreRNAdigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreRNAdigButtonActionPerformed
        qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.rnaInfoText"));
}//GEN-LAST:event_moreRNAdigButtonActionPerformed

    private void histBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_histBoxActionPerformed

    private void moreHistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreHistButtonActionPerformed
        qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.histInfoText"));
}//GEN-LAST:event_moreHistButtonActionPerformed

    private void scatterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scatterBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_scatterBoxActionPerformed

    private void moreScatterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreScatterButtonActionPerformed
        qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.scatterInfoText"));
}//GEN-LAST:event_moreScatterButtonActionPerformed

    private void expertSettingsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expertSettingsBoxActionPerformed
        expertSettingsPanel.setVisible(expertSettingsBox.isSelected());
}//GEN-LAST:event_expertSettingsBoxActionPerformed

    private void normMethodBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normMethodBoxActionPerformed
        dataModel.setAffyNormalizationMethod(normMethodBox.getModel().getSelectedItem().toString());
        //defaultSettings.setProperty("paramNormMethod", normMethodBox.getModel().getSelectedItem().toString());
}//GEN-LAST:event_normMethodBoxActionPerformed

    private void pValCorrMethodBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pValCorrMethodBoxActionPerformed
        dataModel.setPValCorrectionMethod(pValCorrMethodBox.getSelectedItem().toString());
        //defaultSettings.setProperty("paramPval", pValCorrMethodBox.getSelectedItem().toString());
}//GEN-LAST:event_pValCorrMethodBoxActionPerformed

    private void normHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normHelpButtonActionPerformed

        qcInfoTextArea.setText(NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.normalizationInfoText"));
        qcInfoTextArea.setCaretPosition(0);
}//GEN-LAST:event_normHelpButtonActionPerformed

    private void pValCorrHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pValCorrHelpButtonActionPerformed

        qcInfoTextArea.setText(NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.pValCorrInfoText"));
        qcInfoTextArea.setCaretPosition(0);
}//GEN-LAST:event_pValCorrHelpButtonActionPerformed

    private void pValCorrHelpButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pValCorrHelpButton1ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_pValCorrHelpButton1ActionPerformed

    private void pcaBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pcaBoxActionPerformed
        PCchooserBoxActionPerformed(null);
}//GEN-LAST:event_pcaBoxActionPerformed

    private void morePCAButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_morePCAButtonActionPerformed
        qcInfoTextArea.setText(NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.PCAInfoText"));
        qcInfoTextArea.setCaretPosition(0);
}//GEN-LAST:event_morePCAButtonActionPerformed

    private void nuseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuseBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_nuseBoxActionPerformed

    private void moreNUSEButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreNUSEButtonActionPerformed
        qcInfoTextArea.setText(NbBundle.getMessage(CelFileInfoPanel.class, "RobinMainGUI.NUSERLEInfoText"));
        qcInfoTextArea.setCaretPosition(0);
}//GEN-LAST:event_moreNUSEButtonActionPerformed

    private void qcIncludeAllBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qcIncludeAllBoxActionPerformed
        if (qcIncludeAllBox.isSelected()) {
            // set all selected
            boxPlotBox.setSelected(true);
            if (PLMBox.isEnabled()) PLMBox.setSelected(true);
            RNAdigBox.setSelected(true);
            histBox.setSelected(true);
            scatterBox.setSelected(true);
            pcaBox.setSelected(true);
            if (nuseBox.isEnabled()) nuseBox.setSelected(true);
        } else {
            // set all unselected
            boxPlotBox.setSelected(false);
            PLMBox.setSelected(false);
            RNAdigBox.setSelected(false);
            histBox.setSelected(false);
            scatterBox.setSelected(false);
            pcaBox.setSelected(false);
            nuseBox.setSelected(false);
        }
}//GEN-LAST:event_qcIncludeAllBoxActionPerformed

    private void showQCPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showQCPanelPreviousButtonActionPerformed
        mainGUI.setStatusText("Step 2 of 4");
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, QCPANEL);
        qualcheckPanelNextButton.setEnabled(true);
        qualcheckPanelPreviousButton.setEnabled(true);
}//GEN-LAST:event_showQCPanelPreviousButtonActionPerformed

    private void showQCPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showQCPanelNextButtonActionPerformed
        // go through the qcresults and remove any
        // excluded PLM files from the importfiles list

        //TODO Clone the model instead of using the same in 2 lists!!
        DefaultListModel celFileListModel = (DefaultListModel) importFileList.getModel();
        Component[] qcresults = resultPanel.getComponents();
        for (Component item : qcresults) {
            if (item instanceof QCResultListItem) {
                QCResultListItem qcItem = (QCResultListItem) item;
                System.out.println("item:"+qcItem.getMethod()+"\tisExcluded:"+qcItem.isExcluded());
                if ((qcItem.getMethod().equals("plm") && qcItem.isExcluded()) ||
                        (qcItem.getMethod().equals("maplot") && qcItem.isExcluded())    ) {

                    // remove the respective file from the list!
                    System.out.println("excluding file "+qcItem.getCelFile());
                    celFileListModel.removeElement(qcItem.getCelFile());
                }
            }
        }

        // now fill the celFileList with whats left
        if (((String)limmaORrpBox.getSelectedItem()).startsWith("Linear")) {
            celFileList.setModel(celFileListModel);
            initializeGroupListPanel();
            mainGUI.setStatusText("Step 4 of 4");
            CardLayout cl = (CardLayout) this.getLayout();
            cl.show(this, CONFPANEL);
        } else {
            // user chose RankProduct
            celFileListRP.setModel(celFileListModel);
            CardLayout cl = (CardLayout) this.getLayout();
            cl.show(this, RANKPRODPANEL);
        }
    }//GEN-LAST:event_showQCPanelNextButtonActionPerformed

    private void configGroupsPanelPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configGroupsPanelPreviousButtonActionPerformed

        mainGUI.setStatusText("Step 3 of 4");
        CardLayout cl = (CardLayout) this.getLayout();
        if (qcPerformed) {
            cl.show(this, QCSHOWPANEL);
        } else {
            cl.show(this, QCPANEL);
        }
}//GEN-LAST:event_configGroupsPanelPreviousButtonActionPerformed

    private void configGroupsPanelNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configGroupsPanelNextButtonActionPerformed

        // Any files left in the input files list?
        if (celFileList.getModel().getSize() > 0) {
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
        File sourcePath = new File(outputPathFile, "source");
        //File groupListFile = new File(sourcePath, defaultSettings.getProperty("expName") + "_groupList.xml" );
        File groupListFile = new File(sourcePath, dataModel.getExperimentName() + "_groupList.xml" );
        try {
            RobinUtilities.saveGroupList(groupListPanel, groupListFile, dataModel);
            dataModel.setGroupsFile(groupListFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        setupDesignerScene();
        mainExpertBoxActionPerformed(null);

        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, DESIGNPANEL);

        // set values to previously chosen ones to ensure
        // consistency
        mainPvalBox.setSelectedItem(pValCorrMethodBox.getSelectedItem());
        mainNormBox.setSelectedItem(normMethodBox.getSelectedItem());
}//GEN-LAST:event_configGroupsPanelNextButtonActionPerformed

    private void addGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupButtonActionPerformed
        CelFileGroupPanel newPanel = new CelFileGroupPanel("New Group",celFileList);
        groupListPanel.add(newPanel);
        //groupListPanel.doLayout();
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

    private void saveGroupsTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGroupsTableButtonActionPerformed
        // Any files left in the input files list?
        if (celFileList.getModel().getSize() > 0) {
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
}//GEN-LAST:event_saveGroupsTableButtonActionPerformed

    private void loadGroupsTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGroupsTableButtonActionPerformed
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
            JPanel newList =  RobinUtilities.populateGroupListFromFile(chosenPath, celFileList);

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
    }//GEN-LAST:event_loadGroupsTableButtonActionPerformed

    private void confAnalysisPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confAnalysisPreviousButtonActionPerformed
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, CONFPANEL);
}//GEN-LAST:event_confAnalysisPreviousButtonActionPerformed

    private void confAnalysisNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confAnalysisNextButtonActionPerformed
        startMainAnalysis();
    }//GEN-LAST:event_confAnalysisNextButtonActionPerformed

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

    private void resetDesignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDesignButtonActionPerformed
        setupDesignerScene();
}//GEN-LAST:event_resetDesignButtonActionPerformed

    private void mainExpertBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainExpertBoxActionPerformed
        mainExpertPanel.setVisible(mainExpertBox.isSelected());
}//GEN-LAST:event_mainExpertBoxActionPerformed

    private void confRPPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confRPPreviousButtonActionPerformed
        salvageRPfiles();
        if (isImporting) groupListPanelIsInitialzed = false;
        CardLayout cl = (CardLayout) this.getLayout();
        if (qcPerformed) {
            cl.show(this, QCSHOWPANEL);
        } else {
            cl.show(this, QCPANEL);
        }
}//GEN-LAST:event_confRPPreviousButtonActionPerformed

    private void confRPNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confRPNextButtonActionPerformed

        // save a groups file for later import
        File sourceDir = new File(dataModel.getOutputDirFile(), "source");
        File groupsFile = new File(sourceDir, dataModel.getExperimentName()+ "_groupList.xml");

        try {
            if (!groupsFile.exists())
                groupsFile.createNewFile();
            if (!groupsFile.canWrite()) {
                new SimpleErrorMessage(null, "Can't write to specified file:\n"+groupsFile.getCanonicalPath());
                return;
            }

            FileWriter writer = new FileWriter(groupsFile);

            Document doc =  DocumentHelper.createDocument();
            Element root = doc.addElement("group_definition");

            //group1
            Element group1Element = root.addElement("group");
            group1Element.addAttribute("name", group1NameField.getText());
            for (int i = 0; i <= group1Table.getRowCount()-1; i++) {
                Element fileElement = group1Element.addElement("file");
                String file = (String) group1Table.getModel().getValueAt(i, 0);
                String source = String.valueOf(group1Table.getModel().getValueAt(i, 1));
                fileElement.addAttribute("path", file);
                fileElement.addAttribute("source", source);
            }

            //group2
            Element group2Element = root.addElement("group");
            group2Element.addAttribute("name", group2NameField.getText());
            for (int i = 0; i <= group2Table.getRowCount()-1; i++) {
                Element fileElement = group2Element.addElement("file");
                String file = (String) group2Table.getModel().getValueAt(i, 0);
                String source = String.valueOf(group2Table.getModel().getValueAt(i, 1));
                fileElement.addAttribute("path", file);
                fileElement.addAttribute("source", source);
            }

            XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
            xmlWriter.write(doc);
            xmlWriter.flush();
            writer.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        dataModel.setGroupsFile(groupsFile);

        startRPMainAnalysis();
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

    private void group1NameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group1NameFieldKeyPressed
        checkGroupNameEntry(evt);
}//GEN-LAST:event_group1NameFieldKeyPressed

    private void addFileToGroup2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileToGroup2ButtonActionPerformed
        addFileToGroup1ButtonActionPerformed(evt);
}//GEN-LAST:event_addFileToGroup2ButtonActionPerformed

    private void removeFileFromGroup2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileFromGroup2ButtonActionPerformed
        removeFileFromGroup1ButtonActionPerformed(evt);
}//GEN-LAST:event_removeFileFromGroup2ButtonActionPerformed

    private void group2NameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_group2NameFieldKeyPressed
        checkGroupNameEntry(evt);
}//GEN-LAST:event_group2NameFieldKeyPressed

    private void importCDFboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCDFboxActionPerformed
        if ( !importCDFbox.isSelected() ) {
            this.dataModel.setCustomCDFimport(false);
            this.importCDFFileField.setText("");
        }
    }//GEN-LAST:event_importCDFboxActionPerformed

    private void importCDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCDFButtonActionPerformed
        JFileChooser CDFChooser = new JFileChooser(outputPathFile);
        CDFChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        CDFChooser.setMultiSelectionEnabled(false);

        File CDFFile;

        if (CDFChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            CDFFile = CDFChooser.getSelectedFile();
        } else {
            return;
        }

        this.dataModel.setChipType(CDFFile.getName().replaceFirst("(?i)\\.cdf$", ""));
        this.dataModel.setCustomCDFimport(true);
        this.dataModel.setCustomCDFFile(CDFFile);
        try {
            this.importCDFFileField.setText(CDFFile.getCanonicalPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }//GEN-LAST:event_importCDFButtonActionPerformed

    private void PCchooserBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PCchooserBoxActionPerformed
        String pcselection = (String) PCchooserBox.getSelectedItem();

        if (pcselection.equals("PC 1 & 2")) this.dataModel.setPCList("list( c(1,2) )");
        if (pcselection.equals("PC 1 to 4")) this.dataModel.setPCList("list( c(1,2), c(3,4) )");
        if (pcselection.equals("PC 1 to 6")) this.dataModel.setPCList("list( c(1,2), c(3,4), c(5,6) )");
    }//GEN-LAST:event_PCchooserBoxActionPerformed

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

    private void setQCcontrolsEnabled(boolean state) {
        boxPlotBox.setEnabled(state);
        PLMBox.setEnabled(state);
        RNAdigBox.setEnabled(state);
        histBox.setEnabled(state);
        pcaBox.setEnabled(state);
        nuseBox.setEnabled(state);
        scatterBox.setEnabled(state);
        qcIncludeAllBox.setEnabled(state);
        expertSettingsBox.setEnabled(state);
        normMethodBox.setEnabled(state);
        limmaORrpBox.setEnabled(state);
        pValCorrMethodBox.setEnabled(state);
    }

    private void initializeGroupListPanel() {
    if (groupListPanelIsInitialzed) return;
    if (isImporting) {
        try {
            JPanel newList =  RobinUtilities.populateGroupListFromFile(dataModel.getGroupsFile(), celFileList);

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
        BoxLayout gListLayout = new BoxLayout(groupListPanel, BoxLayout.Y_AXIS);
        groupListPanel.setLayout(gListLayout);
        groupListPanel.setBackground(Color.WHITE);

        CelFileGroupPanel p1 = new CelFileGroupPanel("Group1", celFileList);
        groupListPanel.add(p1);
        CelFileGroupPanel p2 = new CelFileGroupPanel("Group2", celFileList);
        groupListPanel.add(p2);
    }

    groupsScrollPane.add(groupListPanel);
    groupsScrollPane.setViewportView(groupListPanel);
    groupListPanelIsInitialzed = true;
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox LFC2Box;
    private javax.swing.JComboBox PCchooserBox;
    private javax.swing.JCheckBox PLMBox;
    private javax.swing.JCheckBox RNAdigBox;
    private javax.swing.JButton addFileToGroup1Button;
    private javax.swing.JButton addFileToGroup2Button;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JButton addImportButton;
    private javax.swing.JCheckBox boxPlotBox;
    private javax.swing.JButton celFileInfoButton;
    private javax.swing.JList celFileList;
    private javax.swing.JList celFileListRP;
    private javax.swing.JButton confAnalysisNextButton;
    private javax.swing.JButton confAnalysisPreviousButton;
    private javax.swing.JButton confRPNextButton;
    private javax.swing.JButton confRPPreviousButton;
    private javax.swing.JButton configGroupsPanelNextButton;
    private javax.swing.JButton configGroupsPanelPreviousButton;
    private javax.swing.JPanel configureAnalysisPanel;
    private javax.swing.JPanel configureGroupsPanel;
    private javax.swing.JPanel configureRankProdGroups;
    private javax.swing.JButton createMetagroupButton;
    private javax.swing.JButton deleteGroupButton;
    private javax.swing.JButton deleteMetagroupButton;
    private javax.swing.JScrollPane experimentDesignerScrollPane;
    private javax.swing.JCheckBox expertSettingsBox;
    private javax.swing.JPanel expertSettingsPanel;
    private javax.swing.JPanel filePanel;
    private javax.swing.JTextField group1NameField;
    private javax.swing.JPanel group1PanelRP;
    private javax.swing.JPanel group1PanelRP1;
    private javax.swing.JTable group1Table;
    private javax.swing.JTextField group2NameField;
    private javax.swing.JTable group2Table;
    private javax.swing.JPanel groupsPanelRP;
    private javax.swing.JScrollPane groupsScrollPane;
    private javax.swing.JCheckBox guruBox;
    private javax.swing.JCheckBox histBox;
    private javax.swing.JButton importCDFButton;
    private javax.swing.JTextField importCDFFileField;
    private javax.swing.JCheckBox importCDFbox;
    private javax.swing.JList importFileList;
    private javax.swing.JPanel importFilesPanel;
    private javax.swing.JButton importPanelNextButton;
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
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox limmaORrpBox;
    private javax.swing.JButton loadGroupsTableButton;
    private javax.swing.JCheckBox mainExpertBox;
    private javax.swing.JPanel mainExpertPanel;
    private javax.swing.JComboBox mainNormBox;
    private javax.swing.JComboBox mainPvalBox;
    private javax.swing.JComboBox mainTestingStrategyBox;
    private javax.swing.JCheckBox mainWriteRawBox;
    private javax.swing.JButton moreBoxPlotButton;
    private javax.swing.JButton moreHistButton;
    private javax.swing.JButton moreNUSEButton;
    private javax.swing.JButton morePCAButton;
    private javax.swing.JButton morePLMButton;
    private javax.swing.JButton moreRNAdigButton;
    private javax.swing.JButton moreScatterButton;
    private javax.swing.JButton normHelpButton;
    private javax.swing.JComboBox normMethodBox;
    private javax.swing.JCheckBox nuseBox;
    private javax.swing.JButton pValCorrHelpButton;
    private javax.swing.JButton pValCorrHelpButton1;
    private javax.swing.JComboBox pValCorrMethodBox;
    private javax.swing.JComboBox pValCutoffBox;
    private javax.swing.JCheckBox pcaBox;
    private javax.swing.JPanel qcChoicePanel;
    private javax.swing.JPanel qcControlPanel;
    private javax.swing.JCheckBox qcIncludeAllBox;
    private javax.swing.JTextArea qcInfoTextArea;
    private javax.swing.JLabel qcLabel;
    private javax.swing.JScrollPane qcResultScrollPane;
    private javax.swing.JButton qualcheckPanelNextButton;
    private javax.swing.JButton qualcheckPanelPreviousButton;
    private javax.swing.JPanel qualityCheckPanel;
    private javax.swing.JButton removeFileFromGroup1Button;
    private javax.swing.JButton removeFileFromGroup2Button;
    private javax.swing.JButton removeImportButton;
    private javax.swing.JButton resetDesignButton;
    private javax.swing.JButton saveGroupsTableButton;
    private javax.swing.JCheckBox scatterBox;
    private javax.swing.JButton showQCPanelNextButton;
    private javax.swing.JButton showQCPanelPreviousButton;
    private javax.swing.JPanel showQCResultsPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    

    @Override
    protected void startMainAnalysis() {
        AnalysisDesignModel designModel =  analysisDesignerPanel.getDesignModel();
        dataModel.setAnalysisType(AnalysisStrategy.LIMMA);

        // see if the model has some apparent design errors
        try {
            designModel.validateModel();
        } catch (NoConnectionsDesignException ex) {
            SimpleLogger.getLogger(true).logException(
                    new Exception("You have to draw at least one connection between two groups\n"
                                    + "to tell me what comparisons i shall make.", ex));
            return;
        } catch (RedundantConnectionsDesignException ex) {
            SimpleLogger.getLogger(true).logException(ex);
            return;
        }
        
        dataModel.setAffyNormalizationMethod(mainNormBox.getSelectedItem().toString());       
        dataModel.setPValCorrectionMethod(mainPvalBox.getSelectedItem().toString());        
        dataModel.setStatStrategy(mainTestingStrategyBox.getSelectedItem().toString());        
        dataModel.setMinLogFoldChangeOf2(LFC2Box.isSelected());        
        dataModel.setWriteRawExprs(mainWriteRawBox.isSelected());   
        dataModel.setPValCutoffValue(pValCutoffBox.getSelectedItem().toString());

        RScriptGenerator generator = new RScriptGenerator(
                mainGUI.getDefaultSettings().getProperty("PathToR"),
                mainGUI.getDefaultSettings());

        String mainScript = generator.generateRScript(designModel, dataModel);

        // are you guru?
        if (guruBox.isSelected()) {
            //TODO show script editor dialog
            RScriptEditor scriptEditor = new RScriptEditor(mainGUI, mainScript);
            if (scriptEditor.getUserChoice() == RScriptEditor.USER_ACCEPT) {
                mainScript = scriptEditor.getModifiedScript();
            } else {
                scriptEditor.dispose();
                System.out.println("Guru bail-out");
                return;
            }
        }

        File sourceDir = new File(outputPathFile, "source");
        File mainScriptPath = new File(sourceDir, dataModel.getExperimentName() + "_main_analysis.R");

        try {
            // explicitly using UTF-8 solves part of the problem
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(mainScriptPath), Charset.forName("UTF-8")));

            writer.write(mainScript);
            writer.close();
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(ex);
            return;
        }

        final RTask mainCall = new RTask(mainGUI.getDefaultSettings().getProperty("PathToR"),
                mainGUI.getDefaultSettings().getProperty("CommandToRunR"),
                mainGUI.getDefaultSettings().getProperty("ArgsToR"), mainScriptPath);

        mainCall.setMethod("MainScript");
        enableDesignControls(false);
        analysisDesignerPanel.writeDesignViewToFile(dataModel.getOutputDir() + sFileSep,
                dataModel.getExperimentName(),
                "png");
        mainCall.start();

        ActionListener mainCallListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!mainCall.isAlive()) {
                    mainGUI.setStatusText("idle");
                    mainGUI.stopBusyAnimation();

                    mainGUI.setProgressLabelText("Finished analysis");
                    qcProcTimer.stop();

                    // check whether the R run finished successfully
                    if (mainCall.getExitValue() != 0) {

                        RTaskErrorMessageEvaluator eval = new RTaskErrorMessageEvaluator(mainCall, AffyWorkflowPanel.this);
                        eval.evaluateError();
                        return;
                    }

                    // get warnings from the main script, if any
                    if (mainCall.hasWarning()) {
                        //TODO implement an automatic warning extractor that takes type, severity and message from the
                        // warning text itself and adds it to the warnings handler. Maybe the handler
                        // could even be passed to the RTask so that this will by done by each task
                        // directly after it has finished. Only requires a bit more parsing work in the
                        // RTask.
                        Warning mainWarning = new Warning("Significance test", mainCall.getWarningMessage(), Warning.SEVERITY_CRITICAL);
                        dataModel.addWarning(mainWarning);
                    }

                    // were there warnings? show them now!
                    if (dataModel.getWarningsHandler().hasWarnings()) {
                        dataModel.getWarningsHandler().writeWarningsToFile(dataModel.getOutputDir() +
                                sFileSep +
                                "warnings.txt");

                        // user chose not to ignore the warnings
                        if (!dataModel.getWarningsHandler().showWarnings(mainGUI)) {
                            enableDesignControls(true);
                            System.out.println("User cancel after warning messages");
                            return;
                        }
                    }

                    // ask whether the user wants to annotate the results
                    //File libPath = new File(mainGUI.getInstallPath(), "lib");
                    File mappingsPath = new File(mainGUI.getResourcePath(), "mappings");
                    File resultFilePath = new File(
                            dataModel.getOutputDir(),
                            dataModel.getExperimentName().concat("_results.txt"));

                    ResultAnnotationDialog annoDialog = new ResultAnnotationDialog(mainGUI, true, mappingsPath, resultFilePath);
                    annoDialog.setVisible(true);
                    try {
                        // save the summary
                        RobinAnalysisSummaryGenerator.writeAnalysisSummary(dataModel);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }

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
                            new Object[] {"Restart", "Modify", "Exit", "View Data in MapMan"},
                            "Modify"
                            );


                    // Restart
                    if (choice == 0) {
                        flushAndResetVariables();
                        RobinMainGUI newMain = new RobinMainGUI();
                        mainGUI.setVisible(false);
                        newMain.setVisible(true);
                        mainGUI.dispose();
                        
                    } else if (choice == 1) {
                        enableDesignControls(true);
                        return;

                        // Exit
                    } else if (choice == 2) {
                        System.exit(0);

                    } else if (choice == 3) {
                        File resultFile = new File(
                                dataModel.getOutputDir(),
                                dataModel.getExperimentName().concat("_results.txt") );

                        boolean success = MapCommunicator.postExperimentToMapMan(resultFile, mainGUI.getDefaultSettings());

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
                                flushAndResetVariables();
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
            }
        };

        mainGUI.startBusyAnimation(sFileSep);
        mainGUI.setStatusText("Step 4 orf 4");
        mainGUI.setProgressLabelText("running...");
        qcProcTimer = new Timer(100, mainCallListener);
        qcProcTimer.start();
    }

    private RTask generateBatchQCScript(List<String> methods) {

        String sParamInputFiles = "\"";
        ArrayList<String> sampleNames = new ArrayList<String>();

        int numberOfInputFiles = importFileList.getModel().getSize();
        for (int i = 0; i < numberOfInputFiles; i++) {
            sParamInputFiles = sParamInputFiles + importFileList.getModel().getElementAt(i) + "\",\n\"";
            sampleNames.add(Utilities.extractFileNamePathComponent((String) importFileList.getModel().getElementAt(i)));
        }
       
        //FIXME Do we need this shit?
        System.out.println("sParamInputFiles:"+sParamInputFiles);
        sParamInputFiles = sParamInputFiles.substring(0,
                sParamInputFiles.length() - 3);
        System.out.println("sParamInputFiles after:"+sParamInputFiles);        
        String fileNames = "\""+StringUtils.join(sampleNames, "\",\n\"")+"\"";

        System.out.println("fileNames = "+fileNames);

        String sParamTempDir = System.getProperty("java.io.tmpdir");
        String sParamTempIdentifier = null;

        // get a real temp file name
        try {
            File tmpFile = File.createTempFile("tmp", "robin_");
            sParamTempIdentifier = tmpFile.getName();
            tmpFile.delete();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        String sOutputFile = sParamTempIdentifier;
        File sourceDir = new File(outputPathFile, "source");
        File sScriptFileName = new File(sourceDir, "qualityChecks.R");
        String sParamPictureFile = null;
        try {
            //sParamOutputFile = sParamTempDir + System.getProperty("file.separator") + sOutputFile;
            File qualPlotDir = new File(outputPathFile, "qualitychecks");
            sParamPictureFile = new File(qualPlotDir, sOutputFile).getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //TODO update model - maybe we move this to a method?
        dataModel.setAffyNormalizationMethod((String) normMethodBox.getSelectedItem());
        
        String jpegDevParams = "width=6, height=6, units=\"in\", res=" + R_JPEGRESOLUTION;
   
        RScriptGenerator gen = new RScriptGenerator();
        String script = gen.generateRScript(methods, 
                jpegDevParams,
                sParamPictureFile,
                fileNames,
                sParamInputFiles,
                dataModel );

        try {
            FileWriter out = new FileWriter(sScriptFileName);
            out.write(script);
            out.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }


        final RTask rc = new RTask( mainGUI.getDefaultSettings().getProperty("PathToR"),
                                    mainGUI.getDefaultSettings().getProperty("CommandToRunR"),
                                    mainGUI.getDefaultSettings().getProperty("ArgsToR"), sScriptFileName);

        // prepare the R call an return it
        rc.setMethod("batchQCscript");
        rc.setInputFiles(sParamInputFiles);
        rc.setOutputFile(sParamPictureFile);
        return rc;
    }

    private void showBatchQCResults(RTask qcBatchTask, ArrayList<String> methods) {

        //DEBUG
        if (qcBatchTask.hasWarning()) {

            for (Warning w : qcBatchTask.getWarnings()) {
                dataModel.addWarning(w);
            }

        }
        //DEBUG

        int nrInputFiles = importFileList.getModel().getSize();

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);

        String baseOutFile = qcBatchTask.getOutputFile();
        System.out.println("BASE OUT:"+ baseOutFile);
        System.out.println("methods:"+methods.toString());

        for (String method : methods) {
            // First handle the single-image type QCs
            if ( (method.equals("hist")) ||
                    (method.equals("rna")) ||
                    (method.equals("boxplot")) ||
                    ( (method.equals("pca")) && (nrInputFiles > 1) ) ||
                    ( (method.equals("hclust")) && (nrInputFiles > 1) ) ||
                    ( (method.equals("rle")) && (nrInputFiles > 1) ) ||
                    ( (method.equals("nuse")) && (nrInputFiles > 1) ) ) {

                QCResultListItem item = new QCResultListItem( qcBatchTask,
                        baseOutFile+method+".png",
                        method,
                        dataModel.getWarningsHandler(),
                        null);

                item.setName("#"+method);
                MouseListener itemClickListener = new QCItemMouseListener();
                item.addMouseListener(itemClickListener);
                item.setDescription("<html>"+method.toUpperCase()+"-Plot<br>"+
                                    "<small> of "+nrInputFiles+" Affymetrix data files</small></html>");
                item.setExcludable(false);
                item.setMethod(method);
                resultPanel.add(item);
            // now handle PLM
            } else if ((method.equals("plm")) || (method.equals("maplot")) ) {
                for (int j = 1; j <= nrInputFiles; j++) {
                    QCResultListItem item = new QCResultListItem(   qcBatchTask,
                            baseOutFile+method+j+".png",
                            method,
                            dataModel.getWarningsHandler(),
                            Utilities.truncateLongString(importFileList.getModel().getElementAt(j-1).toString(), 30)
                            );

                    item.setName("#"+method+"_"+j);
                    MouseListener itemClickListener = new QCItemMouseListener();
                    item.addMouseListener(itemClickListener);
                    item.setDescription("<html>"+method.toUpperCase()+"-Plot<br>"+
                                    "<small> of file: "+importFileList.getModel().getElementAt(j-1).toString()+"</small></html>");
                    //item.setCelFile(importFileList.getModel().getElementAt(j-1).toString());
                    item.setExcludable(true);
                    item.setMethod(method);
                    resultPanel.add(item);
                }
            // and the scatter plots
            } else if (method.equals("scat")) {
                for (int a = 1; a <= nrInputFiles; a++) {
                    for (int b = 1; b <= nrInputFiles; b++) {
                        if (b > a) {
                            QCResultListItem item = new QCResultListItem(qcBatchTask,
                                    baseOutFile+method+Integer.parseInt(""+a+b)+".png",
                                    method,
                                    dataModel.getWarningsHandler(),
                                    null);

                            item.setName("#ding"+a+b);
                            String file1 = Utilities.truncateLongString(importFileList.getModel().getElementAt(a-1).toString(), 30);
                            String file2 = Utilities.truncateLongString(importFileList.getModel().getElementAt(b-1).toString(), 30);
                            item.setDescription("<html>Scatter plot<br>"+
                                    "<small> of file: "+file1+" vs. <br>"+
                                    file2+"</small></html>");
                            MouseListener itemClickListener = new QCItemMouseListener();
                            item.addMouseListener(itemClickListener);
                            item.setExcludable(false);
                            item.setMethod(method);
                            resultPanel.add(item);
                        }
                    }
                }
            }


            //TODO do i still need to do it like that, using BoxLayout?
            resultPanel.setMaximumSize(new Dimension(700,10000));
            qcResultScrollPane.add(resultPanel);
            qcResultScrollPane.setViewportView(resultPanel);
        }
    }

    private void setupDesignerScene() {

        ArrayList<AbstractGroupModel> groups = new ArrayList<AbstractGroupModel>();

        int c = 1;
        for (Component comp : groupListPanel.getComponents()) {
            if (comp instanceof CelFileGroupPanel) {
                groups.add(((CelFileGroupPanel) comp).getGroupModel());
                c++;
            }
        }

        analysisDesignerPanel = new AnalysisDesigner(groups);
        experimentDesignerScrollPane.setViewportView(analysisDesignerPanel);

    }

    private void enableDesignControls(boolean state) {
        resetDesignButton.setEnabled(state);
        createMetagroupButton.setEnabled(state);
        deleteMetagroupButton.setEnabled(state);
        confAnalysisNextButton.setEnabled(state);
        confAnalysisPreviousButton.setEnabled(state);
        //FIXME let's see if this can really be omitted...
        //analysisDesignerPanel.setEnabled(state);
        mainExpertBox.setEnabled(state);
        mainNormBox.setEnabled(state);
        mainPvalBox.setEnabled(state);
        mainTestingStrategyBox.setEnabled(state);
        guruBox.setEnabled(state);
        mainWriteRawBox.setEnabled(state);
        LFC2Box.setEnabled(state);
    }

    public void flushAndResetVariables() {
        //DEBUG
        System.out.println("Looking into Glaurung's eyes...");
        dataModel.setWarnings(new WarningsHandler());
        qcPerformed = false;
        groupListPanelIsInitialzed = false;
        enableDesignControls(true);
        setQCcontrolsEnabled(true);
    }

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

    public void finishGracefully() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void initGUIFromModel() {
        normMethodBox.setSelectedItem(dataModel.getAffyNormalizationMethod());
        pValCorrMethodBox.setSelectedItem(dataModel.getPValCorrectionMethod());
        pValCutoffBox.setSelectedItem(dataModel.getPValCutoffValue());
        mainNormBox.setSelectedItem(dataModel.getAffyNormalizationMethod());
        mainPvalBox.setSelectedItem(dataModel.getPValCorrectionMethod());
        if (dataModel.getAnalysisType() != AnalysisStrategy.RANKPROD)
            mainTestingStrategyBox.setSelectedItem(dataModel.getStatStrategy());
        LFC2Box.setSelected(dataModel.isMinLogFoldChangeOf2());

        if (dataModel.getAnalysisType() == AnalysisStrategy.LIMMA) {
            limmaORrpBox.setSelectedItem("Linear models (package limma)");
        } else {
            limmaORrpBox.setSelectedItem("Rank product (package RankProd)");
        }

    }

    @Override
    public void setImportMode(File importProject, DefaultListModel listModel) {
        //DEBUG
        System.out.println("affy import mode");

        importFileList.setModel(listModel);
        importPanelNextButton.setEnabled(true);
        isImporting = true;
        try {
            this.dataModel.load(importProject);
            this.dataModel.setDataType("affy");
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(new Exception("Could not load project data.", ex));
            System.exit(1);
        }

        initGUIFromModel();
    }

    @Override
    protected void resetGUI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void startRPMainAnalysis() {
        // disable controls
        confRPNextButton.setEnabled(false);
        confRPPreviousButton.setEnabled(false);
        dataModel.setAnalysisType(AnalysisStrategy.RANKPROD);

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



        // start the RankProd main script
        mainGUI.startBusyAnimation("running RankProd analysis...");

        String outputPath = null;
        try {
            outputPath = dataModel.getOutputDir();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        RScriptGenerator editor = new RScriptGenerator();
        String RPscript = editor.generateRankProdMainScript(dataModel, files, classes, origin, outputPath);

        // write file to disk
        File sourceDir = new File(outputPathFile, "source");
        File scriptPathRP = new File(sourceDir, dataModel.getExperimentName() + "_rankprod_main_analysis.R");

        try {
            // explicitly using UTF-8 solves part of the problem
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scriptPathRP), Charset.forName("UTF-8")));

            writer.write(RPscript);
            writer.close();
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(ex);
            return;
        }

        final RTask rankProdTask = new RTask(mainGUI.getDefaultSettings().getProperty("PathToR"),
                mainGUI.getDefaultSettings().getProperty("CommandToRunR"),
                mainGUI.getDefaultSettings().getProperty("ArgsToR"), scriptPathRP);

        final Timer RPTimer =  new Timer(100, null);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!rankProdTask.isAlive()) {
                    RPTimer.stop();
                    mainGUI.setProgressLabelText("idle");
                    mainGUI.stopBusyAnimation();

                    System.out.println("RP EXIT VALUE="+rankProdTask.getExitValue());

                    if (rankProdTask.getExitValue() != 0) {
                        new VerboseWarningDialog(mainGUI, "R error", "The R task finished with an error:", rankProdTask.getOutputMessage(), dataModel);
                        salvageRPfiles();
                        // enable controls
                        confRPNextButton.setEnabled(true);
                        confRPPreviousButton.setEnabled(true);
                        return;
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
                            "<br>Click \"Restart\" to import new data<br>"+
                            "and re-run the analysis.<br>Clicking \"Exit\" will close Robin",
                            "Finished",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            UIManager.getIcon("OptionPane.questionIcon"),
                            new Object[] {"Restart", "Modify", "Exit", "View data in MapMan"},
                            "Restart"
                            );

                    //System.out.println("choice is: "+choice);
                    // Restart
                    if (choice == 0) {
                        // TODO flush everything and move to card 1
                        flushAndResetVariables();
                        RobinMainGUI newMain = new RobinMainGUI();
                        mainGUI.setVisible(false);
                        mainGUI.dispose();
                        newMain.setVisible(true);

                        // Modify
                    } else if (choice == 1) {
                        salvageRPfiles();
                        // enable controls
                        confRPNextButton.setEnabled(true);
                        confRPPreviousButton.setEnabled(true);
                        return;
                        // Exit
                    } else if (choice == 2) {
                        System.exit(0);
                    } else if (choice == 3) {
                        File resultFile = new File(
                                dataModel.getOutputDir(),
                                dataModel.getExperimentName().concat("_results.txt") );

                        boolean success = MapCommunicator.postExperimentToMapMan(resultFile, mainGUI.getDefaultSettings());

                        if (success) {
                            int whatNext = JOptionPane.showOptionDialog(
                                    mainGUI,
                                    "<html><h2>Data successfully transferred to MapMan</h2><br>" +
                                    "<br>Click \"Restart\" if you want to analyse anohter<br>"+
                                    "dataset. Clicking \"Exit\" will close Robin",
                                    "Data transferred",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    UIManager.getIcon("OptionPane.questionIcon"),
                                    new Object[] {"Restart", "Exit"},
                                    "Restart"
                                    );

                            if (whatNext == 0) {
                                // TODO flush everything and move to card 1
                                flushAndResetVariables();
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

            }
        };

        // start the task and a timer polling the task every 100ms
        RPTimer.addActionListener(al);
        rankProdTask.start();
        RPTimer.start();
    }

    @Override
    public void RTaskFinished() {
        this.finishGracefully();
    }

}
