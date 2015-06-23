/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.trimmomatic;

import java.util.HashMap;

/**
 *
 * @author marc
 */
public class TMTrimmerArguments extends HashMap <String, Object> {
    
    private String identifier;

    public TMTrimmerArguments(String name) {
        super();
        this.identifier = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
}
