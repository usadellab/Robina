package de.mpimp.golm.robin.designer.GUI;

import de.mpimp.golm.robin.designer.model.AbstractGroupModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import de.mpimp.golm.robin.designer.model.GroupModel;
import de.mpimp.golm.robin.designer.model.MetaGroupModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;


/**
 * MapMan: EditablePathway.java
 * 
 * @since May 26, 2008
 * @author Axel Nagel, Max Planck Institut für molekulare Pflanzenphysiologie,
 *         nagel@mpimp-golm.mpg.de and sodomized by Marc Lohse, same organisation
 * @version $Revision: $ $Author: $ $Date: $
 * 
 */
public class AnalysisDesigner extends JPanel {

    private AnalysisDesignerScene designerScene;
    private AnalysisDesignModel designModel;
    private List<AbstractGroupModel> groups;

    public AnalysisDesigner() {
        init();
    }
    
    public AnalysisDesigner(List<AbstractGroupModel> groups) {
        init();
        for(AbstractGroupModel group : groups) {
            designerScene.getMainLayer().addChild(new NodeWidget(designerScene, group.getGroupName(), group));
        }
        designModel = new AnalysisDesignModel();
    }

    public final void init() {

        // setup scene
        designerScene = new AnalysisDesignerScene();

        // add scene to panel
        JComponent designerView = designerScene.createView();
        designerView.setBackground(new Color(255, 255, 235));
        super.setLayout(new BorderLayout());
        
        JScrollPane designerScroll = new JScrollPane(designerView,
                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        designerScroll.setBorder(null);        
        super.add(designerScroll, BorderLayout.CENTER);
    }
    
    public Scene getDesignerScene() {
        return this.designerScene;
    }
    
    public void writeDesignViewToFile(String path, String expName, String imageType) {
        BufferedImage bi = new BufferedImage(  this.getDesignerScene().getView().getWidth(), 
                                                this.getDesignerScene().getView().getHeight(), 
                                                BufferedImage.TYPE_4BYTE_ABGR);
        
        Graphics2D graphics = bi.createGraphics ();
        this.getDesignerScene().paint(graphics);
        graphics.dispose();
        
        try {
            ImageIO.write (bi, imageType, new File(path+expName+"_design."+imageType)); // NOI18N
        } catch (IOException e) {
            SimpleLogger.getLogger(true).logException(e);
        }
    }

    public AnalysisDesignModel getDesignModel() {
        designModel.setConnections(designerScene.getGroupConnections());

        //DEBUG
        //System.out.println("extracted "+designerScene.getGroupConnections().size()+" connections");
        
        designModel.setGroups(designerScene.getGroups());
        return designModel;
    }
    
    
    
    public void createMetaNodeOfSelectedNodes() throws Exception{

        ArrayList<NodeWidget> selectedNodes = designerScene.getSelectedNodes();
        
        // are exactly 2 nodes chosen?
        if (selectedNodes.size() < 2) {return;} 
        if (selectedNodes.size() > 2) {
            new SimpleErrorMessage(this, "Creating Metagroups containing more than two subgroups\n"+
                         "does not make sense in most cases. If you accidentally\n"+
                         "selected more than two groups using the selection rectangle\n"+
                         "try selecting the groups you want to combine in a Metagroup\n"+
                         "by holding down CONTROL while you click them");
            return;
        }
        
        // are they not metanodes?
        for (Widget node : selectedNodes) {
            if (node instanceof MetaNodeWidget) {
                new SimpleErrorMessage(this, "Creating metagroups of metagroups not supported");
                return;
            } 
        } 
        
        // are they connected?
        EdgeWidget connectingEdge = designerScene.getEdgeConnectingNodes(selectedNodes.get(0), selectedNodes.get(1));
        if (connectingEdge == null) {
            new SimpleErrorMessage(this, "The groups you want to combine are not connected.\n"+
                         "Please connect them to allow an unambiguous formulation\n"+
                         "of the metagroup. (a-b and b-a will have the same values\n"+
                         "but different sign!)");
            return;
        }        
        
        ArrayList<GroupModel> selgroups = new ArrayList<GroupModel>(); 
        
        // specify direction of contrast
        if (selectedNodes.get(0) == connectingEdge.getSource()) {
            selgroups.add((GroupModel) selectedNodes.get(0).getModel());
            selgroups.add((GroupModel) selectedNodes.get(1).getModel());
        } else {
            selgroups.add((GroupModel) selectedNodes.get(1).getModel());
            selgroups.add((GroupModel) selectedNodes.get(0).getModel());
        }
        
        MetaGroupModel metaGroup = new MetaGroupModel(selgroups);
        MetaNodeWidget metaWidget = new MetaNodeWidget(designerScene, metaGroup.getGroupName(), metaGroup);
        
        metaWidget.setLabel(metaGroup.getGroupName());
        
        designerScene.getMainLayer().addChild(metaWidget);
        designerScene.getMainLayer().revalidate();
        designerScene.validate();
    }
    
    public void deleteSelectedMetaNodes() {
        for (MetaNodeWidget selectedMetaNode : designerScene.getSelectedMetaNodes()) {
            
            ArrayList<EdgeWidget> connections = designerScene.getConnectionsForNode(selectedMetaNode);
            if (connections != null) {
                for (EdgeWidget connection : connections) {
                    designerScene.deleteEdge(connection);
                }                
            }
            designerScene.deleteNode(selectedMetaNode);
        }
        designerScene.validate();
    }

}