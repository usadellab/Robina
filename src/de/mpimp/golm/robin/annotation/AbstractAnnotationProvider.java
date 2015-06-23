/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.annotation;

import de.mpimp.golm.common.datastructures.intervaltree.IntervalTree;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.Location;

/**
 *
 * @author marc
 */
public abstract class AbstractAnnotationProvider {
    
    protected File AnnoFile;
    protected List<String> featureTypes;
    protected HashMap<String, IntervalTree<FeatureI> > annotationTree;
    
    
    public List<FeatureI> getOverlappingFeatures(String chromosome, Location queryLoc) {           
        List<FeatureI> empty = new ArrayList<FeatureI>();        
        if (!annotationTree.containsKey(chromosome)) {
            return empty;
        }        
        List<FeatureI> overlap = annotationTree.get(chromosome).get(queryLoc.plus().start(), queryLoc.plus().end());           
        if (overlap == null) {
            return empty;
        }        
        return overlap;
    }
    
    public void addFeatures(FeatureList list) {
        if (annotationTree == null) {
            annotationTree = new HashMap<String, IntervalTree<FeatureI>>();
        }
        
        for (FeatureI feature : list) {
            if ((featureTypes != null)
                    && (!featureTypes.contains(feature.type()))) { // should this be case sensitive or not?
                continue;
            }
            
            String chromosome = feature.seqname();
            
            if (!annotationTree.containsKey(chromosome)) {
                annotationTree.put(chromosome, new IntervalTree<FeatureI>());
            } 
            long start, end;
            
            start = feature.location().plus().start();
            end = feature.location().plus().end();
            
            annotationTree.get(chromosome).addInterval(start, end, feature);
        }
        
        // build all trees once all features are added
        for (IntervalTree tree : annotationTree.values()) {
            tree.build();
        }
    }
    
    public FeatureList getFeatureList() {
        FeatureList list = new FeatureList();
        
        for (String c : annotationTree.keySet()) {
            list.add(annotationTree.get(c).getAll());
        }
        return list;
    }
    
    protected abstract void readAnnotation() throws IOException;
}
