/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.plots;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author marc
 */
public class BarPlot {
    private Color[] colors;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private IntervalXYDataset dataset;
    private String xlab;
    private String ylab;
    private String title;
    private boolean legend;
    
    public BarPlot(String title, String xlab, String ylab, boolean legend) {        
        this.xlab = xlab;
        this.ylab = ylab;
        this.legend = legend;
        this.title = title;
        chartPanel = null;
    
    }
    
    public void setColors(Color[] cols) {
        this.colors = cols;
    }
    
    public JFreeChart getChart() {
        return chart;
    }

    public ChartPanel getChartPanel(Dimension size) {
        if (chartPanel == null) {
            this.renderPanel(size);
        }
        return chartPanel;
    }

    public IntervalXYDataset getDataset() {
        return dataset;
    }

    public void setDataset(IntervalXYDataset dataset) {
        this.dataset = dataset;
    }

    private void renderPanel(Dimension size) {        
        
        chart = ChartFactory.createXYBarChart(
                title, 
                xlab, 
                false, 
                ylab, 
                dataset, 
                PlotOrientation.VERTICAL, 
                legend, 
                true, 
                true);
        
        chart.setBackgroundPaint(Color.white);
        chart.setTextAntiAlias(true);        
        chart.setBorderVisible(false);
        
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        XYPlot plot =  (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);    
        plot.setOutlineVisible(false);
        
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setRange(1, 5);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(size);
    }
    
}
