import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
 
public class AddingTables {
    JTextPane textPane;
 
    public AddingTables() {
        String[] text = {
            "This component models paragraphs that are composed of " +
            "runs of character level attributes.\n",           //  0 - 89
            " \n",                                             // 90 - 91
            "Each paragraph may have a logical style attached to it which " +
            "contains the default attributes to use if not overridden by " +
            "attributes set on the paragraph or character run. Components " +
            "and images may be embedded in the flow of text."  // 92 - 321
        };
        textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        createStyles(doc);
        setContent(doc, text);
        styleContent(doc);
    }
 
    private void createStyles(StyledDocument doc) {
        Style baseStyle = doc.addStyle("base", null);
        StyleConstants.setFontFamily(baseStyle, "Lucida Sans Unicode");
        StyleConstants.setFontSize(baseStyle, 18);
        StyleConstants.setFirstLineIndent(baseStyle, 20f);
        StyleConstants.setLeftIndent(baseStyle, 10f);
 
        Style style = doc.addStyle("bold", baseStyle);
        StyleConstants.setBold(style, true);
 
        style = doc.addStyle("italic", baseStyle);
        StyleConstants.setItalic(style, true);
 
        style = doc.addStyle("blue", baseStyle);
        StyleConstants.setForeground(style, Color.blue);
 
        style = doc.addStyle("underline", baseStyle);
        StyleConstants.setUnderline(style, true);
 
        style = doc.addStyle("green", baseStyle);
        StyleConstants.setForeground(style, Color.green.darker());
        StyleConstants.setUnderline(style, true);
 
        style = doc.addStyle("highlight", baseStyle);
        StyleConstants.setForeground(style, Color.yellow);
        StyleConstants.setBackground(style, Color.black);
 
        style = doc.addStyle("table", null);
        StyleConstants.setComponent(style, getTableComponent());
 
        style = doc.addStyle("tableParagraph", null);
        StyleConstants.setLeftIndent(style, 35f);
        StyleConstants.setRightIndent(style, 35f);
        StyleConstants.setSpaceAbove(style, 15f);
        StyleConstants.setSpaceBelow(style, 15f);
    }
 
    private void setContent(StyledDocument doc, String[] text) {
        try {
            doc.insertString(0,               text[0], doc.getStyle("base"));
            doc.insertString(doc.getLength(), text[1], doc.getStyle("table"));
            doc.insertString(doc.getLength(), text[2], doc.getStyle("base"));
        } catch(BadLocationException e) {
            System.out.printf("Bad location error: %s%n", e.getMessage());
        }
    }
 
    private void styleContent(StyledDocument doc) {
        Style style = doc.getStyle("base");
        doc.setLogicalStyle(0, style);
        style = doc.getStyle("underline");
        doc.setCharacterAttributes(22, 10, style, false);
        style = doc.getStyle("highlight");
        doc.setCharacterAttributes(62, 26, style, false);
 
        Style logicalStyle = doc.getLogicalStyle(0);
        style = doc.getStyle("tableParagraph");
        doc.setParagraphAttributes(90, 1, style, false);
        style = doc.getStyle("table");
        doc.setCharacterAttributes(90, 1, style, false);
        doc.setLogicalStyle(92, logicalStyle);
 
        style = doc.getStyle("blue");
        doc.setCharacterAttributes(118, 13, style, false);
        style = doc.getStyle("italic");
        doc.setCharacterAttributes(166, 18, style, false);
        style = doc.getStyle("green");
        doc.setCharacterAttributes(235, 9, style, false);
        doc.setCharacterAttributes(248, 9, style, false);
        style = doc.getStyle("bold");
        doc.setCharacterAttributes(263, 10, style, false);
        doc.setCharacterAttributes(278, 6, style, false);
    }
 
    private JScrollPane getTableComponent() {
        JTable table = new JTable(getModel());
        Dimension d = table.getPreferredSize();
        d.width = 300;
        table.setPreferredScrollableViewportSize(d);
        return new JScrollPane(table);
    }
 
    private AbstractTableModel getModel() {
        return new AbstractTableModel() {
            public int getColumnCount() { return 3; }
            public int getRowCount() { return 3; }
            public Object getValueAt(int row, int col) {
                return String.valueOf(row + 1) + (col + 1);
            }
        };
    }
 
    private JScrollPane getContent() {
        return new JScrollPane(textPane);
    }
 
    public static void main(String[] args) {
        System.setProperty("swing.aatext", "true");
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new AddingTables().getContent());
        f.setSize(500,400);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}