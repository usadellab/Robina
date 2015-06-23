/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI.singlechannel;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.affy.QCItemMouseListener;
import de.mpimp.golm.robin.GUI.affy.QCResultListItem;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.data.SCArrayDataModel;
import de.mpimp.golm.robin.misc.RobinUtilities;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class SCDelegate {

    public SCDelegate() {
    }

    public ArrayList<QCResultListItem> generateQCResultItems(SCArrayDataModel dataModel, ArrayList<String> chosenMethods, RTask task) {
        ArrayList<QCResultListItem> items = new ArrayList<QCResultListItem>();
        
        for(String method : chosenMethods) {
            // now generate the QCResultItems for all checks that were run
            
            // single output methods
            if (    method.contains("density") ||
                    method.contains("pca") ||
                    method.contains("hclust")) {
                
                QCResultListItem item = new QCResultListItem(  dataModel, 
                                                                "", 
                                                                getQCMethodDescription(method)+"<br>"+
                                                                "<small><span style=\"color:gray;\">of " +
                                                                dataModel.getInputFiles().size() +
                                                                " input files</span></small></html>",
                                                                method, 
                                                                dataModel.getWarningsHandler(), 
                                                                task);
                QCItemMouseListener itemClickListener = new QCItemMouseListener();   
                item.setName(method);
                item.addMouseListener(itemClickListener);
                item.setExcludable(false);
                items.add(item);
                
            // all-combos-output methods
            } else if (method.contains("scatter")) {

                File qualDir = new File(dataModel.getOutputDirFile(), "qualitychecks");
                for (int i = 1; i <= dataModel.getInputFiles().size(); i++ ) {
                    for (int j = 1; j <= dataModel.getInputFiles().size(); j++ ) {
                        if (j > i) {

                            File qualFile = new File(qualDir, dataModel.getExperimentName() + "_sc_scatter_" + i + j + ".png");
                            String imagePath = null;
                            try {
                                imagePath = qualFile.getCanonicalPath();
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            
                            QCResultListItem item = new QCResultListItem(
                                    task,
                                    imagePath,
                                    method,
                                    dataModel.getWarningsHandler(),
                                    null
                                    );
                            QCItemMouseListener itemClickListener = new QCItemMouseListener();
                            item.setDescription(
                                    getQCMethodDescription(method) +
                                    " of <br><small><span style=\"color:gray;\">" +
                                    Utilities.truncateLongString(dataModel.getInputFiles().get(i - 1), 30) +
                                    " and <br>" +
                                    Utilities.truncateLongString(dataModel.getInputFiles().get(j - 1), 30) +
                                    "</span></small></html>");
                            item.setName(method+ "#" + i + j);
                            item.addMouseListener(itemClickListener);
                            item.setExcludable(false);
                            items.add(item);
                        }
                    }
                }

            // one-per-chip output methods
            } else {
                
                for (int i = 1; i <= dataModel.getInputFiles().size(); i++ ) {
                    QCResultListItem item = new QCResultListItem(  dataModel, 
                                                                dataModel.getInputFiles().get(i-1), 
                                                                getQCMethodDescription(method)+" of <br>"+
                                                                "<small><span style=\"color:gray;\">" +
                                                                Utilities.truncateLongString(dataModel.getInputFiles().get(i-1), 30) +
                                                                "</span></small></html>",
                                                                method,
                                                                i, 
                                                                dataModel.getWarningsHandler(), 
                                                                task);
                    item.setName(method+"#"+i);
                    QCItemMouseListener itemClickListener = new QCItemMouseListener();                
                    item.addMouseListener(itemClickListener);
                    item.setExcludable(true);
                    items.add(item);
                }
            }
        }          
        return items;    
    }

    private String getQCMethodDescription(String method) {
        HashMap<String, String> descriptions = new HashMap<String, String>()   
        {              
            {  
                // two color
                put("density", "<html>Density plot of signal intensity distribution");
                put("bground", "<html>Slide background signal intensities");  
                put("maplot", "<html>MA-plot of red channel vs. green channel signal");  
                put("mvalues", "<html>Unnormalized M values");

                //single channel
                put("sc_density", "<html>Density plot of signal intensity distribution");
                put("sc_bground", "<html>Slide background signal intensities");
                put("sc_maplot", "<html>MA-plot of median intensity against chip signal intensity");
                put("sc_hclust", "<html>Hierarchical clustering of pearson correlation");
                put("sc_pcaplot", "<html>Principal component analysis of expression levels");
                put("sc_scatter", "<html>Scatter plot of the the normalized expression values");
            }  
        }; 
        
        return descriptions.get(method);
    }

}
