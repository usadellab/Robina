/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.misc.RobinUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.gabipd.parser.ParserException;
import org.gabipd.parser.fasta.FastaParser;
import org.gabipd.parser.fasta.FastaSequence;

/**
 *
 * @author marc
 */
public class RNASeqReferenceParser extends FastaParser {
    
    private int N50 = 0;
    private double Ncount;
    private boolean ambiguous = false;
    private ArrayList<String> ambiguousSeqs = new ArrayList<String>(); 
    private HashMap<String, SeqInfo> seqInfoData = new HashMap<String, SeqInfo>();
    
    
    public RNASeqReferenceParser(InputStream inputStream, int debugLevel)
			throws ParserException {
		super(new InputStreamReader(inputStream), debugLevel);
    }

    @Override
    public void processSequence(FastaSequence fs) throws ParserException {        
        
        if ( (fs.getType() != FastaSequence.TYPE.NUCLEOTIDE) && (fs.getType() != FastaSequence.TYPE.NUCLEOTIDE_AMBIGUOUS)) {
            throw new ParserException("Input sequence '" + fs.getHeader() + "' is not a nucleotide sequence.");
        }           
        
        if ( fs.getType() == FastaSequence.TYPE.NUCLEOTIDE_AMBIGUOUS ) {
            ambiguous = true;
            ambiguousSeqs.add(fs.getHeaderId());
        }
        
        Ncount += StringUtils.countMatches(fs.getSequence().toUpperCase(), "N");
        
        SeqInfo info = new SeqInfo(fs.getHeaderId());
        info.setLength(fs.getSequence().length());
        info.setGcCont(RobinUtilities.getGCContent(fs.getSequence()));
        
        seqInfoData.put(info.getName(), info );
        
    }
    
    public String getNcontent() {
        DecimalFormat df = new DecimalFormat("##.##%");
        SimpleLogger.getLogger(true).logMessage("Ncount="+Ncount+"\tsymbolsRead="+this.getNumberSymbolsRead());
        
        return df.format(Ncount / this.getNumberSymbolsRead()) ;
    }
    
    public boolean hasAmbiguous() {
        return ambiguous;
    }
    
    public int getNumberOfAmbiguousSequences() {
        return ambiguousSeqs.size();
    }
    
    public int getN50() {
        if (N50 != 0) return N50;
        if (seqInfoData.size() == 0) {
            System.out.println("call parseNow first!");
            return 0;
        }
        
        long runSum = 0;
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        for (SeqInfo i : seqInfoData.values()) {
            lengths.add(i.getLength());
        }
        Collections.sort(lengths);
        
        for (Integer len : lengths ) {            
            runSum += len;
            if (runSum >= (this.getNumberSymbolsRead() /2 )) {
                N50 = len;
                break;
            }
        }        
        return N50;
    }
    
    public void writeInfoTable(File table) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(table));
        for (String id : seqInfoData.keySet()) {
            SeqInfo info = seqInfoData.get(id);
            writer.write(id + "\t" + info.getLength() + "\t " + info.getGcCont() + "\n");
        }
        writer.close();  
    }    
    
    private class SeqInfo {
        
        private String name;
        private int length;
        private float gcCont;

        public SeqInfo(String name) {
            this.name = name;
        }
        
        public float getGcCont() {
            return gcCont;
        }

        public void setGcCont(float gcCont) {
            this.gcCont = gcCont;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
