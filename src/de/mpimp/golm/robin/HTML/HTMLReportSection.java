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
public class HTMLReportSection implements HTMLItem {

    private String title = null;
    private ArrayList<HTMLItem> items = new ArrayList<HTMLItem>();
    private String description = null;
    
    public HTMLReportSection(String header) {
        this.title = header;        
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
      
    public void addItem(HTMLItem item) {
        items.add(item);
    }

    public String render() {
        StringBuilder html = new StringBuilder();        
        html.append("<div class=\"dotbox_title\">"+this.title+"</div>");
        html.append("<div class=\"dotbox\">");
        html.append(description);
        
        html.append("<ul>");
        
        for (HTMLItem i : items) {
            html.append("<li>");
            html.append(i.render());
            html.append("</li>");
            //html.append("<br>");
        }
        
        html.append("</ul>");
        
        html.append("</div>");
        
        return html.toString();
    }
    
    

}
