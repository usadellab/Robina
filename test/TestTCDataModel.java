
import junit.framework.TestCase;
import de.mpimp.golm.robin.GUI.twocolor.TCTargetsTableModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */import de.mpimp.golm.robin.data.ArrayDataModel.InputDataType;

import de.mpimp.golm.robin.data.TCArrayDataModel;

/**
 *
 * @author marc
 */
public class TestTCDataModel extends TestCase{
    
    private static final TCTargetsTableModel tableModel = new TCTargetsTableModel();
    static {
        tableModel.addRow(new Object[] {"egal", "auch egal", "a", "c"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "a", "d"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "d", "e"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "e", "b"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "b", "g"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "g", "f"});
        tableModel.addRow(new Object[] {"egal", "auch egal", "g", "h"});
    }
    
    public void testTCDataModel() {
        TCArrayDataModel model = new TCArrayDataModel();
        model.allPathsCanBeWalked(tableModel);

        model.setInputType(InputDataType.AFFYMETRIX);

        System.out.println(model.getInputType().name());


    }
    
}
