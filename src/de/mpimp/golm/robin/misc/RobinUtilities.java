package de.mpimp.golm.robin.misc;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.SetupProjectDirectoryDialog;
import de.mpimp.golm.robin.GUI.affy.CelFileGroupPanel;
import de.mpimp.golm.robin.ProjectType;
import de.mpimp.golm.robin.data.ArrayDataModel;
import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openide.util.Exceptions;

public class RobinUtilities {

    public static boolean validateGroupList(JPanel groupListPanel, WarningsHandler warnings ) {
        //Check whether all group names are unique - warn if not
    // also do some more checks on the group size and balance
    // to assess the quality of the experiment
    int maxSize = 0;
    int minSize = 1000;
    int groupSize = 0;
    
    ArrayList<String> names = new ArrayList<String>();
    for (Component comp : groupListPanel.getComponents()) {
        if (comp instanceof CelFileGroupPanel) {
            
            String groupName = ((CelFileGroupPanel) comp).getGroupName();
            
            if (names.contains(groupName)) {
                new SimpleErrorMessage(groupListPanel, "Please choose unique names for all groups!\n\""+groupName+"\" occurs more than once.");
                return false;
            }
            
            // check whether the group actually has some files in it
            if (!((CelFileGroupPanel) comp).hasFiles()) {
                new SimpleErrorMessage(groupListPanel, "Group \""+groupName+"\" does not contain any file.\n"+
                        "Please do either delete or fill it.");
                return false;
            }      
            
            groupSize = ((CelFileGroupPanel) comp).getFileList().size();
            if (groupSize > maxSize) maxSize = groupSize;
            if (groupSize < minSize) minSize = groupSize;
            
            // check whether there are groups with only few data files
            if (groupSize == 1) {
                //TODO discuss error levels with björn
                Warning w = new Warning("Insufficient data","Group "+groupName+"\n"+
                                        "contains only 1 input file.\n"+
                                        "the reliability of the results\n"+
                                        "will be extremely questionable.\n", 
                                        Warning.SEVERITY_CRITICAL);
                warnings.addWarning(w);
            } else if (groupSize == 2) {
                //TODO discuss error levels with björn
                Warning w = new Warning("Insufficient data","Group "+groupName+"\n"+
                                        "contains only 2 input files.\n"+
                                        "the reliability of the results\n"+
                                        "will be reduced.\n", 
                                        Warning.SEVERITY_MEDIUM);
                warnings.addWarning(w);
            }
            names.add(groupName);
        }
    }
    
    System.out.println("GROUP SIZES min:"+minSize+" max:"+maxSize);
    
    // only one file per group? limma will break...
    if ( (minSize == 1) && (maxSize == 1) ) {
        new SimpleErrorMessage(groupListPanel,    "You only supplied one input file per group. This amount\n"+
                        "of input data is not sufficient for further analyses using\n"+
                        "the limma package. Canceling.");
        return false;
    }
    
    // are the groups unbalanced?
    if (minSize != maxSize) {
        double ratio = ((double)minSize / (double)maxSize) * 100;
        System.out.println("ratio:"+ratio);
        
        if (ratio < 33.3) {
            // BAD 3 times less input files in at least one group
            Warning w = new Warning("Unbalanced data","At least one group in the input\n"+
                                        "contains more than three times less\n"+
                                        "input files / chips than the largest\n"+
                                        "group. This imbalance might reduce the\n"+
                                        "reliability of the analysis results considerably.\n", 
                                        Warning.SEVERITY_CRITICAL);
            warnings.addWarning(w);
        } else if (ratio < 50) {
            // half the files in one group 
            Warning w = new Warning("Unbalanced data","At least one group in the input\n"+
                                        "contains less than half the amount of\n"+
                                        "input files / chips than the largest\n"+
                                        "group. This imbalance might reduce the\n"+
                                        "reliability of the analysis results.\n", 
                                        Warning.SEVERITY_MEDIUM);
            warnings.addWarning(w);
        } else if (ratio < 75) {
            // 3/4 of the data of the largest group in the smalles group
            Warning w = new Warning("Unbalanced data","At least one group in the input\n"+
                                        "contains only 3/4 of the amount of input\n"+
                                        "files / chips of the largest group.\n"+
                                        "This imbalance could reduce the\n"+
                                        "reliability of the analysis results.\n", 
                                        Warning.SEVERITY_LIGHT);
            warnings.addWarning(w);
        }
    }

        return true;
    }

    
    public static File setupProjectDirectories(JFrame parent, ProjectType.TYPE type) throws IOException {

        SetupProjectDirectoryDialog setupDialog = new SetupProjectDirectoryDialog(parent, true, type);
        setupDialog.setVisible(true);

        if (setupDialog.getUserChoice() != SetupProjectDirectoryDialog.response.ACCEPT_RESPONSE) {
            setupDialog.dispose();
            return null;
        }

//        File outputPath = setupDialog.getProjectPath();
//
//            try {
//                //DEBUG
//                System.out.println("OUTPUTPATH:" + outputPath.getCanonicalPath());
//            } catch (IOException ex) {
//                // the heck knows how this happened...
//                new em(parent, "output path error:"+ex.getMessage());
//                Exceptions.printStackTrace(ex);
//                return null;
//            }
//
//            // path is null
//            if ((outputPath == null) || (outputPath.getCanonicalPath().equals(""))){
//                new em(parent, "Please specify a name for the output folder");
//                return null;
//            }
//
//
//            // create the path
//            if (!outputPath.exists() ) {
//                boolean res = outputPath.mkdir();
//                if (!res) {
//                    new em(parent, "The specified output folder could not be created");
//                }
//            }
//
//            // create the substructure
//            String sFileSep = System.getProperty("file.separator");
//            try {
//
//                // create the subdirectory structure
//                boolean mkDirOK = false;
//                File sourceDir = new File(outputPath, "source");
//                mkDirOK = sourceDir.mkdir();
//
//                // create plots directory
//                File plotsDir = new File(outputPath, "plots");
//                mkDirOK = plotsDir.mkdir();
//
//                if (!(type == ProjectType.TYPE.AFFYMETRIX)) {
//                    // create input directory for cleaned-up copies of the raw input data
//                    File inputDir = new File(outputPath, "input");
//                    mkDirOK = inputDir.mkdir();
//                }
//
//                // create plots directory
//                File qualDir = new File(outputPath, "qualitychecks");
//                mkDirOK = qualDir.mkdir();
//
//                // create detailed_results directory
//                File detailsDir = new File(outputPath, "detailed_results");
//                mkDirOK = detailsDir.mkdir();
//
//                if (!mkDirOK) {
//                    new em(parent, "Could not create directory substructure.\nPlease check if directory already exists.");
//                    return null;
//                }
//
//            } catch (Exception ex) {
//                Exceptions.printStackTrace(ex);
//                new em(parent, "Could not create directory substructure.\nError: "+ex.getMessage());
//                return null;
//            }
        
        return null;
    }
    
