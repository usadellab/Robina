/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.data;

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.warnings.Warning;
import de.mpimp.golm.robin.warnings.WarningsHandler;
import java.io.FileWriter;
import java.io.IOException;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author marc
 */
public class HTDataModel {

    protected WarningsHandler warnings = new WarningsHandler();

    public HTDataModel() {
    }

    public void setWarnings(WarningsHandler warnings) {
        this.warnings = warnings;
    }

    public WarningsHandler getWarningsHandler() {
        return warnings;
    }

    public void addWarning(Warning warning) {
        warnings.addWarning(warning);
    }
    
    protected void writeXMLDoc(Document doc, FileWriter writer) throws IOException {
        XMLWriter xmlWriter = new XMLWriter(writer, Utilities.getFormat());
        xmlWriter.write(doc);
        xmlWriter.flush();
        xmlWriter.close();
        writer.close();
    }
}
