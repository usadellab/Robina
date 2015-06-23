/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.warnings;
    
 

/**
 *
 * @author marc
 */
public class Warning {
    
    final public static int SEVERITY_LIGHT = 0;
    final public static int SEVERITY_MEDIUM = 1;
    final public static int SEVERITY_CRITICAL = 2;
    
    private String type;
    private String message;
    private int severity;
    
    public Warning(String tp, String msg, int sev) {
        this.severity = sev;
        this.type = tp;
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return sevToString(severity);
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    private String sevToString(int sev) {
        switch (sev) {
            case 0 : return "Light"; 
            case 1 : return "Medium"; 
            case 2 : return "Critical"; 
            default : return "";
        }  
    }
    
    

}
