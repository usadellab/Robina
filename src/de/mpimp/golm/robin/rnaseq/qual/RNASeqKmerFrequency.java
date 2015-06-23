/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.GUI.RNASeq.qualcheck.RNASeqQCResultPanel;
import de.mpimp.golm.robin.GUI.plots.LinePlot;
import de.mpimp.golm.robin.misc.Seq4Packing.PaganUtilException;
import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.distribution.PoissonDistributionImpl;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Exceptions;

/**
 *
 * @author marc
 */
public class RNASeqKmerFrequency implements RNASeqQualityCheck {

    private HashMap<Kmer, Kmer> kmers;
    private int MIN_KMER_LENGTH = 5;
    private int MAX_KMER_LENGTH = 7; // 
    private int MAX_NUMBER_KMERS = 1000000; // @xmx1024M this number is OK...
    private static final int HEAD_MOST_ENRICHED = 10;
    // enrichment factors become larger with longer sequences since these are generally less likely.
    // when using a global cut off the short oligos will not turn up as enriched...
    private static final int MIN_ENRICHMENT_FACTOR = 3;
    private boolean hasComputed = false;
    private long aCount, cCount, gCount, tCount;
    private long entriesRead = 0;
    private long totalKmerCounts = 0;
    private long[] totalKmersAtPos;
    private ArrayList<Kmer> enrichedKmers;
    private static final String ID = "QCkmerFreqs";

    public RNASeqKmerFrequency(int min, int max, int max_unique) {
        kmers = new HashMap<Kmer, Kmer>();
        enrichedKmers = new ArrayList<Kmer>();
        totalKmersAtPos = new long[0];
        aCount = 0;
        cCount = 0;
        gCount = 0;
        tCount = 0;
        MIN_KMER_LENGTH = min;
        MAX_KMER_LENGTH = max;
        MAX_NUMBER_KMERS = max_unique;
    }

    public boolean processSeqEntry(FastQEntry entry) {

        if (kmers.keySet().size() >= MAX_NUMBER_KMERS) {
            return true;
        }

        String seq = entry.getSequence().toUpperCase();

        aCount += StringUtils.countMatches(seq, "A");
        cCount += StringUtils.countMatches(seq, "C");
        gCount += StringUtils.countMatches(seq, "G");
        tCount += StringUtils.countMatches(seq, "T");

        for (int i = MIN_KMER_LENGTH; i <= MAX_KMER_LENGTH; i++) {
            for (int j = 0; j < seq.length() - i; j++) {

                String kmerSeq = seq.substring(j, j + i);
                Kmer k = null;
                try {
                    k = new Kmer(kmerSeq);
                } catch (PaganUtilException ex) {
                    Exceptions.printStackTrace(ex);
                    return false;
                }

                if (kmers.containsKey(k)) {
                    kmers.get(k).incrementCountAtPos(j);
                    totalKmerCounts++;
                    this.incrementTotalCountAtPos(j);
                } else {
                    k.incrementCountAtPos(j);
                    kmers.put(k, k);
                    totalKmerCounts++;
                    this.incrementTotalCountAtPos(j);
                }
            }
        }
        entriesRead++;
        return true;
    }

    private void incrementTotalCountAtPos(int pos) {
        if (pos >= totalKmersAtPos.length) {
            long[] newtotalKmersAtPos = new long[pos + 1];
            System.arraycopy(totalKmersAtPos, 0, newtotalKmersAtPos, 0, totalKmersAtPos.length);
            totalKmersAtPos = newtotalKmersAtPos;
        }
        totalKmersAtPos[pos]++;
    }

