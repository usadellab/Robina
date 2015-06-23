/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.HTML;

/**
 *
 * @author marc
 */
public class HTMLQCItem implements HTMLItem {
    
    private String method;
    private String celFile;
    private String imagePath;
    private String warnings;

    public HTMLQCItem(String method, String celFile, String imagePath, String warnings) {
        this.method = method;
        this.celFile = celFile;
        this.imagePath = imagePath;
        this.warnings = warnings;
    }

    public String render() {
        StringBuilder html = new StringBuilder();
        
        html.append("<div class=\"qcItem\">");
        html.append(method.toUpperCase()+"-analysis");
        
        if (celFile != null) {
            html.append(" of "+celFile+"  ");
        }
        
        html.append("<a href=\""+imagePath+"\"  target=\"blank\"/>>View image< </a>"+
                "<br>");
        
        if (warnings != null) {
            html.append("<img source=\"img/warnings.png\"/>");
            html.append("<span style=\"color:red;\"><img src=\"img/warnings.png\" width=\"20\" height=\"20\" alt=\"warnings\"/>"+warnings+"</span><br>");
        }
        
        html.append("</div>");        
        
        return html.toString(); 
    }
    
}
