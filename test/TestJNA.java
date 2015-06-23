
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
public class TestJNA extends TestCase {

    public void testJNA() throws IOException {
        CLibrary libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);
        File testfile = new File("/Users/marc/Desktop/TESTFILE.TEST");
        testfile.createNewFile();
        libc.chmod(testfile.getCanonicalPath(), 0755);

        System.out.println(Platform.is64Bit() ? "this is a 64 bit machine" : "this is not a 64 bit machine");
        System.out.println(Platform.isMac() ? "this is a mac" : "this is not a mac");
    }



}

interface CLibrary extends Library {
    public int chmod(String path, int mode);
}
