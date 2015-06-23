/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.annotation;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.misc.RobinUtilities;
import de.rzpd.mapman.data.Bin;
import de.rzpd.mapman.data.BinItem;
import de.rzpd.mapman.data.MappingProvider;
import de.rzpd.mapman.data.model.DataType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

;

/**
 *
 * @author marc
 */
public class ResultAnnotator {

    private MappingProvider mapping;
    private File resultTableFile;
    private ConcurrentHashMap<String, AnnotationItem> mapHash;
    private double percentNotMapped, percentMapped;
    private int lineCounter, notMappedCounter, mappedCounter;


    public ResultAnnotator(File results, MappingProvider map) {
        this.mapping = map;
        this.resultTableFile = results;
        prepareMapHash();
        try {
            processResults();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void processResults() throws IOException {
        BufferedReader rd;
        String line;
        lineCounter = 0;
        notMappedCounter = 0;
        mappedCounter = 0;
        String sepChar = "\\t";
        FileWriter writer = new FileWriter(new File(resultTableFile.getCanonicalPath() + ".annotated.txt"));

        try {
            Charset encoding = Utilities.detectEncodingFromFile(resultTableFile);
            InputStreamReader reader=new InputStreamReader(new FileInputStream(resultTableFile), encoding);
            rd = new BufferedReader(reader);
            

            while ((line =  rd.readLine()  ) != null) {                
                String[] cols = line.split(sepChar);
                String id = cols[0].toLowerCase();


                if (id.matches("(?i)^Identifier")) {
                    writer.write(insertAnnotation(cols, "Bins", "Description"));
                    continue;
                }
                lineCounter++;

                AnnotationItem anno = mapHash.get(id);
                if (anno == null) {
                    notMappedCounter++;
                    writer.write(insertAnnotation(cols, "none", "no decription"));
                } else {
                    mappedCounter++;
                    writer.write(insertAnnotation(cols, anno.getBinsAsString(), anno.getDescription()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.close();
        percentNotMapped = ((double)notMappedCounter / (double)lineCounter) *100;       
        percentMapped = ((double)mappedCounter / (double)lineCounter) *100;
    }

    private String insertAnnotation(String[] line, String binAnno, String descAnno) {
        Vector<String> annoline = new Vector<String>();
        annoline.add(line[0]);
        annoline.add(binAnno);
        annoline.add(descAnno);
        for (int i = 1; i <= line.length-1; i++) {
            annoline.add(line[i]);
        }

        String retval = StringUtils.join(annoline, "\t") + "\n";
        return retval;
    }
    
    private void prepareMapHash() {
         mapHash = new ConcurrentHashMap<String, AnnotationItem>();

         // walk through all bins
         for (Bin bin : mapping.getRootBin().getDescendants()) {
            for (BinItem item : bin.getBinItems(DataType.TYPE_UNKNOWN)) {
                if (mapHash.containsKey(item.getIdentifier())) {
                    mapHash.get(item.getIdentifier()).addBin(bin);
                } else {
                    AnnotationItem annoItem = new AnnotationItem();
                    annoItem.setItem(item);
                    annoItem.addBin(bin);
                    mapHash.put(item.getIdentifier().toLowerCase(), annoItem);
                }
            }
        }        
    }

    public String getPercentNotMappedFormatted() {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(3);

        return nf.format(percentNotMapped);
    }

    public String getPercentMappedFormatted() {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(3);

        return nf.format(percentMapped);
    }

    public double getPercentMapped() {
        return percentMapped;
    }

    public double getPercentNotMapped() {
        return percentNotMapped;
    }
    
    public int getLineCounter() {
        return lineCounter;
    }

    public int getMappedCounter() {
        return mappedCounter;
    }

    public int getNotMappedCounter() {
        return notMappedCounter;
    }

}
