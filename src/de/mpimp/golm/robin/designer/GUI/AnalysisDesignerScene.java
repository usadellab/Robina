package de.mpimp.golm.robin.designer.GUI;

import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import de.mpimp.golm.robin.designer.model.GroupConnectionModel;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import java.awt.Point;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JPopupMenu;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.AlignWithMoveDecorator;
import org.netbeans.api.visual.action.AlignWithWidgetCollector;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * MapMan: PathwayScene.java
 * 
 * @since May 26, 2008
 * @author Axel Nagel, Max Planck Institut für molekulare Pflanzenphysiologie, nagel@mpimp-golm.mpg.de
 * @version $Revision: $ $Author: $ $Date: $
 *
 */
/**
 * @author axel
 * 
 */
public class AnalysisDesignerScene extends ObjectScene {

    private LayerWidget       mainLayer;
    private LayerWidget       connectionLayer;
    private LayerWidget       interactionLayer;
    private SelectProvider    selectProvider;
    private PopupMenuProvider popupMenuProvider;

    /**
     * Create new Scene, setting interaction and connection layers.
     */
    public AnalysisDesignerScene() {

        mainLayer = new LayerWidget(this);
        this.addChild(mainLayer);
        connectionLayer = new LayerWidget(this);
        this.addChild(connectionLayer);
        interactionLayer = new LayerWidget(this);
        this.addChild(interactionLayer);
        selectProvider = new PathwaySelectProvider();
        popupMenuProvider = new PathwayPopupMenuProvider();        
        
        this.getActions().addAction(ActionFactory.createRectangularSelectAction(new DesignSceneSelectDecorator(this), 
                                                                                interactionLayer, 
                                                                                ActionFactory.createObjectSceneRectangularSelectProvider(this)));
        LayoutFactory.createDevolveWidgetLayout(
                    this.getMainLayer(), 
                    LayoutFactory.createVerticalFlowLayout(), 
                    true).invokeLayout();
    }
    
    /**
     * Checks whether two nodes are directly connected and returns
     * the connecting edge or null if they aren't
     * @param node1
     * @param node2
     * @return an EdgeWidget or null
     */
    public EdgeWidget getEdgeConnectingNodes(LabelWidget node1, LabelWidget node2) {
        EdgeWidget theEdge = null;
        for (EdgeWidget edge : getConnections()) {
            if ( ((edge.getSource() == node1) && (edge.getTarget() == node2)) ||
                 ((edge.getSource() == node2) && (edge.getTarget() == node1)) ) {
                theEdge = edge;
                System.out.println("nodes are connected");
            }
        }        
        return theEdge;
    }
    
    public ArrayList<EdgeWidget> getConnections() {
        ArrayList<EdgeWidget> edges = new ArrayList<EdgeWidget>();
        for (Widget w : getConnectionLayer().getChildren()) {
            if (w instanceof EdgeWidget) {
                edges.add((EdgeWidget) w);
            }
        }
        return edges;
    }
    
    
    /**
     * Checks whether a LabelWidget is connected
     * and returns the connecting EdgeWidgets else null
     * @param node
     * @return the connections || null
     */
    public ArrayList<EdgeWidget> getConnectionsForNode(LabelWidget node) {
        ArrayList<EdgeWidget> connections = new ArrayList<EdgeWidget>();
        for (Widget widget : connectionLayer.getChildren() ) {
           if (widget instanceof EdgeWidget) {
               if ( (((EdgeWidget) widget).getSource() == node) || 
                    (((EdgeWidget) widget).getTarget() == node)) {
                    connections.add((EdgeWidget) widget);
               }
           } 
        }
        
        // found a connection?
        if (connections.size() > 0) {
            return connections;
        } else {
            return null;
        }         
    }
    
