/*
 * TCMainPanel.java
 *
 * Created on 9. Oktober 2008, 13:58
 */
package de.mpimp.golm.robin.GUI.twocolor;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.R.RTaskController;
import de.mpimp.golm.robin.GUI.ComboBoxCellEditor;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.GUI.RobinWorkflow;
import de.mpimp.golm.robin.GUI.StrictRTermFilterDocument;
import de.mpimp.golm.robin.GUI.affy.QCResultListItem;
import de.mpimp.golm.robin.IPC.MapCommunicator;
import de.mpimp.golm.robin.R.RScriptEditor;
import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.annotation.ResultAnnotationDialog;
import de.mpimp.golm.robin.data.ArrayDataModel.AnalysisStrategy;
import de.mpimp.golm.robin.data.ArrayDataModel.InputDataType;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import de.mpimp.golm.robin.designer.GUI.AnalysisDesigner;
import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.robin.designer.model.GroupModel;
import de.mpimp.golm.robin.designer.model.NoConnectionsDesignException;
import de.mpimp.golm.robin.designer.model.RedundantConnectionsDesignException;
import de.mpimp.golm.robin.misc.RobinAnalysisSummaryGenerator;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.warnings.Warning;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableColumn;
import org.openide.util.Exceptions;

/**
 *
 * @author  marc
 */
public class TCWorkflowPanel extends RobinWorkflow {

    private static final String IMPORT = "card2";
    private static final String TARGETSDESIGNER = "card3";
    private static final String QCCHOICE = "card4";
    private static final String QCRESULTS = "card5";
    private static final String EXPERIMENTDESIGNER = "card6";
    private String sFileSep = System.getProperty("file.separator");
    private ArrayList<String> conditions = new ArrayList<String>();
    private TCArrayDataModel dataModel;
    private ArrayList<String> selectedQC;
    private TCDelegate delegate;
    private JPanel resultPanel;
    private AnalysisDesigner analysisDesignerPanel;
    // flags
    private boolean qcPerformed;
    private File importTargetsFile;

    /** Creates new form TCMainPanel
     * @param main
     * @param projectPath
     */
    public TCWorkflowPanel(RobinMainGUI main, File projectPath) {
        super(main, projectPath);
        initComponents();

        this.dataModel = new TCArrayDataModel();
        this.delegate = new TCDelegate();
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

    @Override
    public RobinMainGUI getMainGUI() {
        return mainGUI;
    }

    public void setImportMode(File importProject, DefaultListModel importFilesModel) {
        this.isImporting = true;
        this.importFileList.setModel(importFilesModel);
        try {
            this.dataModel.load(importProject);
            this.dataModel.setDataType("generic");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            new SimpleErrorMessage(this, "Could not load project data");
            System.exit(1);
        }

        // load the targets table from the model
        //loadTargetsTableFromFile(this.dataModel.getTargetsFile());
        initGUIFromModel();

        // switch to the targets table designer screen
        TCImportNextButton.setEnabled(true);
//        newConditionField.requestFocus();
//        CardLayout cl = (CardLayout) this.getLayout();
//        cl.show(this, TARGETSDESIGNER);
    }

    protected void initGUIFromModel() {

        refSampleBox.setEnabled(true);
        refSampleLabel.setEnabled(true);
        for (String cond : dataModel.getSamples()) {
            refSampleBox.addItem(cond);
            ((DefaultListModel) conditionsList.getModel()).addElement(cond);
        }
        refSampleBox.setSelectedItem(dataModel.getReferenceSample());

        normBetweenBox.setSelected(dataModel.isDoNormBetween());
        normWithinBox.setSelected(dataModel.isDoNormWithin());

        withinMethodBox.setSelectedItem(dataModel.getNormWithinArrays());
        withinBGcorrBox.setSelectedItem(dataModel.getNormWithinArraysBGcorr());
        betweenMethodBox.setSelectedItem(dataModel.getNormBetweenArrays());


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
                "<html><h2>Finished successfully!</h2><br>Results were written to:<br>"
                + mainGUI.defaultSettings.getProperty("displayOutputPath")
                + "<br>Click \"Modify\" if you want to modify the design<br>"
                + "and re-run the analysis. Be sure to specify<br>"
                + "a different name for the output folder unless<br>"
                + "you want to overwrite the results of the previous<br>"
                + "analysis run! Clicking \"Exit\" will close Robin<br>"
                + "If you want to view the results in MapMan please start<br>"
                + "MapMan now. Robin will try to automatically transfer the<br>"
                + "analysed data set to MapMan.",
                "Finished",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[]{"Restart", "Modify", "Exit", "View Data in MapMan"}, //"Restart",
                "Exit");


        // Restart
        if (choice == 0) {
            // restarting
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
                    dataModel.getExperimentName().concat("_results.noduplicates.txt"));

            if (checkRedundant.exists()) {
                resultFile = checkRedundant;
            } else {
                resultFile = new File(
                        dataModel.getOutputDir(),
                        dataModel.getExperimentName().concat("_results.txt"));
            }

            boolean success = MapCommunicator.postExperimentToMapMan(resultFile, dataModel.getAsProperties());

            if (success) {
                int whatNext = JOptionPane.showOptionDialog(
                        mainGUI,
                        "<html><h2>Data successfully transferred to MapMan</h2><br>"
                        + "<br>Click \"Restart\" if you want to analyse another<br>"
                        + "dataset. Clicking \"Exit\" will close Robin.</html>",
                        "Data transferred",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        UIManager.getIcon("OptionPane.questionIcon"),
                        new Object[]{"Restart", "Exit"},
                        "Restart");

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
                        "<html><h2>MapMan data import failure</h2><br>"
                        + "The data could be transferred to MapMan, but the import<br>"
                        + "failed. If the problem persists please seek advice in the<br>"
                        + "MapMan forum (http://mapman.gabipd.org/web/guest/forum)<br>"
                        + "<br>Click \"Restart\" if you want to analyse another<br>"
                        + "dataset. Clicking \"Exit\" will close Robin.</html>",
                        "Data transfer problem",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        UIManager.getIcon("OptionPane.questionIcon"),
                        new Object[]{"Restart", "Exit"},
                        "Restart");

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
        conditionsList.setModel(new DefaultListModel());
        refSampleBox.setModel(new DefaultComboBoxModel());
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

        TCFileChooser = new javax.swing.JFileChooser();
        inputTypeButtonGroup = new javax.swing.ButtonGroup();
        TCImportPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        TCImportNextButton = new javax.swing.JButton();
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
        jPanel9 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        TCTargetsDesignerPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        TCTargetsPreviousButton = new javax.swing.JButton();
        TCTargetsNextButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        conditionsList = new javax.swing.JList();
        addConditionButton = new javax.swing.JButton();
        removeConditionButton = new javax.swing.JButton();
        newConditionField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        targetsTable = new javax.swing.JTable();
        loadTargetsFileButton = new javax.swing.JButton();
        saveTargetsFileButton = new javax.swing.JButton();
        refSampleLabel = new javax.swing.JLabel();
        refSampleBox = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        TCQCChooserPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        TCQCChooserPreviousButton = new javax.swing.JButton();
        TCQCChooserNextButton = new javax.swing.JButton();
        TCqualchecklabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        thumbnailLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        QCbackGroundBox = new javax.swing.JCheckBox();
        bgImageInfoButton = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        thumbnailLabel3 = new javax.swing.JLabel();
        descriptionLabel3 = new javax.swing.JLabel();
        QCdensityBox = new javax.swing.JCheckBox();
        distInfoButton = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        thumbnailLabel4 = new javax.swing.JLabel();
        descriptionLabel4 = new javax.swing.JLabel();
        QCMAplotBox = new javax.swing.JCheckBox();
        maInfoButton = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        thumbnailLabel5 = new javax.swing.JLabel();
        descriptionLabel5 = new javax.swing.JLabel();
        QCmvalueBox = new javax.swing.JCheckBox();
        mvalInfoButton = new javax.swing.JButton();
        expertPanel = new javax.swing.JPanel();
        normWithinBox = new javax.swing.JCheckBox();
        normBetweenBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        withinMethodBox = new javax.swing.JComboBox();
        betweenMethodBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        withinBGcorrBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        mainTestingStrategyBox = new javax.swing.JComboBox();
        mainWriteRawBox = new javax.swing.JCheckBox();
        guruBox = new javax.swing.JCheckBox();
        LFC2Box = new javax.swing.JCheckBox();
        jLabel29 = new javax.swing.JLabel();
        pValCutoffBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        mainPvalBox = new javax.swing.JComboBox();
        selectAllBox = new javax.swing.JCheckBox();
        showExpertBox = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        qcInfoTextArea = new javax.swing.JTextArea();
        TCQCResultsPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        TCQCResultsPreviousButton = new javax.swing.JButton();
        TCQCResultsNextButton = new javax.swing.JButton();
        TCqualchecklabel1 = new javax.swing.JLabel();
        TCQCResultScrollPane = new javax.swing.JScrollPane();
        TCExperimentDesignerPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        TCExperimentDesignerPreviousButton = new javax.swing.JButton();
        TCExperimentDesignerNextButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        designerScrollPane = new javax.swing.JScrollPane();
        jButton8 = new javax.swing.JButton();
        createMetagroupButton = new javax.swing.JButton();
        deleteMetagroupButton = new javax.swing.JButton();

        TCFileChooser.setMultiSelectionEnabled(true);

        setLayout(new java.awt.CardLayout());

        TCImportPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new java.awt.GridBagLayout());

        TCImportNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCImportNextButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCImportNextButton.text")); // NOI18N
        TCImportNextButton.setEnabled(false);
        TCImportNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCImportNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel4.add(TCImportNextButton, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel5.text")); // NOI18N

        jScrollPane3.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Imported files"));

        importFileList.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane3.setViewportView(importFileList);
        DefaultListModel importListModel = new DefaultListModel();
        ListDataListener l = new ListDataListener() {

            public void intervalAdded(ListDataEvent e) {
                TCImportNextButton.setEnabled(true);
            }

            public void intervalRemoved(ListDataEvent e) {
                if (importFileList.getModel().getSize() == 0) {
                    TCImportNextButton.setEnabled(false);
                } else {
                    TCImportNextButton.setEnabled(true);
                }
            }
            public void contentsChanged(ListDataEvent e) {
            }
        };
        importListModel.addListDataListener(l);
        importFileList.setModel(importListModel);

        addImportButton.setBackground(new java.awt.Color(255, 255, 255));
        addImportButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.addImportButton.text")); // NOI18N
        addImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addImportButton.setContentAreaFilled(false);
        addImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImportButtonActionPerformed(evt);
            }
        });

        removeImportButton.setBackground(new java.awt.Color(255, 255, 255));
        removeImportButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.removeImportButton.text")); // NOI18N
        removeImportButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeImportButton.setContentAreaFilled(false);
        removeImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImportButtonActionPerformed(evt);
            }
        });

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jPanel9.border.title"))); // NOI18N

        jRadioButton2.setBackground(new java.awt.Color(255, 255, 255));
        inputTypeButtonGroup.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jRadioButton2.text")); // NOI18N
        jRadioButton2.setActionCommand(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jRadioButton2.actionCommand")); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jRadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        inputTypeButtonGroup.add(jRadioButton1);
        jRadioButton1.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jRadioButton1.text")); // NOI18N
        jRadioButton1.setActionCommand(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jRadioButton1.actionCommand")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRadioButton1)
                    .add(jRadioButton2))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton2)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout TCImportPanelLayout = new org.jdesktop.layout.GroupLayout(TCImportPanel);
        TCImportPanel.setLayout(TCImportPanelLayout);
        TCImportPanelLayout.setHorizontalGroup(
            TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .add(TCImportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 265, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, TCImportPanelLayout.createSequentialGroup()
                        .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)))
        );
        TCImportPanelLayout.setVerticalGroup(
            TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCImportPanelLayout.createSequentialGroup()
                .add(TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCImportPanelLayout.createSequentialGroup()
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                        .add(7, 7, 7)
                        .add(TCImportPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(removeImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(addImportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(TCImportPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(TCImportPanel, "card2");

        TCTargetsDesignerPanel.setBackground(new java.awt.Color(255, 255, 255));
        TCTargetsDesignerPanel.setEnabled(false);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.GridBagLayout());

        TCTargetsPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCTargetsPreviousButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCTargetsPreviousButton.text")); // NOI18N
        TCTargetsPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCTargetsPreviousButtonActionPerformed(evt);
            }
        });
        jPanel5.add(TCTargetsPreviousButton, new java.awt.GridBagConstraints());

        TCTargetsNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCTargetsNextButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCTargetsNextButton.text")); // NOI18N
        TCTargetsNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCTargetsNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel5.add(TCTargetsNextButton, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel1.text")); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jPanel1.border.title"))); // NOI18N

        conditionsList.setModel(new DefaultListModel());
        conditionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        conditionsList.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.conditionsList.toolTipText")); // NOI18N
        jScrollPane1.setViewportView(conditionsList);

        addConditionButton.setBackground(new java.awt.Color(255, 255, 255));
        addConditionButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.addConditionButton.text")); // NOI18N
        addConditionButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addConditionButtonActionPerformed(evt);
            }
        });

        removeConditionButton.setBackground(new java.awt.Color(255, 255, 255));
        removeConditionButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.removeConditionButton.text")); // NOI18N
        removeConditionButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.removeConditionButton.toolTipText")); // NOI18N
        removeConditionButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        removeConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeConditionButtonActionPerformed(evt);
            }
        });

        newConditionField.setDocument(new StrictRTermFilterDocument());
        newConditionField.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.newConditionField.text")); // NOI18N
        newConditionField.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.newConditionField.toolTipText")); // NOI18N
        newConditionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newConditionFieldActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(newConditionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .add(3, 3, 3)
                .add(addConditionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(removeConditionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(newConditionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addConditionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(removeConditionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {addConditionButton, newConditionField, removeConditionButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jScrollPane2.getViewport().setBackground(Color.WHITE);

        targetsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Labels", "File name", "Green (Cy3) sample", "Red (Cy5) sample"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        targetsTable.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.targetsTable.toolTipText")); // NOI18N
        targetsTable.setAutoscrolls(false);
        targetsTable.setEnabled(false);
        targetsTable.setGridColor(new java.awt.Color(204, 204, 204));
        // tool tip text
        targetsTable.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e){
                Point p = e.getPoint();
                int row = targetsTable.rowAtPoint(p);
                int column = targetsTable.columnAtPoint(p);
                targetsTable.setToolTipText(String.valueOf(targetsTable.getValueAt(row,column)));
            }//end MouseMoved
        }); // end MouseMotionAdapter
        jScrollPane2.setViewportView(targetsTable);

        loadTargetsFileButton.setBackground(new java.awt.Color(255, 255, 255));
        loadTargetsFileButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.loadTargetsFileButton.text")); // NOI18N
        loadTargetsFileButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.loadTargetsFileButton.toolTipText")); // NOI18N
        loadTargetsFileButton.setEnabled(false);
        loadTargetsFileButton.setVisible(false);
        loadTargetsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTargetsFileButtonActionPerformed(evt);
            }
        });

        saveTargetsFileButton.setBackground(new java.awt.Color(255, 255, 255));
        saveTargetsFileButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.saveTargetsFileButton.text")); // NOI18N
        saveTargetsFileButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.saveTargetsFileButton.toolTipText")); // NOI18N
        saveTargetsFileButton.setVisible(false);
        saveTargetsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTargetsFileButtonActionPerformed(evt);
            }
        });

        refSampleLabel.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.refSampleLabel.text")); // NOI18N
        refSampleLabel.setEnabled(false);

        refSampleBox.setEnabled(false);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bullet1.png"))); // NOI18N
        jLabel10.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel10.text")); // NOI18N
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel10.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bullet2.png"))); // NOI18N
        jLabel11.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel11.text")); // NOI18N
        jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel11.setFocusTraversalPolicyProvider(true);
        jLabel11.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout TCTargetsDesignerPanelLayout = new org.jdesktop.layout.GroupLayout(TCTargetsDesignerPanel);
        TCTargetsDesignerPanel.setLayout(TCTargetsDesignerPanelLayout);
        TCTargetsDesignerPanelLayout.setHorizontalGroup(
            TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                        .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                                .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 115, Short.MAX_VALUE))
                            .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                                .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 278, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                                .add(refSampleLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(refSampleBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                .addContainerGap(732, Short.MAX_VALUE)
                .add(loadTargetsFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(saveTargetsFileButton)
                .addContainerGap())
        );
        TCTargetsDesignerPanelLayout.setVerticalGroup(
            TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                        .add(152, 152, 152)
                        .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(404, 404, 404))
                    .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                        .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                                .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, TCTargetsDesignerPanelLayout.createSequentialGroup()
                                        .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(TCTargetsDesignerPanelLayout.createSequentialGroup()
                                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                    .add(refSampleLabel)
                                                    .add(refSampleBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                            .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(29, 29, 29)
                                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(TCTargetsDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(saveTargetsFileButton)
                                    .add(loadTargetsFileButton))
                                .add(24, 24, 24)))
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );

        add(TCTargetsDesignerPanel, "card3");

        TCQCChooserPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setLayout(new java.awt.GridBagLayout());

        TCQCChooserPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCQCChooserPreviousButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCQCChooserPreviousButton.text")); // NOI18N
        TCQCChooserPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCChooserPreviousButtonActionPerformed(evt);
            }
        });
        jPanel6.add(TCQCChooserPreviousButton, new java.awt.GridBagConstraints());

        TCQCChooserNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCQCChooserNextButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCQCChooserNextButton.text")); // NOI18N
        TCQCChooserNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCChooserNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel6.add(TCQCChooserNextButton, gridBagConstraints);

        TCqualchecklabel.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCqualchecklabel.text")); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(214, 232, 255));

        thumbnailLabel.setBackground(new java.awt.Color(255, 255, 255));
        thumbnailLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/TCbackground.png"))); // NOI18N
        thumbnailLabel.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.thumbnailLabel.text")); // NOI18N

        descriptionLabel.setBackground(new java.awt.Color(255, 255, 255));
        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.descriptionLabel.text")); // NOI18N

        QCbackGroundBox.setBackground(new java.awt.Color(214, 232, 255));
        QCbackGroundBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.QCbackGroundBox.text")); // NOI18N

        bgImageInfoButton.setBackground(new java.awt.Color(214, 232, 255));
        bgImageInfoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        bgImageInfoButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.bgImageInfoButton.text")); // NOI18N
        bgImageInfoButton.setBorderPainted(false);
        bgImageInfoButton.setContentAreaFilled(false);
        bgImageInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bgImageInfoButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(thumbnailLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(QCbackGroundBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(bgImageInfoButton))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbnailLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(bgImageInfoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .add(QCbackGroundBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
            .add(descriptionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        thumbnailLabel3.setBackground(new java.awt.Color(255, 255, 255));
        thumbnailLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/TCdensity.png"))); // NOI18N
        thumbnailLabel3.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.thumbnailLabel3.text")); // NOI18N

        descriptionLabel3.setBackground(new java.awt.Color(255, 255, 255));
        descriptionLabel3.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.descriptionLabel3.text")); // NOI18N

        QCdensityBox.setBackground(new java.awt.Color(255, 255, 255));
        QCdensityBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.QCdensityBox.text")); // NOI18N

        distInfoButton.setBackground(new java.awt.Color(255, 255, 255));
        distInfoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.jpg"))); // NOI18N
        distInfoButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.distInfoButton.text")); // NOI18N
        distInfoButton.setBorderPainted(false);
        distInfoButton.setContentAreaFilled(false);
        distInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distInfoButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(thumbnailLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(QCdensityBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(distInfoButton))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbnailLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
            .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(distInfoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .add(QCdensityBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
            .add(descriptionLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        );

        jPanel12.setBackground(new java.awt.Color(214, 232, 255));

        thumbnailLabel4.setBackground(new java.awt.Color(255, 255, 255));
        thumbnailLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/TCmaplot.png"))); // NOI18N
        thumbnailLabel4.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.thumbnailLabel4.text")); // NOI18N

        descriptionLabel4.setBackground(new java.awt.Color(255, 255, 255));
        descriptionLabel4.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.descriptionLabel4.text")); // NOI18N

        QCMAplotBox.setBackground(new java.awt.Color(214, 232, 255));
        QCMAplotBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.QCMAplotBox.text")); // NOI18N

        maInfoButton.setBackground(new java.awt.Color(255, 255, 255));
        maInfoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        maInfoButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.maInfoButton.text")); // NOI18N
        maInfoButton.setBorderPainted(false);
        maInfoButton.setContentAreaFilled(false);
        maInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maInfoButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(thumbnailLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(QCMAplotBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(maInfoButton))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbnailLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
            .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(maInfoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .add(QCMAplotBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
            .add(descriptionLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        thumbnailLabel5.setBackground(new java.awt.Color(255, 255, 255));
        thumbnailLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/TCmvalues.png"))); // NOI18N
        thumbnailLabel5.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.thumbnailLabel5.text")); // NOI18N

        descriptionLabel5.setBackground(new java.awt.Color(255, 255, 255));
        descriptionLabel5.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.descriptionLabel5.text")); // NOI18N

        QCmvalueBox.setBackground(new java.awt.Color(255, 255, 255));
        QCmvalueBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.QCmvalueBox.text")); // NOI18N

        mvalInfoButton.setBackground(new java.awt.Color(255, 255, 255));
        mvalInfoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/QuestionMark.jpg"))); // NOI18N
        mvalInfoButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.mvalInfoButton.text")); // NOI18N
        mvalInfoButton.setBorderPainted(false);
        mvalInfoButton.setContentAreaFilled(false);
        mvalInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mvalInfoButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(thumbnailLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descriptionLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(QCmvalueBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mvalInfoButton))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbnailLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
            .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(mvalInfoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .add(QCmvalueBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
            .add(descriptionLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        );

        expertPanel.setBackground(new java.awt.Color(255, 255, 255));
        expertPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.expertPanel.border.title"))); // NOI18N

        normWithinBox.setBackground(new java.awt.Color(255, 255, 255));
        normWithinBox.setSelected(true);
        normWithinBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.normWithinBox.text")); // NOI18N
        normWithinBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normWithinBoxActionPerformed(evt);
            }
        });

        normBetweenBox.setBackground(new java.awt.Color(255, 255, 255));
        normBetweenBox.setSelected(true);
        normBetweenBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.normBetweenBox.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel2.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normWithinBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normBetweenBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        withinMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "printtiploess", "median", "loess", "robustspline", "none" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normWithinBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), withinMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        withinMethodBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withinMethodBoxActionPerformed(evt);
            }
        });

        betweenMethodBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "scale", "quantile", "Aquantile", "Gquantile", "Rquantile", "Tquantile", "vsn" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normBetweenBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), betweenMethodBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel4.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normWithinBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        withinBGcorrBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "subtract", "none", "half", "minimum", "movingmin", "edwards", "normexp", "rma" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, normWithinBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), withinBGcorrBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel6.text")); // NOI18N

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel7.text")); // NOI18N

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel8.text")); // NOI18N

        mainTestingStrategyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "separate", "nestedF", "global", "hierarchical" }));
        mainTestingStrategyBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainTestingStrategyBoxActionPerformed(evt);
            }
        });

        mainWriteRawBox.setBackground(new java.awt.Color(255, 255, 255));
        mainWriteRawBox.setSelected(true);
        mainWriteRawBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.mainWriteRawBox.text")); // NOI18N
        mainWriteRawBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        guruBox.setBackground(new java.awt.Color(255, 255, 255));
        guruBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.guruBox.text")); // NOI18N
        guruBox.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.guruBox.toolTipText")); // NOI18N
        guruBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        guruBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        LFC2Box.setBackground(new java.awt.Color(255, 255, 255));
        LFC2Box.setSelected(true);
        LFC2Box.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.LFC2Box.text")); // NOI18N
        LFC2Box.setToolTipText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.LFC2Box.toolTipText")); // NOI18N
        LFC2Box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LFC2BoxActionPerformed(evt);
            }
        });

        jLabel29.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel29.text")); // NOI18N

        pValCutoffBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0.05", "0.005", "0.0005", "0.00005", "0.1", "0.15", "0.2", "0.25" }));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel9.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel9.text")); // NOI18N

        mainPvalBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BH", "BY", "fdr", "holm", "none" }));

        org.jdesktop.layout.GroupLayout expertPanelLayout = new org.jdesktop.layout.GroupLayout(expertPanel);
        expertPanel.setLayout(expertPanelLayout);
        expertPanelLayout.setHorizontalGroup(
            expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(expertPanelLayout.createSequentialGroup()
                        .add(normBetweenBox)
                        .add(18, 18, 18)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(betweenMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel6)
                    .add(expertPanelLayout.createSequentialGroup()
                        .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(expertPanelLayout.createSequentialGroup()
                                .add(normWithinBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(withinMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel4)
                                .add(3, 3, 3))
                            .add(expertPanelLayout.createSequentialGroup()
                                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel7)
                                    .add(expertPanelLayout.createSequentialGroup()
                                        .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(expertPanelLayout.createSequentialGroup()
                                                .add(jLabel9)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(mainPvalBox, 0, 128, Short.MAX_VALUE))
                                            .add(expertPanelLayout.createSequentialGroup()
                                                .add(jLabel29)
                                                .add(18, 18, 18)
                                                .add(pValCutoffBox, 0, 136, Short.MAX_VALUE)))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(LFC2Box)
                                    .add(mainWriteRawBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(guruBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(withinBGcorrBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, expertPanelLayout.createSequentialGroup()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainTestingStrategyBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(397, 397, 397)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        expertPanelLayout.setVerticalGroup(
            expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(expertPanelLayout.createSequentialGroup()
                .add(jLabel6)
                .add(1, 1, 1)
                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(normWithinBox)
                    .add(withinBGcorrBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(withinMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(normBetweenBox)
                    .add(betweenMethodBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, expertPanelLayout.createSequentialGroup()
                        .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(expertPanelLayout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(mainTestingStrategyBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(mainPvalBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(expertPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel29)
                            .add(pValCutoffBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, expertPanelLayout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(mainWriteRawBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guruBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(LFC2Box))
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                .addContainerGap())
        );

        selectAllBox.setBackground(new java.awt.Color(255, 255, 255));
        selectAllBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.selectAllBox.text")); // NOI18N
        selectAllBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        selectAllBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBoxActionPerformed(evt);
            }
        });

        showExpertBox.setBackground(new java.awt.Color(255, 255, 255));
        showExpertBox.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.showExpertBox.text")); // NOI18N
        showExpertBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showExpertBoxActionPerformed(evt);
            }
        });

        jLabel24.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel24.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(showExpertBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 410, Short.MAX_VALUE)
                .add(selectAllBox)
                .add(38, 38, 38))
            .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2Layout.createSequentialGroup()
                .add(27, 27, 27)
                .add(jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(jPanel2Layout.createSequentialGroup()
                .add(expertPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectAllBox)
                    .add(showExpertBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(expertPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        expertPanel.setVisible(false);

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));

        jScrollPane4.setBorder(null);

        qcInfoTextArea.setColumns(20);
        qcInfoTextArea.setEditable(false);
        qcInfoTextArea.setFont(new java.awt.Font("Arial", 0, 12));
        qcInfoTextArea.setLineWrap(true);
        qcInfoTextArea.setRows(5);
        qcInfoTextArea.setWrapStyleWord(true);
        qcInfoTextArea.setBorder(null);
        jScrollPane4.setViewportView(qcInfoTextArea);

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout TCQCChooserPanelLayout = new org.jdesktop.layout.GroupLayout(TCQCChooserPanel);
        TCQCChooserPanel.setLayout(TCQCChooserPanelLayout);
        TCQCChooserPanelLayout.setHorizontalGroup(
            TCQCChooserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .add(TCQCChooserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCQCChooserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, TCqualchecklabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        TCQCChooserPanelLayout.setVerticalGroup(
            TCQCChooserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCQCChooserPanelLayout.createSequentialGroup()
                .add(TCQCChooserPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCQCChooserPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(TCqualchecklabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(37, 37, 37))
                    .add(TCQCChooserPanelLayout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(TCQCChooserPanel, "card4");

        TCQCResultsPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel7.setLayout(new java.awt.GridBagLayout());

        TCQCResultsPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCQCResultsPreviousButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCQCResultsPreviousButton.text")); // NOI18N
        TCQCResultsPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCResultsPreviousButtonActionPerformed(evt);
            }
        });
        jPanel7.add(TCQCResultsPreviousButton, new java.awt.GridBagConstraints());

        TCQCResultsNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCQCResultsNextButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCQCResultsNextButton.text")); // NOI18N
        TCQCResultsNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCQCResultsNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel7.add(TCQCResultsNextButton, gridBagConstraints);

        TCqualchecklabel1.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCqualchecklabel1.text")); // NOI18N

        TCQCResultScrollPane.setBorder(null);

        org.jdesktop.layout.GroupLayout TCQCResultsPanelLayout = new org.jdesktop.layout.GroupLayout(TCQCResultsPanel);
        TCQCResultsPanel.setLayout(TCQCResultsPanelLayout);
        TCQCResultsPanelLayout.setHorizontalGroup(
            TCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .add(TCQCResultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCqualchecklabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TCQCResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                .addContainerGap())
        );
        TCQCResultsPanelLayout.setVerticalGroup(
            TCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TCQCResultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TCQCResultsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCqualchecklabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(TCQCResultScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(TCQCResultsPanel, "card5");

        TCExperimentDesignerPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel8.setLayout(new java.awt.GridBagLayout());

        TCExperimentDesignerPreviousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToPrevious.png"))); // NOI18N
        TCExperimentDesignerPreviousButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCExperimentDesignerPreviousButton.text")); // NOI18N
        TCExperimentDesignerPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCExperimentDesignerPreviousButtonActionPerformed(evt);
            }
        });
        jPanel8.add(TCExperimentDesignerPreviousButton, new java.awt.GridBagConstraints());

        TCExperimentDesignerNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/GoToNextHS.png"))); // NOI18N
        TCExperimentDesignerNextButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.TCExperimentDesignerNextButton.text")); // NOI18N
        TCExperimentDesignerNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCExperimentDesignerNextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 18;
        jPanel8.add(TCExperimentDesignerNextButton, gridBagConstraints);

        jLabel25.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jLabel25.text")); // NOI18N

        designerScrollPane.setBorder(null);

        jButton8.setBackground(new java.awt.Color(255, 255, 255));
        jButton8.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        createMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        createMetagroupButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.createMetagroupButton.text")); // NOI18N
        createMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMetagroupButtonActionPerformed(evt);
            }
        });

        deleteMetagroupButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteMetagroupButton.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCWorkflowPanel.deleteMetagroupButton.text")); // NOI18N
        deleteMetagroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMetagroupButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout TCExperimentDesignerPanelLayout = new org.jdesktop.layout.GroupLayout(TCExperimentDesignerPanel);
        TCExperimentDesignerPanel.setLayout(TCExperimentDesignerPanelLayout);
        TCExperimentDesignerPanelLayout.setHorizontalGroup(
            TCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
            .add(TCExperimentDesignerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(TCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, TCExperimentDesignerPanelLayout.createSequentialGroup()
                        .add(jButton8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 233, Short.MAX_VALUE)
                        .add(createMetagroupButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteMetagroupButton)
                        .addContainerGap())
                    .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)))
        );
        TCExperimentDesignerPanelLayout.setVerticalGroup(
            TCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, TCExperimentDesignerPanelLayout.createSequentialGroup()
                .add(TCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TCExperimentDesignerPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(designerScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TCExperimentDesignerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deleteMetagroupButton)
                    .add(createMetagroupButton)
                    .add(jButton8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        add(TCExperimentDesignerPanel, "card6");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void TCImportNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCImportNextButtonActionPerformed

    dataModel.setDataType("generic");

    // set up the targets editor table
    TCTargetsTableModel targetsTableModel = new TCTargetsTableModel();
    targetsTable.setModel(targetsTableModel);

    if (isImporting) {
        loadTargetsTableFromFile(dataModel.getTargetsFile());
        targetsTableModel = (TCTargetsTableModel) targetsTable.getModel();
    }

    ArrayList<String> importedFiles = new ArrayList<String>();
    for (int j = 0; j < targetsTableModel.getRowCount(); j++) {
        importedFiles.add((String) targetsTableModel.getValueAt(j, 1));
    }

    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
        String importElement = (String) importFileList.getModel().getElementAt(i);

        // in case we are importing, we do not want to add the imported files again, right?
        if (importedFiles.contains(importElement)) {
            continue;
        }

        if (!dataModel.getDataType().equals("generic")) {
            dataModel.addInputFile(importElement);
        }
        String label = Utilities.extractFileNamePathComponent(importElement);
        targetsTableModel.addRow(new Object[]{label,
                    (String) importFileList.getModel().getElementAt(i),
                    null,
                    null});
    }

    newConditionField.requestFocus();
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, TARGETSDESIGNER);
}//GEN-LAST:event_TCImportNextButtonActionPerformed

private void TCTargetsPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCTargetsPreviousButtonActionPerformed
    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, IMPORT);
}//GEN-LAST:event_TCTargetsPreviousButtonActionPerformed

private void TCTargetsNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCTargetsNextButtonActionPerformed

    // TODO check consistency of targets table
    if (!checkTargetTable()) {
        new SimpleErrorMessage(this, "Target table contains unresolved inconsistencies.");
        return;
    } else {
        TCTargetsTableModel targetsModel = (TCTargetsTableModel) targetsTable.getModel();
        try {
            File tempRoot = File.createTempFile("robin_", "");
            String targetsPath = new File(dataModel.getOutputDirFile(), dataModel.getExperimentName() + "_targets.txt").getCanonicalPath();
            dataModel.setTempRoot(tempRoot.getCanonicalPath());
            tempRoot.delete();

            // DEBUG
            System.out.println("writing targets file to:" + targetsPath);

            delegate.writeTargetsFile(targetsModel, targetsPath);
            dataModel.setTargetsFile(targetsPath);
            dataModel.setSamples(conditions);
            dataModel.setTargestModel(targetsModel);
            dataModel.setReferenceSample((String) refSampleBox.getSelectedItem());

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            new SimpleErrorMessage(this, "Error writing targets file:\n" + ex.getMessage());
            return;
        }
    }

    //DEBUG
    System.out.println("design type" + dataModel.getType());

    // TODO Inform the user about the guessed experiment design
    // and eventually have him confirm/reject the guess
    if (dataModel.getType() == TCArrayDataModel.designType.COMMON_REFERENCE) {
        int choice = JOptionPane.showOptionDialog(
                mainGUI,
                "The entered experiment seems to be based on\n"
                + "a common reference design with \"" + dataModel.getReferenceSample() + "\" being\n"
                + "the common reference sample. If you manually chose\n"
                + "a different sample as the common reference you can\n"
                + "override the automatic choice by clicking \"override\".\n"
                + "To accept the choice \"" + dataModel.getReferenceSample() + "\" click \"accept\".",
                "Common reference design detected",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                UIManager.getIcon("OptionPane.questionIcon"),
                new Object[]{"Accept", "Override"},
                "Accept");


        // Accept
        if (choice == 0) {
            // Override
        } else if (choice == 1) {
            dataModel.setReferenceSample((String) refSampleBox.getSelectedItem());
            dataModel.setUserOverrideRefSample(true);
        }
    }

    if (dataModel.getType() == TCArrayDataModel.designType.UNCONNECTED) {
        new SimpleErrorMessage(this, "Robin detected an unconnected design. The current version does\n"
                + "not support separate channel analysis, which would be\n"
                + "necessary to properly analyse unconnected design experiments.\n"
                + "If you are sure that your input should not correspond to\n"
                + "an unconnected design please check the targets table\n"
                + "an correct the entries.");
        return;

    }

    CardLayout cl = (CardLayout) this.getLayout();
    cl.show(this, QCCHOICE);
}//GEN-LAST:event_TCTargetsNextButtonActionPerformed

private void TCQCChooserPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCChooserPreviousButtonActionPerformed

    CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
    cl.show(TCWorkflowPanel.this, TARGETSDESIGNER);
}//GEN-LAST:event_TCQCChooserPreviousButtonActionPerformed

private void TCQCChooserNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCChooserNextButtonActionPerformed
    // transfer  settings to the data model
    if (normWithinBox.isSelected()) {
        dataModel.setDoNormWithin(true);
        dataModel.setNormWithinArrays((String) withinMethodBox.getSelectedItem());
        dataModel.setNormWithinArraysBGcorr((String) withinBGcorrBox.getSelectedItem());
    } else {
        dataModel.setNormWithinArrays("none");
        dataModel.setNormWithinArraysBGcorr("none");
    }

    if (normBetweenBox.isSelected()) {
        dataModel.setDoNormBetween(true);
        dataModel.setNormBetweenArrays((String) betweenMethodBox.getSelectedItem());
    } else {
        dataModel.setNormBetweenArrays("none");
    }

    dataModel.setStatStrategy((String) mainTestingStrategyBox.getSelectedItem());
    dataModel.setPValCorrectionMethod((String) mainPvalBox.getSelectedItem());
    dataModel.setMinLogFoldChangeOf2(LFC2Box.isSelected());
    dataModel.setPValCutoffValue((String) pValCutoffBox.getSelectedItem());
    dataModel.setWriteRawExprs(mainWriteRawBox.isSelected());


    // no qc desired?
    if (!QCbackGroundBox.isSelected()
            && !QCdensityBox.isSelected()
            && !QCMAplotBox.isSelected()
            && !QCmvalueBox.isSelected()) {
        System.out.println("skipping quality checks");
        qcPerformed = false;

        // move on to either the designer OR, if only 1 conditions
        // were entered start the main analysis right away?
        // TODO maybe we'll have to include something to tackle
        // replicate designs....

        //DEBUG
        if (dataModel.getType() == TCArrayDataModel.designType.SIMPLE_REPLICATE) {
            startMainAnalysis();
            return;
        }

        setupDesignerScene();
        CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
        cl.show(TCWorkflowPanel.this, EXPERIMENTDESIGNER);
        return;
    }

    // get selected QC functions
    selectedQC = new ArrayList<String>();
    if (QCbackGroundBox.isSelected()) {
        selectedQC.add("bground");
    }
    if (QCdensityBox.isSelected()) {
        selectedQC.add("density");
    }
    if (QCMAplotBox.isSelected()) {
        selectedQC.add("maplot");
    }
    if (QCmvalueBox.isSelected()) {
        selectedQC.add("mvalues");
    }


    RScriptGenerator generator = new RScriptGenerator();
    String tcqcscript = generator.generateTwoColorQCScript(selectedQC, dataModel);
    File sourceDir = new File(dataModel.getOutputDir(), "source");
    File tcqcScriptFileName = new File(sourceDir, "qualityChecks.R");

    if (guruBox.isSelected()) {
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

    // write the file
    try {
        FileWriter out = new FileWriter(tcqcScriptFileName);
        out.write(tcqcscript);
        out.close();
    } catch (IOException ex) {
        new SimpleErrorMessage(this, ex.toString() + "\nFile: " + tcqcScriptFileName);
    }


    Properties defaults = mainGUI.getDefaultSettings();
    final RTask qcTask = new RTask(defaults.getProperty("PathToR"),
            defaults.getProperty("CommandToRunR"),
            defaults.getProperty("ArgsToR"),
            tcqcScriptFileName);


    ActionListener al = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (executor.isTerminated()) {
                //DEBUG
                System.out.println("FINISHED.");

                if (qcTask.getExitValue() != 0) {
                    // the task has a problem
                    processTimer.stop();
                    new VerboseWarningDialog(mainGUI,
                            "Quality check process failure",
                            "QC process failed!\nExit code:" + qcTask.getExitValue(), qcTask.getOutputMessage(),
                            dataModel);

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
                    // switch to the results list
                    CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
                    cl.show(TCWorkflowPanel.this, QCRESULTS);
                }
            }
        }
    };
    processTimer = new Timer(100, al);
    executor.execute(qcTask);
    processTimer.start();
    mainGUI.busyIconTimer.start();
    mainGUI.progressLabel.setText("running quality checks...");
    executor.shutdown();
}//GEN-LAST:event_TCQCChooserNextButtonActionPerformed

