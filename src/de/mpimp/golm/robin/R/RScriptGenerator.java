/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.R;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.data.AffyArrayDataModel;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

/**
 * Utility class that generates R code as 
 * a String - should be implemented as a 
 * singleton but i'm too lazy to do this.
 * @author marc
 */
public class RScriptGenerator {

    private String pathToR = "";
    private Properties settings;

    /**
     * Empty standard constructor
     */
    public RScriptGenerator() {
    }

    public RScriptGenerator(String rPath, Properties prop) {
        this.pathToR = rPath;
        this.settings = prop;
    }

    /**
     * 
     * @param qcMethods
     * @param jpegDevParams 
     * @param sParamOutputFile 
     * @param sParamPictureFile 
     * @param fileNames 
     * @param sParamInputFiles 
     * @param normMethod 
     * @return
     */
    public String generateRScript(List<String> qcMethods,
            String jpegDevParams,
            String sParamPictureFile,
            String fileNames,
            String sParamInputFiles,
            AffyArrayDataModel model) {

        StringBuilder script = new StringBuilder();

        // first load the common header that loads the data 
        // and imports all nesessary libraries
        script.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/qc_template_header.R")));

        

        // now load all the chosen qc scripts
        for (String meth : qcMethods) {
            script.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/qc_template_" + meth + ".R")));
        }
        //TODO replace the tags
        String template = script.toString();

        template = template.replaceAll("__IMPORT_CDF_FILE__", model.isCustomCDFimport() ? "TRUE" : "FALSE");
        if (model.isCustomCDFimport()) {
            template = template.replaceAll("__CDF_FILE_NAME__", model.getCustomCDFFile().getName());
            template = template.replaceAll("__CDF_FILE_PATH__", model.getCustomCDFFile().getParent().replaceAll("\\\\", "/")); //replaceAll("\\\\", "\\\\\\\\\\\\\\\\"));
        }

        template = template.replaceAll("__PC_PLOT__LIST__", model.getPCList());
        template = template.replaceAll("__PARAM_NORM_METHOD__", model.getAffyNormalizationMethod());
        //template = template.replaceAll("__PARAM_FILES__", sParamInputFiles.replaceAll("\\\\", "\\\\\\\\\\\\\\\\"));
        template = template.replaceAll("__PARAM_FILES__", sParamInputFiles.replaceAll("\\\\", "/"));
        template = template.replaceAll("__PARAM_FILENAMES__", fileNames);
        template = template.replaceAll("__PARAM_JPEG_PARAMS__", jpegDevParams);
        //template = template.replaceAll("__PARAM_OUTPUTFILE__", sParamPictureFile.replaceAll("\\\\", "\\\\\\\\\\\\\\\\"));
        template = template.replaceAll("__PARAM_OUTPUTFILE__", sParamPictureFile.replaceAll("\\\\", "/"));
        try {
            template = template.replaceAll("__PROJECT_DIR__", model.getOutputDirFile().getCanonicalPath().replaceAll("\\\\", "/"));
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        //System.out.println("picfile="+sParamPictureFile.replaceAll("\\", "\\\\"));
        //System.out.println("picfile=" + sParamPictureFile.replaceAll("\\\\", "\\\\\\\\"));

        return template;
    }

    /**
     * 
     * @param model
     * @param dataModel
     * @return
     */
    public String generateRScript(AnalysisDesignModel model, AffyArrayDataModel dataModel) {

        String template = Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/affy_template_limma.R"));

        // replace all the ##PARAM_XYZ## tags in the template
        // with the actual values
        String fileSep = System.getProperty("file.separator");
        String plotsDir = null, outputDir = null;
        try {
            plotsDir = new File(dataModel.getOutputDir(), "plots").getCanonicalPath();
            //plotsDir = plotsDir.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
            plotsDir = plotsDir.replaceAll("\\\\", "/");

            outputDir = new File(dataModel.getOutputDir()).getCanonicalPath();
            System.out.println("outpath before: <" + outputDir + ">");
            //outputDir = outputDir.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
            outputDir = outputDir.replaceAll("\\\\", "/");
            System.out.println("outpath after: <" + outputDir + ">");

        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        template = template.replaceAll("__IMPORT_CDF_FILE__", dataModel.isCustomCDFimport() ? "TRUE" : "FALSE");
        if (dataModel.isCustomCDFimport()) {
            template = template.replaceAll("__CDF_FILE_NAME__", dataModel.getCustomCDFFile().getName());
            template = template.replaceAll("__CDF_FILE_PATH__", dataModel.getCustomCDFFile().getParent().replaceAll("\\\\", "/"));
        }

        template = template.replaceAll("__PARAM_FILES__", model.getParamFilenamesTerm());
        template = template.replaceAll("__PARAM_GROUPS__", model.getGroupsTerm());
        template = template.replaceAll("__PARAM_MODEL__", model.getModelMatrixTerm());
        template = template.replaceAll("__PARAM_CONTRASTS__", model.getContrastTerms());
        template = template.replaceAll("__PARAM_NORM_METHOD__", dataModel.getAffyNormalizationMethod());
        template = template.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        template = template.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        template = template.replaceAll("__OUTPUT_DIR__", outputDir);
        template = template.replaceAll("__DOWN_WEIGH_PROCEDURE__", dataModel.getStatStrategy());
        template = template.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "TRUE" : "FALSE");
        template = template.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");

        //if (Utilities.isWindows()) {
        //template = template.replaceAll("__PLOT_DIR__", settings.getProperty("outputPath").replaceAll("\\\\", "\\\\\\\\")+"\\\\"+"plots"+"\\\\");
        //template = template.replaceAll("__PLOT_DIR__", (settings.getProperty("outputPath").replaceAll("\\\\", "\\\\\\\\")+"\\\\\\\\"+"plots"+"\\\\\\\\"));
        //} else {
        template = template.replaceAll("__PLOT_DIR__", plotsDir);
        //}

        template = template.replaceAll("__EXPERIMENT_NAME__", dataModel.getExperimentName());
        return template;
    }

    public String generateSingleColorRankProdScript(
            ArrayList<String> files,
            ArrayList<Integer> classes,
            ArrayList<Integer> origins,
            SCArrayDataModel dataModel) {

        StringBuilder scriptBuilder = new StringBuilder();
        
        // first load the common header that loads the data
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_header.R")));

        // first load the common header that loads the data
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_main_rankprod.R")));


        // create strings from the arraylists
        String classStr = StringUtils.join(classes, ", ") + "\n";
        String oriStr = StringUtils.join(origins, ", ") + "\n";
        String inputFiles = ("\"" + StringUtils.join(dataModel.getInputFiles(), "\",\n\"") + "\"\n").replaceAll("\\\\", "/");
        String tmpRoot = dataModel.getTempRoot().replaceAll("\\\\", "/");
        String outPath = dataModel.getOutputDir().replaceAll("\\\\", "/");
        String genCols = "";
        String layout = "";
        String genID = "", genIDstart = "ID";

        //if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
        // put  together generic columns ....
        ArrayList<String> genericColumns = new ArrayList<String>();
        for (String key : new String[]{"G", "Gb"}) {
            genericColumns.add(key + "=\"" + dataModel.getGenericColumns().get(key) + "\"");
        }
        genCols = StringUtils.join(genericColumns, ", ");
        genID = dataModel.getGenericColumns().get("ID");
        genIDstart = dataModel.getIDcolumnStart();
        // ... and printer layout
        ArrayList<String> genLayout = new ArrayList<String>();
        for (String key : dataModel.getPrinterLayout().keySet()) {
            genLayout.add(key + "=" + dataModel.getPrinterLayout().get(key));
        }
        layout = StringUtils.join(genLayout, ", ");
        //} else {
        genIDstart = dataModel.getIDcolumnStart();
        

        String finalScript = scriptBuilder.toString();

        // replace tags
        finalScript = finalScript.replaceAll("__PARAM_CLASSES__", classStr);
        finalScript = finalScript.replaceAll("__PARAM_ORIGIN__", oriStr);
        finalScript = finalScript.replaceAll("__SOURCE_TYPE__", dataModel.getInputType().name().toLowerCase());
        finalScript = finalScript.replaceAll("__TEMP_ROOT__", tmpRoot);
        finalScript = finalScript.replaceAll("__COLUMNS__", genCols);
        finalScript = finalScript.replaceAll("__PRINT_LAYOUT__", layout);
        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__NORM_BETWEEN__", dataModel.isDoNormBetween() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__BGCORR_METHOD__", dataModel.getBackGroundCorrectionMethod());
        finalScript = finalScript.replaceAll("__NORM_BETWEEN_METHOD__", dataModel.getNormBetweenArrays());
        finalScript = finalScript.replaceAll("__EXPERIMENT_NAME__", dataModel.getExperimentName());
        finalScript = finalScript.replaceAll("__CONTRAST_TERMS__", dataModel.getContrastTerms());
        finalScript = finalScript.replaceAll("__DOWN_WEIGH_PROCEDURE__", dataModel.getStatStrategy());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        finalScript = finalScript.replaceAll("__IDENTIFIER__", genID);
        finalScript = finalScript.replaceAll("__ID_COLUMN_START__", genIDstart);
        finalScript = finalScript.replaceAll("__BACKGROUND_CORRECT__", dataModel.isDoBackgroundCorrection() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__PARAM_FILES__", inputFiles);

        return finalScript;

    }

    public String generateRankProdMainScript(AffyArrayDataModel dataModel, ArrayList<String> files, ArrayList<Integer> classes, ArrayList<Integer> origin, String outpath) {
        String template = Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/affy_template_rankprod.R"));

        // create strings from the arraylists
        String fileStr = "\"" + StringUtils.join(files, "\",\n\"") + "\"\n";
        String classStr = StringUtils.join(classes, ", ") + "\n";
        String oriStr = StringUtils.join(origin, ", ") + "\n";

        //fileStr = fileStr.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        fileStr = fileStr.replaceAll("\\\\", "/");
        //outpath = outpath.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        outpath = outpath.replaceAll("\\\\", "/");

        template = template.replaceAll("__PARAM_FILENAMES__", fileStr);
        template = template.replaceAll("__PARAM_CLASSES__", classStr);
        template = template.replaceAll("__PARAM_ORIGIN__", oriStr);
        template = template.replaceAll("__PARAM_OUTPATH__", outpath);
        template = template.replaceAll("__IMPORT_CDF_FILE__", dataModel.isCustomCDFimport() ? "TRUE" : "FALSE");
        if (dataModel.isCustomCDFimport()) {
            template = template.replaceAll("__CDF_FILE_NAME__", dataModel.getCustomCDFFile().getName());
            template = template.replaceAll("__CDF_FILE_PATH__", dataModel.getCustomCDFFile().getParent().replaceAll("\\\\", "/"));
        }
        template = template.replaceAll("__EXPNAME__", dataModel.getExperimentName());
        template = template.replaceAll("__PARAM_NORM_METHOD__", dataModel.getAffyNormalizationMethod());
        template = template.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        return template;
    }

    public String generateSingleColorQCScript(ArrayList<String> qcMethods, SCArrayDataModel dataModel) {
        StringBuilder scriptBuilder = new StringBuilder();
       
        // first load the common header that loads the data
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_header.R")));
     
        // assemble all functions
        for (String method : qcMethods) {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_qc_" + method + ".R")));
        }

        //TODO enter data from the model
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        String tmpRoot = null;
        try {
            tmpRoot = new File(qualDir, dataModel.getExperimentName()).getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        String genCols = "";
        String layout = "";
        String genID = "", genIDstart = "ID";

        //if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
        // put  together generic columns ....
        ArrayList<String> genericColumns = new ArrayList<String>();
        for (String key : new String[]{"G", "Gb"}) {
            genericColumns.add(key + "=\"" + dataModel.getGenericColumns().get(key) + "\"");
        }
        genCols = StringUtils.join(genericColumns, ", ");

        //DEBUG
        System.out.println("GENCOLS=" + genCols);

        genID = dataModel.getGenericColumns().get("ID");
        genIDstart = dataModel.getIDcolumnStart();
        // ... and printer layout
        ArrayList<String> genLayout = new ArrayList<String>();
        for (String key : dataModel.getPrinterLayout().keySet()) {
            genLayout.add(key + "=" + dataModel.getPrinterLayout().get(key));
        }
        layout = StringUtils.join(genLayout, ", ");
        //} else {
        genIDstart = dataModel.getIDcolumnStart();

        //combine the input files parameter
        String inputFiles = ("\"" + StringUtils.join(dataModel.getInputFiles(), "\",\n\"") + "\"\n").replaceAll("\\\\", "/");

        String finalScript = scriptBuilder.toString();

        finalScript = finalScript.replaceAll("__SOURCE_TYPE__", dataModel.getInputType().name().toLowerCase());
        finalScript = finalScript.replaceAll("__TEMP_ROOT__", tmpRoot);
        finalScript = finalScript.replaceAll("__COLUMNS__", genCols);
        finalScript = finalScript.replaceAll("__PRINT_LAYOUT__", layout);
        finalScript = finalScript.replaceAll("__NORM_BETWEEN__", dataModel.isDoNormBetween() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__BACKGROUND_CORRECT__", dataModel.isDoBackgroundCorrection() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__BGCORR_METHOD__", dataModel.getBackGroundCorrectionMethod());
        finalScript = finalScript.replaceAll("__NORM_BETWEEN_METHOD__", dataModel.getNormBetweenArrays());
        finalScript = finalScript.replaceAll("__IDENTIFIER__", genID);
        finalScript = finalScript.replaceAll("__ID_COLUMN_START__", genIDstart);
        finalScript = finalScript.replaceAll("__PARAM_FILES__", inputFiles);
        try {
            finalScript = finalScript.replaceAll("__OUTPUT_DIR__", dataModel.getOutputDirFile().getCanonicalPath().replaceAll("\\\\", "/"));
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }


        return finalScript;
    }

    public String generateTwoColorQCScript(ArrayList<String> qcMethods, TCArrayDataModel dataModel) {
        StringBuilder scriptBuilder = new StringBuilder();

        // first load the common header that loads the data 
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_header.R")));
        
        // assemble all functions
        for (String method : qcMethods) {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_qc_" + method + ".R")));
        }

        // enter data from the model
        String targetsFile = dataModel.getTargetsFile().replaceAll("\\\\", "/");
        File qualDir = new File(dataModel.getOutputDir(), "qualitychecks");
        String tmpRoot = null;
        try {
            tmpRoot = new File(qualDir, dataModel.getExperimentName()).getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        String genCols = "";
        String layout = "";
        String genID = "", genIDstart = "ID";

        //if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
        // put  together generic columns ....
        ArrayList<String> genericColumns = new ArrayList<String>();
        for (String key : new String[]{"R", "G", "Rb", "Gb"}) {
            genericColumns.add(key + "=\"" + dataModel.getGenericColumns().get(key) + "\"");
        }
        genCols = StringUtils.join(genericColumns, ", ");
        genID = dataModel.getGenericColumns().get("ID");
        genIDstart = dataModel.getIDcolumnStart();
        // ... and printer layout
        ArrayList<String> genLayout = new ArrayList<String>();
        for (String key : dataModel.getPrinterLayout().keySet()) {
            genLayout.add(key + "=" + dataModel.getPrinterLayout().get(key));
        }
        layout = StringUtils.join(genLayout, ", ");
        //} else {
        genIDstart = dataModel.getIDcolumnStart();
        //}

//        if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
//            // put  together generic columns ....
//            ArrayList<String> genericColumns = new ArrayList<String>();
//            for (String key : new String[] {"R", "G", "Rb", "Gb"}) {
//                genericColumns.add(key+"=\""+dataModel.getGenericColumns().get(key)+"\"");
//            }
//            genCols = StringUtils.join(genericColumns, ", ");
//            genID = dataModel.getGenericColumns().get("ID");
//            genIDstart = dataModel.getIDcolumnStart();
//            // ... and printer layout
//            ArrayList<String> genLayout = new ArrayList<String>();
//            for (String key : dataModel.getPrinterLayout().keySet()) {
//                genLayout.add(key+"="+dataModel.getPrinterLayout().get(key));
//            }
//            layout = StringUtils.join(genLayout, ", ");
//        }
//
        String finalScript = scriptBuilder.toString();

        finalScript = finalScript.replaceAll("__SOURCE_TYPE__", dataModel.getInputType().name().toLowerCase());
        finalScript = finalScript.replaceAll("__TARGETS_FILE__", targetsFile);
        finalScript = finalScript.replaceAll("__TEMP_ROOT__", tmpRoot);
        finalScript = finalScript.replaceAll("__COLUMNS__", genCols);
        finalScript = finalScript.replaceAll("__PRINT_LAYOUT__", layout);
        finalScript = finalScript.replaceAll("__NORM_WITHIN__", dataModel.isDoNormWithin() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__NORM_BETWEEN__", dataModel.isDoNormBetween() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__NORM_WITHIN_METHOD__", dataModel.getNormWithinArrays());
        finalScript = finalScript.replaceAll("__BGCORR_METHOD__", dataModel.getNormWithinArraysBGcorr());
        finalScript = finalScript.replaceAll("__NORM_BETWEEN_METHOD__", dataModel.getNormBetweenArrays());
        finalScript = finalScript.replaceAll("__IDENTIFIER__", genID);
        finalScript = finalScript.replaceAll("__ID_COLUMN_START__", genIDstart);
        try {
            finalScript = finalScript.replaceAll("__OUTPUT_DIR__", dataModel.getOutputDirFile().getCanonicalPath().replaceAll("\\\\", "/"));
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        return finalScript;
    }

    public String generateTwoColorMainScript(TCArrayDataModel dataModel) {
        StringBuilder scriptBuilder = new StringBuilder();
        
        // first load the common header that loads the data 
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_header.R")));

        // now choose which template to use
        String contrastNames = "";

        switch (dataModel.getType()) {

            case SIMPLE_REPLICATE:
                System.out.println("detected simple replicate design");
                scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_main_simple.R")));
                break;

            case SIMPLE_REPLICATE_DYESWAP:
                scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_main_simple.R")));
                break;

            case COMMON_REFERENCE:
                System.out.println("detected common reference design");
                System.out.println("common reference sample:" + dataModel.getReferenceSample());
                scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_main_comref.R")));
                //FIXME we have to modify the contrast terms for comparisons
                // against the common reference. does not take direction into account, yet (sign!!)
                String ref = dataModel.getReferenceSample();
                contrastNames = dataModel.getContrastNames();
                String tmpContrasts = dataModel.getContrastTerms();
                tmpContrasts = tmpContrasts.replaceAll("-??" + ref + "-??", "");
                System.out.println(tmpContrasts);
                //System.exit(0);
                dataModel.setContrastTerms(tmpContrasts);
                //analyseCommonRef(dataModel);
                break;

            case DIRECT_DESIGN:
                System.out.println("detected direct design");
                System.out.println("reference sample:" + dataModel.getReferenceSample());
                scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/twocolor/twocolor_main_comref.R")));
                //FIXME we have to modify the contrast terms for comparisons
                // against the common reference. does not take direction into account, yet (sign!!)
                String dref = dataModel.getReferenceSample();
                contrastNames = dataModel.getContrastNames();
                String dtmpContrasts = dataModel.getContrastTerms();
                dtmpContrasts = dtmpContrasts.replaceAll("-??" + dref + "-??", "");
                //System.out.println(dtmpContrasts);
                //System.exit(0);
                dataModel.setContrastTerms(dtmpContrasts);
                //analyseDirect(dataModel);
                break;

            case UNCONNECTED:
                System.out.println("detected unconnected design");
                //analyseSeparateChannel(dataModel);
                break;

            default:
                new SimpleErrorMessage(null, "Robin was not able to automatically detect\n"
                        + "the experiment type");
                return null;
            // break;
        }

        // append duplicate and NA filter script
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/lib/collapseDuplicates.R")));

        String targetsFile = dataModel.getTargetsFile().replaceAll("\\\\", "/");
        String tmpRoot = dataModel.getTempRoot().replaceAll("\\\\", "/");
        String outPath = dataModel.getOutputDir().replaceAll("\\\\", "/");
        String genCols = "";
        String layout = "";
        String genID = "", genIDstart = "ID";

        //if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
        // put  together generic columns ....
        ArrayList<String> genericColumns = new ArrayList<String>();
        for (String key : new String[]{"R", "G", "Rb", "Gb"}) {
            //DEBUG
            System.out.println("gencol " + key + "=" + dataModel.getGenericColumns().get(key));

            genericColumns.add(key + "=\"" + dataModel.getGenericColumns().get(key) + "\"");
        }
        genCols = StringUtils.join(genericColumns, ", ");
        genID = dataModel.getGenericColumns().get("ID");
        genIDstart = dataModel.getIDcolumnStart();
        // ... and printer layout
        ArrayList<String> genLayout = new ArrayList<String>();
        for (String key : dataModel.getPrinterLayout().keySet()) {

            genLayout.add(key + "=" + dataModel.getPrinterLayout().get(key));
        }
        layout = StringUtils.join(genLayout, ", ");
        //} else {
        genIDstart = dataModel.getIDcolumnStart();
        //}


        String finalScript = scriptBuilder.toString();

        // replace the placeholders with real data from the model
        finalScript = finalScript.replaceAll("__SOURCE_TYPE__", dataModel.getInputType().name().toLowerCase());
        finalScript = finalScript.replaceAll("__TARGETS_FILE__", targetsFile);
        finalScript = finalScript.replaceAll("__TEMP_ROOT__", tmpRoot);
        finalScript = finalScript.replaceAll("__COLUMNS__", genCols);
        finalScript = finalScript.replaceAll("__PRINT_LAYOUT__", layout);
        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__NORM_WITHIN__", dataModel.isDoNormWithin() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__NORM_BETWEEN__", dataModel.isDoNormBetween() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__NORM_WITHIN_METHOD__", dataModel.getNormWithinArrays());
        finalScript = finalScript.replaceAll("__BGCORR_METHOD__", dataModel.getNormWithinArraysBGcorr());
        finalScript = finalScript.replaceAll("__NORM_BETWEEN_METHOD__", dataModel.getNormBetweenArrays());
        finalScript = finalScript.replaceAll("__REFERENCE_SAMPLE__", dataModel.getReferenceSample());
        finalScript = finalScript.replaceAll("__EXPERIMENT_NAME__", dataModel.getExperimentName());
        finalScript = finalScript.replaceAll("__CONTRAST_TERMS__", dataModel.getContrastTerms());
        finalScript = finalScript.replaceAll("__DOWN_WEIGH_PROCEDURE__", dataModel.getStatStrategy());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        finalScript = finalScript.replaceAll("__IDENTIFIER__", genID);

        //DEBUG
        System.out.println("ID start from model " + dataModel.getIDcolumnStart());
        System.out.println("ID start from variable genID " + genIDstart);


        finalScript = finalScript.replaceAll("__ID_COLUMN_START__", genIDstart);
        finalScript = finalScript.replaceAll("__CONTRAST_NAMES__", contrastNames);


        /*more...*/

        return finalScript;
    }

    public String generateSingleColorMainScript(SCArrayDataModel dataModel, AnalysisDesignModel designModel) {
        StringBuilder scriptBuilder = new StringBuilder();
        

        // first load the common header that loads the data
        // and imports all nesessary libraries
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_header.R")));
        
        //append main analysis template
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/singlechannel/singlechannel_main_analysis.R")));

        // append duplicate and NA filter script
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/lib/collapseDuplicates.R")));

        // enter data from the model
        String tmpRoot = dataModel.getTempRoot().replaceAll("\\\\", "/");
        String outPath = dataModel.getOutputDir().replaceAll("\\\\", "/");
        String genCols = "";
        String layout = "";
        String genID = "", genIDstart = "ID";

        //if (dataModel.getInputType().name().toLowerCase().equals("generic")) {
        // put  together generic columns ....
        ArrayList<String> genericColumns = new ArrayList<String>();
        for (String key : new String[]{"G", "Gb"}) {
            genericColumns.add(key + "=\"" + dataModel.getGenericColumns().get(key) + "\"");
        }
        genCols = StringUtils.join(genericColumns, ", ");
        genID = dataModel.getGenericColumns().get("ID");
        genIDstart = dataModel.getIDcolumnStart();
        // ... and printer layout
        ArrayList<String> genLayout = new ArrayList<String>();
        for (String key : dataModel.getPrinterLayout().keySet()) {
            genLayout.add(key + "=" + dataModel.getPrinterLayout().get(key));
        }
        layout = StringUtils.join(genLayout, ", ");
        //} else {
        genIDstart = dataModel.getIDcolumnStart();
        //}

//        // put  together generic columns ....
//        ArrayList<String> genericColumns = new ArrayList<String>();
//        for (String key : new String[] {"G", "Gb"}) {
//            genericColumns.add(key+"=\""+dataModel.getGenericColumns().get(key)+"\"");
//        }
//        genCols = StringUtils.join(genericColumns, ", ");
//        genID = dataModel.getGenericColumns().get("ID");
//        genIDstart = dataModel.getIDcolumnStart();
//        // ... and printer layout
//        ArrayList<String> genLayout = new ArrayList<String>();
//        for (String key : dataModel.getPrinterLayout().keySet()) {
//            genLayout.add(key+"="+dataModel.getPrinterLayout().get(key));
//        }
//        layout = StringUtils.join(genLayout, ", ");

        //combine the input files parameter
        //String inputFiles = ("\"" + StringUtils.join(dataModel.getInputFiles(), "\",\n\"") + "\"\n").replaceAll("\\\\", "/");

        String finalScript = scriptBuilder.toString();

        // replace the placeholders with real data from the model
        finalScript = finalScript.replaceAll("__SOURCE_TYPE__", dataModel.getInputType().name().toLowerCase());
        finalScript = finalScript.replaceAll("__TEMP_ROOT__", tmpRoot);
        finalScript = finalScript.replaceAll("__COLUMNS__", genCols);
        finalScript = finalScript.replaceAll("__PRINT_LAYOUT__", layout);
        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__NORM_BETWEEN__", dataModel.isDoNormBetween() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__BGCORR_METHOD__", dataModel.getBackGroundCorrectionMethod());
        finalScript = finalScript.replaceAll("__NORM_BETWEEN_METHOD__", dataModel.getNormBetweenArrays());
        finalScript = finalScript.replaceAll("__EXPERIMENT_NAME__", dataModel.getExperimentName());
        finalScript = finalScript.replaceAll("__CONTRAST_TERMS__", dataModel.getContrastTerms());
        finalScript = finalScript.replaceAll("__DOWN_WEIGH_PROCEDURE__", dataModel.getStatStrategy());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        finalScript = finalScript.replaceAll("__IDENTIFIER__", genID);
        finalScript = finalScript.replaceAll("__ID_COLUMN_START__", genIDstart);
        finalScript = finalScript.replaceAll("__BACKGROUND_CORRECT__", dataModel.isDoBackgroundCorrection() ? "TRUE" : "FALSE");
        //finalScript = finalScript.replaceAll("__PARAM_FILES__", inputFiles);
        finalScript = finalScript.replaceAll("__PARAM_FILES__", designModel.getParamFilenamesTerm());


        // design model parameters
        finalScript = finalScript.replaceAll("__PARAM_GROUPS__", designModel.getGroupsTerm());
        finalScript = finalScript.replaceAll("__PARAM_MODEL__", designModel.getModelMatrixTerm());

        return finalScript;
    }

    public String generateRNASeqLimmaScript(RNASeqDataModel dataModel) {

        StringBuilder scriptBuilder = new StringBuilder();
        
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_header.R")));
        
        //append main analysis template
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_limma_template.R")));

        String outPath = null;
        try {
            outPath = dataModel.getProjectDir().getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        String finalScript = scriptBuilder.toString();

        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__CONTRAST_TERMS__", dataModel.getContrastTerms());
        finalScript = finalScript.replaceAll("__MULTITEST_STRAT__", dataModel.getStatStrategy());
        finalScript = finalScript.replaceAll("__PARAM_GROUPS__", dataModel.getGroupsTerm());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());

        return finalScript;
    }

    public String generateRNASeqEdgeRScript(RNASeqDataModel dataModel, boolean glm) {
        StringBuilder scriptBuilder = new StringBuilder();
        
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_header.R")));
        
        if ( !dataModel.getNormGCMethod().equals("none") ) {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_gc_normalize.R")));
        }
        
        //append main analysis template
        if (glm) {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_edger_glm_template.R")));
        } else {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_edger_template.R")));
        }


        String outPath = null;
        try {
            outPath = dataModel.getProjectDir().getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        String finalScript = scriptBuilder.toString();

        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__CONTRAST_TABLE__", dataModel.getContrastTable());
        finalScript = finalScript.replaceAll("__PARAM_GROUPS__", dataModel.getGroupsTerm());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__DISPERSION__", dataModel.getDispersion());
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        finalScript = finalScript.replaceAll("__GC_NORM_METHOD__", dataModel.getNormGCMethod());        

        return finalScript;
    }

    public String generateRNASeqDESeqScript(RNASeqDataModel dataModel) {
        StringBuilder scriptBuilder = new StringBuilder();
        
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_header.R")));
        
        if ( !dataModel.getNormGCMethod().equals("none") ) {
            scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_gc_normalize.R")));
        }

        //append main analysis template
        scriptBuilder.append(Utilities.loadString(getClass().getResourceAsStream("/de/mpimp/golm/robin/resources/R/rnaseq/rnaseq_deseq_template.R")));


        String outPath = null;
        try {
            outPath = dataModel.getProjectDir().getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }

        String finalScript = scriptBuilder.toString();

        finalScript = finalScript.replaceAll("__OUTPUT_DIR__", outPath);
        finalScript = finalScript.replaceAll("__CONTRAST_TABLE__", dataModel.getContrastTable());
        finalScript = finalScript.replaceAll("__PARAM_GROUPS__", dataModel.getGroupsTerm());
        finalScript = finalScript.replaceAll("__PVALMETHOD__", dataModel.getPValCorrectionMethod());
        finalScript = finalScript.replaceAll("__WRITE_RAW_EXPRS__", dataModel.isWriteRawExprs() ? "TRUE" : "FALSE");
        finalScript = finalScript.replaceAll("__DISPERSION__", dataModel.getDispersion());
        finalScript = finalScript.replaceAll("__MIN_LOGFOLDCHANGE_2_WANTED__", dataModel.isMinLogFoldChangeOf2() ? "1" : "0");
        finalScript = finalScript.replaceAll("__PARAM_PVALCUTOFF__", dataModel.getPValCutoffValue());
        finalScript = finalScript.replaceAll("__GC_NORM_METHOD__", dataModel.getNormGCMethod());

        return finalScript;
    }

    public static void copyLibs(String[] libs, File projectDir) throws Exception {

        File sourceDir = new File(projectDir, "source");
        File libsDir = new File(sourceDir, "lib");
        if (!libsDir.exists()) {
            if (!libsDir.mkdir()) {
                SimpleLogger.getLogger(true).logMessage("ERROR: could not create lib directory at project path " + projectDir.getCanonicalPath());
            }
        }

        for (String lib : libs) {
            String code = Utilities.loadString(RScriptGenerator.class.getResourceAsStream("/de/mpimp/golm/robin/resources/R/lib/" + lib));
            Utilities.saveTextFile(new File(libsDir, lib), code, true);
        }

    }
}
