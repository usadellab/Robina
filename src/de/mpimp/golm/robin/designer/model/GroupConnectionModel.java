/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.model;

/**
 *
 * @author marc
 */
public class GroupConnectionModel implements Comparable<GroupConnectionModel> {
    
    AbstractGroupModel sourceGroup;
    AbstractGroupModel targetGroup;
    
    public GroupConnectionModel(AbstractGroupModel source, AbstractGroupModel target) {
        this.sourceGroup = source;
        this.targetGroup = target;        
    }

    public AbstractGroupModel getSourceGroup() {
        return sourceGroup;
    }

    public void setSourceGroup(AbstractGroupModel sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    public AbstractGroupModel getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(AbstractGroupModel targetGroup) {
        this.targetGroup = targetGroup;
    }

    public int compareTo(GroupConnectionModel cmpModel) {

        //DEBUG
//        System.out.println("my source: "+this.getSourceGroup().getGroupName());
//        System.out.println("my target: "+this.getTargetGroup().getGroupName());
//        System.out.println("your source: "+cmpModel.getSourceGroup().getGroupName());
//        System.out.println("your target: "+cmpModel.getTargetGroup().getGroupName());

        if (    this.sourceGroup.getGroupName().equals(cmpModel.sourceGroup.getGroupName()) &&
                this.targetGroup.getGroupName().equals(cmpModel.targetGroup.getGroupName()) ) {
                return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return this.getSourceGroup().getGroupName() + " --> " + this.getTargetGroup().getGroupName();
    }
}
