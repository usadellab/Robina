/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.annotation.GFF3;

import de.mpimp.golm.common.logger.SimpleLogger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;

/**
 *
 * @author marc
 */
public class AnnotationMap {

    private HashMap<String, TreeSet<GenomicLocation>> annoMap;
    private HashMap<String, Integer> exonModelLengths;
    private ArrayList<String> filterType = null;
    private long noAnnoFound = 0;

    public AnnotationMap(File annoFile) throws IOException {
        this.annoMap = new HashMap<String, TreeSet<GenomicLocation>>();
        populateFromGFF3(annoFile);
    }

    public AnnotationMap(File gFF3annotationFile, ArrayList<String> string) throws IOException {
        this.annoMap = new HashMap<String, TreeSet<GenomicLocation>>();
        this.setFilterType(string);
        populateFromGFF3(gFF3annotationFile);
    }

    public ArrayList<String> getFilterType() {
        return filterType;
    }

    private void setFilterType(ArrayList<String> filterType) {
        this.filterType = filterType;
    }

    private void populateFromGFF3(File anno) throws IOException {
        exonModelLengths = new HashMap<String, Integer>();
        GFF3Reader reader = new GFF3Reader();

        FeatureList featList;
        featList = reader.read(anno.getCanonicalPath());

        Iterator iter = featList.iterator();

        // walk through features annotated in the GFF3 file and fill 
        // our data structure...
        int fcounter = 0, nullname = 0, notAdded = 0, reccounter = 0;
        while (iter.hasNext()) {
            FeatureI feature = (FeatureI) iter.next();
            fcounter++;

            // should not be necessary - however the number of gene features in TAIR10.gff
            // is higher than the number that the biojava parser returns... AAAAAAAHRG
            if (!filterType.contains(feature.type())) {
                continue;
            }

            reccounter++;
            String chromosome = feature.seqname();
            if (annoMap.get(chromosome) == null) {
                annoMap.put(chromosome, new TreeSet<GenomicLocation>());
            }

            // that's a bit awkward as other GFF3 files might use a different
            // key to store the gene name. I should iterate over a range or
            // ask the user for the key (assuming he/she knows it...)
            //String name = (feature.hasAttribute("ID") ? feature.getAttribute("ID") : null);
            String name = (feature.hasAttribute("Parent") ? feature.getAttribute("Parent") : null);

            if (name == null) {
                SimpleLogger.getLogger(true).logMessage("null ID feature!! :" + feature);
                nullname++;

                if (nullname > 250 && fcounter == 500) {
                    System.out.println("more than 50% (" + nullname + "/" + fcounter + ") of the entries didn't have a ID tag attached.");
                    System.exit(1);
                }
            }

            if (!exonModelLengths.containsKey(name)) {
                exonModelLengths.put(name, feature.location().length());
            } else {
                exonModelLengths.put(name, exonModelLengths.get(name) + feature.location().length());
            }

            boolean added = annoMap.get(chromosome).add(new GenomicLocation(name, feature.location().plus()));
            if (!added) {
                notAdded++;
                //System.out.println("feature not added:"+name+" "+feature);
            }
        }
        //DEBUG
        System.out.println("done");
        System.out.println("processed " + fcounter + " features total");
        //System.out.println("retained "+reccounter+" gene features");
        System.out.println("saved " + size() + " gene features in map");
        System.out.println("enztries not added because they equalled already added ones " + notAdded + " features total");
        System.out.println("encountered " + nullname + " features without ID tag");
    }

    public int size() {
        int s = 0;
        s += annoMap.size();
        for (String c : annoMap.keySet()) {
            s += annoMap.get(c).size();
        }
        return s;
    }

    public synchronized ArrayList<GenomicLocation> getLocationsOverlappingWith(String chromosome, GenomicLocation loc) {

        ArrayList<GenomicLocation> results = new ArrayList<GenomicLocation>();

        GenomicLocation floor = null, ceiling = null;
        try {
            floor = annoMap.get(chromosome).floor(loc);
        } catch (NullPointerException ne) {
            //SimpleLogger.getLogger(true).logMessage("Annotation failure: No floor found for " + chromosome + " " + loc);
        }

        try {
            ceiling = annoMap.get(chromosome).ceiling(loc);
        } catch (NullPointerException ne) {
            //SimpleLogger.getLogger(true).logMessage("Annotation failure: No ceiling found for " + chromosome + " " + loc);
        }

        // both null?
        if ((floor == null) && (ceiling == null)) {
            SimpleLogger.getLogger(true).logMessage("no mapping results found for " + chromosome + " " + loc);   
            noAnnoFound++;
            return results;
        }

        // DEBUG
        if ((floor == null) || (ceiling == null)) {
            //SimpleLogger.getLogger(true).logMessage("either floor or ceiling is null for " + chromosome + " " + loc);
            if (floor == null) {
                results.add(ceiling);
            } else {
                results.add(floor);
            }
            return results;
        }

//        //DEBUG
//        System.out.println("floor on "+chromosome+" "+loc+" = "+floor.getIdentifier()+" "+floor);
//        System.out.println("ceil on "+chromosome+" "+loc+" = "+ceiling.getIdentifier()+" "+ceiling );

        // found only one?
        if (floor.equals(ceiling)) {
            results.add(floor);
            return results;
        }

        for (Object r : annoMap.get(chromosome).subSet(floor, true, ceiling, true)) {
            if (r instanceof GenomicLocation) {
                GenomicLocation l = (GenomicLocation) r;
                if (l.overlaps(loc)) {
                    results.add(l);
                }
            }
        }

        // DEBUG
        System.out.println("got " + results.size() + " results");
        return results;
    }

    public void writeLengthTable(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        // write them to file for this chromosome
        for (String gene : exonModelLengths.keySet()) {
            writer.write(gene + "\t" + exonModelLengths.get(gene) + "\n");
        }

        writer.close();
    }
    
    public long getNumberUnannotated() {
        return noAnnoFound;
    }
}
