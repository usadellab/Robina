/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.warnings;

import de.mpimp.golm.common.utilities.Utilities;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class WarningsHandler {
    
    private ArrayList<Warning> warnings;
    private boolean hasWarnings = false;
    
    public WarningsHandler() {
        warnings = new ArrayList<Warning>();
    }
    
    public boolean hasWarnings() {
        return hasWarnings;
    }
    
    /**
     * Show a ShowWarningsDialog on the passed frame
     * @param frame
     * @return true if the user choses to ignore the warnings
     * false if he cancels
     */
    public boolean showWarnings(JFrame frame) {
        ShowWarningsDialog swd = new ShowWarningsDialog(frame, this);
        swd.showDialog();
        boolean choice = swd.getChoice();
        swd.dispose();
        return choice;
        /*for (Warning w : warnings) {
        System.out.println("WARNING:"+w.getMessage());
        }
        return true;*/
    }
    
    public void writeWarningsToFile(String path) {
        StringBuilder m = new StringBuilder();
        
        m.append("-----------------------\n");
        m.append("Robin warnings report\n");
        m.append(new Date().toString()+"\n");
        m.append("-----------------------\n\n");
        
        for (Warning w : warnings) {
            m.append("Type: "+w.getType()+"\n");
            m.append("Severity: "+w.getSeverity()+"\n");
            m.append("Details:\n"+w.getMessage()+"\n//\n");            
        }
        try {

            Utilities.saveTextFile(path, m.toString());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
    }  
    
    public void addWarning(Warning wr) {
        warnings.add(wr);
        hasWarnings = true;
    }
}
