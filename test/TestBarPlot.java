
import de.mpimp.golm.robin.GUI.plots.BarPlot;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import junit.framework.TestCase;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marc
 */
public class TestBarPlot {

    public static void main(String[] args) {


        XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
        XYIntervalSeries series = new XYIntervalSeries("comp");
        series.add(1, 0.75, 1.25, 28, 27.25, 28.25);
        series.add(2, 1.75, 2.25, 21, 21.25, 22.25);
        series.add(3, 2.75, 3.25, 21, 21.25, 22.25);
        series.add(4, 3.75, 4.25, 28, 27.25, 28.25);
        series.add(5, 4.75, 5.25, 0.079, 0.005, 1);
        dataset.addSeries(series);
//
//        DefaultIntervalXYDataset dataset = new DefaultIntervalXYDataset();
//        double[][] data = {            
//            {28, 21, 21, 28, 0.7},
//            {1, 2, 3, 4, 5},
//            {28, 21, 21, 28, 0.7},
//            {28, 21, 21, 28, 0.7},
//            {28, 21, 21, 28, 0.7},
//            {28, 21, 21, 28, 0.7},
//            
//        };
//        dataset.addSeries("Nucleotide composition", data);


        final BarPlot plot = new BarPlot("test", "xlabel", "ylabel", true);
        plot.setDataset(dataset);

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("test");
                frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

                Dimension size1 = new Dimension(300, 300);

                ChartPanel seq_q_panel = (ChartPanel) plot.getChartPanel(size1);
                seq_q_panel.setVisible(true);
                frame.add(seq_q_panel);


                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