private void TCQCResultsPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCResultsPreviousButtonActionPerformed

    CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
    cl.show(TCWorkflowPanel.this, QCCHOICE);
}//GEN-LAST:event_TCQCResultsPreviousButtonActionPerformed

private void TCQCResultsNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCQCResultsNextButtonActionPerformed

    //TODO delete input files that the user chose to ignore
    ArrayList<String> excludedFiles = new ArrayList<String>();
    Component[] qcItems = resultPanel.getComponents();
    for (Component item : qcItems) {
        if (item instanceof QCResultListItem) {
            QCResultListItem qcitem = (QCResultListItem) item;

            if (qcitem.isExcluded()) {
                excludedFiles.add(qcitem.getCelFile());
            }
        }
    }

    TCTargetsTableModel tableModel = dataModel.getTargetsModel();
    for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
        String rowFileName = (String) tableModel.getValueAt(i, 1);
        if (excludedFiles.contains(rowFileName)) {
            tableModel.removeRow(i);
        }
    }

    // Now write the modified table to disk, overwriting the original one
    try {
        delegate.writeTargetsFile(tableModel, dataModel.getTargetsFile());
    } catch (Exception ioe) {
        ioe.printStackTrace();
        System.exit(1);
    }

    if (dataModel.getType() == TCArrayDataModel.designType.SIMPLE_REPLICATE) {
        System.out.println("Simple replicate design");
        // if we have a simple design comparing only two treatments
        // we don't have to proceed to the experiment design and
        // can start right away with the main script
        startMainAnalysis();
    } else {
        setupDesignerScene();

        // switch to the designer
        CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
        cl.show(TCWorkflowPanel.this, EXPERIMENTDESIGNER);
    }

}//GEN-LAST:event_TCQCResultsNextButtonActionPerformed

    protected void startMainAnalysis() {
        //DEBUG
        System.out.println("starting main analysis");
        dataModel.setAnalysisType(AnalysisStrategy.LIMMA);

        AnalysisDesignModel designModel = null;

        try {
            designModel = analysisDesignerPanel.getDesignModel();
            designModel.validateModel();
        } catch (NoConnectionsDesignException ex) {
            Exceptions.printStackTrace(ex);
            //TODO nicer text
            new SimpleErrorMessage(this, "You have to draw at least one connection between two groups\n"
                    + "to tell me what comparisons i shall make.");
            return;
        } catch (RedundantConnectionsDesignException ex) {
            new SimpleErrorMessage(this, ex.getMessage());
            //Exceptions.printStackTrace(ex);
            return;
        } catch (NullPointerException ne) {
            //DEBUG
            if (dataModel.getType() != TCArrayDataModel.designType.SIMPLE_REPLICATE) {
                System.out.println("Caught null exception for other reason than simple replicate design\n" + ne.getMessage());
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }




        // disable controls until the task has finished
        TCQCResultsNextButton.setEnabled(false);
        TCQCResultsPreviousButton.setEnabled(false);
        TCExperimentDesignerNextButton.setEnabled(false);
        TCExperimentDesignerPreviousButton.setEnabled(false);


        RScriptGenerator generator = new RScriptGenerator();
        String code = generator.generateTwoColorMainScript(dataModel);

        // open a script editor if the user chose the guru mode
        // are you guru?
        if (guruBox.isSelected()) {
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

        final RTask task = prepareTask(tcScriptFileName, code);
        RTaskController listener = null;

        //TODO for some blasted reason the progress indicator does not start running before the task starts.....
        processTimer.setDelay(100);
        if (qcPerformed) {
            processTimer.removeActionListener(processTimer.getActionListeners()[0]);
        }
        mainGUI.startBusyAnimation("running analysis...");
        executor = Executors.newFixedThreadPool(1);
        listener = new RTaskController(dataModel, task, this, executor, processTimer);        
        executor.execute(task);
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


        for (String condition : conditions) {

            // in a common reference design it doesn't make sense to include
            // the reference on the designer panel?
        /*if (dataModel.getType() == TCDataModel.designType.COMMON_REFERENCE &&
            condition.equals(dataModel.getReferenceSample())) {
            continue;            
            }*/

            GroupModel mod = new GroupModel(condition, null);
            groups.add(mod);
        }

        analysisDesignerPanel = new AnalysisDesigner(groups);
        designerScrollPane.setViewportView(analysisDesignerPanel);
    }

private void TCExperimentDesignerPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCExperimentDesignerPreviousButtonActionPerformed
    if (qcPerformed) {
        CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
        cl.show(TCWorkflowPanel.this, QCRESULTS);
    } else {
        CardLayout cl = (CardLayout) TCWorkflowPanel.this.getLayout();
        cl.show(TCWorkflowPanel.this, QCCHOICE);
    }

}//GEN-LAST:event_TCExperimentDesignerPreviousButtonActionPerformed

private void TCExperimentDesignerNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TCExperimentDesignerNextButtonActionPerformed
    // now we should be able to start the main script
    String contrasts = analysisDesignerPanel.getDesignModel().getContrastTerms();
    dataModel.setContrastTerms(contrasts);
    System.out.println("CONSTRASTS: " + contrasts);
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

        resultPanel.setMaximumSize(new Dimension(700, 10000));
        TCQCResultScrollPane.add(resultPanel);
        TCQCResultScrollPane.setViewportView(resultPanel);
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

    TCFileChooser.setMultiSelectionEnabled(true);
    TCFileChooser.resetChoosableFileFilters();
    TCFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    // deactivated this since it was making problems when trying
    // to load data of different GenePix format versions
    //TCFileChooser.setFileFilter(new GenepixFileFilter());
    //TCFileChooser.addChoosableFileFilter(new GenericFileFilter());
    //TCFileChooser.addChoosableFileFilter(null);//TODO add more filters
    if (TCFileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
        files = TCFileChooser.getSelectedFiles();
    } else {
        return;
    }
    for (File f : files) {
        importListModel.addElement(f.getAbsolutePath());
    }

    //DEBUG
    System.out.println("Generic file type imported.");

    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
        dataModel.addInputFile((String) importFileList.getModel().getElementAt(i));
    }

    String inputType = inputTypeButtonGroup.getSelection().getActionCommand();

    // show the dialog for import of generic data and pass it the data model to
    // get the required data fields filled...
    TCGenericImportDialog genericImport = new TCGenericImportDialog(mainGUI, true, dataModel, importFileList, inputType);

    if (genericImport.getUserChoice() == TCGenericImportDialog.response.CANCEL_RESPONSE) {
        genericImport.dispose();
        return;
    } else {
        // directly switch to the targets designer panel to avoid inconsistencies
        TCImportNextButtonActionPerformed(null);
    }

}//GEN-LAST:event_addImportButtonActionPerformed

