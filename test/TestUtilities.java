/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import de.mpimp.golm.common.utilities.Utilities;
import de.mpimp.golm.robin.misc.RobinUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author marc
 */
public class TestUtilities extends TestCase {

//    public void testloadGroupsList() throws IOException, FileNotFoundException, DocumentException {
//
//        File groups = new File("/Users/marc/Desktop/GROUPS.XML");
//        JFrame frame = new JFrame();
//        JScrollPane scrollpane = new JScrollPane();
//
//        DefaultListModel model = new DefaultListModel();
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColA-2.Cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColN-2.CEL");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColA-1.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColA-3.CEL");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColN-1.CEL");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/ColN-3.CEL");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA15A.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA15B.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA17AA.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA1BB.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA1AA.cel");
//        model.addElement("/Users/marc/Desktop/projects/RobinLab/TestDaten/all/RIKENGODA17BA.cel");
//
//        JList sourceList = new JList(model);
//
//        JPanel panel = Utilities.populateGroupListFromFile(groups, sourceList);
//        scrollpane.add(panel);
//        scrollpane.setViewportView(panel);
//        frame.add(scrollpane);
//        frame.pack();
//        frame.setVisible(true);
//        System.out.println();
//    }

//    public void testDirChooser() {
//        JDirectoryChooser dirChooser = new JDirectoryChooser(System.getProperty("user.home"));
//        int choice = dirChooser.showOpenDialog(new JFrame());
//        if(choice == JDirectoryChooser.CANCEL_OPTION) {
//            System.out.println("User Canceled");
//        } else {
//            System.out.println("Dialog Selection: " + dirChooser.getSelectedFile().getAbsolutePath());
//        }
//    }

//    public void testCountLines() throws IOException {
//        File testfile = new File("/Users/marc/Desktop/people/Yang_Ping_Lee/Agilent/short/Can_1.txt");
//        System.out.println("lines counted wo ignore :" + Utilities.countLinesInTextFile(testfile, ""));
//        System.out.println("lines counted with ignore :" + Utilities.countLinesInTextFile(testfile, "^\\s*\\n$"));
//    }

    public void xtestTruncateString() {
        String test = "/Users/marc/Development/workspace/Robin_CURRENT/src";
        String trunc = Utilities.truncateLongString(test, 100);
        System.out.println("raw:\t"+test+"\ntrunc:\t"+trunc);
    }

    public void xtestPhysicalMemoryCheck() {
        System.out.println("free physical mem: " + new Double( Utilities.longAsMegaBytes(Utilities.getFreePhysicalMemorySize())).intValue()+"MB");
        System.out.println("total physical mem: " + Utilities.getTotalPhysicalMemorySize()+" bytes");
    }
    
    public void xtestGuessEOL() throws FileNotFoundException, IOException {
        System.out.println("file EOL type is "+Utilities.guessEOLType(new File("/Volumes/Backup/SOLEXA_PLAYGROUND/unix")));
        System.out.println("file EOL type is "+Utilities.guessEOLType(new File("/Volumes/Backup/SOLEXA_PLAYGROUND/dos")));
        System.out.println("file EOL type is "+Utilities.guessEOLType(new File("/Volumes/Backup/SOLEXA_PLAYGROUND/mac")));
        System.out.println("file EOL type is "+Utilities.guessEOLType(new File("/Users/marc/Desktop/Frisch_auf_den_Müll.avi")));
    }
    
    public void xtestDecompress() throws IOException {
        File in = new File("/Volumes/Backup/SOLEXA_PLAYGROUND/sample80000.fastq.bz2");
        Utilities.decompressFile(in);
    }
    
    public void xtestMillisAsString() {        
        System.out.println(Utilities.millisecondsToString(787123L));
    }
    
    public void testGCcount() {
        System.out.println(RobinUtilities.getGCContent("acgtt"));
    }
}