/*
 * SCGenericImportDialog.java
 *
 * Created on 27. Juli 2009, 11:52
 * fixed special character
 */

package de.mpimp.golm.robin.GUI.singlechannel;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.warnings.Warning;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author  marc
 */
public class SCGenericImportDialog extends javax.swing.JDialog {

    String sep = "\\t"; // default separator
    File[] files;
    private response userChoice;
    private SCArrayDataModel dataModel;
    private JList importList;
    private File presetRoot;
    private String filesep = System.getProperty("file.separator");
    private final static int MIN_IDENTICAL_COLUMNNUMBER = 100;
    private String inputType;
    private int dataStartIndex = 0;

    public enum response {
        OK_RESPONSE,
        CANCEL_RESPONSE
    }

    /** Creates new form SCGenericImportDialog
     * @param parent
     * @param modal
     * @param files
     * @param model
     * @param importFileList
     * @param type
     * @param files
     */
    public SCGenericImportDialog(
            java.awt.Frame parent,
            boolean modal,
            File[] files,
            SCArrayDataModel model,
            JList importFileList,
            String type) {

        super(parent, modal);
        this.files = files;
        this.dataModel = model;
        this.importList = importFileList;
        this.inputType = type;
        initComponents();        
        try {
            customInitComponents();
            dataStartIndex = findIndexOfFirstDataRow(files[0].getCanonicalPath(), sep);
            loadDataSample(files[0].getCanonicalPath(), sep);
            loadLayoutPresets();
            populateImportSettingsBox();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        this.centerOnParent();
        this.setVisible(true);
    }

    private void customInitComponents() throws IOException {
        DefaultComboBoxModel fileListModel = (DefaultComboBoxModel) sampleFileBox.getModel();
        for (File f: files) {
            fileListModel.addElement(f.getCanonicalPath());
        }
    }

    public response getUserChoice() {
        return userChoice;
    }

    private void centerOnParent() {
        Dimension parentDim = getParent().getSize();
        Point parentLoc = getParent().getLocation();
        Dimension myDim = getSize();

        int myX = (parentDim.width / 2 + parentLoc.x) - myDim.width / 2;
        int myY = (parentDim.height / 2 + parentLoc.y) - myDim.height / 2;

        setLocation(myX, myY);
    }

    private void saveLayoutPresetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String layoutName = JOptionPane.showInputDialog("Please enter layout name:");
        Properties newLayout = new Properties();

        newLayout.setProperty("ngrid.c",(Integer.toString((Integer) gridColSpinner.getValue())));
        newLayout.setProperty("ngrid.r",(Integer.toString((Integer) gridRowSpinner.getValue())));
        newLayout.setProperty("nspot.c",(Integer.toString((Integer) blockColSpinner.getValue())));
        newLayout.setProperty("nspot.r",(Integer.toString((Integer) blockRowSpinner.getValue())));

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(presetRoot + filesep + layoutName + ".sclayout");
            newLayout.store(fos, "Custom layout");

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }

