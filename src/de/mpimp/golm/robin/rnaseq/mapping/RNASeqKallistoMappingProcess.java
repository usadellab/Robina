package de.mpimp.golm.robin.rnaseq.mapping;


import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import de.mpimp.golm.robin.annotation.AbstractAnnotationProvider;
import de.mpimp.golm.robin.misc.GUI.VerboseWarningDialog;
import de.mpimp.golm.robin.rnaseq.parser.FastQFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.Location;
import org.openide.util.Exceptions;

/**
 *
 * @author Björn USadel
 */
public class RNASeqKallistoMappingProcess extends RNASeqAbstractMappingProcess {

    private AbstractAnnotationProvider annotation;
   // private BufferedWriter ambiguousReadsWriter;
   // private BufferedWriter uniqueReadsWriter;
    private long uniqueHits = 0, isoFormHits = 0, ambiguousHits = 0, overLapGenesHits = 0, unannotatedHits = 0;
    private int exitCode; 
    private ArrayList<FastQFile> inputFiles; 
    private RNASeqBowtieErrorRecorder errorRecorder;
    private File KallistoOutFile;

    public RNASeqKallistoMappingProcess(RNASeqWorkflowPanel mainPanel, List<String> command) {
        this.mainPanel = mainPanel;
        this.command = command;
        this.errorRecorder = new RNASeqBowtieErrorRecorder();
        System.out.println("simple oinv");
      
    }
    
