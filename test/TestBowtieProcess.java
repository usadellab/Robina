
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.annotation.GFF3.AnnotationMap;
import de.mpimp.golm.robin.annotation.GFF3.GFF3AnnotationProvider;
import de.mpimp.golm.robin.rnaseq.mapping.RNASeqBowtieMappingProcess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.openide.util.Exceptions;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestBowtieProcess extends TestCase {
    
    public void xtestGenome() throws InterruptedException, IOException {
//        final File btbin = new File("C:\\Documents and Settings\\marc\\workspace\\Robin-NGS\\dist\\bin\\bowtie_windows.exe");
//        final File btind = new File("C:\\Documents and Settings\\marc\\workspace\\Robin-NGS\\dist\\index\\barley_lfl.fasta_TRANSCRIPTOME_bwtindex");
//        final String queries = "\"C:\\Documents and Settings\\marc\\Desktop\\NGTEST10\\input\\TRIMMED_NG-5335_1_T5K1WaL.fastq.sample.fastq\","+
//                            "\"C:\\Documents and Settings\\marc\\Desktop\\NGTEST10\\input\\TRIMMED_NG-5335_1_T5K1WaB.fastq.sample.fastq\"";
        
        final File btbin = new File("/Users/marc/Development/workspace/Robin_CURRENT/bin/bowtie_mac-x86_64");
        //final File btind = new File("/Users/marc/Development/workspace/Robin_CURRENT/index/CM3.5.scaffolds.softmask_20110224.fa_GENOME_bwtindex");
        final File btind = new File("/Users/marc/Desktop/TAIR10_ANNOTEST/bwt_index/TAIR10_chr_all");
        final String queries = "/Users/marc/Desktop/TAIR10_ANNOTEST/RobiNA_testset/100.fq";//+
        //final String queries = "/Users/marc/Development/workspace/Robin_CURRENT/testdata/NG-5335_1_T5K1TrB.fastq.sample.fastq,";//+
               // "/Users/marc/Development/workspace/Robin_CURRENT/testdata/NG-5335_1_T5K1TrL.fastq.sample.fastq";
        
        GFF3Reader reader = new GFF3Reader();
        FeatureList all_annotation =  reader.read("/Users/marc/Desktop/TAIR10_ANNOTEST/TAIR10_GFF3_genes.gff");
        GFF3AnnotationProvider annotation = new GFF3AnnotationProvider();
        annotation.addFeatures(all_annotation.selectByType("exon"));
        annotation.addFeatures(all_annotation.selectByType("five_prime_UTR"));
        annotation.addFeatures(all_annotation.selectByType("three_prime_UTR"));
        
        ArrayList<String> cmd = new ArrayList<String>() {{
            add(btbin.getCanonicalPath());            
            //add("-a");
            add("-k100");
            add("--best");
            add("--strata");
            add("-n3");
            add("-l28");
            add("-e70");
            add(btind.getCanonicalPath());
            add(queries);            
        }};
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        
        RNASeqBowtieMappingProcess process = new RNASeqBowtieMappingProcess(null, cmd);
        process.setAnnotation(annotation);
        
        Future fut = executor.submit(process);
        
        while (!fut.isDone()) {
            //System.out.println("readsaligned: "+process.getReadsAligned());
            System.out.print(".");
            Thread.sleep(1000);
        }
        
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/marc/Desktop/TAIR10_ANNOTEST/genome_res.txt")));
        for (String gene : process.getCountsTable().keySet()) {
            writer.write(gene+"\t"+process.getCountsTable().get(gene)+"\n");
            //System.out.println(gene+"\t"+process.getCountsTable().get(gene));
        }
        writer.close();
        
        System.out.println("Alignment report:\n"+process.getReport()+"\n");
        //System.out.println("Unannotated alignments\n"+annotation.getNumberUnannotated()+"\n");
    }
    
    public void xtestCdna() throws InterruptedException, IOException { 
        
        final File btbin = new File("/Users/marc/Development/workspace/Robin_CURRENT/bin/bowtie_mac-x86_64");              
        final File btind = new File("/Users/marc/Desktop/TAIR10_ANNOTEST/bwt_index/TAIR10_cdna");
        final String queries = "/Users/marc/Desktop/TAIR10_ANNOTEST/RobiNA_testset/100.fq";        
        
        ArrayList<String> cmd = new ArrayList<String>() {{
            add(btbin.getCanonicalPath());            
            //add("-a");
            add("-k100");
            add("--best");
            add("--strata");
            add("-n3");
            add("-l28");
            add("-e70");
            add(btind.getCanonicalPath());
            add(queries);            
        }};        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        
        RNASeqBowtieMappingProcess process = new RNASeqBowtieMappingProcess(null, cmd);
       
        
        Future fut = executor.submit(process);
        
        while (!fut.isDone()) {
            //System.out.println("readsaligned: "+process.getReadsAligned());
            System.out.print(".");
            Thread.sleep(1000);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/marc/Desktop/TAIR10_ANNOTEST/cdna_res.txt")));
        for (String gene : process.getCountsTable().keySet()) {
            writer.write(gene+"\t"+process.getCountsTable().get(gene)+"\n");
            //System.out.println(gene+"\t"+process.getCountsTable().get(gene));
        }
        writer.close();        
        System.out.println("Alignment report:\n"+process.getReport()+"\n");
    }
    
    public void xtestExon() throws InterruptedException, IOException { 
        
        final File btbin = new File("/Users/marc/Development/workspace/Robin_CURRENT/bin/bowtie_mac-x86_64");              
        final File btind = new File("/Users/marc/Desktop/TAIR10_ANNOTEST/bwt_index/TAIR10_exon");
        final String queries = "/Users/marc/Desktop/TAIR10_ANNOTEST/RobiNA_testset/100.fq";        
        
        ArrayList<String> cmd = new ArrayList<String>() {{
            add(btbin.getCanonicalPath());            
            //add("-a");
            add("-k100");
            add("--best");
            add("--strata");
            add("-n3");
            add("-l28");
            add("-e70");
            add(btind.getCanonicalPath());
            add(queries);            
        }};        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        
        RNASeqBowtieMappingProcess process = new RNASeqBowtieMappingProcess(null, cmd);
       
        
        Future fut = executor.submit(process);
        
        while (!fut.isDone()) {
            //System.out.println("readsaligned: "+process.getReadsAligned());
            System.out.print(".");
            Thread.sleep(1000);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/marc/Desktop/TAIR10_ANNOTEST/exon_res.txt")));
        for (String gene : process.getCountsTable().keySet()) {
            writer.write(gene+"\t"+process.getCountsTable().get(gene)+"\n");
            //System.out.println(gene+"\t"+process.getCountsTable().get(gene));
        }
        writer.close();        
        System.out.println("Alignment report:\n"+process.getReport()+"\n");
    }
    
    public void xtestStulle() {
        
        ArrayList<String> command = new ArrayList<String>() {{
            add("/Users/marc/Desktop/aligners/bowtie/mac/x86_64/bowtie-0.12.7/bowtie");
            add("-a");
            add("-m1");
            add("--best");
            add("--strata");
            add("/Users/marc/Desktop/aligners/bowtie/araindex/araindex");
            add("/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq");            
        }};
        
        String cmdline = StringUtils.join(command, " ");
        System.out.println("cmd: <"+cmdline+">");
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        procBuilder.redirectErrorStream(true);
        try {
            final Process proc = procBuilder.start();


            InputStream is = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bReader = new BufferedReader(reader);

            String line;
            try {
                while ((line = bReader.readLine()) != null) {
                    System.out.println(line);
                    Thread.sleep(100);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                proc.destroy();
            }
            proc.waitFor();
            
           System.out.println("exit value="+proc.exitValue());

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException i) {
            Exceptions.printStackTrace(i);
        }
    
    }
    
    public void testgetPercentAligned() {
        String report = "# reads processed: 100000\n" +
                        "# reads with at least one reported alignment: 41668 (41.67%)\n" +
                        "# reads that failed to align: 53211 (53.21%)\n" +
                        "# reads with alignments suppressed due to -m: 5121 (5.12%)\n" +
                        "Reported 41668 alignments to 1 output stream(s)";
        
        Pattern percPattern;
        percPattern = Pattern.compile("^# reads with at least one reported alignment: \\d+ \\((\\d+.\\d+)%\\)$", Pattern.MULTILINE);
        Matcher matcher = percPattern.matcher(report);
        if (!matcher.find()) {
            Exception badRep = new Exception("Could not extract percentage of aligned reads - unknown report format:\n"+ report);
            SimpleLogger.getLogger(true).logException(badRep);
            //throw badRep;
        }
        
        String perc = matcher.group(1);
        float p = Float.parseFloat(perc);
        
        //DEBUG
        System.out.println(p);
        
        if (p < 10) System.out.println("low alignment rate!");
        if (p > 10) System.out.println("high alignment rate!");
        
        
    }
    
}
