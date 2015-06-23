/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import de.mpimp.golm.robin.R.RScriptGenerator;
import de.mpimp.golm.robin.designer.model.AnalysisDesignModel;
import java.util.Properties;
import junit.framework.TestCase;

/**
 *
 * @author marc
 */
public class TestRScriptGenerator extends TestCase {

    private static final String R_PATH = "/Library/Frameworks/R.framework/Resources/bin/";
    private static final Properties PROPS = new Properties();
    static {
        PROPS.setProperty("paramMainNormMethod", "rma");
        PROPS.setProperty("paramMainPval", "BH");
        PROPS.setProperty("outputPath", "/Users/marc/Desktop/");
        PROPS.setProperty("testingStrategy", "nestedF");
        PROPS.setProperty("paramLogFoldMin2", "1"); 
        PROPS.setProperty("paramWriteRaw", "1"); 
        PROPS.setProperty("outputPath", "/Users/marc/Desktop/test/plots"); 
        PROPS.setProperty("expName", "TEST");
    }
    
    public void testRScriptGenerator() throws Exception {
        RScriptGenerator gen = new RScriptGenerator(R_PATH, PROPS);
        //String testTemplate = gen.generateRScript(new AnalysisDesignModel());
        //System.out.println(testTemplate);
    }

   
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}