    /** 
     *  
     * @param mainPanel
     * @param name
     * @param kallistoPath path to mappers
     * @param args
     * @param index
     * @param queryFiles
     */
    public RNASeqKallistoMappingProcess(
            RNASeqWorkflowPanel mainPanel,
            String name,
            final File kallistoPath,
            final ArrayList<String> args,
            final File index,
            final ArrayList<FastQFile> queryFiles) {
    	System.out.println("fop");

        try {
            this.mainPanel = mainPanel;
            this.name = name;
            this.command = new ArrayList<String>();
            this.inputFiles = queryFiles;
            this.errorRecorder = new RNASeqBowtieErrorRecorder();
            //initAmbiguousWriter();
            command.add(kallistoPath.getCanonicalPath());
            command.add("quant");
            command.add("--single");
            command.addAll(args);
          /*  command.add("-l 200");
            command.add("-s 20");*/
            KallistoOutFile =  new File(mainPanel.getDataModel().getDetailedResultsDir(),
            			mainPanel.getDataModel().getExperimentName() + "_" + name); 
            command.add("-o");
            command.add(KallistoOutFile.getCanonicalPath()); 
            //command.addAll(args);
            command.add("--index");
           
            command.add(index.getCanonicalPath());
            //command.add("D:\\backup\\ZUKUNFTSFORUM\\_______HH\\IND");

            /* add individual FASTQ files 
             note that these jointly make up the full file one process one sample! 
             */
            String queries = null;
            if (Utilities.isMacOSX() || Utilities.isLinux()) {
                ArrayList<String> names = new ArrayList<String>();
                for (FastQFile file : queryFiles) {
                    names.add(file.getCanonicalPath());
                }
                queries = StringUtils.join(names, ",").replaceAll(" ", "\\ ");
            } else if (Utilities.isWindows()) {

                queries = "\"";
                for (int i = 0; i < queryFiles.size(); i++) {
                    queries += queryFiles.get(i).getCanonicalPath();
                    if (i == queryFiles.size() - 1) {
                        break;
                    }
                    queries += "\",\"";
                }
                queries += "\"";
            }
            command.add(queries);
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }

 
    public void run() {
        String cmdline = StringUtils.join(command, " ");
        //cmdline="" + queries ;
        System.out.println(command);
        SimpleLogger.getLogger(true).logMessage("kallisto-cmd: <" + cmdline + ">");

        //procBuilder = new ProcessBuilder(cmdline.split("\\s+"));
        procBuilder = new ProcessBuilder(command);

        procBuilder.redirectErrorStream(true);
        try {
            final Process proc = procBuilder.start();
            InputStream is = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bReader = new BufferedReader(reader, 10000000);
            String line;
            try {
                while ((line = bReader.readLine()) != null) {

                    if (wantsToDie.get()) {
                        proc.destroy();
                        proc.waitFor();
                        return;
                    }
                    //parseLine(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SimpleLogger.getLogger(true).logMessage("destroying kallisto process "+proc);
                proc.destroy();
            }
            proc.waitFor();
            
            InputStreamReader Kreader = new FileReader(new File (KallistoOutFile.getCanonicalPath(), "abundance.tsv"));
            BufferedReader KbReader = new BufferedReader(Kreader, 10000000);
            int linecnt=0;
            while ((line = KbReader.readLine()) != null) {
           	 try {
           		 if (linecnt>0) {
           		 parseLine(line);}
           		 linecnt++;
           	 }catch (Exception ex) {  ex.printStackTrace(); }
            }
            Kreader.close();
            System.out.println("analy data");
            //stats json file
            Kreader = new FileReader(new File (KallistoOutFile.getCanonicalPath(), "run_info.json"));
            KbReader = new BufferedReader(Kreader, 10000000);
            while ((line = KbReader.readLine()) != null) {
           	 report.append(line);
             }
            Kreader.close();
        
            this.exitCode = proc.exitValue();
            
            if (exitCode != 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("Kallisto mapping process for input file(s)\n");
                for (FastQFile file : inputFiles) {
                    builder.append(file.getName()+"\n");
                }
                builder.append("finished with errors (exit code "+exitCode+")");           
                SimpleLogger.getLogger(true).logException(
                        new Exception(builder.toString() + "\ncmd: <"+cmdline+">\n"));
            }
           

        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);            
        } catch (InterruptedException i) {
            SimpleLogger.getLogger(true).logException(i);            
        }
    }
    
    public void flushCounts() {
        
    }


	protected void parseLine(String line) throws Exception {
		/* need to see about first line */
		System.out.println(line);
		String readname="";
		String[] elements = line.split("\t");
		try {

			readname = elements[0];
			
				 /* target_id length eff_length est_counts tpm AT1G50920 2016 1817 46 53.5388
				 */
				countsTable.put(readname, (int) Double.parseDouble(elements[3]));
				System.out.println(readname);

			
			// lastReadName.getAndSet(readname); // record the read name
		} catch (ArrayIndexOutOfBoundsException e) {
			SimpleLogger.getLogger(true).logMessage("skipping bad read: <" + line + readname+ ">");
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder();
			builder.append("Kallisto mapping process for input file(s)\n");
			for (FastQFile file : inputFiles) {
				builder.append(file.getName() + "\n");
			}
			builder.append("failed with an unexpected error:" + e.getMessage());
			builder.append("while processing line:\n");
			builder.append("<" + line + ">");
			SimpleLogger.getLogger(true).logException(new Exception(builder.toString(), e));
			throw (e);
		}
	}

 
    public int getExitCode() {
        return exitCode;
    }

    public RNASeqBowtieErrorRecorder getErrorRecorder() {
        return errorRecorder;
    }

    @Override
    /**
     * Needs to extract the percentage of reads with at least one valid alignment
     * from the aligment report of a format like:
     * 
     * 	"p_pseudoaligned": 30.7,
     */
    public float getPercentAligned() {
        String report = this.getReport();
        System.out.println(report);
        Pattern percPattern;
        percPattern = Pattern.compile("p_pseudoaligned\": (\\d+.\\d+)", Pattern.MULTILINE);
        Matcher matcher = percPattern.matcher(report);
       
        if (!matcher.find()) {
            Exception badRep = new Exception("Could not extract percentage "
                    + "of aligned reads - unknown report format:\n"+ report);
            SimpleLogger.getLogger(true).logException(badRep);
            //throw badRep;
        }
        System.out.println("LOG"+matcher.group(1));
        String perc = matcher.group(1);
        return Float.parseFloat(perc);        
    }
    
}