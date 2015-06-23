/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.misc.update;

import de.mpimp.golm.robin.GUI.RobinMainGUI;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class UpdateChecker {

    RobinMainGUI mainGUI;

    public UpdateChecker(RobinMainGUI main) {
        this.mainGUI = main;
    }

    public void checkForUpdates(int installedBuild) {
        String rawResponse = null;
        int remoteBuildNumber = 0;

        UpdateCheckerDialog dialog = new UpdateCheckerDialog(mainGUI, true, mainGUI.getDefaultSettings());
        dialog.setVisible(true);

        if (dialog.getUserChoice() == UpdateCheckerDialog.response.CANCEL_RESPONSE) {
            return;
        }

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://mapman.gabipd.org/web/guest/robin");
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            rawResponse = EntityUtils.toString(entity);

        } catch (Exception ioe) {
            ioe.printStackTrace();
            if (handleFailure())
                checkForUpdates(installedBuild);
            else
                return;
        }

        // extract the Robin version string from the raw resonse
        Pattern versionPattern = Pattern.compile("Download:.+build_(\\d+)");
        Matcher matcher = versionPattern.matcher(rawResponse);
        if (matcher.find()) {
            remoteBuildNumber = Integer.parseInt(matcher.group(1));
            System.out.println("remote build number: " + remoteBuildNumber);
            if (installedBuild < remoteBuildNumber) {
                handleNewRemoteBuild();
                return;
            } else {
                handleUpToDate();
                return;
            }

        } else {
            System.out.println("pattern not found");
        }

    }

    public boolean handleFailure() {
        int response = JOptionPane.showConfirmDialog(null,
                    "Failed to check for updates. Please\n" +
                    "make sure your internet connection is\n" +
                    "working. Click \"Yes\" to retry, \"No\" to cancel.",
                    "Failed update check",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return true;
        }
    }

    public void handleNewRemoteBuild() {
        int response = JOptionPane.showConfirmDialog(null,
                    "A new version of Robin is available for\n" +
                    "download! Click \"Yes\" to visit the\n" +
                    "Robin website.",
                    "New version available",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            return;
        } else {
            //TODO implement opening the webpage using the browser launcher
            System.out.println("DOWNLOAD");
            
            try {
                BrowserLauncher.openURL("http://mapman.gabipd.org/web/guest/robin");
            } catch (UnsupportedOperatingSystemException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BrowserLaunchingExecutionException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BrowserLaunchingInitializingException ex) {
                Exceptions.printStackTrace(ex);
            }
            
        }
    }

    public void handleUpToDate() {
        JOptionPane.showMessageDialog(null, "Your installation is up to date.");
    }
}
