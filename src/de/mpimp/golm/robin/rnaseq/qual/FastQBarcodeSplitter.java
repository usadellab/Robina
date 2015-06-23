/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import com.itextpdf.text.pdf.PdfPTable;
import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class FastQBarcodeSplitter implements RNASeqQualityCheck {
    
    private File inputFile; 
    private File outputPath;
    private HashMap<String, String> barcodes;
    private HashMap<String, FileWriter> outputFiles;
    private int maxMisMatch = 0;

    public FastQBarcodeSplitter(File inputFile, HashMap<String, String> barcodes, int mismatch) throws IOException {
        this.inputFile = inputFile;
        this.barcodes = barcodes;
        this.maxMisMatch = mismatch;
        
        // create output files - one for each barcode
        for (String label : barcodes.keySet()) {
            outputFiles.put(label, new FileWriter(new File(outputPath, label + inputFile.getName())) );
        }      
        outputFiles.put("UNKNOWN", new FileWriter( new File(outputPath, "UNKNOWN" + inputFile.getName())) );
    }  
    
    public boolean processSeqEntry(FastQEntry entry) {
        try {
            for (String label : barcodes.keySet()) {
                if (maxMisMatch == 0) {
                    if (entry.getSequence().startsWith(barcodes.get(label))) {
                        // write entry to appropriate output file
                        outputFiles.get(label).write(entry.toString());
                        continue;
                    }
                } else {
                    if (getMisMatches(barcodes.get(label), entry.getSequence(), barcodes.get(label).length()) <= maxMisMatch) {
                        // write entry to appropriate output file
                        outputFiles.get(label).write(entry.toString());
                        continue;
                    }
                }
                // entry did not match any barcodes - write to UNKNOWN
                outputFiles.get("UNKNOWN").write(entry.toString());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }
    
    public void closeAllFiles() throws IOException {
        for (FileWriter f : outputFiles.values()) {
            f.close();
        }
    }
    
    private int getMisMatches(String a, String b, int range) {
        int mismatch = 0;        
        for (int i = 0; i < range; i++) {
            mismatch += (a.charAt(i) == b.charAt(i)) ? 0 : 1;
        }
        return mismatch;
    }
    
    
    public boolean hasTabularData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ChartPanel getResultChart(Dimension size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JPanel getResultPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getHTMLTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PdfPTable getPDFTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescriptionText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
