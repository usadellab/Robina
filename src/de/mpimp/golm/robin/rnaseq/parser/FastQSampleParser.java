/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.common.utilities.Utilities.EOL_TYPE;
import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 *
 * @author marc
 */
public class FastQSampleParser extends FastQParser {
    
    private long sampleSize = 100;
    private int sampleChunkSize = 400; // number of lines to read
    private RandomAccessFile rFile;
    private long fileLength;
    private long numberSampledEntries = 0;

//    public FastQSampleParser(ArrayList<File> files) {
//        super(files);
//    }

    public FastQSampleParser(FastQFile inputFile) {
        super(inputFile);
        this.setScanDepth(1); // stupid workaround to fix file length estimation without having to override the method
    }
    
//    @Override
//    public float getProgress() throws IOException {
//        float progress = 0;        
//        progress = ((float)rFile.getChannel().position() / (float)fileLength) *100;        
//        return progress;
//    }
    
    @Override
    protected void readFile(FastQFile f) throws FileNotFoundException, IOException, Exception {
        rFile = new RandomAccessFile(f, "r");
        fileLength = rFile.length();        
        
        // this needs further refinement for small files to make sure 
        // that the chunks don't overlap
        long offsetStepSize = fileLength / sampleSize;
        long offset = 0;
        long chunkCounter = 0;
        char lowestQualCharInFile = 128;        
        EOL_TYPE eol = Utilities.guessEOLType(f);
        long bytesRead = 0;
        
        while (offset < rFile.length()) {
            
            // read a bunch of 8 lines to make sure we get at least one FastQEntry
            rFile.seek(offset);            
            StringBuffer sb = new StringBuffer();
            int i;
            for (i=0; i <= sampleChunkSize +1 ; i++) {
                String line = rFile.readLine();
                if (line != null) {
                    bytesRead = offset + line.length() + eol.getByteSize();
                    sb.append(line + "\n");
                } else {
                    break;
                }                
            }            
            
            // does the last chunk contain a full entry?
            if (i < 8) {
                //System.out.println("samples read="+chunkCounter);
                seenVersions.put(QualityEncoding.guessQualityEncoding(lowestQualCharInFile), 1);
                return;
            }    
            
            ArrayList<FastQEntry> sampleEntries = extractFastQEntriesFromStringBuffer(sb);
            for (FastQEntry entry : sampleEntries) {
                char lowestCharInLine = extractLowestChar(entry.getQuality());
                if (lowestCharInLine < lowestQualCharInFile) {
                    lowestQualCharInFile = lowestCharInLine;
                }

                // commit entry if not in scan mode
                if (!scanMode) entries.add(entry);
                entryCount++;
                numberSampledEntries++;
                entryLengths.put(entry.getByteSize() + (eol.getByteSize()*4), 
                            entryLengths.containsKey(entry.getByteSize() + (eol.getByteSize()*4) ) ? entryLengths.get(entry.getByteSize() + (eol.getByteSize()*4)) + 1 : 1);

                runQualityChecks(entry);
            }
            
            offset += offsetStepSize;
            chunkCounter++;          
            
            progress.set((int)(((float)bytesRead/fileLength)*100));    
            
            if (cancelRun) break;
        }   
        //System.out.println("samples read="+chunkCounter);
        seenVersions.put(QualityEncoding.guessQualityEncoding(lowestQualCharInFile), 1);
        progress.set(100);
    }

    public long getNumberOfSampledEntries() {
        return numberSampledEntries;
    }
    
    public long getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(long sampleSize) {
        this.sampleSize = sampleSize;
    }

    public int getSampleChunkSize() {
        return sampleChunkSize;
    }

    public void setSampleChunkSize(int sampleChunkSize) {
        this.sampleChunkSize = sampleChunkSize;
    }
    
    private ArrayList<FastQEntry> extractFastQEntriesFromStringBuffer(StringBuffer buffer) throws Exception {
        ArrayList<FastQEntry> entries = new ArrayList<FastQEntry>();
        String[] lines = buffer.toString().split("\\n"); 
        
        
        for (int i = 0; i < lines.length - 3; i++) {
            if ( (lines[i].startsWith("@")) && (lines[i+2].startsWith("+"))) {
                FastQEntry entry = new FastQEntry();
                entry.setIdentifier(lines[i]);
                entry.setSequence(lines[i+1]);
                entry.setComment(lines[i+2]);
                entry.setQuality(lines[i+3]);
                
                entry.runSelfCheck();
                
                entries.add(entry);
            }            
        }        
        return entries;
    }
    
    private FastQEntry extractFastQEntryFromStringBuffer(StringBuffer buffer) {
        FastQEntry entry = new FastQEntry();
        String[] lines = buffer.toString().split("\\n"); 
        
        
        for (int i = 0; i < lines.length - 3; i++) {
            if ( (lines[i].startsWith("@")) && (lines[i+2].startsWith("+"))) {
                entry.setIdentifier(lines[i]);
                entry.setSequence(lines[i+1]);
                entry.setComment(lines[i+2]);
                entry.setQuality(lines[i+3]);
            }            
        }        
        return entry;
    }
    
    
}
