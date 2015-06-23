/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.qual;

import de.mpimp.golm.robin.rnaseq.parser.FastQEntry;
import de.mpimp.golm.robin.rnaseq.parser.FastQParser;
import de.mpimp.golm.robin.rnaseq.parser.FastQSampleParser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;

/**
 *
 * @author marc
 */
public class RNASeqBasicStats implements RNASeqQualityCheck {

    private long aCount, cCount, gCount, tCount, nCount, totalBases, totalEntries;
    private String aPerc, cPerc, gPerc, tPerc, nPerc;
    private HashMap<Integer, Integer> seqLengths;
    private boolean hasComputed = false;
    private FastQParser parser;
    private static final String ID = "QCbaseStats";

    public RNASeqBasicStats(FastQParser p) {
        aCount = 0;
        cCount = 0;
        gCount = 0;
        tCount = 0;
        totalBases = 0;
        totalEntries = 0;
        seqLengths = new HashMap<Integer, Integer>();
        parser = p;
    }

    public boolean hasTabularData() {
        return true;
    }

    public boolean processSeqEntry(FastQEntry entry) {

        String seq = entry.getSequence().toUpperCase();

        // count base composition the below is way faster than a loop and switch
        aCount += StringUtils.countMatches(seq, "A");
        cCount += StringUtils.countMatches(seq, "C");
        gCount += StringUtils.countMatches(seq, "G");
        tCount += StringUtils.countMatches(seq, "T");
        nCount += StringUtils.countMatches(seq, "N");
        totalBases += seq.length();

        // record lengths
        int length = seq.length();
        int currCount = (seqLengths.get(length) != null ? seqLengths.get(length) : 0);
        currCount++;
        seqLengths.put(length, currCount);

        totalEntries++;

        // what else?
        return true;
    }

    private void compute() {
//        NumberFormat nf = new DecimalFormat("##.#");  

        aPerc = String.format(Locale.ENGLISH, "%2.1f", ((float) ((float) aCount / (float) totalBases) * 100));
        cPerc = String.format(Locale.ENGLISH, "%2.1f", ((float) ((float) cCount / (float) totalBases) * 100));
        gPerc = String.format(Locale.ENGLISH, "%2.1f", ((float) ((float) gCount / (float) totalBases) * 100));
        tPerc = String.format(Locale.ENGLISH, "%2.1f", ((float) ((float) tCount / (float) totalBases) * 100));
        nPerc = String.format(Locale.ENGLISH, "%2.1f", ((float) ((float) nCount / (float) totalBases) * 100));
        hasComputed = true;
    }

    public Object getReport() {
        for (Integer i : seqLengths.keySet()) {
            System.out.println("len= " + i + " count=" + seqLengths.get(i));
        }

        System.out.println("A: " + (float) ((float) aCount / totalBases) * 100);
        System.out.println("C: " + (float) ((float) cCount / totalBases) * 100);
        System.out.println("G: " + (float) ((float) gCount / totalBases) * 100);
        System.out.println("T: " + (float) ((float) tCount / totalBases) * 100);
        System.out.println("N: " + (float) ((float) nCount / totalBases) * 100);

        return null;
    }

    public ChartPanel getResultChart(Dimension size) {
        ChartPanel retPan = new ChartPanel(new JFreeChart(new Plot() {

            @Override
            public String getPlotType() {
                return "";
            }

            @Override
            public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
            }
        }));
        
        retPan.setBackground(Color.WHITE);
        return retPan;
    }

    public String getDescription() {
        if (!hasComputed) {
            compute();
        }

        return getHTMLTable();
    }
    
    public String getHTMLTable() {
        
        String star = "";
        if (parser instanceof FastQSampleParser) {
            star = "*";
        }
        
        StringBuilder desc = new StringBuilder();
        String font = "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #666666; \">";
        String fontBlack = "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #000000; \">";
        String fontRed = "<span style=\"font-family: sans-serif; font-weight: bold; font-size: medium; color: #FF0000; \">";

        String nFont = font;
        if (Float.parseFloat(nPerc) >= 1) {
            nFont = fontRed;
        }

        // build a HTML table of the basic stats
        desc.append("<table border=\"0\" width=\"80%\" frame=\"hsides\" align=\"center\" >");
        desc.append("<tr bgcolor=\"#D6E8FF\" fgcolor=\"black\">");
        desc.append("   <th>").append(fontBlack).append("Number of reads</span></th>");
        desc.append("   <th>").append(fontBlack).append("Nucleotide composition</span></th>");
        desc.append("</tr>");


        desc.append("<tr align=\"center\" bgcolor=\"white\" >");
        desc.append("   <td>").append(font).append("reads: ").append(parser.getNumberOfEntries()).append(star).append("<br>bases read: ").append(this.totalBases).append(star).append("</span></td>");
        desc.append("   <td>").append(font).append("A: ").append(aPerc).append("% C: ").append(cPerc).append("%<br>G: ").append(gPerc).append("% T: ").append(tPerc).append("%<br>N: ").append(nFont).append(nPerc).append("%</span></td>");
        desc.append("</tr>");

        if (parser instanceof FastQSampleParser) {
            desc.append(font).append("<tr><small>* The number of entries was estimated from a small sample of<br>"
                    + "reads comrising the amount of bases indicated</small></span></tr>");
        }
        desc.append("</table>");
        desc.append("<br><hr width=\"80%\" >");
        return desc.toString();
    }
    
    public JPanel getResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setName(ID);
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(this.getDescription());        
        panel.add(pane);
        return panel;
    }

    public String getDescriptionText() {
        return "";
    }

}
