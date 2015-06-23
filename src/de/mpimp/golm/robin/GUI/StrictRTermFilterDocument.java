/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.GUI;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author marc
 */
public class StrictRTermFilterDocument extends PlainDocument {

    int maxValue;
    int maxLength;
    Toolkit toolkit;
    
    public StrictRTermFilterDocument() {
        toolkit = Toolkit.getDefaultToolkit();
    }
    
    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null) {
            return;
        }

        // no leading numbers
        if (str.matches("\\d+") && (offs == 0)) {
            toolkit.beep();
            return;
        }
        
        // only alphanumeric characters - any whitespace will be replaced by underscore
        if (str.matches("^[a-zA-Z_ ]*\\d*$")) {
            super.insertString(offs, str.replaceAll("\\s+", "_"), a);
        } else {
            toolkit.beep();
        }
    }
    
    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
    }
}
