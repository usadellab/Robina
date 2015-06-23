/*
 * TCGenericImportDialog.java
 *
 * Created on 22. Oktober 2008, 17:30
 */

package de.mpimp.golm.robin.GUI.twocolor;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.robin.warnings.Warning;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
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
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * @author  marc
 */
public class TCGenericImportDialog extends javax.swing.JDialog {
    
    private final static int MIN_IDENTICAL_COLUMNNUMBER = 100;
    private TCArrayDataModel dataModel;
    private String sep;
    private JList importList;
    private File presetRoot;
    private String filesep = System.getProperty("file.separator");
    private response userChoice;
    private String inputType;
    
    public enum response {
        OK_RESPONSE,
        CANCEL_RESPONSE
    }
    
    /** Creates new form TCGenericImportDialog
     * @param parent
     * @param modal
     * @param model 
     */
    public TCGenericImportDialog(   java.awt.Frame parent, 
                                    boolean modal, 
                                    TCArrayDataModel model, 
                                    JList importFileList,
                                    String input) {
        super(parent, modal);
        initComponents();
        this.dataModel = model;
        dataSamplePanel.setVisible(false);
        columnsPanel.setVisible(false);
        printerLayoutPanel.setVisible(false);
        cancelButton.setVisible(false);
        OKButton.setVisible(false);
        this.importList = importFileList;
        this.inputType = input;
        
        // initialize sample file box
        sampleFileBox.setModel(new DefaultComboBoxModel(dataModel.getInputFiles().toArray()));
        
        // ... and load the first file as data sample using the standard separator
        // and decimal separator settings. This will produce messy situations if the
        // data is not standard...
        loadSampleButtonActionPerformed(null);
        
        // load presets and populate the presets box
        loadLayoutPresets();
        
        //load import settings presets
        populateImportSettingsBox();
        
        this.centerOnParent();
        this.setBackground(java.awt.SystemColor.window);
        this.setVisible(true);
    }

