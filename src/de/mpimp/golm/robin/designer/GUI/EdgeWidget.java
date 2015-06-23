package de.mpimp.golm.robin.designer.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * MapMan: EdgeWidget.java
 * 
 * @since May 26, 2008
 * @author Axel Nagel, Max Planck Institut für molekulare Pflanzenphysiologie,
 *         nagel@mpimp-golm.mpg.de
 * @version $Revision: $ $Author: $ $Date: $
 * @author sodomized by Marc Lohse, MPIMP-Golm
 */
public class EdgeWidget extends ConnectionWidget {

    private AnalysisDesignerScene pathwayScene;
    private Widget source;
    private Widget target;

    public EdgeWidget(AnalysisDesignerScene pathwayScene,
                      Widget sourceWidget,
                      Widget targetWidget) {
        super(pathwayScene);
        this.source = sourceWidget;
        this.target = targetWidget;       
        
        this.pathwayScene = pathwayScene;
        pathwayScene.addObject(this,
                               this);

        super.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        super.setSourceAnchor(AnchorFactory.createRectangularAnchor(sourceWidget));
        super.setTargetAnchor(AnchorFactory.createRectangularAnchor(targetWidget));
        super.getActions().addAction(pathwayScene.createSelectAction());
        super.getActions()
             .addAction(ActionFactory.createPopupMenuAction(pathwayScene.getPopUpMenuProvider()));
        
        /*System.out.println("connected: "+
        ((NodeWidget)sourceWidget).getGroup().getGroupName()+
        " and "+
        ((NodeWidget)targetWidget).getGroup().getGroupName());*/

    }

    public Widget getSource() {
        return source;
    }

    public void setSource(Widget source) {
        this.source = source;
    }

    public Widget getTarget() {
        return target;
    }

    public void setTarget(Widget target) {
        this.target = target;
    }
    
    

    /**
     * Pop up menu to delete edge.
     * 
     * @return
     */
    public JPopupMenu getPopupMenu() {
        JMenuItem deleteItem = new JMenuItem("delete");
        deleteItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EdgeWidget.this.pathwayScene.deleteEdge(EdgeWidget.this);
            }

        });
        JPopupMenu popup = new JPopupMenu("edge");
        popup.add(deleteItem);
        return popup;
    }
}
