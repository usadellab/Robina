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
public class MetaGroupModel extends AbstractGroupModel {
    
    private ArrayList<GroupModel> subGroups;
    
    public MetaGroupModel(ArrayList<GroupModel> groups) {
        this.subGroups = groups;
        
        // construct the group name
        this.groupName = null;
        for (GroupModel group : groups) {
            if (this.groupName == null) {
                this.groupName =group.getGroupName();
            } else {
                this.groupName = this.groupName.concat("-"+group.getGroupName());
            }
        }
    }

    public ArrayList<GroupModel> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(ArrayList<GroupModel> subGroups) {
        this.subGroups = subGroups;
    }

    @Override
    public String getRterm() {
        this.Rterm = "("+this.groupName.replaceAll("\\s", "_")+")";
        return this.Rterm;
    }
    
    

}
