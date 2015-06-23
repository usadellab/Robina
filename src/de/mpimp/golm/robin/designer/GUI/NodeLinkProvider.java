package de.mpimp.golm.robin.designer.GUI;

import java.awt.Point;

import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * MapMan: NodeLink.java
 * 
 * @since May 26, 2008
 * @author Axel Nagel, Max Planck Institut für molekulare Pflanzenphysiologie,
 *         nagel@mpimp-golm.mpg.de
 * @version $Revision: $ $Author: $ $Date: $
 * 
 */
public class NodeLinkProvider implements ConnectProvider {

    private LayerWidget connectionLayer;
    private AnalysisDesignerScene       pathwayScene;

    public NodeLinkProvider(LayerWidget connectionLayer,
                            AnalysisDesignerScene pathwayScene) {
        this.connectionLayer = connectionLayer;
        this.pathwayScene = pathwayScene;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.visual.action.ConnectProvider#isSourceWidget(org.netbeans.api.visual.widget.Widget)
     */
    public boolean isSourceWidget(Widget sourceWidget) {
        return sourceWidget instanceof LabelWidget;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.visual.action.ConnectProvider#isTargetWidget(org.netbeans.api.visual.widget.Widget, org.netbeans.api.visual.widget.Widget)
     */
    public ConnectorState isTargetWidget(Widget sourceWidget,
                                         Widget targetWidget) {
        return sourceWidget != targetWidget && targetWidget instanceof LabelWidget
                                                                                  ? ConnectorState.ACCEPT
                                                                                  : ConnectorState.REJECT_AND_STOP;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.visual.action.ConnectProvider#hasCustomTargetWidgetResolver(org.netbeans.api.visual.widget.Scene)
     */
    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.visual.action.ConnectProvider#resolveTargetWidget(org.netbeans.api.visual.widget.Scene, java.awt.Point)
     */
    public Widget resolveTargetWidget(Scene scene,
                                      Point sceneLocation) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.visual.action.ConnectProvider#createConnection(org.netbeans.api.visual.widget.Widget, org.netbeans.api.visual.widget.Widget)
     */
    public void createConnection(Widget sourceWidget,
                                 Widget targetWidget) {        
        
        if ( (sourceWidget == null) || (targetWidget == null) ) {
            return;
        }
        connectionLayer.addChild(new EdgeWidget(pathwayScene, sourceWidget, targetWidget));
    }

}
