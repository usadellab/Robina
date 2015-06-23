/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI;

import java.awt.Point;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author marc
 */
public class ExtSceneConnectProvider implements ConnectProvider {
        private LayerWidget connectionLayer;
        private Scene scene;
        private JList list;
                
        
        public ExtSceneConnectProvider(LayerWidget cl, Scene s, JList l) {
             connectionLayer = cl;
             scene = s;
             list = l;
        }
        
        public boolean isSourceWidget (Widget sourceWidget) {
            System.out.println("isSourceWidget:"+sourceWidget.toString());
            return sourceWidget instanceof LabelWidget;
        }

        public ConnectorState isTargetWidget (Widget sourceWidget, Widget targetWidget) {
            return sourceWidget != targetWidget  &&  targetWidget instanceof LabelWidget ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
        }

        public boolean hasCustomTargetWidgetResolver (Scene scene) {
            return false;
        }

        public Widget resolveTargetWidget (Scene scene, Point sceneLocation) {
            return null;
        }

        public void createConnection (Widget sourceWidget, Widget targetWidget) {
            ConnectionWidget connection = new ConnectionWidget (scene);
            connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
            connection.setSourceAnchor(AnchorFactory.createRectangularAnchor(sourceWidget));
            connection.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));  
            
            connectionLayer.addChild (connection);
            GroupLabelWidget s = (GroupLabelWidget) sourceWidget;
            GroupLabelWidget t = (GroupLabelWidget) targetWidget;
            System.out.println("Established connection between "+s.getName()+" and "+t.getName());
            DefaultListModel listModel = (DefaultListModel) list.getModel();
            listModel.addElement(s.getName()+" vs. "+t.getName());                    
        }

    }
