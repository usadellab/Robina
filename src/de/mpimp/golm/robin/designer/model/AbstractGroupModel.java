/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.model;

/**
 *
 * @author marc
 */
public abstract class AbstractGroupModel {
    
    String groupName;
    String Rterm;
    
    public AbstractGroupModel() {
    }

    abstract public String getRterm();

    public void setRterm(String Rterm) {
        this.Rterm = Rterm;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
        
}