private void removeImportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeImportButtonActionPerformed
    DefaultListModel importModel = (DefaultListModel) importFileList.getModel();
    int[] selectedIndices = importFileList.getSelectedIndices();
    java.util.Arrays.sort(selectedIndices);
    for (int index = selectedIndices.length - 1; index >= 0; index--) {
        importModel.removeElementAt(selectedIndices[index]);
    }
}//GEN-LAST:event_removeImportButtonActionPerformed

private void addConditionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addConditionButtonActionPerformed
    String newCon = newConditionField.getText().replaceAll("\\s", "_");
    if (newCon.equals("")) {
        return;
    } else {
        DefaultListModel condListModel = (DefaultListModel) conditionsList.getModel();
        condListModel.addElement(newCon);
        conditions.add(newCon);
        refSampleBox.addItem(newCon);
        newConditionField.setText("");
    }

    if (conditions.size() > 1) {
        refSampleBox.setEnabled(true);
        refSampleLabel.setEnabled(true);
    }

    // set up the targets editor table
    TCTargetsTableModel oldModel = (TCTargetsTableModel) targetsTable.getModel();
    TCTargetsTableModel targetsTableModel = new TCTargetsTableModel();
    targetsTable.setModel(targetsTableModel);

    String[] values = conditions.toArray(new String[0]);

    //System.out.println("CONDITIONS: "+values.toString());

    TableColumn col = targetsTable.getColumnModel().getColumn(2);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));

    col = targetsTable.getColumnModel().getColumn(3);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));*/    

    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
        String label = Utilities.extractFileNamePathComponent((String) importFileList.getModel().getElementAt(i));
        targetsTableModel.addRow(new Object[]{label,
                    (String) importFileList.getModel().getElementAt(i),
                    oldModel.getValueAt(i, 2),
                    oldModel.getValueAt(i, 3)});
    }

    targetsTable.setEnabled(true);
}//GEN-LAST:event_addConditionButtonActionPerformed

