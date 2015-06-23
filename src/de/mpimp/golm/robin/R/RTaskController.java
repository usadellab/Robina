/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.R;

import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.GUI.RobinWorkflow;
import de.mpimp.golm.robin.data.HTDataModel;
import de.mpimp.golm.common.gui.MessageWindow;
import de.mpimp.golm.common.gui.ProgressDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import javax.swing.Timer;

/**
 *
 * @author marc
 */
public class RTaskController implements ActionListener {
    
    private RTask task; 
    private ExecutorService executor;
    private Timer timer;
    private RobinWorkflow robinPanel;
    private HTDataModel model;
    private MessageWindow messWin; 
//    private ProgressDialog progDia; 

    public RTaskController(HTDataModel dataModel, RTask task, RobinWorkflow panel, ExecutorService executor, Timer timer) {
        this.task = task;
        this.executor = executor;
        this.timer = timer;
        this.robinPanel = panel;
        this.model = dataModel;
        this.messWin = new MessageWindow(robinPanel.getMainGUI(), "Running main analysis...");
        
//        this.progDia = new ProgressDialog(robinPanel.getMainGUI(), true, true);
//        this.progDia.setText("Running main analysis...");
//        this.progDia.setIndeterminate(true);                
        ((RobinMainGUI)robinPanel.getMainGUI()).startBusyAnimation("DE analysis...");
        
//        this.progDia.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (executor.isTerminated()) {            
            this.messWin.dispose();            
//            this.progDia.dispose();
            robinPanel.getMainGUI().setGrayedOut(false);
            if (task.getExitValue() != 0) {                
                // the task has a problem
                timer.stop();
                RTaskErrorMessageEvaluator evaluator = new RTaskErrorMessageEvaluator(task, robinPanel);
                evaluator.evaluateError();

            } else {
                // the sun is shining (more or less)                
                if (task.hasWarning()) {
                    model.addWarning(task.getWarning()); 
                }
                
                ((RobinMainGUI) robinPanel.getMainGUI()).stopBusyAnimation();
                timer.stop();      
                robinPanel.RTaskFinished();
//                robinPanel.finishGracefully();
            }
        }
    }
}