    public response getUserChoice() {
        return userChoice;
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
            
            int headerIndex = findIndexOfFirstDataRow(file, sep);
            String pathToStrippedCopy = stripHeaderFromFile(headerIndex, file);
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
            //DEBUG
            System.out.println("clean file -->" +cFile);
            dataModel.getInputFiles().add(cFile);
        }   
    }
    
    private String isNumeric(String line, ArrayList<Integer> indices) throws Exception {
        String[] elements = line.split(sep);
        
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
        return null;
    }
    
    private String stripHeaderFromFile(int headerIndex, String file) throws IOException {
        String baseFileName = Utilities.extractFileNamePathComponent(file);
        File inputDir = new File(dataModel.getOutputDir(), "input");
        File pathToStrippedCopy = new File(inputDir, baseFileName+".robin");
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
                    // remove quotes from headers. we will accept them in all
                    // other fields, though  - maybe we shouldn't
                    String[] heads = line.replaceAll("\"", "").split(sep); //FIXME we should split on the user-chosen separator here!
                    ArrayList<String> headers = new ArrayList<String>();
                    
                    for (String s : heads) {
                        headers.add(s);
                    }
                                                          
                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("R")));
                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("Rb")));
                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("G")));
                    columnIndices.add(headers.indexOf((String) dataModel.getGenericColumns().get("Gb")));
                    
                    //DEBUG
                    System.out.println("COLIND: " + columnIndices);
                }
                
                if (lineCounter >= headerIndex) {
                    
                    // we need to get rid of quotes that might mess up R
                    line = line.replaceAll("\"", "");
                    line = line.replaceAll("'", "");
                    line = line.replaceAll(",", ".");
                    
                    // ...and we need to find out if the data in the chosen
                    // data columns is really numeric 
                    if (lineCounter > headerIndex) {

                        //skip empty lines
                        if (line.matches("^\\s+$")) {
                            continue;
                        }

                        String badVal = null;
                        try {
                            badVal= this.isNumeric(line, columnIndices);
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
    
    private void loadLayoutPresets() {
        // we store the presets as saved property lists in lib/presets?
        
        //String instPath = Utilities.getInstallPath((RobinMainGUI) this.getParent(), "Robin2.jar");        
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
                return name.endsWith(".layout");
            }
        };
        
        String[] presetList = presetRoot.list(filter);
        presetModel.addElement("none");
        for (String preset : presetList) {
            preset = preset.replaceAll("\\.layout", "");
            presetModel.addElement(preset);
        }
        layoutPresetBox.setModel(presetModel);
    }
    
    private void centerOnParent() {
        Dimension parentDim = getParent().getSize();
        Point parentLoc = getParent().getLocation();
        Dimension myDim = getSize();
        
        int myX = (parentDim.width / 2 + parentLoc.x) - myDim.width / 2;
        int myY = (parentDim.height / 2 + parentLoc.y) - myDim.height / 2;
        
        setLocation(myX, myY);        
    }
    
    private void loadDataSample(String dataFilePath, String sep) {
        BufferedReader rd;
        String line;
        int lineCounter = 1, sampleRowCounter = 0;
        int headerIndex = findIndexOfFirstDataRow(dataFilePath, sep);
        DefaultTableModel tableModel = new DefaultTableModel(); 
        String[] cols = null;
        
        try {
            Charset encoding = Utilities.detectEncodingFromFile(new File(dataFilePath));
            InputStreamReader reader=new InputStreamReader(new FileInputStream(dataFilePath), encoding);
            rd = new BufferedReader(reader);

            while ((line =  rd.readLine()  ) != null) {
                
                if (lineCounter == headerIndex) {
                    
                    // we need to get rid of quotes that would mess up R
                    line = line.replaceAll("\"", "");
                    line = line.replaceAll("'", "");

                    // extract column headers
                    cols = line.split(sep);
                    tableModel.setColumnIdentifiers(cols);
                    
                } else if (lineCounter == headerIndex+50) {
                    // finished loading 50 lines
                    dataSampleTable.setModel(tableModel);
                    break;
                    
                 } else if (lineCounter > headerIndex) {
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
            new SimpleErrorMessage(this, "Could not load data sample.\n"+
                         "Please make sure that the input data is\n" +
                         "in tabular text flat files.");
            return;
        }
        
        // load the found columns into all comboboxes
        RfBox.setModel(new DefaultComboBoxModel(cols));
        RbBox.setModel(new DefaultComboBoxModel(cols));
        GfBox.setModel(new DefaultComboBoxModel(cols));
        GbBox.setModel(new DefaultComboBoxModel(cols));
        identifierBox.setModel(new DefaultComboBoxModel(cols));
        
        // flags are optional so they need a "none" option...
        DefaultComboBoxModel flagModel = new DefaultComboBoxModel();
        flagModel.addElement("none");
        for (String col : cols) {
            flagModel.addElement(col);
        }
        flagBox.setModel(flagModel);       
        
    }
    
    private int findIndexOfFirstDataRow(String dataFilePath, String sepChar) {
        int     firstRow = 0, 
                columnNumber = 0, 
                lastCol = 0, 
                unchangedCounter = 0;
        int lineCounter = 1;
        BufferedReader rd;
        String line;
        
        try {
            Charset encoding = Utilities.detectEncodingFromFile(new File(dataFilePath));
            InputStreamReader reader=new InputStreamReader(new FileInputStream(dataFilePath), encoding);
            rd = new BufferedReader(reader);

            while ((line =  rd.readLine()  ) != null) {
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

        if (this.inputType.equals("agilent")) {
            return firstRow;
        } else {
            return firstRow-1;
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

        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        dataSamplePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataSampleTable = new javax.swing.JTable();
        OKButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        colSepBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        decSepBox = new javax.swing.JComboBox();
        loadSampleButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        sampleFileBox = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        loadSettingsButton = new javax.swing.JButton();
        importSettingsBox = new javax.swing.JComboBox();
        saveSettingsButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        columnsPanel = new javax.swing.JPanel();
        gfLabel = new javax.swing.JLabel();
        rfLabel = new javax.swing.JLabel();
        gbLabel = new javax.swing.JLabel();
        rbLabel = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        flagLabel = new javax.swing.JLabel();
        identifierBox = new javax.swing.JComboBox();
        RfBox = new javax.swing.JComboBox();
        RbBox = new javax.swing.JComboBox();
        GfBox = new javax.swing.JComboBox();
        GbBox = new javax.swing.JComboBox();
        flagBox = new javax.swing.JComboBox();
        printerLayoutPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        layoutPresetBox = new javax.swing.JComboBox();
        saveLayoutPresetButton = new javax.swing.JButton();
        saveLayoutPresetButton1 = new javax.swing.JButton();
        editLayoutBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        ngridCBox = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        nspotCBox = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        ngridRBox = new javax.swing.JSpinner();
        nspotRBox = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.title")); // NOI18N

        jLabel9.setText("<html><h2>Import of generic data</h2> \nWhen you click \"Load data sample\", a table showing the first 50 rows of the chosen sample file that are assumed to contain data will open. \nRobin requires <span style=\"color: blue; font-weight: bold;\">all generic-type input data</span> in one analysis run to have the <span style=\"color: blue; font-weight: bold;\">same \nformat</span> so please make sure this is provided for. If your data files contain header \ninformation before the actual data section Robin tries to figure out where the data starts.\n However this might fail in some cases. If it fails you could try removing the headers  \nmanually from copies of your original files and import the modified copies. \nInput files need to be in <span style=\"color: blue; font-weight: bold;\">text format</span>.</html>");

        cancelButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        dataSamplePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.dataSamplePanel.border.title"))); // NOI18N

        dataSampleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        dataSampleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataSampleTable.setEnabled(false);
        jScrollPane1.setViewportView(dataSampleTable);

        org.jdesktop.layout.GroupLayout dataSamplePanelLayout = new org.jdesktop.layout.GroupLayout(dataSamplePanel);
        dataSamplePanel.setLayout(dataSamplePanelLayout);
        dataSamplePanelLayout.setHorizontalGroup(
            dataSamplePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
        );
        dataSamplePanelLayout.setVerticalGroup(
            dataSamplePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
        );

        OKButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.OKButton.text")); // NOI18N
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jPanel3.border.title"))); // NOI18N
        jPanel3.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 170));

        jLabel12.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel12.text")); // NOI18N

        colSepBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tab (\\t)", "Comma (,)", "Semicolon (;)", "Colon (:)", "Pipe (|)", "Hash (#)" }));

        jLabel13.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel13.text")); // NOI18N

        decSepBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Point ( . )", "Comma ( , )" }));

        loadSampleButton.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        loadSampleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/pipette48.png"))); // NOI18N
        loadSampleButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.loadSampleButton.text")); // NOI18N
        loadSampleButton.setIconTextGap(20);
        loadSampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSampleButtonActionPerformed(evt);
            }
        });

        jLabel14.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel14.text")); // NOI18N

        sampleFileBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        sampleFileBox.setMaximumSize(new java.awt.Dimension(165, 32767));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, loadSampleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel14)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sampleFileBox, 0, 274, Short.MAX_VALUE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(decSepBox, 0, 235, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(colSepBox, 0, 230, Short.MAX_VALUE)))
                .addContainerGap())
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
                        .add(9, 9, 9)
                        .add(jLabel13))
                    .add(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(decSepBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jLabel14))
                    .add(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sampleFileBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loadSampleButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jPanel1.border.title"))); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(200, 2147483647));

        loadSettingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        loadSettingsButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.loadSettingsButton.text")); // NOI18N
        loadSettingsButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.loadSettingsButton.toolTipText")); // NOI18N
        loadSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSettingsButtonActionPerformed(evt);
            }
        });

        importSettingsBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        importSettingsBox.setMinimumSize(new java.awt.Dimension(100, 27));
        importSettingsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSettingsBoxActionPerformed(evt);
            }
        });

        saveSettingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/saveHS.png"))); // NOI18N
        saveSettingsButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveSettingsButton.text")); // NOI18N
        saveSettingsButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveSettingsButton.toolTipText")); // NOI18N
        saveSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSettingsButtonActionPerformed(evt);
            }
        });

        jLabel16.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel16.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel16)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importSettingsBox, 0, 234, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveSettingsButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loadSettingsButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, saveSettingsButton)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(importSettingsBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel16))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, loadSettingsButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        columnsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.columnsPanel.border.title"))); // NOI18N
        columnsPanel.setMaximumSize(new java.awt.Dimension(200, 2147483647));

        gfLabel.setForeground(new java.awt.Color(0, 153, 0));
        gfLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gfLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.gfLabel.text")); // NOI18N

        rfLabel.setForeground(new java.awt.Color(255, 0, 0));
        rfLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rfLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.rfLabel.text")); // NOI18N

        gbLabel.setForeground(new java.awt.Color(0, 153, 0));
        gbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gbLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.gbLabel.text")); // NOI18N

        rbLabel.setForeground(new java.awt.Color(255, 0, 0));
        rbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rbLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.rbLabel.text")); // NOI18N

        idLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        idLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.idLabel.text")); // NOI18N

        flagLabel.setBackground(java.awt.SystemColor.window);
        flagLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        flagLabel.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.flagLabel.text")); // NOI18N

        identifierBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        identifierBox.setMaximumSize(new java.awt.Dimension(100, 100));

        RfBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        RfBox.setMaximumSize(new java.awt.Dimension(100, 100));

        RbBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        RbBox.setMaximumSize(new java.awt.Dimension(100, 100));

        GfBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        GfBox.setMaximumSize(new java.awt.Dimension(100, 100));

        GbBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        GbBox.setMaximumSize(new java.awt.Dimension(100, 100));

        flagBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        flagBox.setMaximumSize(new java.awt.Dimension(100, 100));

        org.jdesktop.layout.GroupLayout columnsPanelLayout = new org.jdesktop.layout.GroupLayout(columnsPanel);
        columnsPanel.setLayout(columnsPanelLayout);
        columnsPanelLayout.setHorizontalGroup(
            columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(columnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, flagLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, gbLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, gfLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, rbLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, rfLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, idLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(RfBox, 0, 181, Short.MAX_VALUE)
                    .add(RbBox, 0, 181, Short.MAX_VALUE)
                    .add(GfBox, 0, 181, Short.MAX_VALUE)
                    .add(GbBox, 0, 181, Short.MAX_VALUE)
                    .add(flagBox, 0, 181, Short.MAX_VALUE)
                    .add(identifierBox, 0, 181, Short.MAX_VALUE))
                .addContainerGap())
        );
        columnsPanelLayout.setVerticalGroup(
            columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(columnsPanelLayout.createSequentialGroup()
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(identifierBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(idLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rfLabel)
                    .add(RfBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbLabel)
                    .add(RbBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gfLabel)
                    .add(GfBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gbLabel)
                    .add(GbBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(flagLabel)
                    .add(flagBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        printerLayoutPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.printerLayoutPanel.border.title"))); // NOI18N
        printerLayoutPanel.setMaximumSize(new java.awt.Dimension(200, 2147483647));

        jLabel15.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel15.text")); // NOI18N

        layoutPresetBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none" }));
        layoutPresetBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutPresetBoxActionPerformed(evt);
            }
        });

        saveLayoutPresetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/saveHS.png"))); // NOI18N
        saveLayoutPresetButton.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveLayoutPresetButton.text")); // NOI18N
        saveLayoutPresetButton.setToolTipText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveLayoutPresetButton.toolTipText")); // NOI18N
        saveLayoutPresetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLayoutPresetButtonActionPerformed(evt);
            }
        });

        saveLayoutPresetButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/resetarrow_16.png"))); // NOI18N
        saveLayoutPresetButton1.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveLayoutPresetButton1.text")); // NOI18N
        saveLayoutPresetButton1.setToolTipText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.saveLayoutPresetButton1.toolTipText")); // NOI18N
        saveLayoutPresetButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLayoutPresetButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layoutPresetBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveLayoutPresetButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(saveLayoutPresetButton1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(layoutPresetBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel15))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, saveLayoutPresetButton)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, saveLayoutPresetButton1))
                .addContainerGap(5, Short.MAX_VALUE))
        );

        editLayoutBox.setSelected(true);
        editLayoutBox.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.editLayoutBox.text")); // NOI18N
        editLayoutBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLayoutBoxActionPerformed(evt);
            }
        });

        ngridCBox.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        jLabel8.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel8.text")); // NOI18N

        nspotCBox.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        jLabel10.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel10.text")); // NOI18N

        ngridRBox.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        nspotRBox.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        jLabel11.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel11.text")); // NOI18N

        jLabel7.setText(org.openide.util.NbBundle.getMessage(TCGenericImportDialog.class, "TCGenericImportDialog.jLabel7.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ngridRBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ngridCBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nspotRBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nspotCBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel7)
                            .add(ngridRBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel8)
                            .add(ngridCBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel10)
                            .add(nspotRBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel11)
                            .add(nspotCBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout printerLayoutPanelLayout = new org.jdesktop.layout.GroupLayout(printerLayoutPanel);
        printerLayoutPanel.setLayout(printerLayoutPanelLayout);
        printerLayoutPanelLayout.setHorizontalGroup(
            printerLayoutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(printerLayoutPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(editLayoutBox))
            .add(printerLayoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        printerLayoutPanelLayout.setVerticalGroup(
            printerLayoutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(printerLayoutPanelLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editLayoutBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
            .add(columnsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(printerLayoutPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(columnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(printerLayoutPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 599, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dataSamplePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(871, Short.MAX_VALUE)
                .add(cancelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(OKButton)
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {OKButton, cancelButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataSamplePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OKButton)
                    .add(cancelButton)))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents




private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed

    
    HashMap<String, String> genCols = new HashMap<String, String>();
    HashMap<String, String> layout = new HashMap<String, String>();
    
    genCols.put("R",(String) RfBox.getSelectedItem());
    genCols.put("Rb",(String) RbBox.getSelectedItem());
    genCols.put("G",(String) GfBox.getSelectedItem());
    genCols.put("Gb",(String) GbBox.getSelectedItem());
    genCols.put("ID",(String) identifierBox.getSelectedItem());
    genCols.put("flag",(String) flagBox.getSelectedItem());

    
    dataModel.setGenericColumns(genCols);
    
    layout.put("ngrid.r",ngridRBox.getValue().toString());
    layout.put("ngrid.c",ngridCBox.getValue().toString());
    layout.put("nspot.r",nspotRBox.getValue().toString());
    layout.put("nspot.c",nspotCBox.getValue().toString());
    
    dataModel.setPrinterLayout(layout);
    dataModel.setSepChar(sep);
    String idcol = (String) identifierBox.getSelectedItem();
    dataModel.setIDcolumnStart(idcol); //was .substring(0, 1)
    
    // do the header cleanup for all data files
    // and write the cleaned-up copies somewhere (maybe tmp?) AND also
    // replace the list of input files in the main panel with the cleanup-up
    // copies !! 
    // diplay some feedback on what the machine's doing
    MessageWindow mwin = new MessageWindow(this, "Cleaning up input files. Please be patient...");
        try {
            OKButton.setEnabled(false);
            cleanupInputFiles();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            new SimpleErrorMessage(this, "There was a problem when trying to read/write the input files.\n" +
                    "Please make sure the files are accessible and retry.\n"+
                    "msg: "+ex.getMessage());
        }
    mwin.dispose();
    
    this.userChoice = response.OK_RESPONSE;
    this.setVisible(false);
}//GEN-LAST:event_OKButtonActionPerformed

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
        /*int dataStart = findIndexOfFirstDataRow(path, sep);
    System.out.println("first data row of file "+path+"\n is "+dataStart);*/
        //System.exit(0);
        
        // show panels
        dataSamplePanel.setVisible(true);
        columnsPanel.setVisible(true);
        printerLayoutPanel.setVisible(true);
        cancelButton.setVisible(true);
        OKButton.setVisible(true);
        
        // load the first 20 rows into the table preview
}//GEN-LAST:event_loadSampleButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    importList.setModel(new DefaultListModel());
    dataModel.setInputFiles(new ArrayList<String>());
    this.userChoice = response.CANCEL_RESPONSE;
    this.setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

private void editLayoutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLayoutBoxActionPerformed
    ngridCBox.setEnabled(editLayoutBox.isSelected());
    ngridRBox.setEnabled(editLayoutBox.isSelected());
    nspotCBox.setEnabled(editLayoutBox.isSelected());
    nspotRBox.setEnabled(editLayoutBox.isSelected());
}//GEN-LAST:event_editLayoutBoxActionPerformed

private void layoutPresetBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutPresetBoxActionPerformed
    Properties newLayout = new Properties();
    String chosenPreset = ((String) layoutPresetBox.getSelectedItem())+".layout";
    
    if (chosenPreset.startsWith("none")) {
        ngridCBox.setValue(1);
        ngridRBox.setValue(1);
        nspotCBox.setValue(1);
        nspotRBox.setValue(1);
        
        editLayoutBox.setSelected(true);
        ngridCBox.setEnabled(true);
        ngridRBox.setEnabled(true);
        nspotCBox.setEnabled(true);
        nspotRBox.setEnabled(true);
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
    
    
    ngridCBox.setValue(Integer.valueOf(newLayout.getProperty("ngrid.c")));
    ngridRBox.setValue(Integer.valueOf(newLayout.getProperty("ngrid.r")));
    nspotCBox.setValue(Integer.valueOf(newLayout.getProperty("nspot.c")));
    nspotRBox.setValue(Integer.valueOf(newLayout.getProperty("nspot.r")));
    
    editLayoutBox.setSelected(false);
    ngridCBox.setEnabled(false);
    ngridRBox.setEnabled(false);
    nspotCBox.setEnabled(false);
    nspotRBox.setEnabled(false);
}//GEN-LAST:event_layoutPresetBoxActionPerformed

private void saveLayoutPresetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLayoutPresetButtonActionPerformed
    String layoutName = JOptionPane.showInputDialog("Please enter layout name:");
    Properties newLayout = new Properties();
    
    newLayout.setProperty("ngrid.c",(Integer.toString((Integer) ngridCBox.getValue())));
    newLayout.setProperty("ngrid.r",(Integer.toString((Integer) ngridRBox.getValue())));
    newLayout.setProperty("nspot.c",(Integer.toString((Integer) nspotCBox.getValue())));
    newLayout.setProperty("nspot.r",(Integer.toString((Integer) nspotRBox.getValue())));
    
    FileOutputStream fos;
    try {
        fos = new FileOutputStream(presetRoot + filesep + layoutName + ".layout");
        newLayout.store(fos, "Custom layout");

    } catch (FileNotFoundException ex) {
        Exceptions.printStackTrace(ex);
    } catch (IOException ioe) {
        Exceptions.printStackTrace(ioe);
    }
    
    // now reload the preset box
    loadLayoutPresets();
}//GEN-LAST:event_saveLayoutPresetButtonActionPerformed

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
                return name.endsWith(".import");
            }
        };
        
        String[] presetList = presetRoot.list(filter);
        settingsModel.addElement("none");
        for (String preset : presetList) {
            preset = preset.replaceAll("\\.import", "");
            settingsModel.addElement(preset);
        }
        importSettingsBox.setModel(settingsModel);
}

private void saveSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSettingsButtonActionPerformed
    String settingsName = JOptionPane.showInputDialog("Please choose a name for the settings:");
    
    Properties importSettings = new Properties();
    
    importSettings.setProperty("ID",(String) identifierBox.getSelectedItem());
    importSettings.setProperty("Rf",(String) RfBox.getSelectedItem());
    importSettings.setProperty("Rb",(String) RbBox.getSelectedItem());
    importSettings.setProperty("Gf",(String) GfBox.getSelectedItem());
    importSettings.setProperty("Gb",(String) GbBox.getSelectedItem());
    importSettings.setProperty("flag",(String) flagBox.getSelectedItem());
    importSettings.setProperty("layout",(String) layoutPresetBox.getSelectedItem());
    
    FileOutputStream fos;
    try {
        fos = new FileOutputStream(presetRoot + filesep + settingsName + ".import");
        importSettings.store(fos, "Custom import settings "+settingsName);

    } catch (FileNotFoundException ex) {
        Exceptions.printStackTrace(ex);
    } catch (IOException ioe) {
        Exceptions.printStackTrace(ioe);
    }
    populateImportSettingsBox();
     importSettingsBox.setSelectedItem(settingsName);
}//GEN-LAST:event_saveSettingsButtonActionPerformed

private void importSettingsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSettingsBoxActionPerformed
    // load the preset     
    Properties newImportPreset = new Properties();
    String chosenPreset = ((String) importSettingsBox.getSelectedItem())+".import";
    
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
    RfBox.setSelectedItem(newImportPreset.getProperty("Rf"));
    RbBox.setSelectedItem(newImportPreset.getProperty("Rb"));
    GfBox.setSelectedItem(newImportPreset.getProperty("Gf"));
    GbBox.setSelectedItem(newImportPreset.getProperty("Gb"));
    flagBox.setSelectedItem(newImportPreset.getProperty("flag"));
    // TODO load this layout!!
    layoutPresetBox.setSelectedItem(newImportPreset.getProperty("layout")); 
    layoutPresetBoxActionPerformed(null);
}//GEN-LAST:event_importSettingsBoxActionPerformed

private void loadSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSettingsButtonActionPerformed
    importSettingsBoxActionPerformed(null);
}//GEN-LAST:event_loadSettingsButtonActionPerformed

