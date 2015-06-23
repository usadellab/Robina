/*/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//FIXME To run without warings on a Mac the ququq JNIlib has to be in the 
// lib folder and the -Djava.library.path=lib/ JVM parameter has to be passed!
package de.mpimp.golm.robin;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.SplashScreen;
import de.mpimp.golm.robin.exceptions.RobinDefaultThreadExceptionHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.openide.util.Exceptions;

/**
 *
 * @author Marc Lohse, Max-Planck-Institute of molecular plant physiology,
 * Potsdam-Golm
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException, URISyntaxException {
        //DEBUG
        /*SortedMap<String, Charset> map = Charset.availableCharsets();
         for (String name : map.keySet()) {
         System.out.println(name);
         }*/

        // Register a default exception handler for 
        // uncaught exceptions in all threads
        Thread.setDefaultUncaughtExceptionHandler(new RobinDefaultThreadExceptionHandler());

        

        System.setProperty("swing.aatext", "true");

        // Which platform are we running on?        
        if (Utilities.isMacOSX()) {

            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "RobiNA");
            // set the system look and feel
            System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

            // set the Quaqua Look and Feel in the UIManager
            try {
                UIManager.setLookAndFeel(
                        "ch.randelshofer.quaqua.QuaquaLookAndFeel");
                // set UI manager properties here that affect Quaqua

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Utilities.isWindows() || Utilities.isLinux()) {

            // use the jgoodies looks laf
            try {
                UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            } catch (UnsupportedLookAndFeelException ex) {
                Exceptions.printStackTrace(ex);
            }

            /**
             * check whether the .robindata directory is present in the current
             * user's home - if not set up a default one. The downstream process
             * should take care of the missing robin.conf....
             */
            RobinAppDelegate.checkInstallationIntegrity();            
        }
        // start main GUI
            java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                RobinConstants.lastDirChooserPath = System.getProperty("user.home");

                SplashScreen screen =
                        new SplashScreen(680, 458, 5000);
            }
        });
    }
}
