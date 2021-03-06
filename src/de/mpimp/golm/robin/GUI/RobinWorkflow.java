/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RobinWorkflow.java
 *
 * Created on 10.03.2010, 09:04:30
 */

package de.mpimp.golm.robin.GUI;

import de.mpimp.golm.robin.R.RTask;
import de.mpimp.golm.robin.data.ArrayDataModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import java.awt.CardLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author marc
 */
public abstract class RobinWorkflow extends javax.swing.JPanel {
    
    protected RobinMainGUI        mainGUI;
    protected Timer               processTimer;
    protected ExecutorService     executor;
    protected boolean             isImporting = false;

    // abstract method declarations
    public abstract void finishGracefully();
    public abstract void RTaskFinished();
    protected abstract void initGUIFromModel();
    public abstract void setImportMode(File importProject, DefaultListModel listModel);
    protected abstract void resetGUI();
    protected abstract void startMainAnalysis();
    // abstract method declarations END
    
    
    /** Creates new form RobinWorkflow */
    public RobinWorkflow() {
        initComponents();
    }
    
    public RobinWorkflow(RobinMainGUI main, File projectPath) {
        initComponents();
        this.processTimer = new Timer(100, null);
        this.executor = Executors.newFixedThreadPool(1);
        this.mainGUI = main;
    }

    public RobinMainGUI getMainGUI() {
        return mainGUI;
    }

    protected RTask prepareTask(File scriptFileName, String scriptCode) {
       // write the file
       try {
            FileWriter out = new FileWriter(scriptFileName);
            out.write(scriptCode);
            out.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);            
        }


       Properties defaults = mainGUI.getDefaultSettings();
       final RTask qcTask = new RTask(defaults.getProperty("PathToR"),
                                defaults.getProperty("CommandToRunR"),
                                defaults.getProperty("ArgsToR"), scriptFileName);
       return qcTask;

    }

    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void simulatePanel(String id) {
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, id);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.CardLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
