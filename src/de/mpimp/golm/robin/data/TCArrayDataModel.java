/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.data;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.twocolor.TCTargetsTableModel;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * MOdel object holding all data relevant to the description and analysis
 * of a two color experiment set
 * @author marc
 */
public class TCArrayDataModel extends ArrayDataModel {


    public enum designType {
        SIMPLE_REPLICATE,
        SIMPLE_REPLICATE_DYESWAP,
        SIMPLE_TECHNICAL_REP,
        SIMPLE_PAIRED,
        COMMON_REFERENCE,
        DIRECT_DESIGN,
        UNCONNECTED,
        UNKNOWN
        //...
    }

    
    private HashMap<String, String>   genericColumns; //  only needed if input type is generic: Names for
    private String              identifierColumn;
    private HashMap<String, String> printerLayout;
    private String              sepChar;
                                                //  R, G, Rb, Gb columns in the input file
    private String              targetsFile;
    private TCTargetsTableModel targetsModel;
    private String              normWithinArrays; // method to use when normalzing within arrays
    private String              normWithinArraysBGcorr;
    private String              normBetweenArrays;
    private ArrayList<String>   samples;
    private designType          type;    
    private String              referenceSample;
    private boolean             doNormWithin;
    private boolean             doNormBetween;    
    private boolean             userOverrideRefSample;    
    private String              IDcolumnStart; 
    // to be continued...  
    
    // internal global flag needed for the search
    private boolean foundFlag = false;
    
        
    public TCArrayDataModel() {
        super();
    }

     @Override
    public void store(File dataFile) throws Exception {
        if (!dataFile.exists()) dataFile.createNewFile();

        if (!dataFile.canWrite()) {
            new SimpleErrorMessage(null, "Can't write to specified file:\n"+dataFile.getCanonicalPath());
            return;
        }
        FileWriter writer = new FileWriter(dataFile);
        
        // store contents of all the nice fields...
        Document doc =  DocumentHelper.createDocument();
        Element root = doc.addElement("project").addAttribute("name", this.getExperimentName());
        root.addAttribute("type", "two_color_analysis");

        // import columns
        Element layout = root.addElement("generic_columns");
        for (String keyname : this.getGenericColumns().keySet()) {
            Element column = layout.addElement("column");
            column.addAttribute("name", keyname);
            column.addAttribute("value", this.getGenericColumns().get(keyname));
        }
        layout.addElement("id_column_start").addAttribute("value", this.getIDcolumnStart());
        
        // printer layout
        Element printer = root.addElement("printer_layout");
        for (String printkey : this.getPrinterLayout().keySet()) {
            Element printsetting = printer.addElement("dimension");
            printsetting.addAttribute("name", printkey);
            printsetting.addAttribute("value", this.getPrinterLayout().get(printkey));

        }

        // samples
         Element biosamples = root.addElement("samples");
         biosamples.addAttribute("reference", this.getReferenceSample());
         for (String sample : samples) {
            biosamples.addElement("sample").addAttribute("name", sample);
         }

         // targets file
         root.addElement("targets_file").addAttribute("path", this.getTargetsFile());

        // normalization settings
         Element stats = root.addElement("stat_settings");
         Element normwithin = stats.addElement("norm_within_arrays").addAttribute("method", this.getNormWithinArrays());
         normwithin.addAttribute("do", String.valueOf(doNormBetween));
         stats.addElement("norm_within_arrays_corr").addAttribute("method", this.getNormWithinArraysBGcorr());
         Element normBetween = stats.addElement("norm_between_arrays").addAttribute("method", this.getNormBetweenArrays());
         normBetween.addAttribute("do", String.valueOf(doNormBetween));

         stats.addElement("strategy").addAttribute("method", statStrategy);
         stats.addElement("pval_correction").addAttribute("method", pValCorrectionMethod);
         stats.addElement("pval_cutoff").addAttribute("value", pValCutoffValue);
         stats.addElement("lfc_min_2").addAttribute("value", String.valueOf(minLogFoldChangeOf2));

//        private designType          type;
//        private boolean             userOverrideRefSample;
//        private String              sepChar;
//        private TCTargetsTableModel targetsModel;

        XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
        xmlWriter.write(doc);
        xmlWriter.flush();
        writer.close();
    }

