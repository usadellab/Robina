/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin;

import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author marc
 */
public class RobinAppDelegate {

    private final static File userHome = new File(System.getProperty("user.home"));
    private final static File robinData = new File(userHome, ".robindata");

    public static void checkInstallationIntegrity() throws IOException, URISyntaxException {
        File mappings = new File(robinData, "mappings");
        File presets = new File(robinData, "presets");
        File index = new File(robinData, "index");

        if (!Utilities.existsAndHasContent(robinData, null)) {
            new SimpleErrorMessage(null, "The " + robinData.getCanonicalPath() + " data directory was not found.\n"
                    + "You are either not the user who installed RobiNA or the directory\n"
                    + "was accidentally deleted or moved. RobiNA will now rebuild a default\n"
                    + "data directory.");
            setupDefaultRobindata();
            return;
        }

        // robindata exists - let's check whether the expected content is there  

        //mappings 
        if (!Utilities.existsAndHasContent(mappings, new ArrayList<String>() {
            {
                add(".m02");
                add(".m01");
                add(".xls");
                add(".txt");
            }
        })) {
            new SimpleErrorMessage(null, "RobiNA could not find any MapMan mapping files which are\n"
                    + "needed to annotate the result data at the end of the analysis.\n"
                    + "This step is not essential for successfully finishing the analysis,\n"
                    + "but can nevertheless be useful. RobiNA will now (re)create the default\n"
                    + "set of mappings.");
            installDefaultMappings();
        } else if (mappings.exists() && (mappings.listFiles().length == 0)) {
            new SimpleErrorMessage(null, "RobiNA could not find any MapMan mapping files which are\n"
                    + "needed to annotate the result data at the end of the analysis.\n"
                    + "This step is not essential for successfully finishing the analysis,\n"
                    + "but can nevertheless be useful. RobiNA will now (re)create the default\n"
                    + "set if mappings.");
            installDefaultMappings();
        }


        // presets
        if (!Utilities.existsAndHasContent(presets, new ArrayList<String>() {
            {
                add(".scimport");
                add(".sclayout");
                add(".layout");
            }
        })) {
            new SimpleErrorMessage(null, "RobiNA could not find any import and layout presets.\n"
                    + "These preset values facilitate importing microarray data\n"
                    + "originating from known platforms. RobiNA will now (re)create\n"
                    + "the default set of presets.\n");
            installDefaultPresets();
        } else if (presets.exists() && (presets.listFiles().length == 0)) {
            new SimpleErrorMessage(null, "RobiNA could not find any import and layout presets.\n"
                    + "These preset values facilitate importing microarray data\n"
                    + "originating from known platforms. RobiNA will now (re)create\n"
                    + "the default set of presets.\n");
            installDefaultPresets();
        }

        if (!index.exists()) {
            boolean success = index.mkdir();
            if (!success) {
                throw new IOException("could not create index directory: " + index.getCanonicalPath());
            }
        }


    }

    private static void setupDefaultRobindata() throws IOException, URISyntaxException {

        boolean success = false;
        if (!robinData.exists()) {
            success = robinData.mkdir();
        }

        File index = new File(robinData, "index");
        success = index.mkdir();

        if (!success) {
            throw new IOException("could not create directory: " + index.getCanonicalPath());
        }

        installDefaultMappings();
        installDefaultPresets();
    }

    private static void installDefaultMappings() throws IOException, URISyntaxException {
        File mappingsDir = new File(robinData, "mappings");

        boolean ok = true;
        if (!mappingsDir.exists()) {
            ok = mappingsDir.mkdir();
            mappingsDir.setWritable(true);
        } else if ((!mappingsDir.canWrite()) || (!mappingsDir.canRead())) {
            ok = false;
        }


        if (!ok) {
            throw new IOException("could not create mappings directory: " + mappingsDir.getCanonicalPath());
        }

        // copy default mappings into the mappings dir
        ArrayList<String> mappingFiles =
                getResourcesFromJarFile(RobinMainGUI.class, "de/mpimp/golm/robin/resources/robindata/mappings", Pattern.compile("^.*$"));

        for (String resource : mappingFiles) {
            
            //System.out.println("res:" + resource);

            InputStream resourceInputStream = RobinAppDelegate.class.getResourceAsStream("/" + resource);
            String resFileName = resource.substring(resource.lastIndexOf("/")+1);
            
            //System.out.println("resFileName:"+resFileName);

            File outFile = new File(mappingsDir, resFileName);
            if (outFile.createNewFile()) {
                FileOutputStream outStream = new FileOutputStream(outFile);
                IOUtils.copy(resourceInputStream, outStream);
                outStream.close();
            } else {
                throw new IOException("could not create file " + outFile.getCanonicalPath());
            }
        }        
    }

    private static void installDefaultPresets() throws IOException, URISyntaxException {
        File presetsDir = new File(robinData, "presets");

        boolean ok = true;
        if (!presetsDir.exists()) {
            ok = presetsDir.mkdir();
        } else if ((!presetsDir.canWrite()) || (!presetsDir.canRead())) {
            ok = false;
        }

        if (!ok) {
            throw new IOException("could not create presets directory: " + presetsDir.getCanonicalPath());
        }

        ArrayList<String> presetsFiles =
                getResourcesFromJarFile(RobinMainGUI.class, "de/mpimp/golm/robin/resources/robindata/presets", Pattern.compile("^.*$"));

        for (String resource : presetsFiles) {
            
            //System.out.println("res:" + resource);

            InputStream resourceInputStream = RobinAppDelegate.class.getResourceAsStream("/" + resource);
            String resFileName = resource.substring(resource.lastIndexOf("/")+1);
            
            //System.out.println("resFileName:"+resFileName);

            File outFile = new File(presetsDir, resFileName);
            if (outFile.createNewFile()) {
                FileOutputStream outStream = new FileOutputStream(outFile);
                IOUtils.copy(resourceInputStream, outStream);
                outStream.close();
            } else {
                throw new IOException("could not create file " + outFile.getCanonicalPath());
            }
        }
    }

    private static ArrayList<String> getResourcesList(Class cl, String packagePath) throws IOException {
        Enumeration<URL> contentURLs = Thread.currentThread().getContextClassLoader().getResources(packagePath);

        ArrayList<String> paths = new ArrayList<String>();

        System.out.println("getting resources:");

        while (contentURLs.hasMoreElements()) {
            String resource = contentURLs.nextElement().getPath();
            System.out.println("-->" + resource);
            paths.add(resource);
        }

        return paths;

    }

    private static ArrayList<String> getResourcesFromJarFile(
            final Class clazz,
            final String path,
            final Pattern pattern) throws UnsupportedEncodingException {

        URL dirURL = clazz.getClassLoader().getResource(path);
        if (!dirURL.getProtocol().equals("jar")) {
            System.err.println("not a jar file!");
            return null;
        }

        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
        File jar = new File(URLDecoder.decode(jarPath, "UTF-8"));

        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(jar);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            
            // we don't want the directory name we asked for - just the contents
            if (fileName.equals(path + "/")) {
                continue;
            }
            
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept && fileName.startsWith(path)) {
                retval.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }
}
