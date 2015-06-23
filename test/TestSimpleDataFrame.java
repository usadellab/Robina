
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqDelegate;
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFrame;
import junit.framework.TestCase;
import org.jfree.chart.ChartPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestSimpleDataFrame  {
    
    public static void main(String[] args) throws Exception {
        SimpleIntegerDataFrame frame = new SimpleIntegerDataFrame();
        System.out.println("dimensions after init: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        // add fake column
        HashMap<String, Integer> col1 = new HashMap<String, Integer>() {
            {
                put("gene1", 100);
                put("gene2", 3452345);
                put("gene3", 23454);
                put("gene4", 444);
//                put("gene5", 3453);
//                put("gene6", 676);
//                put("gene7", 11);
//                put("gene8", 897685);
//                put("gene9", 3432); 
            }
        };
        
        HashMap<String, Integer> col2 = new HashMap<String, Integer>() {
            {
                put("gene1", 2332);
                put("gene2", 34);
                put("gene3", 5453);
                put("gene4", 58);
                put("gene5", 3132445);
                put("gene6", 45645);
                put("gene7", 45633);
                put("gene8", 12);
                put("gene9", 9); 
            }
        };
        
        frame.addColumn(col1, "column1");        
        System.out.println("dimensions after adding col1: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        frame.addColumn(col2, "column2");        
        System.out.println("dimensions after adding col2: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        System.out.println("grandsum="+frame.getGrandSum());
        System.out.println("colsums="+frame.getColumnSums());
        System.out.println("colmax1="+frame.getColMax(0));
        System.out.println("colmin1="+frame.getColMin(0));
        
        
        float [] dist = frame.getBinnedCountFreqsForColumn(0, 50);
        
        for (int i = 0; i < dist.length; i++) {
            System.out.println("Bin "+i+" --> genes "+dist[i]);
        }
        
        RNASeqDelegate del = new RNASeqDelegate(null);
        ChartPanel panel = del.getCountFreqDistributionChartForColumn(0, frame, new Dimension(100, 50)); 
        
        JFrame win = new JFrame();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.getContentPane().add(panel);
        win.pack();
        win.setSize(200,200);
        win.setVisible(true);
    }    
}
