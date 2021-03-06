/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RNASeqQCResultContainerPanel.java
 *
 * Created on 12.09.2011, 10:52:23
 */
package de.mpimp.golm.robin.GUI.RNASeq.qualcheck;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import de.mpimp.golm.robin.data.RNASeqDataModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author marc
 */
public class RNASeqQCResultContainerPanel extends javax.swing.JPanel {

    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.HELVETICA, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 12,
            Font.BOLD);
    private static Font normal = new Font(Font.FontFamily.HELVETICA, 12,
            Font.NORMAL, BaseColor.DARK_GRAY);
    private final File inputFile;

    public RNASeqQCResultContainerPanel(File inputFile, boolean error) {
        initComponents();
        this.inputFile = inputFile;
        
        if (error) {
            fileNameLabel.setText(inputFile.getName() + " had parsing errors");
            fileNameLabel.setForeground(Color.red);
        } else {        
            fileNameLabel.setText("Quality check results for " + inputFile.getName());
        }
    }

    public void scrollToBottom() {
        contentPanel.doLayout();
        JScrollBar vertical = contentScrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void addQCView(JPanel panel) {
        this.contentPanel.add(panel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileNameLabel = new javax.swing.JLabel();
        savePDFButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        contentScrollPane = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        fileNameLabel.setFont(fileNameLabel.getFont().deriveFont(fileNameLabel.getFont().getStyle() | java.awt.Font.BOLD, fileNameLabel.getFont().getSize()+2));
        fileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        fileNameLabel.setText(org.openide.util.NbBundle.getMessage(RNASeqQCResultContainerPanel.class, "RNASeqQCResultContainerPanel.fileNameLabel.text")); // NOI18N
        fileNameLabel.setMaximumSize(new java.awt.Dimension(600, 19));
        fileNameLabel.setPreferredSize(new java.awt.Dimension(600, 19));

        savePDFButton.setText(org.openide.util.NbBundle.getMessage(RNASeqQCResultContainerPanel.class, "RNASeqQCResultContainerPanel.savePDFButton.text")); // NOI18N
        savePDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePDFButtonActionPerformed(evt);
            }
        });

        contentScrollPane.setBorder(null);

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        contentScrollPane.setViewportView(contentPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(265, 265, 265)
                .add(savePDFButton))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, contentScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(fileNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(fileNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contentScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(savePDFButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void savePDF(File path) {

        //print the panel to pdf
        Document document = new Document(PageSize.A4);
        Rectangle A4size = document.getPageSize();
        PdfWriter writer = null;
        scrollToBottom();

        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Anchor anchor = new Anchor("Quality check results for " + inputFile.getName(), catFont);
            anchor.setName("First Chapter");
            document.add(anchor);
            Paragraph p = new Paragraph();
            addEmptyLine(p, 3);
            document.add(p);

            for (Component panel : contentPanel.getComponents()) {
                if (panel.getName() == null) {
                    continue;
                }
                Paragraph para = new Paragraph();
                para.setAlignment(Element.ALIGN_CENTER);

                PdfContentByte contentByte = writer.getDirectContent();
                PdfTemplate template = contentByte.createTemplate(panel.getWidth(), panel.getHeight());
                Graphics2D g2 = template.createGraphics(panel.getWidth(), panel.getHeight());
                g2.scale(0.8d, 0.8d);

                if (panel instanceof RNASeqQCResultPanel) {
                    ((RNASeqQCResultPanel) panel).prepareForExport();
                }

                panel.print(g2);
                g2.dispose();
                Image img = Image.getInstance(template);
                para.add(img);
                addEmptyLine(para, 1);
                document.add(para);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void fakeMouseClick(ActionEvent e) {        
        savePDFButtonActionPerformed(e);
    }

private void savePDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePDFButtonActionPerformed
    scrollToBottom();
    File PDFpath;    
    if (evt.getActionCommand().equals("fakeClick")) {        
        RNASeqDataModel model = ((RNASeqDataModel) evt.getSource());        
        PDFpath = new File(model.getQualitychecksDir(), inputFile.getName() + "_qualcheck.pdf");
    } else  {
        java.awt.FileDialog pdChooser = new java.awt.FileDialog(new Frame(), "Please choose barcode table", FileDialog.SAVE);
        pdChooser.setDirectory(System.getProperty("user.home"));
        pdChooser.setFile(inputFile.getName() + "_qualcheck");
        // this might fix it on a mac
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        pdChooser.setVisible(true);
        if (pdChooser.getFile() == null) {
            return;
        }
        PDFpath = new File(pdChooser.getDirectory(), pdChooser.getFile() + ".pdf");
    }    
    savePDF(PDFpath);
}//GEN-LAST:event_savePDFButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton savePDFButton;
    // End of variables declaration//GEN-END:variables
}
