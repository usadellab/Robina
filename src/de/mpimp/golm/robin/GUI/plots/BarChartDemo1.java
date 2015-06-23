/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI.plots;

/**
 *
 * @author marc
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarChartDemo1 extends ApplicationFrame {

    public BarChartDemo1(String title) {
        super(title);

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(30, "A", "A");
        dataset.addValue(33, "C", "C");
        dataset.addValue(10, "G", "G");
        dataset.addValue(15, "T", "T");
        dataset.addValue(0.3, "N", "N");



        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createBarChart(
                "Bar Chart Demo", "Category", "Score", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        return chart;
    }

    public static void main(final String[] args) {
        BarChartDemo1 chart = new BarChartDemo1("Vertical Bar Chart Demo");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}
