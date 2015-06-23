/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

/**
 *
 * @author marc
 */
public class FastQEntry {
    private String identifier;
    private String sequence;
    private String quality;
    private String comment;
    private int byteSize = 0;
    
    public FastQEntry(String id, String seq, String qual, String com) {
        this.identifier = id;
        this.quality = qual;
        this.comment = com;
        this.sequence = seq;
    }

    FastQEntry() {
    }
    
    @Override
    public String toString() {
        return identifier + "\n" + sequence + "\n" + comment + "\n" + quality;
    }

    public String getComment() {
        return comment;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getQuality() {
        return quality;
    }

    public String getSequence() {
        return sequence;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getByteSize() {
        if (byteSize == 0) {
            byteSize = sequence.length() + quality.length() + comment.length() + identifier.length();
        }
        return byteSize;
    }

    public void runSelfCheck() throws Exception {
        if (! (this.getSequence().length() == this.getQuality().length()) ) {
            throw new Exception("Sequence and quality score lengths inconsistent for entry \n" + this.toString());
        } 
        
        for (char s : this.getSequence().toLowerCase().toCharArray()) {
            switch (s) {
                case 'a' : 
                    continue;
                case 'c' :
                    continue;
                case 'g' :
                    continue;
                case 't' :
                    continue;
                case 'n' :
                    continue;
                default : 
                    throw new Exception("Illegal sequence character '"+s+"' in entry: \n"+this.toString());
            }
        }
    }
    
    
}
