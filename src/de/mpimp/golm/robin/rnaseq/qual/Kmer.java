/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.misc.Seq4Packing;
import de.mpimp.golm.robin.misc.Seq4Packing.PaganUtilException;
import java.util.Arrays;

/**
 *
 * @author marc
 */
public class Kmer implements Comparable {
        private byte[] sequence;
        private long[] positions = new long[0];
        private long totalCount;
        private float[] enrichmentAtPos = new float[0];
        private float expectedFreq;
        private float enrichment;
        private float maximalPositionalEnrichment;
        private float probability;
        private int length;

        public Kmer(String sequence) throws PaganUtilException {
            this.sequence = Seq4Packing.pack(sequence.toUpperCase().getBytes());
            this.totalCount = 0;
            this.length = sequence.length();
        }
        
        public void incrementCountAtPos(int pos) {
            totalCount++;
            if (pos >= positions.length) {
                long [] newPositions = new long[pos+1];
                System.arraycopy(positions, 0, newPositions, 0, positions.length);
                positions = newPositions;
            }
            positions[pos]++;
        }

        public float getEnrichment() {
            return enrichment;
        }

        public float getMaximalPositionalEnrichment() {
            return maximalPositionalEnrichment;
        }

        public void setMaximalPositionalEnrichment(float maximalPositionalEnrichment) {
            this.maximalPositionalEnrichment = maximalPositionalEnrichment;
        }
        
        public void setEnrichment(float enrichment) {
            this.enrichment = enrichment;
        }
        
        public long[] getPositions() {
            return positions;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
        
        public float[] getEnrichmentAtPos() {
            return enrichmentAtPos;
        }

        public void setEnrichmentAtPos(float[] enrichmentAtPos) {
            this.enrichmentAtPos = enrichmentAtPos;
        }

        public float getExpectedFreq() {
            return expectedFreq;
        }

        public void setExpectedFreq(float expectedFreq) {
            this.expectedFreq = expectedFreq;
        }

        public String getSequence() {
            return Seq4Packing.unpack(sequence);
        }
        
        public byte[] getByteSequence() {
            return sequence;
        }

        public void setSequence(String sequence) throws PaganUtilException {
            this.sequence = Seq4Packing.pack(sequence.toUpperCase().getBytes());
        }

        public long getTotalCount() {
            return totalCount;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 11 * hash + Arrays.hashCode(this.sequence);
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            boolean result = true;
            if ( Seq4Packing.getUnpackedLength(this.sequence) != Seq4Packing.getUnpackedLength( ((Kmer)o).getByteSequence() ) ) {
                return false;
            }
            
            for (int i = 0; i < sequence.length; i++) {
                if (this.sequence[i] != ((Kmer)o).getByteSequence()[i] ) {
                    result = false;
                }
            }
            return result;
        }       

        public int compareTo(Object o) {
            Kmer k = (Kmer)o;
            return Float.compare(k.getEnrichment(), this.getEnrichment());
        }

        public int getLength() {
            return length;
        }
       
    }