    @Override
    public void load(File dataFile) throws Exception {
        FileReader reader = new FileReader(dataFile);
        Element dataRoot = new SAXReader().read(reader).getRootElement();

        // load generic columns
        genericColumns = new HashMap<String, String>();
        Element gencols = dataRoot.element("generic_columns");
        Iterator<Element> colIter = gencols.elementIterator("column");
        while (colIter.hasNext()) {
            Element column = colIter.next();
            genericColumns.put(column.attributeValue("name"), column.attributeValue("value"));
        }
        this.setIDcolumnStart(gencols.element("id_column_start").attributeValue("value"));

        // load printer layout settings
        printerLayout = new HashMap<String, String>();
        Element printer = dataRoot.element("printer_layout");
        Iterator<Element> printIter = printer.elementIterator("dimension");
        while (printIter.hasNext()) {
            Element setting = printIter.next();
            printerLayout.put(setting.attributeValue("name"), setting.attributeValue("value"));
        }

        // load samples
        samples = new ArrayList<String>();
        Element sam = dataRoot.element("samples");
        this.setReferenceSample(sam.attributeValue("reference"));
        
        Iterator<Element> samIter = sam.elementIterator("sample");
        while (samIter.hasNext()) {
            Element sample = samIter.next();
            samples.add(sample.attributeValue("name"));
        }

        // load targets table
        this.setTargetsFile(dataRoot.element("targets_file").attributeValue("path"));

        // load statistics settings
        Element statSettings = dataRoot.element("stat_settings");
        this.setNormBetweenArrays(statSettings.element("norm_between_arrays").attributeValue("method"));
        this.setDoNormBetween(Boolean.parseBoolean(statSettings.element("norm_between_arrays").attributeValue("do")));
        this.setNormWithinArrays(statSettings.element("norm_within_arrays").attributeValue("method"));
        this.setNormWithinArraysBGcorr(statSettings.element("norm_within_arrays_corr").attributeValue("method"));
        this.setDoNormWithin(Boolean.parseBoolean(statSettings.element("norm_within_arrays").attributeValue("do")));
        
        this.setStatStrategy(statSettings.element("strategy").attributeValue("method"));
        this.setPValCorrectionMethod(statSettings.element("pval_correction").attributeValue("method"));
        this.setPValCutoffValue(statSettings.element("pval_cutoff").attributeValue("value"));
        this.setMinLogFoldChangeOf2(Boolean.parseBoolean(statSettings.element("lfc_min_2").attributeValue("value")));
    }

    @Override
    public String toString() {
        StringBuilder modelString = new StringBuilder();
        
        modelString.append("###################################\n");
        modelString.append("# Robin two color data analysis summary\n");
        modelString.append("# "+this.getExperimentName()+"\n");
        modelString.append("# "+Utilities.currentDateAsString()+"; "+Utilities.currentTimeAsString()+"\n");
        modelString.append("###################################\n");
        modelString.append("\n");
//        modelString.append("# Input files:\n");
//        for (String file : this.getInputFiles()) {
//            modelString.append(file+"\n");
//        }
//        modelString.append("\n");
        modelString.append("# Targets table assigning samples to color channels on each chip\n");
        modelString.append("Label\tCy3 sample\tCy5 sample\n");
        for (int i = 0; i < targetsModel.getRowCount(); i++) {
            modelString.append(targetsModel.getValueAt(i, 0)+"\t");      
            modelString.append(targetsModel.getValueAt(i, 2)+"\t");
            modelString.append(targetsModel.getValueAt(i, 3)+"\n");            
        }
        modelString.append("\n");
        modelString.append("# Normalization settings\n");
        modelString.append("Within array normalization method: "+this.getNormWithinArrays()+"\n");
        modelString.append("using background correction method: "+this.getNormWithinArraysBGcorr()+"\n");
        modelString.append("Between array normalization method: "+this.getNormBetweenArrays()+"\n");
        
        modelString.append("\n");
        modelString.append("# Statistics settings\n");
        modelString.append("Multiple testing strategy: "+this.getStatStrategy()+" using p-value correction: "+this.getPValCorrectionMethod()+"\n");
        modelString.append("P-value cut-off value for significant differential expression: " +this.getPValCutoffValue()+"\n");
        modelString.append("Genes that showed a log2-fold change smaller than two ignored: " +
                (this.isMinLogFoldChangeOf2() ? "yes\n" : "no\n"));
        
        modelString.append("\n");
        modelString.append("# The analysis produced the following warnings\n");
        
        if (!this.getWarningsHandler().hasWarnings()) {
            modelString.append("none\n");
        } else {        
            for(Warning w : this.getWarningsHandler().getWarnings()) {
                modelString.append("Type: " + w.getType()+"\n");
                modelString.append("Severity: " + w.getSeverity()+"\n");
                modelString.append(w.getMessage().trim()+"\n");
                modelString.append("//\n");
            }
        }
        return modelString.toString();
    }
    
