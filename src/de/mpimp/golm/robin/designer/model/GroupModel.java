/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.model;

import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class GroupModel extends AbstractGroupModel {
    
    private ArrayList<String> fileList;
    
    public GroupModel(String name, ArrayList<String> newfiles) {
        this.groupName = name;
        this.fileList = newfiles;
    }

    public ArrayList<String> getFileList() {
        return fileList;
    }
    
    public ArrayList<String> getQuotedFileList() {
        ArrayList<String> quotedFiles = new ArrayList<String>();
        for (String file : fileList) {
            //file = file.replaceAll("\\\\", "\\\\\\\\\\\\\\\\\\\\");
            file = file.replaceAll("\\\\", "/");
            quotedFiles.add("\""+file+"\"");
        }        
        return quotedFiles;
    } 

    public void setFileList(ArrayList<String> fileList) {
        this.fileList = fileList;
    }
    
    @Override
    public String getRterm() {        
        Rterm = groupName.replaceAll("\\s", "_");
        return Rterm;
    }

}