    public static File getOutputPathAndSetupDirectories(JComponent parent, 
                                                        Properties defaultSettings, 
                                                        ArrayList<String> qcFiles,
                                                        String qcScriptPath) {
        
        // configure the fileChooser for output directory choosing
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Please select the output directory");
        fileChooser.setSelectedFile(null);
        fileChooser.resetChoosableFileFilters();

        File outputPath = null;
        if (fileChooser.showSaveDialog(parent) == javax.swing.JFileChooser.APPROVE_OPTION) {        
            outputPath = fileChooser.getSelectedFile(); 
            try {
                //DEBUG
                System.out.println("OUTPUTPATH:" + outputPath.getCanonicalPath());
            } catch (IOException ex) {
                // the heck knows how this happened...
                SimpleLogger.getLogger(true).logException(new Exception("Output path error.",ex));
                return null;
            }

            // path is null
            if ((outputPath == null) || (outputPath.equals(""))){
                new SimpleErrorMessage(parent, "Please specify a name for the output folder");
                return null;
            }


            try {
                //defaultSettings.setProperty("outputPath", outputPath.getCanonicalPath().replaceAll("\\\\", "\\\\\\\\")); //
                defaultSettings.setProperty("outputPath", outputPath.getCanonicalPath().replaceAll("\\\\", "/")); //
                defaultSettings.setProperty("displayOutputPath", outputPath.getCanonicalPath());
            } catch (IOException ex) {
                SimpleLogger.getLogger(true).logException(new Exception("Output path error.",ex));
                return null;
            }

            // create the path
            if (!outputPath.exists() ) {            
                boolean res = outputPath.mkdir();                
                if (!res) {
                    new SimpleErrorMessage(parent, "The specified output folder could not be created");
                }            
            }
            
            MessageWindow mswin = new MessageWindow("Copying files to output folder. Please be patient...");

            // create the substructure
            String sFileSep = System.getProperty("file.separator");
            try {

                // create the subdirectory structure
                boolean mkDirOK = false;
                File sourceDir = new File(outputPath.getCanonicalPath() + sFileSep + "source");
                mkDirOK = sourceDir.mkdir();

                // create plots directory
                File plotsDir = new File(outputPath.getCanonicalPath()+sFileSep+"plots");
                mkDirOK = plotsDir.mkdir();

                // create detailed_results directory
                File detailsDir = new File(outputPath.getCanonicalPath()+sFileSep+"detailed_results");
                mkDirOK = detailsDir.mkdir();

                if (!mkDirOK) {
                    new SimpleErrorMessage(parent, "Could not create directory substructure.\nPlease check if directory already exists.");
                    return null;
                }
                
                if (qcFiles != null) {
                                     
                    File qualDir = new File(outputPath.getCanonicalPath()+sFileSep+"qualitychecks");
                    mkDirOK = qualDir.mkdir();
                    
                    // copy all the quality check results to this folder
                    for (String file: qcFiles) {                        
                       File sourceFile = new File(file);
                       File targetFile = null;
                       if (sourceFile.getCanonicalPath().endsWith("_qc.R")) {
                            targetFile = new File(sourceDir.getCanonicalPath()+sFileSep+"qualityChecks.R");
                       } else if (sourceFile.getCanonicalPath().endsWith("_targets.txt")) {
                            targetFile = new File(outputPath.getCanonicalPath()+sFileSep+"Targets.txt");
                       } else {
                            targetFile = new File(qualDir.getCanonicalPath()+sFileSep+sourceFile.getName());
                       }
                       //DEBUG
                       System.out.println("copy:"+sourceFile.getCanonicalPath()+" to "+targetFile.getCanonicalPath());
                       Utilities.copyFile(sourceFile, targetFile);                        
                    }  
                }
                mswin.dispose();
            } catch (IOException ex) {
                SimpleLogger.getLogger(true).logException(new Exception("Could not create directory substructure.",ex));
                return null;
            }           
        }        
        return outputPath;
    }
    
    
   
    
  
    
   public static String longDescriptionToShortType(String desc) {
        if (desc.equals("Blast against Arabidopsis")) return "ARA";
        if (desc.equals("Blast against UniProtKB/Swiss-Prot")) return "SWISS";
        if (desc.equals("RPSBlast against CDD (Conserved domain DB)")) return "RPS";
        if (desc.equals("RPSBlast against KOG (Clusters of orthologous genes)")) return "RPS";
        if (desc.equals("InterProScan results")) return "IPR";
        if (desc.equals("Blast against UniProt UniRef90 database")) return "TXT";
        
        return "You should never see this";
   }
   