private void newConditionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newConditionFieldActionPerformed
    addConditionButtonActionPerformed(evt);
}//GEN-LAST:event_newConditionFieldActionPerformed

private void removeConditionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConditionButtonActionPerformed
    DefaultListModel importModel = (DefaultListModel) conditionsList.getModel();
    int[] selectedIndices = conditionsList.getSelectedIndices();
    java.util.Arrays.sort(selectedIndices);

    ArrayList<String> deletedConditions = new ArrayList<String>();

    for (int index = selectedIndices.length - 1; index >= 0; index--) {
        deletedConditions.add((String) importModel.getElementAt(selectedIndices[index]));
        importModel.removeElementAt(selectedIndices[index]);
    }

    // update the targets table    
    conditions.clear();
    refSampleBox.removeAllItems();
    for (int i = 0; i <= importModel.getSize() - 1; i++) {
        conditions.add((String) importModel.getElementAt(i));
        refSampleBox.addItem((String) importModel.getElementAt(i));
    }

    if (conditions.size() <= 1) {
        refSampleBox.setEnabled(false);
        refSampleLabel.setEnabled(false);
    }

    String[] values = conditions.toArray(new String[0]);

    // conditions list empty?
    if (values.length == 0) {
        targetsTable.setEnabled(false);
        return;
    }

    TCTargetsTableModel oldModel = (TCTargetsTableModel) targetsTable.getModel();
    TCTargetsTableModel targetsTableModel = new TCTargetsTableModel();
    targetsTable.setModel(targetsTableModel);

    //System.out.println("CONDITIONS: "+values.toString());
    for (String value : values) {

        System.out.println(value);
    }

    TableColumn col = targetsTable.getColumnModel().getColumn(2);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));

    col = targetsTable.getColumnModel().getColumn(3);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));*/    

    for (int i = 0; i < importFileList.getModel().getSize(); i++) {
        String label = Utilities.extractFileNamePathComponent((String) importFileList.getModel().getElementAt(i));

        String oldCy3 = (String) oldModel.getValueAt(i, 2);
        String oldCy5 = (String) oldModel.getValueAt(i, 3);

        if (deletedConditions.contains(oldCy3)) {
            oldCy3 = null;
        } else if (deletedConditions.contains(oldCy5)) {
            oldCy5 = null;
        }

        targetsTableModel.addRow(new Object[]{label,
                    (String) importFileList.getModel().getElementAt(i),
                    oldCy3,
                    oldCy5});
    }
}//GEN-LAST:event_removeConditionButtonActionPerformed

