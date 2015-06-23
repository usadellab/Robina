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
import java.util.HashMap;
import java.util.Iterator;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class GFF3Utils {

    /*
     * Takes a feature list that was created using a GFF3Reader
     * assumes that it contains only exons and sums up the exon model
     * length for each transcript model based on the name
     */
    public static void writeLengthTableExonbased(FeatureList flist, File outFile) {
        HashMap<String, Integer> exonModelLengths = new HashMap<String, Integer>();        
        Iterator<FeatureI> iter = flist.selectByType("exon").iterator();

        while (iter.hasNext()) {
            FeatureI feature = iter.next();
            String name = (feature.hasAttribute("Parent") ? feature.getAttribute("Parent") : null);

            if (!exonModelLengths.containsKey(name)) {
                exonModelLengths.put(name, feature.location().length());
            } else {
                exonModelLengths.put(name, exonModelLengths.get(name) + feature.location().length());
            }
        }

        // write them to file for this chromosome
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(outFile));
            for (String gene : exonModelLengths.keySet()) {
                writer.write(gene + "\t" + exonModelLengths.get(gene) + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }
    
    public static void writeLengthTableGenebased(FeatureList flist, File outFile) {
        HashMap<String, Integer> geneModelLengths = new HashMap<String, Integer>();        
        Iterator<FeatureI> iter = flist.selectByType("gene").iterator();
        
        
        while (iter.hasNext()) {
            FeatureI feature = iter.next();
            String name = (feature.hasAttribute("ID") ? feature.getAttribute("ID") : null);

            if (!geneModelLengths.containsKey(name)) {
                geneModelLengths.put(name, feature.location().length());
            } else {
                geneModelLengths.put(name, geneModelLengths.get(name) + feature.location().length());
            }
        }

        // write them to file for this chromosome
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(outFile));
            for (String gene : geneModelLengths.keySet()) {
                writer.write(gene + "\t" + geneModelLengths.get(gene) + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
    }

    public static void writeLengthTableExonbased(GFF3AnnotationProvider annotation, File file) {
        writeLengthTableExonbased(annotation.getFeatureList(), file);
    }
}