    public static void saveGroupList(JPanel groupListPanel, File groupListFile, ArrayDataModel model) throws IOException {
        if (!groupListFile.exists()) groupListFile.createNewFile();

        if (!groupListFile.canWrite()) {
            new SimpleErrorMessage(null, "Can't write to specified file:\n"+groupListFile.getCanonicalPath());
            return;
        }
        ArrayList<String> names = new ArrayList<String>();
        FileWriter writer = new FileWriter(groupListFile);

        Document doc =  DocumentHelper.createDocument();
        Element root = doc.addElement("group_definition");

        for (Component comp : groupListPanel.getComponents()) {
            if (comp instanceof CelFileGroupPanel) {

                Element groupElement = root.addElement("group");
                groupElement.addAttribute("name", ((CelFileGroupPanel)comp).getGroupName());
                for (String file : ((CelFileGroupPanel)comp).getFileList()) {
                    Element fileElement = groupElement.addElement("file");
                    fileElement.addAttribute("path", file);
                    //model.addInputFile(file);
                }
            }
        }        
        XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
        xmlWriter.write(doc);
        xmlWriter.flush();
        writer.close();
    }


    public static JPanel populateGroupListFromFile(File groupFile, JList sourceList) throws FileNotFoundException, DocumentException {
        //load the group definition from the file
        FileReader reader = new FileReader(groupFile);
        Element groupDef = new SAXReader().read(reader).getRootElement();

        JPanel groupListPanel = new JPanel();
        groupListPanel.setLayout(new BoxLayout(groupListPanel, BoxLayout.Y_AXIS));
        groupListPanel.setBackground(Color.WHITE);

        Iterator<Element> groupIter = groupDef.elementIterator("group");
        while (groupIter.hasNext()) {
            Element group = groupIter.next();
            //DEBUG
            String groupName = group.attributeValue("name");
            System.out.println("group = " + groupName);
            CelFileGroupPanel groupPanel = new CelFileGroupPanel(groupName, sourceList);

            Iterator<Element> fileIter = group.elementIterator("file");
            while (fileIter.hasNext()) {
                Element file = fileIter.next();
                //DEBUG
                String path = file.attributeValue("path");
                System.out.println("-->" + path);
                if (!groupPanel.addFileFromSourceList(path)) {
                    return null;
                }
            }
            groupListPanel.add(groupPanel);
        }

        return groupListPanel;
    }   
    
    public static float getGCContent(String sequence) {
        float GC = 0f;
        int GCcount = 0;
        
        for (char n : sequence.toUpperCase().toCharArray()) {
            if ( (n == 'C') || (n == 'G') ) GCcount++;
        }
        
        GC = (float) GCcount / (float) sequence.length();
        
        return GC;
    }
}
