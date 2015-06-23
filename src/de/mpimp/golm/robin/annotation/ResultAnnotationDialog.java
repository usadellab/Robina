/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ResultAnnotationDialog.java
 *
 * Created on 17.02.2010, 09:57:07
 */
package de.mpimp.golm.robin.annotation;

import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.help.HelpHandler;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.ProgressDialog;
import de.mpimp.golm.robin.misc.FileFilters.MappingFileFilter;
import de.rzpd.mapman.data.ExcelMappingProvider;
import de.rzpd.mapman.data.MappingProvider;
import de.rzpd.mapman.data.TextMappingProvider;
import de.rzpd.mapman.data.XMLMappingProvider;
import de.rzpd.mapman.lock.FileLock;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class ResultAnnotationDialog extends javax.swing.JDialog {

    private File mappingPath;
    private File resultPath;
    private MappingProvider mappingProvider;

    public enum response {

        SKIP_RESPONSE,
        ANNOTATE_RESPONSE;
    };
    private response userChoice;

    /** Creates new form ResultAnnotationDialog */
    public ResultAnnotationDialog(java.awt.Frame parent, boolean modal, File resourcePath, File resultFilePath) {
        super(parent, modal);
        this.mappingPath = resourcePath;
        this.resultPath = resultFilePath;
        initComponents();
        centerOnParent();
        mappingsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        populateMappingsBox();
    }

    private void centerOnParent() {
        if (getParent() == null) return;
        Dimension parentDim = getParent().getSize();
        Point parentLoc = getParent().getLocation();
        Dimension myDim = getSize();

        int myX = (parentDim.width / 2 + parentLoc.x) - myDim.width / 2;
        int myY = (parentDim.height / 2 + parentLoc.y) - myDim.height / 2;

        setLocation(myX, myY);
    }

    public response getUserChoice() {
        return userChoice;
    }

    private void populateMappingsBox() {
        final ProgressDialog mw = new ProgressDialog((java.awt.Frame)this.getParent(), true, true);
        mw.setText("Reading mapping files...");

        SwingWorker mapWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                File[] mappings = mappingPath.listFiles(new FileFilter() {

                    public boolean accept(File pathname) {
                        if ((pathname.getName().toLowerCase().endsWith(".xls"))
                                || (pathname.getName().toLowerCase().endsWith(".txt"))
                                || (pathname.getName().toLowerCase().endsWith(".xml"))
                                || (pathname.getName().toLowerCase().endsWith(".m01"))
                                || (pathname.getName().toLowerCase().endsWith(".m02"))) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Installed Mappings");
                DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                HashMap<String, DefaultMutableTreeNode> speciesNodes = new HashMap<String, DefaultMutableTreeNode>();
                //treeModel.addTreeModelListener(new MyTreeModelListener());       

                int sp = 0, counter = 0;

                for (File m : mappings) {
                    MappingProvider p = getMappingProvider(m);
                    boxModel.addElement(p.getSpecies() + " - " + m.getName());

                    String speciesName = p.getSpecies();
                    if (speciesName == null) {
                        speciesName = "unknown species";
                    }

                    if (!speciesNodes.containsKey(speciesName)) {
                        speciesNodes.put(speciesName, new DefaultMutableTreeNode(speciesName));
                        treeModel.insertNodeInto(speciesNodes.get(speciesName), rootNode, sp);
                        sp++;
                    }

                    DefaultMutableTreeNode mappingNode = new DefaultMutableTreeNode(m.getName());
                    treeModel.insertNodeInto(mappingNode, speciesNodes.get(speciesName), 0);
                    counter++;
                    int prog = (int) (((float) counter / (float) mappings.length) * 100);
                    mw.setProgress(prog);
                    mw.setText("Reading mapping files... (" + prog + "%)");
                }
                mw.setProgress(100);
                //mappingsBox.setModel(boxModel);
                mappingsTree.setModel(treeModel);
                mw.dispose();
                return null;
            }
        };
        mapWorker.execute();
        mw.setVisible(true);
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

        AnnoDialogInfoText = new javax.swing.JLabel();
        skipAnnoButton = new javax.swing.JButton();
        annotateButton = new javax.swing.JButton();
        visitStoreButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mappingsTree = new javax.swing.JTree();
        importNewMappingButton = new javax.swing.JButton();
        RScallQualButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        AnnoDialogInfoText.setText(org.openide.util.NbBundle.getMessage(ResultAnnotationDialog.class, "ResultAnnotationDialog.AnnoDialogInfoText.text")); // NOI18N
        AnnoDialogInfoText.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        getContentPane().add(AnnoDialogInfoText, gridBagConstraints);

        skipAnnoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        skipAnnoButton.setText("Skip");
        skipAnnoButton.setToolTipText("Skip the annotation step");
        skipAnnoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipAnnoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 6, 0);
        getContentPane().add(skipAnnoButton, gridBagConstraints);

        annotateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/gear16.png"))); // NOI18N
        annotateButton.setText("Annotate");
        annotateButton.setToolTipText("Annotate your results using the chosen annotation file");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mappingsTree, org.jdesktop.beansbinding.ELProperty.create("${selectionModel.selectionEmpty}"), annotateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        annotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 6, 17);
        getContentPane().add(annotateButton, gridBagConstraints);

        visitStoreButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/internet.gif"))); // NOI18N
        visitStoreButton.setText(org.openide.util.NbBundle.getMessage(ResultAnnotationDialog.class, "ResultAnnotationDialog.visitStoreButton.text")); // NOI18N
        visitStoreButton.setToolTipText("Open the MapManStore web site in your default browser");
        visitStoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitStoreButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 20, 6, 0);
        getContentPane().add(visitStoreButton, gridBagConstraints);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        mappingsTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(mappingsTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 0, 20);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        importNewMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/openFile.png"))); // NOI18N
        importNewMappingButton.setText(org.openide.util.NbBundle.getMessage(ResultAnnotationDialog.class, "ResultAnnotationDialog.importNewMappingButton.text")); // NOI18N
        importNewMappingButton.setToolTipText("Import a new annotation from file");
        importNewMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importNewMappingButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 6, 0);
        getContentPane().add(importNewMappingButton, gridBagConstraints);

        RScallQualButton.setBackground(new java.awt.Color(214, 232, 255));
        RScallQualButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/questionMark.gif"))); // NOI18N
        RScallQualButton.setText(org.openide.util.NbBundle.getMessage(ResultAnnotationDialog.class, "ResultAnnotationDialog.RScallQualButton.text")); // NOI18N
        RScallQualButton.setActionCommand(org.openide.util.NbBundle.getMessage(ResultAnnotationDialog.class, "ResultAnnotationDialog.RScallQualButton.actionCommand")); // NOI18N
        RScallQualButton.setBorderPainted(false);
        RScallQualButton.setContentAreaFilled(false);
        RScallQualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RScallQualButtonhandleHelpButtonClick(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 20);
        getContentPane().add(RScallQualButton, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void visitStoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitStoreButtonActionPerformed
        try {
            BrowserLauncher.openURL("http://mapman.gabipd.org/web/guest/mapmanstore");
        } catch (UnsupportedOperatingSystemException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BrowserLaunchingExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BrowserLaunchingInitializingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_visitStoreButtonActionPerformed

    private void importNewMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importNewMappingButtonActionPerformed

//        final ProgressDialog mwin = new ProgressDialog(this.getParent(), true, true);
//        mwin.setText("Importing new mappings...");
//        mwin.setIndeterminate(true);

        SwingWorker importWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileFilter(new MappingFileFilter());

                File[] chosenFiles = null;
                if (chooser.showOpenDialog(ResultAnnotationDialog.this.getParent()) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    chosenFiles = chooser.getSelectedFiles();
                    if ((chosenFiles == null) || (chosenFiles.length == 0)) {
                        return null;
                    }


                    for (File sourceFile : chosenFiles) {
                        File targetFile = new File(ResultAnnotationDialog.this.mappingPath, sourceFile.getName());
                        try {
                            Utilities.copyFile(sourceFile, targetFile);
                        } catch (IOException ex) {
                            SimpleLogger.getLogger(true).logException(new Exception("Could not import new mapping file", ex));
                        }
                    }
                    populateMappingsBox();
//                    mwin.dispose();
                    return null;
                } else {
//                    mwin.dispose();
                    return null;
                }
            }
        };
        importWorker.execute();
