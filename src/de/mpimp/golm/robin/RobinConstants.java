/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author marc
 */
public class RobinConstants {

    // colors used for highlighting list items
    public static final Color redInactive = new Color(255, 212, 212);
    public static final Color redActive = new Color(255, 148, 148);
    public static final Color LIGHT_BLUE = new Color(214, 232, 255);
    
    public static final int MAX_VALID_BOWTIE_ALIGNMENTS = 50;
    public static final int MAX_SEQUENCE_LENGTH = 1500;
    public static final int MAX_QUAL_SCORE = 42;
    public static final long INPUTCHECK_SCANDEPTH = 5000;
    public static final float PERCENTAGE_READS_ALIGNED_WARNING_THRESHOLD = 10f;    
    
    public static final int MAX_BAD_SAM_ENTRIES = 10;
    
    public static String lastDirChooserPath;
    
    // text attributes for editorpane text display
    public static final SimpleAttributeSet attrNormalGrayItalic = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.GRAY);
        }
    };
    public static final SimpleAttributeSet attrBoldRed = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.RED);
        }
    };
    public static final SimpleAttributeSet attrBoldGreen = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0, 102, 0));
        }
    };
    public static final SimpleAttributeSet attrBoldBlack = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.BLACK);
        }
    };
    public static final SimpleAttributeSet attrHeader1 = new SimpleAttributeSet() {
        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.BLACK);
            addAttribute(StyleConstants.CharacterConstants.Size, 16);
            addAttribute(StyleConstants.CharacterConstants.FontFamily, "SansSerif");
        }
    };
    
    public static final SimpleAttributeSet attrHeader2 = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.DARK_GRAY);
            addAttribute(StyleConstants.CharacterConstants.Size, 14);
            addAttribute(StyleConstants.CharacterConstants.FontFamily, "SansSerif");
        }
    };
    public static final SimpleAttributeSet attrNormal = new SimpleAttributeSet() {

        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.DARK_GRAY);
            addAttribute(StyleConstants.CharacterConstants.Size, 12);
            addAttribute(StyleConstants.CharacterConstants.FontFamily, "SansSerif");
            addAttribute(StyleConstants.CharacterConstants.LineSpacing, 1.5f);
        }
    };
    
    public static final SimpleAttributeSet attrNormalMonospaced = new SimpleAttributeSet() {
        {
            addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
            addAttribute(StyleConstants.CharacterConstants.Foreground, Color.DARK_GRAY);
            addAttribute(StyleConstants.CharacterConstants.Size, 12);
            addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
            addAttribute(StyleConstants.CharacterConstants.LineSpacing, 1.5f);

        }
    };
    public static final String[] STANDARD_R_EXTENSIONS = new String[]{
        "RGbox.R",
        "ellipse.R",
        "malowess.R",
        "modifiedAffyCoreTools.R",
        "plotSigDensities.R",
        "robinPlotMDS.dge.R",
        "robinVennDiagram.R",
        "info.R",
        "computeGenomicGCcontentTable.R"
    };
    
    public static final String[] EXTRACT_GFF3_FEATURES_LIST = new String[]{
        //"exon",
        "gene",
        //"CDS"
    };
    
    public static final ArrayList<String> EXON_CONTAINING_FEATURES = new ArrayList<String>() {
        {
            add("mrna");
            add("mirna");
            add("trna");
            add("ncrna");
            add("mrna_te_gene");
            add("snorna");
            add("snrna");
            add("rrna");
        }
    };
    
    
    public static final ArrayList<Integer> LEGAL_KEYCODES = new ArrayList<Integer>() {
        {
            for (int i = 65; i <= 90; i++) {
                add(i);
            }

            for (int i = 37; i <= 40; i++) {
                add(i);
            }

            // numbers - they are allowed if not used as the first character
            for (int i = 48; i <= 57; i++) {
                add(i);
            }
            add(127); // DEL
            add(10); // ENTER
            add(8); //BACKSPACE
            add(16); // SHIFT
            add(20); //SHIFT LOCK
            add(47); // _
            add(18); // alt
            add(17); // control
            add(157); // APPLE
            add(27); // ESC
        }
    };    
    
}