        // now reload the preset box
        loadLayoutPresets();
    }

    private void loadLayoutPresets() {

        // we store the presets as saved property lists in lib/presets?

        //String instPath = Utilities.getInstallPath((RobinMainGUI) this.getParent(), "Robin2.jar");
        //String instPath = ((RobinMainGUI) this.getParent()).getInstallPath();
        //presetRoot = new File(instPath+filesep+"lib"+filesep+"presets");

        File dataPath = ((RobinMainGUI) this.getParent()).getResourcePath();
        presetRoot = new File(dataPath, "presets");

        //DEBUG
        try {

            System.out.println("trying to load presets from" + presetRoot.getCanonicalPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        DefaultComboBoxModel presetModel = new DefaultComboBoxModel();

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sclayout");
            }
        };

        String[] presetList = presetRoot.list(filter);
        presetModel.addElement("none");
        for (String preset : presetList) {
            preset = preset.replaceAll("\\.sclayout", "");
            presetModel.addElement(preset);
        }
        layoutPresetCombo.setModel(presetModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel2 = new javax.swing.JPanel();
        columnsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        identifierBox = new javax.swing.JComboBox();
        fgBox = new javax.swing.JComboBox();
        bgBox = new javax.swing.JComboBox();
        flagBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        colSepBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        decSepBox = new javax.swing.JComboBox();
        loadSampleButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        sampleFileBox = new javax.swing.JComboBox();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        importPresetCombo = new javax.swing.JComboBox();
        loadPresetButton = new javax.swing.JButton();
        savePresetButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        editLayoutBox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        saveLayoutButton = new javax.swing.JButton();
        layoutPresetCombo = new javax.swing.JComboBox();
        loadLayoutButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        labeljshd = new javax.swing.JLabel();
        blockRowSpinner = new javax.swing.JSpinner();
        blockColSpinner = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        gridColSpinner = new javax.swing.JSpinner();
        gridRowSpinner = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataSampleTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        columnsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.columnsPanel.border.title"))); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel3.text")); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel6.text")); // NOI18N
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel7.setBackground(java.awt.SystemColor.window);
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel7.text")); // NOI18N
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        identifierBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "dummy", " " }));
        identifierBox.setMaximumSize(new java.awt.Dimension(100, 100));

        fgBox.setMaximumSize(new java.awt.Dimension(100, 100));

        bgBox.setMaximumSize(new java.awt.Dimension(100, 100));

        flagBox.setMaximumSize(new java.awt.Dimension(100, 100));

        org.jdesktop.layout.GroupLayout columnsPanelLayout = new org.jdesktop.layout.GroupLayout(columnsPanel);
        columnsPanel.setLayout(columnsPanelLayout);
        columnsPanelLayout.setHorizontalGroup(
            columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, columnsPanelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(bgBox, 0, 202, Short.MAX_VALUE)
                    .add(fgBox, 0, 202, Short.MAX_VALUE)
                    .add(identifierBox, 0, 202, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, flagBox, 0, 202, Short.MAX_VALUE)))
        );
        columnsPanelLayout.setVerticalGroup(
            columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(columnsPanelLayout.createSequentialGroup()
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(columnsPanelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jLabel6))
                    .add(identifierBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(columnsPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jLabel2))
                    .add(columnsPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fgBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(columnsPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jLabel3))
                    .add(columnsPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bgBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(columnsPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jLabel7))
                    .add(columnsPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(flagBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel1.text")); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jPanel3.border.title"))); // NOI18N

        jLabel12.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel12.text")); // NOI18N

        colSepBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tab (\\t)", "Comma (,)", "Semicolon (;)", "Colon (:)", "Pipe (|)", "Hash (#)" }));

        jLabel13.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel13.text")); // NOI18N

        decSepBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Point ( . )", "Comma ( , )" }));

        loadSampleButton.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        loadSampleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/pipette48.png"))); // NOI18N
        loadSampleButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.loadSampleButton.text")); // NOI18N
        loadSampleButton.setIconTextGap(20);
        loadSampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSampleButtonActionPerformed(evt);
            }
        });

        jLabel14.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel14.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jLabel12)
                .add(colSepBox, 0, 239, Short.MAX_VALUE))
            .add(jPanel3Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel13)
                .add(decSepBox, 0, 239, Short.MAX_VALUE))
            .add(jPanel3Layout.createSequentialGroup()
                .add(jLabel14)
                .add(sampleFileBox, 0, 283, Short.MAX_VALUE))
            .add(loadSampleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jLabel12))
                    .add(colSepBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jLabel13))
                    .add(decSepBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jLabel14))
                    .add(sampleFileBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loadSampleButton)
                .addContainerGap())
        );

        cancelButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jPanel1.border.title"))); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel4.text")); // NOI18N

        importPresetCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importPresetComboActionPerformed(evt);
            }
        });

        loadPresetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        loadPresetButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.loadPresetButton.text")); // NOI18N
        loadPresetButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.loadPresetButton.toolTipText")); // NOI18N
        loadPresetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importPresetComboActionPerformed(evt);
            }
        });

        savePresetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/saveHS.png"))); // NOI18N
        savePresetButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.savePresetButton.text")); // NOI18N
        savePresetButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.savePresetButton.toolTipText")); // NOI18N
        savePresetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePresetButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importPresetCombo, 0, 230, Short.MAX_VALUE)
                .add(8, 8, 8)
                .add(loadPresetButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(savePresetButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel4)
                    .add(savePresetButton)
                    .add(importPresetCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(loadPresetButton)))
        );

        okButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jPanel4.border.title"))); // NOI18N

        editLayoutBox.setFont(editLayoutBox.getFont());
        editLayoutBox.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.editLayoutBox.text")); // NOI18N
        editLayoutBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLayoutBoxActionPerformed(evt);
            }
        });

        saveLayoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/saveHS.png"))); // NOI18N
        saveLayoutButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.saveLayoutButton.text")); // NOI18N
        saveLayoutButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.saveLayoutButton.toolTipText")); // NOI18N
        saveLayoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLayoutButtonActionPerformed(evt);
            }
        });

        layoutPresetCombo.setFont(layoutPresetCombo.getFont());
        layoutPresetCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutPresetComboActionPerformed(evt);
            }
        });

        loadLayoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        loadLayoutButton.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.loadLayoutButton.text")); // NOI18N
        loadLayoutButton.setToolTipText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.loadLayoutButton.toolTipText")); // NOI18N
        loadLayoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutPresetComboActionPerformed(evt);
            }
        });

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel5.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layoutPresetCombo, 0, 243, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loadLayoutButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveLayoutButton))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(jLabel5))
            .add(saveLayoutButton)
            .add(jPanel5Layout.createSequentialGroup()
                .add(2, 2, 2)
                .add(layoutPresetCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(loadLayoutButton)
        );

        jLabel10.setFont(jLabel10.getFont().deriveFont((float)11));
        jLabel10.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel10.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        labeljshd.setFont(labeljshd.getFont().deriveFont((float)11));
        labeljshd.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.labeljshd.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), labeljshd, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        blockRowSpinner.setFont(blockRowSpinner.getFont());
        blockRowSpinner.setModel(new javax.swing.SpinnerNumberModel());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), blockRowSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        blockColSpinner.setFont(blockColSpinner.getFont());
        blockColSpinner.setModel(new javax.swing.SpinnerNumberModel());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), blockColSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(labeljshd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(blockRowSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(blockColSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(blockRowSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labeljshd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel10)
                    .add(blockColSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        gridColSpinner.setFont(gridColSpinner.getFont());
        gridColSpinner.setModel(new javax.swing.SpinnerNumberModel());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), gridColSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridRowSpinner.setFont(gridRowSpinner.getFont());
        gridRowSpinner.setModel(new javax.swing.SpinnerNumberModel());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), gridRowSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel8.setFont(jLabel8.getFont().deriveFont((float)11));
        jLabel8.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel8.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel8, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel9.setFont(jLabel9.getFont().deriveFont((float)11));
        jLabel9.setText(org.openide.util.NbBundle.getMessage(SCGenericImportDialog.class, "SCGenericImportDialog.jLabel9.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, editLayoutBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gridRowSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gridColSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel8)
                    .add(gridRowSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel9)
                    .add(gridColSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(editLayoutBox)
                .add(231, 231, 231))
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editLayoutBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        dataSampleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        dataSampleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(dataSampleTable);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                                .add(35, 35, 35))
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, columnsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(okButton))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(columnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadSampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSampleButtonActionPerformed
        // get the first input file and try to figure out where the real data starts
        // data must be in character-separated text format
        //String path = dataModel.getInputFiles().get(0);

        String path = (String) sampleFileBox.getSelectedItem();

        sep = (String) colSepBox.getSelectedItem();

        if (sep.contains("Tab")) {
            sep = "\\t";
        } else if (sep.contains("Comma")) {
            sep = ",";
        } else if (sep.contains("Semicolon")) {
            sep = ";";
        } else if (sep.contains("Colon")) {
            sep = ":";
        } else if (sep.contains("Pipe")) {
            sep = "\\|";
        } else if (sep.contains("Hash")) {
            sep = "#";
        }

        loadDataSample(path, sep);
        
}//GEN-LAST:event_loadSampleButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        importList.setModel(new DefaultListModel());
        dataModel.setInputFiles(new ArrayList<String>());
        this.userChoice = response.CANCEL_RESPONSE;
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
        if (!layoutAndDataConsistent()) {
            if (inputType.equals("agilent")) {
                new SimpleErrorMessage(this, "The layout you've entered is not fitting to\n" +
                        "the number of data rows in the input!\n" +
                        "This is a known problem when dealing with\n" +
                        "AGILENT data since AGILENT sometimes removes\n" +
                        "lines from the raw data files (e.g. control probes etc.).\n" +
                        "Robin will try to fix this by inserting \"dummy\" lines\n" + 
                        "thereby padding the input to match the layout\n" + 
                        "correctly.");
            } else {
                new SimpleErrorMessage(this, "The layout you've entered is not fitting to\n" +
                        "the number of data rows in the input!\n" +
                        "Please correct the layout and try again.");
                return;
            }
        }

        HashMap<String, String> genCols = new HashMap<String, String>();
        HashMap<String, String> layout = new HashMap<String, String>();

        genCols.put("G",(String) fgBox.getSelectedItem());
        genCols.put("Gb",(String) bgBox.getSelectedItem());
        genCols.put("ID",(String) identifierBox.getSelectedItem());
        genCols.put("flag",(String) flagBox.getSelectedItem());

        dataModel.setGenericColumns(genCols);

        layout.put("ngrid.r",gridRowSpinner.getValue().toString());
        layout.put("ngrid.c",gridColSpinner.getValue().toString());
        layout.put("nspot.r",blockRowSpinner.getValue().toString());
        layout.put("nspot.c",blockColSpinner.getValue().toString());

        dataModel.setPrinterLayout(layout);
        dataModel.setSepChar(sep);
        String idcol = (String) identifierBox.getSelectedItem();
        dataModel.setIDcolumnStart(idcol);

        // do the header cleanup for all data files
        // and write the cleaned-up copies somewhere (maybe tmp?) AND also
        // replace the list of input files in the main panel with the cleanup-up
        // copies !!
        // diplay some feedback on what the machine's doing
        MessageWindow mwin = new MessageWindow(this, "Cleaning up input files. Please be patient...");
        try {
            okButton.setEnabled(false);
            cleanupInputFiles();
        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(
                    new Exception("There was a problem when trying to read/write the input files.\n" +
                    "Please make sure the files are accessible and retry.",ex));
        }
        mwin.dispose();

        this.userChoice = response.OK_RESPONSE;
        this.setVisible(false);

    }//GEN-LAST:event_okButtonActionPerformed

    private void saveLayoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLayoutButtonActionPerformed
        saveLayoutPresetButtonActionPerformed(evt);
    }//GEN-LAST:event_saveLayoutButtonActionPerformed

    private void layoutPresetComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutPresetComboActionPerformed
        Properties newLayout = new Properties();
        String chosenPreset = ((String) layoutPresetCombo.getSelectedItem())+".sclayout";
    
        if (chosenPreset.startsWith("none")) {
            gridColSpinner.setValue(1);
            gridRowSpinner.setValue(1);
            blockColSpinner.setValue(1);
            blockRowSpinner.setValue(1);

            editLayoutBox.setSelected(true);
            gridColSpinner.setEnabled(true);
            gridRowSpinner.setEnabled(true);
            blockColSpinner.setEnabled(true);
            blockRowSpinner.setEnabled(true);
            return;
        }

        FileInputStream is;

        try {
            System.out.println("Loading layout preset: "+chosenPreset);
            is = new FileInputStream(presetRoot.getCanonicalPath()+filesep+chosenPreset);
            newLayout.load(is);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        gridColSpinner.setValue(Integer.valueOf(newLayout.getProperty("ngrid.c")));
        gridRowSpinner.setValue(Integer.valueOf(newLayout.getProperty("ngrid.r")));
        blockColSpinner.setValue(Integer.valueOf(newLayout.getProperty("nspot.c")));
        blockRowSpinner.setValue(Integer.valueOf(newLayout.getProperty("nspot.r")));

        editLayoutBox.setSelected(false);
        gridColSpinner.setEnabled(false);
        gridRowSpinner.setEnabled(false);
        blockColSpinner.setEnabled(false);
        blockRowSpinner.setEnabled(false);
    }//GEN-LAST:event_layoutPresetComboActionPerformed

    private void importPresetComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importPresetComboActionPerformed
        // load the preset
        Properties newImportPreset = new Properties();
        String chosenPreset = ((String) importPresetCombo.getSelectedItem())+".scimport";

        //DEBUG
        System.out.println("loading import settings:"+chosenPreset);

        if (chosenPreset.startsWith("none")) return;

        FileInputStream is;

        try {
            System.out.println("Loading import settings preset: "+chosenPreset);
            is = new FileInputStream(presetRoot.getCanonicalPath()+filesep+chosenPreset);
            newImportPreset.load(is);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //DEBUG
        System.out.println(newImportPreset.toString());

        identifierBox.setSelectedItem(newImportPreset.getProperty("ID"));
        fgBox.setSelectedItem(newImportPreset.getProperty("G"));
        bgBox.setSelectedItem(newImportPreset.getProperty("Gb"));
        flagBox.setSelectedItem(newImportPreset.getProperty("flag"));

        // load this layout!!
        layoutPresetCombo.setSelectedItem(newImportPreset.getProperty("layout"));
        layoutPresetComboActionPerformed(null);
    }//GEN-LAST:event_importPresetComboActionPerformed

    private void savePresetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePresetButtonActionPerformed
        String settingsName = JOptionPane.showInputDialog("Please choose a name for the settings:");

        Properties importSettings = new Properties();

        importSettings.setProperty("ID",(String) identifierBox.getSelectedItem());
        importSettings.setProperty("G",(String) fgBox.getSelectedItem());
        importSettings.setProperty("Gb",(String) bgBox.getSelectedItem());
        importSettings.setProperty("flag",(String) flagBox.getSelectedItem());
        importSettings.setProperty("layout",(String) layoutPresetCombo.getSelectedItem());

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(presetRoot + filesep + settingsName + ".scimport");
            importSettings.store(fos, "Custom single channel import settings "+settingsName);

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        populateImportSettingsBox();
        importPresetCombo.setSelectedItem(settingsName);
    }//GEN-LAST:event_savePresetButtonActionPerformed

    private void editLayoutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLayoutBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editLayoutBoxActionPerformed

    private void populateImportSettingsBox() {
    //DEBUG
        try {

            System.out.println("trying to load import settings from" + presetRoot.getCanonicalPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        DefaultComboBoxModel settingsModel = new DefaultComboBoxModel();

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".scimport");
            }
        };

        String[] presetList = presetRoot.list(filter);
        settingsModel.addElement("none");
        for (String preset : presetList) {
            preset = preset.replaceAll("\\.scimport", "");
            settingsModel.addElement(preset);
        }
        importPresetCombo.setModel(settingsModel);
    }

    private void cleanupInputFiles() throws IOException, Exception {

        // iterate over the input files, clean them, write clean copy to temp and update both
        // the file import list and at the end the inputFileList in the dataModel

        // flush the import file list on the main panel
        ((DefaultListModel) importList.getModel()).removeAllElements();
        ArrayList<String> originalFiles = dataModel.getInputFiles();
        ArrayList<String> cleanFiles = new ArrayList<String>();

        for (String file : dataModel.getInputFiles()) {

            if (file.endsWith(".robin")) {
                //DEBUG
                System.out.println("skipping already cleaned-up file "+ file);
                cleanFiles.add(file);
                continue;
            }

            //DEBUG
            System.out.println("stripping file:"+file);

            //int headerIndex = findIndexOfFirstDataRow(file, sep);
            String pathToStrippedCopy = stripHeaderFromFile(dataStartIndex, file);
            if (pathToStrippedCopy == null ) {
                throw new Exception("Input file cleanup failure");
            }
            cleanFiles.add(pathToStrippedCopy);

            // update import file list
            ((DefaultListModel) importList.getModel()).addElement(pathToStrippedCopy);
        }

        // now update the import file list in the data model
        dataModel.getInputFiles().removeAll(originalFiles);
        for (String cFile : cleanFiles) {
            dataModel.getInputFiles().add(cFile);
        }
    }

