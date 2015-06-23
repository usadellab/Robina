/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.misc;

import de.mpimp.golm.robin.data.AffyArrayDataModel;
import de.mpimp.golm.robin.data.ArrayDataModel;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import de.mpimp.golm.robin.warnings.Warning;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RobinAnalysisSummaryGenerator {

    public static void writeAnalysisSummary(ArrayDataModel model) throws Exception {
//        if (model instanceof AffyArrayDataModel) {
//            writeAffyAnalysisSummary((AffyArrayDataModel) model);
//        } else if (model instanceof SCArrayDataModel) {
//            writeSCAnalysisSummary((SCArrayDataModel) model);
//        } else if (model instanceof TCArrayDataModel) {
//            writeTCAnalysisSummary((TCArrayDataModel) model);
//        } else {
//            // we should never end up here....
//            throw new Exception("unknown array data model");
//        }
        // write the summary text file
        FileWriter fw = new FileWriter(new File(model.getOutputDir(),
                                                model.getExperimentName() +
                                                "_summary.txt"));

        fw.write(model.toString());
        fw.close();
    }

//    private static void writeSCAnalysisSummary(SCArrayDataModel dataModel) {
//        //TODO implement for SC
//    }
//
//    private static void writeTCAnalysisSummary(TCArrayDataModel dataModel) {
//        //TODO implament for TC
//    }
//
//    private static void writeAffyAnalysisSummary(AffyArrayDataModel affyModel) throws IOException {
//
//
//
//
//    }
}
