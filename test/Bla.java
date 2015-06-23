/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marc
 */
import java.net.URL;

public class Bla {
    public static void main(String[] args) {
        URL u = new Bla().getClass().getResource("/Bla.class");
        System.out.println(u.getPath());
    }
}