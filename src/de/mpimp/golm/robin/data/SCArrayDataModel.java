/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.data;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.warnings.Warning;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Class holding data on a single channel microarray
 * experiments (e.g. Affymetrix data)
 * @author marc
 */
public class SCArrayDataModel extends ArrayDataModel {

    private HashMap<String, String>     genericColumns; //  only needed if input type is generic: Names for
    private HashMap<String, String>     printerLayout;
    private String                      sepChar;
    private String                      IDcolumnStart; // i do not know why i did this. And i do not want to find out.
    private String                      backGroundCorrectionMethod;
    private String                      normBetweenArrays;
    private boolean                     doNormBetween;
    private boolean                     doBackgroundCorrection;
    private String                      groupsTerm;
    private String                      designTerm;
    private File                        groupsFile;
    
    
    public SCArrayDataModel() {
        super();
    }

    public File getGroupsFile() {
        return groupsFile;
    }

    public void setGroupsFile(File groupsFile) {
        this.groupsFile = groupsFile;
    }

    @Override
    public String toString() {
        StringBuilder modelString = new StringBuilder();

        modelString.append("###################################\n");
        modelString.append("# Robin single channel data analysis summary\n");
        modelString.append("# "+this.getExperimentName()+"\n");
        modelString.append("# "+Utilities.currentDateAsString()+"; "+Utilities.currentTimeAsString()+"\n");
        modelString.append("###################################\n");
        modelString.append("\n");
        modelString.append("# Input files:\n");
        for (String file : this.getInputFiles()) {
            modelString.append(file+"\n");
        }

        modelString.append("\n");
        modelString.append("# Normalization settings\n");
        modelString.append("Between array normalization method: "+this.getNormBetweenArrays()+"\n");
        modelString.append("Using background correction method: "+this.getBackGroundCorrectionMethod()+"\n");

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

    @Override
    public String generateTextReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String generateHTMLReport() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public boolean isDoBackgroundCorrection() {
        return doBackgroundCorrection;
    }

    public void setDoBackgroundCorrection(boolean doBackgroundCorrection) {
        this.doBackgroundCorrection = doBackgroundCorrection;
    }

    public String getDesignTerm() {
        return designTerm;
    }

    public void setDesignTerm(String designTerm) {
        this.designTerm = designTerm;
    }

    public String getGroupsTerm() {
        return groupsTerm;
    }

    public void setGroupsTerm(String groupsTerm) {
        this.groupsTerm = groupsTerm;
    }

    public boolean isDoNormBetween() {
        return doNormBetween;
    }

    public void setDoNormBetween(boolean doNormBetween) {
        this.doNormBetween = doNormBetween;
    }

    public String getBackGroundCorrectionMethod() {
        return backGroundCorrectionMethod;
    }

    public void setBackGroundCorrectionMethod(String backGroundCorrectionMethod) {
        this.backGroundCorrectionMethod = backGroundCorrectionMethod;
    }

    public String getNormBetweenArrays() {
        return normBetweenArrays;
    }

    public void setNormBetweenArrays(String normBetweenArrays) {
        this.normBetweenArrays = normBetweenArrays;
    }

    

    public HashMap<String, String> getGenericColumns() {
        return genericColumns;
    }

    public void setGenericColumns(HashMap<String, String> genericColumns) {
        this.genericColumns = genericColumns;
    }

    public HashMap<String, String> getPrinterLayout() {
        return printerLayout;
    }

    public void setPrinterLayout(HashMap<String, String> printerLayout) {
        this.printerLayout = printerLayout;
    }

    public String getSepChar() {
        return sepChar;
    }

    public void setSepChar(String sepChar) {
        this.sepChar = sepChar;
    }

    public String getIDcolumnStart() {
        return IDcolumnStart;
    }

    public void setIDcolumnStart(String IDcolumnStart) {
        this.IDcolumnStart = IDcolumnStart;
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
        root.addAttribute("type", "single_channel_analysis");

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

        // groups file
        root.addElement("groups_file").addAttribute("path", this.getGroupsFile().getCanonicalPath());

        //analysis type
        Element analysis = root.addElement("analysis");
        analysis.addAttribute("method", (analysisType == AnalysisStrategy.LIMMA) ? "limma" : "rankprod" );

        // normalization settings
        Element stats = root.addElement("stat_settings");
        Element normBetween = stats.addElement("norm_between_arrays").addAttribute("method", this.getNormBetweenArrays());
        normBetween.addAttribute("do", String.valueOf(doNormBetween));
        Element bgCorr = stats.addElement("background_correction").addAttribute("method", this.getBackGroundCorrectionMethod());
        normBetween.addAttribute("do", String.valueOf(doBackgroundCorrection));

        stats.addElement("strategy").addAttribute("method", statStrategy);
        stats.addElement("pval_correction").addAttribute("method", pValCorrectionMethod);
        stats.addElement("pval_cutoff").addAttribute("value", pValCutoffValue);
        stats.addElement("lfc_min_2").addAttribute("value", String.valueOf(minLogFoldChangeOf2));

        XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
        xmlWriter.write(doc);
        xmlWriter.flush();
        writer.close();
        
         
    }// end store

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

        // groups file
        this.setGroupsFile(new File(dataRoot.element("groups_file").attributeValue("path")));

        // analysis type
        Element analysis = dataRoot.element("analysis");
        this.setAnalysisType( (analysis.attributeValue("method").equals("limma")) ?
            AnalysisStrategy.LIMMA : AnalysisStrategy.RANKPROD);
        
        // load statistics settings
        Element statSettings = dataRoot.element("stat_settings");
        this.setNormBetweenArrays(statSettings.element("norm_between_arrays").attributeValue("method"));
        this.setDoNormBetween(Boolean.parseBoolean(statSettings.element("norm_between_arrays").attributeValue("do")));
        this.setBackGroundCorrectionMethod(statSettings.element("background_correction").attributeValue("method"));
        this.setDoBackgroundCorrection(Boolean.parseBoolean(statSettings.element("background_correction").attributeValue("do")));
        this.setStatStrategy(statSettings.element("strategy").attributeValue("method"));
        this.setPValCorrectionMethod(statSettings.element("pval_correction").attributeValue("method"));
        this.setPValCutoffValue(statSettings.element("pval_cutoff").attributeValue("value"));
        this.setMinLogFoldChangeOf2(Boolean.parseBoolean(statSettings.element("lfc_min_2").attributeValue("value")));

    }// end load

    
}
