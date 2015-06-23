/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RNASeqResultBrowser.java
 *
 * Created on 24.10.2011, 16:45:50
 */
package de.mpimp.golm.robin.GUI.RNASeq;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import de.mpimp.golm.common.logger.SimpleLogger;
import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.RobinConstants;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.ImageIcon;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author marc
 */
public class RNASeqResultBrowser extends javax.swing.JPanel {
    
    private RNASeqDataModel dataModel;
    private RNASeqWorkflowPanel mainPanel;
    private StyledDocument doc;

    /** Creates new form RNASeqResultBrowser */
    public RNASeqResultBrowser(RNASeqWorkflowPanel mainPane, RNASeqDataModel data) {
        initComponents();
        this.mainPanel = mainPane;
        this.dataModel = data;
        this.doc = (StyledDocument) browserEditorPane.getDocument();
        try {
            buildContent();            
            browserEditorPane.setCaretPosition(0);
        } catch (MalformedURLException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        } catch (BadLocationException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        } catch (FileNotFoundException ex) {
            SimpleLogger.getLogger(true).logException(ex);
        }
        
    }
    
    private void addStaticStyles() {
        Style style = doc.addStyle("tableParagraph", null);
        StyleConstants.setLeftIndent(style, 60f);
        StyleConstants.setRightIndent(style, 50f);
        StyleConstants.setSpaceAbove(style, 15f);
        StyleConstants.setSpaceBelow(style, 35f);
    }
    
    private void buildContent() throws MalformedURLException, BadLocationException, FileNotFoundException {
        addStaticStyles();
        
        doc.insertString(0, "\n\tResult overview for RNA-Seq analysis project:\n\t"
                + dataModel.getProjectDir().getName() + "\n\n", RobinConstants.attrHeader1);

        // add sections common to all methods
        addGeneralSummary();
        addMAplots();
        addVennDiagrams();

        // add specific plots and data
        switch (dataModel.getAnalysisType()) {
            case EDGER:
                addMDSPlot();
                break;
            case DESEQ:
                break;
            case LIMMA:
                break;
        }

        // add the top100 tables
        addTopTables();
        addReferences();
        attachSessionInfo();
        
        // scroll to top - all of the below does not work
        Dimension d = contentScrollPane.getPreferredSize();
        int x = d.width;
        browserEditorPane.setCaretPosition(0);
        browserEditorPane.scrollRectToVisible(new java.awt.Rectangle(0,0));//        
//        contentScrollPane.getViewport().setViewPosition(new Point(x,0));
        contentScrollPane.repaint();
    }
    
