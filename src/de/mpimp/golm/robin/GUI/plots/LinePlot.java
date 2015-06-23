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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author marc
 */
public class LinePlot {    
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    
    private String title, xlab, ylab;
    private double xmin, ymin, xmax, ymax;
    private boolean showLegend; 
    private float lineWidth;
    private Color[] colors;
    private ArrayList<Marker> markers;

    public LinePlot(String title, String xlab, String ylab, double xmin, double ymin, double xmax, double ymax, boolean legend) {
        this.title = title;
        this.xlab = xlab;
        this.ylab = ylab;
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.showLegend = legend;
        this.markers = new ArrayList<Marker>();       
        
    }

    private void renderPanel(Dimension size) {
        chart = ChartFactory.createXYLineChart(
                title, 
                xlab, 
                ylab, 
                dataset, 
                PlotOrientation.VERTICAL, 
                showLegend,
                true,  
                false
         );
        
        chart.setBackgroundPaint(Color.white);
        chart.setTextAntiAlias(true);        
        chart.setBorderVisible(false);
        
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // get a reference to the plot for further customisation... 
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white); 
        //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0)); 
        plot.setDomainGridlinePaint(Color.GRAY); 
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setOutlineVisible(false);
        
        for (Marker m : markers) {
            plot.addDomainMarker(m, Layer.BACKGROUND);
        }
        
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(); 
//        renderer.setShapesVisible(true); 
//        renderer.setShapesFilled(true);        
        
        for (int i = 0; i < plot.getSeriesCount(); i++) {                
            renderer.setSeriesStroke(i, new BasicStroke(lineWidth));
            if (colors != null) renderer.setSeriesPaint(i, colors[i]); 
        }
        
        // change the auto tick unit selection to integer units only... 
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(); 
        if ( (ymin != -1) && (ymax != -1) ) rangeAxis.setRange(ymin, ymax);    
        rangeAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        
        chartPanel = new ChartPanel(chart); 
        chartPanel.setPreferredSize(size);
        
//        try {
//            // test saving as PDF
//            ChartUtils.saveChartAsPDF(new File("/Users/marc/Desktop/test.pdf"), chart, 400, 300, new DefaultFontMapper());
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (DocumentException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        
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

    public XYSeriesCollection getDataset() {
        return dataset;
    }

    public void setDataset(XYSeriesCollection dataset) {
        this.dataset = dataset;
    }

    public void setLineWidth(float f) {
        this.lineWidth = f;
    }
    
    
}
