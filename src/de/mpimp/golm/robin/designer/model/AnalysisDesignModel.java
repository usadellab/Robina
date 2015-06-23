/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.model;

import java.io.File;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author marc
 * 
 * This class holds all the info necessary to
 * configure the analysis of affymetrix data
 * using limma/R
 */

public class AnalysisDesignModel {
    
    private ArrayList<AbstractGroupModel> groups;
    private ArrayList<GroupConnectionModel> connections;
    
    public AnalysisDesignModel() {
    }

    public ArrayList<GroupConnectionModel> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<GroupConnectionModel> connections) {
        this.connections = connections;
    }

    public ArrayList<AbstractGroupModel> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<AbstractGroupModel> groups) {
        this.groups = groups;
    }
    
    public String getParamFilenamesTerm() {
        StringBuilder paramFileNames = new StringBuilder();
        int groupCounter = 1;
        for (AbstractGroupModel group : this.getGroups()) {
            
            // we only want to extract files from the basic
            // group models - meta group models do not contain
            // path information
           
            if (group instanceof GroupModel) {
                paramFileNames.append("\n# "+((GroupModel)group).getGroupName()+"\n");
                
                // last group?
                if (groupCounter == this.getGroups().size()) {
                    paramFileNames.append(StringUtils.join(((GroupModel) group).getQuotedFileList(), ",\n"));
                } else {
                    paramFileNames.append(StringUtils.join(((GroupModel) group).getQuotedFileList(), ",\n")+",\n");
                }                
            }
            groupCounter++;
            
        }
        return paramFileNames.toString();
    }
    
    public String getGroupsTerm() {
        
        StringBuilder colNamesTerm = new StringBuilder();        
        int groupCounter = 1;        
        for (AbstractGroupModel group : this.getGroups()) {
            if (group instanceof GroupModel) {
                if (groupCounter == this.getGroups().size()) {                   
                    colNamesTerm.append("\""+((GroupModel)group).getRterm()+"\"");
                } else {                    
                    colNamesTerm.append("\""+((GroupModel)group).getRterm()+"\",");
                }                
            }
            groupCounter++;            
        }        
        return colNamesTerm.toString();    
    }
    
    public String getModelMatrixTerm() {
        StringBuilder matrixTerm = new StringBuilder();
       
        int groupCounter = 1;
        //matrixTerm.append("samples <- as.factor(c(");
        
        for (AbstractGroupModel group : this.getGroups()) {
            if (group instanceof GroupModel) {
                if (groupCounter == this.getGroups().size()) {                    
                    matrixTerm.append("rep("+groupCounter+","+((GroupModel)group).getFileList().size()+")");                    
                } else {
                    matrixTerm.append("rep("+groupCounter+","+((GroupModel)group).getFileList().size()+"), ");                   
                }                
            }
            groupCounter++;            
        }
        //matrixTerm.append("))\ndesign <- model.matrix(~ -1+samples)\n");
       
        return matrixTerm.toString();
    }
    
    public String getContrastTerms() {
        StringBuilder contrastTerms = new StringBuilder();
        int connectionCounter = 1;
        
        for (GroupConnectionModel connection: this.getConnections()) {
            
            if (connectionCounter == this.getConnections().size()) {
                    contrastTerms.append(connection.sourceGroup.getRterm()+"-"+connection.getTargetGroup().getRterm()+"\n");
                } else {
                    contrastTerms.append(connection.sourceGroup.getRterm()+"-"+connection.getTargetGroup().getRterm()+",\n");
                } 
            connectionCounter++;
        }
        
        return contrastTerms.toString();
    }
    
    /**
     * needed to define the pairwise contrasts in a format that
     * is suitable for DESeq
     * @return a string
     */
    public String getContrastTable() {
        StringBuilder contrastTable = new StringBuilder();
        int connectionCounter = 1;
        
        contrastTable.append("matrix(c(");
        
        for (GroupConnectionModel connection: this.getConnections()) {
            
            if (connectionCounter == this.getConnections().size()) {
                    contrastTable.append("\"" + connection.sourceGroup.getRterm()+"\", \""+connection.getTargetGroup().getRterm()+"\"\n");
                } else {
                    contrastTable.append("\"" + connection.sourceGroup.getRterm()+"\", \""+connection.getTargetGroup().getRterm()+"\",\n");
                } 
            connectionCounter++;
        }
        
        contrastTable.append("),byrow=T, ncol=2)");
        return contrastTable.toString();
    }
    
    public boolean validateModel() throws   NoConnectionsDesignException, 
                                            RedundantConnectionsDesignException {
        //TODO check the model. the above list of exceptions
        // will surely have to be extended
        if (this.getConnections().size() == 0) throw new NoConnectionsDesignException();

        if (this.getConnections().size() == 1) return true;

        for (int i = 0; i < this.getConnections().size(); i++) {
            for (int j = 0; j < this.getConnections().size(); j++) {
                if (i == j) continue;
                if (this.getConnections().get(i).compareTo(this.getConnections().get(j)) == 0) {

                    throw new RedundantConnectionsDesignException("The contrast \n " +
                            this.getConnections().get(i) +
                            "\nwas defined several times.\n" +
                            "Please delete the redundant definitions.");
                }
            }
        }
        return true;
    }
}