private void loadDataSample(String path, String separator) {
    BufferedReader rd;
    String line;
    int lineCounter = 1, sampleRowCounter = 0;
    //dataStartIndex = findIndexOfFirstDataRow(path, sep);
    DefaultTableModel tableModel = new DefaultTableModel();
    String[] cols = null;

    try {
        Charset encoding = Utilities.detectEncodingFromFile(new File(path));
            InputStreamReader reader=new InputStreamReader(new FileInputStream(path), encoding);
            rd = new BufferedReader(reader);

        while ((line =  rd.readLine()  ) != null) {
            if (lineCounter == dataStartIndex) {

                // we need to get rid of quotes that would mess up R
                line = line.replaceAll("\"", "");
                line = line.replaceAll("'", "");

                // extract column headers
                // if we are importing a preprocessed AGILENT data file
                // the standard format of AGILENT data might not be
                // present any more... let's check this

//                if (inputType.equals("agilent") && !line.startsWith("FEATURES\tFeatureNum\tRow\tCol")) {
//                    dataStartIndex -= 1;
//                    loadDataSample(path, separator);
//                    break;
//                }


                cols = line.split(sep);
                tableModel.setColumnIdentifiers(cols);

            } else if (lineCounter == dataStartIndex+50) {
                // finished loading 50 lines
                dataSampleTable.setModel(tableModel);
                break;

             } else if (lineCounter > dataStartIndex) {
                // load data line into model
                String[] fields = line.split(sep);
                tableModel.addRow(fields);
            }
            lineCounter++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    // check whether we got something we can split
    if (cols == null) {

        //DEBUG
        System.out.println("cols="+cols+" dataStartIndex="+dataStartIndex);
        if (!inputType.equals("agilent") && dataStartIndex != 1 ) { // preprocessed agilent file with header on first row
            new SimpleErrorMessage(this, "Could not load data sample.\n"+
                         "Please make sure that the input data is\n" +
                         "in tabular text flat files.");
            return;
        }
    }

    // load the found columns into all comboboxes
    fgBox.setModel(new DefaultComboBoxModel(cols));
    bgBox.setModel(new DefaultComboBoxModel(cols));

    identifierBox.setModel(new DefaultComboBoxModel(cols));

    // flags are optional so they need a "none" option...
    DefaultComboBoxModel flagModel = new DefaultComboBoxModel();
    flagModel.addElement("none");
    for (String col : cols) {
        flagModel.addElement(col);
    }
    flagBox.setModel(flagModel);
}

private String isNumeric(String line, ArrayList<Integer> indices) throws Exception {
        String[] elements = line.split(sep);

        try {
            for (Integer index : indices) {

                 //check whether the line contains less elements than
                // expected
                if ((index > elements.length) && !line.equals("")) {
                    System.out.println("LINE: <"+line+">\ndoes not have enough elements!");
                    System.out.println("ELEM: "+elements.length);
                    throw new Exception("Input data is inconsistent! The line \n<"+line+">\n" +
                            "contains less elements than expected. Please check\n " +
                            "the input data and retry.");

                } else if (line.equals("")){
                    System.out.println("skipping empty line in input");
                    continue;
                }

                if ( !elements[index].matches("^[\\d\\.]+$") ) {
                    if (elements[index].matches("^\\d+\\.\\d+(E|e)(\\+|-)\\d+$")) {
                        //System.out.println("FOUND SCIENTIFIC NOTATION: " + elements[index]);
                        return null;
                    }
                    System.out.println("BAD LINE: <" + line + ">");
                    System.out.println("BAD ELEMENT: <" + elements[index] + ">");
                    return elements[index];
                }
            }
        } catch (Exception e) {
            //DEBUG
            System.out.println("Exception during file check for numeric data");
            System.out.println("LINE:<" + line + ">");
            System.out.println("INDICES" + indices);
            throw e;
        }
        return null;
    }

private String stripHeaderFromFile(int headerIndex, String file) throws IOException {
        String baseFileName = Utilities.extractFileNamePathComponent(file);
        File inputDir = new File(dataModel.getOutputDir(), "input");
        File pathToStrippedCopy = new File(inputDir, baseFileName+".robin");
                //System.getProperty("java.io.tmpdir")+filesep+baseFileName+".robin";
        ArrayList<Integer> columnIndices = new ArrayList<Integer>();

        // open file reader and writer
        BufferedReader reader;
        BufferedWriter writer;
        String line;
        int lineCounter = 1;

        try {
            Charset encoding = Utilities.detectEncodingFromFile(new File(file));
            InputStreamReader r = new InputStreamReader(new FileInputStream(file), encoding);
            reader = new BufferedReader(r);
            writer = new BufferedWriter(new FileWriter(pathToStrippedCopy));

            while((line = reader.readLine()) != null) {

                if (lineCounter == headerIndex) {

                    // split header line into indiv column headers
                    String[] heads = line.replaceAll("\"", "").split(sep);
                    ArrayList<String> headers = new ArrayList<String>();

                    for (String s : heads) {
                        headers.add(s);
                    }

                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("G")));
                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("Gb")));

                    //DEBUG
                    //System.out.println("COLIND: " + columnIndices);
                }

                if (lineCounter >= headerIndex) {

                    // we need to get rid of quotes that might mess up R
                    line = line.replaceAll("\"", "");
                    line = line.replaceAll("'", "");
                    line = line.replaceAll(",", ".");

                    // ...and we need to find out if the data in the chosen
                    // data columns is really numeric
                    if (lineCounter > headerIndex) {

                        // skip empty lines
                        if (line.matches("^\\s+$")) {
                            continue;
                        }
                        
                        String badVal = null;
                        try {
                            badVal = this.isNumeric(line, columnIndices);
                        } catch (Exception e) {
                            SimpleLogger.getLogger(true).logException(e);
                            return null;
                        }
                        if (badVal != null ) {
                            int choice = JOptionPane.showOptionDialog(
                            this,
                            "<html><h2>Inconsistent input data<br></h2>In file: " + file +
                            "<br>Line: " + lineCounter + "<br>"+
                            "The input data contains non-numeric values in columns<br>" +
                            "that should only contain numeric values. Robin will<br>"  +
                            "coerce all values to numeric format eventually introducing<br>"  +
                            "NAs - this might break/interfere with downstream analyses.<br>"  +
                            "We strongly recommend that the input data be checked and<br>"  +
                            "cleaned up. We are not taking any responsibility for the<br>"  +
                            "soundness of results generated from inconsistend data.<br>" +
                            "Bad value: \"" + badVal + "\"<br>" +
                            "<em>Hint</em>: This error can also occur if the input files<br>" +
                            "do not all have the same amount of columns </html>",
                            "Input data warning",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            UIManager.getIcon("OptionPane.warningIcon"),
                            new Object[] { "Cancel","Ignore"},
                            "Cancel"
                            );

                            if (choice == 0) {
                                return null;
                            } else {
                                // generate a warning for the records!
                                dataModel.getWarningsHandler().addWarning(
                                        new Warning("Input data inconsistency",
                                                    "The input data contains non-numeric values in columns" +
                                                    "that should only contain numeric values. Robin will"  +
                                                    "coerce all values to numeric format eventually introducing"  +
                                                    "NAs - this might break/interfere with downstream analyses."  +
                                                    "We strongly recommend that the input data be checked and"  +
                                                    "cleaned up. We are not taking any responsibility for the"  +
                                                    "soundness of results generated from inconsistend data.",
                                                    2));
                            }
                        }
                    }


                    writer.write(line+"\n");
                }
                lineCounter++;
            }
            reader.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }

        return pathToStrippedCopy.getCanonicalPath();
    }

