/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.R;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.GUI.RobinWorkflow;
import de.mpimp.golm.robin.GUI.affy.AffyWorkflowPanel;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author marc
 */
public class RTaskErrorMessageEvaluator {

    private RTask task;
    private RobinWorkflow robinPanel;
    private static final HashMap<String, String> errorList = new HashMap<String, String>() {
        {
            put("Could not obtain CDF environment",
                            "RobiNA could not process the data because the CDF environment\n" +
                            "could not be loaded. This might be due to  a problem with\n" +
                            "your internet connection. Another reason might be that the\n" +
                            "data was created on a chip platform for which there is no\n" +
                            "publicly available CDF package. In this case, please restart\n" +
                            "RobiNA and supply an appropriate CDF file at the import step.");

            put("no replication within conditions",
                      "Your RNA-Seq experiment setup does not include replicates\n"
                    + "which greatly decreases the statistical power of the analysis.");

        }
    };


    public RTaskErrorMessageEvaluator(RTask checkMeTask, RobinWorkflow panel) {
        this.task = checkMeTask;
        this.robinPanel = panel;
        SimpleLogger.getLogger(true).logMessage(task.getOutputMessage());
    }

    public void evaluateError() {

        for (String errSignature : errorList.keySet()) {
            
            if (task.getOutputMessage().contains(errSignature)) {
                int choice = JOptionPane.showOptionDialog(
                            robinPanel.getMainGUI(),
                            errorList.get(errSignature),
                            "R engine error",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            UIManager.getIcon("OptionPane.questionIcon"),
                            new Object[] {"Restart", "Exit"},
                            "Restart"
                            );
                if (choice == 0) {
                    // TODO flush everything and move to card 1
                    ( (AffyWorkflowPanel) robinPanel).flushAndResetVariables();
                    RobinMainGUI newMain = new RobinMainGUI();
                    robinPanel.getMainGUI().setVisible(false);
                    newMain.setVisible(true);
                    robinPanel.getMainGUI().dispose();
                    return;
                } else {
                    System.exit(0);
                }
            }
        }

        // no specific error was found - show general error message
        new VerboseWarningDialog(   robinPanel.getMainGUI(),
                                    "R process failure",
                                    "Exit code:"+task.getExitValue(), task.getOutputMessage());
    }
}
