/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author marc
 */
public class LineDeviationPlot {
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private YIntervalSeriesCollection dataset;
    
    private String title, xlab, ylab;
    private double xmin, ymin, xmax, ymax;
    private boolean showLegend; 
    private float lineWidth;
    private Color[] colors;
    private ArrayList<Marker> markers;

    public LineDeviationPlot(String title, String xlab, String ylab, double xmin, double ymin, double xmax, double ymax, boolean showLegend) {
        this.title = title;
        this.xlab = xlab;
        this.ylab = ylab;
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.showLegend = showLegend;
        this.markers = new ArrayList<Marker>();
    }
    
    private void renderPanel(Dimension size) {
        
        chart = ChartFactory.createTimeSeriesChart(title, xlab, ylab, dataset, showLegend, true, true);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        XYPlot xyplot = (XYPlot)chart.getPlot();   
        xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));   
        xyplot.setBackgroundPaint(Color.WHITE);   
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));   
        xyplot.setDomainGridlinePaint(Color.GRAY); 
        xyplot.setRangeGridlinePaint(Color.GRAY);
        xyplot.setOutlineVisible(false);
        
        DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);  
        
        for (int i = 0; i < xyplot.getDatasetCount(); i++) {
            deviationrenderer.setSeriesStroke(i, new BasicStroke(2f, 1, 1));       
            deviationrenderer.setSeriesFillPaint(i, new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 192));
            deviationrenderer.setSeriesPaint(i, colors[i]);
            deviationrenderer.setSeriesOutlinePaint(i, colors[i]);
            deviationrenderer.setSeriesOutlineStroke(i, new BasicStroke(1f, 1, 1));
            deviationrenderer.setDrawOutlines(true);
        }
        xyplot.setRenderer(deviationrenderer); 
        
        NumberAxis yAxis = (NumberAxis)xyplot.getRangeAxis();   
        yAxis.setAutoRangeIncludesZero(false);   
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        if ( (ymin != -1) && (ymax != -1) ) yAxis.setRange(ymin, ymax);        
        
        NumberAxis xAxis = new NumberAxis();
        if ( (xmin != -1) && (xmax != -1) ) xAxis.setRange(xmin, xmax);        
        xyplot.setDomainAxis(xAxis);
        
        if (!markers.isEmpty()) {
            for (Marker m : markers) {
                xyplot.addDomainMarker(m, Layer.BACKGROUND);
            }
        }
        
        chartPanel = new ChartPanel(chart); 
        chartPanel.setPreferredSize(size);
    
    }
    
    public void addMarker(Marker m) {
        markers.add(m);
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

    public YIntervalSeriesCollection getDataset() {
        return dataset;
    }

    public void setDataset(YIntervalSeriesCollection dataset) {
        this.dataset = dataset;
    }

    public void setLineWidth(float f) {
        this.lineWidth = f;
    }
    
}