    private void computeEnrichedKmers() {
        // first we need to work out how often we would have expected to 
        // se each kmer at each position. This is biased by the general
        // nucleotide composition of the sampled sequences
//        System.out.println("total entries analysed "+entriesRead);

        long totalCount = aCount + cCount + gCount + cCount;
        float aFrac = ((float) aCount) / totalCount;
        float cFrac = ((float) cCount) / totalCount;
        float gFrac = ((float) gCount) / totalCount;
        float tFrac = ((float) tCount) / totalCount;
//        float nFrac = ((float)nCount) / totalCount;
        float probability;
        for (Kmer k : kmers.keySet()) {
            try {
                // we wouldn't expect any position to be more likely for any Kmer
                probability = 1;
                for (char s : k.getSequence().toUpperCase().toCharArray()) {
                    switch (s) {
                        case 'A':
                            probability *= aFrac;
                            break;
                        case 'C':
                            probability *= cFrac;
                            break;
                        case 'G':
                            probability *= gFrac;
                            break;
                        case 'T':
                            probability *= tFrac;
                            break;
                        //                    case 'N' :
                        //                        probability *= nFrac;
                        //                        break; 
                    }
                }

                float expected = probability * totalKmerCounts;

                k.setExpectedFreq(expected);
                k.setProbability(probability);
                k.setEnrichment(k.getTotalCount() / expected);

                // this should give me the probability for observing a value like k.totalCount or greater
//                PoissonDistributionImpl poisson = new PoissonDistributionImpl(k.getExpectedFreq());                  
//                double obsProb = poisson.cumulativeProbability(k.getTotalCount(), Float.POSITIVE_INFINITY);
                //                System.out.println("Kmer="+k.getSequence()+ " count="+k.getTotalCount()+" expected="+k.getExpectedFreq()+" enrichment="+k.getEnrichment()+" probaility="+obsProb);

                // compute the positional enrichment
                float[] positionalEnrichments = new float[k.getPositions().length];
                float maxEnr = 0;
                for (int i = 0; i < positionalEnrichments.length; i++) {
                    float expectedAtPos = k.getProbability() * totalKmersAtPos[i];
                    long myKmerAtPos = k.getPositions()[i];
                    positionalEnrichments[i] = (float) myKmerAtPos / expectedAtPos;
                    if (positionalEnrichments[i] > maxEnr) {
                        maxEnr = positionalEnrichments[i];
                    }
                    //                    System.out.println("\tcount at pos "+i+" ="+myKmerAtPos+" expected="+expectedAtPos+" enriched="+positionalEnrichments[i]);

                }
                k.setEnrichmentAtPos(positionalEnrichments);
                k.setMaximalPositionalEnrichment(maxEnr);

                if (k.getMaximalPositionalEnrichment() > MIN_ENRICHMENT_FACTOR) {
                    enrichedKmers.add(k);
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Kmer[] sortedKmers = enrichedKmers.toArray(new Kmer[0]);
        Arrays.sort(sortedKmers);
        int c = 0;
        enrichedKmers = new ArrayList<Kmer>();
        for (Kmer k : sortedKmers) {
            if (c >= HEAD_MOST_ENRICHED) {
                break;
            }
            enrichedKmers.add(k);
            c++;
        }
        hasComputed = true;
    }

    public Object getReport() {
        if (!hasComputed) {
            computeEnrichedKmers();
        }
        return null;
    }

    public KmerTableModel getTableData() {
        return new KmerTableModel(enrichedKmers);
    }

    public ChartPanel getResultChart(Dimension size) {
        if (!hasComputed) {
            computeEnrichedKmers();
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        float max = 0, min = 100000;

        for (Kmer k : enrichedKmers) {
            XYSeries dataSeries = new XYSeries(k.getSequence());
            for (int i = 0; i < k.getEnrichmentAtPos().length; i++) {

                if (k.getEnrichmentAtPos()[i] < min) {
                    min = k.getEnrichmentAtPos()[i];
                }
                if (k.getEnrichmentAtPos()[i] > max) {
                    max = k.getEnrichmentAtPos()[i];
                }

                dataSeries.add(i + 1, k.getEnrichmentAtPos()[i]);
            }
            dataset.addSeries(dataSeries);
        }

        if (max < min) {
            max = 1;
            min = 0;
        }

        LinePlot plot = new LinePlot("Kmer enrichment", "Base position", "Enrichment factor", 0, min, 0, max, true);
        plot.setDataset(dataset);
        plot.setLineWidth(2f);
        return plot.getChartPanel(size);
    }

    public ArrayList<Kmer> getEnrichedKmers() {
        if (!hasComputed) {
            computeEnrichedKmers();
        }
        return enrichedKmers;
    }

    public int getTotalRecordedUniqueKmers() {
        return kmers.keySet().size();
    }

    public long getTotalKmerCounts() {
        return totalKmerCounts;
    }

    public boolean hasTabularData() {
        return true;
    }

    public String getDescription() {
        
        StringBuilder desc = new StringBuilder();
        String font = "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #666666; \">";
        String fontBlack = "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #000000; \">";
        
        desc.append("The Kmer frequency check identifies short sequences between " + MIN_KMER_LENGTH + " and " + MAX_KMER_LENGTH + ""
                + " nucleotides that occur more often than expected. The computation is based on"
                + " the probablilty to observe a certain sequence given the probabilities of the "
                + "occurrence of each nucleotide. N base calls are excluded. The plot shows the overenrichment"
                + " of frequent Kmer sequences per cycle (i.e. position in the read). Up to 10 Kmers that occur 3 times more often"
                + " than expected are indicated.<br>"
                + "In case unclipped, barcoded reads from a multiplexed sequencing run were used as input"
                + " the barcodes are likely to be detected as overenriched Kmers at the start of the reads.<br><br><br>");
        
        // build a HTML table of the Kmers
        desc.append("<table border=\"0\" width=\"80%\" frame=\"hsides\" align=\"center\" >");
        desc.append("<tr bgcolor=\"#D6E8FF\" fgcolor=\"black\">");
        desc.append("   <th>"+fontBlack+"Kmer</span></th>");
        desc.append("   <th>"+fontBlack+"Exp. freq. (global)</span></th>");
        desc.append("   <th>"+fontBlack+"Obs. freq. (global)</span></th>");
        desc.append("   <th>"+fontBlack+"Max. positional Enrichment.</span></th>");            
        desc.append("</tr>");
        for (Kmer k : this.getEnrichedKmers()) {
            desc.append("<tr align=\"center\" bgcolor=\"white\" >");
            desc.append("   <td>"+font+k.getSequence()+"</span></td>");
            desc.append("   <td>"+font+k.getExpectedFreq()+"</span></td>");
            desc.append("   <td>"+font+k.getTotalCount()+"</span></td>");
            desc.append("   <td>"+font+k.getMaximalPositionalEnrichment()+"</span></td>");                 
            desc.append("</tr>");
        }
        desc.append("</table>");
        
        return desc.toString();
        
    }

    public JPanel getResultPanel() {

        if (this.getEnrichedKmers().size() > 0) {
            RNASeqQCResultPanel qc_view = new RNASeqQCResultPanel(this.getDescription(), ID, this.getResultChart(null));
            return qc_view;
        } else {
            JPanel qc_view = new JPanel();
            JLabel jLabel1;
            JSeparator jSeparator1;
            jLabel1 = new javax.swing.JLabel();
            jSeparator1 = new javax.swing.JSeparator();

            qc_view.setBackground(new java.awt.Color(255, 255, 255));

            jLabel1.setFont(jLabel1.getFont().deriveFont((jLabel1.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, jLabel1.getFont().getSize() + 3));
            jLabel1.setForeground(new java.awt.Color(102, 102, 102));
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel1.setText("No enriched Kmers detected"); 

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(qc_view);
            qc_view.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE).add(layout.createSequentialGroup().add(20, 20, 20).add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE).addContainerGap()));
            layout.setVerticalGroup(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
            return qc_view;
        }
    }
}