    private void addGeneralSummary() throws BadLocationException {
        doc.insertString(doc.getLength(), "\tSummary\n\n", RobinConstants.attrHeader2);

        /** 
         * list raw input files; files that passed the QC
         * raw reads / reads that passed the trimming (percentages + warning if many were removed)
         * 
         * samples defined and contrasts computed
         * 
         * analysis details: settings for the statistics (pvalues cutoff, multiple testing correction
         * method, general statistics method)
         * 
         * general warnings, like no sig genes and so on
         */
        
        if (dataModel.isImportCountsTable()) {
            doc.insertString(doc.getLength(),
                    "\tThe analysis was run using count data imported from a precomputed\n"
                    + "\tcounts table file: "+ dataModel.getImportCountsTableFile().getName()
                    + "\n\n", RobinConstants.attrNormal);
            
            doc.insertString(doc.getLength(),
                "\tBetween the samples defined in the imported counts table, the following contrasts\n"
                + "\thave been computed:\n\n", RobinConstants.attrNormal);
        } else {
            doc.insertString(doc.getLength(),
                    "\tThe analysis was run with a total of " + dataModel.getInputFiles().size()
                    + " input files that were organized in " + dataModel.getSamples().keySet().size() + " samples."
                    + "\n\n", RobinConstants.attrNormal);

            // insert a table giving raw file name, sample name, raw reads and reads that passed the QC/Trimmomatic
            addTable(new SampleTableModel(dataModel), "Sample table");
            
            doc.insertString(doc.getLength(),
                "\tBetween the samples defined in the table given above, the following contrasts\n"
                + "\thave been computed:\n\n", RobinConstants.attrNormal);
        }
        
        
        
        doc.insertString(doc.getLength(),
                "\t\t" + StringUtils.join(dataModel.getContrastTerms().replaceAll("\\s+", "").split(",\\n?"),"\n\t\t" ) + "\n\n", RobinConstants.attrNormalGrayItalic);
        
        doc.insertString(doc.getLength(),
                "\tThe normalization and statistical evaluation of differential gene expression has been\n"
                + "\tperformed using ", RobinConstants.attrNormal);
        
        
        switch (dataModel.getAnalysisType()) {
            case EDGER:                
                doc.insertString(doc.getLength(),
                        "edgeR (Robinson et al., 2010)", RobinConstants.attrNormal);
                break;
            case LIMMA:
                doc.insertString(doc.getLength(),
                        "limma (Smyth et al., 2004 and Robinson et al., 2010)", RobinConstants.attrNormal);
                break;
            case DESEQ:
                doc.insertString(doc.getLength(),
                        "DESeq (Anders and Huber, 2010)", RobinConstants.attrNormal);
                break;
        }
        
        doc.insertString(doc.getLength(),
                " with a p-value cut-off of "+dataModel.getPValCutoffValue()+" and using the\n"
                + "\t"+dataModel.getPValCorrectionMethodHuman()+" method for multiple testing correction."                
                + "\n\n", RobinConstants.attrNormal);
    }
    
    
    private void addTopTables() throws BadLocationException {
        
        doc.insertString(doc.getLength(), "\tTop 100 differentially expressed genes tables for each contrast\n\n", RobinConstants.attrHeader2);
        
        doc.insertString(doc.getLength(),
                "\tThe following section contains the full results for the top 100\n"
                + "\t(or less, if less genes had raw p-values below the threshold) differentially\n"
                + "\texpressed genes in each comparison. The tables are also saved as\n"
                + "\ttab-separated text files in the 'detailed_results' subfolder of\n"
                + "\tthe project folder."
                + "\n\n", RobinConstants.attrNormal);
        
        
        File[] topTables = dataModel.getDetailedResultsDir().listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.getName().toLowerCase().startsWith("full_table")) {
                    return true;
                }
                return false;
            }
        });
        
        for (File table : topTables) {
            addTable(new TopTableModel(table), 
                    "Top differentially expressed genes: " + table.getName());
        }
    }
    
    
    private void addTable(AbstractTableModel model, String headline) throws BadLocationException {           
        // construct the component
        JTable table = new JTable(model);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowHorizontalLines(true);
        table.setBackground(Color.WHITE);
        Dimension d = table.getPreferredSize();
        d.width = 300;        
        table.setPreferredScrollableViewportSize(d);
        
        // add the style
        Style style = doc.addStyle("table", null);
        StyleConstants.setComponent(style, new JScrollPane(table));
        
        doc.insertString(doc.getLength(), "\t" + headline + "\n", RobinConstants.attrNormalGrayItalic);
        
        // insert
        int start = doc.getLength();
        doc.insertString(start, "\n", doc.getStyle("table"));
        int end = doc.getLength();

        // apply the styling 
        style = doc.getStyle("tableParagraph");
        doc.setParagraphAttributes(start, end - start, style, false);
        style = doc.getStyle("table");
        doc.setCharacterAttributes(start, end - start, style, false);    
    }
    
    private void addMDSPlot() throws MalformedURLException, BadLocationException {
        
        if ( !(new File(dataModel.getPlotsDir(), "MDSplot.png").exists()) ) return;
        
        doc.insertString(doc.getLength(), "\tMulti-dimensional scaling (MDS) plot\n\n", RobinConstants.attrHeader2);
        
        doc.insertString(doc.getLength(),
                "\tThe MDS or principal coordinate plot visualizes the distances between the\n"
                + "\tRNA-Seq libraries in the experiment. To compute the points, a set of 500\n"
                + "\ttags(genes) that have the largest variation bewteen the libraries (i.e. the\n"
                + "\tlargest tagwise dispersion when treating all libraries as one experimental group)\n"
                + "\tis selected. The distance between each pair of libraries is equivalent to the\n"
                + "\tsquare root of the common disperion between these two libraries (using the top 500\n"
                + "\tgenes.\n"
                + "\tHence, the MDS plot gives an insight into the structure of the experiment - libraries\n"
                + "\tthat were generated on biological replicates of the same treatment should cluster\n"
                + "\ttogether"
                + "\n\n", RobinConstants.attrNormal);
        
        insertPlots("mdsplot");
    }
    
    private void addMAplots() throws BadLocationException, MalformedURLException {
        doc.insertString(doc.getLength(), "\tMA plots of each comparison\n\n", RobinConstants.attrHeader2);
        
        doc.insertString(doc.getLength(),
                "\tThe MA plots show the log2 fold change (M; logFC) plotted versus the average\n"
                + "\texpression strength (A; LogConc) for each of the comparisons that was computed.\n"
                + "\tUsually, these scatter plots show a trumpet-like shape which is attributed\n"
                + "\tto the fact that genes with a lower expression signal strength are more\n"
                + "\tstrongly affected by noise than strongly expressed genes.\n\n"
                + "\tAccording to the assumption that under most experimental conditions the\n"
                + "\tbulk of the genes of an organism are not responding differentially, the cloud\n"
                + "\tof points should be centered around a log fold change of 0.\n"
                + "\tGenes that were called significantly differentially expressed are shown in red.\n\n", RobinConstants.attrNormal);
        
        insertPlots("maplot");
    }
    
    private void addVennDiagrams() throws BadLocationException, MalformedURLException {
        doc.insertString(doc.getLength(), "\n\n\tVenn Diagrams\n\n", RobinConstants.attrHeader2);
        
        doc.insertString(doc.getLength(),
                "\tVenn diagrams visualize the amount of genes that were called significantly\n"
                + "\tdifferentially expressed in each comparison. The conditions are represented\n"
                + "\tby circles. Genes that show a significant reponse to more than one condition\n"
                + "\tare plotted in the overlapping areas while the amount of not significantly\n"
                + "\tchanged genes is given in the lower right corner of the plots.\n\n"
                + "\tVenn diagrams allow a simple and quick overview of the impact of the treatments\n"
                + "\ton the gene expression profile and also the specificity of the responses\n\n", RobinConstants.attrNormal);
        
        insertPlots("venn");
        
    }
    
    private void insertPlots(final String prefix) throws MalformedURLException, BadLocationException {
        // Insert per-comparison MA plots
        File[] plots = dataModel.getPlotsDir().listFiles(new FileFilter() {
            
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().startsWith(prefix);
            }
        });
        
        for (File plot : plots) {
            insertImage(plot);
        }
    }
    
    private void addReferences() throws BadLocationException {
        
        doc.insertString(doc.getLength(),
                "\tLiterature\n\n", RobinConstants.attrHeader1);
        
        switch (dataModel.getAnalysisType()) {
            case EDGER:                
                doc.insertString(doc.getLength(),
                        "\tRobinson MD, McCarthy DJ, Smyth GK (2010) edgeR: a Bioconductor\n"
                        + "\tpackage for differential expression analysis of digital gene\n"
                        + "\texpression data. ", RobinConstants.attrHeader2);
                doc.insertString(doc.getLength(),
                        "Bioinformatics 26: 139-140\n\n", RobinConstants.attrNormal);                
                break;
            
            case LIMMA:
                doc.insertString(doc.getLength(),
                        "\tSmyth GK (2004) Linear models and empirical bayes methods for\n"
                        + "\tassessing differential texpression in microarray experiments.\n", RobinConstants.attrHeader2);
                doc.insertString(doc.getLength(),
                        "\tStatistical applications in genetics and molecular\n"
                        + "\tbiology 3: Article3\n\n", RobinConstants.attrNormal);
                doc.insertString(doc.getLength(),
                        "\tRobinson MD, McCarthy DJ, Smyth GK (2010) edgeR: a Bioconductor\n"
                        + "\tpackage for differential expression analysis of digital gene\n"
                        + "\texpression data. ", RobinConstants.attrHeader2);
                doc.insertString(doc.getLength(),
                        "Bioinformatics 26: 139-140\n\n", RobinConstants.attrNormal);
                break;
            
            case DESEQ:
                doc.insertString(doc.getLength(),
                        "\tAnders S, Huber W (2010) Differential expression analysis for\n"
                        + "\tsequence count data. ", RobinConstants.attrHeader2);
                doc.insertString(doc.getLength(),
                        "Genome Biol 11: R106\n\n", RobinConstants.attrNormal);                
                break;
        }
        
        if (dataModel.getPValCorrectionMethod().equals("BH") || dataModel.getPValCorrectionMethod().equals("fdr") ) {
            doc.insertString(doc.getLength(),
                        "\tBenjamini, Y., and Hochberg, Y. (1995). Controlling the false \n"
                        + "\tdiscovery rate: a practical and powerful approach to multiple testing.\n"
                        , RobinConstants.attrHeader2);
            doc.insertString(doc.getLength(),
                        "\tJournal of the Royal Statistical Society Series B, 57, 289–300.\n", RobinConstants.attrNormal);
        } else if (dataModel.getPValCorrectionMethod().equals("BY")) {
            doc.insertString(doc.getLength(),
                        "\tBenjamini, Y., and Yekutieli, D. (2001). The control of the false discovery\n"
                        + "\trate in multiple testing under dependency.\n"                        
                        , RobinConstants.attrHeader2);
            doc.insertString(doc.getLength(),
                        "\tAnnals of Statistics 29, 1165–1188.\n", RobinConstants.attrNormal);
        } else if (dataModel.getPValCorrectionMethod().equals("holm")) {
            doc.insertString(doc.getLength(),
                        "\tHolm, S. (1979). A simple sequentially rejective multiple test procedure.\n"                        
                        , RobinConstants.attrHeader2);
            doc.insertString(doc.getLength(),
                        "\tScandinavian Journal of Statistics, 6, 65–70.\n", RobinConstants.attrNormal);
        } 
    }
    
    
    private void attachSessionInfo() throws BadLocationException, FileNotFoundException {
        
        doc.insertString(doc.getLength(),
                "\n\n\tR session information: \n\n", RobinConstants.attrHeader1);
        
        String info = Utilities.loadString(new FileInputStream(new File(dataModel.getSourceDir(), "R.session.info.txt")));
        
        doc.insertString(doc.getLength(),
                "\t" + info + "\n\n", RobinConstants.attrNormalMonospaced);
    
    }
    
    private void insertImage(File imagepath) throws MalformedURLException, BadLocationException {
        Style style = doc.addStyle("StyleName", null);
        
        ImageIcon img = new ImageIcon(imagepath.toURI().toURL());
        int height = img.getIconHeight();
        int width = img.getIconWidth();
        
        if (img.getIconHeight() > 600) {
            float factor = 600f / (float) height;
            height = (int) (height * factor);
            width = (int) (width * factor);
        }
        
        img = new ImageIcon(img.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
        
        StyleConstants.setIcon(style, img);
        doc.insertString(doc.getLength(), "ignored text", style);
        doc.insertString(doc.getLength(), "\n", null);
    }
    
    public void saveAsRTF(File outfile) {
        RTFEditorKit rtfkit = new RTFEditorKit();
        try {
            FileOutputStream fwi = new FileOutputStream(outfile);
            rtfkit.write(fwi, doc, 0, doc.getEndPosition().getOffset());
            fwi.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    
    public void saveAsHTML(File outfile) {
        HTMLEditorKit htmlkit = new HTMLEditorKit();
        try {
            FileOutputStream fwi = new FileOutputStream(outfile);
            htmlkit.write(fwi, doc, 0, doc.getEndPosition().getOffset());
            fwi.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void saveAsPDF(File path) {
        
         //print the panel to pdf
        Document document = new Document(PageSize.A4);
        Rectangle A4size = document.getPageSize();
        PdfWriter writer = null;
        scrollToBottom();

        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Anchor anchor = new Anchor("Result overview for RNA-Seq analysis project:\n\t"
                + dataModel.getProjectDir().getName() + "\n\n");
            anchor.setName("First Chapter");
            document.add(anchor);
            Paragraph p = new Paragraph();
            //addEmptyLine(p, 3);
            document.add(p);

//            for (Component panel : this.getComponents()) {
//                if (panel.getName() == null) {
//                    continue;
//                }
                
                System.out.println("printing "+this);
                
                Paragraph para = new Paragraph();
                para.setAlignment(Element.ALIGN_CENTER);

                PdfContentByte contentByte = writer.getDirectContent();
                PdfTemplate template = contentByte.createTemplate(this.getWidth(), this.getHeight());
                Graphics2D g2 = template.createGraphics(this.getWidth(), - this.getHeight());
                g2.scale(0.8d, 0.8d);

               

                this.print(g2);
                g2.dispose();
                Image img = Image.getInstance(template);
                para.add(img);
                //addEmptyLine(para, 1);
                document.add(para);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    
    }
    
    public void scrollToBottom() {
        this.doLayout();
        JScrollBar vertical = contentScrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentScrollPane = new javax.swing.JScrollPane();
        browserEditorPane = new javax.swing.JTextPane();

        setBackground(new java.awt.Color(255, 255, 255));

        contentScrollPane.setBorder(null);

        browserEditorPane.setBorder(null);
        browserEditorPane.setEditable(false);
        contentScrollPane.setViewportView(browserEditorPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, contentScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contentScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane browserEditorPane;
    private javax.swing.JScrollPane contentScrollPane;
    // End of variables declaration//GEN-END:variables

    

   
}
