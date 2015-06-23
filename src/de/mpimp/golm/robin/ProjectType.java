/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin;

/**
 *
 * @author marc
 */
public class ProjectType {
    public static enum TYPE {
        AFFYMETRIX,
        SINGLE_CHANNEL,
        TWO_COLOR,
        RNA_SEQ
    }

    public static String typeToString(ProjectType.TYPE type) {
        if (type == TYPE.AFFYMETRIX) {
            return "Affymetrix";
        } else if (type == TYPE.SINGLE_CHANNEL) {
            return "single channel";
        } else if (type == TYPE.TWO_COLOR) {
            return "two color";
        } else if (type == TYPE.RNA_SEQ) {
            return "RNA-Seq";
        } else {
            return null;
        }
    }

}