private int findIndexOfFirstDataRow(String dataFilePath, String sepChar) {
        int     firstRow = 0,
                columnNumber = 0,
                lastCol = 0,
                unchangedCounter = 0;

        int     lineCounter = 1;

        BufferedReader rd;
        String line = null;
        String firstLine = null;

        try {
            Charset encoding = Utilities.detectEncodingFromFile(new File(dataFilePath));
            InputStreamReader reader=new InputStreamReader(new FileInputStream(dataFilePath), encoding);
            rd = new BufferedReader(reader);

            while ((line =  rd.readLine()  ) != null) {
                if (lineCounter == 1) {
                    firstLine = line;
                }
                lineCounter++;
                String[] cols = line.split(sepChar);
                columnNumber = cols.length;
                if (columnNumber == lastCol) {
                    unchangedCounter++;
                    if (unchangedCounter == MIN_IDENTICAL_COLUMNNUMBER) {
                        firstRow = lineCounter - MIN_IDENTICAL_COLUMNNUMBER;
                        break;
                    }
                } else {
                    unchangedCounter = 0;
                }
                lastCol = columnNumber;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //DEBUG
        System.out.println("FIRSTLINE="+firstLine);

        if (inputType.equals("agilent") && firstLine.startsWith("FEATURES\tFeatureNum\tRow\tCol")) {
            return firstRow-1 ;
        } else if (inputType.equals("agilent")) {
            return firstRow;
        } else {
            return firstRow-1;
        }
    }

    /* find out whether the entered layout
     * corresponds to the amount of data rows found
     * in the input
     */
    private boolean layoutAndDataConsistent() {
        int dataRows = 0;
        try {
            // count rows ignoring empty and whitespace-only lines
            dataRows = Utilities.countLinesInTextFile(files[0], "^\\s*\\n$") - dataStartIndex;

            //DEBUG
            System.out.println("Number of data rows counted: " + dataRows);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        int gRows = ((Integer)gridRowSpinner.getValue()).intValue();
        int gCols = ((Integer)gridColSpinner.getValue()).intValue();
        int bRows = ((Integer)blockRowSpinner.getValue()).intValue();
        int bCols = ((Integer)blockColSpinner.getValue()).intValue();

        int layoutLength = gRows * gCols * bRows * bCols;

        if (layoutLength != dataRows) {
            return false;
        } else {
            return true;
        }
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bgBox;
    private javax.swing.JSpinner blockColSpinner;
    private javax.swing.JSpinner blockRowSpinner;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox colSepBox;
    private javax.swing.JPanel columnsPanel;
    private javax.swing.JTable dataSampleTable;
    private javax.swing.JComboBox decSepBox;
    private javax.swing.JCheckBox editLayoutBox;
    private javax.swing.JComboBox fgBox;
    private javax.swing.JComboBox flagBox;
    private javax.swing.JSpinner gridColSpinner;
    private javax.swing.JSpinner gridRowSpinner;
    private javax.swing.JComboBox identifierBox;
    private javax.swing.JComboBox importPresetCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labeljshd;
    private javax.swing.JComboBox layoutPresetCombo;
    private javax.swing.JButton loadLayoutButton;
    private javax.swing.JButton loadPresetButton;
    private javax.swing.JButton loadSampleButton;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox sampleFileBox;
    private javax.swing.JButton saveLayoutButton;
    private javax.swing.JButton savePresetButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
