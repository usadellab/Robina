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
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter; 

/**
 *
 * @author marc
 */
public class AffyArrayDataModel extends ArrayDataModel {

    private String      chipType;
    private String      affyNormalizationMethod;
    private File        groupsFile;
    private boolean     isCustomImport;
    private File        customCDFFile;
    private String      PClist = "list(c(1,2))"; // initialize to always show PC 1 & 2



    public AffyArrayDataModel() {
        super();
    }

    public String getPCList() {
        return PClist;
    }

    public void setPCList(String PClist) {
        this.PClist = PClist;
    }



    public File getGroupsFile() {
        return groupsFile;
    }

    public void setGroupsFile(File groupsFile) {
        this.groupsFile = groupsFile;
    }

    public String getAffyNormalizationMethod() {
        return affyNormalizationMethod;
    }

    public void setAffyNormalizationMethod(String affyNormalizationMethod) {
        this.affyNormalizationMethod = affyNormalizationMethod;
    }

    public String getChipType() {
        return chipType;
    }

    public void setChipType(String chipType) {
        this.chipType = chipType;
    }

    public void setCustomCDFimport(boolean b) {
        this.isCustomImport = b;
    }


    public void setCustomCDFFile(File CDFFile) {
        this.customCDFFile = CDFFile;
    }
    
    public File getCustomCDFFile() {
        return this.customCDFFile;
    }

    public boolean isCustomCDFimport() {
        return this.isCustomImport;
    }

    @Override
    public String generateTextReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String generateHTMLReport() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        root.addAttribute("type", "affymetrix_analysis");

        // groups file
        root.addElement("groups_file").addAttribute("path", this.getGroupsFile().getCanonicalPath());

        //analysis type
        Element analysis = root.addElement("analysis");
        analysis.addAttribute("method", (analysisType == AnalysisStrategy.LIMMA) ? "limma" : "rankprod" );

        // stats
        Element stats = root.addElement("stat_settings");
        stats.addElement("normalization").addAttribute("method", affyNormalizationMethod);
        stats.addElement("strategy").addAttribute("method", statStrategy);
        stats.addElement("pval_correction").addAttribute("method", pValCorrectionMethod);
        stats.addElement("pval_cutoff").addAttribute("value", pValCutoffValue);
        stats.addElement("lfc_min_2").addAttribute("value", String.valueOf(minLogFoldChangeOf2));


        XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
        xmlWriter.write(doc);
        xmlWriter.flush();
        writer.close();

    }

    @Override
    public void load(File dataFile) throws Exception {
        FileReader reader = new FileReader(dataFile);
        Element dataRoot = new SAXReader().read(reader).getRootElement();

        // groups file
        this.setGroupsFile(new File(dataRoot.element("groups_file").attributeValue("path")));

        // analysis type
        Element analysis = dataRoot.element("analysis");
        this.setAnalysisType( (analysis.attributeValue("method").equals("limma")) ?
            AnalysisStrategy.LIMMA : AnalysisStrategy.RANKPROD);

        // load statistics settings
        Element statSettings = dataRoot.element("stat_settings");
        this.setAffyNormalizationMethod(statSettings.element("normalization").attributeValue("method"));
        this.setStatStrategy(statSettings.element("strategy").attributeValue("method"));
        this.setPValCorrectionMethod(statSettings.element("pval_correction").attributeValue("method"));
        this.setPValCutoffValue(statSettings.element("pval_cutoff").attributeValue("value"));
        this.setMinLogFoldChangeOf2(Boolean.parseBoolean(statSettings.element("lfc_min_2").attributeValue("value")));
    }

    @Override
    public String toString() {
        StringBuilder modelString = new StringBuilder();

        modelString.append("###################################\n");
        modelString.append("# Robin affymetrix data analysis summary\n");
        modelString.append("# "+this.getExperimentName()+"\n");
        modelString.append("# "+Utilities.currentDateAsString()+"; "+Utilities.currentTimeAsString()+"\n");
        modelString.append("###################################\n");
        modelString.append("\n");
        modelString.append("# Input files:\n");
        modelString.append(StringUtils.join(this.getInputFiles(), "\n"));
        modelString.append("\n\n");
        modelString.append("# Normalization settings for quality control\n");
        modelString.append("normalization method: " + this.getAffyNormalizationMethod() + "\n");
        modelString.append("P-value correction method: " + this.getPValCorrectionMethod() + "\n");
        modelString.append("analysis strategy: " +
                ((this.getAnalysisType() == ArrayDataModel.AnalysisStrategy.LIMMA) ? "Limma" : "Rank Product")
                +"\n");
        modelString.append("\n");
        modelString.append("# Normalization settings for main analysis\n");
        modelString.append("normalization method: " + this.getAffyNormalizationMethod() + "\n");
        modelString.append("P-value correction method: " + this.getPValCorrectionMethod() + "\n");
        modelString.append("Multiple testing strategy: " + this.getStatStrategy() + "\n");
        modelString.append("P-value cut-off value for significant differential expression: " + this.getPValCutoffValue() + "\n");
        modelString.append("Genes that showed a log2-fold change smaller than two ignored: " +
                    (this.isMinLogFoldChangeOf2() ? "yes\n" : "no\n"));

        modelString.append("\n");
        modelString.append("# The analysis produced the following warnings\n");
        if (!this.getWarningsHandler().hasWarnings()) {
            modelString.append("none\n");
        } else {
            for (Warning w : this.getWarningsHandler().getWarnings()) {
                modelString.append("Type: " + w.getType() + "\n");
                modelString.append("Severity: " + w.getSeverity() + "\n");
                modelString.append(w.getMessage().trim() + "\n");
                modelString.append("//\n");
            }
        }
        return modelString.toString();
    }



}