    public String getIDcolumnStart() {
        return IDcolumnStart;
    }

    public void setIDcolumnStart(String IDcolumnStart) {
        this.IDcolumnStart = IDcolumnStart;
    }    

    public String getSepChar() {
        return sepChar;
    }

    public String getIdentifierColumn() {
        return identifierColumn;
    }

    public void setIdentifierColumn(String identifierColumn) {
        this.identifierColumn = identifierColumn;
    }

    public void setSepChar(String sepChar) {
        this.sepChar = sepChar;
    }

    public HashMap<String, String> getPrinterLayout() {
        return printerLayout;
    }

    public void setPrinterLayout(HashMap<String, String> printerLayout) {
        this.printerLayout = printerLayout;
    }
    
    public void addWarning(Warning warning) {
        warnings.addWarning(warning);
    }

    public boolean isWriteRawExprs() {
        return writeRawExprs;
    }

    public void setWriteRawExprs(boolean writeRawExprs) {
        this.writeRawExprs = writeRawExprs;
    }
    
    public boolean isMinLogFoldChangeOf2() {
        return minLogFoldChangeOf2;
    }

    public void setMinLogFoldChangeOf2(boolean minLogFoldChangeOf2) {
        this.minLogFoldChangeOf2 = minLogFoldChangeOf2;
    }

    public String getPValCorrectionMethod() {
        return pValCorrectionMethod;
    }

    public void setPValCorrectionMethod(String pValCorrectionMethod) {
        this.pValCorrectionMethod = pValCorrectionMethod;
    }

    public String getPValCutoffValue() {
        return pValCutoffValue;
    }

    public void setPValCutoffValue(String pValCutoffValue) {
        this.pValCutoffValue = pValCutoffValue;
    }

    public String getStatStrategy() {
        return statStrategy;
    }

    public void setStatStrategy(String statStrategy) {
        this.statStrategy = statStrategy;
    }

    public TCTargetsTableModel getTargetsModel() {
        return targetsModel;
    }

    public void setTargetsModel(TCTargetsTableModel targetsModel) {
        this.targetsModel = targetsModel;
    }

    public WarningsHandler getWarningsHandler() {
        return warnings;
    }
    
    public boolean isUserOverrideRefSample() {
        return userOverrideRefSample;
    }

    public void setUserOverrideRefSample(boolean userOverrideRefSample) {
        this.userOverrideRefSample = userOverrideRefSample;
    }

    public String getContrastTerms() {
        return contrastTerms;
    }
    
        public void setContrastTerms(String contrastTerms) {
        this.contrastTerms = contrastTerms;
    }    
    
    public boolean isDoNormBetween() {
        return doNormBetween;
    }

    public void setDoNormBetween(boolean doNormBetween) {
        this.doNormBetween = doNormBetween;
    }

    public boolean isDoNormWithin() {
        return doNormWithin;
    }

    public void setDoNormWithin(boolean doNormWithin) {
        this.doNormWithin = doNormWithin;
    }
    
    
    
