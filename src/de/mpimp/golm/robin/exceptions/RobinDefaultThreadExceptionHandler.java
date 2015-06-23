/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.exceptions;

import de.mpimp.golm.common.gui.SimpleLongErrorMessage;
import de.mpimp.golm.common.logger.SimpleLogger;
import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author marc
 */
public class RobinDefaultThreadExceptionHandler implements UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        
        /**
         * work around for windows-specific prgressbarGUI NullPointerException
         * as reported in http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=4576f4cecc12b6d395f89b503df4?bug_id=6888157
         * and https://netbeans.org/bugzilla/show_bug.cgi?id=191427
         */        
        if (ExceptionUtils.getStackTrace(e).contains("com.sun.java.swing.plaf.windows.WindowsProgressBarUI.getBox(")) {
            SimpleLogger.getLogger(true).logMessage("Windows ProgressBar GUI NullPointerException IGNORED:\n"+ExceptionUtils.getStackTrace(e));
            return;
        }       
        
        
        SimpleLogger.getLogger(true).logException(new Exception(e));        
        
        String message = "An unexpected internal error occurred on thread\n"
                + t.getName() + ", " + t.getId() + "\n";
        
        SimpleLongErrorMessage error = new SimpleLongErrorMessage(null, message, true);
    }
    
}
