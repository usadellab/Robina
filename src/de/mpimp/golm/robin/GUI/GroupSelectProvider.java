/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;


import java.awt.Point;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author marc
 */
class GroupSelectProvider implements SelectProvider {
    
    private Scene scene;
    private LayerWidget layer;
    
    
    public GroupSelectProvider(LayerWidget mainLayer, Scene designerScene) {
        this.scene = designerScene;
        this.layer = mainLayer;
    }

    public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
       return false;
    }

    public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
       return true;
    }

    public void select(Widget arg0, Point arg1, boolean arg2) {
        
        System.out.println("Selected:"+arg0.getLocation().toString()+arg0.toString());
    }

 

}