//        mwin.setVisible(true);



    }//GEN-LAST:event_importNewMappingButtonActionPerformed

    private void annotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotateButtonActionPerformed
        // get the chosen mapping
        //String mappingName = (String)mappingsBox.getSelectedItem();
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) mappingsTree.getSelectionPath().getLastPathComponent();
        if (selectedNode.getChildCount() != 0) {
            new SimpleErrorMessage(this, "You have selected a species - for annotation, please select one of\n"
                    + "the mapping files provided for this species. You can\n"
                    + "open the list off all mappings available by double\n"
                    + "clicking the species name in the tree");
            return;
        }

        String mappingName = (String) (selectedNode.getUserObject());

        //DEBUG
        System.out.println("clicked: " + mappingName);



        File mappingFile = new File(mappingPath, mappingName);

        MessageWindow mwin = new MessageWindow(this, " Loading annotation");
        try {
            mappingProvider = getMappingProvider(mappingFile);

        } catch (OutOfMemoryError memerr) {
            mwin.dispose();
            long heapMaxSize = Runtime.getRuntime().maxMemory();
            //Utilities.longAsMegaBytes(heapMaxSize);
            new SimpleErrorMessage(this, "Robin ran out of memory when trying to load the mapping.\n"
                    + "The maximal memory currently available to Robin is:" + Utilities.longAsMegaBytes(heapMaxSize) + "MB\n"
                    + "You can increase this if your computer has more RAM by opening\n"
                    + "a command window, navigating to the Robin installation directory\n"
                    + "and issuing the command:\n"
                    + "java -jar -Xmx{heapsize}M Robin.exe\n"
                    + "where {heapsize} has to be replaced by the amount of megabytes you\n"
                    + "want to make available to the program.");
            return;

        } catch (Exception e) {
            mwin.dispose();
            SimpleLogger.getLogger(true).logException(e);
            return;
        }
        mwin.dispose();

        MessageWindow mwin2 = new MessageWindow(this, " Annotating results");
        ResultAnnotator anno = new ResultAnnotator(resultPath, mappingProvider);
        mwin2.dispose();

        if (anno.getPercentMapped() < 90) {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Using the mapping you chose, Robin could only\n"
                    + "<html>annotate <span style=\"color:red;\"><b>"
                    + anno.getPercentMappedFormatted()
                    + "%</b></span> of the probes in the result file.</html>\n"
                    + "We would strongly suggest that you check whether the\n"
                    + "correct mapping was chosen and use another mapping\n"
                    + "if appropriate.",
                    "Incomplete annotation coverage",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    UIManager.getIcon("OptionPane.warningIcon"),
                    new Object[]{"Retry", "Ignore"},
                    "Retry");

            if (choice == 0) {
                return;
            } else {
                userChoice = response.ANNOTATE_RESPONSE;
                this.setVisible(false);
            }
        }

        JOptionPane.showMessageDialog(this,
                "<html><span style=\"color:green;\"><b>" + anno.getPercentMappedFormatted() + "%</b></span> of the identifiers could</html>\n"
                + "be annotated using the mapping file you chose\n\n"
                + "Lines processed : " + anno.getLineCounter() + "\n"
                + "IDs mapped       : " + anno.getMappedCounter() + "\n"
                + "IDs not mapped : " + anno.getNotMappedCounter(),
                "Mapping successful",
                JOptionPane.INFORMATION_MESSAGE);

        userChoice = response.ANNOTATE_RESPONSE;
        this.setVisible(false);
    }//GEN-LAST:event_annotateButtonActionPerformed

    private void skipAnnoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipAnnoButtonActionPerformed
        userChoice = response.SKIP_RESPONSE;
        this.setVisible(false);
    }//GEN-LAST:event_skipAnnoButtonActionPerformed

private void RScallQualButtonhandleHelpButtonClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RScallQualButtonhandleHelpButtonClick
    HelpHandler.getHandler().showHelpDialogForKey("rnaseqhelp.ANNOTATION", null);
}//GEN-LAST:event_RScallQualButtonhandleHelpButtonClick

    private MappingProvider getMappingProvider(File mapfile) {

        MappingProvider mappingProvider = null;
        String mappingName = mapfile.getName();

        // check mapping type and load
        try {
            FileInputStream fileInputStream = new FileInputStream(mapfile);

            if (mappingName.toLowerCase().endsWith(".xls")
                    || mappingName.toLowerCase().endsWith(".m01")) {
                mappingProvider = new ExcelMappingProvider(mappingName,
                        FileLock.unlockStream(fileInputStream));
            } else if (mappingName.toLowerCase().endsWith(".xml")
                    || mappingName.toLowerCase().endsWith(".m02")) {
                mappingProvider = new XMLMappingProvider(mappingName,
                        FileLock.unlockStream(fileInputStream));
            } else if (mappingName.toLowerCase().endsWith(".txt")) {
                mappingProvider = new TextMappingProvider(mappingName,
                        fileInputStream);
            } else {
                throw new Exception("File format not supported: '" + mappingName
                        + "'!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappingProvider;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                File startPath = new File("installers/packs/mappings");
                File resultPath = new File("/Users/marc/Desktop/SCTESTSESTESET/SCTESTSESTESET_results.txt");

                ResultAnnotationDialog dialog = new ResultAnnotationDialog(new javax.swing.JFrame(), false, startPath, resultPath);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AnnoDialogInfoText;
    private javax.swing.JButton RScallQualButton;
    private javax.swing.JButton annotateButton;
    private javax.swing.JButton importNewMappingButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree mappingsTree;
    private javax.swing.JButton skipAnnoButton;
    private javax.swing.JButton visitStoreButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
