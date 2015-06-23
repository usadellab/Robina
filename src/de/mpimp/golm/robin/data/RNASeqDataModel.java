/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.data;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import de.mpimp.golm.robin.rnaseq.trimmomatic.TMTrimmerArguments;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author marc
 */
public class RNASeqDataModel extends HTDataModel {

    private ArrayList<FastQFile> inputFiles;
    private File referenceFile;
    private File GFF3annotationFile;
    private String sFileSep = System.getProperty("file.separator");
    private String outputDir;
    private String experimentName;
    private String contrastTerms;
    private String mappingTool;
    private String mappingToolSettings;
    private WarningsHandler warnings;
    private String tempRoot;
    private VERSION qualityEncoding;
    private ArrayList<TMTrimmerArguments> trimmers;
    private HashMap<String, Integer> conditions;
    private HashMap<String, RNASeqSample> samples;
    private REFERENCE_TYPE referenceType;
    private String referenceindexName;
    private RNASEQ_ANALYSIS_TYPE analysisType;
    private RNASEQ_WORKFLOW_STAGE workflowStage;
    private boolean importBAMSAM;
    private boolean importCountsTable;
    private boolean canSkipMapping = false;
    private String contrastTable;
    private File projectDir;
    private File inputDir;
    private File logDir;
    private File plotsDir;
    private File qualitychecksDir;
    private File detailedResultsDir;
    private File sourceDir;
    private String normGCMethod = "none";
    private File countsTableFile;

    public enum RNASEQ_WORKFLOW_STAGE {

        RAW_IMPORT(1),
        QUALITY_CHECK_SETUP(2),
        QUALITY_CHECK_DONE(3),
        TRIMMOMATIC_SETUP(4),
        TRIMMOMATIC_DONE(5),
        LIBRARY_CONFIG(6),
        MAPPING_CONFIG(7),
        STATS_CONFIG(8),
        COMPLETE_ANALYSIS(9);
        private final int stage;
        private static Map<Integer, RNASEQ_WORKFLOW_STAGE> nameMap = new HashMap<Integer, RNASEQ_WORKFLOW_STAGE>();

        static {
            for (RNASEQ_WORKFLOW_STAGE s : EnumSet.allOf(RNASEQ_WORKFLOW_STAGE.class)) {
                nameMap.put(s.value(), s);
            }
        }

        RNASEQ_WORKFLOW_STAGE(int stage) {
            this.stage = stage;
        }

        public int value() {
            return this.stage;
        }

        public static RNASEQ_WORKFLOW_STAGE getStageByNumber(int i) {
            return nameMap.get(i);
        }
    }

    public enum RNASEQ_ANALYSIS_TYPE {
        LIMMA,
        EDGER,
        DESEQ
    }

    public static enum REFERENCE_TYPE {
        TRANSCRIPTOME,
        GENOME,
        NONE
    }
    //limma specific settings
    protected String statStrategy;
    protected String pValCorrectionMethod;
    protected String pValCutoffValue;
    protected boolean minLogFoldChangeOf2;
    protected boolean writeRawExprs;
    //edgeR specific settings
    private String dispersion = "auto";

    public RNASeqDataModel() {
        initializeModel();
    }

    private void initializeModel() {
        inputFiles = new ArrayList<FastQFile>();
        this.trimmers = new ArrayList<TMTrimmerArguments>();
        this.samples = new HashMap<String, RNASeqSample>();
        this.conditions = new HashMap<String, Integer>();
        this.importBAMSAM = false;
        this.importCountsTable = false;
    }

    public RNASEQ_WORKFLOW_STAGE getWorkflowStage() {
        return workflowStage;
    }

    public void setWorkflowStage(RNASEQ_WORKFLOW_STAGE workflowStage) {
        this.workflowStage = workflowStage;
        SimpleLogger.getLogger(true).logMessage("entering stage " + workflowStage.toString());
    }

    public void setImportCountsTableFile(File file) {
        this.countsTableFile = file;
    }

    public File getImportCountsTableFile() {
        return countsTableFile;
    }

    public String getNormGCMethod() {
        return normGCMethod;
    }

    public void setNormGCMethod(String normGCMethod) {
        this.normGCMethod = normGCMethod;
    }

    public String getDispersion() {
        return dispersion;
    }

    public void setDispersion(String dispersion) {
        this.dispersion = dispersion;
    }

    public boolean isImportBAMSAM() {
        return importBAMSAM;
    }

    public void setImportBAMSAM(boolean importBAMSAM) {
        this.importBAMSAM = importBAMSAM;
    }

    public boolean isImportCountsTable() {
        return importCountsTable;
    }

