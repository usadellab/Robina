/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

/**
 *
 * @author marc
 */
public class QualityEncoding {
    
    public static enum VERSION {
        SOLEXA(64),
        ILLUMINA_1_3(64),
        ILLUMINA_1_5(64),
        ILLUMINA_1_8(33),
        UNKNOWN(0);
        
        private int offset;
        
        private VERSION(int offs) {  
            this.offset = offs;  
        }
        
        public int getOffset() {
            return offset;
        }
    }
    
    /*
     
     Quality encoding is guessed according to http://en.wikipedia.org/wiki/FASTQ_format
     
          SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS.....................................................
          ..........................XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX......................
          ...............................IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII......................
          .................................JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ......................
          LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.....................................................
          !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
          |                         |    |        |                              |                     |
         33                        59   64       73                            104                   126

         S - Sanger        Phred+33,  raw reads typically (0, 40)
         X - Solexa        Solexa+64, raw reads typically (-5, 40)
         I - Illumina 1.3+ Phred+64,  raw reads typically (0, 40)
         J - Illumina 1.5+ Phred+64,  raw reads typically (3, 40)
            with 0=unused, 1=unused, 2=Read Segment Quality Control Indicator (bold) 
            (Note: See discussion above).
         L - Illumina 1.8+ Phred+33,  raw reads typically (0, 40) 
     */

    public static VERSION guessQualityEncoding(char lowest) throws Exception {
        if (lowest < 33) {
            throw new Exception("Unknown quality score encoding: found character < 33");
        } else if (lowest > 126) {
            throw new Exception("Unknown quality score encoding: found character > 126");
        } else if (lowest < 59) {
            return VERSION.ILLUMINA_1_8; // or Sanger phred both use the same chars
        } else if (lowest < 64) {
            // quality scores need to be converted from solexa to phred
            return VERSION.SOLEXA;
        } else if (lowest < 66) {
            return VERSION.ILLUMINA_1_3;
        } else {
            
            // there's always the possibility that the input has already been
            // quality clipped - this would lead to higher min scores that cannot
            // be assigned clearly...
            return VERSION.ILLUMINA_1_5;
        }
    }
    
    
    // stolen from FastQC
    public static double convertSangerPhredToProbability (int phred) {
		return Math.pow(10,phred/-10d);
    }

    public static double convertOldIlluminaPhredToProbability (int phred) {
            return Math.pow(10, ((double)phred/(phred+1))/-10d);
    }

    public static int convertProbabilityToSangerPhred (double p) {
            return (int)Math.round(-10d*Math.log10(p));
    }

    public static int convertProbabilityToOldIlluminaPhred (double p) {
            return (int)Math.round(-10d*Math.log10(p/1-p));
    }
    
}
