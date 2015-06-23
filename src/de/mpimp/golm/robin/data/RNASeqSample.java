/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.data;

import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class RNASeqSample extends AbstractGroupModel {
    
    private String condition;
    private ArrayList<FastQFile> inputFiles;
    private long numberOfReads = 0;
    private String sampleName;

    public RNASeqSample() {
    }

    public RNASeqSample(String condition, ArrayList<FastQFile> inputFiles) {
        this.condition = condition;
        this.inputFiles = inputFiles;
    }
    
    private void computeNumberOfReads() {
        for (FastQFile file : inputFiles) {
            if (file.getName().toLowerCase().startsWith("trimmed"))
                numberOfReads += file.getFilteredReadCount();
            else 
                numberOfReads += file.getReadCount();
        }
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ArrayList<FastQFile> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(ArrayList<FastQFile> inputFiles) {
        this.inputFiles = inputFiles;
    }
    
    public long getNumberOfReads() {
        if (numberOfReads == 0) {
            computeNumberOfReads();
        }     
        for (FastQFile file : inputFiles) { 
            numberOfReads += file.getReadCount();
        }
        return numberOfReads;        
    }

    @Override
    public String getRterm() {        
        return getGroupName().replaceAll("\\s", "_");
    }
    
    @Override
    public String getGroupName() {
        return condition;
    }

    @Override
    public void setGroupName(String groupName) {
        this.condition = groupName;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.getSampleName() + "\n");
        for (FastQFile f : this.getInputFiles()) {
            s.append("\t" + f.getName() + "\n");
        }        
        return s.toString();
    }
    
}
