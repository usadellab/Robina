/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.mapping;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

/**
 *
 * Constructs a convenience object for accessing the fields of a bowtie alignment
 * expects format described in :http://bowtie-bio.sourceforge.net/manual.shtml#output
 * @author marc
 */
public class RNASeqBowtieAlignment {
    
    private String readName;
    private String referenceStrand;
    private String hitName;
    private String alignmentStart;
    private String readSequence;
    private String readQualities;
    private String otherAlignments;
    private ArrayList<Mismatch> mismatches;

    public RNASeqBowtieAlignment(String[] alignment) {
        readName = alignment[0];
        referenceStrand = alignment[1];
        hitName = alignment[2];
        alignmentStart = alignment[3];
        readSequence = alignment[4];
        readQualities = alignment[5];
        otherAlignments = alignment[6];
        mismatches = new ArrayList<Mismatch>();
        
        if ( (alignment.length >= 8) && alignment[7].matches("^(.*:.>.,?)+$") ) {
            //DEBUG
            //System.out.println("mis: <"+alignment[7]+">");
            for (String mismatch : alignment[7].split(",")) {
                mismatches.add(new Mismatch(mismatch));
            }
        } else {
            // DEBUG
            //System.out.println("no mismatches: "+StringUtils.join(alignment, " "));
        }
    } 
    
    public int getReadLength() {
        return getReadSequence().length();
    }

    public String getReadName() {
        return readName;
    }

    public String getReferenceStrand() {
        return referenceStrand;
    }

    public String getHitName() {
        return hitName;
    }

    public String getAlignmentStart() {
        return alignmentStart;
    }

    public String getReadSequence() {
        return readSequence;
    }

    public String getReadQualities() {
        return readQualities;
    }

    public String getOtherAlignments() {
        return otherAlignments;
    }

    public ArrayList<Mismatch> getMismatches() {
        return mismatches;
    }

    public static class Mismatch {
        
        private String offset;
        private String readBase;
        private String referenceBase; 

        public Mismatch(String mismatch) {
            String[] fields = mismatch.split(":");
            offset = fields[0];
            String[] bases = fields[1].split(">");
            readBase = bases[1];
            referenceBase = bases[0];
        }

        public int getOffset() {
            return Integer.parseInt(offset);
        }

        public String getReadBase() {
            return readBase;
        }

        public String getReferenceBase() {
            return referenceBase;
        }        
    }
}
