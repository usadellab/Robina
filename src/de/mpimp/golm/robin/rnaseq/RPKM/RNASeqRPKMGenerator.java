/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.RPKM;

import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.robin.GUI.RNASeq.RNASeqWorkflowPanel;
import de.mpimp.golm.robin.GUI.RobinMainGUI;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import de.mpimp.golm.robin.data.RNASeqSample;
import de.mpimp.golm.common.datastructures.SimpleFloatDataFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.biojava3.core.sequence.location.SimpleLocation;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.Location;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqRPKMGenerator {
    
    private FeatureList annotation;
    private RNASeqDataModel dataModel;
    private HashMap<String, GeneCounts> geneCounts = new HashMap<String, GeneCounts>();
    private HashMap<String, Integer> exonLengthsTable = null;    
    private RobinMainGUI mainGUI; 
    
    public RNASeqRPKMGenerator(RobinMainGUI main, FeatureList annotation, RNASeqDataModel dataModel) {
        this.annotation = annotation;
        this.dataModel = dataModel;
        this.mainGUI = main;
    }

    /**
     * compute RPKM for each column in a counts table
     */
    public void computeAmbiguousRPKMCountsTable() throws FileNotFoundException, IOException, Exception {
        
        SimpleFloatDataFrame rpkmTable = new SimpleFloatDataFrame();
        
        for (RNASeqSample sample : dataModel.getSamples().values()) {
            File uniqFile = new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_" + sample.getSampleName() + "_unique_reads.txt");
            File ambFile = new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_" + sample.getSampleName() + "_ambiguous_reads.txt");
            computeAmbiguousRPKM(uniqFile, ambFile);
            HashMap<String, Float> column = new HashMap<String, Float>();
            for (GeneCounts gc : geneCounts.values()) {
                column.put(gc.getID(), gc.getRPKM());
            }
            rpkmTable.addColumn(column, sample.getSampleName());
            geneCounts = new HashMap<String, GeneCounts>();
        }
        
        File rpkmFile = new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_RPKM.txt");
        rpkmTable.writeToFile(rpkmFile);
    }

    /**
     * Compute the reads per kilobase of exon model per million mapped reads as an 
     * estimate of the expression level of the genes.
     */
    private void computeAmbiguousRPKM(File uniqFile, File ambFile) throws FileNotFoundException, IOException {
        if (exonLengthsTable == null) {
            try {
                if (annotation != null) {
                    generateLengthTable(annotation);
                } else {    
                    //File instPath = new File(mainGUI.getInstallPath());
                    //File indexDir = new File(instPath, "index");
                    //File lengthsFile = new File(indexDir, dataModel.getReferenceFile().getName() + "_" +dataModel.getReferenceType() + ".lengths");
                    File lengthsFile = new File( dataModel.getInputDir(), "gene.lengths");
                    generateLengthTable(lengthsFile);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        BufferedReader uniqReader = new BufferedReader(new FileReader(uniqFile));        
        BufferedReader ambReader = new BufferedReader(new FileReader(ambFile));
        long numberOfMappedReads = 0;

        // first, we need to record the uniquely mapping reads
        String line = null;
        while ((line = uniqReader.readLine()) != null) {
            String[] elements = line.split("\t");
            String geneID = elements[1];
            
            if (geneCounts.containsKey(geneID)) {
                geneCounts.get(geneID).incrementUniqueCounts(1);
            } else {
                geneCounts.put(geneID, new GeneCounts(geneID));
                geneCounts.get(geneID).incrementUniqueCounts(1);
            }
            numberOfMappedReads++;
        }

        // normalize by merged exon model lengths
        for (String ID : geneCounts.keySet()) {
            int rawCounts = geneCounts.get(ID).getUniqueCounts();
            geneCounts.get(ID).setUniqueRPKM(RPKM((float) rawCounts, exonLengthsTable.get(ID), numberOfMappedReads));
            //System.out.println(ID + "\t" + geneCounts.get(ID).getUniqueRPKM());
        }

        //DEBUG 
        //System.out.println("no of uniquely mappable reads: " + numberOfMappedReads);

        // now read the ambiduous reads - for each of them we partitition the count
        // weighted by the unique (half normalized) counts we got for each of the
        // genes that the read aligns to.
        line = "";
        String lastRead = "";
        ArrayList<GeneCounts> hitGenes = new ArrayList<GeneCounts>();
        
        while ((line = ambReader.readLine()) != null) {
            String[] elements = line.split("\t");
            String geneID = elements[1];
            
            if (elements[0].equals(lastRead)) {
                // collect genes hit by ambiguous read
                if (geneCounts.containsKey(elements[1])) {
                    hitGenes.add(geneCounts.get(elements[1]));
                } else {
                    GeneCounts g = new GeneCounts(elements[1]);
                    hitGenes.add(g);                    
                    geneCounts.put(elements[1], g);
                }                
            } else {
                // new chunk begins - process the old one
                processAmbiguousChunk(hitGenes);
                hitGenes.clear();                
                numberOfMappedReads++; // ambigously mapped reads are added now                
                if (geneCounts.containsKey(elements[1])) {
                    hitGenes.add(geneCounts.get(elements[1]));
                } else {
                    GeneCounts g = new GeneCounts(elements[1]);
                    hitGenes.add(g);                    
                    geneCounts.put(elements[1], g);
                }                
            }
            lastRead = elements[0];
        }

        //DEBUG
        //System.out.println("no of total mappable reads: " + numberOfMappedReads);
        computeRPKM(numberOfMappedReads);
    }
    
    private void processAmbiguousChunk(ArrayList<GeneCounts> hitGenes) {
        //DEBUG
        //System.out.println("----->processing chunk of " + hitGenes.size());
        
        int max = getMaxUniqueCounts(hitGenes);

        //DEBUG
        //System.out.println("----->max unique counts " + max);
        
        for (GeneCounts g : hitGenes) {
            
            float weight;
            if (max == 0) {
                weight = 1f / (float) hitGenes.size();
            } else {
                weight = (float) (g.getUniqueCounts() / max);
            }

            //DEBUG           
            //System.out.println(g.getID() + "\t weight: " + weight);            
            g.incrementAmbiguousCounts(weight);
        }        
    }

    /**
     * internal method to compute the RPKM based on the current state of 
     * geneCounts after adding the weighted ambiguous "counts"
     */
    private void computeRPKM(long mappedReads) {
        // normalize by merged exon model lengths
        for (String ID : geneCounts.keySet()) {
            float ambCounts = (float) geneCounts.get(ID).getUniqueCounts() + geneCounts.get(ID).getAmbiguousCounts();            
            geneCounts.get(ID).setRPKM(RPKM(ambCounts, exonLengthsTable.get(ID), mappedReads));
            //System.out.println(ID + "\t" + geneCounts.get(ID).getUniqueRPKM()+"\tamb: "+geneCounts.get(ID).getRPKM());
        }        
    }
    
    private int getMaxUniqueCounts(ArrayList<GeneCounts> hitGenes) {
        int max = 0;
        for (GeneCounts g : hitGenes) {
            if (g.getUniqueCounts() > max) {
                max = g.getUniqueCounts();
            }
        }
        return max;
    }
    
    private float RPKM(float counts, int exonLength, long totalMappedReads) {
        float rpkm = (counts * 1000000000f) / ((float) exonLength * (float) totalMappedReads);
        return rpkm;
    }

    /**
     * generate a table of gene lengths by building a merge of all exons of
     * all transcript isoforms of each locus. This will yield something along
     * the lines of representative transcript length as provided by TAIR. For 
     * this to work we need a GFF3 annotation that contains at least "gene", "mRNA"
     * and "exon" annotated with consistent usage of the "ID" or "Name" and "Parent"
     * fields.
     */
    public void generateLengthTable(FeatureList anno) throws IOException {
        HashMap< String, FeatureI> mRNAs = new HashMap<String, FeatureI>();
        HashMap< String, ArrayList<Location>> lengthTable = new HashMap<String, ArrayList<Location>>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dataModel.getDetailedResultsDir(), dataModel.getExperimentName() + "_combinedExonModelLengths.txt")));
        exonLengthsTable = new HashMap<String, Integer>();
        
        Iterator<FeatureI> iter = anno.iterator();
        // initialize HashMap
        for (FeatureI f : anno) {
            if (f.hasAttribute("ID") && (RobinConstants.EXON_CONTAINING_FEATURES.contains(f.type().toLowerCase()))) {
                mRNAs.put(f.getAttribute("ID"), f);
            }
        }
        // collect all exons
        while (iter.hasNext()) {
            FeatureI feature = iter.next();
            if (feature.type().equals("exon")) {
                String parentID = feature.getAttribute("Parent");
                FeatureI parentTranscript = mRNAs.get(parentID);
                if (parentTranscript == null) {
                    //System.out.println("no mRNA parent found for exon <" + feature + ">");
                }
                String geneID = parentTranscript.getAttribute("Parent");
                
                if (!lengthTable.containsKey(parentTranscript.getAttribute("Parent"))) {
                    lengthTable.put(geneID, new ArrayList<Location>());
                }
                // Genomic locations implement compareTo - hence duplicates should be eliminated
                boolean added = lengthTable.get(geneID).add(feature.location().plus());
            }
        } // end while        

        for (String gene : lengthTable.keySet()) {
            //System.out.println(gene + "\texons in list " +  lengthTable.get(gene).size());            
            Iterator<Location> locIter = lengthTable.get(gene).iterator();
            Location first = locIter.next();
            long minstart = first.start();
            long maxend = first.end();
            
            while (locIter.hasNext()) {
                Location l = locIter.next();
                if (l.start() < minstart) {
                    minstart = l.start();
                }
                if (l.end() > maxend) {
                    maxend = l.end();
                }
            }
            
            int range = (int) (maxend - minstart);
            byte[] mergeMap = new byte[range + 1];
            
            for (Location loc : lengthTable.get(gene)) {
                int start = (int) (loc.start() - minstart);
                int end = (int) (loc.end() - minstart);
                
                for (int i = start; i < end; i++) {
                    mergeMap[i] = 1;
                }
            }
            int combinedExonLengths = 0;
            for (int j = 0; j < mergeMap.length; j++) {
                combinedExonLengths += mergeMap[j];
            }
            
            writer.write(gene + "\t" + combinedExonLengths + "\n");
            exonLengthsTable.put(gene, combinedExonLengths);
        }
        writer.close();
        annotation = null;
    }

    private void generateLengthTable(File lFile) throws FileNotFoundException, IOException {
        exonLengthsTable = new HashMap<String, Integer>();        
        SimpleLogger.getLogger(true).logMessage("reading length information from "+lFile.getCanonicalPath());        
        BufferedReader reader = new BufferedReader(new FileReader(lFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] elems = line.split("\t");
            exonLengthsTable.put(elems[0], Integer.parseInt(elems[1]));                    
        }
    }
    
    private class GeneCounts {
        
        private int uniqueCounts;
        private float ambiguousCounts;
        private float uniqueRPKM;
        private float RPKM;
        private String ID;
        
        public GeneCounts(String ID) {
            this.ID = ID;
            this.uniqueCounts = 0;
            this.ambiguousCounts = 0;
        }
        
        public void incrementUniqueCounts(int i) {
            uniqueCounts += i;
        }
        
        public void incrementAmbiguousCounts(float f) {
            ambiguousCounts += f;
        }
        
        public String getID() {
            return ID;
        }
        
        public float getRPKM() {
            return RPKM;
        }
        
        public void setRPKM(float RPKM) {
            this.RPKM = RPKM;
        }
        
        public int getUniqueCounts() {
            return uniqueCounts;
        }
        
        public float getAmbiguousCounts() {
            return ambiguousCounts;
        }
        
        public float getUniqueRPKM() {
            return uniqueRPKM;
        }
        
        public void setUniqueRPKM(float uniqueRPKM) {
            this.uniqueRPKM = uniqueRPKM;
        }
    }
}