private void saveTargetsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTargetsFileButtonActionPerformed

    JFileChooser chooser = new JFileChooser();
    String outPath = "";

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            outPath = chooser.getSelectedFile().getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    TCTargetsTableModel targetsModel = (TCTargetsTableModel) targetsTable.getModel();
    try {
        delegate.writeTargetsFile(targetsModel, outPath);
    } catch (Exception ex) {
        Exceptions.printStackTrace(ex);
        new SimpleErrorMessage(this, "Error writing targets file:\n" + ex.getMessage());
        return;
    }
}//GEN-LAST:event_saveTargetsFileButtonActionPerformed

private void showExpertBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showExpertBoxActionPerformed
    expertPanel.setVisible(showExpertBox.isSelected());
}//GEN-LAST:event_showExpertBoxActionPerformed

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    setupDesignerScene();
}//GEN-LAST:event_jButton8ActionPerformed

private void createMetagroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMetagroupButtonActionPerformed
    try {
        analysisDesignerPanel.createMetaNodeOfSelectedNodes();
    } catch (Exception ex) {
        Exceptions.printStackTrace(ex);
        new SimpleErrorMessage(this, ex.getMessage());
        return;
    }
    analysisDesignerPanel.revalidate();
}//GEN-LAST:event_createMetagroupButtonActionPerformed

