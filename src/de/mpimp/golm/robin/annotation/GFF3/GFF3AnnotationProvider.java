/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.annotation.GFF3;

import de.mpimp.golm.common.datastructures.intervaltree.IntervalTree;
import de.mpimp.golm.robin.annotation.AbstractAnnotationProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.biojava3.genome.parsers.gff.Location;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class GFF3AnnotationProvider extends AbstractAnnotationProvider {

    public GFF3AnnotationProvider(File GFF3file) {
        this(GFF3file, null);
    }
    
    public GFF3AnnotationProvider() {        
    }

    public GFF3AnnotationProvider(File GFF3file, List<String> featureTypes) {

        this.AnnoFile = GFF3file;
        this.featureTypes = featureTypes;
        try {
            this.readAnnotation();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    protected void readAnnotation() throws IOException {
        Iterator<FeatureI> iter = GFF3Reader.read(AnnoFile.getCanonicalPath()).iterator();
        if (annotationTree == null) {
            annotationTree = new HashMap<String, IntervalTree<FeatureI>>();
        }

        // walk through features annotated in the GFF3 file and fill 
        // our data structure...
        int fcounter = 0, nullname = 0, notAdded = 0, reccounter = 0;
        while (iter.hasNext()) {
            final FeatureI feature = iter.next();

            if ((featureTypes != null)
                    && (!featureTypes.contains(feature.type()))) { // should this be case sensitive or not?
                continue;
            } else {
                fcounter++;
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
}
