/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;

/**
 *
 * @author marc
 */
public class RNASeqOverrepSequences implements RNASeqQualityCheck {
    
    private static final String ID = "QCoverrepSeq";

    public boolean processSeqEntry(FastQEntry entry) {
        return true;
    }

    public Object getReport() {
        return null;
    }

    public ChartPanel getResultChart(Dimension size) {
        return new ChartPanel(new JFreeChart(new Plot() {

            @Override
            public String getPlotType() {
                return "";
            }

            @Override
            public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
                
            }
        }));        
    }

    public boolean hasTabularData() {
        return false;
    }

    public String getDescription() {
        return "";
    }

    public JPanel getResultPanel() {
        JPanel panel = new JPanel();
        panel.setName(ID);
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
}