private void deleteMetagroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMetagroupButtonActionPerformed
    analysisDesignerPanel.deleteSelectedMetaNodes();
}//GEN-LAST:event_deleteMetagroupButtonActionPerformed

private void mainTestingStrategyBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainTestingStrategyBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_mainTestingStrategyBoxActionPerformed

private void withinMethodBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withinMethodBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_withinMethodBoxActionPerformed

private void loadTargetsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadTargetsFileButtonActionPerformed
    JOptionPane.showConfirmDialog(this, "When loading the targets information from a file\n"
            + "you must make sure that the files listed in the\n"
            + "file are identical to the ones that were chosen on\n"
            + "the import panel. Loading a different targets file\n"
            + "will disrupt further analysis.");

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            delegate.readTargetsFile(fileChooser.getSelectedFile().getCanonicalPath(), targetsTable, conditions);
            DefaultListModel newmod = new DefaultListModel();
            for (String c : conditions) {
                newmod.addElement(c);
            }
            conditionsList.setModel(newmod);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // set up the targets editor table
    /*TCTargetsTableModel oldModel = (TCTargetsTableModel) targetsTable.getModel();
    TCTargetsTableModel targetsTableModel = new TCTargetsTableModel();    
    targetsTable.setModel(targetsTableModel);*/

    String[] values = conditions.toArray(new String[0]);

    //System.out.println("CONDITIONS: "+values.toString());

    TableColumn col = targetsTable.getColumnModel().getColumn(2);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));

    col = targetsTable.getColumnModel().getColumn(3);
    col.setCellEditor(new ComboBoxCellEditor(values));
    //col.setCellRenderer(new ComboBoxCellRenderer(dummyVals));*/    

    /*for (int i = 0; i < importFileList.getModel().getSize(); i++) {
    String label = Utilities.extractFileNamePathComponent((String) importFileList.getModel().getElementAt(i));
    targetsTableModel.addRow(new Object[] { label, 
    (String) importFileList.getModel().getElementAt(i),
    oldModel.getValueAt(i, 2),
    oldModel.getValueAt(i, 3)});
    }*/

    targetsTable.setEnabled(true);
}//GEN-LAST:event_loadTargetsFileButtonActionPerformed

    private void loadTargetsTableFromFile(String targetsFilePath) {

        delegate.readTargetsFile(targetsFilePath, targetsTable, conditions);
        DefaultListModel newmod = new DefaultListModel();
        for (String c : conditions) {
            newmod.addElement(c);
        }
        conditionsList.setModel(newmod);

        String[] values = conditions.toArray(new String[0]);

        TableColumn col = targetsTable.getColumnModel().getColumn(2);
        col.setCellEditor(new ComboBoxCellEditor(values));

        col = targetsTable.getColumnModel().getColumn(3);
        col.setCellEditor(new ComboBoxCellEditor(values));
        targetsTable.setEnabled(true);
    }

