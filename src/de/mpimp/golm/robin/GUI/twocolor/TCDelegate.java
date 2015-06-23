/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.GUI.twocolor;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.misc.*;
import de.mpimp.golm.robin.GUI.affy.QCItemMouseListener;
import de.mpimp.golm.robin.GUI.twocolor.TCTargetsTableModel;
import de.mpimp.golm.robin.GUI.affy.QCResultListItem;
import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.data.TCArrayDataModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author marc
 */
public class TCDelegate {

    public TCDelegate() {
    }

    
    
    /**
     * Write targets table to file
     * @param model
     * @param path
     * @throws java.lang.Exception
     */
    public void writeTargetsFile(TCTargetsTableModel model, String path) throws Exception {
        StringBuilder data = new StringBuilder();
        
        data.append("Labels\tFileName\tCy3\tCy5\n");
        
        for(int i = 0; i < model.getRowCount(); i++) {
            String label     = (String) model.getValueAt(i, 0);
            String file      = (String) model.getValueAt(i, 1);
            String cy3sample = (String) model.getValueAt(i, 2);
            String cy5sample = (String) model.getValueAt(i, 3);

            data.append(StringUtils.join(new Object[] {label, file, cy3sample, cy5sample}, "\t")+"\n");

            }
        
        Utilities.saveTextFile(path, data.toString(), true);
    }
    
    /**
     * Load a Targets file from disk and use the
     * data to populate the targets designer
     * panel 
     * 
     * @param path
     * @param table
     * @param conditions
     */
    public void readTargetsFile(String path, JTable table, ArrayList<String> conditions) {
        TCTargetsTableModel model = new TCTargetsTableModel();        
        HashMap<String, Integer> samples = new HashMap<String, Integer>();
        
        String line = "";
        BufferedReader br;
        int lineCounter = 1;
        
        try {
                br = new BufferedReader(new FileReader(path));
                while((line = br.readLine()) != null) {
                        String[] components = line.split("\\t");
                        
                        if (lineCounter == 1) {
                            // this should be the header line
                            // we need to make sure that 
                            
                            model.setColumnIdentifiers(components);
                        } else {
                            samples.put(components[2], 1);
                            samples.put(components[3], 1);
                            model.addRow(components);
                        }
                        lineCounter++;                        
                }
        } catch (IOException ioe) {
                ioe.printStackTrace();
        }
        table.setModel(model);
        conditions.clear();
        conditions.addAll(samples.keySet());        
    }
    
    public ArrayList<QCResultListItem> generateQCResultItems(TCArrayDataModel dataModel, ArrayList<String> chosenMethods, RTask task) {
        ArrayList<QCResultListItem> items = new ArrayList<QCResultListItem>();
        
        for(String method : chosenMethods) {
            // now generate the QCResultItems for all checks that were run
            
            // single output methods
            if (method.equals("density")) {
                
                QCResultListItem item = new QCResultListItem(  dataModel, 
                                                                "", 
                                                                getQCMethodDescription(method)+"<br>"+
                                                                "<small>of "+dataModel.getInputFiles().size()+" input files</small></html>",
                                                                method, 
                                                                dataModel.getWarningsHandler(), 
                                                                task);
                QCItemMouseListener itemClickListener = new QCItemMouseListener();   
                item.setName(method);
                item.addMouseListener(itemClickListener);
                item.setExcludable(false);
                items.add(item);
                
            // one-per-chip output methods    
            } else {
                
                for (int i = 1; i <= dataModel.getInputFiles().size(); i++ ) {
                    QCResultListItem item = new QCResultListItem(  dataModel, 
                                                                dataModel.getInputFiles().get(i-1), 
                                                                getQCMethodDescription(method)+" of <br>"+
                                                                "<small>" +
                                                                Utilities.truncateLongString(dataModel.getInputFiles().get(i-1), 30) +
                                                                "</small></html>",
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
                put("density", "<html>Density plot of signal intensity distribution");  
                put("bground", "<html>Slide background signal intensities");  
                put("maplot", "<html>MA-plot of red channel vs. green channel signal");  
                put("mvalues", "<html>Unnormalized M values");  
            }  
        }; 
        
        return descriptions.get(method);
    }

}
