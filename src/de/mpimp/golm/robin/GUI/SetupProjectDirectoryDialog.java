/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * setupProjectDirectoryDialog.java
 *
 * Created on 10.02.2010, 16:03:25
 */
package de.mpimp.golm.robin.GUI;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqResultBrowser;
import de.mpimp.golm.robin.ProjectType;
import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.apache.commons.io.FileSystemUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class SetupProjectDirectoryDialog extends javax.swing.JDialog {

    public enum response {

        CANCEL_RESPONSE,
        ACCEPT_RESPONSE
    };
    public response userChoice;
    public File projectPath;
    private ProjectType.TYPE projectType;
    private boolean importing;
    private ArrayList<String> importFiles;
    public File groupListFile;
    public File TCtargetsFile;
    private File importProjectFile;

    /** Creates new form setupProjectDirectoryDialog */
    public SetupProjectDirectoryDialog(java.awt.Frame parent, boolean modal, ProjectType.TYPE type) {
        super(parent, modal);
        importing = false;
        initComponents();
        centerOnParent();
        projectType = type;
    }

    public File getImportProjectFile() {
        return importProjectFile;
    }

    public response getUserChoice() {
        return userChoice;
    }

    public File getProjectPath() {
        return projectPath;
    }

    public boolean isImporting() {
        return importing;
    }

    public ArrayList<String> getImportFiles() {
        return importFiles;
    }

    public File getGroupListFile() {
        return groupListFile;
    }

    public File getTargetsFile() {
        return TCtargetsFile;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        continueButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pathField = new javax.swing.JTextField();
        freeBytesLabel = new javax.swing.JLabel();
        newProjectButton = new javax.swing.JButton();
        chooseProjDirButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        continueButton.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.continueButton.text")); // NOI18N
        continueButton.setEnabled(false);
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });
        continueButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                continueButtonKeyPressed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.jPanel1.border.title"))); // NOI18N

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()-2f));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.jLabel2.text")); // NOI18N

        pathField.setEditable(false);
        pathField.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.pathField.text")); // NOI18N

        freeBytesLabel.setFont(freeBytesLabel.getFont().deriveFont(freeBytesLabel.getFont().getSize()-2f));
        freeBytesLabel.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.freeBytesLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(freeBytesLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(pathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(pathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(freeBytesLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE))
                .addContainerGap())
        );

        newProjectButton.setFont(new java.awt.Font("Lucida Grande", 1, 15));
        newProjectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/folder-new.png"))); // NOI18N
        newProjectButton.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.newProjectButton.text")); // NOI18N
        newProjectButton.setToolTipText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.newProjectButton.toolTipText")); // NOI18N
        newProjectButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        newProjectButton.setBorderPainted(false);
        newProjectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newProjectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newProjectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                newProjectButtonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newProjectButtonMouseEntered(evt);
            }
        });
        newProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseProjDirButtonActionPerformed(evt);
            }
        });

        chooseProjDirButton.setFont(new java.awt.Font("Lucida Grande", 1, 15));
        chooseProjDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/folder-open.png"))); // NOI18N
        chooseProjDirButton.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.chooseProjDirButton.text")); // NOI18N
        chooseProjDirButton.setToolTipText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.chooseProjDirButton.toolTipText")); // NOI18N
        chooseProjDirButton.setActionCommand(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.chooseProjDirButton.actionCommand")); // NOI18N
        chooseProjDirButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chooseProjDirButton.setBorderPainted(false);
        chooseProjDirButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chooseProjDirButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chooseProjDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseProjDirButtonActionPerformed(evt);
            }
        });
        chooseProjDirButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                chooseProjDirButtonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                chooseProjDirButtonMouseEntered(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 3, 18)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.jLabel3.text")); // NOI18N

        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        infoLabel.setText(org.openide.util.NbBundle.getMessage(SetupProjectDirectoryDialog.class, "SetupProjectDirectoryDialog.infoLabel.text")); // NOI18N
        infoLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        infoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        infoLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(135, 135, 135)
                        .add(jLabel3))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(newProjectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(chooseProjDirButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(cancelButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(continueButton))))
                    .add(layout.createSequentialGroup()
                        .add(44, 44, 44)
                        .add(infoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 348, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(newProjectButton)
                    .add(chooseProjDirButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(infoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(continueButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chooseProjDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseProjDirButtonActionPerformed

        // the directory chooser will produce a more native-like user experience under windows
        // but it sucks when trying to list network shares - switch back to AWT for now?

        File outputPath = null;
        String JVMarch = System.getProperty("sun.arch.data.model");
        // DEBUG
        System.out.println("sun.arch.data.model=" + JVMarch);

        // DEACTIVATED CALLING INTO THE WIN32 SYSTEM DIALOGS SINCE IT WAS UBNSTABLE ACROSS
        // WINDOWS VERSIONS (WHO WOULD HAVE EXPECTED THAT TO HAPPEN?)


//        if (Utilities.isWindows() && JVMarch.equals("32") ) {
//            // since the java file chooser suck for windows, we
//            // try and use the native dialogs. Ave caesar morituri te salutant...
//
//            //DEBUG
//            System.out.println("entering native windows32 directory chooser dialog invocation");
//
//             try {
//                HWND foregroundWindow = WinUtilsUser32.getForegroundWindow();
//
//                DWORD flags = new DWORD(BrowseInfo.BIF_STATUSTEXT +
//                                        BrowseInfo.BIF_NEWDIALOGSTYLE +
//                                        BrowseInfo.BIF_UAHINT);
//
//
//                WinUtilsShell32.ChooseFolderResult r = WinUtilsShell32.SHBrowseForFolder(foregroundWindow,
//                        flags,
//                        "Please specify the project folder", "C:\\Documents and Settings\\");
//                if (r.success) {
//                    outputPath =  r.chosenDirectory;
//                    pathField.setText(outputPath.getCanonicalPath());
//                    long freeKB = FileSystemUtils.freeSpaceKb(outputPath.getParentFile().getCanonicalPath());
//                    NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
//                    nf.setMaximumFractionDigits(2);
//                    String freeMB = nf.format(Utilities.longAsKiloBytes(freeKB));
//                    freeBytesLabel.setText(freeMB + " MB");
//                } else {
//                    new em(this.getParent(), "User cancelled or chose non-standard folder");
//                }
//            } catch (NativeException ex) {
//                Logger.getLogger(WinUtilsComDlg32.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(WinUtilsComDlg32.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                // something bigger went wrong - probably some imcompatibility issue
//                // of JNative. We should fall back to using the java JFileChooser
//                e.printStackTrace();  
//            }
//        
//        } else 
        if (Utilities.isMacOSX()) {
            // ... awt does not provide a directory chooser...unfortunately
            java.awt.FileDialog pdChooser = new java.awt.FileDialog(this, "Please choose project directory", FileDialog.LOAD);
            pdChooser.setDirectory(RobinConstants.lastDirChooserPath);

            // this might fix it on a mac
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            pdChooser.setVisible(true);
            System.setProperty("apple.awt.fileDialogForDirectories", "false");

            outputPath = new File(pdChooser.getDirectory(), pdChooser.getFile());

            if (!outputPath.isDirectory()) {
                new SimpleErrorMessage(this, "Please chose a directory");
                return;
            }
            try {
                if ((outputPath == null) || (outputPath.getCanonicalPath().equals(""))) {
                    new SimpleErrorMessage(this.getParent(), "Please specify a name for the output folder");
                    return;
                }
                pathField.setText(outputPath.getCanonicalPath());
                long freeKB = FileSystemUtils.freeSpaceKb(outputPath.getParentFile().getCanonicalPath());
                RobinConstants.lastDirChooserPath = outputPath.getParentFile().getCanonicalPath();
                NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
                nf.setMaximumFractionDigits(2);
                String freeMB = nf.format(Utilities.longAsKiloBytes(freeKB));
                freeBytesLabel.setText(freeMB + " MB");
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else { // this is for linux and any other java enabled platform and non 32bit windows JVM (these do not support JNative)

            JFileChooser dirChooser = new JFileChooser(RobinConstants.lastDirChooserPath);
            dirChooser.setDialogTitle("Please choose project directory");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            // might need further tweaking?
            if (dirChooser.showSaveDialog(this.getParent()) == JFileChooser.APPROVE_OPTION) {
                outputPath = dirChooser.getSelectedFile();
                try {
                    //DEBUG
                    System.out.println("OUTPUTPATH:" + outputPath.getCanonicalPath());
                    // path is null
                    if ((outputPath == null) || (outputPath.getCanonicalPath().equals(""))) {
                        new SimpleErrorMessage(this.getParent(), "Please specify a name for the output folder");
                        return;
                    }
                    pathField.setText(outputPath.getCanonicalPath());
                    RobinConstants.lastDirChooserPath =outputPath.getParentFile().getCanonicalPath();
                    long freeKB = FileSystemUtils.freeSpaceKb(outputPath.getParentFile().getCanonicalPath());
                    NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
                    nf.setMaximumFractionDigits(2);
                    String freeMB = nf.format(Utilities.longAsKiloBytes(freeKB));
                    freeBytesLabel.setText(freeMB + " MB");
                } catch (IOException ex) {
                    // the heck knows how this happened...
                    SimpleLogger.getLogger(true).logException(ex);
                    return;
                }
            } else {
                return;
            }
        }


        // is the user trying to import an existing path?
        if (outputPath.exists() && evt.getActionCommand().equals("open")) {
            //DEBUG
            System.out.println("import existing project");

            // is this an affy or sc project?
            File sourcePath = new File(outputPath, "source");
            File groupsFile = new File(sourcePath, outputPath.getName() + "_groupList.xml");
            // ... or a tc project
            File targetsFile = new File(outputPath, outputPath.getName() + "_targets.txt");

            ProjectType.TYPE projType = getProjectType(sourcePath);

            if (projType != projectType) {
                new SimpleErrorMessage(this, "You are trying to import an existing " + ProjectType.typeToString(projType) + " project\n"
                        + "into the " + ProjectType.typeToString(projectType) + " workflow. Please make\n"
                        + "sure you import compatible data.");
                continueButton.setEnabled(false);
                pathField.setText("");
                return;
            }
            importProjectFile = new File(sourcePath, outputPath.getName() + "_data.xml");

            if (groupsFile.exists()) {
                //DEBUG
                System.out.println("affy or sc import");
                try {
                    String newName = JOptionPane.showInputDialog(this, "You are reloading an existing project.\n"
                            + "All input files, settings and group resp. target\n"
                            + "definitions will be imported from the project\n"
                            + outputPath.getCanonicalPath() + ".\n"
                            + "You can freely modify all settings and add more data files.\n"
                            + "Please enter a name for the new project - the name will be\n"
                            + "appended to the old project's name.\n");

                    projectPath = new File(outputPath.getCanonicalPath() + "_" + newName);
                    pathField.setText(projectPath.getCanonicalPath());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                continueButton.setEnabled(true);
                importing = true;
                groupListFile = groupsFile;
                try {
                    importFiles = extractInputFilesFromGroupList(groupsFile);
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (DocumentException ex) {
                    Exceptions.printStackTrace(ex);
                }

            } else if (targetsFile.exists()) {
                //DEBUG
                System.out.println("tc import");

                try {
                    String newName = JOptionPane.showInputDialog(this, "You are reloading an existing project.\n"
                            + "All input files, settings and group resp. target\n"
                            + "definitions will be imported from the project\n"
                            + outputPath.getCanonicalPath() + ".\n"
                            + "You can freely modify all settings and add more data files.\n"
                            + "Please enter a name for the new project - the name will be\n"
                            + "appended to the old project's name.\n");
                    projectPath = new File(outputPath.getCanonicalPath() + "_" + newName);
                    pathField.setText(projectPath.getCanonicalPath());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                continueButton.setEnabled(true);
                importing = true;
                TCtargetsFile = targetsFile;
                try {
                    importFiles = extractInputFilesFromTargetsFile(targetsFile);
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            } else if (projType == ProjectType.TYPE.RNA_SEQ) {

                RNASeqDataModel data = new RNASeqDataModel();
                data.setProjectDir(outputPath);
                File sourceDir = new File(outputPath, "source");
                try {
                    File dataFile = new File(sourceDir, outputPath.getName() + "_data.xml");
                    
                    if (!dataFile.exists()) {
                        new SimpleErrorMessage(this, "The folder you have chosen does not appear to be a\n"
                                + "valid RobiNA project. Please try again and make sure to\n"
                                + "select a valid project folder for import.\n"
                                + "(The project description file\n"
                                + dataFile.getCanonicalPath() + "\n"
                                + "was not found.)");
                        return;                                
                    }
                    
                    data.load(dataFile);
                } catch (Exception ex) {
                    SimpleLogger.getLogger(true).logException(ex);                            
                }

                if (data.getWorkflowStage() == RNASeqDataModel.RNASEQ_WORKFLOW_STAGE.COMPLETE_ANALYSIS) {
                    if (browseResultsOnly(outputPath)) {
                        this.dispose();
                        return;
                    } 
                }

                SimpleLogger.getLogger(true).logMessage("RNA-Seq project import");
                try {
                    String newName = JOptionPane.showInputDialog(this, "You are reloading an existing project.\n"
                            + "All input files, settings and group definitions\n"
                            + "will be imported from the project\n"
                            + outputPath.getCanonicalPath() + ".\n"
                            + "You can freely modify all settings and add more data files.\n"
                            + "Please enter a name for the new project - the name will be\n"
                            + "appended to the old project's name.\n");
                    this.projectPath = new File(outputPath.getCanonicalPath() + "_" + newName);
                    this.pathField.setText(projectPath.getCanonicalPath());
                    this.importing = true;
                    
                    // if the mapping step was already executed and there's a
                    // count table - copy it over to enable skipping
                    File countsTable = new File(data.getDetailedResultsDir(), data.getExperimentName() + "_raw_countstable.txt");
                    if (countsTable.exists()) {
                        File destinationFolder = new File(projectPath, "detailed_results");
                        destinationFolder.mkdirs();
                        File destFile = new File(destinationFolder, projectPath.getName() + "_raw_countstable.txt");
                        Utilities.copyFile(countsTable, destFile);
                    }                    
                    
                    continueButton.setEnabled(true);
                } catch (IOException ex) {
                    SimpleLogger.getLogger(true).logException(ex);
                }
            } else {
                new SimpleErrorMessage(this, "The folder you specified is not a valid project.");
                continueButton.setEnabled(false);
                pathField.setText("");
                return;
            }
        } else {
            // is new path
            continueButton.setEnabled(true);
            projectPath = outputPath;
            continueButton.requestFocusInWindow();
            continueButton.setSelected(true);
        }
    }//GEN-LAST:event_chooseProjDirButtonActionPerformed

    private void showOpenExistingDialog(String oldProject) {
        JOptionPane.showConfirmDialog(this, "You are reloading an existing project.\n"
                + "All input files, settings and group resp. target\n"
                + "definitions will be imported from the project\n"
                + oldProject + ".\n"
                + "You can freely modify all settings and add more data files.\n"
                + "The results will be written to a new folder.");
    }

    private ArrayList<String> extractInputFilesFromTargetsFile(File targetsList) throws FileNotFoundException, IOException {
        ArrayList<String> importFilesList = new ArrayList<String>();
        String line;

        Charset encoding = Utilities.detectEncodingFromFile(targetsList);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(targetsList), encoding);
        BufferedReader rd = new BufferedReader(reader);

        while ((line = rd.readLine()) != null) {
            String[] cols = line.split("\\t");
            if (cols[1].equals("FileName")) {
                continue;
            }
            importFilesList.add(cols[1]);
        }
        return importFilesList;
    }

    private ArrayList<String> extractInputFilesFromGroupList(File groupList) throws FileNotFoundException, DocumentException {
        ArrayList<String> importFilesList = new ArrayList<String>();
        FileReader reader = new FileReader(groupList);
        Element groupDef = new SAXReader().read(reader).getRootElement();

        Iterator<Element> groupIter = groupDef.elementIterator("group");
        while (groupIter.hasNext()) {
            Element group = groupIter.next();
            Iterator<Element> fileIter = group.elementIterator("file");
            while (fileIter.hasNext()) {
                Element file = fileIter.next();
                String path = file.attributeValue("path");
                importFilesList.add(path);
            }
        }
        return importFilesList;
    }

    public ProjectType.TYPE getProjectType(File sourcePath) {
        if (new File(sourcePath, "affy.type").exists()) {
            return ProjectType.TYPE.AFFYMETRIX;
        } else if (new File(sourcePath, "generic_sc.type").exists()) {
            return ProjectType.TYPE.SINGLE_CHANNEL;
        } else if (new File(sourcePath, "generic_tc.type").exists()) {
            return ProjectType.TYPE.TWO_COLOR;
        } else if (new File(sourcePath, "rna_seq.type").exists()) {
            return ProjectType.TYPE.RNA_SEQ;
        } else {
            return null;
        }
    }

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        userChoice = response.ACCEPT_RESPONSE;

        File outputPath = this.getProjectPath();

        try {
            //DEBUG
            System.out.println("OUTPUTPATH:" + outputPath.getCanonicalPath());
        } catch (IOException ex) {
            // the heck knows how this happened...
            SimpleLogger.getLogger(true).logException(ex);
            return;
        }
        try {
            if ((outputPath == null) || (outputPath.getCanonicalPath().equals(""))) {
                new SimpleErrorMessage(this, "Please specify a name for the output folder");
                return;
            }
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        MessageWindow mwin = new MessageWindow(this, " Setting up project...");

        // create the path
        if (!outputPath.exists()) {
            boolean res = outputPath.mkdir();
            if (!res) {
                new SimpleErrorMessage(this, "The specified output folder could not be created");
            }
        }

        // create the substructure
        String sFileSep = System.getProperty("file.separator");
        try {

            // create the subdirectory structure
            boolean mkDirOK = false;
            File sourceDir = new File(outputPath, "source");
            mkDirOK = sourceDir.mkdir();

            // create plots directory
            File plotsDir = new File(outputPath, "plots");
            mkDirOK = plotsDir.mkdir();

            if (!(projectType == ProjectType.TYPE.AFFYMETRIX)) {
                // create input directory for cleaned-up copies of the raw input data
                File inputDir = new File(outputPath, "input");
                mkDirOK = inputDir.mkdir();
            }

            // create plots directory
            File qualDir = new File(outputPath, "qualitychecks");
            mkDirOK = qualDir.mkdir();

            // create detailed_results directory
            File detailsDir = new File(outputPath, "detailed_results");
            if (!detailsDir.exists()) {
                mkDirOK = detailsDir.mkdir();
            }

            if (!mkDirOK) {
                new SimpleErrorMessage(this, "Could not create directory substructure.\nPlease check if directory already exists.");
                return;
            }

            RScriptGenerator.copyLibs(RobinConstants.STANDARD_R_EXTENSIONS, projectPath);

        } catch (Exception ex) {
            SimpleLogger.getLogger(true).logException(new Exception("Could not create directory substructure.", ex));
            return;
        }
        mwin.dispose();
        this.setVisible(false);
    }//GEN-LAST:event_continueButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        userChoice = response.CANCEL_RESPONSE;
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void newProjectButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newProjectButtonMouseEntered
        infoLabel.setText("<html><small><p style=\"color: gray;\">The first step of the workflow will be"
                + "to choose a project directory in which all files related to<br>"
                + "the analysis will be stored. Please make sure that the chosen<br>"
                + "target directory will be on a volume (hard drive, USB stick etc.)<br>"
                + "that provides enough free space to possibly accomodate your<br>"
                + "raw data since Robin will make working copies of the data in case<br>"
                + "processing of the raw files is necessary prior to analysis</p></small></html>");
        newProjectButton.setBorderPainted(true);
    }//GEN-LAST:event_newProjectButtonMouseEntered

    private void newProjectButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newProjectButtonMouseExited
        infoLabel.setText("");
        newProjectButton.setBorderPainted(false);
    }//GEN-LAST:event_newProjectButtonMouseExited

    private void chooseProjDirButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chooseProjDirButtonMouseEntered
        infoLabel.setText("<html><small><p style=\"color: gray;\">Use this option is you did already analyse a dataset and<br>"
                + "would like to rerun the analysis using different settings or including<br>"
                + "new files. When adding more raw data files, please make sure that the<br>"
                + "files are of the same format and type.<br>"
                + "</p></small></html>");
        chooseProjDirButton.setBorderPainted(true);
    }//GEN-LAST:event_chooseProjDirButtonMouseEntered

    private void chooseProjDirButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chooseProjDirButtonMouseExited
        infoLabel.setText("");
        chooseProjDirButton.setBorderPainted(false);
    }//GEN-LAST:event_chooseProjDirButtonMouseExited

private void continueButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_continueButtonKeyPressed
    continueButtonActionPerformed(null);
}//GEN-LAST:event_continueButtonKeyPressed

    private void centerOnParent() {
        Dimension parentDim = getParent().getSize();
        Point parentLoc = getParent().getLocation();
        Dimension myDim = getSize();

        int myX = (parentDim.width / 2 + parentLoc.x) - myDim.width / 2;
        int myY = (parentDim.height / 2 + parentLoc.y) - myDim.height / 2;

        setLocation(myX, myY);
    }

    private boolean browseResultsOnly(File projectPath) {
        int choice = JOptionPane.showOptionDialog(
                this,
                "You are about to import a completed analysis.\n"
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
            return false;
        }

        final MessageWindow mwin = new MessageWindow(this, "Generating project summary...");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        RNASeqDataModel data = new RNASeqDataModel();
        data.setProjectDir(projectPath);
        File sourceDir = new File(projectPath, "source");
        try {
            data.load(new File(sourceDir, projectPath.getName() + "_data.xml"));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        RNASeqResultBrowser browser = new RNASeqResultBrowser(null, data);
        frame.add(browser);
        frame.pack();
        frame.setVisible(true);
        mwin.dispose();
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton chooseProjDirButton;
    private javax.swing.JButton continueButton;
    private javax.swing.JLabel freeBytesLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton newProjectButton;
    private javax.swing.JTextField pathField;
    // End of variables declaration//GEN-END:variables
}