    public void setImportCountsTable(boolean importCountsTable) {
        this.importCountsTable = importCountsTable;
    }

    public File getGFF3annotationFile() {
        return GFF3annotationFile;
    }

    public void setGFF3annotationFile(File GFF3annotationFile) {
        this.GFF3annotationFile = GFF3annotationFile;
    }

    public String getSampleByInputfileName(FastQFile testfile) {

        for (RNASeqSample sample : samples.values()) {
            for (FastQFile file : sample.getInputFiles()) {
                if (file.getName().equals(testfile.getName())) {
                    return sample.getSampleName();
                }
            }
        }
        // this should never happen
        return "not found";
    }

    public String getMainAnalysisSettingsAsString() {
        StringBuilder b = new StringBuilder();
        b.append("\tAnalysis method: " + this.getAnalysisType() + "\n");
        if (this.getAnalysisType() == RNASEQ_ANALYSIS_TYPE.EDGER) {
            b.append("\testimate dispersion: " + this.getDispersion() + "\n");
        }
        b.append("\tP-value correction: " + this.getPValCorrectionMethodHuman() + "\n");
        b.append("\tP-value cut-off: " + this.getPValCutoffValue() + "\n");
        b.append("\tLog2-fold change <1 ignored: " + (this.isMinLogFoldChangeOf2() ? "yes" : "no") + "\n");
        b.append("\tWrite out raw normalized expression estimates: " + (this.isWriteRawExprs() ? "yes" : "no") + "\n");
        b.append("\tComparisons defined: " + this.getContrastTerms());
        return b.toString();
    }

    public String getPValCorrectionMethodHuman() {
        HashMap<String, String> pCorrToHuman = new HashMap<String, String>() {
            {
                put("BH", "Benjamini-Hochberg (1995)");
                put("BY", "Benjamini-Yekutieli (2001)");
                put("fdr", "Benjamini-Hochberg (1995)");
                put("holm", "Holm (1979)");
                put("none", "none");
            }
        };
        return pCorrToHuman.get(this.getPValCorrectionMethod());
    }

    public File getDetailedResultsDir() {
        return detailedResultsDir;
    }

    public File getInputDir() {
        return inputDir;
    }

    public File getLogDir() {
        return logDir;
    }

    public File getPlotsDir() {
        return plotsDir;
    }