    /**
     * Extracts a list of all group connections from the
     * designer scene
     * @return list of group connection models
     */
    public ArrayList<GroupConnectionModel> getGroupConnections() {
        ArrayList<GroupConnectionModel> groupConnections = new ArrayList<GroupConnectionModel>();
        GroupConnectionModel connection = null;
        //TOODO read all edges from the connectionLayer and generate the corr ConnectionModels
        for (Widget widget : connectionLayer.getChildren() ) {
            if (widget instanceof EdgeWidget) {                
                
                GroupNodeInterface source = (GroupNodeInterface) ((EdgeWidget) widget).getSource();
                GroupNodeInterface target = (GroupNodeInterface) ((EdgeWidget) widget).getTarget();             
                
                try {
                    //System.out.println("getting connection:"+source.getModel().getGroupName()+"->"+target.getModel().getGroupName());
                    connection = new GroupConnectionModel(source.getModel(), target.getModel());
                } catch (Exception e) {
                    StringBuilder stackTrace = new StringBuilder();
                    for (StackTraceElement elem : e.getStackTrace()) {
                        stackTrace.append(elem.toString()+"\n");
                    }
                    new VerboseWarningDialog(null, "Internal error", "<html>An internal error has occurred. If this problem<br>"+
                                                    "persists please report it and include the error<br>"+
                                                    "message below to help us track it down ASAP.<br>"+
                                                    //FIXME maybe another adress here?
                                                    "Send bug reports to: lohse@mpimp-golm.mpg.de", 
                                                    "Exception: "+e.getClass().getName()+"\n"+
                                                    "Message: "+e.getMessage()+"\n"+
                                                    "Stack trace: "+stackTrace);
                }
                groupConnections.add(connection);
            }
        }        
        return groupConnections;
    }
    
    public ArrayList<AbstractGroupModel> getGroups() {
        ArrayList<AbstractGroupModel> designGroups = new ArrayList<AbstractGroupModel>();
       
        for (Widget widget : this.getMainLayer().getChildren()) {
            if (widget instanceof NodeWidget) {
                designGroups.add(((NodeWidget) widget).getModel());
            }
        }
        return designGroups;
    }
    
    /**
     * Get all selected NodeWidgets
     * 
     * 
     * @returns List of selected nodes
     */
    public ArrayList<NodeWidget> getSelectedNodes() {
        ArrayList<NodeWidget> selectedNodes = new ArrayList<NodeWidget>();
        for (Widget widget : this.getMainLayer().getChildren()) {
            System.out.println("on the scene:"+widget.toString());
            if (    (widget instanceof NodeWidget) && 
                    ((NodeWidget) widget).getState().isSelected()   )  {
                selectedNodes.add(((NodeWidget) widget));
                System.out.println("selected:"+widget.toString());
            }
        }
        System.out.println("returning "+selectedNodes.size()+" selected nodes");
        return selectedNodes;    
    }
    
    public ArrayList<MetaNodeWidget> getSelectedMetaNodes() {
        ArrayList<MetaNodeWidget> selectedMetaNodes = new ArrayList<MetaNodeWidget>();
        
        for (Widget widget : this.getMainLayer().getChildren()) {
            System.out.println("on the scene:"+widget.toString());
            if (    (widget instanceof MetaNodeWidget) && 
                    ((MetaNodeWidget) widget).getState().isSelected()   )  {
                selectedMetaNodes.add(((MetaNodeWidget) widget));
                System.out.println("selected metanode:"+widget.toString());
            }
        }
        return selectedMetaNodes;
    }

    /**
     * Remove the given edge.
     * 
     * @param edge
     */
    public void deleteEdge(EdgeWidget edge) {
        connectionLayer.removeChild(edge);
    }

    /**
     * Remove the given node.
     * 
     * @param edge
     */
    public void deleteNode(LabelWidget node) {
        
        mainLayer.removeChild(node);
    }

    /**
     * @return the mainLayer
     */
    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    /**
     * @return the connectionLayer
     */
    public LayerWidget getConnectionLayer() {
        return connectionLayer;
    }

    /**
     * @return the interactionLayer
     */
    public LayerWidget getInteractionLayer() {
        return interactionLayer;
    }

    /**
     * @return the selectProvider
     */
    public SelectProvider getSelectProvider() {
        return selectProvider;
    }
    
   
    /**
     * @return the selectProvider
     */
    public PopupMenuProvider getPopUpMenuProvider() {
        return popupMenuProvider;
    }

    

    class PathwayPopupMenuProvider implements PopupMenuProvider {

        public JPopupMenu getPopupMenu(Widget widget,
                                       Point position1) {
            if ( widget instanceof EdgeWidget )
                return ((EdgeWidget) widget).getPopupMenu();
            return null;
        }

    }

    class PathwaySelectProvider implements SelectProvider {

        public PathwaySelectProvider() {

        }

        public boolean isAimingAllowed(Widget widget,
                                       Point point,
                                       boolean allowed) {
            return false;
        }

        public boolean isSelectionAllowed(Widget widget,
                                          Point point,
                                          boolean allowed) {
            return true;
        }

        public void select(Widget widget,
                           Point point,
                           boolean allowed) {
        }

    }
}
