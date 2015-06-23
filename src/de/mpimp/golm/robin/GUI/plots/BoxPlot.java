/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;

/**
 *
 * @author marc
 */
public class BoxPlot {
    
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private BoxAndWhiskerCategoryDataset dataset;
    
    private String title, xlab, ylab;
    private double ymin, ymax;
    private boolean showLegend; 
    private float lineWidth;
    private Color color;
    private boolean showMeans = false;

    public BoxPlot(String title, String xlab, String ylab, double ymin, double ymax, boolean legend) {
        this.title = title;
        this.xlab = xlab;
        this.ylab = ylab;
        this.ymin = ymin;
        this.ymax = ymax;
        this.showLegend = legend;
    }
    
    private void renderPanel(Dimension size) {
        
        CategoryAxis xAxis = new CategoryAxis(xlab);                
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        
        NumberAxis yAxis = new NumberAxis(ylab);
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setRange(ymin, ymax);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        chart = new JFreeChart(
            title,
            new Font("SansSerif", Font.BOLD, 14),
            plot,
            showLegend
        );
        
        chart.removeLegend();
        chart.setTextAntiAlias(true);        
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);
        
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        
        BoxAndWhiskerRenderer datarenderer = (BoxAndWhiskerRenderer) plot.getRendererForDataset(dataset);
        
        // funnily enough, the deprecated method does the job...
        renderer.setPaint(color);        
        renderer.setStroke(new BasicStroke(1f));
        renderer.setMeanVisible(showMeans);
        
        renderer.setFillBox(true);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(size);
    }
    
    public JFreeChart getChart() {
        return chart;
    }

    public boolean isShowMeans() {
        return showMeans;
    }

    public void setShowMeans(boolean showMeans) {
        this.showMeans = showMeans;
    }
    
    public ChartPanel getChartPanel(Dimension size) {
        if (chartPanel == null) {
            this.renderPanel(size);
        }
        return chartPanel;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    public BoxAndWhiskerCategoryDataset getDataset() {
        return dataset;
    }

    public void setDataset(BoxAndWhiskerCategoryDataset dataset) {
        this.dataset = dataset;
    }

    public void setLineWidth(float f) {
        this.lineWidth = f;
    }
    
}
