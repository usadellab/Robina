/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;

import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author marc
 */
public class GroupLabelWidget extends LabelWidget {
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public GroupLabelWidget(Scene scene, String label, String name) {
        super(scene, label);
        this.name = name;        
    }
}
