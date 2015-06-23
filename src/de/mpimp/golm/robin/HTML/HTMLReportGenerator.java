/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.HTML;

import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class HTMLReportGenerator {
    
    private ArrayList<HTMLReportSection> sections = new ArrayList<HTMLReportSection>(); 
    
    
    public HTMLReportGenerator() {
    }
    
    public void addSection(HTMLReportSection sec) {
        sections.add(sec);
    }
    
    public void writeToFile(String path) {
    }
    
    private String render() {
        return null;
    }
    
    

}
