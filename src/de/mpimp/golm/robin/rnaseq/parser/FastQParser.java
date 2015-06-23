/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.common.utilities.Utilities.EOL_TYPE;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import de.mpimp.golm.robin.rnaseq.qual.RNASeqQualityCheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import org.itadaki.bzip2.BZip2InputStream;

/**
 *
 * @author marc
 */
public class FastQParser {
    
    protected ArrayList<FastQFile> inputFiles = new ArrayList<FastQFile>();
    protected ArrayList<FastQEntry> entries;
    protected ArrayList<RNASeqQualityCheck> qualChecks = new ArrayList<RNASeqQualityCheck>();    
    protected HashMap<VERSION, Integer> seenVersions = new HashMap<VERSION, Integer>(); 
    protected boolean scanMode = false;
    private long scanDepth = 0;
    protected long entryCount = 0;
    private FileInputStream inputStream;
    private long fileLength;
    protected AtomicInteger progress;
    private boolean hasParsed = false;    
    protected HashMap<Integer, Integer> entryLengths;
    protected boolean cancelRun = false;
    protected float compressionFactor = 1.0f;

    
    public FastQParser(FastQFile inputFile) { 
        this.inputFiles.add(inputFile);
        initialize();
    }
    
    public FastQParser(ArrayList<FastQFile> files) {
        this.inputFiles = files;
        initialize();
    }
    
    public void parseNow() throws FileNotFoundException, IOException, Exception {        
        for (FastQFile f : this.inputFiles) {
            inputStream = new FileInputStream(f);            
            fileLength = f.length();
            readFile(f);
            f.setReadCount(this.getNumberOfEntries());
        }      
        hasParsed = true;
    }
    
    private void initialize() {
        this.entries = new ArrayList<FastQEntry>();        
        this.progress = new AtomicInteger(0); 
        this.hasParsed = false;
        this.entryLengths = new HashMap<Integer, Integer>();
    }
    
    public void reset() {
        this.inputFiles = new ArrayList<FastQFile>();
        this.entryCount = 0;
        this.entries = new ArrayList<FastQEntry>();
        initialize();
    }
    
    public int getProgress() throws IOException {
        int p = progress.get();
        return (p >= 100) ? 100 : p;
    }
    
    public long getNumberOfEntries() {
//        if (!hasParsed) {
//            System.out.println("parser did not run yet. call parseNow first.");
//            return 0;
//        }
        
        // we only scanned into the file - need to estimate total number based on entry size and file size
        if (scanDepth != 0) {
            float averageEntryByteSize = 0;            
            for (int i : entryLengths.keySet()) {
                averageEntryByteSize += (float)((float)entryLengths.get(i) / entryCount) * i;
            }            
            return (long) (fileLength / averageEntryByteSize);
        } else {
            return entryCount;
        }
    
    }
        