private void saveLayoutPresetButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLayoutPresetButton1ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_saveLayoutPresetButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    /*public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
    TCGenericImportDialog dialog = new TCGenericImportDialog(new javax.swing.JFrame(), true);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
    public void windowClosing(java.awt.event.WindowEvent e) {
    System.exit(0);
    }
    });
    dialog.setVisible(true);
    }
    });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox GbBox;
    private javax.swing.JComboBox GfBox;
    private javax.swing.JButton OKButton;
    private javax.swing.JComboBox RbBox;
    private javax.swing.JComboBox RfBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox colSepBox;
    private javax.swing.JPanel columnsPanel;
    private javax.swing.JPanel dataSamplePanel;
    private javax.swing.JTable dataSampleTable;
    private javax.swing.JComboBox decSepBox;
    private javax.swing.JCheckBox editLayoutBox;
    private javax.swing.JComboBox flagBox;
    private javax.swing.JLabel flagLabel;
    private javax.swing.JLabel gbLabel;
    private javax.swing.JLabel gfLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JComboBox identifierBox;
    private javax.swing.JComboBox importSettingsBox;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox layoutPresetBox;
    private javax.swing.JButton loadSampleButton;
    private javax.swing.JButton loadSettingsButton;
    private javax.swing.JSpinner ngridCBox;
    private javax.swing.JSpinner ngridRBox;
    private javax.swing.JSpinner nspotCBox;
    private javax.swing.JSpinner nspotRBox;
    private javax.swing.JPanel printerLayoutPanel;
    private javax.swing.JLabel rbLabel;
    private javax.swing.JLabel rfLabel;
    private javax.swing.JComboBox sampleFileBox;
    private javax.swing.JButton saveLayoutPresetButton;
    private javax.swing.JButton saveLayoutPresetButton1;
    private javax.swing.JButton saveSettingsButton;
    // End of variables declaration//GEN-END:variables

    

}
