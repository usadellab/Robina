/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.R;


import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.warnings.Warning;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;


/**
 *
 * @author marc
 */
public class RTask extends Thread {
    private String sRCall = "";
    private String inputFiles = "";
    private String outputFile = "";
    private String method = "";
    
    private String pathToR = "";
    private String startRcommand = "";
    private String scriptFile = "";
    private String Rargs;
        
    private int exitValue = 0;
    private String outputMessage = "";
    private String warningMessage;
    private boolean hasWarning;
    private Warning warning;
    private ArrayList<Warning> warnings = new ArrayList<Warning>();

    public Warning getWarning() {
        return warning;
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
    }    

    public boolean hasWarning() {
        return hasWarning;
    }

    public String getWarningMessage() {
        return warningMessage;
    }
    
    public String getOutputMessage() {
        return outputMessage;
    }
    
    public int getExitValue() {
        return exitValue;
    }   
    
    public String getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(String inputFiles) {
        this.inputFiles = inputFiles;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
    
    /*public RTask(String sRCall) {
    this.sRCall = sRCall;
    }*/

    /**
     * 
     * @param path  Pathto where R is
     * @param cmd   Command to run R
     * @param args  arguments for R
     * @param scriptfile the scriptfile that has been genereated and that R should process
     * most of these come from the robin.conf file
     */
    public RTask(String path, String cmd, String args, File scriptfile) {
        this.pathToR = path;
        this.startRcommand = cmd;
        try {
            this.scriptFile = scriptfile.getCanonicalPath();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }


        // check memory availability and allocate all free memory?
        // this actually produces errors over errors 
        //int freeMB = new Double( Utilities.longAsMegaBytes(Utilities.getFreePhysicalMemorySize())).intValue();
        //DEBUG
        //new em(null, "free size "+freeMB);
        //args = "--max-vsize=" + freeMB + "M " + args;

        this.Rargs = args;
    } 


    @Override
    public void run() {
        try {

            ArrayList<String> args = new ArrayList<String>();
            args.add(this.pathToR+this.startRcommand);
           
            for (String par : StringUtils.split(this.Rargs, "|")) {
                
                if (par.contains("file")) {
                    par = par.concat(this.scriptFile);
                }
                args.add(par);
                //System.out.println("argsarray>"+par);
            }
              
            String command = StringUtils.join(args, " ");
            SimpleLogger.getLogger(true).logMessage("R-cmd: <"+command+">");
            
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            
            //FIXME this throws a null pointer exception because
            // the process builder DOES NOT HAVE A VALID WORKING DIR?!
            // pass him one!
            //pb.directory(new File(pathToR));
            //System.out.println("working dir of pb :"+pb.directory());

            final Process p  = pb.start();
            final StringBuilder msg = new StringBuilder();            
           
           // read the output blocking - doesn't matter
           // because we're in a detached thread already
           // and actually want this thing to blobk until
           // the R task is finished and all out put from 
           // the R engine is read
           InputStream is = p.getInputStream();
           InputStreamReader reader = new InputStreamReader(is);
           BufferedReader bReader = new BufferedReader(reader);


           String line;                   
           try {
               while ((line = bReader.readLine()) != null) {
                    msg.append(line+"\n");
                    Thread.sleep(100);
               }
           } catch (Exception ex){
                ex.printStackTrace();
                p.destroy();
           }
           p.waitFor();
           this.exitValue = p.exitValue();
           this.outputMessage = msg.toString();
           
           // parse output for warnings
           Pattern errCapt =  Pattern.compile("(?s).*?__WARNINGS__(.+?)__WARNINGS_END__.*?");
           Matcher matcher = errCapt.matcher(outputMessage);

           while (matcher.find()) {
               hasWarning = true;
               System.out.println(this.getMethod()+" has warnings");
               System.out.println("TASK HAS "+matcher.groupCount()+" WARNINGS.");
               //TODO sequentially process all warnings!
               //while (matcher.find()) {
                   System.out.println("match");
                   warningMessage = matcher.group(1);
                   warningMessage = warningMessage.replaceAll("\\[.*\\]\\s*", "");
                   warningMessage = warningMessage.replaceAll("__WARNINGS__", "");
                   warningMessage = warningMessage.replaceAll("__WARNINGS_END__", "");
                   // construct a warning object from the message
                   constructWarning(warningMessage);
               //}       
           }
           
           System.out.println("method: "+method+" exit value:"+exitValue);
        } catch (Exception ex1) {
            SimpleLogger.getLogger(true).logException(ex1);
            this.outputMessage = ex1.toString();
            this.exitValue = 1;            
        }
    }

    private void constructWarning(String wmsg) {
        String type = "";
        int sev = 0;
        
        Pattern pat =  Pattern.compile("(?s).*TYPE:([\\w ]+).*");
        Matcher matcher = pat.matcher(wmsg);
        if (matcher.matches()) {
            type = matcher.group(1);
        }
        
        pat =  Pattern.compile("(?s).*SEVERITY:(\\d+).*");
        matcher = pat.matcher(wmsg);
        if (matcher.matches()) {
            sev = Integer.parseInt(matcher.group(1));            
        }
        
        wmsg = wmsg.replaceAll("(?m)^.*TYPE:.*$", "");
        wmsg = wmsg.replaceAll("(?m)^.*SEVERITY:.*$", "");
        
        this.warningMessage = wmsg;
        
        System.out.println("WARNING MESSAGE:"+wmsg);
        System.out.println("WARNING TYPE:"+type);
        System.out.println("WARNING SEV:"+sev);
        
        warning = new Warning(type, wmsg, sev);
        warnings.add(new Warning(type, wmsg, sev));
    }
}
