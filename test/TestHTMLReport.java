
import de.mpimp.golm.robin.HTML.HTMLQCItem;
import de.mpimp.golm.robin.HTML.HTMLReportGenerator;
import de.mpimp.golm.robin.HTML.HTMLReportSection;
import junit.framework.TestCase;

/** 
 *
 * @author marc
 */
public class TestHTMLReport extends TestCase {
    
    public void testHTMLReport() {
        HTMLReportGenerator generator = new HTMLReportGenerator();        
        HTMLQCItem qcitem = new HTMLQCItem("Face control", "marclohse.cel", "/Users/marc/Desktop/MarcLohse1.jpg", "Looks like idiot-warning");
        
        HTMLReportSection section = new HTMLReportSection("Lumumba macht stark");
        section.setDescription("test description to se whether this works properly");
        section.addItem(qcitem);
        section.addItem(qcitem);
        System.out.println(section.render());
    }
}
