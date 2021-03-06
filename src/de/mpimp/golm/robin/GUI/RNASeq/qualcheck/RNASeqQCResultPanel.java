/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.RNASeq.qualcheck;



import de.mpimp.golm.robin.GUI.plots.ChartUtils;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.ScrollPaneConstants;
import org.jfree.chart.ChartPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqQCResultPanel extends javax.swing.JPanel {
    
    
    private ChartPanel chartPanel;
    private javax.swing.JSeparator jSeparator1;
    private JEditorPane textArea;
    private String textContent;
    private File chartPNG;

    /** Creates new form RNASeqQCViewPanel */
    public RNASeqQCResultPanel(String text, String name, ChartPanel chart) { 
        chartPanel = chart;
        this.textContent = text;
        initComponents();
        this.setName(name);
        textArea.setText("<html>"
                + "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #666666; \"> <br>"
                + text
                + "</span>"
                + "<html>");
        this.repaint();
    }

    public File getChartPNG(int width, int height) {
        // render chart as temporary file and return this file
        File chartFile = null;
        try {
            chartFile = File.createTempFile(this.getName(), ".png");            
            ChartUtils.saveChartAsPNG(chartFile, this.chartPanel.getChart() , width, height);            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return chartFile;
    }

    public String getTextContent() {
        return textContent;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {     
//        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new JEditorPane();
        textArea.setContentType("text/html");
        jSeparator1 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout chartPanelLayout = new org.jdesktop.layout.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 392, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 296, Short.MAX_VALUE)
        );

//        jScrollPane1.setBorder(null);
//        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        textArea.setEditable(false);
//        jScrollPane1.setViewportView(textArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(chartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(textArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
//                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(chartPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
//                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20))
        );
    }// </editor-fold>

    public void prepareForExport() {
        this.remove(jSeparator1);        
        this.doLayout();
        this.revalidate();
    }
    
}

