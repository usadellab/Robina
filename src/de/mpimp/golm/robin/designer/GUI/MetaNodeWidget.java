/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.designer.GUI;

import de.mpimp.golm.robin.designer.model.MetaGroupModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;

/**
 *
 * @author marc
 */
public class MetaNodeWidget extends LabelWidget 
                            implements GroupNodeInterface {
    
    final static Color   SELECTED_COLOR   = Color.RED;
    final static Color   UNSELECTED_COLOR = Color.BLUE;
    final static Color   BACKGROUND_COLOR = Color.ORANGE;

    private AnalysisDesignerScene scene;
    private Object       nodeObject;
    private Border       border;
    private String       name;
    private MetaGroupModel groups;
        

    public MetaNodeWidget(AnalysisDesignerScene scene,
                      Object nodeObject,
                      MetaGroupModel group) {
        super(scene);
        this.scene = scene;
        this.nodeObject = nodeObject;
        if (nodeObject instanceof String) {
            this.name = (String) nodeObject;
        }
        this.groups = group;
        init();

    }

    public void init() {
        scene.addObject(nodeObject,this);
        
        border = BorderFactory.createRoundedBorder(20,20,BACKGROUND_COLOR, UNSELECTED_COLOR);
        super.setBackground(Color.ORANGE);
        super.setBorder(border);        
        super.setFont(new Font("Lucida Grande", Font.ITALIC, 14));
        super.setLabel(nodeObject.toString());
        super.getActions().addAction(scene.createSelectAction());
        super.getActions()
             .addAction(ActionFactory.createExtendedConnectAction(scene.getInteractionLayer(),
                                                                  new NodeLinkProvider(scene.getConnectionLayer(),
                                                                                       scene)));
        //super.getActions().addAction(ActionFactory.createMoveAction());
        super.getActions().addAction(ActionFactory.createAlignWithMoveAction(scene.getMainLayer(), scene.getInteractionLayer(), null));
        super.getActions().addAction(ActionFactory.createPopupMenuAction(scene.getPopUpMenuProvider()));
        
        this.setToolTipText("<html>MetaGroup containing "+this.groups.getSubGroups().size()+" subgroups</html>");
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public MetaGroupModel getModel() {
        return groups;
    }

    public void setModel(MetaGroupModel newGroups) {
        this.groups = newGroups;
    }
    
    

    /**
     * Implemented to visualize the current state. Set the selected status to graphically represent
     * that. Set the shape according to the
     * 
     * @see org.netbeans.api.visual.widget.ConnectionWidget#notifyStateChanged(org.netbeans.api.visual.model.ObjectState,
     *      org.netbeans.api.visual.model.ObjectState)
     */   
    @Override
    public void notifyStateChanged(ObjectState oldState,
                                   ObjectState newState) {
        super.notifyStateChanged(newState,
                                 oldState);
        if ( newState.isSelected() != oldState.isSelected()) {
            //System.out.println("selection changed"+System.currentTimeMillis());
            border = BorderFactory.createRoundedBorder(20,20,BACKGROUND_COLOR, (newState.isSelected())
                                                                            ? SELECTED_COLOR
                                                                            : UNSELECTED_COLOR);
            super.setBorder(border);            
        }
    }

    /**
     * Pop up menu to delete edge.
     * 
     * @return
     */
    public JPopupMenu getPopupMenu() {
        JMenuItem renameItem = new JMenuItem("rename");
        renameItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
              //  NodeWidget.this.scene.renameNode(NodeWidget.this);
            }

        });
        JMenuItem deleteItem = new JMenuItem("delete");
        deleteItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MetaNodeWidget.this.scene.deleteNode(MetaNodeWidget.this);
            }

        });
        JPopupMenu popup = new JPopupMenu("edge");
        popup.add(renameItem);
        popup.add(deleteItem);
        return popup;
    }

}
