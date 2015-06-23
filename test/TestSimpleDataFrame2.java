
import de.mpimp.golm.common.datastructures.SimpleIntegerDataFrame;
import java.util.HashMap;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestSimpleDataFrame2 extends TestCase {
    
    public void testDataFrame() throws Exception {
        SimpleIntegerDataFrame frame = new SimpleIntegerDataFrame();
        System.out.println("dimensions after init: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        // add fake column
        HashMap<String, Integer> a2 = new HashMap<String, Integer>() {
            {
                put("gene2480", 30);
                put("gene2479", 56);
            }
        };
        
        HashMap<String, Integer> a1 = new HashMap<String, Integer>() {
            {
                put("gene2480", 32);
                put("gene2479", 95);
            }
        };
        
        HashMap<String, Integer> b1 = new HashMap<String, Integer>() {
            {
                put("gene2480", 51);
                put("gene2479", 71);
            }
        };
        
        HashMap<String, Integer> b2 = new HashMap<String, Integer>() {
            {
                put("gene2480", 51);
                put("gene2479", 49);
            }
        };
        
       
        
        frame.addColumn(a2, "a2");        
        System.out.println("dimensions after adding col1: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        frame.addColumn(a1, "a1");        
        System.out.println("dimensions after adding col2: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        frame.addColumn(b1, "b1");        
        System.out.println("dimensions after adding col2: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        frame.addColumn(b2, "b2");        
        System.out.println("dimensions after adding col2: rows="+frame.getDimensions()[0]+" columns="+frame.getDimensions()[1]);
        
        System.out.println("colsums = "+frame.getColumnSums());
        
        
    
    }
    
}