    protected void readFile(FastQFile f) throws FileNotFoundException, IOException, Exception {
        long lines = 0;
        long entryCounter = 0;
        long bytesRead = 0;
        String line = null;
        BufferedReader br = null;
        if (f.getName().toLowerCase().endsWith(".gz")) {
            System.out.println("GZIP: "+f.getName());
            br = new BufferedReader( new InputStreamReader(new GZIPInputStream(inputStream)) );
            compressionFactor = 4.5f;
        } else if (f.getName().toLowerCase().endsWith(".bz2")) {
            System.out.println("BZIP2: "+f.getName());
            br = new BufferedReader( new InputStreamReader( new BZip2InputStream(inputStream, false)) );
            compressionFactor = 5f;
            // makes problems:
        } else if (f.getName().toLowerCase().endsWith(".zip")) {
            System.out.println("ZIP: "+f.getName());
            br = new BufferedReader( new InputStreamReader( new ZipInputStream(inputStream)) );
        } else {
            br = new BufferedReader( new InputStreamReader(inputStream) );
        }
        
        FastQEntry entry = null;
        char lowestQualCharInFile = 128;
        EOL_TYPE eol = Utilities.guessEOLType(f);
        
        while ((line = br.readLine()) != null) {
            bytesRead += line.length() + 1;
            
            if (lines % 4 == 0) {
                if (!line.startsWith("@")) {
                    throw new Exception("FastQ entry does not start with @ID line! at line "+lines+"\n in file:\n"+f.getCanonicalPath()+
                            "\nline: '"+line+"'");
                }
                entry = new FastQEntry();
                entryCounter++;
                entry.setIdentifier(line);
            }
            if (lines % 4 == 1) {
                entry.setSequence(line);
            }
            if (lines % 4 == 2) {  
                if (!line.startsWith("+")) {
                    throw new Exception("FastQ entry comment does not start with '+' at line "+lines+"\n in file:\n"+f.getCanonicalPath()+
                            "\nline: '"+line+"'");
                }
                entry.setComment(line);
            }
            if (lines % 4 == 3) {  
                    
                // we could check for the complete range of chars here, regardless of the actual encoding version
//                if (!line.matches("(?i)^[BLABLABLABLA]+$")) {
//                    throw new Exception("FastQ entry has corrupted sequence at line "+lines+"\n in file:\n"+f.getCanonicalPath());
//                }
                
                entry.setQuality(line);   
                entry.runSelfCheck();
                
                char lowestCharInLine = extractLowestChar(line);
                if (lowestCharInLine < lowestQualCharInFile) {
                    lowestQualCharInFile = lowestCharInLine;
                }
                
                // commit entry if not in scan mode
                if (!scanMode) entries.add(entry);
                entryCount++;
                
                entryLengths.put(entry.getByteSize() + (eol.getByteSize()*4), 
                        entryLengths.containsKey(entry.getByteSize() + (eol.getByteSize()*4) ) ? entryLengths.get(entry.getByteSize() + (eol.getByteSize()*4)) + 1 : 1);
                
                // call quality check modules here
                runQualityChecks(entry);
            }   
            
            lines++;           
           
            progress.set((int)((((float)bytesRead/fileLength)*100)/compressionFactor));
            
            if ( (scanDepth != 0) && (entryCounter > this.scanDepth) ) {

                // guess entry's pipeline version...
                seenVersions.put(QualityEncoding.guessQualityEncoding(lowestQualCharInFile), 1);
                progress.set(100);
                return;
            }
            if (cancelRun) break;
        }
        progress.set(100);
        seenVersions.put(QualityEncoding.guessQualityEncoding(lowestQualCharInFile), 1);
    }
    
    protected void runQualityChecks(FastQEntry entry) {
        boolean allWell = true;
        for (RNASeqQualityCheck qc : qualChecks) {
            allWell = qc.processSeqEntry(entry);
            if(!allWell) {
                System.out.println("Problem processing entry: \n" +
                        entry.toString() + 
                        "\nmodule: \n" + 
                        qc.getClass().toString());
            }
        }
        
        if (!allWell) {
            //FIXME: Produce some warning
        }
    }
    
    public boolean inputHasSameVersions() {
        return (seenVersions.keySet().size() == 1) ? true : false;
    }
    
    public Set<VERSION> getVersions() {
        return seenVersions.keySet();
    }

    public ArrayList<FastQFile> getInputFiles() {
        return inputFiles;
    }

    public long getScanDepth() {
        return scanDepth;
    }

    public void setScanDepth(long scanDepth) {
        this.scanDepth = scanDepth;
    }

    public boolean isScanMode() {
        return scanMode;
    }

    public void setScanMode(boolean scanMode) {
        this.scanMode = scanMode;
    }

    public ArrayList<RNASeqQualityCheck> getQualChecks() {
        return qualChecks;
    }
    
    public ArrayList<FastQEntry> getEntries() {
        return entries;
    }
    
    protected char extractLowestChar(String line) {
        int lowest = 128;
        for (int i=0; i < line.length(); i++) {
            if (line.charAt(i) < lowest) {
                lowest = line.charAt(i);
            }
        }
        return (char) lowest;
    }
    
    public void registerQualCheckMethod(RNASeqQualityCheck qcMeth) {
        qualChecks.add(qcMeth);
    }

    public void flushMemory() {
        for (RNASeqQualityCheck c : qualChecks) {
            c = null;
        }
    }

    public void cancel() {
        this.cancelRun = true;
    }
    
    
    
}
