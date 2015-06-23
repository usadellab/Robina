/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.annotation.GFF3;

import java.util.Comparator;
import org.biojava3.genome.parsers.gff.Location;

/**
 *
 * @author marc
 */
public class GenomicLocation extends Location implements Comparable {
    
    private String identifier;
    
    // locations just overlapping one another will be 
    // compare equal as a default as long as the overlap
    // is at least minOverlap percent
    private boolean overlapEquals = false;
    private double minOverlap = 80.0D;

    public GenomicLocation(String id, Location other) {
        super(other);
        this.identifier = id;
    }

    public GenomicLocation(String id, int start, int end) {
        super(start, end);
        this.identifier = id;
    }

    public double getMinOverlap() {
        return minOverlap;
    }

    public void setMinOverlap(double minOverlap) {
        this.minOverlap = minOverlap;
    }

    public boolean isOverlapEquals() {
        return overlapEquals;
    }

    public void setOverlapEquals(boolean overlapEquals) {
        this.overlapEquals = overlapEquals;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public int compareTo(Object o) {
        if (o instanceof GenomicLocation ) {        
            GenomicLocation l1 = (GenomicLocation)((GenomicLocation) o).plus();      
            Location me = this.plus();
            
            if (l1.equals(me) || me.contains(l1)) {
                //System.out.println(this.getIdentifier()+this+" equals "+l1.getIdentifier()+l1);                
                
                // record same location different name locations ?!?
//                if ( ! this.getIdentifier().equals(l1.getIdentifier()) ) {
//                    return -1;
//                }                
                return 0;
            }            
            if (l1.overlaps(me) && overlapEquals) {
                if (l1.percentOverlap(me) >= minOverlap) return 0;     
                if (l1.percentOverlap(me) < minOverlap) {
                    if (l1.startsBefore(me) && l1.overlaps(me)) return 1;
                    if (l1.endsAfter(me) && l1.overlaps(me)) return -1;
                }
            } else if (l1.overlaps(me)) {
                if (l1.startsBefore(me) && l1.overlaps(me)) return 1;
                if (l1.endsAfter(me) && l1.overlaps(me)) return -1;
            } else {           
                if (l1.endsBefore(me)) return 1;
                if (l1.startsAfter(me)) return -1;
            }
        } else {
            // no genomic location
            return -1;
        }
        
        // you should never end up here.
        return 0;
    }
    
    
}


// public int compareTo(Object o) {
//        if (o instanceof GenomicLocation ) {        
//            GenomicLocation l1 = (GenomicLocation)((GenomicLocation) o).plus();      
//            Location me = this.plus();
//            
//            if (l1.equals(me) || me.contains(l1)) {
//                //System.out.println(this.getIdentifier()+this+" equals "+l1.getIdentifier()+l1);                
//                
//                // record same location different name locations ?!?
////                if ( ! this.getIdentifier().equals(l1.getIdentifier()) ) {
////                    return -1;
////                }                
//                return 0;
//            }            
//            if (l1.overlaps(me) && overlapEquals) {
//                if (l1.percentOverlap(me) >= minOverlap) return 0;     
//                if (l1.percentOverlap(me) < minOverlap) {
//                    if (l1.startsBefore(me) && l1.overlaps(me)) return 1;
//                    if (l1.endsAfter(me) && l1.overlaps(me)) return -1;
//                }
//            } else if (l1.overlaps(me)) {
//                if (l1.startsBefore(me) && l1.overlaps(me)) return 1;
//                if (l1.endsAfter(me) && l1.overlaps(me)) return -1;
//            } else {           
//                if (l1.endsBefore(me)) return 1;
//                if (l1.startsAfter(me)) return -1;
//            }
//        } else {
//            // no genomic location
//            return -1;
//        }
//        
//        // you should never end up here.
//        return 0;
//    }