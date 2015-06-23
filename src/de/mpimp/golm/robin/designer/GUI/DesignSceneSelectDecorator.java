/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.GUI;

import java.awt.Color;
import org.netbeans.api.visual.action.RectangularSelectDecorator;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author marc
 */
public class DesignSceneSelectDecorator implements RectangularSelectDecorator {


    private Scene scene;
    
    public DesignSceneSelectDecorator(Scene scene){
        this.scene = scene;
    }
    
    public Widget createSelectionWidget() {
        Widget decorator = new Widget(scene);
        decorator.setBorder(BorderFactory.createDashedBorder(new Color(172, 216, 233), 5, 1));
        return decorator;
    }
}
