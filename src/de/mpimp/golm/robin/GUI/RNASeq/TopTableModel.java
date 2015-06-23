/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import de.mpimp.golm.common.logger.SimpleLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author marc
 */
public class TopTableModel extends StringTableModel {

    public TopTableModel(File topTableFile) {
        try {
            FileReader fr = new FileReader(topTableFile);
            BufferedReader br = new BufferedReader(fr);
            
            String line = null;
            rows = 0;
            while ((line = br.readLine()) != null ) {
                
                String[] elems = line.split("\\t");
                for (int i = 0; i < elems.length; i++) {
                    
                    if (rows == 0) {
                        columns = elems.length;
                        data = new String[100][columns];
                        colNames = elems;
                    } else {                    
                        data[rows-1][i] = elems[i];
                    }
                }
                
                rows++;
                
                if (rows > 99)
                    break;
            }
        } catch (FileNotFoundException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }
}
