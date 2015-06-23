/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.IPC;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.Remote;
import java.util.Properties;
import org.gabipd.mapman.commserver.CommunicationServer;
import org.gabipd.mapman.commserver.PostExperimentData;
import org.openide.util.Exceptions;
import java.nio.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author marc
 */
public class MapCommunicator {

    private static final String SERVICE_NAME = "MapManPostExperimentData";
    private static final int    SERVER_POLL_TIMEOUT = 100000;

    public static boolean postExperimentToMapMan(File resultFile, Properties props) {

        int pollcounter = 0;
        boolean gotServer = false;
        while (pollcounter <= 20) {
            gotServer = pollCommunicationServer();
            if (gotServer) {
                break;
            }
            pollcounter++;

            //DEBUG
            System.out.println("polling ..."+pollcounter);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (!gotServer) {
            new SimpleErrorMessage(null, "Robin could not connect to the\n"+
                    "MapMan communication server.\n"+
                    "Please make sure MapMan is running.");
            return false;
        }


        //TODO fully implement this method!
        boolean retVal = false;
        try {
            Remote remoteService = new CommunicationServer().getRemoteService(SERVICE_NAME);
            if (remoteService instanceof PostExperimentData) {
                retVal = ((PostExperimentData) remoteService).postExperimentData(resultFile, props);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return retVal;
    }

    private static boolean pollCommunicationServer() {
        try {
            Remote remoteService = new CommunicationServer().getRemoteService(SERVICE_NAME);
            if (remoteService instanceof PostExperimentData) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }   

    public static File findMapMan() throws IOException {
        File mapManPath = null;
        //File homeDir = new File(System.getProperty("user.home"));
        File homeDir = new File("/Users/marc");

        

        FilenameFilter nameFilter= new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith("ImageAnnotator.xml"))
                    return true;
                else
                    return false;
            }
        };
        List<File> files = Utilities.getFileListing(homeDir);

        for (File f : files) {
            try {
                /*if (f.getCanonicalPath().endsWith("ImageAnnotator.xml")) {
                    System.out.println("found image annotator");
                    break;
                }*/
                System.out.println(f.getCanonicalPath());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return mapManPath;
    }
}
