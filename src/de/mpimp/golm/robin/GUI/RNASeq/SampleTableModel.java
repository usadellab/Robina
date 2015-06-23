/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;

/**
 *
 * @author marc
 */
public class SampleTableModel extends StringTableModel {

    private RNASeqDataModel dataModel;

    public SampleTableModel(RNASeqDataModel model) {
        this.dataModel = model;
        this.colNames = new String[]{"File", "raw reads", "filtered reads", "sample ID", "comments"};
        this.columns = this.colNames.length;
        this.data = new String[dataModel.getInputFiles().size()][colNames.length];
        rows = 0;
        
        for (FastQFile file : dataModel.getInputFiles()) {
            data[rows][0] = file.getName();
            data[rows][1] = (dataModel.isImportBAMSAM() || dataModel.isImportCountsTable()) ? "nd" : String.valueOf(file.getReadCount());
            data[rows][2] = (dataModel.isImportBAMSAM() || dataModel.isImportCountsTable()) ? "nd" : String.valueOf(file.getFilteredReadCount());
            data[rows][3] = dataModel.getSampleByInputfileName(file);
            data[rows][4] = file.getComment();   
            rows++;
        }
    } 
}