private void selectAllBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBoxActionPerformed
    QCbackGroundBox.setSelected(selectAllBox.isSelected());
    QCdensityBox.setSelected(selectAllBox.isSelected());
    QCmvalueBox.setSelected(selectAllBox.isSelected());
    QCMAplotBox.setSelected(selectAllBox.isSelected());
}//GEN-LAST:event_selectAllBoxActionPerformed

private void bgImageInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bgImageInfoButtonActionPerformed
    qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCMainPanel.bgImageInfo.text"));
}//GEN-LAST:event_bgImageInfoButtonActionPerformed

private void distInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distInfoButtonActionPerformed
    qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCMainPanel.distInfo.text"));
}//GEN-LAST:event_distInfoButtonActionPerformed

private void maInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maInfoButtonActionPerformed
    qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCMainPanel.MAInfo.text"));
}//GEN-LAST:event_maInfoButtonActionPerformed

private void mvalInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mvalInfoButtonActionPerformed
    qcInfoTextArea.setText(org.openide.util.NbBundle.getMessage(TCWorkflowPanel.class, "TCMainPanel.MValInfo.text"));
}//GEN-LAST:event_mvalInfoButtonActionPerformed

private void LFC2BoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LFC2BoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_LFC2BoxActionPerformed

private void normWithinBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normWithinBoxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_normWithinBoxActionPerformed

private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
    dataModel.setInputType(InputDataType.GENERIC);
}//GEN-LAST:event_jRadioButton2ActionPerformed

private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
    dataModel.setInputType(InputDataType.AGILENT);
    dataModel.setIDcolumnStart("ProbeName");

    //DEBUG 
    System.out.println("ID COLUMNS START: " + dataModel.getIDcolumnStart());
}//GEN-LAST:event_jRadioButton1ActionPerformed

    private boolean checkTargetTable() {
        TCTargetsTableModel targetsModel = (TCTargetsTableModel) targetsTable.getModel();

        for (int i = 0; i < targetsModel.getRowCount(); i++) {
            String cy3sample = (String) targetsModel.getValueAt(i, 2);
            String cy5sample = (String) targetsModel.getValueAt(i, 3);
            if ((cy3sample == null) || (cy5sample == null)) {
                targetsTable.setRowSelectionInterval(i, i);
                new SimpleErrorMessage(this, "Missing sample in row " + (i + 1) + ".");
                return false;
            } else if (cy3sample.equals(cy5sample)) {
                targetsTable.setRowSelectionInterval(i, i);

                int choice = JOptionPane.showOptionDialog(
                        mainGUI,
                        "You indicated that the same RNA sample was hybridized\n"
                        + "to both color channles on the chip in line " + (i + 1) + "\n"
                        + "(" + (String) targetsModel.getValueAt(i, 1) + ")\n"
                        + "If this was a mistake you can correct it by clicking\n"
                        + "\"Correct\".",
                        "Same sample on both channels",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        UIManager.getIcon("OptionPane.questionIcon"),
                        new Object[]{"Accept", "Correct"},
                        "Correct");


                // Accept
                if (choice == 0) {
                    dataModel.addWarning(new Warning("SAME SAMPLE",
                            "The same RNA sample has been hybridized to\n"
                            + "both channels on the following chip:\n"
                            + (String) targetsModel.getValueAt(i, 1), 1));
                    return true;

                    // Correct
                } else if (choice == 1) {
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public void RTaskFinished() {
        this.finishGracefully();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox LFC2Box;
    private javax.swing.JCheckBox QCMAplotBox;
    private javax.swing.JCheckBox QCbackGroundBox;
    private javax.swing.JCheckBox QCdensityBox;
    private javax.swing.JCheckBox QCmvalueBox;
    private javax.swing.JButton TCExperimentDesignerNextButton;
    private javax.swing.JPanel TCExperimentDesignerPanel;
    private javax.swing.JButton TCExperimentDesignerPreviousButton;
    private javax.swing.JFileChooser TCFileChooser;
    private javax.swing.JButton TCImportNextButton;
    private javax.swing.JPanel TCImportPanel;
    private javax.swing.JButton TCQCChooserNextButton;
    private javax.swing.JPanel TCQCChooserPanel;
    private javax.swing.JButton TCQCChooserPreviousButton;
    private javax.swing.JScrollPane TCQCResultScrollPane;
    private javax.swing.JButton TCQCResultsNextButton;
    private javax.swing.JPanel TCQCResultsPanel;
    private javax.swing.JButton TCQCResultsPreviousButton;
    private javax.swing.JPanel TCTargetsDesignerPanel;
    private javax.swing.JButton TCTargetsNextButton;
    private javax.swing.JButton TCTargetsPreviousButton;
    private javax.swing.JLabel TCqualchecklabel;
    private javax.swing.JLabel TCqualchecklabel1;
    private javax.swing.JButton addConditionButton;
    private javax.swing.JButton addImportButton;
    private javax.swing.JComboBox betweenMethodBox;
    private javax.swing.JButton bgImageInfoButton;
    private javax.swing.JList conditionsList;
    private javax.swing.JButton createMetagroupButton;
    private javax.swing.JButton deleteMetagroupButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel descriptionLabel3;
    private javax.swing.JLabel descriptionLabel4;
    private javax.swing.JLabel descriptionLabel5;
    private javax.swing.JScrollPane designerScrollPane;
    private javax.swing.JButton distInfoButton;
    private javax.swing.JPanel expertPanel;
    private javax.swing.JCheckBox guruBox;
    private javax.swing.JList importFileList;
    private javax.swing.ButtonGroup inputTypeButtonGroup;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadTargetsFileButton;
    private javax.swing.JButton maInfoButton;
    private javax.swing.JComboBox mainPvalBox;
    private javax.swing.JComboBox mainTestingStrategyBox;
    private javax.swing.JCheckBox mainWriteRawBox;
    private javax.swing.JButton mvalInfoButton;
    private javax.swing.JTextField newConditionField;
    private javax.swing.JCheckBox normBetweenBox;
    private javax.swing.JCheckBox normWithinBox;
    private javax.swing.JComboBox pValCutoffBox;
    private javax.swing.JTextArea qcInfoTextArea;
    private javax.swing.JComboBox refSampleBox;
    private javax.swing.JLabel refSampleLabel;
    private javax.swing.JButton removeConditionButton;
    private javax.swing.JButton removeImportButton;
    private javax.swing.JButton saveTargetsFileButton;
    private javax.swing.JCheckBox selectAllBox;
    private javax.swing.JCheckBox showExpertBox;
    private javax.swing.JTable targetsTable;
    private javax.swing.JLabel thumbnailLabel;
    private javax.swing.JLabel thumbnailLabel3;
    private javax.swing.JLabel thumbnailLabel4;
    private javax.swing.JLabel thumbnailLabel5;
    private javax.swing.JComboBox withinBGcorrBox;
    private javax.swing.JComboBox withinMethodBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