    public String getTempIdentifier() {
        return Utilities.extractFileNamePathComponent(tempRoot);
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getReferenceSample() {
        return referenceSample;
    }

    public void setReferenceSample(String referenceSample) {
        this.referenceSample = referenceSample;
    }
    
       
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setTargestModel(TCTargetsTableModel targestModel) {
        this.targetsModel = targestModel;
    }
    
    public designType getType() {
        return guessExperimentType();
    }
    
    public ArrayList<String> getSamples() {
        return samples;
    }

    public void setSamples(ArrayList<String> samples) {
        this.samples = samples;
    }
    
    
    
    public String getTempRoot() {
        return tempRoot;
    }

    public void setTempRoot(String tempRoot) {
        this.tempRoot = tempRoot;
    }
    
    
    
    public void addInputFile(String path) {
        inputFiles.add(path);                
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public HashMap<String, String> getGenericColumns() {
        return genericColumns;
    }

    public void setGenericColumns(HashMap<String, String> genericColumns) {
        this.genericColumns = genericColumns;
    }

    public ArrayList<String> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(ArrayList<String> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public String getNormBetweenArrays() {
        return normBetweenArrays;
    }

    public void setNormBetweenArrays(String normBetweenArrays) {
        this.normBetweenArrays = normBetweenArrays;
    }

    public String getNormWithinArrays() {
        return normWithinArrays;
    }

    public void setNormWithinArrays(String normWithinArrays) {
        this.normWithinArrays = normWithinArrays;
    }

    public String getNormWithinArraysBGcorr() {
        return normWithinArraysBGcorr;
    }

    public void setNormWithinArraysBGcorr(String normWithinArraysBGcorr) {
        this.normWithinArraysBGcorr = normWithinArraysBGcorr;
    }

    public String getTargetsFile() {
        return targetsFile;
    }

    public void setTargetsFile(String targetsFile) {
        this.targetsFile = targetsFile;
    }
    
    public void copyInputFiles() throws IOException {
        // create dir "input"
        File inputDir = new File(this.outputDir+sFileSep+"input");
        inputDir.mkdir();
        
        for (String inputFile : this.getInputFiles()) {
            File sourceFile = new File(inputFile);
            File targetFile = new File(this.outputDir+sFileSep+"input"+sFileSep+Utilities.extractFileNamePathComponent(inputFile));
            Utilities.copyFile(sourceFile, targetFile);
        }
    }
    
    /**
     * Try to determine what type of experiment design
     * the user wants analyse by analysing the so-far made
     * inputs. Situations that can be encountered and 
     * solved using limma functions are:
     * 
     * two group comparisons:
     * simple replicate design,
     * simple replicate with dye swaps
     * simple with technical replication
     * simple paired samples
     * common reference
     * 
     * several groups:
     * common reference
     * direct design
     * 
     * separate channel analysis (in unconnected designs)
     * 
     * @param dataModel
     * @param targetsModel 
     */
    private designType guessExperimentType () {
        // TODO this'll need a lot of work before it's comprehensive...
                
        // simple two group comparisons
        if (this.samples.size() == 2) {
            return designType.SIMPLE_REPLICATE;
        } else if (this.samples.size() > 2) {
            // check if if is a common reference design
            HashMap<String, Integer> sampleCount = new HashMap<String, Integer>();
            
            // count the occurrences of the individual samples
            for (int i = 0; i < targetsModel.getRowCount(); i++) {
                sampleCount.put((String) targetsModel.getValueAt(i, 2), 0);
                sampleCount.put((String) targetsModel.getValueAt(i, 3), 0);                
            }
            
            for (int i = 0; i < targetsModel.getRowCount(); i++) {
                
                // we have to take the possibility that someone hybridized the same
                // sample to both channels of one chip into account. In this case the
                // count increases only by 1!
                
                String cy3 = (String) targetsModel.getValueAt(i, 2);
                String cy5 = (String) targetsModel.getValueAt(i, 3);
                
                if (cy3.equals(cy5)) {
                    sampleCount.put(cy3, sampleCount.get(cy3)+1);
                } else {
                    sampleCount.put(cy3, sampleCount.get(cy3)+1);
                    sampleCount.put(cy5, sampleCount.get(cy5)+1); 
                }
                               
            }
            
            // is there opne sample that occurs rowCount times?
            for (String sample : sampleCount.keySet()) {
                if (sampleCount.get(sample) == targetsModel.getRowCount()) {
                    if (!this.isUserOverrideRefSample()) this.referenceSample = sample;
                    return designType.COMMON_REFERENCE;
                }
            }
            //FIXME check direct and unconnected designs properly!!!
            if (allPathsCanBeWalked(targetsModel)) {
                return designType.DIRECT_DESIGN;
            } else {
                return designType.UNCONNECTED;
            }
            
            
            // not a common reference design...
            // could be direct or disconnected
            // direct design: can i walk all possible paths?
        }
        
        return designType.UNKNOWN;
    }
    
    public boolean allPathsCanBeWalked(TCTargetsTableModel model) {
        //TODO implement a way to try and walk all paths
        // firstget a unique list of all the nodes
        ArrayList<String> uniqueNodes = new ArrayList<String>();
        HashMap<String, ArrayList<String>> nodes = new HashMap<String, ArrayList<String>>();
        
        for (int i = 0; i < model.getRowCount(); i++) {  
            
            String cy3 = (String) model.getValueAt(i, 2);
            String cy5 = (String) model.getValueAt(i, 3);
            
            if (!uniqueNodes.contains(cy5)) {
                uniqueNodes.add(cy5);
                // initialize hash key
                nodes.put(cy5, new ArrayList<String>());
            }
            
            if (!uniqueNodes.contains(cy3)) {
                uniqueNodes.add(cy3);
                nodes.put(cy3, new ArrayList<String>());
            }
        }   
        
               
        // build a Hash of the nodes and their direct neighbors
        for (int i = 0; i < model.getRowCount(); i++) {                
            
            String cy3 = (String) model.getValueAt(i, 2);
            String cy5 = (String) model.getValueAt(i, 3);

            // if the list does not already contain this neighbor, add it
            if (!nodes.get(cy3).contains(cy5)) {
                nodes.get(cy3).add(cy5);
            }
            
            // do the same for the other node
            if (!nodes.get(cy5).contains(cy3)) {
                nodes.get(cy5).add(cy3);
            }
        }
        
        /*for (String k : nodes.keySet()) {
        System.out.print("key: "+k+" --> ");
        for (String n : nodes.get(k)) {
        System.out.print(n+",");
        }
        System.out.println();
        }*/
        
        // now check for each node if all the other nodes can be reached
        for (String nodeA : uniqueNodes) {
            for (String nodeB : uniqueNodes) {
                if (nodeB.equals(nodeA)) continue;
                //System.out.println("checking path from "+nodeA+" to "+nodeB);
                foundFlag = false;
                if (!canWalkFromAtoB("", nodeA, nodeB, nodes)) {
                    //System.out.println("no connection possible between "+nodeA+" and "+nodeB);
                    return false;
                } else {
                    //System.out.println(" OK");
                }
            }
        }
        return true;
    }
    
    private boolean canWalkFromAtoB(String previous, String start, String target, HashMap<String, ArrayList<String>> neighbors) {
        // for each neighbor, ask whether target is a direct neighbor
        
        if (foundFlag) {
            return true;
        } else {
        
            Stack stck = new Stack();
            String directNeighbor = null;

            for (String neighbor : neighbors.get(start)) stck.push(neighbor);
            while (!stck.empty()) {
                directNeighbor = (String) stck.pop();
                
                if (previous.equals(directNeighbor)) continue;

                if (directNeighbor.equals(target)) {
                    // we found a path
                    foundFlag = true;
                    return foundFlag;                        
                }
                
                //DEBUG 
                //System.out.println("checking neighbor "+directNeighbor+" of startnode "+start+" against target "+target+" previous start was "+previous+"");  

                // no path found? walk on.
                canWalkFromAtoB(start, directNeighbor, target, neighbors);            
            }
        }
        return foundFlag;
    }
    
    private boolean DepthFirstSearch(String start, String target, HashMap<String, ArrayList<String>> neighbors) {
        if (start.equals(target)) {
            return true;
        } else {
            
            // fill stack of direct neighbors
            Stack stck = new Stack();
            for (String neighbor : neighbors.get(start)) stck.push(neighbor);
            
            while (!stck.empty()) {
                String nodeB = (String) stck.pop();
                DepthFirstSearch(nodeB, target, neighbors);
            }            
        }
        return false;
    }

    @Override
    public String generateTextReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String generateHTMLReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