    public File getQualitychecksDir() {
        return qualitychecksDir;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setContrastTable(String contrastTable) {
        this.contrastTable = contrastTable;
    }

    public String getContrastTable() {
        return contrastTable;
    }

    public RNASEQ_ANALYSIS_TYPE getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(RNASEQ_ANALYSIS_TYPE analysisType) {
        this.analysisType = analysisType;
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

    public boolean isWriteRawExprs() {
        return writeRawExprs;
    }

    public void setWriteRawExprs(boolean writeRawExprs) {
        this.writeRawExprs = writeRawExprs;
    }

    public REFERENCE_TYPE getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(REFERENCE_TYPE refererenceType) {
        this.referenceType = refererenceType;
    }

    public String getReferenceindexName() {
        return referenceindexName;
    }

    public void setReferenceindexName(String referenceindexName) {
        this.referenceindexName = referenceindexName;
    }

    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
        this.inputDir = new File(projectDir, "input");
        this.logDir = new File(projectDir, "log");
        this.plotsDir = new File(projectDir, "plots");
        this.qualitychecksDir = new File(projectDir, "qualitychecks");
        this.detailedResultsDir = new File(projectDir, "detailed_results");
        this.sourceDir = new File(projectDir, "source");
    }

    public void addTrimmer(TMTrimmerArguments args) {
        this.trimmers.add(args);
    }

    public void clearTrimmers() {
        this.trimmers.clear();
    }

    public ArrayList<TMTrimmerArguments> getTrimmers() {
        return trimmers;
    }

    public HashMap<String, Integer> getConditions() {
        return conditions;
    }

    public void addCondition(String condName) {
        this.conditions.put(condName, 0);
    }

    public boolean canSkipMapping() {
        return canSkipMapping;
    }

    public void setCanSkipMapping(boolean canSkipMapping) {
        this.canSkipMapping = canSkipMapping;
    }

    public void setConditions(HashMap<String, Integer> con) {
        this.conditions = con;
    }

    public HashMap<String, RNASeqSample> getSamples() {
        return samples;
    }

    public void addSample(RNASeqSample sample) {
        this.conditions.put(sample.getCondition(), this.conditions.get(sample.getCondition()) + 1);
        this.samples.put(sample.getSampleName(), sample);
    }

    public String getContrastTerms() {
        return contrastTerms;
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

    public ArrayList<FastQFile> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(ArrayList<FastQFile> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public File getReferenceFile() {
        return referenceFile;
    }

    public void setReferenceFile(File referenceFile) {
        this.referenceFile = referenceFile;
    }

    public String getsFileSep() {
        return sFileSep;
    }

    public void setsFileSep(String sFileSep) {
        this.sFileSep = sFileSep;
    }

    public String getTempRoot() {
        return tempRoot;
    }

    public void setTempRoot(String tempRoot) {
        this.tempRoot = tempRoot;
    }

    public WarningsHandler getWarnings() {
        return warnings;
    }

    public void setWarnings(WarningsHandler warnings) {
        this.warnings = warnings;
    }

    public void setQualityEncoding(VERSION vERSION) {
        this.qualityEncoding = vERSION;
    }

    public VERSION getQualityEncoding() {
        return this.qualityEncoding;
    }

    public String getMappingTool() {
        return mappingTool;
    }

    public void setMappingTool(String mappingTool) {
        this.mappingTool = mappingTool;
    }

    public String getMappingToolSettings() {
        return mappingToolSettings;
    }

    public void setMappingToolSettings(String mappingToolSettings) {
        this.mappingToolSettings = mappingToolSettings;
    }

    public String getGroupsTerm() {
        ArrayList<String> conds = new ArrayList<String>(this.getConditions().keySet());
        Collections.sort(conds);

        ArrayList<String> terms = new ArrayList<String>();
        for (String cond : conds) {
            terms.add("rep(\"" + cond + "\", " + this.getConditions().get(cond) + ")");
        }

        String groupsTerm = StringUtils.join(terms, ", ");
        return "as.factor( c(" + groupsTerm + "))";
    }

    public void store(File dataFile) throws Exception {
        if (dataFile == null) {
            dataFile = new File(this.getSourceDir(), this.getExperimentName() + "_data.xml");
        }
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        if (!dataFile.canWrite()) {
            new SimpleErrorMessage(null, "Can't write to specified file:\n" + dataFile.getCanonicalPath());
            return;
        }

        FileWriter writer = new FileWriter(dataFile);

        // store contents of all the nice fields...
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("project").addAttribute("name", this.getExperimentName());
        root.addAttribute("type", "rna_seq_analysis");
        root.addAttribute("stage", Integer.toString(this.getWorkflowStage().value()));

        Element inputFiles = root.addElement("input_files");
        for (FastQFile inputfile : this.getInputFiles()) {
            Element file = inputFiles.addElement("file");
            file.addAttribute("name", inputfile.getCanonicalPath());
        }

        if (this.getWorkflowStage().value() <= 5) {
            writeXMLDoc(doc, writer);
            return;
        }

        Element trimmers = root.addElement("trimmer_modules");
        for (TMTrimmerArguments module : this.getTrimmers()) {
            Element trimmer = trimmers.addElement("trimmer_module");
            trimmer.addAttribute("id", module.getIdentifier());
            for (String arg : module.keySet()) {
                Element argset = trimmer.addElement("arg");
                argset.addAttribute("name", arg);
                argset.addAttribute("value", String.valueOf(module.get(arg)));
            }
        }

        Element samples = root.addElement("samples");
        for (RNASeqSample sample : this.getSamples().values()) {
            Element s = samples.addElement("sample");
            s.addAttribute("name", sample.getSampleName());
            s.addAttribute("condition", sample.getCondition());

            for (FastQFile f : sample.getInputFiles()) {
                Element file = s.addElement("file");
                file.addAttribute("name", f.getCanonicalPath());
            }
        }

        if (this.getWorkflowStage().value() <= 6) {
            writeXMLDoc(doc, writer);
            return;
        }

        // reference
        if (!this.isImportCountsTable()) {
            Element refType = root.addElement("reference");
            refType.addAttribute("type", this.getReferenceType().toString());
            //refType.addAttribute("file", this.getReferenceFile().getCanonicalPath());
            refType.addAttribute("index", this.getReferenceindexName());
            if (this.getReferenceType() == REFERENCE_TYPE.GENOME) {
                refType.addAttribute("annotation", this.getGFF3annotationFile().getCanonicalPath());
            }
        }

        //mapping
        Element mapSettings = root.addElement("mapping");
        mapSettings.addAttribute("tool", this.getMappingTool());
        mapSettings.addAttribute("settings", this.getMappingToolSettings());

        if (this.getWorkflowStage().value() <= 7) {
            writeXMLDoc(doc, writer);
            return;
        }

        // stats settings
        Element stats = root.addElement("statistics");
        stats.addAttribute("method", analysisType.toString());
        stats.addAttribute("dispersion", this.getDispersion());
        stats.addAttribute("norm_gc_method", this.getNormGCMethod());
        stats.addElement("pval_correction").addAttribute("method", pValCorrectionMethod);
        stats.addElement("pval_cutoff").addAttribute("value", pValCutoffValue);
        stats.addElement("lfc_min_2").addAttribute("value", String.valueOf(minLogFoldChangeOf2));
        stats.addElement("write_RPKM").addAttribute("value", Boolean.toString(this.isWriteRawExprs()));

        // contrasts
        stats.addElement("contrasts").addAttribute("value", this.getContrastTerms());

        writeXMLDoc(doc, writer);
    }

    public void load(File dataFile) throws Exception {
        FileReader reader = new FileReader(dataFile);
        Element dataRoot = new SAXReader().read(reader).getRootElement();

        this.setExperimentName(dataRoot.attributeValue("name"));
        this.setWorkflowStage(RNASEQ_WORKFLOW_STAGE.getStageByNumber(
                Integer.parseInt(dataRoot.attributeValue("stage"))));

        Element inputFiles = dataRoot.element("input_files");
        Iterator<Element> iter = inputFiles.elementIterator("file");
        while (iter.hasNext()) {
            this.getInputFiles().add(new FastQFile(iter.next().attributeValue("name")));
        }

        if (this.getWorkflowStage().value() <= 5) {
            return;
        }

        // load trimmers
        Element trimmerModules = dataRoot.element("trimmer_modules");
        Iterator<Element> modIter = trimmerModules.elementIterator("trimmer_module");
        while (modIter.hasNext()) {
            Element module = modIter.next();
            TMTrimmerArguments tmargs = new TMTrimmerArguments(module.attributeValue("id"));
            Iterator<Element> argIter = module.elementIterator("arg");
            while (argIter.hasNext()) {
                Element arg = argIter.next();
                tmargs.put(arg.attributeValue("name"), arg.attributeValue("value"));
            }
            this.addTrimmer(tmargs);
        }

        // load samples
        Element samples = dataRoot.element("samples");
        Iterator<Element> samIter = samples.elementIterator("sample");
        while (samIter.hasNext()) {
            Element sample = samIter.next();
            String name = sample.attributeValue("name");
            String condition = sample.attributeValue("condition");
            this.addCondition(condition);

            ArrayList<FastQFile> files = new ArrayList<FastQFile>();
            Iterator<Element> sampleFilesIter = sample.elementIterator("file");
            while (sampleFilesIter.hasNext()) {
                Element file = sampleFilesIter.next();
                files.add(new FastQFile(file.attributeValue("name")));
            }
            RNASeqSample newSample = new RNASeqSample(condition, files);
            newSample.setSampleName(name);
            this.addSample(newSample);
        }

        if (this.getWorkflowStage().value() <= 6) {
            return;
        }


        // reference
        Element reference = dataRoot.element("reference");
        this.setReferenceType(reference.attributeValue("type").equals("TRANSCRIPTOME") ? REFERENCE_TYPE.TRANSCRIPTOME : REFERENCE_TYPE.GENOME);
        this.setReferenceindexName(reference.attributeValue("index"));
        if (this.getReferenceType() == REFERENCE_TYPE.GENOME) {
            this.setGFF3annotationFile(new File(reference.attributeValue("annotation")));
        }

        // mapping
        Element mapping = dataRoot.element("mapping");
        this.setMappingTool(mapping.attributeValue("tool"));
        this.setMappingToolSettings(mapping.attributeValue("settings"));

        if (this.getWorkflowStage().value() <= 7) {
            return;
        }

        // stats
        Element stats = dataRoot.element("statistics");

        String method = stats.attributeValue("method");
        if (method.equals("EDGER")) {
            this.setAnalysisType(RNASEQ_ANALYSIS_TYPE.EDGER);
        }
        if (method.equals("DESEQ")) {
            this.setAnalysisType(RNASEQ_ANALYSIS_TYPE.DESEQ);
        }
        if (method.equals("LIMMA")) {
            this.setAnalysisType(RNASEQ_ANALYSIS_TYPE.LIMMA);
        }

        this.setDispersion(stats.attributeValue("dispersion"));
        this.setNormGCMethod(stats.attributeValue("norm_gc_method"));

        this.setPValCorrectionMethod(stats.element("pval_correction").attributeValue("method"));
        this.setPValCutoffValue(stats.element("pval_cutoff").attributeValue("value"));
        this.setMinLogFoldChangeOf2(Boolean.parseBoolean(stats.element("lfc_min_2").attributeValue("value")));
        this.setWriteRawExprs(Boolean.parseBoolean(stats.element("write_RPKM").attributeValue("value")));
        this.setContrastTerms(stats.element("contrasts").attributeValue("value"));
    }
}
