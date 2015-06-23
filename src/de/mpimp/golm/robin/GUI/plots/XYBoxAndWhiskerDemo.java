/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.plots;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.date.DateUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a box and whisker chart.
 *
 * @author David Browning
 */
public class XYBoxAndWhiskerDemo extends ApplicationFrame {

    /**
     * A demonstration application showing a box and whisker chart.
     *
     * @param title  the frame title.
     */
    public XYBoxAndWhiskerDemo(final String title) {

        super(title);

        final BoxAndWhiskerXYDataset dataset = createSampleDataset();
        final JFreeChart chart = createChart(dataset);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
        setContentPane(chartPanel);

    }

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.     * 
     * @return The dataset.
     */
    private JFreeChart createChart(final BoxAndWhiskerXYDataset dataset) {
        
        final JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
            "Box-and-Whisker Demo",
            "Time", 
            "Value",
            dataset, 
            true
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYBoxAndWhiskerRenderer renderer = (XYBoxAndWhiskerRenderer) plot.getRenderer();
        renderer.setFillBox(true);
        renderer.setPaint(Color.RED);
        return chart;
        
    }
    
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Creates a sample {@link BoxAndWhiskerXYDataset}.
     *
     * @return A sample dataset.
     */
    public static BoxAndWhiskerXYDataset createSampleDataset() {
        
        final int entityCount = 14;
        
        

        final DefaultBoxAndWhiskerXYDataset dataset = new DefaultBoxAndWhiskerXYDataset("Test");
        for (int i = 0; i < entityCount; i++) {
            Date date = DateUtilities.createDate(2003, 7, i + 1, 12, 0);
//            final List values = new ArrayList();
//            for (int j = 0; j < 10; j++) {
//                values.add(new Double(10.0 + Math.random() * 10.0));
//                values.add(new Double(13.0 + Math.random() * 4.0));
//            }
//            dataset.add(date, BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(values));
            BoxAndWhiskerItem item = new BoxAndWhiskerItem(10, 11, 2, 15, 1, 20, 0.1, 30.4, new ArrayList());
            dataset.add(date, item);
           
           
        }

        return dataset; 
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final XYBoxAndWhiskerDemo demo = new XYBoxAndWhiskerDemo("Box-and-Whisker Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
