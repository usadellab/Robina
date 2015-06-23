/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.data;

import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

/**
 *  Generic class to hold data on an array experiment
 * @author marc
 */
public abstract class ArrayDataModel extends HTDataModel {

    // general
    public static enum InputDataType {
        GENERIC,
        AFFYMETRIX,
        AGILENT
        //...
    }
    
    public static enum AnalysisStrategy {
        LIMMA,
        RANKPROD
        //...
    }
    
    protected ArrayList<String>   inputFiles;
    protected String              sFileSep = System.getProperty("file.separator");
    protected String              outputDir;
    protected String              experimentName;
    protected String              contrastTerms;
   
    protected String              tempRoot; // base path for creating temporary files for the analysis of the data in this model
    @Deprecated
    protected String              dataType; // genepix, spot, imagene, generic, affymetrix etc. use inputType instead
    protected InputDataType       inputType;
    protected AnalysisStrategy    analysisType;
    
    // limma-specific settings
    protected String              statStrategy;
    protected String              pValCorrectionMethod;
    protected String              pValCutoffValue;
    protected boolean             minLogFoldChangeOf2;

    protected boolean             writeRawExprs;
    
    
    public ArrayDataModel() {
        // initialize instance variables
        super();
        inputFiles = new ArrayList<String>();
        this.setInputType(InputDataType.GENERIC);
    }
    
    // these have to be imlpemented by the individual subclasses
    public abstract String  generateTextReport();
    public abstract String  generateHTMLReport();
    public abstract void    store(File dataFile) throws Exception;
    public abstract void    load(File dataFile) throws Exception;


    public Properties getAsProperties() {
        Properties props = new Properties();
        props.setProperty("chipID", "non-affymetrix");
        props.setProperty("expName", this.getExperimentName());
        
        // this list may have to be extended in future

        return props;
    }

    public String getDataType() {
        return dataType;
    }

    public File getOutputDirFile() {
        return new File(this.outputDir);
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public AnalysisStrategy getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisStrategy analysisType) {
        this.analysisType = analysisType;
    }    

    public InputDataType getInputType() {
        return inputType;
    }

    public void setInputType(InputDataType inputType) {
        this.inputType = inputType;
    }   
    
    public void addInputFile(String path) {
        inputFiles.add(path);                
    }    
   
    public String getTempRoot() {
        return tempRoot;
    }

    public void setTempRoot(String tempRoot) {
        this.tempRoot = tempRoot;
    }

    
    public String getContrastTerms() {
        return contrastTerms;
    }

    public String getContrastNames() {
        String terms = contrastTerms.replaceAll("\\n", "");
        String[] contrasts = terms.split("\\s*,\\s*");
        return "\"" + StringUtils.join(contrasts, "\", \"") + "\"";
    }

    public void setContrastTerms(String contrastTerms) {
        this.contrastTerms = contrastTerms;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public ArrayList<String> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(ArrayList<String> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public boolean isMinLogFoldChangeOf2() {
        return minLogFoldChangeOf2;
    }

    public void setMinLogFoldChangeOf2(boolean minLogFoldChangeOf2) {
        this.minLogFoldChangeOf2 = minLogFoldChangeOf2;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
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

    public String getSFileSep() {
        return sFileSep;
    }

    public void setSFileSep(String sFileSep) {
        this.sFileSep = sFileSep;
    }

    public String getStatStrategy() {
        return statStrategy;
    }

    public void setStatStrategy(String statStrategy) {
        this.statStrategy = statStrategy;
    }

    public boolean isWriteRawExprs() {
        return writeRawExprs;
    }

    public void setWriteRawExprs(boolean writeRawExprs) {
        this.writeRawExprs = writeRawExprs;
    }
}
